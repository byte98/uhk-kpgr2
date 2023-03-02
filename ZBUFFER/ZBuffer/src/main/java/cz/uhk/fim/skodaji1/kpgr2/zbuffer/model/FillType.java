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
 * Enumeration of all available fill types for vertices
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public enum FillType
{
    /**
     * Fill is solid colour
     */
    COLOR("COLOR");
    
    /**
     * String representation of fill type
     */
    private String str;
    
    /**
     * Creates new fill type
     * @param str String representation of fill type
     */
    private FillType(String str)
    {
        this.str = str;
    }
    
    /**
     * Creates fill type from its string representation
     * @param str String representation of fill type
     * @return Fill type defined by its string representation or NULL,
     *         if there is no such a fill type
     */
    public static FillType fromString(String str)
    {
        FillType reti = null;
        switch(str.toUpperCase())
        {
            case "COLOR": reti  = FillType.COLOR; break;
            default: reti = null;
        }
        return reti;
    }
    
    @Override
    public String toString()
    {
        return this.str;
    }
}
