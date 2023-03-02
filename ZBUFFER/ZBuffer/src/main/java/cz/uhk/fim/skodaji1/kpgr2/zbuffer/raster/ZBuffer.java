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
 * Class implementing ZBuffer for handling visibility
 * Copied from GitLab and implemented another functionality
 * @author Jakub Benes <jakub.benes@uhk.cz> & Jiri Skoda <jiri.skoda@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/raster/ZBuffer.java>
 */
public class ZBuffer implements Raster<Col>
{
    /**
     * Buffer of image data
     */
    private final ImageBuffer imageBuffer;
    
    /**
     * Buffer of depth data
     */
    private final DepthBuffer depthBuffer;

    /**
     * Creates new ZBuffer
     * @param imageBuffer Buffer with image data
     */
    public ZBuffer(ImageBuffer imageBuffer)
    {
        this.imageBuffer = imageBuffer;
        this.depthBuffer = new DepthBuffer(imageBuffer.getWidth(), imageBuffer.getHeight());
    }

    @Override
    public void clear()
    {
        this.imageBuffer.clear();
    }

    @Override
    public void setClearElement(Col element)
    {
        this.imageBuffer.setClearElement(element);
    }

    @Override
    public int getWidth()
    {
        return this.imageBuffer.getWidth();
    }

    @Override
    public int getHeight()
    {
        return this.imageBuffer.getHeight();
    }

    @Override
    public Col getElement(int x, int y)
    {
        Col reti = null;
        if (this.isInside(x, y))
        {
            reti = this.imageBuffer.getElement(x, y);
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(String.format("[%d; %d] is not valid address in depth buffer! Dimensions of buffer: %d x %d.", x, y, this.getWidth(), this.getHeight()));
        }
        return reti;
    }

    @Override
    public void setElement(int x, int y, Col element)
    {
        throw new IllegalStateException("Method ZBuffer::setElement(int, int, Col) cannot be called directly! Instead of this, call ZBuffer::setElement(int, int, double, Col).");
    }
    
    /**
     * Sets element to ZBuffer
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param z Position on Z axis
     * @param element New value of selected position
     */
    public void setElement(int x, int y, double z, Col element)
    {
        if (this.isInside(x, y))
        {
            if (z < this.depthBuffer.getElement(x, y).doubleValue())
            {
                this.imageBuffer.setElement(x, y, element);
            }
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(String.format("[%d; %d] is not valid address in Z buffer! Dimensions of buffer: %d x %d.", x, y, this.getWidth(), this.getHeight()));
        }
    }
}
