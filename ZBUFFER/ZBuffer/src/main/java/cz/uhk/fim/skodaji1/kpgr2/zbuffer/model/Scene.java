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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        private int index;
        
        /**
         * Number of primitives stored in buffer
         */
        private int count;
        
        /**
         * Original number of primitives stored in buffer before clipping
         */
        private final int originalCount;

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
            this.originalCount = count;
        }

        /**
         * Gets type of part
         * @return Type of part
         */
        public PrimitiveType getType()
        {
            return this.type;
        }

        /**
         * Gets starting index on index buffer
         * @return Starting index on index buffer
         */
        public int getIndex()
        {
            return this.index;
        }

        /**
         * Gets number of primitives stored in buffer
         * @return Number of primitives stored in buffer
         */
        public int getCount()
        {
            return this.count;
        }        
        
        /**
         * Sets number of primitives stored in buffer
         * @param count New number of primitives stored in buffer
         */
        public void setCount(int count)
        {
            this.count = count;
        }
        
        /**
         * Sets starting index of part in index buffer
         * @param index Starting index of part in index buffer
         */
        public void setIndex(int index)
        {
            this.index = index;
        }
        
        /**
         * Gets number of removed primitives
         * @return Number of removed primitives
         */
        public int getRemovedCount()
        {
            return this.originalCount - this.count;
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
     * Solid representing axis
     */
    private Solid axisSolid;
    
    /**
     * Flag, whether axis should be visible or not
     */
    private boolean showAxis = false;
    
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
        if (this.showAxis == true && Objects.nonNull(this.axisSolid))
        {
            this.solids.add(this.axisSolid);
        }
        else if (this.showAxis == false && Objects.nonNull(this.axisSolid))
        {
            this.solids.remove(this.axisSolid);
        }
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
        for(Solid solid: this.solids)
        {
            for(Part part: solid.getParts())
            {
                for(Primitive primitive: part.getPrimitives())
                {
                    for (Vertex vertex: primitive.getVertices())
                    {
                        if (tmp.contains(vertex) == false)
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
            if (this.vertexBuffer[i].equals(v))
            {
                reti = i;
                break;
            }
        }
        return reti;
    }
    
    /**
     * Sets solid which represents axis
     * @param axis Solid representation of axis
     */
    public void setAxis(Solid axis)
    {
        if (Objects.nonNull(this.axisSolid) && this.solids.contains(this.axisSolid))
        {
            this.solids.remove(this.axisSolid);
        }
        this.axisSolid = axis;
    }
    
    /**
     * Shows axis
     */
    public void showAxis()
    {
        this.showAxis = true;
    }
    
    /**
     * Hides axis
     */
    public void hideAxis()
    {
        this.showAxis = false;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název"};
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
