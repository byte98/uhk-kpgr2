package cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.persistence.JsonLoader;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.render.Renderer;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.view.MainWindow;
import java.io.File;

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

/**
 * Class which handles behaviour of main window
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class MainWindowController
{
    /**
     * Speed of view angle change
     */
    private static final double VIEW_SPEED = 0.01;
    
    /**
     * Reference to main window of program
     */
    private final MainWindow mainWindow;
    
    /**
     * Object which handles whole rendering process
     */
    private Renderer renderer;
    
    /**
     * Scene which will be rendered
     */
    private Scene scene;
    
    /**
     * Flag, whether application is in interactive mode
     */
    private boolean interactive;
       
    /**
     * Creates new controller of main window
     */
    public MainWindowController()
    {
        this.mainWindow = new MainWindow(this);
        this.interactive = false;
    }
    
    /**
     * Function which tells controller to take control over program
     */
    public void takeControl()
    {
        this.mainWindow.setVisible(true);
    }
    
    /**
     * Handles click on load button
     * @param f File selected to be loaded
     */
    public void loadClicked(File f)
    {
        JsonLoader loader = new JsonLoader(f.getAbsolutePath());
        loader.load();
        this.scene = loader.getResult();
        this.renderer = loader.getRenderer();
        this.mainWindow.setScene(this.scene);
        this.mainWindow.setRenderer(this.renderer);
        this.renderer.setScene(this.scene);
        this.renderer.run();
    }

    /**
     * Handles toggle of axis button
     * @param selected Flag, whether axis button is selected or not
     */
    public void axisToggled(boolean selected)
    {
        if (selected == true)
        {
            this.renderer.showAxis();
        }
        else if (selected == false)
        {
            this.renderer.hideAxis();
        }
        this.renderer.run();
    }
    
    /**
     * Checks, whether application is in interactive mode
     * @return TRUE, if application is in interactive mode, FALSE otherwise
     */
    public boolean isInteractive()
    {
        return this.interactive;
    }
    
    /**
     * Handles toggle of interactive mode button
     * @param interactive Flag, whether interactive button is selected or not
     */
    public void toggleInteractive(boolean interactive)
    {
        this.interactive = interactive;
    }
    
    /**
     * Handles move of mouse
     * @param dx Delta on X axis
     * @param dy Delta on Y axis
     */
    public void mouseMoved(int dx, int dy)
    {
        double azimuth = (this.scene.getCamera().getAzimuth() + (double)dx * MainWindowController.VIEW_SPEED) % (2 * Math.PI);
        double zenith = (this.scene.getCamera().getZenith() + (double)dy * MainWindowController.VIEW_SPEED);
        if (zenith > Math.PI) zenith = Math.PI;
        if (zenith < 0)       zenith = 0;
        this.scene.getCamera().setAzimuth(azimuth);
        this.scene.getCamera().setZenith(zenith);
    }
}
