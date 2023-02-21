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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Mutable;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * Class representing node in tree view
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class TreeNode extends DefaultMutableTreeNode
{
    /**
     * Icon of node
     */
    private final Icon icon;
    
    /**
     * Text of node
     */
    private final String text;
    
    /**
     * Object to which node references to
     */
    private final Mutable object;
    
    public TreeNode(Icon icon, String text, Mutable object)
    {
        this.icon = icon;
        this.text = text;
        this.object = object;
    }

    /**
     * Gets icon of node
     * @return Icon of node
     */
    public Icon getIcon()
    {
        return icon;
    }

    /**
     * Gets text of node
     * @return Text of node
     */
    public String getText()
    {
        return text;
    }

    /**
     * Gets object of node
     * @return Object to which node references
     */
    public Mutable getObject()
    {
        return object;
    }

    @Override
    public String toString()
    {
        return this.text;
    }
}
