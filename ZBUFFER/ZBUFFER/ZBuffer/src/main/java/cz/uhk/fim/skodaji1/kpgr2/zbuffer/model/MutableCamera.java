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
     * Moves camera forward
     * @param speed Speed of camera movement
     */
    public void forward(double speed)
    {
        this.updateCamera();
        this.camera.forward(speed);
        this.move();
        
    }
    
    /**
     * Moves camera backward
     * @param speed Speed of camera movement
     */
    public void backward(double speed)
    {
        this.updateCamera();
        this.camera.backward(speed);
        this.move();
    }
    
    /**
     * Moves camera left
     * @param speed Speed of camera movement
     */
    public void left(double speed)
    {
        this.updateCamera();
        this.camera.left(speed);
        this.move();
    }
    
    /**
     * Moves camera right
     * @param speed Speed of camera movement
     */
    public void right(double speed)
    {
        this.updateCamera();
        this.camera.right(speed);
        this.move();
    }
    
    /**
     * Moves camera
     */
    private void move()
    {
        this.x = this.camera.getPosition().x;
        this.y = this.camera.getPosition().y;
        this.z = this.camera.getPosition().z;
        this.updateCamera();
        this.informChange();
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
     * Sets azimuth of camera
     * @param azimuth New azimuth of camera
     */
    public void setAzimuth(double azimuth)
    {
        this.azimuth = azimuth;
        this.updateCamera();
        this.informChange();
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
     * Sets zenith of camera
     * @param zenith New zenith of camera
     */
    public void setZenith(double zenith)
    {
        this.zenith = zenith;
        this.updateCamera();
        this.informChange();
    }
    
    /**
     * Gets X coordinate of camera
     * @return X coordinate of camera
     */
    public double getX()
    {
        return this.x;
    }
    
    /**
     * Sets X coordinate of camera
     * @param x New X coordinate of camera
     */
    public void setX(double x)
    {
        this.x = x;
        this.updateCamera();
        this.informChange();
    }
    
    /**
     * Gets Y coordinate of camera
     * @return Y coordinate of camera
     */
    public double getY()
    {
        return this.y;
    }
    
    /**
     * Sets Y coordinate of camera
     * @param y New Y coordinate of camera
     */
    public void setY(double y)
    {
        this.y = y;
        this.updateCamera();
        this.informChange();
    }
    
    /**
     * Get Z coordinate of camera
     * @return Z coordinate of camera
     */
    public double getZ()
    {
        return this.z;
    }
    
    /**
     * Sets Z coordinate of camera
     * @param z New Z coordinate of camera
     */
    public void setZ(double z)
    {
        this.z = z;
        this.updateCamera();
        this.informChange();
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
            case "x": this.setX(value); break;
            case "y": this.setY(value); break;
            case "z": this.setZ(value); break;
            case "azimut": this.setAzimuth(value); break;
            case "zenit": this.setZenith(value); break;
        }
    }

}
