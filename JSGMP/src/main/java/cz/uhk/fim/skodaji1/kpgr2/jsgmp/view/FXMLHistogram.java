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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TabPane;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class of histogram document
 *
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class FXMLHistogram extends FXMLController implements Initializable {

    @FXML
    private ImageView imageViewRed;
    @FXML
    private ImageView imageViewGreen;
    @FXML
    private ImageView imageViewBlue;
    @FXML
    private ImageView imageViewCyan;
    @FXML
    private ImageView imageViewMagenta;
    @FXML
    private ImageView imageViewYellow;
    @FXML
    private TabPane tabPaneContent;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.tabPaneContent.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            FXMLHistogram.this.imageViewRed.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
            FXMLHistogram.this.imageViewGreen.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
            FXMLHistogram.this.imageViewBlue.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
            FXMLHistogram.this.imageViewCyan.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
            FXMLHistogram.this.imageViewMagenta.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
            FXMLHistogram.this.imageViewYellow.setFitWidth(FXMLHistogram.this.tabPaneContent.getWidth() - 10);
        });
    }
    
    /**
     * Sets histogram of red color channel
     * @param image Image representation of histogram of red color channel
     */
    public void setRedHistogram(Image image)
    {
        this.imageViewRed.setImage(image);
    }
    
    /**
     * Sets histogram of green color channel
     * @param image Image representation of histogram of green color channel
     */
    public void setGreenHistogram(Image image)
    {
        this.imageViewGreen.setImage(image);
    }
    
    /**
     * Sets histogram of blue color channel
     * @param image Image representation of histogram of blue color channel
     */
    public void setBlueHistogram(Image image)
    {
        this.imageViewBlue.setImage(image);
    }
    
    /**
     * Sets histogram of cyan color channel
     * @param image Image representation of histogram of cyan color channel
     */
    public void setCyanHistogram(Image image)
    {
        this.imageViewCyan.setImage(image);
    }
    
    /**
     * Sets histogram of blue magenta channel
     * @param image Image representation of histogram of blue magenta channel
     */
    public void setMagentaHistogram(Image image)
    {
        this.imageViewMagenta.setImage(image);
    }
    
    /**
     * Sets histogram of yellow color channel
     * @param image Image representation of histogram of yellow color channel
     */
    public void setYellowHistogram(Image image)
    {
        this.imageViewYellow.setImage(image);
    }

    @Override
    public void resetValue()
    {
        // NOP
    }

}
