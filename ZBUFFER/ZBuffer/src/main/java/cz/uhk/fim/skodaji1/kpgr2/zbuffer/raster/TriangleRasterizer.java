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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster;

import cz.uhk.fim.kpgr2.transforms.Point3D;
import java.util.function.Function;

/**
 * Class which has ability to rasterize triangles
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class TriangleRasterizer extends AbstractRasterizer
{
    /**
     * Rasterizer of lines
     */
    private LineRasterizer lineRasterizer;
    
    /**
     * Creates new rasterizer of triangles
     */
    public TriangleRasterizer()
    {
        this.lineRasterizer = new LineRasterizer();
    }
    
    /**
     * Rasterizes triangle
     * @param x1 X coordinate of first point of triangle
     * @param y1 Y coordinate of first point of triangle
     * @param z1 Z coordinate of first point of triangle
     * @param x2 X coordinate of second point of triangle
     * @param y2 Y coordinate of second point of triangle
     * @param z2 Z coordinate of second point of triangle
     * @param x3 X coordinate of third point of triangle
     * @param y3 Y coordinate of third point of triangle
     * @param z3 Z coordinate of third point of triangle
     */
    public void rasterize (int x1, int y1, double z1, int x2, int y2, double z2, int x3, int y3, double z3)
    {
        // Initialize line rasterizer
        this.lineRasterizer.setRaster(this.raster);
        this.lineRasterizer.setColour(this.colour);
        // Get intervals on X,Y axis
        int maxX = Integer.max(x1, Integer.max(x2, x3));
        int minX = Integer.min(x1, Integer.min(x2, x3));
        int maxY = Integer.max(y1, Integer.max(y2, y3));
        int minY = Integer.min(y1, Integer.min(y2, y3));
        int dX = maxX - minX;
        int dY = maxY - minY;
        if (dX > dY)
        {
            // Sort points according to X axis
            Point3D p1 = new Point3D(x1, y1, z1);
            Point3D p2 = new Point3D(x2, y2, z2);
            Point3D p3 = new Point3D(x3, y3, z3);
            if (p3.x < p2.x)
            {
                Point3D tmp = p2;
                p2 = p3;
                p3 = tmp;
            }
            if (p2.x < p1.x)
            {
                Point3D tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p3.x < p2.x)
            {
                Point3D tmp = p2;
                p2 = p3;
                p3 = tmp;
            }
            this.rasterizeX(
                    p1.x, p1.y, p1.z,
                    p2.x, p2.y, p2.z,
                    p3.x, p3.y, p3.z
            );
        }
        else
        {
            // Sort points according to Y axis
            Point3D p1 = new Point3D(x1, y1, z1);
            Point3D p2 = new Point3D(x2, y2, z2);
            Point3D p3 = new Point3D(x3, y3, z3);
            if (p3.y < p2.y)
            {
                Point3D tmp = p2;
                p2 = p3;
                p3 = tmp;
            }
            if (p2.y < p1.y)
            {
                Point3D tmp = p1;
                p1 = p2;
                p2 = tmp;
            }
            if (p3.y < p2.y)
            {
                Point3D tmp = p2;
                p2 = p3;
                p3 = tmp;
            }
            this.rasterizeY(
                    p1.x, p1.y, p1.z,
                    p2.x, p2.y, p2.z,
                    p3.x, p3.y, p3.z
            );
        }
    }
    
    /**
     * Rasterizes triangle with X axis as controlling axis
     * @param x1 X coordinate of first point of triangle
     * @param y1 Y coordinate of first point of triangle
     * @param z1 Z coordinate of first point of triangle
     * @param x2 X coordinate of second point of triangle
     * @param y2 Y coordinate of second point of triangle
     * @param z2 Z coordinate of second point of triangle
     * @param x3 X coordinate of third point of triangle
     * @param y3 Y coordinate of third point of triangle
     * @param z3 Z coordinate of third point of triangle
     */
    private void rasterizeX(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3)
    {
        double x = x1;
        double stepY2 = (y2 - y1)/(x2 - x1);
        double stepY3 = (y3 - y1)/(x3 - x1);
        double stepZ2 = (z2 - z1)/(x2 - x1);
        double stepZ3 = (z3 - z1)/(x3 - x1);
        double stepY23 = (y3 - y2)/(x3 - x2);
        double stepZ23 = (z3 - z2)/(x3 - x2);
        // X1 -> X2
        for(; x < x2; x++)
        {
            this.lineRasterizer.rasterize(
                    (int)Math.round(x), (int)Math.round(y1 + (x * stepY3)), z1 + (x * stepZ3),
                    (int)Math.round(x), (int)Math.round(y1 + (x * stepY2)), z1 + (x * stepZ2)
            );
        }
        // X2 -> X3
        for(; x < x3; x++)
        {
            this.lineRasterizer.rasterize(
                    (int)Math.round(x), (int)Math.round(y1 + (x * stepY3)), z1 + (x * stepZ3),
                    (int)Math.round(x), (int)Math.round(y2 + (x * stepY23)), z2 + (x * stepZ23)
            );
        }
    }
    
    /**
     * Rasterizes triangle with Y axis as controlling axis
     * @param x1 X coordinate of first point of triangle
     * @param y1 Y coordinate of first point of triangle
     * @param z1 Z coordinate of first point of triangle
     * @param x2 X coordinate of second point of triangle
     * @param y2 Y coordinate of second point of triangle
     * @param z2 Z coordinate of second point of triangle
     * @param x3 X coordinate of third point of triangle
     * @param y3 Y coordinate of third point of triangle
     * @param z3 Z coordinate of third point of triangle
     */
    private void rasterizeY(double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3)
    {
        double y = y1;
        double stepX2 = (x2 - x1)/(y2 - y1);
        double stepX3 = (x3 - x1)/(y3 - y1);
        double stepZ2 = (z2 - z1)/(y2 - y1);
        double stepZ3 = (z3 - z1)/(y3 - y1);
        // Y1 -> Y2
        for(y = y1; y < y2; y++)
        {
            int xA = (int) Math.round(x1 + (y * stepX2));
            int yA = (int) Math.round(y);
            double zA = z1 + (y * stepZ2);
            
            int xB = (int) Math.round(x1 + (y * stepX3));
            int yB = (int) Math.round(y);
            double zB = z1 + (y * stepZ3);
            
            this.lineRasterizer.rasterize(xA, yA, zA, xB, yB, zB);
        }
    }
}
