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

import com.sun.javafx.PlatformUtil;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.JSGMP;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller.MainWindowController;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.Bitmap;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.model.ImageFile;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.JMetroStyleClass;
import jfxtras.styles.jmetro.Style;

/**
 * FXML Controller class of main window
 *
 * @author Jiri Skoda <jiri.skoda@student.upce.cz>
 */
public class FXMLMainWindow implements Initializable {

    @FXML
    private ImageView imageViewDetail;
    
    /**
     * Primary stage of application
     */
    private Stage primaryStage;
    
    /**
     * Controller of behaviour of main window
     */
    private MainWindowController controller;
    
    
    /**
     * Pane with content of histogram tool
     */
    private Pane histogramPane;
    
    /**
     * Controller of histogram tool
     */
    private FXMLHistogram histogramController;
    
    @FXML
    private ImageView imageViewMain;
    
    @FXML
    private Pane paneMainImageWrapper;
    @FXML
    private ScrollPane scrollPaneMainImage;
    @FXML
    private Label labelFileName;
    @FXML
    private Label labelFileSize;
    @FXML
    private Hyperlink hyperLinkFilePath;
    @FXML
    private Tab tabHistogram;
    @FXML
    private TabPane tabPaneTools;
    @FXML
    private ImageView imageCheckHistogram;

