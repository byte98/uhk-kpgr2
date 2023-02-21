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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model;

import cz.uhk.fim.kpgr2.transforms.Col;

/**
 * Class representing fill of vertices
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Fill implements Cloneable
{
    /**
     * Type of fill
     */
    private final FillType type;
    
    /**
     * Colour of fill
     */
    private final Col colour;       
    
    /**
     * Creates new fill of vertex
     * @param colour Colour of vertex
     */
    public Fill(Col colour)
    {
        this.type = FillType.COLOR;
        this.colour = colour;
    }
    
    /**
     * Gets type of fill
     * @return Type of fill
     */
    public FillType getType()
    {
        return this.type;
    }
    
    /**
     * Gets colour of fill
     * @return Colour of fill
     */
    public Col getColour()
    {
        return this.colour;
    }
    
    /**
     * Clones this fill
     * @return Copy of this fill
     */
    public Fill clone()
    {
        Fill reti = null;
        switch(this.type)
        {
            case COLOR: reti = new Fill(new Col(this.colour)); break;
            default: reti = null;
        }
        return reti;
    }
}
