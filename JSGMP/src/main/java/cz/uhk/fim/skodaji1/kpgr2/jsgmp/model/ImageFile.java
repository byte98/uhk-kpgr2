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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.model;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;

/**
 * Class representing file containing image data
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class ImageFile
{
    /**
     * Path to file
     */
    private final String path;    
    
    /**
     * Image data stored in file
     */
    private final Bitmap data;
    
    /**
     * Creates new wrapper for file with image data
     * @param path Path to file with image data
     */
    public ImageFile(String path)
    {
        this.path = path;
        Bitmap data = new Bitmap(1, 1);
        try
        {
            BufferedImage rawImage = ImageIO.read(new File(path));
            data = ThreadManager.createBitmap(rawImage.getWidth(), rawImage.getHeight());
            Bitmap.BitmapTransaction transaction = new Bitmap.BitmapTransaction();
            for (int y = 0; y < rawImage.getHeight(); y++)
            {
                for (int x = 0; x < rawImage.getWidth(); x++)
                {
                    Color c = new Color(rawImage.getRGB(x, y), true);
                    transaction.setPixel(x, y, new Pixel(
                            (short)c.getRed(),
                            (short)c.getGreen(),
                            (short)c.getBlue(),
                            (short)c.getAlpha()
                    ));
                }
            }
            data.processTransaction(transaction);
        }
        catch (IOException ex)
        {
            Logger.getLogger(ImageFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.data = data;
    }
    
    /**
     * Gets path to file
     * @return Path to file
     */
    public String getPath()
    {
        return Paths.get(this.path).toAbsolutePath().normalize().toString();
    }
    
    /**
     * Gets bitmap with data
     * @return Bitmap with data from image file
     */
    public Bitmap getBitmap()
    {
        return this.data;
    }
}
