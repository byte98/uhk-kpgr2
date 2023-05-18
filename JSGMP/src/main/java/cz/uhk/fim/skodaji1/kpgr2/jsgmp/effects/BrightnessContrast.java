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
     * Color of curve in chart
     */
    private static final Pixel CHART_COLOR = new Pixel((short)255, (short)255, (short)255);
    
    /**
     * Sleep time between checking for refreshing chart (in miliseconds)
     */
    private static final int SLEEP = 1000;
    
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
    private int brightness;
    
    /**
     * Actual value of contrast
     */
    private double contrast;
        
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
    private boolean update = true;
    
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
                Color.rgb(255, 227, 105),
                256
        );
        this.chart = ThreadManager.createBitmap(Globals.HISTOGRAM_WIDTH, Globals.HISTOGRAM_HEIGHT);
        this.thread = new Thread(this, "JSGMP:BrightnessContrast-" + BrightnessContrast.counter);
        BrightnessContrast.counter++;
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
        return new Pixel(this.computeValue(pixel.getRed()), this.computeValue(pixel.getGreen()), this.computeValue(pixel.getBlue()), pixel.getAlpha());
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
        System.out.println("draw");
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
                    this.chart.setPixel(x, y, new Pixel(Globals.HISTOGRAM_CLEAR.getRed(), Globals.HISTOGRAM_CLEAR.getGreen(), Globals.HISTOGRAM_CLEAR.getBlue(), (short)0));      
                }
                else
                {
                    this.chart.setPixel(x, y, Globals.HISTOGRAM_CLEAR);
                }
            }
        }
        
        // Draw line
        // a ... this.contrast
        // b ... this.brightness
        //           y = ax + b
        //       y - b = ax
        // (y - b) / a = x
        // Because of coordinates are indexed from top (not from bottom):
        // y = 0 <=> y = this.chart.getHeight() - 1;
        // 
        // For starting point (most bottom, most left; y ~ 0):
        // xA = (0 - this.brightness) / this.contrast
        // yA = (this.contrast * xA) + this.brightness 
        int xA = (int)Math.round((0f - this.brightness) / this.contrast);
        int yA = (int)Math.round((this.contrast * (double)xA) + this.brightness);
        // For ending point (most top, most right; y ~ this.chart.getHeight()
        // xB = (this.chart.getHeight() - this.brightness) / this.contrast
        // yB = (this.contrast * xB) + this.brightness
        int xB = (int)Math.round((double)(this.chart.getHeight() - this.brightness) / this.contrast);
        int yB = (int)Math.round((this.contrast * (double)xB) + this.brightness);
        
        int dX = Math.abs(xB - xA);
        int dY = Math.abs(yB - yA);
        Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
        
        System.out.println(String.format("[%d %d] -> [%d %d] (%d %d)", xA, yA, xB, yB,dX, dY));
        
        if (dX > dY)
        {
            for (int i = xA; i <= xB; i++)
            {
                int x = i;
                int y = this.interpolateNumber(yA, yB, i, dY);
                System.out.println(String.format("[%d; %d]", x, y));
                transaction.setPixel(x, y,BrightnessContrast.CHART_COLOR);
            }
        }
        else
        {
            for (int i = yA; i <= yB; i++)
            {
                int x = this.interpolateNumber(xA, xB, i, dY);
                int y = i;
                System.out.println(String.format("[%d; %d]", x, y));
                transaction.setPixel(x, y,BrightnessContrast.CHART_COLOR);
            }
        }
        this.chart.processTransaction(transaction);
    }
    
    /**
     * Interpolates number
     * @param i1 First number
     * @param i2 Second number
     * @param step Number of step
     * @param steps Number of all steps between numbers
     * @return Interpolated number
     */
    private final int interpolateNumber(int i1, int i2, int step, int steps)
    {
        int delta = i2 - i1;
        double stepReal = (double)delta / (double)steps;
        return (int)Math.round(i1 + ((double)step) * stepReal);
    }
    
    @Override
    public void run()
    {
        while(this.running == true)
        {
            if (this.update == true)
            {
                this.update = false;
                BrightnessContrast.this.drawChart();
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
}
