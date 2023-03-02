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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.render;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import java.util.HashMap;

/**
 * Class representing camera space
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class CameraSpace extends MutableAdapter
{
    /**
     * Nearest Z coordinate which will be displayed
     */
    private double zNear;
    
    
    /**
     * Furthermost Z coordinate which will be displayed
     */
    private double zFar;
    
    /**
     * Width of camera space
     */
    private int width;
    
    /**
     * Height of camera space
     */
    private int height;
    
    /**
     * Creates new camera space
     * @param zNear Nearest Z coordinate which will be displayed
     * @param zFar Furthermost Z coordinate which will be displayed
     * @param width Width of camera space
     * @param height Height of camera space
     */
    public CameraSpace(double zNear, double zFar, int width, int height)
    {
        this.zNear = zNear;
        this.zFar = zFar;
        this.width = width;
        this.height = height;
    }
    
    /**
     * Gets nearest Z coordinate which will be displayed
     * @return Nearest Z coordinate which will be displayed
     */
    public double getZNear()
    {
        return this.zNear;
    }
    
    /**
     * Sets nearest Z coordinate which will be displayed
     * @param zNear New nearest Z coordinate which will be displayed
     */
    public void setZNear(double zNear)
    {
        this.zNear = zNear;
    }
    
    /**
     * Gets furthermost Z coordinate which will be displayed
     * @return Furthermost Z coordinate which will be displayed
     */
    public double getZFar()
    {
        return this.zFar;
    }
    
    /**
     * Sets furthermost Z coordinate which will be displayed
     * @param zFar New furthermost Z coordinate which will be displayed
     */
    public void setZFar(double zFar)
    {
        this.zFar = zFar;
    }
    
    /**
     * Gets width of camera space
     * @return Width of camera space
     */
    public int getWidth()
    {
        return this.width;
    }
    
    /**
     * Sets width of camera space
     * @param width New width of camera space
     */
    public void setWidth(int width)
    {
        this.width = width;
    }
    
    /**
     * Gets height of camera space
     * @return Height of camera space
     */
    public int getHeight()
    {
        return this.height;
    }
    
    /**
     * Sets height of camera space
     * @param height New height of camera space
     */
    public void setHeight(int height)
    {
        this.height = height;
    }

    @Override
    public String[] getProperties()
    {
        return new String[]{"Zn", "Zf", "Šířka", "Výška"};
    }
    
    
    
    @Override
    public Class getType(String property)
    {
        Class reti = Object.class;
        String propName = property.toLowerCase().trim();
        if (propName.equals("výška") || propName.equals("šířka"))
        {
            reti = Integer.class;
        }
        else if (propName.equals("zn") || propName.equals("zf"))
        {
            reti = Double.class;
        }
        return reti;
    }

    @Override
    public void set(String property, int value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("výška"))
        {
            this.setHeight(value);
        }
        else if (propName.equals("šířka"))
        {
            this.setWidth(value);
        }
    }

    @Override
    public void set(String property, double value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("zf"))
        {
            this.setZFar(value);
        }
        else if (propName.equals("zn"))
        {
            this.setZNear(value);
        }
    }

    @Override
    public int getInt(String property)
    {
        int reti = super.getInt(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("šířka"))
        {
            reti = this.getWidth();
        }
        else if (propName.equals("výška"))
        {
            reti = this.getHeight();
        }
        return reti;
    }

    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("zn"))
        {
            reti = this.getZNear();
        }
        else if (propName.equals("zf"))
        {
            reti = this.getZFar();
        }
        return reti;
    }

    
}
