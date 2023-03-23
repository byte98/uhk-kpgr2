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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.raster;

import cz.uhk.fim.kpgr2.transforms.Col;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableAdapter;
import java.awt.Color;

/**
 * Provider of pixels in some kind of pattern
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class PatternPixelProvider extends MutableAdapter implements PixelProvider
{
    /**
     * Default size of pattern
     */
    private static final int DEFAULT_SIZE = 4; 
    
    /**
     * Default pattern
     */
    private static final PatternType DEFAULT_PATTERN = PatternType.CHECKER;
    
    /**
     * First default colour of pattern
     */
    private static final Col DEFAULT_COLOUR_1 = new Col(Color.WHITE.getRGB());
    
    /**
     * Second default colour of pattern
     */
    private static final Col DEFAULT_COLOUR_2 = new Col(Color.WHITE.getRGB());
    
    /**
     * Enumeration of all available pattern types
     */
    public enum PatternType
    {
        /**
         * Checker pattern
         */
        CHECKER;
        
        /**
         * Gets pattern type from string
         * @param str String representation of pattern type
         * @return Pattern type based on its string representation
         */
        public static PatternType fromString(String str)
        {
            String strVal = str.toLowerCase().trim();
            PatternType reti = PatternPixelProvider.DEFAULT_PATTERN;
            switch(strVal)
            {
                case "checker": reti = PatternType.CHECKER; break;
            }
            return reti;
        }
    }
        
    /**
     * First colour of pattern
     */
    private Col colour1;    
    
    /**
     * Second colour of pattern
     */
    private Col colour2;
    
    /**
     * Type of pattern
     */
    private PatternType type;
    
    /**
     * Size of pattern
     */
    private int size;
    
    /**
     * Creates new pattern pixel provider
     */
    public PatternPixelProvider()
    {
        this(
                PatternPixelProvider.DEFAULT_PATTERN,
                PatternPixelProvider.DEFAULT_COLOUR_1,
                PatternPixelProvider.DEFAULT_COLOUR_2,
                PatternPixelProvider.DEFAULT_SIZE
        );
    }
    
    /**
     * Creates new pattern pixel provider
     * @param pattern Pattern of pixel provider
     * @param colour1 First colour of pattern
     * @param colour2 Second colour of pattern
     * @param size Size of pattern
     */
    public PatternPixelProvider(PatternType pattern, Col colour1, Col colour2, int size)
    {
        this.colour1 = colour1;
        this.colour2 = colour2;
        this.type = pattern;
        this.size = size;
    }

    //<editor-fold defaultstate="collapsed" desc="Getters & setters">
    /**
     * Gets first colour of pattern
     * @return First colour of pattern
     */
    public Col getColour1()
    {
        return colour1;
    }

    /**
     * Sets first colour of pattern
     * @param colour1 New first colour of pattern
     */
    public void setColour1(Col colour1)
    {
        this.colour1 = colour1;
        this.informChange();
    }

    /**
     * Gets second colour of pattern
     * @return Second colour of pattern
     */
    public Col getColour2()
    {
        return colour2;
    }

    /**
     * Sets second colour of pattern
     * @param colour2 New second colour of pattern
     */
    public void setColour2(Col colour2)
    {
        this.colour2 = colour2;
        this.informChange();
    }

    /**
     * Gets type of pattern
     * @return Type of pattern
     */
    public PatternType getType()
    {
        return type;
    }

    /**
     * Sets type of pattern
     * @param type New type of pattern
     */
    public void setType(PatternType type)
    {
        this.type = type;
        this.informChange();
    }

    /**
     * Gets size of pattern
     * @return Size of pattern
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Sets size of pattern
     * @param size New size of pattern
     */
    public void setSize(int size)
    {
        this.size = size;
        this.informChange();
    }
    //</editor-fold>
    
    
    @Override
    public Col getPixel(int x, int y)
    {
        Col reti = this.colour1;
        switch (this.type)
        {
            case CHECKER: reti = this.checkerPattern(x, y); break;
        }
        return reti;
    }
    
    /**
     * Creates checker pattern
     * @param x X coordinate of pixel
     * @param y Y coordinate of pixel
     * @return Colour of pixel
     */
    private Col checkerPattern(int x, int y)
    {
        Col reti = this.colour2;
        x = x % (2 * this.size);
        y = y % (2 * this.size);
        if ((x < this.size && y < this.size) || 
            (x > this.size && y > this.size))
        {
            reti = this.colour1;
        }
        return reti;
    }

    @Override
    public void setEnum(String property, String value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("druh vzorku"))
        {
            switch(value.toLowerCase().trim())
            {
                case "checker": this.setType(PatternType.CHECKER); break;
            }
        }
    }

    @Override
    public String getEnumValue(String enumName)
    {
        String propName = enumName.toLowerCase().trim();
        String reti = super.getEnumValue(enumName);
        if (propName.equals("druh vzroku"))
        {
            switch(this.getType())
            {
                case CHECKER: reti = "Šachovnice"; break;
            }
        }
        return reti;
    }

    @Override
    public String[] getAllowedValues(String enumName)
    {
        String[] reti = super.getAllowedValues(enumName);
        String propName = enumName.toLowerCase().trim();
        if(propName.equals("typ vzorku"))
        {
            reti = new String[]{"Šachovnice"};
        }
        return reti;
    }

    @Override
    public void set(String property, Col value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva 1"))
        {
            this.setColour1(value);
        }
        else if (propName.equals("barva 2"))
        {
            this.setColour2(value);
        }
        else
        {
            super.set(property, value);
        }
    }

    @Override
    public void set(String property, int value)
    {
        String propName = property.toLowerCase().trim();
        if (propName.equals("velikost"))
        {
            this.setSize(value);
        }
        else
        {
            super.set(property, value);
        }
    }

    @Override
    public Col getColour(String property)
    {
        Col reti = super.getColour(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva 1"))
        {
            reti = this.getColour1();
        }
        else if (propName.equals("barva 2"))
        {
            reti = this.getColour2();
        }
        return reti;
    }

    @Override
    public int getInt(String property)
    {
        int reti = super.getInt(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("velikost"))
        {
            reti = this.getSize();
        }
        return reti;
    }

    @Override
    public Class getType(String property)
    {
        Class reti = super.getType(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva 1"))
        {
            reti = Col.class;
        }
        else if (propName.equals("barva 2"))
        {
            reti = Col.class;
        }
        else if (propName.equals("typ vzorku"))
        {
            reti = Enum.class;
        }
        else if (propName.equals("velikost"))
        {
            reti = Integer.class;
        }
        return reti;
    }

    @Override
    public boolean isMutable(String property)
    {
        boolean reti = super.isMutable(property);
        String propName = property.toLowerCase().trim();
        if (propName.equals("barva 1") ||
            propName.equals("barva 2") ||
            propName.equals("typ vzorku") ||
            propName.equals("velikost"))
        {
            reti = true;
        }
        return reti;
    }

    @Override
    public String[] getProperties()
    {
        return new String[]{"Typ vzorku", "Barva 1", "Barva 2", "Velikost"};
    }
    
    
}
