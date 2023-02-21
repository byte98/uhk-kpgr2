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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.Main;
import javax.swing.ImageIcon;

/**
 * Class handling all actions with icons
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Icon
{
    /**
     * Icon of detail
     */
    public static final Icon DETAIL = new Icon("detail.png");
    
    /**
     * Save icon
     */
    public static final Icon SAVE = new Icon("save.png");
    
    /**
     * Save as icon
     */
    public static final Icon SAVE_AS = new Icon("save-as.png");
    
    /**
     * Loads icon
     */
    public static final Icon LOAD = new Icon("load.png");
    
    /**
     * Icon of Z buffer
     */
    public static final Icon Z_BUFFER = new Icon("zbuffer.png");
    
    /**
     * Icon of panel
     */
    public static final Icon PANEL = new Icon("panel.png");
    
    /**
     * Icon of scene
     */
    public static final Icon SCENE = new Icon("scene.png");
    
    /**
     * Icon of tree hierarchy
     */
    public static final Icon TREE = new Icon("tree.png");
    
    /**
     * Icon of properties
     */
    public static final Icon PROPERTIES = new Icon("properties.png");
    
    /**
     * Icon of camera used in tree view
     */
    public static final Icon TREE_CAMERA = new Icon("tree_cam.png");
    
    /**
     * Icon of scene used in tree view
     */
    public static final Icon TREE_SCENE = new Icon("tree_scene.png");
    
    /**
     * Icon of solids used in tree view
     */
    public static final Icon TREE_SOLIDS = new Icon("tree_solids.png");
    
    /**
     * Icon of solid used in tree view
     */
    public static final Icon TREE_SOLID = new Icon("tree_solid.png");
    
    /**
     * Icon of part used in tree view
     */
    public static final Icon TREE_PART = new Icon("tree_part.png");
    
    /**
     * Icon of vertex used in tree view
     */
    public static final Icon TREE_VERTEX = new Icon("tree_vertex.png");
    
    /**
     * Icon of vertex with texture used in tree view
     */
    public static final Icon TREE_VERTEX_TEX = new Icon("tree_vertex_tex.png");
    
    /**
     * Icon of angle used in tree view
     */
    public static final Icon TREE_ANGLE = new Icon("tree_angle.png");
    
    /**
     * Icon of text used in tree view
     */
    public static final Icon TREE_TEXT = new Icon("tree_name.png");
    
    /**
     * Icon of X axis used in tree view
     */    
    public static final Icon TREE_X = new Icon("tree_x.png");
    
    /**
     * Icon of Y axis used in tree view
     */
    public static final Icon TREE_Y = new Icon("tree_y.png");
    
    /**
     * Icon of Z axis used in tree view
     */
    public static final Icon TREE_Z = new Icon("tree_z.png");
    
    /**
     * Icon of branch used in tree view
     */
    public static final Icon TREE_BRANCH = new Icon("tree_branch.png");
    
    /**
     * Icon of leaf used in tree view
     */
    public static final Icon TREE_LEAF = new Icon("tree_leaf.png");
    
    /**
     * Icon of parts used in tree view
     */
    public static final Icon TREE_PARTS = new Icon("tree_parts.png");
    
    /**
     * Icon of primitive used in tree view
     */
    public static final Icon TREE_PRIMITIVE = new Icon("tree_primitive.png");
    
    /**
     * Icon of type used in tree view
     */
    public static final Icon TREE_TYPE = new Icon("tree_type.png");
    
    /**
     * Name of file containing icon
     */
    private final String file;
    
    /**
     * Creates new icon
     * @param file Name of file containing icon
     */
    private Icon(String file)
    {
        this.file = file;
    }
    
    /**
     * Gets icon in image icon object
     * @return Image icon object containing defined icon
     */
    public ImageIcon toImageIcon()
    {
        return new ImageIcon(Main.class.getClassLoader().getResource(this.file));
    }
}
