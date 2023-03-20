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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.render.Renderer;

/**
 * Class which abstracts all rasterizers
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public abstract class AbstractRasterizer
{
    /**
     * Raster to which will be line rasterized
     */
    protected ZBuffer raster;
    
    /**
     * Colour of pixels
     */
    protected PixelProvider colour;
    
    /**
     * Renderer mode of rasterizer
     */
    protected Renderer.RenderType mode = Renderer.RenderType.NORMAL;
    
    /**
     * Sets raster to which line will be rasterized
     * @param raster Raster to which line will be rasterized
     */
    public void setRaster(ZBuffer raster)
    {
        this.raster = raster;
    }
    
    /**
     * Sets colour of line
     * @param colour Provider of pixel colours defining colour of line
     */
    public void setColour(PixelProvider colour)
    {
        this.colour = colour;
    }
    
    /**
     * Sets mode of rasterizer
     * @param mode New mode of rasterizer
     */
    public void setMode(Renderer.RenderType mode)
    {
        this.mode = mode;
    }
}
