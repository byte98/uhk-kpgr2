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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.view;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class representing histogram of bitmap
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Histogram implements Threadable
{   
    /**
     * Counter of created histograms
     */
    private static long counter = 0;
    
    /**
     * Sleep time between checking, whether histogram needs to be refreshed
     */
    private static final int SLEEP = 100;
    
    /**
     * Function which computes value of histogram from pixel
     */
    private final Function<Pixel, Integer> histogramFunction;
    
    /**
     * Bitmap which histogram will be computed
     */
    private final Bitmap source;
    
    /**
     * Bitmap to which result will be drawn
     */
    private final Bitmap result;
    
    /**
     * Starting color of histogram
     */
    private final Color startColor;
    
    /**
     * Final color of histogram
     */
    private final Color finalColor;
    
    /**
     * Flag, whether there is need to refresh histogram
     */
    private boolean refresh = false;
    
    /**
     * Flag, whether histogram generator is running
     */
    private boolean running = false;
    
    /**
     * Thread which handles asynchronous histogram generation
     */
    private final Thread thread;
    
    /**
     * Flag, whether histogram data should be smoothed or not
     */
    private final boolean smooth;
    
    /**
     * Data displayed in histogram
     */
    private final int[] data;
    
    /**
     * Creates new histogram
     * @param function Function which computes value of histogram for pixel
     * @param source Bitmap which histogram will be computed
     * @param result Bitmap to which results will be drawn into
     * @param startColor Starting color of histogram
     * @param finalColor Final color of histogram
     * @param dataLength Length of data (i.e. number of possible values from function)
     */
    public Histogram(
            Function<Pixel, Integer> function,
            Bitmap source,
            Bitmap result,
            Color startColor,
            Color finalColor,
            int dataLength
    )
    {
        this(function, source, result, startColor, finalColor, dataLength, false);
    }
    
    /**
     * Creates new histogram
     * @param function Function which computes value of histogram for pixel
     * @param source Bitmap which histogram will be computed
     * @param result Bitmap to which results will be drawn into
     * @param startColor Starting color of histogram
     * @param finalColor Final color of histogram
     * @param dataLength Length of data (i.e. number of possible values from function)
     * @param smooth Flag, whether histogram data should be smoothed or not
     */
    public Histogram(
            Function<Pixel, Integer> function,
            Bitmap source,
            Bitmap result,
            Color startColor,
            Color finalColor,
            int dataLength,
            boolean smooth
    )
    {
        this.histogramFunction = function;
        this.source = source;
        this.result = result;
        this.startColor = startColor;
        this.finalColor = finalColor;
        this.thread = new Thread(this, String.format("JSGMP:Histogram-%d", Histogram.counter));
        Histogram.counter++;
        this.data = new int[dataLength];
        this.smooth = true;
        this.source.addChangeActionListener(new Bitmap.BitmapChangedActionListener()
        {
            @Override
            public void onChange(Bitmap bitmap)
            {
                Histogram.this.refresh = true;
            }        
        });
    }
    
    /**
     * Gets image representation of histogram
     * @return Image representation of histogram
     */
    public Image getImage()
    {
        return this.result.toImage();
    }

    @Override
    public void start()
    {
        this.running = true;
        this.thread.start();
    }

    @Override
    public void stop() {
        this.running = false;
    }
    
    /**
     * Smoothes data
     */
    private void smooth()
    {
        for (int i = 0; i < this.data.length; i++)
        {
            if (this.data[i] == 0 && i > 0 && i < this.data.length - 1)
            {
                this.data[i] = (int)Math.round(((double)this.data[i - 1] + (double)this.data[i + 1]) / 2f);
            }
        }
    }
    
    /**
     * Interpolates color
     * @param startColor Starting color
     * @param finalColor Final color
     * @param steps Number of steps of transition between colors
     * @param step Actual number of step
     * @return Interpolated color between starting color and final color
     */
    private final Pixel interpolateColor(Color startColor, Color finalColor, int steps, int step)
    {
        double r = startColor.getRed() * 255f;
        double g = startColor.getBlue() * 255f;
        double b = startColor.getGreen() * 255f;
        double stepR = ((finalColor.getRed() * 255) - r) / (double)steps;
        double stepG = ((finalColor.getGreen() * 255) - g) / (double)steps;
        double stepB = ((finalColor.getBlue() * 255) - b) / (double)steps;
        r = r + (step * stepR);
        g = g + (step * stepG);
        b = b + (step * stepB);
        return new Pixel((short)Math.round(r), (short)Math.round(g), (short)Math.round(b));
    }

    @Override
    public void run()
    {
        while (this.running == true)
        {
            if (this.refresh == true)
            {
                // First, compute data
                Arrays.fill(this.data, 0);
                int max = Integer.MIN_VALUE;
                for(Pixel px: this.source)
                {
                    if (px != null)
                    {
                        int val = this.histogramFunction.apply(px);
                        if (val >= this.data.length)
                        {
                            val = this.data.length - 1;
                        }
                        this.data[val]++;
                        if (this.data[val] > max)
                        {
                            max = this.data[val];
                        }
                    }
                }
                
                if (this.smooth == true)
                {
                    this.smooth();
                }
                
                // Second, display data
                double widthStep = (double)this.result.getWidth() / (double)this.data.length;
                double heightStep = (double)this.result.getHeight() / (double)max;
                Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
                for (int i = 0; i < this.data.length; i++)
                {
                    int xStart = (int)Math.round((double)i * widthStep);
                    int xEnd = (int)Math.round((double)xStart + widthStep);
                    int height = (int)Math.round((double)this.data[i] * heightStep);
                    for (int x = xStart; x <= xEnd; x++)
                    {
                        for (int y = this.result.getHeight() - 1; y >= 0; y--)
                        {
                            if (y > (this.result.getHeight() - height))
                            {
                                transaction.setPixel(x, y, this.interpolateColor(this.startColor, this.finalColor, this.data.length - 1, i));
                            }
                            else
                            {
                                transaction.setPixel(x, y, Globals.HISTOGRAM_CLEAR);
                            }
                        }
                    }
                }
                this.result.processTransaction(transaction);
                this.refresh = false;
            }
            try
            {
                Thread.sleep(Histogram.SLEEP);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(Histogram.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
