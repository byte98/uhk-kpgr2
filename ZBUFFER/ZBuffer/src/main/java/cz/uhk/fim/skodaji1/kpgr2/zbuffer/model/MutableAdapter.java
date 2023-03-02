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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.model;

import cz.uhk.fim.kpgr2.transforms.Col;
import java.awt.Color;
import java.util.Map;

/**
 * Class which represents default implementation of mutable object
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class MutableAdapter implements Mutable
{
    @Override
    public String[] getProperties()
    {
        return new String[0];
    }

    @Override
    public boolean isMutable(String property)
    {
        return false;
    }

    @Override
    public Class getType(String property)
    {
        return String.class;
    }

    @Override
    public String getString(String property)
    {
        return "";
    }

    @Override
    public double getDouble(String property)
    {
        return Double.NaN;
    }

    @Override
    public int getInt(String property)
    {
        return Integer.MIN_VALUE;
    }

    @Override
    public Col getColour(String property)
    {
        return new Col(Color.WHITE.getRGB());
    }

    @Override public void set(String property, String value) {}
    @Override public void set(String property, double value) {}
    @Override public void set(String property, int value)    {}
    @Override public void set(String property, Col value)    {}
    
}
