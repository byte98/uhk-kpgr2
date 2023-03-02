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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

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
     * Creates new rotation transformation
     */
    public Rotation()
    {
        this(0, 0, 0);
    }
    
    /**
     * Creates new rotation transformation
     * @param alpha Angle of rotation around X axis
     * @param beta Angle of rotation around Y axis
     * @param gamma Angle of rotation around Z axis
     */
    public Rotation(double alpha, double beta, double gamma)
    {
        this.alpha = alpha;
        this.beta = beta;
        this.gamma = gamma;
    }
    
    @Override
    public Vertex apply(Vertex v)
    {
        return null;
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"Alfa", "Beta", "Gama"};
    }
    
    @Override
    public double getDouble(String property)
    {
        double reti = super.getDouble(property);
        String propName = property.trim().toLowerCase();
        if (propName.equals("alfa"))
        {
            reti = this.alpha;
        }
        else if (propName.equals("beta"))
        {
            reti = this.beta;
        }
        else if (propName.equals("gama"))
        {
            reti = this.gamma;
        }
        return reti;
    }
    
    @Override
    public void set(String property, double value)
    {
        String propName = property.trim().toLowerCase();
        if (propName.equals("alfa"))
        {
            this.alpha = value;
        }
        else if (propName.equals("beta"))
        {
            this.beta = value;
        }
        else if (propName.equals("gama"))
        {
            this.gamma = value;
        }
    }
}