    /**
     * Sets reference to primary stage of program
     * @param stage Reference to primary stage of program
     */
    public void setPrimaryStage(Stage stage)
    {
        this.primaryStage = stage;
        this.primaryStage.maximizedProperty().addListener((ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        this.controller = new MainWindowController(this);
        this.scrollPaneMainImage.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.scrollPaneMainImage.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.initializeTabs();
        this.hideAllTabs();
    }    
    
    
    /**
     * Initializes all tabs from FXML documents
     */
    private void initializeTabs()
    {
        this.initializeHistogram();
    }
    
    /**
     * Initializes histogram
     */
    private void initializeHistogram()
    {
        FXMLLoader fxmlLoader = new FXMLLoader(JSGMP.class.getResource("fxml/FXMLHistogram.fxml"));
        try
        {
            this.histogramPane = (Pane)fxmlLoader.load();
            this.histogramController = (FXMLHistogram)fxmlLoader.getController();
            this.histogramPane.prefWidthProperty().bind(this.tabHistogram.getTabPane().widthProperty());
            this.histogramPane.prefHeightProperty().bind(this.tabHistogram.getTabPane().heightProperty());
            this.histogramPane.getStyleClass().add(JMetroStyleClass.BACKGROUND);
            this.tabHistogram.setContent(this.histogramPane);
        }
        catch (IOException ex)
        {
            Logger.getLogger(FXMLMainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Hides all tabs which should not be visible
     */
    private void hideAllTabs()
    {
        this.tabPaneTools.getTabs().remove(this.tabHistogram);
    }
    
    /**
     * Changes position of main image to be always in center
     * @param parentWidth Width of parent pane
     * @param parentHeight Height of parent pane
     */
    private void relocateMainImage(double parentWidth, double parentHeight)
    {
        double x = (parentWidth / 2f) - (this.imageViewMain.getFitWidth() / 2f);
        double y = (parentHeight / 2f) - (this.imageViewMain.getFitHeight() / 2f);
        if (this.imageViewMain.getFitHeight() > parentHeight)
        {
            y = 0;
        }
        if (this.imageViewMain.getFitWidth() > parentWidth)
        {
            x = 0;
        }
        this.imageViewMain.setLayoutX(x);
        this.imageViewMain.setLayoutY(y);
    }
    
    /**
     * Resizes pane which wraps main image
     * @param imageWidth Width of main image
     * @param imageHeight Height of main image
     */
    private void resizeMainImageWrapper(double imageWidth, double imageHeight)
    {
        this.paneMainImageWrapper.setPrefWidth(Math.max(this.scrollPaneMainImage.getViewportBounds().getWidth(), imageWidth));
        this.paneMainImageWrapper.setPrefHeight(Math.max(this.scrollPaneMainImage.getViewportBounds().getHeight(), imageHeight));
    }

    @FXML
    private void menuOpenOnAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Otevřít soubor");
        fc.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Všechny obrázky", new ArrayList<String>(){
                    {
                        add("*.bmp");
                        add("*.gif");
                        add("*.jpg");
                        add("*.jpeg");
                        add("*.png");
                    }  
                }),
                new FileChooser.ExtensionFilter("Obrázky BMP", "*.bmp"),
                new FileChooser.ExtensionFilter("Obrázky GIF", "*.gif"),
                new FileChooser.ExtensionFilter("Obrázky JPG", new ArrayList<String>(){
                    {
                        add("*.jpg");
                        add("*.jpeg");
                    }
                }),
                new FileChooser.ExtensionFilter("Obrázky PNG", "*.png")
        );
        File selectedFile = fc.showOpenDialog(this.primaryStage);
        if (Objects.nonNull(selectedFile) && selectedFile.exists())
        {
            this.controller.fileOpen(selectedFile.getAbsolutePath());
        }        
    }
    
    /**
     * Sets displayed image
     * @param bitmap Bitmap containing data of image
     */
    public void setImage(Bitmap bitmap)
    {
        this.imageViewMain.setImage(bitmap.toImage());
        this.imageViewMain.setFitWidth(bitmap.getWidth());
        this.imageViewMain.setFitHeight(bitmap.getHeight());
        this.resizeMainImageWrapper(bitmap.getWidth(), bitmap.getHeight());
        this.relocateMainImage(this.scrollPaneMainImage.getViewportBounds().getWidth(), this.scrollPaneMainImage.getViewportBounds().getHeight());
    }
    
    /**
     * Sets displayed name of file
     * @param name Name of file which will be displayed
     */
    public void setFileName(String name)
    {
        this.labelFileName.setText(name);
    }
    
    /**
     * Sets displayed path to file
     * @param path Path to file which will be displayed
     */
    public void setFilePath(String path)
    {
        this.hyperLinkFilePath.setText(path);
    }
    
    /**
     * Sets displayed size of file
     * @param width Width of image in file
     * @param height Height of image in file
     */
    public void setFileSize(int width, int height)
    {
        this.labelFileSize.setText(String.format("%d px × %d px", width, height));
    }
    
    /**
     * Sets histogram which will be displayed
     * @param histogram Histogram which will be displayed
     */
    public void setHistogram(Histogram histogram)
    {
        this.histogramController.setHistogram(histogram);
    }

    @FXML
    private void hyperLinkFilePathOnAction(ActionEvent event)
    {
        if (this.hyperLinkFilePath.getText().equals("(žádný soubor)") == false)
        {
            File f = new File(this.hyperLinkFilePath.getText());
            String dir = f.getParentFile().getAbsolutePath();
            String os = System.getProperty("os.name").toLowerCase();
            if (os.indexOf("win") >= 0)
            {
                try {
                    Runtime.getRuntime().exec("explorer /select, " + this.hyperLinkFilePath.getText());
                } catch (IOException ex) {
                    Logger.getLogger(FXMLMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("aix") > 0)
            {
                try {
                    Runtime.getRuntime().exec("xdg-open " + dir);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else if (os.indexOf("mac") >= 0)
            {
                try {
                    Runtime.getRuntime().exec("open -R " + dir);
                } catch (IOException ex) {
                    Logger.getLogger(FXMLMainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    /**
     * Closes tool tab
     * @param image Check image which will be hidden
     * @param tab Tab which will be closed
     */
    private void closeTab(ImageView image, Tab tab)
    {
        image.setVisible(false);
        this.tabPaneTools.getTabs().remove(tab);
    }
    
    /**
     * Pop ups tab in new window
     * @param tab Tab which will be opened in new pop up window
     * @param title Title of pop up window
     * @param image Check image which will be hidden when pop up window is closed
     */
    private void popupTab(Tab tab, String title, ImageView image)
    {        
        Stage stage = new Stage();        
        Parent parent = tab.getTabPane();        
        this.tabPaneTools.getTabs().remove(tab);
        Scene scene = new Scene(parent);
        JMetro jmetro = new JMetro(scene, Style.DARK);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle(title);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(event -> {
            image.setVisible(false);
        });
        stage.iconifiedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                if (stage.isIconified())
                {
                    stage.close();
                    FXMLMainWindow.this.tabPaneTools.getTabs().add(FXMLMainWindow.this.tabHistogram);
                }
            }        
        });
        
    }

    @FXML
    private void menuHistogramOnAction(ActionEvent event) {
        if (this.imageCheckHistogram.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabHistogram);
            this.imageCheckHistogram.setVisible(true);
        }
    }    

    @FXML
    private void histogramPopupOnAction(ActionEvent event) {
        this.tabHistogram.setContent(null);
        Scene scene = new Scene(this.histogramPane);
        Stage stage = new Stage();
        stage.setTitle("Histogram");
        stage.setScene(scene);
        this.tabPaneTools.getTabs().remove(this.tabHistogram);
        stage.setOnCloseRequest(evt -> {
            FXMLMainWindow.this.imageCheckHistogram.setVisible(false);
            FXMLMainWindow.this.tabHistogram.setContent(FXMLMainWindow.this.histogramPane);
            stage.close();
        });
        stage.iconifiedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                if (stage.isIconified())
                {
                    stage.close();
                    FXMLMainWindow.this.tabHistogram.setContent(FXMLMainWindow.this.histogramPane);
                    FXMLMainWindow.this.tabPaneTools.getTabs().add(FXMLMainWindow.this.tabHistogram);
                }
            }        
        });
        JMetro jmetro = new JMetro(scene, Style.DARK);
        stage.show();
    }

    @FXML
    private void histogramCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckHistogram, this.tabHistogram);
    }
}
