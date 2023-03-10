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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations;

import cz.uhk.fim.kpgr2.transforms.Mat4Transl;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class representing translation transformation
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Translation extends MutableAdapter implements Transformation
{
    /**
     * Name of translation
     */
    private String name;
    
    /**
     * Delta of coordinates on X axis
     */
    private double x;
    
    /**
     * Delta of coordinates on Y axis
     */
    private double y;
    
    /**
     * Delta of coordinates on Z axis
     */
    private double z;
    
    /**
     * Creates new translation transformation
     * @param name Name of translation
     */
    public Translation(String name)
    {
        this(name, 0, 0, 0);
    }
    
    /**
     * Creates new translation transformation
     * @param name Name of translation
     * @param x Delta of coordinates on X axis
     * @param y Delta of coordinates on Y axis
     * @param z Delta of coordinates on Z axis
     */
    public Translation(String name, double x, double y, double z)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & setters">
    /**
     * Gets delta of X coordinate
     * @return Delta of X coordinate
     */
    public double getX()
    {
        return x;
    }

    /**
     * Sets delta of X coordinate
     * @param x New delta of X coordinate
     */
    public void setX(double x)
    {
        this.x = x;
        this.informChange();
    }

    /**
     * Gets delta of Y coordinate
     * @return Delta of Y coordinate
     */
    public double getY()
    {
        return y;
    }

    /**
     * Sets delta of Y coordinate
     * @param y New delta of Y coordinate
     */
    public void setY(double y)
    {
        this.y = y;
        this.informChange();
    }

    /**
     * Gets delta of Z coordinate
     * @return Delta of Z coordinate
     */
    public double getZ()
    {
        return z;
    }

    /**
     * Sets delta of Z coordinate
     * @param z New delta of Z coordinate
     */
    public void setZ(double z)
    {
        this.z = z;
        this.informChange();
    }
    //</editor-fold>

    @Override
    public Vertex apply(Vertex v)
    {
        Vertex reti = v.clone();
        reti = reti.mul(new Mat4Transl(this.x, this.y, this.z));
        return reti;
    }

    @Override
    public String[] getProperties()
    {
        return new String[]{"Název", "X", "Y", "Z"};
    }
    
    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.trim().toLowerCase();
        if (propName.equals("x"))
        {
            reti = this.getX();
        }
        else if (propName.equals("y"))
        {
            reti = this.getY();
        }
        else if (propName.equals("z"))
        {
            reti = this.getZ();
        }
        return reti;
    }
    
    @Override
    public void set(String property, double value)
    {
        String propName = property.trim().toLowerCase();
        if (propName.equals("x"))
        {
            this.setX(value);
        }
        else if (propName.equals("y"))
        {
            this.setY(value);
        }
        else if (propName.equals("z"))
        {
            this.setZ(value);
        }
    }

    @Override
    public void set(String property, String value)
    {
        if (property.toLowerCase().trim().equals("název"))
        {
            this.setName(value);
        }
    }

    @Override
    public String getString(String property)
    {
        String reti = super.getString(property);
        if (property.toLowerCase().trim().equals("název"))
        {
            reti = this.getName();
        }
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = super.getType(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("x") || propName.equals("y") || propName.equals("z"))
        {
            reti = Double.class;
        }
        else if (propName.equals("název"))
        {
            reti = String.class;
        }
        return reti;
    }

    @Override
    public boolean isMutable(String property)
    {
        return true;
    }
    
    @Override
    public TransformationType getTransformationType()
    {
        return TransformationType.TRANSLATION;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
        this.informChange();
    }
    
    
    
}
