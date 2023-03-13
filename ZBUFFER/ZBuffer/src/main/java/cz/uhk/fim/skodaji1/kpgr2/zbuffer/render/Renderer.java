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
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Fill;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Line;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableVertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Part;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Primitive;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.PrimitiveType;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Solid;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Vertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.ZBuffer;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.view.Panel;
import java.awt.GridBagLayout;
import java.util.Objects;
import javax.swing.JComponent;
import javax.swing.JPanel;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Transformation;

/**
 * Class representing renderer of the scene
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Renderer extends MutableAdapter
{
    /**
     * Size of rendered axis
     */
    private static final int AXIS_SIZE = 100;
    
    /**
     * Fill of X axis
     */
    private static final Fill X_FILL = new Fill(new Col(0xff0000));
    
    /**
     * Fill of Y axis
     */
    private static final Fill Y_FILL = new Fill(new Col(0x00ff00));
    
    /**
     * Fill of Z axis
     */
    private static final Fill Z_FILL = new Fill(new Col(0x0000ff));
    
    /**
     * Enumeration of all available rendering types
     */
    public enum RenderType
    {
        /**
         * Render just wire frame
         */
        WIREFRAME,
        
        /**
         * Normal rendering
         */
        NORMAL
    }
    
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
     * Dehomogeniser of vertices
     */
    private final Dehomogeniser dehomogeniser;
    
    /**
     * Scene which will be rendered
     */
    private Scene scene;
    
    /**
     * Raster which handles final drawing
     */
    private ZBuffer raster;
    
    /**
     * Vertex buffer which will be rendered
     */
    private Vertex[] vertexBuffer;
    
    /**
     * Index buffer holding pointers to vertex buffer
     */
    private int[] indexBuffer;
    
    /**
     * Buffer of parts which will be rendered
     */
    private Scene.PartBufferItem[] partBuffer;
    
    /**
     * Object which can perform object clipping
     */
    private final Clipper clipper;
    
    /**
     * Object which can perform projection into 2D space
     */
    private final Projector projector;
    
    /**
     * Object which can perform transformation into window
     */
    private Viewport viewport;
    
    /**
     * Object which can rasterize all object in scene
     */
    private final Rasterizer rasterizer;
    
    /**
     * Type of rendering output
     */
    private RenderType renderType;
    
    /**
     * Flag, whether axis should be visible or not
     */
    private boolean axisVisible;
    
    /**
     * Creates new renderer
     */
    public Renderer()
    {
        this.wrapper = new JPanel();
        this.wrapper.setLayout(new GridBagLayout());
        this.wrapper.setOpaque(false);
        this.dehomogeniser = new Dehomogeniser();
        this.clipper = new Clipper();
        this.projector = new Projector();
        this.renderType = RenderType.NORMAL;
        this.viewport = new Viewport(0, 0);
        this.rasterizer = new Rasterizer();
        this.axisVisible = false;
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
        this.dehomogeniser = new Dehomogeniser();
        this.clipper = new Clipper();
        this.projector = new Projector();
        this.renderType = RenderType.NORMAL;
        this.viewport = new Viewport(cs.getWidth(), cs.getHeight());
        this.rasterizer = new Rasterizer();
        this.registerVertexChangers();
        this.axisVisible = false;
    }

    
    /**
     * Initializes output
     */
    private void initOutput()
    {
        this.wrapper.removeAll();
        this.output = new Panel(this.camSpace.getWidth(), this.camSpace.getHeight());        
        this.raster = new ZBuffer(this.output.getRaster());
        this.viewport = new Viewport(this.camSpace.getWidth(), this.camSpace.getHeight());
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
        this.registerVertexChangers();
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
     * Registers change listeners to vertices
     */
    private void registerVertexChangers()
    {
        if(Objects.nonNull(this.scene))
        {
            for(Solid solid: this.scene.getSolids())
            {
                for (Part part: solid.getParts())
                {
                    for (Primitive primitive: part.getPrimitives())
                    {
                        for (Vertex vertex: primitive.getVertices())
                        {
                            if (vertex instanceof MutableVertex)
                            {
                                MutableVertex mutableVertex = (MutableVertex)vertex;
                                mutableVertex.addChangeCallback(() -> {
                                    Renderer.this.run();
                                });
                            }
                        }
                        primitive.getFill().addChangeCallback(() -> {
                            Renderer.this.run();
                        });
                    }
                }
                for (Transformation transformation: solid.getTransformations())
                {
                    transformation.addChangeCallback(() -> {
                        Renderer.this.run();
                    });
                }
            }
            this.scene.getCamera().addChangeCallback(() -> {
                Renderer.this.run();
            });
            this.addChangeCallback(() -> {
                Renderer.this.run();
            });
        }
    }
        
    /**
     * Replaces vertex in vertex buffer
     * @param v Vertex which will be replaced
     * @param r Replacement
     */
    private void replaceVertex(Vertex v, Vertex r)
    {
        for (int i = 0; i < this.vertexBuffer.length; i++)
        {
            Vertex vf = this.vertexBuffer[i];
            if (vf.getX() == v.getX() && vf.getY() == v.getY() && vf.getZ() == v.getZ())
            {
                this.vertexBuffer[i] = r;
                break;
            }
        }
    }
    
    /**
     * Runs renderer through whole rendering process
     */
    public void run()
    {
        this.raster.clear();
        if (Objects.nonNull(this.scene))
        {
            if (this.axisVisible == true)
            {
                this.scene.setAxis(this.generateAxis());
                this.scene.showAxis();
            }
            else
            {
                this.scene.hideAxis();
            }
            // Follows steps of rendering pipeline
            this.scene.generateBuffers();                                                    // 0) Initialization
            this.indexBuffer = this.scene.getIndexBuffer();                                  //    (generating buffers)
            this.partBuffer = this.scene.getPartBuffer();                                    //
            this.vertexBuffer = this.scene.getVertexBuffer();                                //
                                                                                             //
            for (Solid solid: this.scene.getSolids())                                        // 1) Apply transformations
            {                                                                                //    in model space
                for(Transformation transformation: solid.getTransformations())               //
                {                                                                            //
                    for(Part part: solid.getParts())                                         //
                    {                                                                        //
                        for(Primitive primitive: part.getPrimitives())                       //
                        {                                                                    //
                            for (Vertex vertex: primitive.getVertices())                     //
                            {                                                                //
                                this.replaceVertex(vertex, transformation.apply(vertex));    //
                            }                                                                //
                        }                                                                    //
                    }                                                                        //
                }                                                                            //
            }                                                                                //
            this.vertexBuffer = this.worldSpace.apply(this.vertexBuffer);                    // 2) Transformation to world space
            this.vertexBuffer = this.camSpace.apply(this.vertexBuffer);                      // 3) Transformation to camera space
            this.clipper.setInputs(this.vertexBuffer, this.indexBuffer, this.partBuffer);    // 4) Clipping 
            this.clipper.run();                                                              //    (strict but simple)
            this.vertexBuffer = this.clipper.getVertexBuffer();                              // 
            this.indexBuffer = this.clipper.getIndexBuffer();                                //
            this.partBuffer = this.clipper.getPartBuffer();                                  //
            this.vertexBuffer = this.dehomogeniser.apply(this.vertexBuffer);                 // 5) Dehomogenisation
            this.vertexBuffer = this.projector.apply(this.vertexBuffer);                     // 6) Projection into 2D space
            this.vertexBuffer = this.viewport.apply(this.vertexBuffer);                      // 7) Transformation into window
            this.rasterizer.setRaster(this.raster);                                          // 8) Rasterization
            this.rasterizer.setRenderType(this.renderType);                                  //
            this.rasterizer.setInputs(this.vertexBuffer, this.indexBuffer, this.partBuffer); //
            this.rasterizer.rasterize();                                                     //
            this.redraw();
        }
    }

    /**
     * Generates solid representing axis
     */
    private Solid generateAxis()
    {
        Part part = new Part(PrimitiveType.LINE);
        Line x = new Line();
        x.addVertex(new MutableVertex(0, 0, 0));
        x.addVertex(new MutableVertex(Renderer.AXIS_SIZE, 0, 0));
        x.setFill(Renderer.X_FILL);
        Line y = new Line();
        y.addVertex(new MutableVertex(0, 0, 0));
        y.addVertex(new MutableVertex(0, Renderer.AXIS_SIZE, 0));
        y.setFill(Renderer.Y_FILL);
        Line z = new Line();
        z.addVertex(new MutableVertex(0, 0, 0));
        z.addVertex(new MutableVertex(0, 0, Renderer.AXIS_SIZE));
        z.setFill(Renderer.Z_FILL);
        part.addPrimitive(x);
        part.addPrimitive(y);
        part.addPrimitive(z);
        Solid reti = new Solid();
        reti.addPart(part);
        return reti;
    }
    
    /**
     * Gets type of rendering output
     * @return Type of rendering output
     */
    public RenderType getRenderType()
    {
        return this.renderType;
    }
    
    /**
     * Sets type of rendering output
     * @param type New type of rendering output
     */
    public void setRenderType(RenderType type)
    {
        this.renderType = type;
        this.informChange();
    }
    
    /**
     * Shows axis
     */
    public void showAxis()
    {
        this.axisVisible = true;
    }
    
    /**
     * Hides axis
     */
    public void hideAxis()
    {
        this.axisVisible = false;
    }
    
    @Override
    public void setEnum(String property, String value)
    {
        if (property.trim().toLowerCase().equals("typ"))
        {
            switch(value.trim().toLowerCase())
            {
                case "drátový model": this.setRenderType(RenderType.WIREFRAME); break;
                case "normální": this.setRenderType(RenderType.NORMAL); break;
            }
        }
    }

    @Override
    public String getEnumValue(String enumName)
    {
        String reti = super.getEnumValue(enumName);
        if (enumName.trim().toLowerCase().equals("typ"))
        {
            switch(this.renderType)
            {
                case WIREFRAME: reti = "Drátový model"; break;
                case NORMAL: reti = "Normální"; break;
            }
        }
        return reti;
    }

    @Override
    public String[] getAllowedValues(String enumName)
    {
        String[] reti = super.getAllowedValues(enumName);
        if (enumName.trim().toLowerCase().equals("typ"))
        {
            reti = new String[]{"Normální", "Drátový model"};
        }
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = super.getType(property);
        if (property.trim().toLowerCase().equals("typ"))
        {
            reti = Enum.class;
        }
        return reti;
    }

    @Override
    public String[] getProperties()
    {
        return new String[]{"Typ"};
    }
    
    
}
