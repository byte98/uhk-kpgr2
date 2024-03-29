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

import cz.uhk.fim.kpgr2.transforms.Mat4;
import cz.uhk.fim.kpgr2.transforms.Point3D;
import cz.uhk.fim.kpgr2.transforms.Vec3D;

/**
 * Class implementing vertex of any solid
 * Copied from GitLab and implemented another functionality
 * @author Jakub Benes <jakub.benes@uhk.cz> & Jiri Skoda <jiri.skoda@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/model/Vertex.java>
 */
public class Vertex implements Cloneable
{
    
    /**
     * Counter of created vertices
     */
    protected static long COUNTER = 0;
    
    /**
     * Position of vertex
     */
    protected Point3D position;
    
    /**
     * Parent primitive to which vertex belongs to
     */
    protected Primitive parent;
    
    /**
     * Identifier of vertex
     */
    protected final long id;
        
    /**
     * Creates new vertex
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     * @param parent Primitive to which vertex belongs to
     */
    public Vertex(double x, double y, double z, Primitive parent)
    {
        this.position = new Point3D(x, y, z);
        this.parent = parent;
        this.id = Vertex.COUNTER;
        Vertex.COUNTER++;
    }
    
    /**
     * Gets fill of vertex
     * @return Fill of vertex
     */
    public Fill getFill()
    {
        return this.parent.getFill();
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
     * Gets coordinate of vertex on X axis
     * @return Coordinate of vertex on X axis
     */
    public int getX()
    {
        return (int) Math.round(this.position.x);
    }
    
    /**
     * Gets coordinate of vertex on Y axis
     * @return Coordinate of vertex on Y axis
     */
    public int getY()
    {
        return (int) Math.round(this.position.y);
    }
    
    /**
     * Gets coordinate of vertex on Z axis
     * @return Coordinate of vertex on Z axis
     */
    public double getZ()
    {
        return this.position.z;
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
        Vertex reti = new Vertex(newPos.x, newPos.y, newPos.z, this.parent);
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
        Vertex reti = new Vertex(this.position.x, this.position.y, this.position.z, this.parent);
        reti.setPosition(reti.getPosition().mul(mat4));
        return reti;
    }
    
    /**
     * Multiplies vertex by real number
     * @param val Number by which will be vertex multiplied
     * @return Vertex multiplied by real number
     */
    public Vertex mul(double val)
    {
        Vertex reti = new Vertex(this.position.x, this.position.y, this.position.z, this.parent);
        double x = this.position.x * val;
        double y = this.position.y * val;
        double z = this.position.z * val;
        double w = this.position.w * val;
        reti.setPosition(new Point3D(x, y, z, w));
        return reti;
    }
    
    /**
     * Dehomogenisates vertex
     * @return Dehomogenisated vertex
     */
    public Vertex dehomog()
    {
        Vec3D dehomogVec = this.getPosition().dehomog();
        return new Vertex(dehomogVec.x, dehomogVec.y, dehomogVec.z, this.parent);    
    }
    
    @Override
    public Vertex clone()
    {
        Vertex reti = new Vertex(this.position.x, this.position.y, this.position.z, this.parent);
        reti.setPosition(new Point3D(this.position.x, this.position.y, this.position.z, this.position.w));
        return reti;
    }
    
    @Override
    public String toString()
    {
        return String.format("[%f, %f, %f, %f]", this.position.x, this.position.y, this.position.z, this.position.w);
    }

    @Override
    public boolean equals(Object obj)
    {
        boolean reti = false;
        if (obj instanceof Vertex)
        {
            Vertex vertex = (Vertex) obj;
            reti = this.id == vertex.id;
        }
        return reti;
    }

    @Override
    public int hashCode()
    {
        int hash = 7;
        hash = 71 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
    
    
}
