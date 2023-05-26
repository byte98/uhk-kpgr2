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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing grayscale effect
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Grayscale implements Effect{
    
    /**
     * List of all listeners on 'effect changed' effect
     */
    private final List<EffectChangedListener> listeners;
    
    /**
     * Actual value of effect
     */
    private double value = 0f;
    
    /**
     * Creates new grayscale effect
     */
    public Grayscale()
    {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
    }
    
    /**
     * Sets new value of effect
     * @param newValue New value of grayscale effect
     */
    public void setValue(double newValue)
    {
        this.value = newValue;
        this.invokeChange();
    }
    
    /**
     * Applies effect on one color value
     * @param start Starting color value
     * @param target Target color value
     * @return Effect applied on one color value
     */
    private short apply(short start, short target)
    {
        double diff = (double)target - (double)start;
        double step = diff / 100f;
        double reti = (double)start + (this.value * step);
        return (short)Math.round(reti);
    }
    
    /**
     * Informs all change listeners about change
     */
    private void invokeChange()
    {
        for(EffectChangedListener listener: this.listeners)
        {
            listener.effectChanged();
        }
    }

    @Override
    public void addEffectChangedListener(EffectChangedListener listener)
    {
       this.listeners.add(listener);
    }

    @Override
    public Pixel apply(Pixel pixel)
    {
        short r = pixel.getRed();
        short g = pixel.getGreen();
        short b = pixel.getBlue();
        int target = (int)Math.round(((double)r * 0.299f) + ((double)g * 0.7152) + ((double)b * 0.0722));
        if (target < 0) target = 0; if (target > 255) target = 255;
        r = this.apply(r, (short)target);
        g = this.apply(g, (short)target);
        b = this.apply(b, (short)target);
        return new Pixel(r, g, b, pixel.getAlpha());

    }
    
}
