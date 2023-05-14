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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.model;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;


/**
 * Class which holds whole bitmap image
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Bitmap implements Iterable<Pixel>
{
    /**
     * Interface defining handler for bitmap change action
     */
    public static interface BitmapChangedActionListener
    {
        /**
         * Function called when bitmap changes in any matter
         * @param bitmap Bitmap which has changed
         */
        public abstract void onChange(Bitmap bitmap);
    }
    
    /**
     * List of listeners on bitmap change action
     */
    private List<Bitmap.BitmapChangedActionListener> changeActionListeners;
    
    /**
     * Width of bitmap
     */
    private final int width;
    
    /**
     * Height of bitmap
     */
    private final int height;
    
    /**
     * Data of bitmap
     */
    private final Pixel[][] data;
    
    /**
     * Flag, whether bitmap is part of transaction of change
     */
    private boolean inTransaction = false;
    
    
    /**
     * Graphical representation of bitmap
     */
    private final WritableImage image;
    
    /**
     * Creates new empty bitmap
     * @param width Width of bitmap
     * @param height Height of bitmap
     */
    public Bitmap(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.data = new Pixel[this.height][this.width];
        this.changeActionListeners = new ArrayList<>();
        this.image = new WritableImage(this.width, this.height);
    }
    
    /**
     * Invokes all action listeners on bitmap change
     */
    private void invokeChange()
    {
        if (this.inTransaction == false)
        {
            for(Bitmap.BitmapChangedActionListener listener: this.changeActionListeners)
            {
                listener.onChange(this);
            }
        }        
    }
    
    /**
     * Starts transaction of change
     */
    public void startTransaction()
    {
        this.inTransaction = true;
    }
    
    /**
     * Finishes transaction of change
     */
    public void finishTransaction()
    {
        this.inTransaction = false;
        this.invokeChange();
    }
    
    /**
     * Adds listener on change action
     * @param listener Listener which will be added
     */
    public void addChangeActionListener(Bitmap.BitmapChangedActionListener listener)
    {
        this.changeActionListeners.add(listener);
    }

    /**
     * Checks, whether position is in bitmap or not
     * @param x Position on X axis
     * @param y Position on Y axis
     * @return TRUE if position is in bitmap, FALSE otherwise
     */
    private boolean isInBitmap(int x, int y)
    {
        return (
                x >= 0 && x < this.width &&
                y >= 0 && y < this.height
        );
    }
    
    /**
     * Gets pixel at specified position
     * @param x Position on X axis
     * @param y Position on Y axis
     * @return Pixel at specified position or NULL if position is out of bitmap
     */
    public Pixel getPixel(int x, int y)
    {
        Pixel reti = null;
        if (this.isInBitmap(x, y))
        {
            reti = this.data[y][x];
        }
        return reti;
    }
    
    /**
     * Sets value of pixel in bitmap
     * @param x Position of pixel on X axis
     * @param y Position of pixel on Y axis
     * @param px New value of pixel
     */
    public void setPixel(int x, int y, Pixel px)
    {
        if (this.isInBitmap(x, y))
        {
            PixelWriter pw = this.image.getPixelWriter();
            this.data[y][x] = px;
            pw.setColor(x, y, Color.rgb(px.getRed(), px.getGreen(), px.getBlue(), (double)px.getAlpha() / 255f));
            this.invokeChange();
        }
    }
    
    
    /**
     * Gets width of bitmap
     * @return Width of bitmap
     */
    public int getWidth()
    {
        return this.width;
    }
    
    /**
     * Gets height of bitmap
     * @return Height of bitmap
     */
    public int getHeight()
    {
        return this.height;
    }
    
    /**
     * Transforms bitmap into image
     * @return Image created from bitmap
     */
    public Image toImage()
    {
        return this.image;
    }

    @Override
    public Iterator<Pixel> iterator()
    {
        return new Iterator<Pixel>()
        {
            
            /**
             * Width of bitmap
             */
            final int width = Bitmap.this.getWidth();
            
            /**
             * Height of bitmap
             */
            final int height = Bitmap.this.getHeight();
            
            /**
             * Maximal allowed index
             */
            final int max = this.width * this.height;
            
            /**
             * Actual index of pixel
             */
            int idx = 0;
            
            /**
             * Gets X coordinate from index of pixel
             * @param idx Index of pixel
             * @return X coordinate valid for actual index of pixel
             */
            private int getX(int idx)
            {
                return idx % this.width;
            }
            
            /**
             * Gets Y coordinate from index of pixel
             * @param idx Index of pixel
             * @return Y coordinate valid for actual index of pixel
             */
            private int getY(int idx)
            {
                return idx / this.width;
            }

            @Override
            public boolean hasNext()
            {
                return this.idx < this.max;
            }

            @Override
            public Pixel next()
            {
                Pixel reti = null;
                if (this.hasNext())
                {
                    reti = Bitmap.this.getPixel(this.getX(idx), this.getY(idx));
                    this.idx++;
                }
                return reti;
            }            
        };
    }
}
