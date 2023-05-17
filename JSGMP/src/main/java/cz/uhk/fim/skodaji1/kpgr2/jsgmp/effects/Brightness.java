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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap.BitmapChangedActionListener;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

/**
 * Class which handles changing of brightness
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Brightness implements Effect
{
    
    /**
     * Histogram of brightness
     */
    private final Histogram histogram;
    
    /**
     * Bitmap on which brightness effect will be applied on
     */
    private final Bitmap bitmap;
    
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
     * Actual value of effect
     */
    private int value = 0;
    
    /**
     * Last applied value of effect
     */
    private int lastApplied = 0;
    
    /**
     * Creates new handler of brightness effect
     * @param bitmap Bitmap which brightness will be handled
     */
    public Brightness(Bitmap bitmap)
    {
        this.bitmap = bitmap;
        this.histogram = ThreadManager.createHistogram(
                (Pixel px) -> {
                    return (int)Math.round(Brightness.R_COEFF * px.getRed() + Brightness.G_COEFF * px.getGreen() + Brightness.B_COEFF * px.getBlue());
                },
                this.bitmap,
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.BLACK,
                Color.rgb(255, 227, 105),
                256
        );
    }    
    
    /**
     * Gets histogram of brightness
     * @return Image containing histogram of brightness
     */
    public Image getHistogram()
    {
        return this.histogram.getImage();
    }
    
    @Override
    public Pixel apply(Pixel pixel)
    {
        int delta = this.value - this.lastApplied;
        double target = (100f + (double)delta) / 100f;
        Pixel reti = new Pixel(pixel.getRed(), pixel.getGreen(), pixel.getBlue(), pixel.getAlpha());
        if (delta != 0)
        {
            Color color = Color.rgb(pixel.getRed(), pixel.getGreen(), pixel.getBlue());
            double brightness = color.getBrightness() * target;
            if (brightness < 0) brightness = 0f; if (brightness > 1) brightness = 1f;
            Color newColor = Color.hsb(color.getHue(), color.getSaturation(), brightness);
            reti = new Pixel(
                    (short)Math.round(newColor.getRed() * 255f),
                    (short)Math.round(newColor.getGreen() * 255f),
                    (short)Math.round(newColor.getBlue() * 255f)
            );
        }
        return reti;
    }

    @Override
    public void setValue(int value)
    {
        this.lastApplied = this.value;
        this.value = value;
    }

    @Override
    public int getValue()
    {
        return this.value;
    }
}
