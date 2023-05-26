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

/**
 * Interface abstracting all effects which could be applied on image
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public interface Effect
{
    /**
     * Interface abstracting listener of effect change
     */
    public interface EffectChangedListener
    {
        /**
         * Function which will be called when effect changed
         */
        public abstract void effectChanged();
    }
    
    /**
     * Adds listener to effect changed event
     * @param listener Object which will be informed about effect change
     */
    public abstract void addEffectChangedListener(Effect.EffectChangedListener listener);
    
    /**
     * Applies effect on image
     * @param pixel Pixel on which effect will be applied
     * @return Pixel with applied effect
     */
    public abstract Pixel apply(Pixel pixel);
    
}
