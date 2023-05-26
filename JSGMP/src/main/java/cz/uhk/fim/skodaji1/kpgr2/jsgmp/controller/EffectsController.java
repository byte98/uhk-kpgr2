/*
 * Copyright (C) 2023 Jiri Skoda <jiri.skoda@uhk.cz>
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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.Threadable;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Effect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class which handles all effects applied on bitmap
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class EffectsController implements Threadable, Effect.EffectChangedListener
{
    /**
     * Interval between checking, whether any effect has changed (in miliseconds)
     */
    private static final int SLEEP = 250;
    
    /**
     * Counter of created effect controllers
     */
    private static long counter = 0;
    
    /**
     * Bitmap on which effects will be applied
     */
    private final Bitmap bitmap;
    
    /**
     * List with all effects which will be applied on bitmap
     */
    private final List<Effect> effects;
        
    /**
     * Thread which handles all effect application
     */
    private final Thread thread;
    
    /**
     * Flag, whether thread is running or not
     */
    private boolean running;
    
    /**
     * Flag, whether effects should be applied or not
     */
    private boolean apply;
    
    /**
     * Creates new controller of effects applied on bitmap
     * @param bitmap Bitmap on which effects will be applied
     */
    public EffectsController(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.effects = Collections.synchronizedList(new ArrayList<>());
        this.thread = new Thread(this, String.format("JSGMP:EffectsController-%d", EffectsController.counter));
        EffectsController.counter++;
        this.running = false;
        this.apply = false;
    }
    
    /**
     * Adds effect which will be applied on bitmap
     * @param effect Effect which will be applied on bitmap
     */
    public void addEffect(Effect effect)
    {
        synchronized (this.effects)
        {
            this.effects.add(effect);
            effect.addEffectChangedListener(this);
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

    @Override
    public void run()
    {
        while (this.running == true)
        {
            if (this.apply)
            {
                Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
                for(int y = 0; y < this.bitmap.getHeight(); y++)
                {
                    for (int x = 0; x < this.bitmap.getWidth(); x++)
                    {
                        int r = 0;
                        int g = 0;
                        int b = 0;
                        int a = 0;
                        Pixel px = this.bitmap.getOriginal(x, y);
                        for(Effect e: this.effects)
                            {
                                Pixel applied = e.apply(px);
                                r += (applied.getRed() - px.getRed());
                                g += (applied.getGreen() - px.getGreen());
                                b += (applied.getBlue() - px.getBlue());
                                a += (applied.getAlpha() - px.getAlpha());
                            }
                        r = px.getRed() + r;
                        g = px.getGreen() + g;
                        b = px.getBlue() + b;
                        a = px.getAlpha() + a;
                        if (r < 0) r = 0; if (r > 255) r = 255;
                        if (g < 0) g = 0; if (g > 255) g = 255;
                        if (b < 0) b = 0; if (b > 255) b = 255;
                        if (a < 0) a = 0; if (a > 255) a = 255;
                        Pixel newPixel = new Pixel((short)r, (short)g, (short)b, (short)a);
                        transaction.setPixel(x, y, newPixel);
                    }
                }
                this.bitmap.processTransaction(transaction);
            }
            try
            {
                Thread.sleep(EffectsController.SLEEP);
            }
            catch (InterruptedException ex)
            {
                Logger.getLogger(EffectsController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void effectChanged()
    {
        this.apply = true;
    }
}
