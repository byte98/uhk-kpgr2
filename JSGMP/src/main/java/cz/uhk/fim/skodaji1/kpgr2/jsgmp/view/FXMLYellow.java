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
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class
 *
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class FXMLYellow extends FXMLController implements Initializable {

    @FXML
    private VBox vBoxContent;
    @FXML
    private ImageView imageViewHistogram;
    @FXML
    private Slider sliderColor;
    @FXML
    private Label labelValue;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.vBoxContent.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            FXMLYellow.this.imageViewHistogram.setFitWidth(FXMLYellow.this.vBoxContent.getWidth() - 10);
        });
        this.sliderColor.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            int prev = (int)Math.round((Double)t);
            int next = (int)Math.round((Double)t1);
            this.labelValue.setText(((Double)t1 > 0 ? "+" : "") + String.format("%d", (int)Math.round((Double)t1)));
            int delta = next - prev;
            if (delta != 0)
            {
                this.mainController.yellowChanged(next);
            }            
        });
    }    

    @FXML
    private void buttonRefreshOnAction(ActionEvent event) {
        this.resetValue();
    }

    @Override
    public void resetValue() {
        this.sliderColor.setValue(0f);
    }
    
    /**
     * Sets histogram of color
     * @param histogram Image containing histogram of color
     */
    public void setHistogram(Image histogram)
    {
        this.imageViewHistogram.setImage(histogram);
    }
    
}
