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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class representing transformation of scale
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Scale extends MutableAdapter implements Transformation
{
    /**
     * Scale on X axis
     */
    private double x;
    
    /**
     * Scale on Y axis
     */
    private double y;
    
    /**
     * Scale on Z axis
     */
    private double z;
    
    /**
     * Creates new scale transformation
     * @param x Scale on X axis
     * @param y Scale on Y axis
     * @param z Scale on Z axis
     */
    public Scale(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    /**
     * Creates new scale transformation
     */
    public Scale()
    {
        this(0, 0, 0);
    }

    @Override
    public Vertex apply(Vertex v)
    {
        return null;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"X", "Y", "Z"};
    }
    
    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.trim().toLowerCase();
        if (propName.equals("x"))
        {
            reti = this.x;
        }
        else if (propName.equals("y"))
        {
            reti = this.y;
        }
        else if (propName.equals("z"))
        {
            reti = this.z;
        }
        return reti;
    }
    
    @Override
    public void set(String property, double value)
    {
        String propName = property.trim().toLowerCase();
        if (propName.equals("x"))
        {
            this.x = value;
        }
        else if (propName.equals("y"))
        {
            this.y = value;
        }
        else if (propName.equals("z"))
        {
            this.z = value;
        }
    }
}
