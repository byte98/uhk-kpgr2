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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Effect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;

/**
 * Class which handles applying effect on bitmap
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class EffectApplier implements Threadable{

    /**
     * Thread which handles all action
     */
    private final Thread thread;
    
    /**
     * Effect which will be applied
     */
    private final Effect effect;
    
    /**
     * Bitmap on which effect will be applied
     */
    private final Bitmap bitmap;
    
    /**
     * Flag, whether thread is running
     */
    private boolean running;
    
    /**
     * Creates new applier of effect on bitmap
     * @param e Effect which will be applied
     * @param b Bitmap on which effect will be applied
     */
    public EffectApplier(Effect e, Bitmap b)
    {
        this.thread = new Thread(this);
        this.effect = e;
        this.bitmap = b;
        this.running = false;
    }
    
    @Override
    public void start()
    {
        this.thread.start();
        this.running = true;
    }

    @Override
    public void stop()
    {
        this.running = false;
    }

    @Override
    public void run()
    {
        Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
        for (int y = 0; y < this.bitmap.getHeight(); y++)
        {
            for (int x = 0; x < this.bitmap.getWidth(); x++)
            {
                Pixel result = this.effect.apply(this.bitmap.getPixel(x, y));
                transaction.setPixel(x, y, result);
            }
        }
        if (this.running == false) return;
        this.bitmap.processTransaction(transaction);
    }
    
}
