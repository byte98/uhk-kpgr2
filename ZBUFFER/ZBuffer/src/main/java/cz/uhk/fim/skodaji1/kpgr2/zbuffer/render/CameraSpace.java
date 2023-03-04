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
import cz.uhk.fim.kpgr2.transforms.Mat4Identity;
import cz.uhk.fim.kpgr2.transforms.Mat4OrthoRH;
import cz.uhk.fim.kpgr2.transforms.Mat4PerspRH;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class representing camera space
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class CameraSpace extends MutableAdapter
{
    /**
     * Enumeration of all available camera space types
     */
    public static enum CamSpaceType
    {
        /**
         * Perspective camera space
         */
        PERSPECTIVE("PERSPECTIVE"),
        
        /**
         * Orthogonal camera space
         */
        ORTHOGONAL("ORTHOGONAL");
    
        /**
         * String representation of camera space type
         */
        private String str;

        /**
         * Creates new camera space type
         * @param str String representation of camera space type
         */
        private CamSpaceType(String str)
        {
            this.str = str;
        }

        /**
         * Creates primitive type from its string representation
         * @param str String representation of primitive type
         * @return Primitive type defined by its string representation or NULL,
         *         if there is no such a primitive type
         */
        public static CamSpaceType fromString(String str)
        {
            CamSpaceType reti = null;
            switch(str.trim().toUpperCase())
            {
                case "PERSPECTIVE": reti = CamSpaceType.PERSPECTIVE; break;
                case "ORTHOGONAL":  reti = CamSpaceType.ORTHOGONAL;  break; 
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
    
    /**
     * Nearest Z coordinate which will be displayed
     */
    private double zNear;
    
    
    /**
     * Furthermost Z coordinate which will be displayed
     */
    private double zFar;
    
    /**
     * Width of camera space
     */
    private int width;
    
    /**
     * Height of camera space
     */
    private int height;
    
    /**
     * Viewing angle
     */
    private double alpha;
    
    /**
     * Type of camera space
     */
    private CamSpaceType type;
    
    /**
     * Creates new camera space
     * @param zNear Nearest Z coordinate which will be displayed
     * @param zFar Furthermost Z coordinate which will be displayed
     * @param width Width of camera space
     * @param height Height of camera space
     */
    public CameraSpace(double zNear, double zFar, int width, int height, CameraSpace.CamSpaceType type)
    {
        this.zNear = zNear;
        this.zFar = zFar;
        this.width = width;
        this.height = height;
        this.type = type;
    }
    
    //<editor-fold defaultstate="collapsed" desc="Getters & setters">
    
    /**
     * Gets nearest Z coordinate which will be displayed
     * @return Nearest Z coordinate which will be displayed
     */
    public double getZNear()
    {
        return this.zNear;
    }
    
    /**
     * Sets nearest Z coordinate which will be displayed
     * @param zNear New nearest Z coordinate which will be displayed
     */
    public void setZNear(double zNear)
    {
        this.zNear = zNear;
        this.informChange();
    }
    
    /**
     * Gets furthermost Z coordinate which will be displayed
     * @return Furthermost Z coordinate which will be displayed
     */
    public double getZFar()
    {
        return this.zFar;
    }
    
    /**
     * Sets furthermost Z coordinate which will be displayed
     * @param zFar New furthermost Z coordinate which will be displayed
     */
    public void setZFar(double zFar)
    {
        this.zFar = zFar;
        this.informChange();
    }
    
    /**
     * Gets width of camera space
     * @return Width of camera space
     */
    public int getWidth()
    {
        return this.width;
    }
    
    /**
     * Sets width of camera space
     * @param width New width of camera space
     */
    public void setWidth(int width)
    {
        this.width = width;
        this.informChange();
    }
    
    /**
     * Gets height of camera space
     * @return Height of camera space
     */
    public int getHeight()
    {
        return this.height;
    }
    
    /**
     * Sets height of camera space
     * @param height New height of camera space
     */
    public void setHeight(int height)
    {
        this.height = height;
        this.informChange();
    }

    /**
     * Gets viewing angle
     * @return Viewing angle
     */
    public double getAlpha()
    {
        return this.alpha;
    }
    
    /**
     * Sets viewing angle
     * @param alpha New viewing angle
     */
    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
        this.informChange();
    }
    
    /**
     * Gets type of camera space
     * @return 
     */
    public CameraSpace.CamSpaceType getType()
    {
        return this.type;
    }
    
    /**
     * Sets type of camera space
     * @param type New type of camera space
     */
    public void setType(CameraSpace.CamSpaceType type)
    {
        this.type = type;
        this.informChange();
    }

    //</editor-fold>
    
    /**
     * Gets matrix which performs transformation into camera space
     * @return Matrix of transformation into camera space
     */
    private Mat4 getMatrix()
    {
        Mat4 reti = new Mat4Identity();
        if (this.getType() == CameraSpace.CamSpaceType.ORTHOGONAL)
        {
            reti = new Mat4OrthoRH(this.getWidth(), this.getHeight(), this.getZNear(), this.getZFar());
        }
        else if (this.getType() == CameraSpace.CamSpaceType.PERSPECTIVE)
        {
            reti = new Mat4PerspRH(this.getAlpha(), (float)this.width / (float)this.height, this.getZNear(), this.getZFar());
        }
        return reti;
    }
    
    /**
     * Applies transformation to camera space on vertices
     * @param vertexBuffer Buffer containing vertices to which transformation will be applied
     * @return Array with vertices on which transformation has been applied
     */
    public Vertex[] apply(Vertex[] vertexBuffer)
    {
        Vertex[] reti = new Vertex[vertexBuffer.length];
        for (int i = 0; i < vertexBuffer.length; i++)
        {
            reti[i] = vertexBuffer[i].mul(this.getMatrix());
            System.out.println(reti[i]);
        }
        return reti;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Zn", "Zf", "Šířka", "Výška", "Zorný úhel", "Typ"};
    }
    
    
    
    @Override
    public Class getType(String property)
    {
        Class reti = Object.class;
        String propName = property.toLowerCase().trim();
        if (propName.equals("výška") || propName.equals("šířka"))
        {
            reti = Integer.class;
        }
        else if (propName.equals("zn") || propName.equals("zf") || propName.equals("zorný úhel"))
        {
            reti = Double.class;
        }
        else if (propName.equals("typ"))
        {
            reti = Enum.class;
        }
        return reti;
    }

    @Override
    public void set(String property, int value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("výška"))
        {
            this.setHeight(value);
        }
        else if (propName.equals("šířka"))
        {
            this.setWidth(value);
        }
    }

    @Override
    public void set(String property, double value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("zf"))
        {
            this.setZFar(value);
        }
        else if (propName.equals("zn"))
        {
            this.setZNear(value);
        }
        else if (propName.equals("zorný úhel"))
        {
            this.setAlpha(value);
        }
    }

    @Override
    public int getInt(String property)
    {
        int reti = super.getInt(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("šířka"))
        {
            reti = this.getWidth();
        }
        else if (propName.equals("výška"))
        {
            reti = this.getHeight();
        }
        return reti;
    }

    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("zn"))
        {
            reti = this.getZNear();
        }
        else if (propName.equals("zf"))
        {
            reti = this.getZFar();
        }
        else if (propName.equals("zorný úhel"))
        {
            reti = this.getAlpha();
        }
        return reti;
    }    

    @Override
    public void setEnum(String property, String value)
    {
        if (property.toLowerCase().trim().equals("typ"))
        {
            switch(value.toLowerCase().trim())
            {
                case "pravoúhlý": this.setType(CameraSpace.CamSpaceType.ORTHOGONAL); break;
                case "perspektivní": this.setType(CameraSpace.CamSpaceType.PERSPECTIVE); break;
            }
            this.informChange();
        }
    }

    @Override
    public String[] getAllowedValues(String enumName)
    {
        String[] reti = new String[0];
        if (enumName.toLowerCase().trim().equals("typ"))
        {
            reti = new String[]{"Pravoúhlý", "Perspektivní"};
        }
        return reti;
    }

    @Override
    public String getEnumValue(String enumName)
    {
        String reti = super.getEnumValue(enumName);
        if (enumName.toLowerCase().trim().equals("typ"))
        {
            switch(this.getType())
            {
                case ORTHOGONAL: reti = "Pravoúhlý"; break;
                case PERSPECTIVE: reti = "Perspektivní"; break;
            }
        }
        return reti;
    }
    
    
}
