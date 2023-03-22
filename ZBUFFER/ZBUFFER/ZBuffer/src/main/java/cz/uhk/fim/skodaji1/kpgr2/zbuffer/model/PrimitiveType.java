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

/**
 * 
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public enum PrimitiveType
{
    /**
     * Primitive is triangle
     */
    TRIANGLE("TRIANGLE"),
    
    /**
     * Primitive is line
     */
    LINE("LINE"),
    
    /**
     * Bicubics primitive type
     */
    BICUBICS("BICUBICS");
    
    /**
     * String representation of fill type
     */
    private String str;
    
    /**
     * Creates new primitive type
     * @param str String representation of primitive type
     */
    private PrimitiveType(String str)
    {
        this.str = str;
    }
    
    /**
     * Creates primitive type from its string representation
     * @param str String representation of primitive type
     * @return Primitive type defined by its string representation or NULL,
     *         if there is no such a primitive type
     */
    public static PrimitiveType fromString(String str)
    {
        PrimitiveType reti = null;
        switch(str.toUpperCase())
        {
            case "TRIANGLE": reti  = PrimitiveType.TRIANGLE; break;
            case "LINE":     reti  = PrimitiveType.LINE;     break; 
            case "BICUBICS": reti  = PrimitiveType.BICUBICS; break;
            default: reti = null;
        }
        return reti;
    }
    
    /**
     * Gets number of vertices which creates primitive
     * @return Number of vertices needed to create primitive
     */
    public int getVerticesCount()
    {
        int reti = 0;
        switch(this)
        {
            case TRIANGLE: reti = 3; break;
            case LINE:     reti = 2; break;
            case BICUBICS: reti = 4; break;
        }
        return reti;
    }
    
    @Override
    public String toString()
    {
        return this.str;
    }
}
