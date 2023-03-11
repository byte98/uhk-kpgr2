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

import cz.uhk.fim.kpgr2.transforms.Point3D;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Class which has ability to rasterize triangles
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
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
        Point3D[] points = new Point3D[]{new Point3D(x1, y1, z1), new Point3D(x2, y2, z2), new Point3D(x3, y3, z3)};
        if (dX > dY)
        {
            // Sort points according to X axis
            Arrays.sort(points, 0, points.length, new Comparator<Point3D>(){
                @Override
                public int compare(Point3D t, Point3D t1)
                {
                    return (int)Math.round(t1.x - t.x);
                }
            });
            this.rasterizeX(
                    points[0].x, points[0].y, points[0].z,
                    points[1].x, points[1].y, points[1].z,
                    points[2].x, points[2].y, points[2].z
            );
        }
        else
        {
            // Sort points according to Y axis
            Arrays.sort(points, 0, points.length, new Comparator<Point3D>(){
                @Override
                public int compare(Point3D t, Point3D t1)
                {
                    return (int)Math.round(t.y - t1.y);
                }
            });
            this.rasterizeY(
                    points[0].x, points[0].y, points[0].z,
                    points[1].x, points[1].y, points[1].z,
                    points[2].x, points[2].y, points[2].z
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
        // <editor-fold defaultstate="collapsed" desc="X1 -> X2">
        
        // Deltas from X1 to X2
        double dX1X2     = x2 - x1;
        double dY1Y2     = y2 - y1;
        double dZ1Z2     = z2 - z1;
        int    rangeX1X2 = (int) Math.round(Math.abs(dX1X2));
        
        // Deltas from X1 to X3
        double dX1X3     = x3 - x1;
        double dY1Y3     = y3 - y1;
        double dZ1Z3     = z3 - z1;
        int    rangeX1X3 = (int) Math.round(Math.abs(dX1X3));
        
        // Steps from X1 to X2
        double stepX1X2  = dX1X2 < 0 ? -1 : 1;
        double stepY1Y2  = dY1Y2 / rangeX1X2;
        double stepZ1Z2  = dZ1Z2 / rangeX1X2;
        
        // Steps from X1 to X3
        double stepX1X3  = dX1X3 < 0 ? -1 : 1;
        double stepY1Y3  = dY1Y3 / rangeX1X3;
        double stepZ1Z3  = dZ1Z3 / rangeX1X3;
        
        // Draw loop
        for (int i = 0; i < rangeX1X2; i++)
        {
            int    Ax = (int) Math.round(x1 + (i * stepX1X2));
            int    Ay = (int) Math.round(y1 + (i * stepY1Y2));
            double Az = z1 + (i * stepZ1Z2);
            
            int    Bx = (int) Math.round(x1 + (i * stepX1X3));
            int    By = (int) Math.round(y1 + (i * stepY1Y3));
            double Bz = z1 + (i * stepZ1Z3);
            
            this.lineRasterizer.rasterize(
                    Ax, Ay, Az,
                    Bx, By, Bz
            );
        }
        
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="X2 -> X3">
        
        // Deltas from Y2 to Y3
        double dX2X3     = x3 - x2;
        double dY2Y3     = y3 - y2;
        double dZ2Z3     = z3 - z2;
        int    rangeX2X3 = (int)Math.round(Math.abs(dX2X3));
        
        // Steps from Y2 to Y2
        double stepX2X3  = dX2X3 < 0 ? -1 : 1;
        double stepY2Y3  = dY2Y3 / rangeX2X3;
        double stepZ2Z3  = dZ2Z3 / rangeX2X3;
        
        // Draw loop
        for (int i = 0; i < rangeX2X3; i++)
        {
            int    Ax = (int) Math.round(x2 + (i * stepX2X3));
            int    Ay = (int) Math.round(y2 + (i * stepY2Y3));
            double Az = z2 + (i * stepZ2Z3);
            
            int    Bx = (int) Math.round(x1 + ((rangeX1X2 + i) * stepX1X3));
            int    By = (int) Math.round(y1 + ((rangeX1X2 + i) * stepY1Y3));
            double Bz = z1 + ((rangeX1X2 + i) * stepZ1Z3);
            
            this.lineRasterizer.rasterize(
                    Ax, Ay, Az,
                    Bx, By, Bz
            );
        }
        
        // </editor-fold>
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
        // <editor-fold defaultstate="collapsed" desc="Y1 -> Y2">
        
        // Deltas from Y1 to Y2
        double dX1X2     = x2 - x1;
        double dY1Y2     = y2 - y1;
        double dZ1Z2     = z2 - z1;
        int    rangeY1Y2 = (int) Math.round(Math.abs(dY1Y2));
        
        // Deltas from Y1 to Y3
        double dX1X3     = x3 - x1;
        double dY1Y3     = y3 - y1;
        double dZ1Z3     = z3 - z1;
        int    rangeY1Y3 = (int) Math.round(Math.abs(dY1Y3));
        
        // Steps from Y1 to Y2
        double stepX1X2  = dX1X2 / rangeY1Y2;
        double stepY1Y2  = dY1Y2 < 0 ? -1 : 1;
        double stepZ1Z2  = dZ1Z2 / rangeY1Y2;
        
        // Steps from Y1 to Y3
        double stepX1X3  = dX1X3 / rangeY1Y3;
        double stepY1Y3  = dY1Y3 < 0 ? -1 : 1;
        double stepZ1Z3  = dZ1Z3 / rangeY1Y3;
        
        // Draw loop
        for (int i = 0; i < rangeY1Y2; i++)
        {
            int    Ax = (int) Math.round(x1 + (i * stepX1X2));
            int    Ay = (int) Math.round(y1 + (i * stepY1Y2));
            double Az = z1 + (i * stepZ1Z2);
            
            int    Bx = (int) Math.round(x1 + (i * stepX1X3));
            int    By = (int) Math.round(y1 + (i * stepY1Y3));
            double Bz = z1 + (i * stepZ1Z3);
            
            this.lineRasterizer.rasterize(
                    Ax, Ay, Az,
                    Bx, By, Bz
            );
        }
        
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Y2 -> Y3">
        
        // Deltas from Y2 to Y3
        double dX2X3     = x3 - x2;
        double dY2Y3     = y3 - y2;
        double dZ2Z3     = z3 - z2;
        int    rangeY2Y3 = (int)Math.round(Math.abs(dY2Y3));
        
        // Steps from Y2 to Y2
        double stepX2X3  = dX2X3 / rangeY2Y3;
        double stepY2Y3  = dY2Y3 < 0 ? -1 : 1;
        double stepZ2Z3  = dZ2Z3 / rangeY2Y3;
        
        // Draw loop
        for (int i = 0; i < rangeY2Y3; i++)
        {
            int    Ax = (int) Math.round(x2 + (i * stepX2X3));
            int    Ay = (int) Math.round(y2 + (i * stepY2Y3));
            double Az = z2 + (i * stepZ2Z3);
            
            int    Bx = (int) Math.round(x1 + ((rangeY1Y2 + i) * stepX1X3));
            int    By = (int) Math.round(y1 + ((rangeY1Y2 + i) * stepY1Y3));
            double Bz = z1 + ((rangeY1Y2 + i) * stepZ1Z3);
            
            this.lineRasterizer.rasterize(
                    Ax, Ay, Az,
                    Bx, By, Bz
            );
        }
        
        // </editor-fold>
    }
}
