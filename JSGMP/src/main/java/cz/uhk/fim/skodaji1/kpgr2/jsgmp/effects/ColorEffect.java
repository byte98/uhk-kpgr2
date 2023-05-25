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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;

/**
 * Class representing coloring effect
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class ColorEffect extends AbstractEffect
{
    /**
     * Flag, whether red color channel should be changed
     */
    private final boolean red;
    
    /**
     * Flag, whether green color channel should be changed
     */
    private final boolean green;
    
    /**
     * Flag, whether blue color channel should be changed
     */
    private final boolean blue;
    
    /**
     * Actual value of effect
     */
    private int value;
    
    /**
     * Creates new coloring effect
     * @param histogram Histogram of effect
     * @param red Flag, whether red color channel should be changed
     * @param green Flag, whether green color channel should be changed
     * @param blue Flag, whether blue color channel should be changed
     */
    public ColorEffect(Histogram histogram, boolean red, boolean green, boolean blue)
    {
        super(histogram);
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.value = 0;
    }
    
    /**
     * Sets value of coloring effect
     * @param newValue New value of coloring effect
     */
    public void setValue(int newValue)
    {
        this.value = newValue;
        this.invokeChange();
    }

    @Override
    public Pixel apply(Pixel pixel)
    {
        int r = pixel.getRed();
        int g = pixel.getGreen();
        int b = pixel.getBlue();
        int a = pixel.getAlpha();
        
        if (this.red == true) r = r + this.value;
        if (this.green == true) g = g + this.value;
        if (this.blue == true) b = b + this.value;
        
        if (r < 0) r = 0; if (r > 255) r = 255;
        if (g < 0) g = 0; if (g > 255) g = 255;
        if (b < 0) b = 0; if (b > 255) b = 255;
        
        return new Pixel((short)r, (short)g, (short)b, (short)a);
    }
    
}
