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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import javafx.scene.paint.Color;

/**
 * Class representing temperature effect
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Temperature extends AbstractEffect
{
    /**
     * Actually set value of temperature
     */
    private int value;
    
    /**
     * Creates new temperature effect
     * @param bitmap Reference to bitmap used to create histogram
     */
    public Temperature(Bitmap bitmap)
    {
        super(ThreadManager.createHistogram(
                (Pixel px) ->
                {
                    return (px.getRed() - px.getBlue()) + 255;
                },
                bitmap,
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 255),
                Color.rgb(255, 0, 0),
                512,
                true
        ));
    }
    
    /**
     * Sets actual value of temperature effect
     * @param temperature New value of temperature effect
     */
    public void setTemperature(int temperature)
    {
        this.value = temperature;
        this.invokeChange();
    }
    
    /**
     * Gets actually set value of temperature
     * @return Actual value of temperature
     */
    public int getTemperature()
    {
        return this.value;
    }

    @Override
    public Pixel apply(Pixel pixel)
    {
        short r = (short)(pixel.getRed() + this.value);
        short g = pixel.getGreen();
        short b = (short)(pixel.getBlue() - this.value);
        short a = pixel.getAlpha();
        if (r > 255) r = 255; if (r < 0) r = 0;
        if (b > 255) b = 255; if (b < 0) b = 0;
        return new Pixel(r, g, b, a);
    }
    
}
