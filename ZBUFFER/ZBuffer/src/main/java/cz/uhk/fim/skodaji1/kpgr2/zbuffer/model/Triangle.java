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
public class Triangle implements Primitive
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
     * Creates new triangle
     */
    public Triangle()
    {
        this(String.format("Trojuhelnik_%03d", Triangle.COUNTER));
    }
    
    /**
     * Creates new triangle
     * @param name Name of triangle
     */
    public Triangle(String name)
    {
        this.name = name;
        Triangle.COUNTER = Triangle.COUNTER + 1;
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
        return PrimitiveType.TRIANGLE;
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
    public String[] getAllowedValues(String enumName)
    {
        return new String[0];
    }
    
    @Override public String getEnumValue(String enumName)
    {
        return "";
    }

    @Override public void setEnum(String property, String value){}
}
