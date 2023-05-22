/*
 * Copyright (C) 2023 Jiri Skoda <jiri.skoda@student.upce.cz>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap.BitmapChangedActionListener;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class which handles changing of brightness
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class BrightnessContrast implements Effect, Threadable
{
    /**
     * Counter of created brightness/contrast effects
     */
    private static long counter = 0;
    
    /**
     * Size of grid in chart (in pixels)
     */
    private static final int CHART_GRID = 20;
    
    /**
     * Color of axis in chart
     */
    private static final Pixel CHART_AXIS = new Pixel((short)96, (short)96, (short)96);
    
    /**
     * Color of curve in chart
     */
    private static final Pixel CHART_COLOR = new Pixel((short)255, (short)255, (short)255);
    
    /**
     * Sleep time between checking for refreshing chart (in miliseconds)
     */
    private static final int SLEEP = 200;
    
    /**
     * Histogram of brightness
     */
    private final Histogram brightnessHistogram;
    
    /**
     * Bitmap with chart of curve of brightness and contrast
     */
    private final Bitmap chart;
    
    /**
     * Bitmap on which brightness effect will be applied on
     */
    private final Bitmap bitmap;
    
    /**
     * Coefficient of red channel used when computing brightness
     */
    private static final double R_COEFF = 0.299;
    
    /**
     * Coefficient of green channel used when computing brightness
     */
    private static final double G_COEFF = 0.7152;
    
    /**
     * Coefficient of blue channel used when computing brightness
     */
    private static final double B_COEFF = 0.0722;
    
    /**
     * Actual value of brightness
     */
    private int brightness = 0;
    
    /**
     * Actual value of contrast
     */
    private double contrast = 1f;
        
    /**
     * Thread which handles update of chart
     */
    private final Thread thread;
    
    /**
     * Flag, whether thread is running
     */
    private boolean running = false;
    
    /**
     * Flag, whether chart should be updated
     */
    private boolean update = false;
    
    /**
     * Histogram of contrast
     */
    private final Histogram contrastHistogram;
    
    /**
     * Listeners of brightness/contrast changed
     */
    private final List<Effect.EffectChangedListener> listeners;
    
    /**
     * Creates new handler of brightness effect
     * @param bitmap Bitmap which brightness will be handled
     */
    public BrightnessContrast(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.brightnessHistogram = ThreadManager.createHistogram((Pixel px) -> {
                    return (int)Math.round(BrightnessContrast.R_COEFF * px.getRed() + BrightnessContrast.G_COEFF * px.getGreen() + BrightnessContrast.B_COEFF * px.getBlue());
                },
                this.bitmap,
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.BLACK,
                Color.rgb(255, 238, 179),
                256
        );
        this.contrastHistogram = ThreadManager.createHistogram((Pixel px) ->
        {
            int delta = BrightnessContrast.this.bitmap.getMaxIntensity() - BrightnessContrast.this.bitmap.getMinIntensity();
            double step = 255f / (double)delta;
            int intensity = Globals.INTENSITY.apply(px);
            return (int)Math.round((double)intensity * step);
        },
                this.bitmap,
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.BLACK,
                Color.rgb(176, 255, 244),
                256,
                true
        );
        this.chart = ThreadManager.createBitmap(Globals.HISTOGRAM_WIDTH, Globals.HISTOGRAM_HEIGHT);
        this.thread = new Thread(this, "JSGMP:BrightnessContrast-" + BrightnessContrast.counter);
        BrightnessContrast.counter++;
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }    
    
    /**
     * Gets histogram of brightness
     * @return Image containing histogram of brightness
     */
    public Image getBrightnessHistogram()
    {
        return this.brightnessHistogram.getImage();
    }
    
    /**
     * Gets histogram of contrast
     * @return Image containing histogram of contrast
     */
    public Image getContrastHistogram()
    {
        return this.contrastHistogram.getImage();
    }
    
    /**
     * Gets chart of brightness and contrast
     * @return Image containing chart of brightness and contrast curve
     */
    public Image getChart()
    {
        return this.chart.toImage();
    }
    
    /**
     * Sets new value of brightness
     * @param brightness New value of brightness [-100 100]
     */
    public void setBrightness(int brightness)
    {
        this.brightness = brightness;
        this.update = true;
        this.invokeChange();
    }
    
    /**
     * Sets new value of contrast
     * @param contrast New value of contrast [0.9 1.9]
     */
    public void setContrast(double contrast)
    {
        this.contrast = contrast;
        this.update = true;
        this.invokeChange();
    }
    
    /**
     * Computes value of subpixel
     * @param input Subpixel which value will be computed
     * @return Value of subpixel after brightness and contrast has been applied
     */
    private short computeValue(short input)
    {
        short reti = (short)Math.round((this.contrast * (double)input) + (double)this.brightness);
        if (reti < 0) reti = 0;
        if (reti > 255) reti = 255;
        return reti;
    }
    
    @Override
    public Pixel apply(Pixel pixel)
    {
        return new Pixel(
                this.computeValue(pixel.getRed()),
                this.computeValue(pixel.getGreen()),
                this.computeValue(pixel.getBlue()),
                pixel.getAlpha()
        );
    }

    @Override
    public void start()
    {
        this.running = true;
        this.thread.start();
    }

    @Override
    public void stop()
    {
        this.running = false;
    }

    /**
     * Draws chart
     */
    private void drawChart()
    {
        Runnable task = () -> {
            Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
            int centerX = (int)Math.round((double)this.chart.getWidth() / 2f);
            int centerY = (int)Math.round((double)this.chart.getHeight() / 2f);
            int deltaX = centerX % BrightnessContrast.CHART_GRID;
            int deltaY = centerY % BrightnessContrast.CHART_GRID;
            // Draw background with grid
            for (int y = 0; y < this.chart.getHeight(); y++)
            {
                for (int x = 0; x < this.chart.getWidth(); x++)
                {
                    if ((x - deltaX) % BrightnessContrast.CHART_GRID == 0 || (y - deltaY) % BrightnessContrast.CHART_GRID == 0)
                    {
                        transaction.setPixel(x, y, new Pixel(Globals.HISTOGRAM_CLEAR.getRed(), Globals.HISTOGRAM_CLEAR.getGreen(), Globals.HISTOGRAM_CLEAR.getBlue(), (short)0));      
                    }
                    else
                    {
                        transaction.setPixel(x, y, Globals.HISTOGRAM_CLEAR);
                    }
                }
            }
            
            // Draw axis
            for (int y = 0; y < this.chart.getHeight(); y++)
            {
                transaction.setPixel(this.chart.getWidth() / 2, y, BrightnessContrast.CHART_AXIS);
            }
            for (int x = 0; x < this.chart.getWidth(); x++)
            {
                transaction.setPixel(x, this.chart.getHeight() / 2, BrightnessContrast.CHART_AXIS);
            }

            final Function<Integer, Integer> translateX = (inX) -> 
            {
                return inX + this.chart.getWidth() / 2;
            };

            final Function<Integer, Integer> translateY = (inY) ->
            {
                return this.chart.getHeight() / 2 - inY;
            };

            // Draw chart
            int startX = this.chart.getHeight() / (-2);
            int startY = startX;
            int endX = this.chart.getHeight() / 2;
            int endY = endX;
            startX = (int)Math.round((double)startX * this.contrast);
            endX = (int)Math.round((double)endX * this.contrast);
            startX = startX + this.brightness;
            endX = endX + this.brightness;
            startX = translateX.apply(startX);
            startY = translateY.apply(startY);
            endX = translateX.apply(endX);
            endY = translateY.apply(endY);
            int dX = Math.abs(endX - startX);
            int dY = Math.abs(endY - startY);
            int counter = 0;
            if (dX > dY)
            {
                for (int x = startX; x <= endX; x++)
                {
                    int y = this.interpolateNumber(startY, endY, counter, dX);
                    transaction.setPixel(x, y, BrightnessContrast.CHART_COLOR);
                    transaction.setPixel(x + 1, y, BrightnessContrast.CHART_COLOR);
                    transaction.setPixel(x - 1, y, BrightnessContrast.CHART_COLOR);
                    counter++;
                }
            }
            else
            {
                for (int y = startY; y >= endY; y--)
                {
                    int x = this.interpolateNumber(startX, endX, counter, dY);
                    transaction.setPixel(x, y, BrightnessContrast.CHART_COLOR);
                    transaction.setPixel(x + 1, y, BrightnessContrast.CHART_COLOR);
                    transaction.setPixel(x - 1, y, BrightnessContrast.CHART_COLOR);
                    counter++;
                }
            }
            this.chart.processTransaction(transaction);
        };
        Thread t = new Thread(task, "JSGMP:DrawBrightnessContrastChart");
        t.start();
    }
    
    /**
     * Interpolates number
     * @param i1 First number
     * @param i2 Second number
     * @param step Number of step
     * @param steps Number of all steps between numbers
     * @return Interpolated number
     */
    private int interpolateNumber(int i1, int i2, int step, int steps)
    {
        int delta = i2 - i1;
        double stepReal = (double)delta / (double)steps;
        return (int)Math.round(i1 + ((double)step) * stepReal);
    }
    
    @Override
    public void run()
    {
        this.drawChart();
        while(this.running == true)
        {
            if (this.update == true)
            {
                this.update = false;
                this.drawChart();
            }
            try
            {
                Thread.sleep(BrightnessContrast.SLEEP);
            }
            catch (InterruptedException ex) 
            {
                Logger.getLogger(BrightnessContrast.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void addEffectChangedListener(EffectChangedListener listener)
    {
        this.listeners.add(listener);
    }
    
    /**
     * Invokes change of brightness and/or contrast
     */
    private void invokeChange()
    {
        for(Effect.EffectChangedListener listener: this.listeners)
        {
            listener.effectChanged();
        }
    }
}
