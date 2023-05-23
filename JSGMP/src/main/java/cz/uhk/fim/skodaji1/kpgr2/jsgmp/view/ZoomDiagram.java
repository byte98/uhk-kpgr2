/*
 * Copyright (C) 2076 Jiri Skoda <jiri.skoda@student.upce.cz>
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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;

/**
 * Class representing diagram of zoom
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class ZoomDiagram implements Threadable
{
    /**
     * Interval between checking, whether diagram should be refreshed (in miliseconds)
     */
    private static final int SLEEP = 200;
    
    /**
     * Counter of created diagrams
     */
    private static long counter = 0;
    
    /**
     * Actual width of view
     */
    private int viewWidth; 
    
    /**
     * Actual height of view
     */
    private int viewHeight;
    
    /**
     * Actual width of image
     */
    private int imageWidth;
    
    /**
     * Actual height of image
     */
    private int imageHeight;
    
    /**
     * Actual value of scroll distance from top
     */
    private int scrollTop;
    
    /**
     * Actual value of scroll distance from left
     */
    private int scrollLeft;
    
    /**
     * Thread which manages refresh of diagram
     */
    private final Thread thread;
    
    /**
     * Flag, whether thread is running
     */
    private boolean running;
    
    /**
     * Flag, whether diagram should be refreshed
     */
    private boolean refresh;
    
    /**
     * Bitmap to which diagram will be drawn
     */
    private final Bitmap bitmap;
    
    /**
     * Source of image data
     */
    private final Bitmap source;
    
    /**
     * Creates new diagram of zoom
     * @param viewWidthProperty Width of view
     * @param viewHeightProperty Height of view
     * @param imageWidthProperty Width of image
     * @param imageHeightProperty Height of image
     * @param scrollTopProperty Scroll distance from top
     * @param scrollLeftProperty Scroll distance from left
     * @param source Source of image data (image itself)
     */
    public ZoomDiagram(
            ReadOnlyDoubleProperty viewWidthProperty,
            ReadOnlyDoubleProperty viewHeightProperty,
            ReadOnlyDoubleProperty imageWidthProperty,
            ReadOnlyDoubleProperty imageHeightProperty,
            ReadOnlyDoubleProperty scrollTopProperty,
            ReadOnlyDoubleProperty scrollLeftProperty,
            Bitmap source
    )
    {
        this.thread = new Thread(this, String.format("JSGMP:ZoomDiagram-%d", counter));
        this.source = source;
        this.running = false;
        this.refresh = false;
        this.bitmap = ThreadManager.createBitmap(Globals.HISTOGRAM_WIDTH, Globals.HISTOGRAM_HEIGHT);
        viewWidthProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.viewWidth = (int)Math.round((Double)t1);
            this.refresh = true;
        });
        viewHeightProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.viewHeight = (int)Math.round((Double)t1);
            this.refresh = true;
        });
        imageWidthProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.imageWidth = (int)Math.round((Double)t1);
            this.refresh = true;
        });
        imageHeightProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.imageHeight = (int)Math.round((Double)t1);
            this.refresh = true;
        });
        scrollTopProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.scrollTop = (int)Math.round((Double)t1);
            this.refresh = true;
        });
        scrollLeftProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.scrollLeft = (int)Math.round((Double)t1);
            this.refresh = true;
        });
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
        while (this.running == true)
        {
            if (this.refresh == true)
            {
                this.refresh = false;
            }
            try
            {
                Thread.sleep(ZoomDiagram.SLEEP);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ZoomDiagram.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
