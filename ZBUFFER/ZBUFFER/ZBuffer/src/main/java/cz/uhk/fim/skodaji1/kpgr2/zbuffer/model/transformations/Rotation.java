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

import cz.uhk.fim.kpgr2.transforms.Mat4;
import cz.uhk.fim.kpgr2.transforms.Mat4RotX;
import cz.uhk.fim.kpgr2.transforms.Mat4RotY;
import cz.uhk.fim.kpgr2.transforms.Mat4RotZ;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing rotation transformation
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Rotation extends MutableAdapter implements Transformation
{

    /**
     * Angle of rotation around X axis
     */
    private double alpha;
    
    /**
     * Angle of rotation around Y axis
     */
    private double beta;
    
    /**
     * Angle of rotation around Z axis
     */
    private double gamma;
    
    /**
     * Name of rotation
     */
    private String name;
    
    /**
     * Creates new rotation transformation
     * @param name Name of rotation
     */
    public Rotation(String name)
    {
        this(name, 0, 0, 0);
    }
    
    /**
     * Creates new rotation transformation
     * @param name Name of rotation
     * @param alpha Angle of rotation around X axis
     * @param beta Angle of rotation around Y axis
     * @param gamma Angle of rotation around Z axis
     */
    public Rotation(String name, double alpha, double beta, double gamma)
    {
        this.name = name;
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & setters">
    /**
     * Gets angle of rotation around X axis
     * @return Angle of rotation around X axis
     */
    public double getAlpha()
    {
        return alpha;
    }

    /**
     * Sets angle of rotation around X axis
     * @param alpha New angle of rotation around X axis
     */
    public void setAlpha(double alpha)
    {
        this.alpha = alpha;
        this.informChange();
    }

    /**
     * Gets angle of rotation around Y axis
     * @return Angle of rotation around Y axis
     */
    public double getBeta()
    {
        return beta;
    }

    /**
     * Sets angle of rotation around Y axis
     * @param beta New angle of rotation around Y axis
     */
    public void setBeta(double beta)
    {
        this.beta = beta;
        this.informChange();
    }

    /**
     * Gets angle of rotation around Z axis
     * @return Angle of rotation around Z axis
     */
    public double getGamma()
    {
        return gamma;
    }

    /**
     * Sets angle of rotation around Z axis
     * @param gamma New angle of rotation around Z axis
     */
    public void setGamma(double gamma)
    {
        this.gamma = gamma;
        this.informChange();
    }
    //</editor-fold>
    
    
    @Override
    public Vertex apply(Vertex v)
    {
        // First step: prepare list of matrices needed to be performed
        // to make complete entered rotation
        List<Mat4> matrices = new ArrayList<>();
        if (this.alpha != 0)
        {
            matrices.add(new Mat4RotX(this.alpha));
        }
        if (this.beta != 0)
        {
            matrices.add(new Mat4RotY(this.beta));
        }
        if (this.gamma != 0)
        {
            matrices.add(new Mat4RotZ(this.gamma));
        }
        // Then multiply veretex with all matrices
        Vertex reti = v.clone();
        for(Mat4 matrix: matrices)
        {
            reti = reti.mul(matrix);
        }
        return reti;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Název", "Alfa", "Beta", "Gama"};
    }
    
    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.trim().toLowerCase();
        if (propName.equals("alfa"))
        {
            reti = this.getAlpha();
        }
        else if (propName.equals("beta"))
        {
            reti = this.getBeta();
        }
        else if (propName.equals("gama"))
        {
            reti = this.getGamma();
        }
        return reti;
    }
    
    @Override
    public void set(String property, double value)
    {
        String propName = property.trim().toLowerCase();
        if (propName.equals("alfa"))
        {
            this.setAlpha(value);
        }
        else if (propName.equals("beta"))
        {
            this.setBeta(value);
        }
        else if (propName.equals("gama"))
        {
            this.setGamma(value);
        }
    }

    @Override
    public void set(String property, String value)
    {
        if (property.trim().toLowerCase().equals("název"))
        {
            this.setName(value);
        }
    }

    @Override
    public String getString(String property)
    {
        String reti = super.getString(property);
        if (property.trim().toLowerCase().equals("název"))
        {
            reti = this.getName();
        }
        return reti;
    }

    @Override
    public boolean isMutable(String property)
    {
        return (property.toLowerCase().trim().equals("název") == false);
    }

    @Override
    public Class getType(String property)
    {
        Class reti = super.getType(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("alfa") || propName.equals("beta") || propName.equals("gama"))
        {
            reti = Double.class;
        }
        else if (propName.equals("název"))
        {
            reti = String.class;
        }
        return reti;
    }
    
    

    @Override
    public TransformationType getTransformationType()
    {
        return TransformationType.ROTATION;
    }

    @Override
    public String getName()
    {
        return this.name;
    }

    @Override
    public void setName(String name)
    {
        this.name = name;
        this.informChange();
    }
}
