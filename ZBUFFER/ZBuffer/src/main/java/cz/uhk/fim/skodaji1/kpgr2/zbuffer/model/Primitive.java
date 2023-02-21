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
import java.util.List;

/**
 * Interface abstracting parts primitives
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public interface Primitive extends Mutable
{
    /**
     * Counter of created primitives
     */
    public static long COUNTER = 0;
    
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
    
    @Override
    default public String[] getProperties()
    {
        return new String[]{"Název", "Typ"};
    }
    
    @Override
    default public boolean isMutable(String property)
    {
        return false;
    }
    
    @Override
    default public Class getType(String property)
    {
        return String.class;
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
                case TRIANGLE: reti = "trojúhelník"; break;
            }
        }
        return reti;
    }
    
    @Override
    default public int getInt(String property)
    {
        return Integer.MIN_VALUE;
    }
    
    @Override
    default public double getDouble(String property)
    {
        return Double.NaN;
    }
    
    @Override
    default public Col getColour(String property)
    {
        return new Col(Color.WHITE.getRGB());
    }
    
    @Override default public void set(String property, int value){}
    @Override default public void set(String property, double value){}
    @Override default public void set(String property, String value){}
    @Override default public void set(String property, Col value){}
}
