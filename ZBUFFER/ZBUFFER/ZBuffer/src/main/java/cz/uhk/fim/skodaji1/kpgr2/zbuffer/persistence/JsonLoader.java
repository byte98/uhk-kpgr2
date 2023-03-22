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
package cz.uhk.fim.skodaji1.kpgr2.zbuffer.persistence;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import cz.uhk.fim.kpgr2.transforms.Col;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Bicubics;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Fill;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Line;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableCamera;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.MutableVertex;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Part;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Primitive;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.PrimitiveType;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Scene;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Solid;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.Triangle;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Rotation;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Scale;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Translation;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.render.CameraSpace;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.render.Renderer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import cz.uhk.fim.skodaji1.kpgr2.zbuffer.model.transformations.Transformation;

/**
 * Class representing loader of data from JSON file
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class JsonLoader
{
    /**
     * Path to file from which data will be loaded
     */
    private final String file;
        
    /**
     * Result of loading scene from JSON file
     */
    private Scene result;
    
    /**
     * Renderer loaded from JSON file
     */
    private Renderer renderer;
    
    /**
     * Creates new loader of data from JSON file
     * @param file Path to file
     */
    public JsonLoader(String file)
    {
        this.file = file;
    }
    
    /**
     * Gets result of loader from scene
     * @return Scene loaded from JSON file
     */
    public Scene getResult()
    {
        return this.result;
    }
    
    /**
     * Gets renderer loaded from JSON
     * @return Renderer loaded from JSON file
     */
    public Renderer getRenderer()
    {
        return this.renderer;
    }
    
    /**
     * Loads data from file
     */
    public void load()
    {
        try
        {
            final JsonParser parser = new JsonParser();
            String content = Files.readString(Paths.get(this.file));
            JsonElement root = parser.parse(content);
            JsonObject details = root.getAsJsonObject();
            this.result = new Scene(details.get("name").getAsString());
            this.result.setCamera(this.loadCamera(details.get("camera")));
            this.renderer = this.loadRenderer(details.get("render"));
            JsonElement solids = details.get("solids");
            JsonArray solidsArray = solids.getAsJsonArray();
            for(JsonElement solid: solidsArray)
            {
                this.result.addSolid(this.loadSolid(solid));
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(JsonLoader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Loads renderer from JSON element
     * @param elem Element containing information about rendering process
     * @return Renderer loaded from JSON
     */
    private Renderer loadRenderer(JsonElement elem)
    {
        Renderer reti = new Renderer();
        JsonObject rendererObj = elem.getAsJsonObject();
        reti.setCameraSpace(this.loadCamSpace(rendererObj.get("camspace")));
        return reti;
    }
    
    /**
     * Loads camera space from JSON element
     * @param elem Element containing information about camera space
     * @return Camera space loaded from JSON
     */
    private CameraSpace loadCamSpace(JsonElement elem)
    {
        JsonObject csObj = elem.getAsJsonObject();
        double zf = csObj.get("zf").getAsDouble();
        double zn = csObj.get("zn").getAsDouble();
        int w     = csObj.get("width").getAsInt();
        int h     = csObj.get("height").getAsInt();
        CameraSpace reti = new CameraSpace(zn, zf, w, h, CameraSpace.CamSpaceType.fromString(csObj.get("type").getAsString()));
        reti.setAlpha(csObj.get("alpha").getAsDouble());
        return reti;
    }
    
    /**
     * Loads camera from JSON element
     * @param elem Element containing information about camera
     * @return Camera loaded from JSON
     */
    private MutableCamera loadCamera(JsonElement elem)
    {
        JsonObject camObj = elem.getAsJsonObject();
        double x = camObj.get("x").getAsDouble();
        double y = camObj.get("y").getAsDouble();
        double z = camObj.get("z").getAsDouble();
        double azimuth = camObj.get("azimuth").getAsDouble();
        double zenith  = camObj.get("zenith").getAsDouble();
        MutableCamera reti = new MutableCamera(x, y, z, azimuth, zenith);
        return reti;
    }
    
    /**
     * Loads solid from JSON element
     * @param elem Element containing information about solid
     * @return Solid loaded from JSON
     */
    private Solid loadSolid(JsonElement elem)
    {
        JsonObject solidObj = elem.getAsJsonObject();
        Solid reti = new Solid(solidObj.get("name").getAsString());
        JsonArray parts = solidObj.get("parts").getAsJsonArray();
        for(JsonElement part: parts)
        {
            reti.addPart(this.loadPart(part));
        }
        JsonArray transformations = solidObj.get("transformations").getAsJsonArray();
        for(JsonElement transformation: transformations)
        {
            reti.addTransformation(this.loadTransformation(transformation));
        }
        return reti;
    }
    
    /**
     * Loads transformation from JSON element
     * @param elem Element containing information about transformation
     * @return Transformation loaded from JSON
     */
    private Transformation loadTransformation(JsonElement elem)
    {
        Transformation reti = null;
        JsonObject transObj = elem.getAsJsonObject();
        String transType = transObj.get("type").getAsString().trim().toLowerCase();
        switch (transType)
        {
            case "rotation":    reti = this.loadRotation(elem);    break;
            case "translation": reti = this.loadTranslation(elem); break;
            case "scale":       reti = this.loadScale(elem);       break;
        }
        return reti;
        
    }
    
    /**
     * Loads rotation from JSON element
     * @param elem Element containing information about rotation
     * @return Rotation loaded from JSON
     */
    private Rotation loadRotation(JsonElement elem)
    {
        JsonObject rotObj = elem.getAsJsonObject();
        Rotation reti = new Rotation(rotObj.get("name").getAsString());
        reti.setAlpha(rotObj.get("alpha").getAsDouble());
        reti.setBeta(rotObj.get("beta").getAsDouble());
        reti.setGamma(rotObj.get("gamma").getAsDouble());
        return reti;
    }
    
    /**
     * Loads scale from JSON element
     * @param elem Element containing information about scale
     * @return Transformation loaded from JSON
     */
    private Transformation loadScale(JsonElement elem)
    {
        JsonObject scaleObj = elem.getAsJsonObject();
        Scale reti = new Scale(scaleObj.get("name").getAsString());
        reti.setX(scaleObj.get("x").getAsDouble());
        reti.setY(scaleObj.get("y").getAsDouble());
        reti.setZ(scaleObj.get("z").getAsDouble());
        return reti;
    }
    
    /**
     * Loads translation from JSON element
     * @param elem Element containing information about translation
     * @return Translation loaded from JSON
     */
    private Translation loadTranslation(JsonElement elem)
    {
        JsonObject transObj = elem.getAsJsonObject();
        Translation reti = new Translation(transObj.get("name").getAsString());
        reti.setX(transObj.get("x").getAsDouble());
        reti.setY(transObj.get("y").getAsDouble());
        reti.setZ(transObj.get("z").getAsDouble());
        return reti;
    }
    
    /**
     * Loads part from JSON element
     * @param elem Element containing information about part
     * @return Part loaded from JSON
     */
    private Part loadPart(JsonElement elem)
    {
        JsonObject partObj = elem.getAsJsonObject();
        PrimitiveType type = PrimitiveType.fromString(partObj.get("type").getAsString());
        Part reti = new Part(type, partObj.get("name").getAsString());
        JsonArray primitivesArr = partObj.get("primitives").getAsJsonArray();
        for(JsonElement primitive: primitivesArr)
        {
            reti.addPrimitive(this.loadPrimitive(type, primitive));
        }
        return reti;
    }
    
    /**
     * Loads primitive from JSON element
     * @param type Type of primitive which will be loaded
     * @param elem Element containing information about primitive
     * @return Primitive loaded from JSON
     */
    private Primitive loadPrimitive(PrimitiveType type, JsonElement elem)
    {
        Primitive reti = null;
        if (type == PrimitiveType.TRIANGLE)
        {
            reti = this.loadTriangle(elem);
        }
        else if (type == PrimitiveType.LINE)
        {
            reti = this.loadLine(elem);
        }
        else if (type == PrimitiveType.BICUBICS)
        {
            reti = this.loadBicubics(elem);
        }
        Fill fill = this.loadFill(elem.getAsJsonObject().get("fill"));
        reti.setFill(fill);
        return reti;
    }
    
    /**
     * Loads bicubics from JSON element
     * @param elem Element containing information about bicubics
     * @return Bicubics loaded from JSON
     */
    private Bicubics loadBicubics(JsonElement elem)
    {
        JsonObject biObj = elem.getAsJsonObject();
        Bicubics.BicubicsType bType = Bicubics.BicubicsType.fromString(biObj.get("type").getAsString());
        Bicubics reti = new Bicubics(
                Bicubics.BicubicsType.fromString(biObj.get("type").getAsString()),
                biObj.get("precision").getAsInt(),
                biObj.get("name").getAsString()
        );
        for(JsonElement vertex: biObj.get("vertices").getAsJsonArray())
        {
            reti.addVertex(this.loadVertex(vertex, reti));
        }
        return reti;
    }
    
    /**
     * Loads line from JSON element
     * @param elem Element containing information about line
     * @return Line loaded from JSON
     */
    private Line loadLine(JsonElement elem)
    {
        JsonObject lineObj = elem.getAsJsonObject();
        Line reti = new Line(lineObj.get("name").getAsString());
        for(JsonElement vertex: lineObj.get("vertices").getAsJsonArray())
        {
            reti.addVertex(this.loadVertex(vertex, reti));
        }
        return reti;
    }
    
    /**
     * Loads fill from JSON element
     * @param elem Element containing information about fill
     * @return Fill loaded from JSON
     */
    private Fill loadFill(JsonElement elem)
    {
        JsonObject fillObj = elem.getAsJsonObject();
        String fillType = fillObj.get("type").getAsString().trim().toLowerCase();
        Fill reti = null;
        if (fillType.equals("colour"))
        {
            reti = new Fill(new Col(Integer.parseInt(fillObj.get("value").getAsString(), 16)));
        }
        return reti;
    }
    
    /**
     * Loads triangle from JSON element
     * @param elem Element containing information about triangle
     * @return Triangle loaded from JSON
     */
    private Triangle loadTriangle(JsonElement elem)
    {
        JsonObject triangleObj = elem.getAsJsonObject();
        Triangle reti = new Triangle(triangleObj.get("name").getAsString());
        for(JsonElement vertex: triangleObj.get("vertices").getAsJsonArray())
        {
            reti.addVertex(this.loadVertex(vertex, reti));
        }
        return reti;
    }
    
    /**
     * Loads vertex from JSON element
     * @param elem Element containing information about vertex
     * @param primitive Primitive to which vertex belongs to
     * @return Vertex loaded from JSON
     */
    private MutableVertex loadVertex(JsonElement elem, Primitive primitive)
    {
        JsonObject vObj = elem.getAsJsonObject();
        MutableVertex reti = new MutableVertex(
                vObj.get("name").getAsString(),
                vObj.get("x").getAsDouble(),
                vObj.get("y").getAsDouble(),
                vObj.get("z").getAsDouble(),
                primitive
        );
        return reti;
    }
}
