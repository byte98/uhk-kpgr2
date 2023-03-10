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

import cz.uhk.fim.kpgr2.transforms.Point3D;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class which performs transformation into window
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Viewport
{
    /**
     * Width of window
     */
    private final int width;
    
    /**
     * Height of window
     */
    private final int height;
    
    /**
     * Creates new view port
     * @param width Width of window
     * @param height Height of window
     */
    public Viewport(int width, int height)
    {
        this.width = width;
        this.height = height;
    }
    
    /**
     * Applies transformation to window on vertex buffer
     * @param vertexBuffer Vertex buffer on which will be transformation applied
     * @return Buffer of vertices transformed into view port
     */
    public Vertex[] apply(Vertex[] vertexBuffer)
    {
        Vertex[] reti = new Vertex[vertexBuffer.length];
        for (int i = 0; i < vertexBuffer.length; i++)
        {
            reti[i] = vertexBuffer[i].clone();
            double x = reti[i].getPosition().x;
            double y = reti[i].getPosition().y;
            double w = (double)this.width;
            double h = (double)this.height;
            double x1 = ((double)1 / (double)2)*(w - (double)1)*(x + (double)1);
            double y1 = ((double)1 / (double)2)*(h - (double)1)*((double)1 - y);
            reti[i].setPosition(new Point3D(x1, y1, reti[i].getPosition().z, reti[i].getPosition().w));
        }
        return reti;
    }
}
