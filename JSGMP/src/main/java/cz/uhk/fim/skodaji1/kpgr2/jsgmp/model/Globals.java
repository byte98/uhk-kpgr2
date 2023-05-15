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

import javafx.scene.paint.Color;

/**
 * Class which stores global settings
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Globals {
    /**
     * Height of histograms
     */
    public static final int HISTOGRAM_HEIGHT = 200;
    
    /**
     * Width of histograms
     */
    public static final int HISTOGRAM_WIDTH = 512;
    
    /**
     * Background of histogram where are no data present
     */
    public static final Pixel HISTOGRAM_CLEAR = new Pixel((short)51, (short)51, (short)51);
}
