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
import cz.uhk.fim.kpgr2.transforms.Point3D;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

/**
 * Class representing whole scene
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Scene extends MutableAdapter
{
    //<editor-fold defaultstate="collapsed" desc="Part buffer item">
    /**
     * Class representing item in part buffer
     */
    public static class PartBufferItem
    {
        /**
         * Type of primitive
         */
        private final PrimitiveType type;
        
        /**
         * Starting index in index buffer
         */
        private final int index;
        
        /**
         * Number of primitives stored in buffer
         */
        private final int count;

        /**
         * Creates new item of part buffer
         * @param type Type of part
         * @param index Starting index on index buffer
         * @param count Number of primitives stored in buffer
         */
        public PartBufferItem(PrimitiveType type, int index, int count)
        {
            this.type = type;
            this.index = index;
            this.count = count;
        }

        /**
         * Gets type of part
         * @return Type of part
         */
        public PrimitiveType getType()
        {
            return type;
        }

        /**
         * Gets starting index on index buffer
         * @return Starting index on index buffer
         */
        public int getIndex()
        {
            return index;
        }

        /**
         * Gets number of primitives stored in buffer
         * @return Number of primitives stored in buffer
         */
        public int getCount()
        {
            return count;
        }        
    }
    //</editor-fold>
    
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
     * Buffer of vertices
     */
    private Vertex[] vertexBuffer;
    
    /**
     * Buffer of parts
     */
    private PartBufferItem[] partBuffer;
    
    /**
     * Buffer of indexes of vertices
     */
    private int[] indexBuffer;
    
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
        this.informChange();
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
        this.informChange();
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
        this.informChange();
    }
    
    /**
     * Generates all needed buffers
     */
    public void generateBuffers()
    {
        this.generateVertexBuffer();
        this.generatePartBuffer();
    }
    
    /**
     * Gets buffer of vertices
     * @return Buffer of vertices
     */
    public Vertex[] getVertexBuffer()
    {
        return this.vertexBuffer;
    }
    
    /**
     * Gets buffer of parts
     * @return Buffer of parts
     */
    public Scene.PartBufferItem[] getPartBuffer()
    {
        return this.partBuffer;
    }
    
    /**
     * Gets buffer of indexes of vertices
     * @return Buffer of indexes
     */
    public int[] getIndexBuffer()
    {
        return this.indexBuffer;
    }
    
    /**
     * Generates buffer with all vertices
     */
    private void generateVertexBuffer()
    {
        List<Vertex> tmp = new ArrayList<>();
        Predicate<Vertex> isInTmp = (t) ->
        {
            BiPredicate<Point3D, Point3D> eq = (p1, p2) -> {
                return (
                        p1.x == p2.x &&
                        p1.y == p2.y &&
                        p1.z == p2.z &&
                        p1.w == p2.w
                );
            };
            boolean reti = false;
            for(Vertex v: tmp)
            {
                if (eq.test(t.getPosition(), v.getPosition()))
                {
                    reti = true;
                    break;
                }
            }
            return reti;
        };
        for(Solid solid: this.solids)
        {
            for(Part part: solid.getParts())
            {
                for(Primitive primitive: part.getPrimitives())
                {
                    for (Vertex vertex: primitive.getVertices())
                    {
                        if (tmp.contains(vertex) == false && isInTmp.test(vertex) == false)
                        {
                            tmp.add(vertex);
                        }
                    }
                }
            }
        }
        this.vertexBuffer = new Vertex[tmp.size()];
        for(int i = 0; i < tmp.size(); i++)
        {
            this.vertexBuffer[i] = tmp.get(i);
        }
        
    }
    
    /**
     * Generates part buffer and also index buffer
     */
    private void generatePartBuffer()
    {
        List<Scene.PartBufferItem> tmp = new ArrayList<>();
        List<Integer> tmp2 = new ArrayList<>();
        for(Solid solid: this.solids)
        {
            for(Part part: solid.getParts())
            {
                PrimitiveType type = part.getPrimitiveType();
                int count = part.getPrimitivesCount();
                int start = tmp2.size();
                tmp.add(new PartBufferItem(type, start, count));
                for(Primitive primitive: part.getPrimitives())
                {
                    for(Vertex vertex: primitive.getVertices())
                    {
                        tmp2.add(this.getIndexOfVertex(vertex));
                    }
                }
            }
        }
        this.partBuffer = new PartBufferItem[tmp.size()];
        for (int i = 0; i < partBuffer.length; i++)
        {
            this.partBuffer[i] = tmp.get(i);
        }
        this.indexBuffer = tmp2.stream().mapToInt(i -> i).toArray();
    }
    
    /**
     * Gets index of vertex from vertex buffer
     * @param v Vertex which index will be searched
     * @return Index of vertex or negative number
     */
    private int getIndexOfVertex(Vertex v)
    {
        int reti = -1;
        for (int i = 0; i < this.vertexBuffer.length; i++)
        {
            if (this.vertexBuffer[i].getPosition().x == v.getPosition().x &&
                this.vertexBuffer[i].getPosition().y == v.getPosition().y &&
                this.vertexBuffer[i].getPosition().z == v.getPosition().z && 
                this.vertexBuffer[i].getPosition().w == v.getPosition().w)
            {
                reti = i;
                break;
            }
        }
        return reti;
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
    public void set(String property, String value)
    {
        if (property.toLowerCase().equals("název"))
        {
            this.name = value;
            this.informChange();
        }
    }
}
