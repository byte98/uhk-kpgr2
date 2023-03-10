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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;
import java.util.ArrayList;
import java.util.List;

/**
 * Class which can perform clipping of objects
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Clipper
{
    /**
     * Buffer of vertices which will be checked and clipped
     */
    private Vertex[] vertexBuffer;
    
    /**
     * Buffer of indexes of vertices
     */
    private int[] indexBuffer;
    
    /**
     * Buffer of parts which holds structure of vertices
     */
    private Scene.PartBufferItem[] partBuffer;
    
    /**
     * Sets inputs for clipper
     * @param vertexBuffer Buffer of vertices
     * @param indexBuffer Buffer of indexes
     * @param partBuffer Buffer of parts
     */
    public void setInputs(Vertex[] vertexBuffer, int[] indexBuffer, Scene.PartBufferItem[] partBuffer)
    {
        this.vertexBuffer = new Vertex[vertexBuffer.length];
        System.arraycopy(vertexBuffer, 0, this.vertexBuffer, 0, this.vertexBuffer.length);
        this.indexBuffer = new int[indexBuffer.length];
        System.arraycopy(indexBuffer, 0, this.indexBuffer, 0, this.indexBuffer.length);
        this.partBuffer = new Scene.PartBufferItem[partBuffer.length];
        System.arraycopy(partBuffer, 0, this.partBuffer, 0, this.partBuffer.length);
    }
    
    /**
     * Runs clipper
     */
    public void run()
    {
        for(Scene.PartBufferItem part: this.partBuffer)
        {
            Vertex[][]subParts = this.getPartVertices(part);
            for(int subPart = 0; subPart < subParts.length; subPart++)
            {
                if (Clipper.testVertices(subParts[subPart]) == false)
                {
                    this.removeSubpart(part, subPart);
                    part.setCount(part.getCount() - 1);
                }
            }
        }
    }
    
    /**
     * Tests, whether all vertices are visible or not
     * @param vertices Array of vertices which will be tested
     * @return TRUE if all vertices are visible, FALSE otherwise
     */
    private static boolean testVertices(Vertex[] vertices)
    {
        boolean reti = true;
        for(Vertex v: vertices)
        {
            if (Clipper.testVertex(v) == false)
            {
                reti = false;
                break;
            }
        }
        return reti;
    }
    
    /**
     * Tests, whether vertex is in visible area or not
     * @param v Vertex which will be tested
     * @return TRUE if vertex is in visible area, FALSE otherwise
     * @see Presentation at {@link https://oliva.uhk.cz}, course KPGR2, presentation 01, slide 88
     */
    private static boolean testVertex(Vertex v)
    {
        double x = v.getPosition().x;
        double y = v.getPosition().y;
        double z = v.getPosition().z;
        double w = v.getPosition().w;
        double mw = (-1) * w;
        return(
                mw <= x && x <= w &&
                mw <= y && y <= w &&
                0 <= z && z <= w
        );
    }
    
    /**
     * Removes subpart from part
     * @param part Part from which subpart will be removed
     * @param subpart Index of subpart in part
     */
    private void removeSubpart(Scene.PartBufferItem part, int subpart)
    {
        int[] newIndexBuffer = new int[this.indexBuffer.length - part.getType().getVerticesCount()];
        int start = part.getIndex() + (subpart * part.getType().getVerticesCount());
        int end = start + part.getType().getVerticesCount();
        int delta = 0;
        for (int i = 0; i < this.indexBuffer.length; i++)
        {
            if (i >= start && i < end)
            {
                delta++;
            }
            else
            {
                newIndexBuffer[i - delta] = this.indexBuffer[i];
            }
        }
        this.indexBuffer = newIndexBuffer;
    }
    
    /**
     * Gets all vertices which belongs to part
     * @param part Part which vertices will be found
     * @return Array of vertices which belongs to part split by primitives
     */
    private Vertex[][] getPartVertices(Scene.PartBufferItem part)
    {
        List<Vertex[]> tmp = new ArrayList<>();
        for (int i = 0; i < part.getCount(); i++)   
        {
            Vertex[] item = new Vertex[part.getType().getVerticesCount()];
            for (int j = 0; j < part.getType().getVerticesCount(); j++)
            {
                int idx = part.getIndex() + (i * part.getType().getVerticesCount()) + j;
                item[j] = this.vertexBuffer[this.indexBuffer[idx]];
            }
            tmp.add(item);
        }
        Vertex[][] reti = new Vertex[tmp.size()][part.getType().getVerticesCount()];
        for (int i = 0; i < tmp.size(); i++)
        {
            reti[i] = tmp.get(i);
        }
        return reti;
    }
    
    /**
     * Gets vertex buffer
     * @return Buffer of vertices
     */
    public Vertex[] getVertexBuffer()
    {
        return this.vertexBuffer;
    }
    
    /**
     * Gets index buffer
     * @return Buffer of indexes
     */
    public int[] getIndexBuffer()
    {
        return this.indexBuffer;
    }
    
    /**
     * Gets part buffer
     * @return Buffer of parts
     */
    public Scene.PartBufferItem[] getPartBuffer()
    {
        return this.partBuffer;
    }
}
