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

import cz.uhk.fim.kpgr2.transforms.Bikubika;
import cz.uhk.fim.kpgr2.transforms.Point3D;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class representing bicubics
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Bicubics implements Primitive
{
    /**
     * Enumeration of all available bicubics types
     */
    public enum BicubicsType
    {
        /**
         * Bezier bicubics
         */
        BEZIER(0),
        
        /**
         * Coons bicubics
         */
        COONS(2),
        
        /**
         * Ferguson bicubics
         */
        FERGUSON(1);
        
        /**
         * Numeric representation of bicubics type
         */
        private final int value;
        
        /**
         * Creates new bicubics type
         * @param value Value of bicubics
         */
        private BicubicsType(int value)
        {
            this.value = value;
        }
        
        /**
         * Gets numeric representation of bicubic type
         * @return Numeric representation of bicubic type
         */
        public int toInt()
        {
            return this.value;
        }
        
        /**
         * Gets bicubics type from its textual representation
         * @param str String representation of bicubics type
         * @return Bicubics type from string representation
         *         or NULL if there is no such bicubics type
         */
        public static BicubicsType fromString(String str)
        {
            BicubicsType reti = null;
            String s = str.trim().toUpperCase();
            switch(s)
            {
                case "BEZIER":   reti = BicubicsType.BEZIER;   break;
                case "COONS":    reti = BicubicsType.COONS;    break;
                case "FERGUSON": reti = BicubicsType.FERGUSON; break;
            }
            return reti;
        }
    }
    
    /**
     * Number of steps created to compute points on bicubics
     */
    private int steps;
    
    /**
     * Counter of created bicubics
     */
    private static long COUNTER = 0;
    
    /**
     * List with all handlers of object change
     */
    private final List<ObjectChangeCallback> handlers;
    
    /**
     * Name of bicubics
     */
    private String name;
    
    /**
     * List of vertices which creates primitive
     */
    private final List<MutableVertex> vertices;
    
    /**
     * Fill of bicubics
     */
    private Fill fill;
    
    /**
     * Type of bicubics
     */
    private BicubicsType type;
    
    /**
     * Creates new bicubics
     * @param type Type of bicubics
     * @param steps Number of steps of computing of bicubics points
     */
    public Bicubics(Bicubics.BicubicsType type, int steps)
    {
        this(type, steps, String.format("Bikubika_%03d", Bicubics.COUNTER));
    }
    
    /**
     * Creates new bicubics
     * @param type Type of bicubics
     * @param name Name of bicubics
     * * @param steps Number of steps of computing of bicubics points
     */
    public Bicubics(Bicubics.BicubicsType type, int steps, String name)
    {
        this.type = type;
        this.steps = steps;
        this.name = name;
        Bicubics.COUNTER = Bicubics.COUNTER + 1;
        this.vertices = new ArrayList<>();
        this.handlers = new ArrayList<>();
    }
    
    /**
     * Generates triangles which represents bicubics
     * @return Triangle representation of bicubics
     */
    public Triangle[] generateTriangles()
    {
        List<Triangle> tmp = new ArrayList<>();
        Point3D[][] area = this.generateArea();
        for(int r = 0; r < area.length - 1; r++)
        {
            for (int c = 0; c < area.length - 1; c++)
            {
                Point3D A = area[r][c];
                Point3D B = area[r + 1][c];
                Point3D C = area[r][c + 1];
                Point3D D = area[r + 1][c + 1];
                Triangle t1 = new Triangle();
                t1.setFill(this.fill);
                t1.addVertex(new MutableVertex(A.x, A.y, A.z, t1));
                t1.addVertex(new MutableVertex(B.x, B.y, B.z, t1));
                t1.addVertex(new MutableVertex(C.x, C.y, C.z, t1));
                Triangle t2 = new Triangle();
                t2.setFill(this.fill);
                t2.addVertex(new MutableVertex(B.x, B.y, B.z, t2));
                t2.addVertex(new MutableVertex(C.x, C.y, C.z, t2));
                t2.addVertex(new MutableVertex(D.x, D.y, D.z, t2));
                tmp.add(t1);
                tmp.add(t2);
            }
        }
        
        Triangle[] reti = new Triangle[tmp.size()];
        for (int i = 0; i < tmp.size(); i++)
        {
            reti[i] = tmp.get(i);
        }
        return reti;
    }
    
    /**
     * Generates area of bicubics
     * @return Point which belongs to area of bicubics
     */
    private Point3D[][] generateArea()
    {
        Point3D[][] reti = new Point3D[0][0];
        if (this.vertices.size() >= 16)
        {
            Bikubika bi = new Bikubika(this.type.toInt());
            bi.init(
                    this.vertices.get(0).getPosition(),
                    this.vertices.get(1).getPosition(),
                    this.vertices.get(2).getPosition(),
                    this.vertices.get(3).getPosition(),
                    this.vertices.get(4).getPosition(),
                    this.vertices.get(5).getPosition(),
                    this.vertices.get(6).getPosition(),
                    this.vertices.get(7).getPosition(),
                    this.vertices.get(8).getPosition(),
                    this.vertices.get(9).getPosition(),
                    this.vertices.get(10).getPosition(),
                    this.vertices.get(11).getPosition(),
                    this.vertices.get(12).getPosition(),
                    this.vertices.get(13).getPosition(),
                    this.vertices.get(14).getPosition(),
                    this.vertices.get(15).getPosition()
            );
            reti = new Point3D[this.steps + 1][this.steps + 1];
            for (int r = 0; r <= this.steps; r++)
            {
                for (int c = 0; c <= this.steps; c++)
                {
                    double u = (double)((double)r / (double)this.steps);
                    double v = (double)((double)c / (double)this.steps);
                    reti[r][c] = bi.compute(u, v);
                }
            }
        }
        return reti;
    }
    
    /**
     * Sets number of steps of computing of points
     * @param steps New number of steps of computation
     */
    private void setSteps(int steps)
    {
        if (steps < 1)
        {
            steps = 1;
        }
        this.steps = steps;
        this.informChange();
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
        return PrimitiveType.BICUBICS;
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
        return "BICUBICS (" + this.vertices.get(0).toString() + ", " + this.vertices.get(1).toString() + "," + this.vertices.get(2).toString() +  "," + this.vertices.get(3).toString() + ")";
    }

    @Override
    public void set(String property, int value)
    {
        if (property.trim().toLowerCase().equals("přesnost"))
        {
            this.setSteps(value);
        }
        else
        {
            Primitive.super.set(property, value);
        }
    }

    @Override
    public int getInt(String property)
    {
        int reti = Primitive.super.getInt(property);
        if (property.trim().toLowerCase().equals("přesnost"))
        {
            reti = this.steps;
        }
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = Primitive.super.getType(property);
        if (property.trim().toLowerCase().equals("přesnost"))
        {
            reti = Integer.class;
        }
        return reti;
    }

    @Override
    public boolean isMutable(String property)
    {
        boolean reti = Primitive.super.isMutable(property);
        if (property.trim().toLowerCase().equals("přesnost"))
        {
            reti = true;
        }
        return reti;
    }

    @Override
    public String[] getProperties()
    {
        List<String> tmp = new ArrayList<>(Arrays.asList(Primitive.super.getProperties()));
        tmp.add("Přesnost");
        String[] reti = new String[tmp.size()];
        for (int i = 0; i < tmp.size(); i++)
        {
            reti[i] = tmp.get(i);
        }
        return reti;
    }
    
    
    
}
