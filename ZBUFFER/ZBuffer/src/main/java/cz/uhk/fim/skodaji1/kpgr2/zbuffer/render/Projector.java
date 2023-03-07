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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.render;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class which can perform projection into 2D space (with preserving Z coordinate)
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Projector
{
    /**
     * Performs projection into 2D space
     * @param vertexBuffer Vertex buffer which will be projected into 2D space
     * @return Buffer of vertices projected into 2D space
     */
    public Vertex[] apply(Vertex[] vertexBuffer)
    {
        Vertex[] reti = new Vertex[vertexBuffer.length];
        for (int i = 0; i < vertexBuffer.length; i++)
        {
            reti[i] = vertexBuffer[i].mul((double)1 / vertexBuffer[i].getPosition().w);
        }
        return reti;
    }
    
}
