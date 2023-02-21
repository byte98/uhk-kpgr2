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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.view;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

/**
 * Class representing window with details of objects
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class DetailsWindow extends JPanel
{
    /**
     * Content of window
     */
    private JSplitPane content;
    
    /**
     * Scene which details will be shown
     */
    private Scene scene;
    
    /**
     * Creates new window with details of objects
     */
    public DetailsWindow()
    {
        this.initializeComponents();
    }
    
    /**
     * Initializes components
     */
    private void initializeComponents()
    {
        super.setLayout(new BorderLayout());
        this.content = new JSplitPane();
        this.initializeTree();
    }
    
    /**
     * Initializes tree of items in scene
     */
    private void initializeTree()
    {
        
    }
    
    
}
