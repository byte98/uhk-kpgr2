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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JLabel;

/**
 * Class representing viewer of icon
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class IconView extends JLabel
{
    /**
     * Icon which will be displayed
     */
    private final Icon icon;
    
    /**
     * Creates new viewer of icon
     * @param icon Icon which will be displayed
     */
    public IconView(Icon icon)
    {
        super(icon.toImageIcon());
        this.icon = icon;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        
        Graphics2D g2 = (Graphics2D)g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
        );
        g2.setRenderingHints(rh);
        g2.drawImage(
                this.icon.toImageIcon().getImage(),
                (this.getWidth() / 2) - (this.icon.toImageIcon().getIconWidth() / 2),
                (this.getHeight() / 2) - (this.icon.toImageIcon().getIconHeight() / 2),
                null
        );
        super.paintComponent(g);
    }
}
