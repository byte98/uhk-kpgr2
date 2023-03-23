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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster;

import cz.uhk.fim.kpgr2.transforms.Col;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import java.awt.Color;

/**
 * Simple pixel provider which provides just colours
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class ColourPixelProvider extends MutableAdapter implements PixelProvider
{
    /**
     * Default fill colour
     */
    private static final Col DEFAULT_COLOUR = new Col(Color.WHITE.getRGB());
    
    /**
     * Colour which will be returned
     */
    private Col colour;
    
    /**
     * Creates new provider of static colour
     */
    public ColourPixelProvider()
    {
        this(ColourPixelProvider.DEFAULT_COLOUR);
    }
    
    /**
     * Creates new provider of static colour
     * @param colour Colour which will be returned
     */
    public ColourPixelProvider(Col colour)
    {
        this.colour = colour;
    }

    @Override
    public Col getPixel(int x, int y)
    {
        return this.colour;
    }

    @Override
    public void set(String property, Col value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva"))
        {
            this.colour = value;
            this.informChange();
        }
    }

    @Override
    public Col getColour(String property)
    {
        Col reti = super.getColour(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva"))
        {
            reti = this.colour;
        }
        return reti;
    }

    @Override
    public Class getType(String property) 
    {
        Class reti = super.getType(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva"))
        {
            reti = Col.class;
        }
        return reti;
    }

    @Override
    public String[] getProperties()
    {
        return new String[]{"Barva"};
    }

    @Override
    public boolean isMutable(String property)
    {
        boolean reti = super.isMutable(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva"))
        {
            reti = true;
        }
        return reti;
    }
    
    
    
}
