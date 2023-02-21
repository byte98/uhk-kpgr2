package cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.persistence.JsonLoader;
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
     * Reference to main window of program
     */
    private final MainWindow mainWindow;
       
    /**
     * Creates new controller of main window
     */
    public MainWindowController()
    {
        this.mainWindow = new MainWindow(this);
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
        this.mainWindow.setScene(loader.getResult());
    }
}
