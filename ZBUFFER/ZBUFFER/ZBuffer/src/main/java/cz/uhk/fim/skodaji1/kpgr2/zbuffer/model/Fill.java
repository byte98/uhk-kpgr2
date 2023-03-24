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
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.ColourPixelProvider;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster.PixelProvider;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing fill of vertices
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class Fill implements Cloneable, Mutable
{
    /**
     * List with all handlers of object change
     */
    private final List<ObjectChangeCallback> handlers = new ArrayList<>();
    
    /**
     * Type of fill
     */
    private FillType type;
        
    /**
     * Provider of pixels used to fill objects
     */
    private PixelProvider pixelProvider;
    
    /**
     * Creates new fill of vertex
     * @param colour Colour of vertex
     */
    public Fill(Col colour)
    {
        this.type = FillType.COLOR;
        this.pixelProvider = new ColourPixelProvider(colour);
    }
    
    /**
     * Creates new fill of vertex
     * @param type Type of fill
     * @param pixelProvider Provider of pixels fill
     */
    private Fill(FillType type, PixelProvider pixelProvider)
    {
        this.type = type;
        this.pixelProvider = pixelProvider;
    }
    
    /**
     * Gets type of fill
     * @return Type of fill
     */
    public FillType getType()
    {
        return this.type;
    }
    
    /**
     * Gets provider of pixels which represents fill
     * @return Provider of pixels
     */
    public PixelProvider getPixelProvider()
    {
        return this.pixelProvider;
    }
    
    /**
     * Sets fill type
     * @param type New fill type
     */
    private void setType(FillType type)
    {
        this.type = type;
        switch(this.type)
        {
            case COLOR: this.pixelProvider = new ColourPixelProvider(); break;
        }
        this.informChange();
    }
    
    /**
     * Clones this fill
     * @return Copy of this fill
     */
    public Fill clone()
    {
        return new Fill(this.type, this.pixelProvider);
    }

    @Override
    public String[] getProperties()
    {
        String[] ppProps = this.pixelProvider.getProperties();
        String[] reti = new String[ppProps.length + 1];
        reti[0] = "Typ výplně";
        for (int i = 0; i < ppProps.length; i++)
        {
            reti[i + 1] = ppProps[i];
        }
        return reti;
    }

    @Override
    public boolean isMutable(String property)
    {   
        boolean reti = this.pixelProvider.isMutable(property);
        if (property.toLowerCase().trim().equals("typ výplně"))
        {
            reti = true;
        }        
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = this.pixelProvider.getType(property);
        if (property.trim().toLowerCase().equals("typ výplně"))
        {
            reti = Enum.class;
        }
        return reti;
    }

    @Override
    public String getString(String property) 
    {
        return this.pixelProvider.getString(property);
    }

    @Override
    public double getDouble(String property)
    {
        return this.pixelProvider.getDouble(property);
    }

    @Override
    public int getInt(String property)
    {
        return this.pixelProvider.getInt(property);
    }

    @Override
    public Col getColour(String property)
    {
        return this.pixelProvider.getColour(property);
    }

    @Override
    public String[] getAllowedValues(String enumName)
    {
        String[] reti = this.pixelProvider.getAllowedValues(enumName);
        if (enumName.toLowerCase().trim().equals("typ výplně"))
        {
            reti = new String[]{"Barva"};
        }
        return reti;
    }

    @Override
    public String getEnumValue(String enumName)
    {
        String reti = this.pixelProvider.getEnumValue(enumName);
        if (enumName.toLowerCase().trim().equals("typ výplně"))
        {
            switch (this.type)
            {
                case COLOR: reti = "Barva"; break;
                
            }
        }
        return reti;
    }

    @Override
    public void set(String property, String value)
    {
        this.pixelProvider.set(property, value);
        this.informChange();
    }

    @Override
    public void set(String property, double value)
    {
        this.pixelProvider.set(property, value);
        this.informChange();
    }

    @Override
    public void set(String property, int value)
    {
        this.pixelProvider.set(property, value);
        this.informChange();
    }

    @Override
    public void set(String property, Col value)
    {
        this.pixelProvider.set(property, value);
        this.informChange();
    }

    @Override
    public void setEnum(String property, String value)
    {
        if (property.toLowerCase().trim().equals("typ výplně"))
        {
            
        }
        else
        {
            this.pixelProvider.set(property, value);
        }        
        this.informChange();
    }

    @Override
    public void addChangeCallback(ObjectChangeCallback callback)
    {
        this.handlers.add(callback);
    }
    
    /**
     * Informs all handlers about change of object
     */
    private void informChange()
    {
        for (ObjectChangeCallback callback: this.handlers)
        {
            callback.objectChanged();
        }
    }
    
    
}
