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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;

/**
 * Class which represents dehomogonisation of vertices
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Dehomogeniser
{
    /**
     * Dehomogenisates vertices
     * @param vertexBuffer Buffer of vertices which will be dehomogenisated
     * @return 
     */
    public Vertex[] apply(Vertex[] vertexBuffer)
    {
        Vertex[] reti = new Vertex[vertexBuffer.length];
        for(int i = 0; i < vertexBuffer.length; i++)
        {
            reti[i] = vertexBuffer[i].dehomog();
        }
        return reti;
    }
}
