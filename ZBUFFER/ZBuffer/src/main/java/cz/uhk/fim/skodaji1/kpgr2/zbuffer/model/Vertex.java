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
import cz.uhk.fim.kpgr2.transforms.Mat4;
import cz.uhk.fim.kpgr2.transforms.Point3D;
import cz.uhk.fim.kpgr2.transforms.Vec3D;

/**
 * Class implementing vertex of any solid
 * Copied from GitLab and implemented another functionality
 * @author Jakub Benes <jakub.benes@uhk.cz> & Jiri Skoda <jiri.skoda@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/model/Vertex.java>
 */
public class Vertex
{
    protected static final Col DEFAULT_COLOR = new Col(0xffffff);
    
    /**
     * Position of vertex
     */
    protected Point3D position;
    
    /**
     * Fill of vertex
     */
    protected Fill fill;
    
    /**
     * Creates new vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     */
    public Vertex(double x, double y, double z)
    {
        this(x, y, z, new Fill(Vertex.DEFAULT_COLOR));
    }
    
    /**
     * Creates new vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     * @param fill Fill of vertex
     */
    public Vertex(double x, double y, double z, Fill fill)
    {
        this.position = new Point3D(x, y, z);
        this.fill = fill;
    }
    
    /**
     * Sets new position of vertex
     * @param p New position of vertex
     */
    public void setPosition(Point3D p)
    {
        this.position = p;
    }
    
    /**
     * Gets position of vertex
     * @return Position of vertex
     */
    public Point3D getPosition()
    {
        return this.position;
    }
    
    /**
     * Multiplies vertex by vector
     * @param vec Vector by which will will be vertex multiplied
     * @return Reference to vertex
     */
    public Vertex mul(Vec3D vec)
    {
        Point3D newPos = new Point3D(this.position.x, this.position.y, this.position.z);
        Vec3D res = newPos.ignoreW().mul(vec);
        newPos.x = res.x;
        newPos.y = res.y;
        newPos.z = res.z;
        newPos.w = this.position.w;
        Vertex reti = new Vertex(newPos.x, newPos.y, newPos.z, this.fill.clone());
        reti.setPosition(newPos);
        return reti;
    }
    
    /**
     * Multiplies vertex by matrix
     * @param mat4 Matrix by which will be vertex multiplied
     * @return Vertex multiplied by matrix
     */
    public Vertex mul(Mat4 mat4)
    {
        Vertex reti = new Vertex(this.position.x, this.position.y, this.position.z, this.fill.clone());
        reti.setPosition(reti.getPosition().mul(mat4));
        return reti;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f, %f, %f, %f]", this.position.x, this.position.y, this.position.z, this.position.w);
    }
    
}
