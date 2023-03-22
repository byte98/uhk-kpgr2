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

/**
 * Class which represents rasterizer of line
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class LineRasterizer extends AbstractRasterizer
{
    /**
     * Rasterizes line
     * @param x1 X coordinate of starting point
     * @param y1 Y coordinate of starting point
     * @param z1 Z coordinate of starting point
     * @param x2 X coordinate of ending point
     * @param y2 Y coordinate of ending point
     * @param z2 Z coordinate of ending point
     */
    public void rasterize(int x1, int y1, double z1, int x2, int y2, double z2)
    {
        int dX = Math.abs(x2 - x1);
        int dY = Math.abs(y2 - y1);
        if (dX > dY)
        {
            if (x1 > x2)
            {
                this.rasterizeX((double)x2, (double)y2, z2, (double)x1, (double)y1, z1);
            }
            else
            {
                this.rasterizeX((double)x1, (double)y1, z1, (double)x2, (double)y2, z2);
            }
        }
        else
        {
            if (y1 > y2)
            {
                this.rasterizeY((double)x2, (double)y2, z2, (double)x1, (double)y1, z1);
            }
            else
            {
                this.rasterizeY((double)x1, (double)y1, z1, (double)x2, (double)y2, z2);
            }
        }
    }
    
    /**
     * Rasterizes line with X axis as controlling axis
     * @param x1 X coordinate of starting point
     * @param y1 Y coordinate of starting point
     * @param z1 Z coordinate of starting point
     * @param x2 X coordinate of ending point
     * @param y2 Y coordinate of ending point
     * @param z2 Z coordinate of ending point
     */
    private void rasterizeX(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double dX = Math.abs(x2 - x1);
        double dY = y2 - y1;
        double dZ = z2 - z1;
        double stepY = dY / dX;
        double stepZ = dZ / dX;
        for (double step = 0; step < dX; step++)
        {
            double pct = step / dX;
            int x = (int) Math.round(x1 + (pct * dX));
            int y = (int) Math.round(y1 + (stepY * step));
            double z = z1 + (stepZ * step);
            if (this.raster.isInside(x, y))
            {
                this.raster.setElement(x, y, z, this.colour.getPixel(x, y));
            }            
        }
    }
    
    /**
     * Rasterizes line with Y axis as controlling axis
     * @param x1 X coordinate of starting point
     * @param y1 Y coordinate of starting point
     * @param z1 Z coordinate of starting point
     * @param x2 X coordinate of ending point
     * @param y2 Y coordinate of ending point
     * @param z2 Z coordinate of ending point
     */
    private void rasterizeY(double x1, double y1, double z1, double x2, double y2, double z2)
    {
        double dY = Math.abs(y2 - y1);
        double dX = x2 - x1;
        double dZ = z2 - z1;
        double stepX = dX / dY;
        double stepZ = dZ / dY;
        for (double step = 0; step < dY; step++)
        {
            double pct = step / dY;
            int y = (int) Math.round(y1 + (pct * dY));
            int x = (int) Math.round(x1 + (stepX * step));
            double z = z1 + (stepZ * step);
            if (this.raster.isInside(x, y))
            {
                this.raster.setElement(x, y, z, this.colour.getPixel(x, y));
            }    
        }
    }
    
}
