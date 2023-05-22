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
import java.util.Objects;
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
     * Class representing transaction done over bitmap
     */
    public static class BitmapTransaction
    {
        /**
         * Items done in transaction
         */
        protected final List<TransactionItem> items;
        
        /**
         * Class representing one transaction item
         */
        public static class TransactionItem
        {
            /**
             * X coordinate of pixel which will be changed
             */
            private final int x;
            
            /**
             * Y coordinate of pixel which will be changed
             */
            private final int y;
            
            /**
             * New value of pixel
             */
            private final Pixel px;
            
            /**
             * Creates new item of transaction
             * @param x X coordinate of pixel which will be changed
             * @param y Y coordinate of pixel which will be changed
             * @param px New value of pixel
             */
            public TransactionItem(int x, int y, Pixel px)
            {
                this.x = x;
                this.y = y;
                this.px = px;
            }
            
            /**
             * Gets X coordinate of changed pixel
             * @return X coordinate of changed pixel
             */
            public int getX()
            {
                return this.x;
            }
            
            /**
             * Gets Y coordinate of changed pixel
             * @return Y coordinate of changed pixel
             */
            public int getY()
            {
                return this.y;
            }
            
            /**
             * Gets new value of pixel
             * @return New value of pixel
             */
            public Pixel getValue()
            {
                return this.px;
            }
        }
        
        /**
         * Creates new bitmap transaction
         */
        public BitmapTransaction()
        {
            this.items = new ArrayList<>();
        }
        
        /**
         * Sets pixel in bitmap
         * @param x X coordinate of pixel
         * @param y Y coordinate of pixel
         * @param px New value of pixel
         */
        public void setPixel(int x, int y, Pixel px)
        {
            this.items.add(new TransactionItem(x, y, px));
        }
        
        /**
         * Gets list of transaction items which should be processed over pixels in bitmap
         * @return List of transaction items
         */
        public List<BitmapTransaction.TransactionItem> getItems()
        {
            return this.items;
        }
    }
    
    /**
     * List of listeners on bitmap change action
     */
    private final List<Bitmap.BitmapChangedActionListener> changeActionListeners;
    
    /**
     * Width of bitmap
     */
    protected final int width;
    
    /**
     * Height of bitmap
     */
    protected final int height;
    
    /**
     * Data of bitmap
     */
    protected final int[][] data;
    
    /**
     * Original data of bitmap
     */
    protected final int[][]original;
        
    /**
     * Graphical representation of bitmap
     */
    protected final WritableImage image;
    
    /**
     * Flag, whether original state of bitmap has been set (TRUE) or not (FALSE)
     */
    protected boolean originalSet = false;
    
    /**
     * Maximal value of intensity of pixel in bitmap
     */
    protected int maxIntensity = Integer.MIN_VALUE;
    
    /**
     * Minimal value of intensity of pixel in bitmap
     */
    protected int minIntensity = Integer.MAX_VALUE;
    
    /**
     * Creates new empty bitmap
     * @param width Width of bitmap
     * @param height Height of bitmap
     */
    public Bitmap(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.data = new int[this.height][this.width];
        this.original = new int[this.height][this.width];
        this.changeActionListeners = new ArrayList<>();
        this.image = new WritableImage(this.width, this.height);
    }
    
    /**
     * Sets actual state of bitmap as original one
     */
    public void setOriginal()
    {
        if (this.originalSet == false)
        {
            this.originalSet = true;
            for (int y = 0; y < this.height; y++)
            {
                for (int x = 0; x < this.width; x++)
                {
                    this.original[y][x] = this.data[y][x];
                }
            }
            this.invokeChange();
        }
        else
        {
            throw new IllegalStateException("Cannot set original state of bitmap: original state already set!");
        }
    }
    
    /**
     * Gets original value of pixel
     * @param x X coordinate of pixel
     * @param y Y coordinate of pixel
     * @return Original value of pixel
     */
    public Pixel getOriginal(int x, int y)
    {
        Pixel reti = null;
        if (this.originalSet == false)
        {
            throw new IllegalStateException("Cannot get original state of bitmap: original state has not been set!");
        }
        else
        {
            reti = new Pixel(this.original[y][x]);
        }
        return reti;
    }
    
    /**
     * Removes applied changes
     */
    public void removeChanges()
    {
        Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
        for (int y = 0; y < this.height; y++)
        {
            for (int x = 0; x < this.width; x++)
            {
                transaction.setPixel(x, y, this.getOriginal(x, y));
            }
        }
        this.processTransaction(transaction);
    }
    
    /**
     * Invokes all action listeners on bitmap change
     */
    protected void invokeChange()
    {
        for(Bitmap.BitmapChangedActionListener listener: this.changeActionListeners)
        {
            listener.onChange(this);
        }       
}
    
    /**
     * Processes transaction over pixels in bitmap
     * @param transaction Transaction which will be processed
     */
    public void processTransaction(BitmapTransaction transaction)
    {
        for(BitmapTransaction.TransactionItem item: transaction.getItems())
        {
            this.setPixel(item.getX(), item.getY(), item.getValue(), false);
        }
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
    protected boolean isInBitmap(int x, int y)
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
            reti = new Pixel(this.data[y][x]);
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
        this.setPixel(x, y, px, true);
    }
    
    /**
     * Sets value of pixel in bitmap
     * @param x Position of pixel on X axis
     * @param y Position of pixel on Y axis
     * @param px New value of pixel
     * @param inform Flag, whether listeners should be informed about bitmap change
     */
    private void setPixel(int x, int y, Pixel px, boolean inform)
    {
        if (this.isInBitmap(x, y))
        {
            PixelWriter pw = this.image.getPixelWriter();
            this.data[y][x] = px.toARGB();
            int intensity = Globals.INTENSITY.apply(px);
            if (intensity > this.maxIntensity) maxIntensity = intensity;
            if (intensity < this.minIntensity) minIntensity = intensity;
            pw.setColor(x, y, px.toColor());
            if (inform == true)
            {
                this.invokeChange();
            }
        }
    }
    
    /**
     * Gets maximal value of pixel intensity of actual bitmap
     * @return Maximal pixel intensity
     */
    public int getMaxIntensity()
    {
        return this.maxIntensity;
    }
    
    /**
     * Gets minimal value of pixel intensity of actual bitmap
     * @return Minimal pixel intensity
     */
    public int getMinIntensity()
    {
        return this.minIntensity;
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
