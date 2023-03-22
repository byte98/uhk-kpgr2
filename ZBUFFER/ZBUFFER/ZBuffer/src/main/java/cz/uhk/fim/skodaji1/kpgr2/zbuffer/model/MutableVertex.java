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
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing mutable version of vertex
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class MutableVertex extends Vertex implements Mutable
{
    /**
     * Counter of created vertices
     */
    private static long COUNTER = 0;
    
    /**
     * List with all handlers of object change
     */
    private final List<ObjectChangeCallback> handlers;
    
    /**
     * Name of vertex
     */
    private final String name;
    
    /**
     * Creates new vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     * @param parent Primitive to which vertex belongs to
     */
    public MutableVertex(double x, double y, double z, Primitive parent)
    {
        this(String.format("Vrchol_%03d", MutableVertex.COUNTER), x, y, z, parent);
    }
    
    /**
     * Creates new vertex
     * @param name Name of vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     * @param parent Primitive to which vertex belongs to
     */
    public MutableVertex(String name, double x, double y, double z, Primitive parent)
    {
        super(x, y, z, parent);
        this.name =  name;
        this.handlers = new ArrayList<>();
        MutableVertex.COUNTER++;
    }
    
    /**
     * Creates new vertex
     * @param v Vertex from which data will be copied
     */
    public MutableVertex(Vertex v)
    {
        this(v.getX(), v.getY(), v.getZ(), v.parent);
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
            this.informChange();
        }
        else if (property.toLowerCase().trim().equals("y"))
        {
            this.position.y = value;
            this.informChange();
        }
        else if (property.toLowerCase().trim().equals("z"))
        {
            this.position.z = value;
            this.informChange();
        }
    }

    @Override
    public void set(String property, int value) {}

    @Override
    public void set(String property, Col value) {}

    @Override
    public void addChangeCallback(ObjectChangeCallback callback)
    {
        this.handlers.add(callback);
    }
    
    /**
     * Informs all handlers about change of object
     */
    protected void informChange()
    {
        for (ObjectChangeCallback callback: this.handlers)
        {
            callback.objectChanged();
        }
    }
    
    @Override
    public String[] getAllowedValues(String enumName)
    {
        return new String[0];
    }

    @Override public String getEnumValue(String enumName)
    {
        return "";
    }
    
    @Override public void setEnum(String property, String value){}
    
    @Override
    public String toString()
    {
        return String.format("%s %s", this.name, super.toString());
    }
    
    @Override
    public boolean equals(Object obj)
    {
        boolean reti = false;
        if (obj instanceof MutableVertex)
        {
            MutableVertex vertex = (MutableVertex) obj;
            reti = this.id == vertex.id;
        }
        return reti;
    }
    
    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}
