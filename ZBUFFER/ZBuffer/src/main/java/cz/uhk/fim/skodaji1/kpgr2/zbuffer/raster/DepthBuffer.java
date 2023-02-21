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
 * Class implementing buffer of depth information
 * Copied from GitLab and implemented another functionality
 * @author Jakub Benes <jakub.benes@uhk.cz> & Jiri Skoda <jiri.skoda@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/raster/DepthBuffer.java>
 */
public class DepthBuffer implements Raster<Double>
{
    /**
     * Array with information about depth
     */
    private final double[][] data;
    
    /**
     * Element indicating clear pixel
     */
    private double clearElement;
    
    /**
     * Width of buffer
     */
    private int width;
    
    /**
     * Height of buffer
     */
    private int height;
    
    /**
     * Creates new buffer of depth information
     * @param width Width of buffer
     * @param height Height of buffer
     */
    public DepthBuffer(int width, int height)
    {
        this.data = new double[height][width];
        this.clearElement = 1;
    }

    @Override
    public void clear()
    {
        for (int row = 0; row < this.height; row++)
        {
            for (int col = 0; col < this.data[row].length; col++)
            {
                this.data[row][col] = this.clearElement;
            }
        }
    }

    @Override
    public void setClearElement(Double element)
    {
        this.clearElement = element.doubleValue();
    }

    @Override
    public int getWidth()
    {
        return this.width;
    }

    @Override
    public int getHeight()
    {
        return this.height;
    }

    @Override
    public Double getElement(int x, int y)
    {
        Double reti = null;
        if (this.isInside(x, y))
        {
            reti = this.data[y][x];
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(String.format("[%d; %d] is not valid address in depth buffer! Dimensions of buffer: %d x %d.", x, y, this.getWidth(), this.getHeight()));
        }
        return reti;
    }

    @Override
    public void setElement(int x, int y, Double element)
    {
        if (this.isInside(x, y))
        {
            this.data[y][x] = element.doubleValue();
        }
        else
        {
            throw new ArrayIndexOutOfBoundsException(String.format("[%d; %d] is not valid address in depth buffer! Dimensions of buffer: %d x %d.", x, y, this.getWidth(), this.getHeight()));
        }
    }
}
