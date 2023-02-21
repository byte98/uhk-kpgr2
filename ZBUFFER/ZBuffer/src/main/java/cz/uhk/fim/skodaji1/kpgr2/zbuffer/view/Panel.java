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

import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.ImageBuffer;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;


/**
 * Class representing panel which can view image buffer
 * Copied from GitLab
 * @author Jakub Benes <jakub.benes@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/view/Panel.java>
 */
public class Panel extends JPanel
{
       public static final int WIDTH = 1366, HEIGHT = 768;

    private ImageBuffer raster;

    public Panel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        raster = new ImageBuffer(WIDTH, HEIGHT);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        raster.repaint(g);
    }

    public void resize() {
        if (this.getWidth() < 1 || this.getHeight() < 1)
            return;

        ImageBuffer newRaster = new ImageBuffer(this.getWidth(), this.getHeight());
        newRaster.draw(raster);
        raster = newRaster;
    }

    public ImageBuffer getRaster()
    {
        return raster;
    }

    public void clear()
    {
        raster.clear();
    }
}
