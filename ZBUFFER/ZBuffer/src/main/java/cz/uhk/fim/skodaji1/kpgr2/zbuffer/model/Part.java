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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model;

import cz.uhk.fim.kpgr2.transforms.Col;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing part of solid
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Part implements Mutable
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
     * Name of part
     */
    private final String name;
    
    /**
     * Creates new part
     */
    public Part()
    {
        this.primitives = new ArrayList<>();
        this.name = String.format("Cast_%03d", Part.COUNTER);
        Part.COUNTER++;
    }
    
    /**
     * Creates new part
     * @param name Name of part
     */
    public Part(String name)
    {
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
    }
    
    /**
     * Removes all primitives from part
     */
    public void removeAllPrimitives()
    {
        this.primitives.clear();
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název"};
    }

    @Override
    public boolean isMutable(String property)
    {
        return false;
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
        return reti;
    }

    @Override
    public double getDouble(String property)
    {
        return Double.NaN;
    }

    @Override
    public int getInt(String property)
    {
        return Integer.MIN_VALUE;
    }

    @Override
    public Col getColour(String property)
    {
        return new Col(Color.WHITE.getRGB());
    }

    @Override
    public void set(String property, String value)
    {
        // pass
    }

    @Override
    public void set(String property, double value)
    {
        // pass
    }

    @Override
    public void set(String property, int value)
    {
        // pass
    }

    @Override
    public void set(String property, Col value)
    {
        // pass
    }
    
}
