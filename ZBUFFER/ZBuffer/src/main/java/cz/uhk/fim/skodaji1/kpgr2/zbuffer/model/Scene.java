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
 * Class representing whole scene
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Scene implements Mutable
{
    /**
     * Name of scene
     */
    private String name;
    
    /**
     * Camera of scene
     */
    private MutableCamera camera;
    
    /**
     * List with all solids
     */
    private final List<Solid> solids;
    
    /**
     * Creates new scene
     * @param name Name of scene
     */
    public Scene(String name)
    {
        this.name = name;
        this.solids = new ArrayList<>();
    }
    
    /**
     * Gets name of scene
     * @return Name of scene
     */
    public String getName()
    {
        return this.name;
    }
    
    /**
     * Gets camera of scene
     * @return Camera of scene
     */
    public MutableCamera getCamera()
    {
        return this.camera;
    }
    
    /**
     * Sets new camera of scene
     * @param camera New camera of scene
     */
    public void setCamera(MutableCamera camera)
    {
        this.camera = camera;
    }
    
    /**
     * Gets all solids in scene
     * @return List with all scene solids
     */
    public List<Solid> getSolids()
    {
        return this.solids;
    }
    
    /**
     * Removes all solids from scene
     */
    public void removeAllSolids()
    {
        this.solids.clear();
    }
    
    /**
     * Gets number of solids in scene
     * @return Number of solids in scene
     */
    public int getSolidsCount()
    {
        return this.solids.size();
    }
    
    /**
     * Adds solid to scene
     * @param s Solid which will be added to scene
     */
    public void addSolid(Solid s)
    {
        this.solids.add(s);
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
        String reti = "";
        if (property.toLowerCase().equals("název"))
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
        if (property.toLowerCase().equals("název"))
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
