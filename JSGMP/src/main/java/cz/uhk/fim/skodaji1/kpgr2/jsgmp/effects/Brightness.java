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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap.BitmapChangedActionListener;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class which handles changing of brightness
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Brightness {
    
    /**
     * Histogram of brightness
     */
    private WritableImage histogram;
    
    /**
     * Data used to show histogram
     */
    private final int[] histogramData;
    
    /**
     * Bitmap which brightness will be computed
     */
    private Bitmap bitmap;
    
    /**
     * Color of histogram
     */
    private static final Color HISTOGRAM_COLOR = Color.rgb(255, 247, 201);
    
    /**
     * Coefficient of red channel used when computing brightness
     */
    private static final double R_COEFF = 0.299;
    
    /**
     * Coefficient of green channel used when computing brightness
     */
    private static final double G_COEFF = 0.7152;
    
    /**
     * Coefficient of blue channel used when computing brightness
     */
    private static final double B_COEFF = 0.0722;
    
    /**
     * Creates new handler of brightness effect
     * @param bitmap Bitmap which brightness will be handled
     */
    public Brightness(Bitmap bitmap)
    {
        this.histogram = new WritableImage(Histogram.WIDTH, Histogram.HEIGHT);
        this.histogramData = new int[256];
        this.bitmap = bitmap;
        this.generateHistogram();
        this.bitmap.addChangeActionListener(new BitmapChangedActionListener()
        {
            @Override
            public void onChange(Bitmap bitmap)
            {
                Brightness.this.generateHistogram();
            }            
        });
    }    
    /**
     * Gets histogram of brightness
     * @return Image containing histogram of brightness
     */
    public Image getHistogram()
    {
        return this.histogram;
    }
    
    /**
     * Changes brightness of image
     * @param delta Delta of percentage of brightness incrementation/decrementation
     */
    public void changeBrightness(int delta)
    {
        double target = 1f + ((double)delta / 100f);
        this.bitmap.startTransaction();
        for (int y = 0; y < this.bitmap.getHeight(); y++)
        {
            for (int x = 0; x < this.bitmap.getWidth(); x++)
            {
                Pixel px = this.bitmap.getPixel(x, y);
                int r = (int)Math.round((double)px.getRed() + (target * Brightness.R_COEFF));
                if (r < 0) r = 0; if (r > 255) r = 255;
                int g = (int)Math.round((double)px.getGreen() + (target * Brightness.G_COEFF));
                if (g < 0) g = 0; if (g > 255) g = 255;
                int b = (int)Math.round((double)px.getBlue() + (target * Brightness.B_COEFF));
                if (b < 0) b = 0; if (b > 255) b = 255;
                Pixel newPx = new Pixel((short)r, (short)g, (short)b, px.getAlpha());
                this.bitmap.setPixel(x, y, newPx);
            }
        }
        this.bitmap.finishTransaction();
    }
    
    /**
     * Generates histogram
     */
    private void generateHistogram()
    {
        PixelWriter pw = this.histogram.getPixelWriter();
        
        // Set default background color
        for (int y = 0; y < this.histogram.getHeight(); y++)
        {
            for (int x = 0; x < this.histogram.getWidth(); x++)
            {
                pw.setColor(x, y, Histogram.CLEAR);
            }
        }
        
        // Clear data
        for (int i = 0; i < 256; i++)
        {
            this.histogramData[i] = 0;
        }
        
        // Compute data
        int brightnessMax = Integer.MIN_VALUE;
        for(Pixel px: this.bitmap)
        {
            int brightness = (int)Math.round(Brightness.R_COEFF * px.getRed() + Brightness.G_COEFF * px.getGreen() + Brightness.B_COEFF * px.getBlue());
            if (brightness > 255)
            {
                brightness = 255;
            }
            
            this.histogramData[brightness]++;
            if (this.histogramData[brightness] > brightnessMax)
            {
                brightnessMax = this.histogramData[brightness];
            }
        }
        
        // Generate image
        double pct = (double)Histogram.HEIGHT / 100f;
        double width = Histogram.WIDTH / 256f;
        
        // Interpolate histogram color
        int redFinal = (int)Math.round(Brightness.HISTOGRAM_COLOR.getRed() * 255f);
        int greenFinal = (int)Math.round(Brightness.HISTOGRAM_COLOR.getGreen() * 255f);
        int blueFinal = (int)Math.round(Brightness.HISTOGRAM_COLOR.getBlue() * 255f);
        double redStep = (double)redFinal / 255f;
        double greenStep = (double)greenFinal / 255f;
        double blueStep = (double)blueFinal / 255f;
        
        for (int i = 0; i < 256; i++)
        {            
            int r = (int)Math.round(i * redStep);
            int g = (int)Math.round(i * greenStep);
            int b = (int)Math.round(i * blueStep);
            Color c = Color.rgb(r, g, b);
            int height = (int)Math.round((((double)histogramData[i] / (double)brightnessMax) * 100f) * pct);
            for(int x = (int)(Math.round(i * width)); x < (int)(Math.round(((double)i * width) + width)); x++)
            {
                for (int y = Histogram.HEIGHT - 1; y >= (Histogram.HEIGHT - height < 0 ? 0 : Histogram.HEIGHT - height); y--)
                {
                    
                    pw.setColor(x, y, c);
                }
            }
        }
    }
}
