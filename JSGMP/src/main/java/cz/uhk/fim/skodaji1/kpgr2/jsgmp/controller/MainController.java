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
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Temperature;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Globals;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.ImageFile;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Pixel;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.FXMLMainWindow;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.nio.file.Path;
import java.nio.file.Paths;
import javafx.scene.paint.Color;

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
     * Creates new controller of main window
     * @param mainWindow Reference to main window
     */
    public MainController(FXMLMainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
        this.initialOpen = true;
    }
    
    /**
     * Handles file to open has been selected
     * @param path Path to selected file
     */
    public void fileOpen(String path)
    {
        this.image = new ImageFile(path);
        this.effects = ThreadManager.createEffectsController(this.image.getBitmap());
        
        this.mainWindow.setImage(this.image.getBitmap());
        
        this.redHistogram = ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getRed();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(255, 0, 0),
                256
        );
        this.mainWindow.setRedHistogram(this.redHistogram.getImage());
        
        this.greenHistogram = ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getGreen();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(0, 255, 0),
                256
        );
        this.mainWindow.setGreenHistogram(this.greenHistogram.getImage());
        
        this.blueHistogram = ThreadManager.createHistogram(
                (Pixel px) -> {return (int)px.getBlue();},
                this.image.getBitmap(),
                Globals.HISTOGRAM_WIDTH,
                Globals.HISTOGRAM_HEIGHT,
                Color.rgb(0, 0, 0),
                Color.rgb(0, 0, 255),
                256
        );
        this.mainWindow.setBlueHistogram(this.blueHistogram.getImage());
        
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
        
        this.image.getBitmap().setOriginal();
        if (this.initialOpen == false)
        {            
            this.mainWindow.diableMenu(false);
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
        this.mainWindow.diableMenu(true);
    }
}
