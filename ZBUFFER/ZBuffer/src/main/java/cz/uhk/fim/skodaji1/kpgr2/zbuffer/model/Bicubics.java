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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;
import java.util.ArrayList;
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
     */
    public Bicubics(Bicubics.BicubicsType type)
    {
        this(type, String.format("Bikubika_%03d", Bicubics.COUNTER));
    }
    
    /**
     * Creates new bicubics
     * @param type Type of bicubics
     * @param name Name of bicubics
     */
    public Bicubics(Bicubics.BicubicsType type, String name)
    {
        this.type = type;
        this.name = name;
        Bicubics.COUNTER = Bicubics.COUNTER + 1;
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
    
}
