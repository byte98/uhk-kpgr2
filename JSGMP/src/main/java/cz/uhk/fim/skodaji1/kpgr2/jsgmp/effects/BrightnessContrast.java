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
    private static final Color CHART_COLOR = Color.WHITE;
    
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
     * Last applied value of effect
     */
    private int lastApplied = 0;
    
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
     * Generates chart of brightness and contrast
     */
    private void generateChart()
    {
        
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

    @Override
    public void run()
    {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
