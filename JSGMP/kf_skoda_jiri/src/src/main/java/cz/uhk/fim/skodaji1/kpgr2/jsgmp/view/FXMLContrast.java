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

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller.MainController;
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
 * FXML Controller class of contrast tool
 *
 * @author Jiri Skoda <jiri.skoda@uhk.cz>
 */
public class FXMLContrast extends FXMLController implements Initializable {

    @FXML
    private VBox vBoxContent;
    @FXML
    private ImageView imageViewContrast;
    @FXML
    private ImageView imageViewChart;
        @FXML
    private Slider sliderContrast;
    @FXML
    private Label labelValue;
    
    @Override
    public void resetValue()
    {
        this.sliderContrast.setValue(1.0f);
    }    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.vBoxContent.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            FXMLContrast.this.imageViewContrast.setFitWidth(FXMLContrast.this.vBoxContent.getWidth() - 10);
        });
        this.sliderContrast.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            double prev = (Double)t;
            double next = (Double)t1;
            double delta = next - prev;
            this.labelValue.setText(String.format("%.2f", ((double)Math.round((Double)t1 * 100f) / 100f)));
            if (delta != 0)
            {
                this.mainController.contrastChanged(next);
            }            
        });
    }    
    
    /**
     * Sets histogram of contrast
     * @param image Image containing histogram of contrast
     */
    public void setHistogram(Image image)
    {
        this.imageViewContrast.setImage(image);
    }
    
    /**
     * Sets chart of brightness/contrast
     * @param chart Image containing chart of brightness/contrast
     */
    public void setChart(Image chart)
    {
        this.imageViewChart.setImage(chart);
    }

    @FXML
    private void buttonRefreshOnAction(ActionEvent event) {
        this.resetValue();
    }
}
