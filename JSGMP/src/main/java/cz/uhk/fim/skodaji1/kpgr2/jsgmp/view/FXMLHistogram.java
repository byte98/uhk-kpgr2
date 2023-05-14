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
package cz.uhk.fim.skodaji1.kpgr2.jsgmp.view;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * FXML Controller class of histogram document
 *
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class FXMLHistogram implements Initializable {

    @FXML
    private ImageView imageViewRed;
    @FXML
    private ImageView imageViewGreen;
    @FXML
    private ImageView imageViewBlue;

    /**
     * Histogram which data will be displayed
     */
    private Histogram histogram;
    
    @FXML
    private VBox vBoxContent;
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.vBoxContent.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) ->
        {
            FXMLHistogram.this.imageViewRed.setFitWidth(FXMLHistogram.this.vBoxContent.getWidth() - 10);
            FXMLHistogram.this.imageViewGreen.setFitWidth(FXMLHistogram.this.vBoxContent.getWidth() - 10);
            FXMLHistogram.this.imageViewBlue.setFitWidth(FXMLHistogram.this.vBoxContent.getWidth() - 10);
        });
    }
    
    /**
     * Sets histogram which data will be displayed
     * @param histogram Histogram which data will be displayed
     */
    public void setHistogram(Histogram histogram)
    {
        this.histogram = histogram;
        this.imageViewRed.setImage(this.histogram.getRed());
        this.imageViewGreen.setImage(this.histogram.getGreen());
        this.imageViewBlue.setImage(this.histogram.getBlue());
    }
}