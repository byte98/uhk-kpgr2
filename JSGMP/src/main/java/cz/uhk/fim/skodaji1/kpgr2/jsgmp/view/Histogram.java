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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.view;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class representing histogram of bitmap
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Histogram
{   
    /**
     * Height of image representation of histogram
     */
    private static final int HEIGHT = 200;
    
    /**
     * Width of image representation of histogram
     */
    private static final int WIDTH = 512;
    
    /**
     * Clear colour of histogram (used as background)
     */
    private static final Color CLEAR = Color.rgb(51, 51, 51);
    
    /**
     * Image of histogram of red colour channel
     */
    private final WritableImage redImage;
    
    /**
     * Image of histogram of green colour channel
     */
    private final WritableImage greenImage;
    
    /**
     * Image of histogram of blue colour channel
     */
    private final WritableImage blueImage;
    
    /**
     * Array with values for red channel
     */
    private final int[] redData = new int[Histogram.WIDTH];
    
    /**
     * Array with values for green channel
     */
    private final int[] greenData = new int[Histogram.WIDTH];
    
    /**
     * Array with values for blue channel
     */
    private final int[] blueData = new int[Histogram.WIDTH];
    
    /**
     * Bitmap which histogram represents
     */
    private final Bitmap bitmap;
        
        
    /**
     * Creates new histogram
     * @param bitmap Bitmap which histogram will be generated
     */
    public Histogram(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.redImage = new WritableImage(Histogram.WIDTH, Histogram.HEIGHT);
        this.greenImage = new WritableImage(Histogram.WIDTH, Histogram.HEIGHT);
        this.blueImage = new WritableImage(Histogram.WIDTH, Histogram.HEIGHT);
        this.clearImages();
        this.bitmap.addChangeActionListener(new Bitmap.BitmapChangedActionListener()
        {
            @Override
            public void onChange(Bitmap bitmap)
            {
                Histogram.this.generate();
            }        
        });
    }
    
    /**
     * Clear all histogram images
     */
    private void clearImages()
    {
        PixelWriter redWriter = this.redImage.getPixelWriter();
        PixelWriter greenWriter = this.greenImage.getPixelWriter();
        PixelWriter blueWriter = this.blueImage.getPixelWriter();
        for (int y = 0; y < Histogram.HEIGHT; y++)
        {
            for (int x = 0; x < Histogram.WIDTH; x++)
            {
                redWriter.setColor(x, y, Histogram.CLEAR);
                greenWriter.setColor(x, y, Histogram.CLEAR);
                blueWriter.setColor(x, y, Histogram.CLEAR);
            }
        }
    }
    
    /**
     * Gets image with histogram of red colour
     * @return Image with histogram of red colour
     */
    public Image getRed()
    {
        return this.redImage;
    }
    
    /**
     * Gets image with histogram of green colour
     * @return Image with histogram of green colour
     */
    public Image getGreen()
    {
        return this.greenImage;
    }
    
    /**
     * Gets image with histogram of blue colour
     * @return 
     */
    public Image getBlue()
    {
        return this.blueImage;
    }
    
    /**
     * Generates graphical representation of histogram
     */
    private void generate()
    {
        this.clearImages();
    }
}
