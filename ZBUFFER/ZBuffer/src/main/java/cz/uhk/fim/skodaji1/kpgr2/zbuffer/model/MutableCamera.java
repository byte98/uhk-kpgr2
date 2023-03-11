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

import cz.uhk.fim.kpgr2.transforms.Camera;
import cz.uhk.fim.kpgr2.transforms.Point3D;
import cz.uhk.fim.kpgr2.transforms.Vec3D;

/**
 * Class representing camera with mutable properties
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class MutableCamera extends MutableAdapter
{
    /**
     * Position of camera on X axis
     */
    private double x;
    
    /**
     * Position of camera on Y axis
     */
    private double y;
    
    /**
     * Position of camera on Z axis
     */    
    private double z;
    
    /**
     * Azimuth of camera
     */
    private double azimuth;
    
    /**
     * Zenith of camera
     */
    private double zenith;
    
    /**
     * Camera used for rendering
     */
    private final Camera camera;
    
    /**
     * Creates new mutable camera
     * @param x Position of camera on X axis
     * @param y Position of camera on Y axis
     * @param z Position of camera on Z axis
     * @param azimuth Azimuth of camera
     * @param zenith Zenith of camera
     */
    public MutableCamera(double x, double y, double z, double azimuth, double zenith)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.azimuth = azimuth;
        this.zenith = zenith;
        this.camera = new Camera();
        this.updateCamera();
    }

    /**
     * Generates camera object
     * @return Camera object
     */
    public Camera toCamera()
    {
        return this.camera;
    }
    
    /**
     * Updates camera used for rendering
     */
    private void updateCamera()
    {
        this.camera.setAzimuth(this.azimuth);
        this.camera.setZenith(this.zenith);
        this.camera.setPosition(new Vec3D(this.x, this.y, this.z));
    }
    
    /**
     * Gets azimuth of camera
     * @return Azimuth of camera
     */
    public double getAzimuth()
    {
        return this.azimuth;
    }
    
    /**
     * Gets zenith of camera
     * @return Zenith of camera
     */
    public double getZenith()
    {
        return this.zenith;
    }
    
    /**
     * Gets position of camera
     * @return Position of camera
     */
    public Point3D getPosition()
    {
        return new Point3D(this.x, this.y, this.z);
    }
    
    @Override
    public String[] getProperties()
    {
        return new String[]{"X", "Y", "Z", "Azimut", "Zenit"};
    }

    @Override
    public boolean isMutable(String property)
    {
        return true;
    }

    @Override
    public Class getType(String property)
    {
        return Double.class;
    }

    @Override
    public double getDouble(String property)
    {
        double reti = Double.NaN;
        switch(property.toLowerCase().trim())
        {
            case "x": reti = this.x; break;
            case "y": reti = this.y; break;
            case "z": reti = this.z; break;
            case "azimut": reti = this.azimuth; break;
            case "zenit": reti = this.zenith; break;
            default: reti = Double.NaN; break;
        }
        return reti;        
    }

    @Override
    public void set(String property, double value)
    {
        switch (property.toLowerCase().trim())
        {
            case "x": this.x = value; break;
            case "y": this.y = value; break;
            case "z": this.z = value; break;
            case "azimut": this.azimuth = value; break;
            case "zenit": this.zenith = value; break;
        }
        this.updateCamera();
        this.informChange();
    }

}
