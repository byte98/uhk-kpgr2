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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.JSGMP;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.BrightnessContrast;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.ColorEffect;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Grayscale;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Temperature;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.ImageFile;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.FXMLMainWindow;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.GrayscaleChart;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Zoom;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import org.apache.commons.io.FilenameUtils;

/**
 * Class which handles behaviour of main window
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class MainController
{
    /**
     * Reference to main window
     */
    private final FXMLMainWindow mainWindow;
    
    /**
     * Actually processed image
     */
    private ImageFile image;
    
    /**
     * Histogram of red color channel
     */
    private Histogram redHistogram;
    
    /**
     * Histogram of green color channel
     */
    private Histogram greenHistogram;
    
    /**
     * Histogram of blue color channel
     */
    private Histogram blueHistogram;
    
    /**
     * Histogram of cyan color channel
     */
    private Histogram cyanHistogram;
    
    /**
     * Histogram of magenta color channel
     */
    private Histogram magentaHistogram;
    
    /**
     * Histogram of yellow color channel
     */
    private Histogram yellowHistogram;
    
    /**
     * Handler of brightness and contrast of image
     */
    private BrightnessContrast brightness;
    
    /**
     * Handler of all available effects
     */
    private EffectsController effects;
    
    /**
     * Handler of temperature effect
     */
    private Temperature temperature;
    
    /**
     * Flag, whether opened file is initial title or not
     */
    private boolean initialOpen;
    
    /**
     * Handler of image zoom
     */
    private Zoom zoom;
    
    /**
     * Handler of red coloring effect
     */
    private ColorEffect redColorEffect;
    
    /**
     * Handler of green coloring effect
     */
    private ColorEffect greenColorEffect;
    
    /**
     * Handler of blue coloring effect
     */
    private ColorEffect blueColorEffect;
    
    /**
     * Handler of cyan coloring effect
     */
    private ColorEffect cyanColorEffect;
    
    /**
     * Handler of magenta coloring effect
     */
    private ColorEffect magentaColorEffect;
    
    /**
     * Handler of yellow coloring effect
     */
    private ColorEffect yellowColorEffect;
    
    /**
     * Provider of data for grayscale chart
     */
    private GrayscaleChart grayscaleChart;
    
    /**
     * Grayscale effect handler
     */
    private Grayscale grayscaleEffect;
    
    /**
     * Creates new controller of main window
     * @param mainWindow Reference to main window
     */
    public MainController(FXMLMainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        this.initialOpen = true;
    }
    
    /**
     * Kills all unnecessary threads
     */
    private void killUnnecessaryy()
    {
        if (Objects.nonNull(this.redHistogram))
        {
            ThreadManager.stopOne(this.redHistogram);
            this.redHistogram = null;
        }
        if (Objects.nonNull(this.greenHistogram))
        {
            ThreadManager.stopOne(this.greenHistogram);
            this.greenHistogram = null;
        }
        if (Objects.nonNull(this.blueHistogram))
        {
            ThreadManager.stopOne(this.blueHistogram);
            this.blueHistogram = null;
        }
        if (Objects.nonNull(this.cyanHistogram))
        {
            ThreadManager.stopOne(this.cyanHistogram);
            this.cyanHistogram = null;
        }
        if (Objects.nonNull(this.magentaHistogram))
        {
            ThreadManager.stopOne(this.magentaHistogram);
            this.magentaHistogram = null;
        }
        if (Objects.nonNull(this.yellowHistogram))
        {
            ThreadManager.stopOne(this.yellowHistogram);
            this.yellowHistogram = null;
        }
        
        if (Objects.nonNull(this.brightness))
        {
            ThreadManager.stopOne(this.brightness);
            this.brightness = null;
        }
        
        if (Objects.nonNull(this.zoom))
        {
            this.zoom.stop();
            this.zoom = null;
        }
        
        if (Objects.nonNull(this.effects))
        {
            ThreadManager.stopOne(this.effects);
            this.effects = null;
        }
        if (Objects.nonNull(this.grayscaleChart))
        {
            ThreadManager.stopOne(this.grayscaleChart);
            this.grayscaleChart = null;
        }
        
        this.temperature = null;
        this.redColorEffect = null;
        this.greenColorEffect = null;
        this.blueColorEffect = null;
        this.cyanColorEffect = null;
        this.magentaColorEffect = null;
        this.yellowColorEffect = null;
        this.grayscaleEffect = null;
        
        System.gc();
    }
    
    /**
     * Handles file to open has been selected
     * @param path Path to selected file
     */
    public void fileOpen(String path)
    {
        this.killUnnecessaryy();
        this.image = new ImageFile(path);
        this.effects = ThreadManager.createEffectsController(this.image.getBitmap());
        
        this.mainWindow.setImage(this.image.getBitmap());
        
        this.redColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getRed();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(255, 0, 0),
                256
        ), true, false, false);
        this.redHistogram = this.redColorEffect.getHistogram();
        this.mainWindow.setRedHistogram(this.redHistogram.getImage());
        
        this.greenColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getGreen();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(0, 255, 0),
                256
        ), false, true, false);
        this.greenHistogram = this.greenColorEffect.getHistogram();
        this.mainWindow.setGreenHistogram(this.greenHistogram.getImage());
        
        this.blueColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getBlue();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(0, 0, 255),
                256
        ), false, false, true);
        this.blueHistogram = this.blueColorEffect.getHistogram();
        this.mainWindow.setBlueHistogram(this.blueHistogram.getImage());
        
        
        this.cyanColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)Math.round((double)(px.getGreen() + px.getBlue()) / 2f);},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(0, 255, 255),
                256
        ), false, true, true);
        this.cyanHistogram = this.cyanColorEffect.getHistogram();
        this.mainWindow.setCyanHistogram(this.cyanHistogram.getImage());
        
        this.magentaColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)Math.round((double)(px.getRed() + px.getBlue()) / 2f);},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(255, 0, 255),
                256
        ), true, false, true);
        this.magentaHistogram = this.magentaColorEffect.getHistogram();
        this.mainWindow.setMagentaHistogram(this.magentaHistogram.getImage());
        
        this.yellowColorEffect = new ColorEffect(ThreadManager.createHistogram(
                (Pixel px) -> {return (int)Math.round((double)(px.getRed() + px.getGreen()) / 2f);},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(255, 255, 0),
                256
        ), true, true, false);
        this.yellowHistogram = this.yellowColorEffect.getHistogram();
        this.mainWindow.setYellowHistogram(this.yellowHistogram.getImage());
        
        this.effects.addEffect(this.redColorEffect);
        this.effects.addEffect(this.greenColorEffect);
        this.effects.addEffect(this.blueColorEffect);
        this.effects.addEffect(this.cyanColorEffect);
        this.effects.addEffect(this.magentaColorEffect);
        this.effects.addEffect(this.yellowColorEffect);
        
        this.brightness = ThreadManager.createBrightnessContrastEffect(this.image.getBitmap());
        this.effects.addEffect(this.brightness);
        this.mainWindow.setBrightnessHistogram(this.brightness.getBrightnessHistogram());
        this.mainWindow.setContrastHistogram(this.brightness.getContrastHistogram());
        this.mainWindow.setBrightnessContrastChart(this.brightness.getChart());
        
        this.temperature = new Temperature(this.image.getBitmap());
        this.effects.addEffect(this.temperature);
        this.mainWindow.setTemperatureHistogram(this.temperature.getHistogram().getImage());
        
        Path p = Paths.get(path);
        this.mainWindow.setFileName(p.getFileName().toString());
        this.mainWindow.setFilePath(Paths.get(path).toAbsolutePath().normalize().toString());
        this.mainWindow.setFileSize(image.getBitmap().getWidth(), image.getBitmap().getHeight());
        
        this.zoom = new Zoom(this.image.getBitmap());
        this.mainWindow.setZoom(this.zoom);
        
        this.grayscaleEffect = new Grayscale();
        this.grayscaleChart = ThreadManager.createGrayscaleChart(this.image.getBitmap());
        this.mainWindow.setGrayscaleChart(this.grayscaleChart);
        this.effects.addEffect(this.grayscaleEffect);
        
        this.image.getBitmap().setOriginal();
        if (this.initialOpen == false)
        {            
            this.mainWindow.disableMenu(false);
            this.mainWindow.resetValues();
        }
        this.initialOpen = false;
    }
    
    /**
     * Handles change of brightness
     * @param newValue New value of brightness
     */
    public void brightnessChanged(int newValue)
    {
        this.brightness.setBrightness(newValue);
    }
    
    /**
     * Handles change of contrast
     * @param newValue New value of contrast
     */
    public void contrastChanged(double newValue)
    {
        this.brightness.setContrast(newValue);
    }
    
    /**
     * Handles change of temperature
     * @param newValue New value of temperature
     */
    public void temperatureChanged(int newValue)
    {
        this.temperature.setTemperature(newValue);
    }
    
    /**
     * Handles load of main window
     */
    public void mainWindowLoaded()
    {
        this.fileOpen(JSGMP.class.getResource("icons/title.png").getFile());
        this.mainWindow.setFileName("(žádný soubor)");
        this.mainWindow.setFilePath("(žádný soubor)");
        this.mainWindow.setFileSize(0, 0);
        this.mainWindow.disableMenu(true);
        this.mainWindow.centerImage();
    }
    
    /**
     * Handles change of blue color value
     * @param newValue New value of blue color
     */
    public void blueChanged(int newValue)
    {
        this.blueColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of red color value
     * @param newValue New value of red color
     */
    public void redChanged(int newValue)
    {
        this.redColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of green color value
     * @param newValue New value of green color
     */
    public void greenChanged(int newValue)
    {
        this.greenColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of yellow color value
     * @param newValue New value of yellow color
     */
    public void yellowChanged(int newValue)
    {
        this.yellowColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of magenta color value
     * @param newValue New value of magenta color
     */
    public void magentaChanged(int newValue)
    {
        this.magentaColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of cyan color value
     * @param newValue New value of cyan color
     */
    public void cyanChanged(int newValue)
    {
        this.cyanColorEffect.setValue(newValue);
    }
    
    /**
     * Handles change of grayscale percentage
     * @param newValue New percentage of grayscale
     */
    public void grayscaleChanged(double newValue)
    {
        this.grayscaleEffect.setValue(newValue);
    }
    
    /**
     * Handles click on save button
     */
    public void saveClicked()
    {
        this.saveClicked(this.image.getPath());
    }
    
    
    /**
     * Handles click on save button
     * @param path Path to output file
     */
    public void saveClicked(String path)
    {
        File output = new File(path);
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(this.image.getBitmap().toImage(), null);
        String format = FilenameUtils.getExtension(path);
        try
        {
            ImageIO.write(bufferedImage, format, output);
        }
        catch (IOException ex)
        {
            Logger.getLogger(MainController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
