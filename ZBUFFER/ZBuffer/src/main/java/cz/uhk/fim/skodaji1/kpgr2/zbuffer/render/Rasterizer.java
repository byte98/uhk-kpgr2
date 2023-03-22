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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.PrimitiveType;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.LineRasterizer;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.TriangleRasterizer;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.ZBuffer;

/**
 * Class which can rasterize primitives
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Rasterizer
{
    /**
     * Raster to which primitives will be rasterized
     */
    private ZBuffer raster;
    
    /**
     * Buffer of vertices
     */
    private Vertex[] vertexBuffer;
    
    /**
     * Buffer of indexes
     */
    private int[] indexBuffer;
    
    /**
     * Buffer of parts
     */
    private Scene.PartBufferItem[] partBuffer;
    
    /**
     * Type of output of renderer
     */
    private Renderer.RenderType renderType;
    
    /**
     * Rasterizer of lines
     */
    private final LineRasterizer lineRasterizer;
    
    /**
     * Rasterizer of triangles
     */
    private final TriangleRasterizer triangleRasterizer;
    
    /**
     * Creates new rasterizer
     */
    public Rasterizer()
    {
        this.lineRasterizer = new LineRasterizer();
        this.triangleRasterizer = new TriangleRasterizer();
    }
    
    /**
     * Sets raster to which primitives will be rasterized
     * @param raster New raster for rasterization
     */
    public void setRaster(ZBuffer raster)
    {
        this.raster = raster;
    }
    
    /**
     * Sets type of output of renderer
     * @param type New type of output of renderer
     */
    public void setRenderType(Renderer.RenderType type)
    {
        this.renderType = type;
    }
    
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
     * Rasterizes whole scene
     */
    public void rasterize()
    {
        this.lineRasterizer.setRaster(this.raster);
        this.lineRasterizer.setMode(this.renderType);
        this.triangleRasterizer.setRaster(this.raster);
        this.triangleRasterizer.setMode(this.renderType);
        for (Scene.PartBufferItem part: this.partBuffer)
        {
            if (part.getType() == PrimitiveType.TRIANGLE)
            {
                for (int i = 0; i < part.getCount(); i++)
                {
                    int index = part.getIndex() + (i * part.getType().getVerticesCount());
                    Vertex v1 = this.vertexBuffer[this.indexBuffer[index]];
                    Vertex v2 = this.vertexBuffer[this.indexBuffer[index + 1]];
                    Vertex v3 = this.vertexBuffer[this.indexBuffer[index + 2]];
                    this.triangleRasterizer.setColour(v1.getFill().getPixelProvider());
                    this.triangleRasterizer.rasterize(
                            v1.getX(), v1.getY(), v1.getZ(),
                            v2.getX(), v2.getY(), v2.getZ(),
                            v3.getX(), v3.getY(), v3.getZ()
                    );
                }
            }
            else if (part.getType() == PrimitiveType.LINE)
            {
                for (int i = 0; i < part.getCount(); i++)
                {
                    int index = part.getIndex() + (i * part.getType().getVerticesCount());
                    Vertex v1 = this.vertexBuffer[this.indexBuffer[index]];
                    Vertex v2 = this.vertexBuffer[this.indexBuffer[index + 1]];
                    this.lineRasterizer.setColour(v1.getFill().getPixelProvider());
                    this.lineRasterizer.rasterize(
                            v1.getX(), v1.getY(), v1.getZ(),
                            v2.getX(), v2.getY(), v2.getZ()
                    );
                }
            }                
        }
    }
    
    /**
     * Render wireframe
     */
    private void renderWireframe()
    {
        for(Scene.PartBufferItem part: this.partBuffer)
        {
            int partType = part.getType().getVerticesCount();
            for (int i = 0; i < part.getCount(); i++)
            {
                for (int c = 0; c < partType; c++)
                {
                    int index = part.getIndex() + (i * partType) + c;
                    Vertex v1 = this.vertexBuffer[this.indexBuffer[index]];
                    Vertex v2 = this.vertexBuffer[this.indexBuffer[(index + 1) % partType]];
                    this.lineRasterizer.setColour(v1.getFill().getPixelProvider());
                    this.lineRasterizer.rasterize(
                            v1.getX(),
                            v1.getY(),
                            v1.getZ(),
                            v2.getX(),
                            v2.getY(),
                            v2.getZ()
                    );
                }
            }
        }
    }
}
