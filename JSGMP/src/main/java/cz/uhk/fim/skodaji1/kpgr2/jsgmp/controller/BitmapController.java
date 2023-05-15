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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Effect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Controller of operations over bitmap
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class BitmapController
{
    /**
     * Bitmap which will be controller by this controller
     */
    private final Bitmap bitmap;
    
    /**
     * Creates new controller of operations over bitmap
     * @param bitmap Bitmap which will be controlled
     */
    public BitmapController(Bitmap bitmap)
    {
        this.bitmap = bitmap;
    }
    
    /**
     * Applies effect on bitmap
     * @param effect Effect which will be applied
     */
    public void applyEffect(Effect effect)
    {
        Runnable task = new Runnable()
        {
            @Override
            public void run()
            {
                BitmapController.this.bitmap.startTransaction();
                ExecutorService exec = Executors.newFixedThreadPool(BitmapController.this.bitmap.getHeight());
                List<Callable<Void>> tasks = new ArrayList<>();
                for (int row = 0; row < BitmapController.this.bitmap.getHeight(); row++)
                {
                    tasks.add(BitmapController.this.applyEffect(effect, row));
                }
                /*try
                {
                    exec.invokeAll(tasks);*/
                    BitmapController.this.bitmap.finishTransaction();/*
                }/*
                catch (InterruptedException ex)
                {
                    Logger.getLogger(BitmapController.class.getName()).log(Level.SEVERE, null, ex);
                    BitmapController.this.bitmap.finishTransaction();
                }*/
            }            
        };
        Thread t = new Thread(task);
        t.start();
    }
    
    /**
     * Applies effect on row of bitmap
     * @param effect Effect which will be applied
     * @param row Number of row on which effect will be applied
     * @return Runnable task which applies effect
     */
    private Callable<Void> applyEffect(Effect effect, int row)
    {
        return new Callable<Void>()
        {    
            @Override
            public Void call() throws Exception
            {
                Void reti = null;
                for (int col = 0; col < BitmapController.this.bitmap.getWidth(); col++)
                {
                    BitmapController.this.bitmap.setPixel(col, row, effect.apply(BitmapController.this.bitmap.getPixel(col, row)));
                }
                return reti;
            }
        };
    }
}
