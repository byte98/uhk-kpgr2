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

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing part of solid
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Part extends MutableAdapter
{
    /**
     * Counter of created parts
     */
    private static long COUNTER = 0;
    
    /**
     * List of primitives which creates part
     */
    private final List<Primitive> primitives;
    
    /**
     * Type of primitive stored in list
     */
    private final PrimitiveType type;
    
    /**
     * Name of part
     */
    private final String name;
    
    /**
     * Creates new part
     * @param type Type of primitives which creates part
     */
    public Part(PrimitiveType type)
    {
        this(type, String.format("Cast_%03d", Part.COUNTER));
    }
    
    /**
     * Creates new part
     * @param type Type of primitives which creates part
     * @param name Name of part
     */
    public Part(PrimitiveType type, String name)
    {
        this.type = type;
        this.primitives = new ArrayList<>();
        this.name = name;
        Part.COUNTER++;
    }
     
    /**
     * Gets name of part
     * @return Name of part
     */
    public String getName()
    {
        return this.name;
    }

    /**
     * Gets primitives which creates part
     * @return List with primitives
     */
    public List<Primitive> getPrimitives()
    {
        return this.primitives;
    }
    
    /**
     * Adds primitive to part
     * @param p Primitive, which will be added to part
     */
    public void addPrimitive(Primitive p)
    {
        this.primitives.add(p);
        this.informChange();
    }
    
    /**
     * Removes all primitives from part
     */
    public void removeAllPrimitives()
    {
        this.primitives.clear();
    }
    
    /**
     * Removes primitive from part
     * @param p Primitive which will be removed
     */
    public void removePrimitive(Primitive p)
    {
        this.primitives.remove(p);
    }
    
    /**
     * Gets type of primitives stored in part
     * @return Type of primitives stored in part
     */
    public PrimitiveType getPrimitiveType()
    {
        return this.type;
    }
    
    /**
     * Gets number of primitives which creates part
     * @return Number of primitives
     */
    public int getPrimitivesCount()
    {
        return this.primitives.size();
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název", "Typ"};
    }

    @Override
    public Class getType(String property)
    {
        return String.class;
    }

    @Override
    public String getString(String property)
    {
        String reti = null;
        if (property.toLowerCase().trim().equals("název"))
        {
            reti = this.name;
        }
        else if (property.toLowerCase().trim().equals("typ"))
        {
            switch(this.type)
            {
                case TRIANGLE: reti = "Trojúhelníky"; break;
                case LINE: reti = "Úsečky"; break;
                case BICUBICS: reti = "Bikubické plochy"; break;
            }
        }
        return reti;
    }
}
