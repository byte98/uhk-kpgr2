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

import cz.uhk.fim.kpgr2.transforms.Col;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.Raster;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.ZBuffer;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.view.Panel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JPanel;

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
     * Panel wrapping output
     */
    private final JPanel wrapper;
    
    /**
     * Camera space of renderer
     */
    private CameraSpace camSpace;
    
    /**
     * World space
     */
    private WorldSpace worldSpace;
    
    /**
     * Scene which will be rendered
     */
    private Scene scene;
    
    /**
     * Raster which handles final drawing
     */
    private ZBuffer raster;
    
    /**
     * Creates new renderer
     */
    public Renderer()
    {
        this.wrapper = new JPanel();
        this.wrapper.setLayout(new GridBagLayout());
        this.wrapper.setOpaque(false);
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
        this.wrapper = new JPanel();
        this.wrapper.setLayout(new GridBagLayout());
        this.wrapper.setOpaque(false);
        this.initOutput();
    }

    
    /**
     * Initializes output
     */
    private void initOutput()
    {
        this.wrapper.removeAll();
        this.output = new Panel(this.camSpace.getWidth(), this.camSpace.getHeight());        
        this.raster = new ZBuffer(this.output.getRaster());
        this.wrapper.add(this.output);
    }
    
    /**
     * Sets scene which will be rendered
     * @param s Scene which will be rendered
     */
    public void setScene(Scene s)
    {
        this.scene = s;
        this.worldSpace = new WorldSpace(s.getCamera());
    }
    
    /**
     * Sets camera space of renderer
     * @param cs New camera space of renderer
     */
    public void setCameraSpace(CameraSpace cs)
    {
        this.camSpace = cs;
        this.initOutput();
        this.camSpace.addChangeCallback((ObjectChangeCallback) () -> {
            this.cameraSpaceChanged();
        });
    }
    
    /**
     * Handles change of camera space
     */
    private void cameraSpaceChanged()
    {
        this.initOutput();
        this.run();
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
    public JComponent getOutput()
    {
        return this.wrapper;
    }
    
    /**
     * Redraws output
     */
    private void redraw()
    {
        this.wrapper.revalidate();
        this.wrapper.repaint();
        this.output.revalidate();
        this.output.repaint();
    }
    
    /**
     * Runs renderer through whole rendering process
     */
    public void run()
    {
        this.raster.clear();
        if (Objects.nonNull(this.scene))
        {
            this.scene.generateBuffers();
            // Follows steps of rendering pipeline

            Vertex[] vbuffer = this.worldSpace.apply(this.scene.getVertexBuffer()); // 2) Transformation to world space
                     vbuffer = this.camSpace.apply(vbuffer);                        // 3) Transform to camera space
        }        
        this.redraw();
    }
}
