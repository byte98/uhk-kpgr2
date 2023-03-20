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
import java.util.Arrays;
import java.util.List;

/**
 * Interface abstracting parts primitives
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public interface Primitive extends Mutable
{    
    /**
     * Gets vertices which creates primitive
     * @return List of vertices in primitive
     */
    public abstract List<MutableVertex> getVertices();
    
    /**
     * Adds vertex to primitive
     * @param v Vertex which will be added
     */
    public abstract void addVertex(MutableVertex v);
    
    /**
     * Removes all vertices from primitive
     */
    public abstract void removeAllVertices();
    
    /**
     * Gets number of vertices which creates primitive
     * @return Number of vertices in primitive
     */
    public abstract int getVerticesCount();
    
    /**
     * Gets name of primitive
     * @return Name of primitive
     */
    public abstract String getName();
    
    /**
     * Gets type of primitive
     * @return Type of primitive
     */
    public abstract PrimitiveType getPrimitiveType();
    
    /**
     * Sets fill of primitive
     * @param fill New fill of primitive
     */
    public abstract void setFill(Fill fill);
    
    /**
     * Gets fill of primitive
     * @return Fill of primitive
     */
    public abstract Fill getFill();
    
    /**
     * Informs all registered handlers about object change
     */
    public abstract void informChange();
    
    @Override
    default public String[] getProperties()
    {
        List<String> list = new ArrayList<>();
        list.add("Název");
        list.add("Typ");
        list.addAll(Arrays.asList(this.getFill().getProperties()));
        String[] reti = new String[list.size()];
        for(int i = 0; i < list.size(); i++)
        {
            reti[i] = list.get(i);
        }
        return reti;
    }
    
    @Override
    default public boolean isMutable(String property)
    {
        boolean reti = false;
        if (property.trim().toLowerCase().equals("název") == false && 
            property.trim().toLowerCase().equals("typ") == false)
        {
            reti = this.getFill().isMutable(property);
        }
        return reti;
    }
    
    @Override
    default public Class getType(String property)
    {
        Class reti = String.class;
        if (property.trim().toLowerCase().equals("název") == false && 
            property.trim().toLowerCase().equals("typ") == false)
        {
            reti = this.getFill().getType(property);
        }
        return reti;
    }
    
    @Override
    default public String getString(String property)
    {
        String reti = "";
        if (property.toLowerCase().trim().equals("název"))
        {
            reti = this.getName();
        }
        else if (property.toLowerCase().trim().equals("typ"))
        {
            switch(this.getPrimitiveType())
            {
                case TRIANGLE: reti = "Trojúhelník"; break;
                case LINE: reti = "Úsečka"; break;
            }
        }
        else
        {
            reti = this.getFill().getString(property);
        }
        return reti;
    }
    
    @Override
    default public int getInt(String property)
    {
        return this.getFill().getInt(property);
    }
    
    @Override
    default public double getDouble(String property)
    {
        return this.getFill().getDouble(property);
    }
    
    @Override
    default public Col getColour(String property)
    {
        return this.getFill().getColour(property);
    }
    
    @Override
    default public void set(String property, int value)
    {
        this.getFill().set(property, value);
    }
    
    @Override
    default public void set(String property, double value)
    {
        this.getFill().set(property, value);
    }
    
    @Override 
    default public void set(String property, String value)
    {
        this.getFill().set(property, value);
    }
    
    @Override
    default public void set(String property, Col value)
    {
        this.getFill().set(property, value);
    }    

    @Override
    default public void setEnum(String property, String value)
    {
        this.getFill().setEnum(property, value);
    }

    @Override
    default public String getEnumValue(String enumName)
    {
        return this.getFill().getEnumValue(enumName);
    }

    @Override
    default public String[] getAllowedValues(String enumName)
    {
        return this.getFill().getAllowedValues(enumName);
    }
    
    
}
