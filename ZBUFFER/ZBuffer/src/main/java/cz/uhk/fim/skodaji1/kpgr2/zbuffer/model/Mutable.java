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
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.controller.ObjectChangeCallback;

/**
 * Interface abstracting mutable objects
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public interface Mutable
{
    /**
     * Gets array of all properties
     * @return Array with all properties
     */
    public abstract String[] getProperties();
    
    /**
     * Checks, whether property is mutable
     * @param property Property which will be checked
     * @return TRUE if property is mutable, FALSE otherwise
     */
    public abstract boolean isMutable(String property);
    
    /**
     * Gets data type of property
     * @param property Name of property
     * @return Data type of property
     */
    public abstract Class getType(String property);
    
    /**
     * Gets string value of property
     * @param property Name of property
     * @return String value of property
     */
    public abstract String getString(String property);
    
    /**
     * Gets double value of property
     * @param property Name of property
     * @return Double value of property
     */
    public abstract double getDouble(String property);
    
    /**
     * Gets integer value of property
     * @param property Name of property
     * @return Integer value of property
     */
    public abstract int getInt(String property);
    
    /**
     * Gets colour value of property
     * @param property Name of property
     * @return Colour value of property
     */
    public abstract Col getColour(String property);
    
    /**
     * Gets all allowed value for enumeration data type
     * @param enumName Name of property which has enumeration data type
     * @return Array with allowed values
     */
    public abstract String[] getAllowedValues(String enumName);
    
    /**
     * Gets value of enumeration
     * @param enumName Name of enumeration
     * @return Value of enumeration
     */
    public abstract String getEnumValue(String enumName);
    
    /**
     * Sets value to property
     * @param property Name of property
     * @param value New value of property
     */
    public abstract void set(String property, String value);
    
    /**
     * Sets value to property
     * @param property Name of property
     * @param value New value of property
     */
    public abstract void set(String property, double value);
    
    /**
     * Sets value to property
     * @param property Name of property
     * @param value New value of property
     */
    public abstract void set(String property, int value);
    
    /**
     * Sets value to property
     * @param property Name of property
     * @param value New value of property
     */
    public abstract void set(String property, Col value);
    
    /**
     * Sets value for enumeration data type
     * @param property Name of property which has enumeration data type
     * @param value Value of enumeration
     */
    public abstract void setEnum(String property, String value);
    
    /**
     * Adds function which will be called when mutable object has changed
     * @param callback Callback which will be called when object has changed
     */
    public abstract void addChangeCallback(ObjectChangeCallback callback);
}
