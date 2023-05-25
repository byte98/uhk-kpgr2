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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller.MainController;

/**
 * Class abstracting all FXML controllers
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public abstract class FXMLController
{
    /**
     * Main controller of application
     */
    protected MainController mainController;
    
    public abstract void resetValue();
    
    /**
     * Sets main controller of application
     * @param controller Reference to main controller of application
     */
    public void setMainController(MainController controller)
    {
        this.mainController = controller;
    }
}
