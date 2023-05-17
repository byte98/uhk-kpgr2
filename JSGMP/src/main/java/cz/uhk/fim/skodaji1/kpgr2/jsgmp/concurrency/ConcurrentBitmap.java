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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

/**
 * Class which represents bitmap which can work across multiple threads
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class ConcurrentBitmap extends Bitmap implements Threadable
{
    /**
     * Counter of created bitmaps
     */
    private static long counter = 0;
    
    /**
     * Timeout for polling actions from queue (in microseconds)
     */
    private static final long POLL_TIMEOUT = 1;
        
    /**
     * Class which represents 'set pixel' action
     */
    private static class BitmapSetter
    {
        /**
         * Enumeration of all transaction states
         */
        public enum TransactionState
        {
            /**
             * Transaction is started
             */
            START,
            
            /**
             * Transaction is finished
             */
            FINISH
        }
        
        
        /**
         * X coordinate of pixel
         */
        private final int x;
        
        /**
         * Y coordinate of pixel
         */
        private final int y;
        
        /**
         * New data of pixel
         */
        private final Pixel px;
        
        /**
         * Transaction which will be processed
         */
        private final Bitmap.BitmapTransaction transaction;
        
        /**
         * Flag, whether setter contains transaction
         */
        private final boolean isTransaction;
        
        /**
         * Creates new set pixel action
         * @param x X coordinate of pixel
         * @param y Y coordinate of pixel
         * @param px New data of pixel
         */
        public BitmapSetter(int x, int y, Pixel px)
        {
            this.x = x;
            this.y = y;
            this.px = px;
            this.transaction = null;
            this.isTransaction = false;
        }
        
        /**
         * Creates new set pixel action
         * @param transaction Transaction which will be processed
         */
        public BitmapSetter(Bitmap.BitmapTransaction transaction)
        {
            this.x = 0;
            this.y = 0;
            this.px = new Pixel((short)0, (short)0, (short)0);
            this.transaction = transaction;
            this.isTransaction = true;
        }
        
        /**
         * Checks, whether setter contains transaction
         * @return TRUE if setter contains transaction, FALSE otherwise
         */
        public boolean isTransaction()
        {
            return this.isTransaction;
        }
        
        /**
         * Gets transaction which should be processed over pixels in bitmap
         * @return Transaction which should be processed over pixels in bitmap
         */
        public Bitmap.BitmapTransaction getTransaction()
        {
            return this.transaction;
        }
        
        /**
         * Gets X coordinate of pixel
         * @return X coordinate of pixel
         */
        public int getX()
        {
            return this.x;
        }
        
        /**
         * Gets Y coordinate of pixel
         * @return Y coordinate of pixel
         */
        public int getY()
        {
            return this.y;
        }
        
        /**
         * Gets new pixel value
         * @return New pixel value
         */
        public Pixel getPixel()
        {
            return this.px;
        }
    }
    
    /**
     * Thread which handles all actions over bitmap
     */
    private final Thread thread;
    
    /**
     * Flag, whether bitmap thread is running or not
     */
    private boolean running = false;
    
    /**
     * Queue with all setting actions
     */
    private final BlockingQueue<ConcurrentBitmap.BitmapSetter> setQueue;
    
    /**
     * List of listeners on bitmap change action
     */
    private final List<Bitmap.BitmapChangedActionListener> changeListeners;
        
    /**
     * Creates new bitmap which works across threads
     * @param width Width of bitmap
     * @param height Height of bitmap
     */
    public ConcurrentBitmap(int width, int height)
    {
        super(width, height);
        this.thread = new Thread(this, String.format("JSGMP:Bitmap-%d", ConcurrentBitmap.counter));
        ConcurrentBitmap.counter++;
        this.setQueue = new LinkedBlockingDeque();
        this.changeListeners = Collections.synchronizedList(new ArrayList<>());
    }

    @Override
    public void addChangeActionListener(BitmapChangedActionListener listener) {
        this.changeListeners.add(listener);
    }

    @Override
    protected void invokeChange() {
        synchronized(this.changeListeners)
        {
            this.changeListeners.forEach((action) ->
            {
                action.onChange(this);
            });
        }
    }

    @Override
    public void processTransaction(BitmapTransaction transaction)
    {
        try
        {
            ConcurrentBitmap.BitmapSetter setter = new ConcurrentBitmap.BitmapSetter(transaction);
            this.setQueue.put(setter);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ConcurrentBitmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

    @Override
    public void setPixel(int x, int y, Pixel px)
    {
        try
        {
            ConcurrentBitmap.BitmapSetter setter = new ConcurrentBitmap.BitmapSetter(x, y, px);
            this.setQueue.put(setter);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(ConcurrentBitmap.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    @Override
    public void run()
    {
        while(this.running == true)
        {
            try
            {
                ConcurrentBitmap.BitmapSetter item = this.setQueue.poll(ConcurrentBitmap.POLL_TIMEOUT, TimeUnit.MICROSECONDS);
                if (Objects.nonNull(item))
                {
                    if (item.isTransaction() == false)
                    {
                        if (this.isInBitmap(item.getX(), item.getY()))
                        {
                            this.data[item.getY()][item.getX()] = item.getPixel();
                            Platform.runLater(new Runnable(){
                                @Override
                                public void run()
                                {
                                    PixelWriter pw = ConcurrentBitmap.this.image.getPixelWriter();
                                    pw.setColor(item.getX(), item.getY(), Color.rgb(item.getPixel().getRed(), item.getPixel().getGreen(), item.getPixel().getBlue(), (double)item.getPixel().getAlpha() / 255f));
                                }
                            });
                            this.invokeChange();
                        }
                    }
                    else
                    {
                        for(Bitmap.BitmapTransaction.TransactionItem t: item.getTransaction().getItems())
                        {
                            if (this.isInBitmap(t.getX(), t.getY()))
                            {
                                this.data[t.getY()][t.getX()] = t.getValue();
                            }
                        }
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run()
                            {
                                PixelWriter pw = ConcurrentBitmap.this.image.getPixelWriter();
                                for(Bitmap.BitmapTransaction.TransactionItem t: item.getTransaction().getItems())
                                {
                                    if (ConcurrentBitmap.this.isInBitmap(t.getX(), t.getY()))
                                    {
                                        pw.setColor(t.getX(), t.getY(), Color.rgb(t.getValue().getRed(), t.getValue().getGreen(), t.getValue().getBlue(), (double)t.getValue().getAlpha() / 255f));
                                    }
                                }
                            }                        
                        });
                        this.invokeChange();
                    }
                }
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(ConcurrentBitmap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
    
}
