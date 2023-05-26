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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.model;

import javafx.scene.paint.Color;

/**
 * Class representing one single pixel
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Pixel
{
    /**
     * Red part of colour of pixel
     */
    private final short red;
    
    /**
     * Green part of colour of pixel
     */
    private final short green;
    
    /**
     * Blue part of colour of pixel
     */
    private final short blue;
    
    /**
     * Value of alpha channel
     */
    private final short alpha;
    
    /**
     * Creates new pixel
     * @param red Red part of colour of pixel
     * @param green Green part of colour of pixel
     * @param blue Blue part of colour of pixel
     */
    public Pixel(short red, short green, short blue)
    {
        this(red, green, blue, (short)255);
    }
    
    /**
     * Creates new pixel
     * @param red Red part of colour of pixel
     * @param green Green part of colour of pixel
     * @param blue Blue part of colour of pixel
     * @param alpha Value of alpha channel
     */
    public Pixel(short red, short green, short blue, short alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    
    /**
     * Creates new pixel
     * @param argb Integer containing red, green, blue and alpha values
     */
    public Pixel(int argb)
    {
        this.alpha = (short)((argb >> 24) & 0xFF);
        this.red   = (short)((argb >> 16) & 0xFF);
        this.green = (short)((argb >> 8 ) & 0xFF);
        this.blue  = (short)((argb >> 0 ) & 0xFF);
    }
    
    /**
     * Creates new pixel
     * @param c Color holding all pixel values
     */
    public Pixel (Color c)
    {
        this.alpha = (short)Math.round(c.getOpacity() * 255f);
        this.red = (short)Math.round(c.getRed() * 255f);
        this.green = (short)Math.round(c.getGreen() * 255f);
        this.blue = (short)Math.round(c.getBlue() * 255f);
    }
    
    /**
     * Gets color object representation of pixel
     * @return Color object representing pixel
     */
    public Color toColor()
    {
        return Color.rgb(this.red, this.green, this.blue, (double)this.alpha / 255f);
    }
    
    /**
     * Gets red part of colour of pixel
     * @return Red part of colour of pixel
     */
    public short getRed()
    {
        return this.red;
    }
    
    /**
     * Gets green part of colour of pixel
     * @return Green part of colour of pixel
     */
    public short getGreen()
    {
        return this.green;
    }
    
    /**
     * Gets blue part of colour of pixel
     * @return Blue part of colour of pixel
     */
    public short getBlue()
    {
        return this.blue;
    }
    
    /**
     * Gets value of alpha channel
     * @return Value of alpha channel
     */
    public short getAlpha()
    {
        return this.alpha;
    }
    
    /**
     * Gets integer with all red, green and blue values and alpha channel value
     * @return Integer with all RGB values and alpha channel value
     */
    public int toARGB()
    {
        int reti = 0;
        reti |= (int)(this.alpha & 0xff) << 24;
        reti |= (int)(this.red   & 0xff) << 16;
        reti |= (int)(this.green & 0xff) << 8;
        reti |= (int)(this.blue  & 0xff) << 0;
        return reti;
    }
}
