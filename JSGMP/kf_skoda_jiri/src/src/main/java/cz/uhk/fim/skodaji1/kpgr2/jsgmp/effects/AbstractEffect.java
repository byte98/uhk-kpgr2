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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class with default implementation of effect
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public abstract class AbstractEffect implements Effect
{
    /**
     * List of all listeners on 'effect changed' effect
     */
    private final List<EffectChangedListener> listeners;
    
    /**
     * Histogram displaying actual data according to effect
     */
    private final Histogram histogram;
        
    /**
     * Creates new abstract effect
     * @param histogram Histogram displaying actual data according to effect
     */
    public AbstractEffect(Histogram histogram)
    {
        this.listeners = Collections.synchronizedList(new ArrayList<>());
        this.histogram = histogram;
    }
    
    /**
     * Gets histogram of effect
     * @return Histogram displaying bitmap according to the effect
     */
    public Histogram getHistogram()
    {
        return this.histogram;
    }

    @Override
    public void addEffectChangedListener(EffectChangedListener listener)
    {
        this.listeners.add(listener);
    }
    
    /**
     * Informs all change listeners about change
     */
    protected void invokeChange()
    {
        for(EffectChangedListener listener: this.listeners)
        {
            listener.effectChanged();
        }
    }
    
}
