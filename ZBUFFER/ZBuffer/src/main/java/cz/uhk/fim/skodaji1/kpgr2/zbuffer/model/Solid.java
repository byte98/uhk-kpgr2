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
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing solid
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Solid implements Mutable
{
    /**
     * Name of solid
     */
    private String name;
    
    /**
     * List of parts which together creates solid
     */
    private final List<Part> parts;
    
    /**
     * Creates new solid
     * @param name Name of solid
     */
    public Solid(String name) 
    {
        this.name = name;
        this.parts = new ArrayList<>();
    }
    
    /**
     * Adds part to solid
     * @param p Part which will be added
     */
    public void addPart(Part p)
    {
        this.parts.add(p);
    }
    
    /**
     * Gets number of parts contained in solid
     * @return Number of parts
     */
    public int getPartsCount()
    {
        return this.parts.size();
    }
    
    /**
     * Removes all parts
     */
    public void removeAllParts()
    {
        this.parts.clear();
    }
    
    /**
     * Gets all parts
     * @return List with parts
     */
    public List<Part> getParts()
    {
        return this.parts;
    }

    /**
     * Gets name of solid
     * @return Name of solid
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Sets name of solid
     * @param name New name of solid
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název"};
    }

    @Override
    public boolean isMutable(String property)
    {
        return true;
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
        if (property.toLowerCase().trim().equals("název"))
        {
            this.name = value;
        }
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
