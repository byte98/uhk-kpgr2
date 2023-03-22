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
import cz.uhk.fim.kpgr2.transforms.Point3D;
import java.awt.Color;

/**
 * Class representing mutable version of vertex
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class MutableVertex extends Vertex implements Mutable
{
    /**
     * Name of vertex
     */
    private final String name;
    
    /**
     * Creates new vertex
     * @param name Name of vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     */
    public MutableVertex(String name, double x, double y, double z)
    {
        super(x, y, z);
        this.name =  name;
    }

    /**
     * Gets name of vertex
     * @return Name of vertex
     */
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název", "X", "Y", "Z"};
    }

    @Override
    public boolean isMutable(String property)
    {
        boolean reti = true;
        if (property.toLowerCase().trim().equals("název"))
        {
            reti = false;
        }
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = Double.class;
        if (property.toLowerCase().trim().equals("název"))
        {
            reti = String.class;
        }
        return reti;
    }

    @Override
    public String getString(String property)
    {
        return this.name;
    }

    @Override
    public double getDouble(String property)
    {
        double reti = Double.NaN;
        switch(property.toLowerCase().trim())
        {
            case "x": reti = this.position.x; break;
            case "y": reti = this.position.y; break;
            case "z": reti = this.position.z; break;
        }
        return reti;
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
    public void set(String property, String value){}

    @Override
    public void set(String property, double value)
    {
        if (property.toLowerCase().trim().equals("x"))
        {
            this.position.x = value;
        }
        else if (property.toLowerCase().trim().equals("y"))
        {
            this.position.y = value;
        }
        else if (property.toLowerCase().trim().equals("z"))
        {
            this.position.z = value;
        }
    }

    @Override
    public void set(String property, int value) {}

    @Override
    public void set(String property, Col value) {}
    
}
