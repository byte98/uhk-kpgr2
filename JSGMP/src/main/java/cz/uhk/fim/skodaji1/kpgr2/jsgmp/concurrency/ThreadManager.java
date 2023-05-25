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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller.EffectsController;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.BrightnessContrast;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Effect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.ZoomDiagram;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.scene.paint.Color;

/**
 * Class which manages all created threads in application
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class ThreadManager
{
    /**
     * List of all managed threads
     */
    private static final List<Threadable> threads = Collections.synchronizedList(new ArrayList<>());
    
    /**
     * Creates new bitmap which works over multiple threads
     * @param width Width of bitmap
     * @param height Height of bitmap
     * @return New bitmap with defined dimensions and which works over multiple threads
     */
    public static final ConcurrentBitmap createBitmap(int width, int height)
    {
        ConcurrentBitmap reti = new ConcurrentBitmap(width, height);
        ThreadManager.threads.add(reti);
        reti.start();
        return reti;
    }
    
    /**
     * Creates new histogram which works over multiple threads
     * @param function Function which computes value of histogram for pixel
     * @param source Bitmap which histogram will be computed
     * @param width Width of histogram
     * @param height Height of histogram
     * @param startColor Starting color of histogram
     * @param finalColor Final color of histogram
     * @param dataLength Length of data (i.e. number of possible values from function)
     * @return New histogram which works over multiple threads
     */
    public static final Histogram createHistogram(
            Function<Pixel, Integer> function,
            Bitmap source,
            int width,
            int height,
            Color startColor,
            Color finalColor,
            int dataLength
    )
    {
        return ThreadManager.createHistogram(function, source, width, height, startColor, finalColor, dataLength, false);
    }
    
    /**
     * Creates new histogram which works over multiple threads
     * @param function Function which computes value of histogram for pixel
     * @param source Bitmap which histogram will be computed
     * @param width Width of histogram
     * @param height Height of histogram
     * @param startColor Starting color of histogram
     * @param finalColor Final color of histogram
     * @param dataLength Length of data (i.e. number of possible values from function)
     * @param smooth Flag, whether data in histogram should be smoothed
     * @return New histogram which works over multiple threads
     */
    public static final Histogram createHistogram(
            Function<Pixel, Integer> function,
            Bitmap source,
            int width,
            int height,
            Color startColor,
            Color finalColor,
            int dataLength,
            boolean smooth
    )
    {
        Histogram reti = new Histogram(function, source, ThreadManager.createBitmap(width, height), startColor, finalColor, dataLength, smooth);
        ThreadManager.threads.add(reti);
        reti.start();
        return reti;
    }
        
    /**
     * Creates new brightness/contrast effect
     * @param bitmap Bitmap on which histograms will be computed
     * @return Brightness/contrast effect handler
     */
    public static BrightnessContrast createBrightnessContrastEffect(Bitmap bitmap)
    {
        BrightnessContrast reti = new BrightnessContrast(bitmap);
        ThreadManager.threads.add(reti);
        reti.start();
        return reti;
    }
    
    /**
     * Creates new controller of all effects
     * @param bitmap Bitmap on which effects will be applied
     * @return Controller of all effects
     */
    public static EffectsController createEffectsController(Bitmap bitmap)
    {
        EffectsController reti = new EffectsController(bitmap);
        ThreadManager.threads.add(reti);
        reti.start();
        return reti;
    }
    
    /**
     * Creates new diagram of zoom
     * @param viewWidthProperty Width of view
     * @param viewHeightProperty Height of view
     * @param imageWidthProperty Width of image
     * @param imageHeightProperty Height of image
     * @param scrollTopProperty Scroll distance from top
     * @param scrollLeftProperty Scroll distance from left
     * @param source Source of image data (image itself)
     * @return New diagram of zoom
     */
    public static ZoomDiagram createZoomDiagram(
            ReadOnlyDoubleProperty viewWidthProperty,
            ReadOnlyDoubleProperty viewHeightProperty,
            ReadOnlyDoubleProperty imageWidthProperty,
            ReadOnlyDoubleProperty imageHeightProperty,
            ReadOnlyDoubleProperty scrollTopProperty,
            ReadOnlyDoubleProperty scrollLeftProperty,
            Bitmap source
    )
    {
        ZoomDiagram reti = new ZoomDiagram(
                viewWidthProperty, viewHeightProperty,
                imageWidthProperty, imageHeightProperty,
                scrollTopProperty, scrollLeftProperty,
                source
        );
        ThreadManager.threads.add(reti);
        reti.start();
        return reti;
    }
    
    /**
     * Stops one object
     * @param t Object which will be stopped
     */
    public static synchronized final void stopOne(Threadable t)
    {
        if (ThreadManager.threads.contains(t))
        {
            ThreadManager.threads.remove(t);
            t.stop();
        }
        
    }
    
    /**
     * Stops all managed threads
     */
    public static synchronized final void stopAll()
    {
        BlockingQueue<Threadable> toRemove = new LinkedBlockingQueue<>(ThreadManager.threads.size());
        for(Threadable t: ThreadManager.threads)
        {
            try
            {
                toRemove.put(t);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ThreadManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        ThreadManager.threads.clear();
        for(Threadable t: toRemove)
        {
            t.stop();
        }
        toRemove.clear();
        toRemove = null;
        System.gc();
    }
}

