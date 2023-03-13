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
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Transformation;

/**
 * Class representing solid
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Solid extends MutableAdapter
{
    /**
     * Counter of created solids
     */
    private static long COUNTER = 0;
    
    /**
     * Name of solid
     */
    private String name;
    
    /**
     * List of parts which together creates solid
     */
    private final List<Part> parts;
    
    /**
     * List of transformations performed on solid
     */
    private final List<Transformation> transformations;
    
    /**
     * Crates new solid
     */
    public Solid()
    {
        this(String.format("Teleso_%03d", Solid.COUNTER));
    }
    
    /**
     * Creates new solid
     * @param name Name of solid
     */
    public Solid(String name) 
    {
        this.name = name;
        this.parts = new ArrayList<>();
        this.transformations = new ArrayList<>();
        Solid.COUNTER++;
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
     * Adds transformation to solid
     * @param t Transformation which will be added
     */
    public void addTransformation(Transformation t)
    {
        this.transformations.add(t);
    }
    
    /**
     * Gets number of transformations performed on solid
     * @return Number of transformations performed on solid
     */
    public int getTransformationsCount()
    {
        return this.transformations.size();
    }
    
    /**
     * Removes all transformations performed on solid
     */
    public void removeAllTransformations()
    {
        this.transformations.clear();
    }
    
    /**
     * Gets list of transformations performed on solid
     * @return List of transformations performed on solid
     */
    public List<Transformation> getTransformations()
    {
        return this.transformations;
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
        this.informChange();
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
    public void set(String property, String value)
    {
        if (property.toLowerCase().trim().equals("název"))
        {
            this.setName(value);
        }
    }
}
