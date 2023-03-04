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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Mutable;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class representing transformation of object
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public interface Transformation extends Mutable
{
    /**
     * Enumeration of all available transformation types
     */
    public enum TransformationType
    {
        /**
         * Rotation transformation
         */
        ROTATION,
        
        /**
         * Scale transformation
         */
        SCALE,
        
        /**
         * Translation transformation
         */
        TRANSLATION
    }
    
    /**
     * Applies transformation to vertex
     * @param v Vertex to which transformation will be applied
     * @return Vertex with transformation applied
     */
    public abstract Vertex apply(Vertex v);
    
    /**
     * Gets type of transformation
     * @return Type of transformation
     */
    public abstract TransformationType getTransformationType();
    
    /**
     * Gets name of transformation
     * @return 
     */
    public abstract String getName();
    
    /**
     * Sets name of transformation
     * @param name New name of transformation
     */
    public abstract void setName(String name);
    
    
}
