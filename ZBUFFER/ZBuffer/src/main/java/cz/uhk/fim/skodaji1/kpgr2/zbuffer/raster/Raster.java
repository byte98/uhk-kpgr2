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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster;

/**
 * Interface abstracting all raster objects
 * Copied and added functionality from GitLab
 * @author Jakub Benes <jakub.benes@uhk.cz> & Jiri Skoda <jiri.skoda@uhk.cz>
 * @see <https://gitlab.com/jakub.benes/kpgr2-2023/-/blob/4d3a740571a20436a6f56369662e51058f199c0c/src/raster/Raster.java>
 */
public interface Raster<E>
{
    /**
     * Clears whole raster
     */
    public abstract void clear();

    /**
     * Sets element which indicates clear pixel
     * @param element Element indicating clear pixel
     */
    void setClearElement(E element);

    /**
     * Gets width of raster
     * @return Width of raster
     */
    int getWidth();

    /**
     * Gets height of raster
     * @return Height of raster
     */
    int getHeight();

    /**
     * Gets element from raster
     * @param x Position on X axis
     * @param y Position on Y axis
     * @return Element from raster
     */
    E getElement(int x, int y);

    /**
     * Sets element in raster
     * @param x Position on X axis
     * @param y Position on Y axis
     * @param element Element which will be set on defined position
     */
    void setElement(int x, int y, E element);

    /**
     * Checks, whether position is inside of raster or not
     * @param x Position on X axis
     * @param y Position on Y axis
     * @return TRUE, if position is in raster, FALSE otherwise
     */
    default boolean isInside(int x, int y)
    {
        return (
                x >= 0 &&
                x < this.getWidth() &&
                y >= 0 &&
                y < this.getHeight()
                );
    }

}
