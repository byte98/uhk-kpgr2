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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ConcurrentBitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

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
     * Padding of diagram in bitmap
     */
    private static final int PADDING = 10;
    
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
     * Actual value of scroll distance from top (in percents)
     */
    private double scrollTop;
    
    /**
     * Actual value of scroll distance from left (in percent)
     */
    private double scrollLeft;
    
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
        this.thread = new Thread(this, String.format("JSGMP:ZoomDiagram-%d", ZoomDiagram.counter));
        ZoomDiagram.counter++;
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
            this.scrollTop = (Double)t1;
            this.refresh = true;
        });
        scrollLeftProperty.addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            this.scrollLeft = (Double)t1;
            this.refresh = true;
        });
        this.viewWidth = (int)Math.round(viewWidthProperty.doubleValue());
        this.viewHeight = (int)Math.round(viewHeightProperty.doubleValue());
        this.imageWidth = (int)Math.round(imageWidthProperty.doubleValue());
        this.imageHeight = (int)Math.round(imageHeightProperty.doubleValue());
        this.scrollTop = scrollTopProperty.doubleValue();
        this.scrollLeft = scrollLeftProperty.doubleValue();
    }
    
    /**
     * Gets image representation of diagram
     * @return Image containing diagram
     */
    public Image toImage()
    {
        return this.bitmap.toImage();
    }

    /**
     * Interpolates integer
     * @param i1 First integer
     * @param i2 Second integer
     * @param step Actual number of step between first and second integer
     * @param steps Number of all steps between first and second integer
     * @return 
     */
    private int interpolateInt(int i1, int i2, int step, int steps)
    {
        int reti = i1;
        double delta = (double)i2 - (double)i1;
        double stepDelta = (double)steps / delta;
        reti = reti + (int)Math.round((double)step * stepDelta);
        return reti;
    }
    
    /**
     * Draws line
     * @param x1 X coordinate of starting point
     * @param y1 Y coordinate of starting point
     * @param x2 X coordinate of ending point
     * @param y2 Y coordinate of ending point
     * @param transaction Transaction in which line will be drawn 
     * @param color Color of the line
     */
    private void drawLine(int x1, int y1, int x2, int y2, Bitmap.BitmapTransaction transaction, Pixel color)
    {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        if (dX > dY)
        {
            int startX = x1;
            int startY = y1;
            int endX = x2;
            int endY = y2;
            if (endX < startX)
            {
                int tmp = startX;
                startX = endX;
                endX = tmp;
                tmp = startY;
                startY = endY;
                endY = tmp;
            }
            for (int x = startX; x < endX; x++)
            {
                transaction.setPixel(x, this.interpolateInt(startY, endY, x - startX, dX), color);
            }
        }
        else
        {
            int startX = x1;
            int startY = y1;
            int endX = x2;
            int endY = y2;
            if (endY < startY)
            {
                int tmp = startX;
                startX = endX;
                endX = tmp;
                tmp = startY;
                startY = endY;
                endY = tmp;
            }
            for (int y = startY; y < endY; y++)
            {
                transaction.setPixel(this.interpolateInt(startX, endX, y - startY, dY), y, color);
            }
        }
    }
    
    /**
     * Draws rectangle
     * @param x X coordinate of starting point of rectangle
     * @param y Y coordinate of starting point of rectangle
     * @param width Width of rectangle
     * @param height Height of rectangle
     * @param color Color of rectangle
     * @param transaction Transaction in which rectangle will be drawn
     */
    private void drawRectangle(int x, int y, int width, int height, Pixel color, Bitmap.BitmapTransaction transaction)
    {
        Function<Integer, Function<Integer, Function<Integer, Function<Integer, Function<Pixel, Consumer<Bitmap.BitmapTransaction>>>>>> rectangleDrawer = 
        _x -> _y -> _width -> _height -> _color -> _transaction ->
        {
            this.drawLine(_x, _y, _x + _width, _y, _transaction, _color);
            this.drawLine(_x + _width, _y, _x + _width, _y + _height, _transaction, _color);
            this.drawLine(_x + _width, _y + _height, _x, _y + _height, _transaction, _color);
            this.drawLine(_x, _y + _height, _x, _y, _transaction, _color);
        };
        
        // Draw rectangle
        rectangleDrawer.apply(x).apply(y).apply(width).apply(height).apply(color).accept(transaction);
        rectangleDrawer.apply(x + 1).apply(y + 1).apply(width - 2).apply(height - 2).apply(color).accept(transaction);
        rectangleDrawer.apply(x - 1).apply(y - 1).apply(width + 2).apply(height + 2).apply(color).accept(transaction);
        
        // Draw border
        rectangleDrawer.apply(x + 2).apply(y + 2).apply(width - 4).apply(height - 4).apply(Globals.HISTOGRAM_CLEAR).accept(transaction);
        rectangleDrawer.apply(x - 2).apply(y - 2).apply(width + 4).apply(height + 4).apply(Globals.HISTOGRAM_CLEAR).accept(transaction);
    }
    
    /**
     * Clears all data in diagram bitmap
     * @param transaction Transaction in which data will be cleared
     */
    private void clearBitmap(Bitmap.BitmapTransaction transaction)
    {
        for (int y = 0; y < this.bitmap.getHeight(); y++)
        {
            for (int x = 0; x < this.bitmap.getWidth(); x++)
            {
                transaction.setPixel(x, y, Globals.HISTOGRAM_CLEAR);
            }
        }
    }
    
    /**
     * Draws resized version of image
     * @param x X coordinate of top left pixel of image
     * @param y Y coordinate of top left pixel of image
     * @param width Required width of image
     * @param height Required height of image
     * @param transaction Transaction in which image will be drawn
     */
    private void drawImage(int x, int y, int width, int height, Bitmap.BitmapTransaction transaction)
    {
        double pxWidth = (double)this.source.getWidth() / (double)width;
        double pxHeight = (double)this.source.getHeight() / (double)height;
        for (int deltaY = 0; deltaY < height; deltaY++)
        {
            for(int deltaX = 0; deltaX < width; deltaX++)
            {
                int realX = (int)Math.round((double)deltaX * pxWidth);
                int realY = (int)Math.round((double)deltaY * pxHeight);
                if (realX < 0) realX = 0; if (realX >= this.source.getWidth()) realX = this.source.getWidth() - 1;
                if (realY < 0) realY = 0; if (realY >= this.source.getHeight()) realY = this.source.getHeight() - 1;
                transaction.setPixel(x + deltaX, y + deltaY, this.source.getPixel(realX, realY));
            }
        }
        
    }
    
    /**
     * Refreshes diagram
     */
    public void refresh()
    {
        this.refresh = true;
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
                ConcurrentBitmap.ConcurrentBitmapTransaction transaction = new ConcurrentBitmap.ConcurrentBitmapTransaction();
                int diagramViewWidth = this.bitmap.getWidth() - (2 * ZoomDiagram.PADDING);
                int diagramViewHeight = this.bitmap.getHeight() - (2 * ZoomDiagram.PADDING);
                this.clearBitmap(transaction);     
                double diagramAspectRatio = (double)this.bitmap.getWidth() / (double)this.bitmap.getHeight();
                double viewAspectRatio = (double)this.viewWidth / (double)this.viewHeight;                
                double viewHeightAspectRatio = (double)this.viewHeight / (double)this.viewWidth;
                if (viewAspectRatio <= diagramAspectRatio)
                {
                    diagramViewHeight = this.bitmap.getHeight() - (2 * ZoomDiagram.PADDING);                
                    diagramViewWidth = (int)Math.round((double)diagramViewHeight * viewAspectRatio);
                }
                else
                {
                    diagramViewWidth = this.bitmap.getWidth() - (2 * ZoomDiagram.PADDING);
                    diagramViewHeight = (int)Math.round((double)diagramViewWidth * viewHeightAspectRatio);
                }
                
                
                
                
                int diagramImageHeight = (int)Math.round(((double)this.imageHeight / (double)this.viewHeight) * diagramViewHeight);
                int diagramImageWidth = (int)Math.round(((double)this.imageWidth / (double)this.viewWidth) * diagramViewWidth);
                
                double imageAspectRatio = (double)this.imageWidth / (double)this.imageHeight;
                double imageHeightAspectRatio = (double)this.imageHeight / (double)this.imageWidth;
                if (diagramImageHeight > diagramViewHeight || diagramImageWidth > diagramViewWidth)
                {                    
                    if(imageAspectRatio <= diagramAspectRatio)
                    {
                        diagramImageHeight = this.bitmap.getHeight() - (2 * ZoomDiagram.PADDING);
                        diagramImageWidth = (int)Math.round((double)diagramImageHeight * imageAspectRatio);
                        diagramViewHeight = (int)Math.round(((double)this.viewHeight / (double)this.imageHeight) * (double)diagramImageHeight);
                        diagramViewWidth = (int)Math.round((double)diagramViewHeight * viewAspectRatio);
                    }
                    else
                    {                        
                        diagramImageWidth = this.bitmap.getWidth() - (2 * ZoomDiagram.PADDING);
                        diagramImageHeight = (int)Math.round((double)diagramImageWidth * imageHeightAspectRatio);
                        diagramViewWidth = (int)Math.round(((double)this.viewWidth / (double)this.imageWidth) * (double)diagramImageWidth);
                        diagramViewHeight = (int)Math.round((double)diagramViewWidth * viewHeightAspectRatio);
                    }
                }
                
                if (diagramImageHeight > (this.bitmap.getHeight() - (2 * ZoomDiagram.PADDING)))
                {
                    double ratio = ((double)this.bitmap.getHeight() - (double)(2 * ZoomDiagram.PADDING)) / (double)diagramImageHeight;
                    diagramImageHeight = (int)Math.round((double)diagramImageHeight * ratio);
                    diagramImageWidth = (int)Math.round((double)diagramImageHeight * imageAspectRatio);
                    diagramViewHeight = (int)Math.round((double)diagramViewHeight * ratio);
                    diagramViewWidth = (int)Math.round((double)diagramViewHeight * viewAspectRatio);
                }
                if (diagramViewHeight > (this.bitmap.getHeight() - (2 * ZoomDiagram.PADDING)))
                {
                    double ratio = ((double)this.bitmap.getHeight() - (double)(2 * ZoomDiagram.PADDING)) / (double)diagramViewHeight;
                    diagramImageHeight = (int)Math.round((double)diagramImageHeight * ratio);
                    diagramImageWidth = (int)Math.round((double)diagramImageHeight * imageAspectRatio);
                    diagramViewHeight = (int)Math.round((double)diagramViewHeight * ratio);
                    diagramViewWidth = (int)Math.round((double)diagramViewHeight * viewAspectRatio);
                }
                if (diagramImageWidth > (this.bitmap.getWidth() - (2 * ZoomDiagram.PADDING)))
                {
                    double ratio = ((double)this.bitmap.getWidth() - (double)(2 * ZoomDiagram.PADDING)) / (double)diagramImageWidth;
                    diagramImageWidth = (int)Math.round((double)diagramImageWidth * ratio);
                    diagramImageHeight = (int)Math.round((double)diagramImageWidth * imageHeightAspectRatio);
                    diagramViewWidth = (int)Math.round((double)diagramViewWidth * ratio);
                    diagramViewHeight = (int)Math.round((double)diagramViewHeight * viewHeightAspectRatio);
                }
                if (diagramViewWidth > (this.bitmap.getWidth() - (2 * ZoomDiagram.PADDING)))
                {
                    double ratio = ((double)this.bitmap.getWidth() - (double)(2 * ZoomDiagram.PADDING)) / (double)diagramViewWidth;
                    diagramImageWidth = (int)Math.round((double)diagramImageWidth * ratio);
                    diagramImageHeight = (int)Math.round((double)diagramImageWidth * imageHeightAspectRatio);
                    diagramViewWidth = (int)Math.round((double)diagramViewWidth * ratio);
                    diagramViewHeight = (int)Math.round((double)diagramViewHeight * viewHeightAspectRatio);
                }
                
                int diagramViewTop = (this.bitmap.getHeight() / 2) - (diagramViewHeight / 2);
                int diagramViewLeft = (this.bitmap.getWidth() / 2) - (diagramViewWidth / 2);
                
                int diagramImageTop = (this.bitmap.getHeight() / 2) - (diagramImageHeight / 2);
                int diagramImageLeft = (this.bitmap.getWidth() / 2) - (diagramImageWidth / 2);
                
                double minLeft = diagramImageLeft;
                double maxLeft = diagramImageLeft + diagramImageWidth - diagramViewWidth;
                double deltaLeft = maxLeft - minLeft;
                
                double minTop = diagramImageTop;
                double maxTop = diagramImageTop + diagramImageHeight - diagramViewHeight;
                double deltaTop = maxTop - minTop;
                
                if (this.imageHeight > this.viewHeight)
                {
                    diagramViewTop = (int)Math.round((double)diagramImageTop + (this.scrollTop * deltaTop));
                }
                if (this.imageWidth > this.viewWidth)
                {
                    diagramViewLeft = (int)Math.round((double)diagramImageLeft + (this.scrollLeft * deltaLeft));
                }
                
                
                this.drawImage(diagramImageLeft, diagramImageTop, diagramImageWidth, diagramImageHeight, transaction);
                this.drawRectangle(diagramViewLeft, diagramViewTop, diagramViewWidth, diagramViewHeight, new Pixel(Color.WHITE), transaction);
                
                this.bitmap.processTransaction(transaction);
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
        this.thread.stop();
    }
}
