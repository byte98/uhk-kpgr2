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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import java.util.Objects;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Class which handles zooming
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class Zoom
{
    /**
     * Source of data
     */
    private final Bitmap source;
    
    /**
     * Scrollable pane which contains image
     */
    private ScrollPane scrollPane = null;
    
    /**
     * View of image
     */
    private ImageView imageView = null;
    
    /**
     * Diagram of actual level of zoom
     */
    private ZoomDiagram diagram = null;

    /**
     * Default height of image
     */
    private double defaultHeight;
    
    /**
     * Default width of image
     */
    private double defaultWidth;
    
    /**
     * Actual level of zoom (in percents)
     */
    private int level;
    
    /**
     * Creates new handler of zooming
     * @param source Source of image data
     */
    public Zoom(Bitmap source)
    {
        this.source = source;
        this.level = 100;
        this.source.addChangeActionListener(new Bitmap.BitmapChangedActionListener() {
            @Override
            public void onChange(Bitmap bitmap){
                if (Objects.nonNull(Zoom.this.diagram))
                {
                    Zoom.this.diagram.refresh();
                }                
            }
        });
    }
    
    /**
     * Sets actual zoom level
     * @param level Actual level of zoom (in percents)
     */
    public void setZoomLevel(int level)
    {
        this.level = level;
        double newWidth = this.defaultWidth * ((double)this.level / 100f);
        double newHeight = this.defaultHeight * ((double)this.level / 100f);
        this.imageView.setFitWidth(newWidth);
        this.imageView.setFitHeight(newHeight);
    }
    
    /**
     * Sets pane which contains image
     * @param sp Scrollable pane which contains image
     */
    public void setScrollPane(ScrollPane sp)
    {
        this.scrollPane = sp;
        this.generateDiagram();
    }
    
    /**
     * Sets view of image
     * @param iv View of image
     */
    public void setImageView(ImageView iv)
    {
        this.imageView = iv;
        this.defaultHeight = this.imageView.getFitHeight();
        this.defaultWidth = this.imageView.getFitWidth();
        this.generateDiagram();
    }
    
    /**
     * Generates diagram of zoom
     */
    private void generateDiagram()
    {
        if (Objects.nonNull(this.imageView) && Objects.nonNull(this.scrollPane))
        {
            this.diagram = ThreadManager.createZoomDiagram(
                    this.scrollPane.widthProperty(),
                    this.scrollPane.heightProperty(),
                    this.imageView.fitWidthProperty(),
                    this.imageView.fitHeightProperty(),
                    this.scrollPane.vvalueProperty(),
                    this.scrollPane.hvalueProperty(),
                    this.source
            );
            this.diagram.refresh();
        }
    }
    
    /**
     * Stops diagram of zoom
     */
    public void stop()
    {
        ThreadManager.stopOne(this.diagram);
        this.diagram = null;
    }
    
    /**
     * Gets diagram of actual level of zoom
     * @return Image containing diagram of actual level of zoom
     */
    public Image getDiagram()
    {
        Image reti = null;
        if (Objects.nonNull(this.diagram))
        {
            reti = this.diagram.toImage();
        }
        else
        {
            throw new IllegalStateException("Cannot get image representation of zoom diagram: either image view or scroll pane is not set!");
        }
        return reti;
    }
    
}
