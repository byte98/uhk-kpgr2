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

import cz.uhk.fim.kpgr2.transforms.Mat4;
import cz.uhk.fim.kpgr2.transforms.Mat4ViewRH;
import cz.uhk.fim.kpgr2.transforms.Vec3D;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableCamera;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class representing world space
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class WorldSpace
{
    /**
     * Camera of world
     */
    private final MutableCamera camera;
    
    /**
     * Creates new world space
     * @param camera Camera of world
     */
    public WorldSpace(MutableCamera camera)
    {
        this.camera = camera;
    }
    
    /**
     * Gets vector of view from camera settings
     * @return Vector of view from camera settings
     */
    private Vec3D getViewVector()
    {
        return new Vec3D(
                Math.cos(this.camera.getAzimuth()) * Math.cos(this.camera.getZenith()),
                Math.sin(this.camera.getAzimuth()) * Math.cos(this.camera.getZenith()),
                Math.sin(this.camera.getZenith())
        );
    }
    
    /**
     * Gets up vector from camera settings
     * @return Up vector from camera settings
     */
    private Vec3D getUpVector()
    {
        return new Vec3D(
                Math.cos(this.camera.getAzimuth()) * Math.cos(this.camera.getZenith() + (Math.PI / 2)),
                Math.sin(this.camera.getAzimuth()) * Math.cos(this.camera.getZenith() + (Math.PI / 2)),
                Math.sin(this.camera.getZenith() + (Math.PI / 2))
        );
    }
    
    /**
     * Gets transformation matrix to world space
     * @return Transformation matrix to world space
     */
    private Mat4 getMatrix()
    {
        return new Mat4ViewRH(
                new Vec3D(this.camera.getPosition()),
                this.getViewVector(),
                this.getUpVector()
        );
    }
    
    /**
     * Applies transformation to world space to vertex buffer
     * @param vertexBuffer Vertex buffer which will be transformed into world space
     * @return New buffer of vertices in world space
     */
    public Vertex[] apply(Vertex[] vertexBuffer)
    {
        Vertex[] reti = new Vertex[vertexBuffer.length];
        for (int i = 0; i < vertexBuffer.length; i++)
        {
            reti[i] = vertexBuffer[i].mul(this.getMatrix());
        }
        return reti;
    }
}
