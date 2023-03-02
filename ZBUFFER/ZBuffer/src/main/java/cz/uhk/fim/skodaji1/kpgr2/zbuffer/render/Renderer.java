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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.render;

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.Raster;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.view.Panel;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Objects;

/**
 * Class representing renderer of the scene
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Renderer
{
    /**
     * Object where output of renderer will be visible
     */
    private Panel output;
    
    /**
     * Camera space of renderer
     */
    private CameraSpace camSpace;
    
    /**
     * Scene which will be rendered
     */
    private Scene scene;
    
    /**
     * Creates new renderer
     */
    public Renderer()
    {
        // pass
    }
    
    /**
     * Creates new renderer
     * @param scene Scene which will be rendered
     * @param cs Camera space of renderer
     */
    public Renderer(Scene scene, CameraSpace cs)
    {
        this.scene = scene;
        this.camSpace = cs;
        this.output = new Panel(this.camSpace.getWidth(), this.camSpace.getHeight());
    }

    
    /**
     * Sets scene which will be rendered
     * @param s Scene which will be rendered
     */
    public void setScene(Scene s)
    {
        this.scene = s;
    }
    
    /**
     * Sets camera space of renderer
     * @param cs New camera space of renderer
     */
    public void setCameraSpace(CameraSpace cs)
    {
        this.camSpace = cs;
        if (Objects.isNull(this.output))
        {
            this.output = new Panel(this.camSpace.getWidth(), this.camSpace.getHeight());
        }
        else
        {
            this.output.setPreferredSize(new Dimension(this.camSpace.getWidth(), this.camSpace.getHeight()));
        }
    }
    
    /**
     * Gets camera space of renderer
     * @return Camera space of renderer
     */
    public CameraSpace getCameraSpace()
    {
        return this.camSpace;
    }
    
    /**
     * Gets object where output of renderer will be displayed
     * @return Graphical object containing output of renderer
     */
    public Panel getOutput()
    {
        return this.output;
    }
}
