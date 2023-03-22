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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing one triangle
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Line implements Primitive
{
    /**
     * Counter of created triangles
     */
    private static long COUNTER = 0;
    
    /**
     * List with all handlers of object change
     */
    private final List<ObjectChangeCallback> handlers;
    
    /**
     * Name of triangle
     */
    private String name;
    
    /**
     * List of vertices which creates primitive
     */
    private final List<MutableVertex> vertices;
    
    /**
     * Fill of triangle
     */
    private Fill fill;
    
    /**
     * Creates new triangle
     */
    public Line()
    {
        this(String.format("Usecka_%03d", Line.COUNTER));
    }
    
    /**
     * Creates new triangle
     * @param name Name of triangle
     */
    public Line(String name)
    {
        this.name = name;
        Line.COUNTER = Line.COUNTER + 1;
        this.vertices = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }
    
    
    @Override
    public List<MutableVertex> getVertices()
    {
        return this.vertices;
    }

    @Override
    public String getName()
    {
        return this.name;
    }
    
    @Override
    public PrimitiveType getPrimitiveType()
    {
        return PrimitiveType.LINE;
    }

    @Override
    public void addVertex(MutableVertex v)
    {
        this.vertices.add(v);
    }

    @Override
    public void removeAllVertices()
    {
        this.vertices.clear();
    }

    @Override
    public int getVerticesCount()
    {
        return this.vertices.size();
    }   
    
    @Override
    public void addChangeCallback(ObjectChangeCallback callback)
    {
        this.handlers.add(callback);
    }
    
    @Override
    public void informChange()
    {
        for (ObjectChangeCallback callback: this.handlers)
        {
            callback.objectChanged();
        }
    }

    @Override
    public void setFill(Fill fill)
    {
        this.fill = fill;
    }

    @Override
    public Fill getFill()
    {
        return this.fill;
    }
    
    @Override 
    public String toString()
    {
        return "LINE (" + this.vertices.get(0).toString() + ", " + this.vertices.get(1).toString() + ")";
    }
}
