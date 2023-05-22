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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.BrightnessContrast;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Effect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
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
     * @return 
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
        Histogram reti = new Histogram(function, source, ThreadManager.createBitmap(width, height), startColor, finalColor, dataLength);
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
     * Stops all managed threads
     */
    public static synchronized final void stopThreads()
    {
        for(Threadable t: ThreadManager.threads)
        {
            t.stop();
        }
    }
}

