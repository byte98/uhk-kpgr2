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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.Brightness;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.ImageFile;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.FXMLMainWindow;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.Histogram;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class which handles behaviour of main window
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class MainWindowController
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
     * Histogram of image
     */
    private Histogram histogram;
    
    /**
     * Handler of brightness of image
     */
    private Brightness brightness;
    
    /**
     * Creates new controller of main window
     * @param mainWindow Reference to main window
     */
    public MainWindowController(FXMLMainWindow mainWindow)
    {
        this.mainWindow = mainWindow;
    }
    
    /**
     * Handles file to open has been selected
     * @param path Path to selected file
     */
    public void fileOpen(String path)
    {
        this.image = new ImageFile(path);
        this.histogram = new Histogram(this.image.getBitmap());
        this.brightness = new Brightness(this.image.getBitmap());
        this.mainWindow.setImage(this.image.getBitmap());
        this.mainWindow.setHistogram(this.histogram);
        this.mainWindow.setBrightness(this.brightness);
        Path p = Paths.get(path);
        this.mainWindow.setFileName(p.getFileName().toString());
        this.mainWindow.setFilePath(Paths.get(path).toAbsolutePath().normalize().toString());
        this.mainWindow.setFileSize(image.getBitmap().getWidth(), image.getBitmap().getHeight());
    }
}
