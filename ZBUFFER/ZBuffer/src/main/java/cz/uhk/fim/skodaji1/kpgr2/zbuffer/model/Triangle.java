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

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing one triangle
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Triangle implements Primitive
{
    /**
     * Name of triangle
     */
    private final String name;
    
    /**
     * List of vertices which creates primitive
     */
    private final List<MutableVertex> vertices;
    
    /**
     * Creates new triangle
     */
    public Triangle()
    {
        this.name = String.format("Trojuhelnik_%03d", Primitive.COUNTER);
        Primitive.COUNTER = Primitive.COUNTER + 1;
        this.vertices = new ArrayList<>();
    }
    
    /**
     * Creates new triangle
     * @param name Name of triangle
     */
    public Triangle(String name)
    {
        this.name = name;
        this.vertices = new ArrayList<>();
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
    
}
