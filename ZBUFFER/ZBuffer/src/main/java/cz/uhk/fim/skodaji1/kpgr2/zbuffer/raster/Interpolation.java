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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster;

import cz.uhk.fim.kpgr2.transforms.Col;

/**
 * Class which can perform interpolation
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public abstract class Interpolation
{
    /**
     * Interpolates two colours
     * @param c1 First colour
     * @param c2 Second colour
     * @param step Actual step of interpolation
     * @return Interpolation of two colours
     */
    public static Col colour(Col c1, Col c2, double step)
    {
        Col min = c1.getARGB() < c2.getARGB() ? c1 : c2;
        Col max = min == c1 ? c2 : c1;
        double r = Interpolation.compute(min.r, max.r, step);
        double g = Interpolation.compute(min.g, max.g, step);
        double b = Interpolation.compute(min.b, max.b, step);
        double a = Interpolation.compute(min.a, max.a, step);
        return new Col(r, g, b, a);
    }
    
    /**
     * Performs interpolation
     * @param min Minimal value
     * @param max Maximal value
     * @param step Actual step
     * @return Interpolation of value
     */
    private static double compute(double min, double max, double step)
    {
        double reti = min;
        if (min != max)
        {
            reti = (step - min)/(max - min);
        }
        return reti;
    }
}
