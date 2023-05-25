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
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.concurrency.ThreadManager;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.controller.MainController;
import cz.uhk.fim.skodaji1.kpgr2.jsgmp.effects.BrightnessContrast;
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
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.robot.Robot;
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
    private ImageView imageCheckGrayscale;

    @FXML
    private void menuGrayscaleOnAction(ActionEvent event) {
    }

    /**
     * Class which performs loading tool from FXML file
     * @param T Type of tool (data type of controller of tool)
     */
    private static class ToolLoader<T extends FXMLController>
    {
        /**
         * Controller of tool
         */
        private T controller;
        
        /**
         * Content of tool
         */
        private Pane content;
        
        /**
         * Creates new loader of tool
         * @param fileName Name of file which contains definition of tool
         * @param tab Tab to which tool will be loaded
         * @param controller Reference to main controller
         */
        public ToolLoader(String fileName, Tab tab, MainController controller)
        {
            FXMLLoader loader = new FXMLLoader(JSGMP.class.getResource("fxml/" + fileName));
            try
            {
                this.content = (Pane)loader.load();
                this.controller = (T)loader.getController();
                this.controller.setMainController(controller);
                this.controller.resetValue();
                this.content.prefWidthProperty().bind(tab.getTabPane().widthProperty());
                this.content.prefHeightProperty().bind(tab.getTabPane().heightProperty());
                this.content.getStyleClass().add(JMetroStyleClass.BACKGROUND);
                tab.setContent(this.content);
            }
            catch (IOException ex)
            {
                Logger.getLogger(FXMLMainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        /**
         * Gets content of tool
         * @return Pane with content of tool
         */
        public Pane getContent()
        {
            return this.content;
        }
        
        /**
         * Gets controller of tool
         * @return Controller of tool
         */
        public T getController()
        {
            return this.controller;
        }
    }
    
    /**
     * Last position of mouse on X axis
     */
    private double lastX;
    
    /**
     * Last position of mouse on Y axis
     */
    private double lastY;
    
    /**
     * Primary stage of application
     */
    private Stage primaryStage;
    
    /**
     * Controller of behaviour of main window
     */
    private MainController controller;
    
    
    /**
     * Pane with content of histogram tool
     */
    private Pane histogramPane;
    
    /**
     * Controller of histogram tool
     */
    private FXMLHistogram histogramController;
    
    /**
     * Pane with content of brightness tool
     */
    private Pane brightnessPane;
    
    /**
     * Controller of brightness tool
     */
    private FXMLBrightness brightnessController;
    
    /**
     * Pane with content of contrast tool
     */
    private Pane contrastPane;
    
    /**
     * Controller of contrast tool
     */
    private FXMLContrast contrastController;
    
    /**
     * Pane with content of temperature tool
     */
    private Pane temperaturePane;
    
    /**
     * Controller of temperature tool
     */
    private FXMLTemperature temperatureController;
    
    /**
     * Controller of blue tool
     */
    private FXMLBlue blueController;
    
    /**
     * Content of blue tool
     */
    private Pane bluePane;
    
    /**
     * Controller of red tool
     */
    private FXMLRed redController;
    
    /**
     * Content of red tool
     */
    private Pane redPane;
    
    /**
     * Controller of green tool
     */
    private FXMLGreen greenController;
    
    /**
     * Content of green tool
     */
    private Pane greenPane;
    
    /**
     * Controller of cyan tool
     */
    private FXMLCyan cyanController;
    
    /**
     * Content of cyan tool
     */
    private Pane cyanPane;
    
    /**
     * Controller of yellow tool
     */
    private FXMLYellow yellowController;
    
    /**
     * Content of yellow tool
     */
    private Pane yellowPane;
    
    /**
     * Controller of magenta tool
     */
    private FXMLMagenta magentaController;
    
    /**
     * Content of magenta tool
     */
    private Pane magentaPane;
    
    
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
    @FXML
    private ImageView imageCheckBrightness;
    @FXML
    private Tab tabBrightness;
    @FXML
    private ImageView imageCheckContrast;
    @FXML
    private Tab tabContrast;
    @FXML
    private ImageView imageCheckTemperature;
    @FXML
    private Tab tabTemperature;
    @FXML
    private ImageView imageCheckZoom;
    @FXML
    private Tab tabZoom;
    @FXML
    private ImageView imageViewZoom;
    @FXML
    private Slider sliderZoom;
    @FXML
    private Label labelZoomValue;
    @FXML
    private AnchorPane tabZoomContent;
    
    /**
     * Handler of zoom of image
     */
    private Zoom zoom;
    @FXML
    private Tab tabInfo;
    @FXML
    private Tab tabRed;
    @FXML
    private Tab tabBlue;
    @FXML
    private Tab tabGreen;
    @FXML
    private Tab tabCyan;
    @FXML
    private Tab tabMagenta;
    @FXML
    private Tab tabYellow;
    @FXML
    private MenuBar menuBarTopMenu;
    @FXML
    private MenuItem menuItemOpen;
    @FXML
    private ImageView imageCheckRed;
    @FXML
    private ImageView imageCheckGreen;
    @FXML
    private ImageView imageCheckBlue;
    @FXML
    private ImageView imageCheckCyan;
    @FXML
    private ImageView imageCheckMagenta;
    @FXML
    private ImageView imageCheckYellow;
    
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
        this.controller = new MainController(this);        
        this.initializeTabs();
        this.scrollPaneMainImage.widthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.scrollPaneMainImage.heightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.imageViewMain.fitHeightProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.imageViewMain.fitWidthProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            FXMLMainWindow.this.relocateMainImage(FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getWidth(), FXMLMainWindow.this.scrollPaneMainImage.getViewportBounds().getHeight());
            FXMLMainWindow.this.resizeMainImageWrapper(this.imageViewMain.getFitWidth(), this.imageViewMain.getFitHeight());
        });
        this.hideAllTabs();
        this.sliderZoom.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            this.labelZoomValue.setText(String.format("%.0f %%", ((double)Math.round((Double)t1 * 100f) / 100f)));            
        });
        this.controller.mainWindowLoaded();
    }    
    
    
    /**
     * Initializes all tabs from FXML documents
     */
    private void initializeTabs()
    {
        FXMLMainWindow.ToolLoader<FXMLHistogram> toolHistogram = new FXMLMainWindow.ToolLoader<>("FXMLHistogram.fxml", this.tabHistogram, this.controller);
        this.histogramController = toolHistogram.getController();
        this.histogramPane = toolHistogram.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLBrightness> toolBrightness = new FXMLMainWindow.ToolLoader<>("FXMLBrightness.fxml", this.tabBrightness, this.controller);
        this.brightnessController = toolBrightness.getController();
        this.brightnessPane = toolBrightness.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLContrast> toolContrast = new FXMLMainWindow.ToolLoader<>("FXMLContrast.fxml", this.tabContrast, this.controller);
        this.contrastController = toolContrast.getController();
        this.contrastPane = toolContrast.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLTemperature> toolTemperature = new FXMLMainWindow.ToolLoader<>("FXMLTemperature.fxml", this.tabTemperature, this.controller);
        this.temperatureController = toolTemperature.getController();
        this.temperaturePane = toolTemperature.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLBlue> toolBlue = new FXMLMainWindow.ToolLoader<>("FXMLBlue.fxml", this.tabBlue, this.controller);
        this.blueController = toolBlue.getController();
        this.bluePane = toolBlue.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLGreen> toolGreen = new FXMLMainWindow.ToolLoader<>("FXMLGreen.fxml", this.tabGreen, this.controller);
        this.greenController = toolGreen.getController();
        this.greenPane = toolGreen.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLRed> toolRed = new FXMLMainWindow.ToolLoader<>("FXMLRed.fxml", this.tabRed, this.controller);
        this.redController = toolRed.getController();
        this.redPane = toolRed.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLMagenta> toolMagenta = new FXMLMainWindow.ToolLoader<>("FXMLMagenta.fxml", this.tabMagenta, this.controller);
        this.magentaController = toolMagenta.getController();
        this.magentaPane = toolMagenta.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLYellow> toolYellow = new FXMLMainWindow.ToolLoader<>("FXMLYellow.fxml", this.tabYellow, this.controller);
        this.yellowController = toolYellow.getController();
        this.yellowPane = toolYellow.getContent();
        
        FXMLMainWindow.ToolLoader<FXMLCyan> toolCyan = new FXMLMainWindow.ToolLoader<>("FXMLCyan.fxml", this.tabCyan, this.controller);
        this.cyanController = toolCyan.getController();
        this.cyanPane = toolCyan.getContent();
        
        this.initializeZoom();
    }
    
    /**
     * Initializes zoom zool
     */
    private void initializeZoom()
    {
        this.tabZoom.getContent().getStyleClass().add(JMetroStyleClass.BACKGROUND);
    }
        
    /**
     * Hides all tabs which should not be visible
     */
    private void hideAllTabs()
    {
        this.tabPaneTools.getTabs().clear();
        this.tabPaneTools.getTabs().add(this.tabInfo);
    }
    
    /**
     * Moves image into center
     */
    public void centerImage()
    {
        this.relocateMainImage(this.scrollPaneMainImage.getWidth(), this.scrollPaneMainImage.getHeight());
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
     * Resets all set values
     */
    public void resetValues()
    {
        this.brightnessController.resetValue();
        this.contrastController.resetValue();
        this.temperatureController.resetValue();
        this.redController.resetValue();
        this.greenController.resetValue();
        this.blueController.resetValue();
        this.cyanController.resetValue();
        this.magentaController.resetValue();
        this.yellowController.resetValue();
    }
    
    /**
     * Disables all menu items except open file
     * @param disable Flag, whether items should be disabled (TRUE) or not (FALSE)
     */
    public void diableMenu(boolean disable)
    {
        for(Menu m: this.menuBarTopMenu.getMenus())
        {
            for(MenuItem mI: m.getItems())
            {
                mI.setDisable(disable);
            }
        }
        this.menuItemOpen.setDisable(false);
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
     * Sets handler of image zoom
     * @param zoom Handler of image zoom
     */
    public void setZoom(Zoom zoom)
    {
        this.zoom = zoom;
        this.zoom.setImageView(this.imageViewMain);
        this.zoom.setScrollPane(this.scrollPaneMainImage);
        this.imageViewZoom.setImage(this.zoom.getDiagram());
        this.sliderZoom.valueProperty().addListener((ObservableValue<? extends Number> ov, Number t, Number t1) -> {
            int value = (int)Math.round((Double)t1);
            this.zoom.setZoomLevel(value);
        });
        this.zoom.setZoomLevel(101);
        this.zoom.setZoomLevel(100);
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
        if (width == 0 && height == 0)
        {
            this.labelFileSize.setText("(žádný soubor)");
        }
        else
        {
            this.labelFileSize.setText(String.format("%d px × %d px", width, height));
        }
    }
    
    /**
     * Sets histogram of red color channel
     * @param image Image representation of histogram of red color channel
     */
    public void setRedHistogram(Image image)
    {
        this.histogramController.setRedHistogram(image);
        this.redController.setHistogram(image);
    }
    
    /**
     * Sets histogram of green color channel
     * @param image Image representation of histogram of green color channel
     */
    public void setGreenHistogram(Image image)
    {
        this.histogramController.setGreenHistogram(image);
        this.greenController.setHistogram(image);
    }
    
    /**
     * Sets histogram of blue color channel
     * @param image Image representation of histogram of blue color channel
     */
    public void setBlueHistogram(Image image)
    {
        this.histogramController.setBlueHistogram(image);
        this.blueController.setHistogram(image);
    }
    
    /**
     * Sets histogram of cyan color channel
     * @param image Image representation of histogram of cyan color channel
     */
    public void setCyanHistogram(Image image)
    {
        this.histogramController.setCyanHistogram(image);
        this.cyanController.setHistogram(image);
    }
    
    /**
     * Sets histogram of magenta color channel
     * @param image Image representation of histogram of magenta color channel
     */
    public void setMagentaHistogram(Image image)
    {
        this.histogramController.setMagentaHistogram(image);
        this.magentaController.setHistogram(image);
    }
    
    /**
     * Sets histogram of yellow color channel
     * @param image Image representation of histogram of yellow color channel
     */
    public void setYellowHistogram(Image image)
    {
        this.histogramController.setYellowHistogram(image);
        this.yellowController.setHistogram(image);
    }
    
    /**
     * Sets histogram of brightness of image
     * @param brightnessHistogram New histogram of brightness of image
     */
    public void setBrightnessHistogram(Image brightnessHistogram)
    {
        this.brightnessController.setBrightnessHistogram(brightnessHistogram);
    }

    /**
     * Sets histogram of contrast of image
     * @param contrastHistogram New histogram of contrast of image
     */
    public void setContrastHistogram(Image contrastHistogram)
    {
        this.contrastController.setHistogram(contrastHistogram);
    }
    
    /**
     * Sets chart of brightness/contrast curve
     * @param brightnessContrastChart Image with chart of brightness/contrast
     */
    public void setBrightnessContrastChart(Image brightnessContrastChart)
    {
        this.brightnessController.setChart(brightnessContrastChart);
        this.contrastController.setChart(brightnessContrastChart);
    }
    
    /**
     * Sets histogram of temperature
     * @param temperatureHistogram Image containing histogram of temperature
     */
    public void setTemperatureHistogram(Image temperatureHistogram)
    {
        this.temperatureController.setHistogram(temperatureHistogram);
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
        this.popupOnAction("Histogram", this.histogramPane, this.tabHistogram, this.imageCheckHistogram);
    }

    @FXML
    private void histogramCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckHistogram, this.tabHistogram);
    }

    @FXML
    private void menuBrightnessOnAction(ActionEvent event) {
        if (this.imageCheckBrightness.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabBrightness);
            this.imageCheckBrightness.setVisible(true);
        }
    }

    /**
     * Handles pop up action of tab
     * @param title Title of tool
     * @param content Content of tool
     * @param tab Tab with tool
     * @param imageCheck Image showing whether tool is opened or not
     */
    private void popupOnAction(String title, Pane content, Tab tab, ImageView imageCheck)
    {
        tab.setContent(null);
        Scene scene = new Scene(content);
        Stage stage = new Stage();
        stage.setTitle(title);
        stage.setScene(scene);
        this.tabPaneTools.getTabs().remove(tab);
        stage.setOnCloseRequest(evt -> {
            imageCheck.setVisible(false);
            tab.setContent(content);
            stage.close();
        });
        stage.setWidth(this.primaryStage.getWidth() * (2f / 5f));
        stage.setHeight(this.primaryStage.getHeight());
        stage.iconifiedProperty().addListener(new ChangeListener<Boolean>(){
            @Override
            public void changed(ObservableValue<? extends Boolean> ov, Boolean t, Boolean t1)
            {
                if (stage.isIconified())
                {
                    stage.close();
                    tab.setContent(content);
                    FXMLMainWindow.this.tabPaneTools.getTabs().add(tab);
                    FXMLMainWindow.this.tabPaneTools.getSelectionModel().select(tab);
                }
            }        
        });
        JMetro jmetro = new JMetro(scene, Style.DARK);
        scene.getStylesheets().add(JSGMP.class.getResource("fxml/CSSTabs.css").toExternalForm());
        stage.show();
    }
    
    @FXML
    private void brightnessPopupOnAction(ActionEvent event) {
        this.popupOnAction("Jas", this.brightnessPane, this.tabBrightness, this.imageCheckBrightness);
    }

    @FXML
    private void brightnessCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckBrightness, this.tabBrightness);
    }

    @FXML
    private void menuContrastOnAction(ActionEvent event) {
        if (this.imageCheckContrast.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabContrast);
            this.imageCheckContrast.setVisible(true);
        }
    }

    @FXML
    private void contrastPopupOnAction(ActionEvent event) {
       this.popupOnAction("Kontrast", this.contrastPane, this.tabContrast, this.imageCheckContrast);
    }

    @FXML
    private void contrastCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckContrast, this.tabContrast);
    }

    @FXML
    private void menuTemperatureOnAction(ActionEvent event) {
        if (this.imageCheckTemperature.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabTemperature);
            this.imageCheckTemperature.setVisible(true);
        }
    }

    @FXML
    private void temperaturePopupOnAction(ActionEvent event) {
        this.popupOnAction("Teplota", this.temperaturePane, this.tabTemperature, this.imageCheckTemperature);
    }

    @FXML
    private void temperatureCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckTemperature, this.tabTemperature);
    }

    @FXML
    private void menuZoomOnAction(ActionEvent event) {
        if (this.imageCheckZoom.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabZoom);
            this.imageCheckZoom.setVisible(true);
        }
    }

    @FXML
    private void menuDiscardOnAction(ActionEvent event)
    {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        
        alert.setTitle("Zahození všech změn");
        alert.setHeaderText("Zahodit všechny změny");
        alert.setContentText("Opravdu chcete zahodit všechny změny?\nTato akce je nevratná.");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK)
        {
            this.resetValues();
        }
    }

    @FXML
    private void zoomPopupOnAction(ActionEvent event) {
        this.popupOnAction("Lupa", this.tabZoomContent, this.tabZoom, this.imageCheckZoom);
    }

    @FXML
    private void zoomCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckZoom, this.tabZoom);
    }

    @FXML
    private void buttomZoomRefreshOnAction(ActionEvent event) {
        this.sliderZoom.setValue(100f);
    }

    @FXML
    private void imageViewMainOnMouseReleased(MouseEvent event) {
        this.imageViewMain.setCursor(Cursor.OPEN_HAND);
    }

    @FXML
    private void imageViewMainOnMouseDragged(MouseEvent event) {
        
        double stepX = (this.scrollPaneMainImage.getHmax() - this.scrollPaneMainImage.getHmin()) / this.imageViewMain.getFitWidth();
        double stepY = (this.scrollPaneMainImage.getVmax() - this.scrollPaneMainImage.getVmin()) / this.imageViewMain.getFitHeight();
        
        double dX = event.getX() - this.lastX;
        double dY = event.getY() - this.lastY;
        
        double targetX = this.scrollPaneMainImage.getHvalue() - (dX * stepX);
        if (targetX > this.scrollPaneMainImage.getHmax()) targetX = this.scrollPaneMainImage.getHmax(); if (targetX < this.scrollPaneMainImage.getHmin()) targetX = this.scrollPaneMainImage.getHmin();

        double targetY = this.scrollPaneMainImage.getVvalue() - (dY * stepY);
        if (targetY > this.scrollPaneMainImage.getVmax()) targetY = this.scrollPaneMainImage.getVmax(); if (targetY < this.scrollPaneMainImage.getVmin()) targetY = this.scrollPaneMainImage.getVmin();
        
        this.scrollPaneMainImage.setHvalue(targetX);
        this.scrollPaneMainImage.setVvalue(targetY);
        
        this.lastX = event.getX();
        this.lastY = event.getY();
    }

    @FXML
    private void imageViewMainOnMousePressed(MouseEvent event) {
        this.imageViewMain.setCursor(Cursor.CLOSED_HAND);
        this.lastX = event.getX();
        this.lastY = event.getY();
    }

    @FXML
    private void menuRedOnAction(ActionEvent event) {
        if (this.imageCheckRed.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabRed);
            this.imageCheckRed.setVisible(true);
        }
    }

    @FXML
    private void menuGreenOnAction(ActionEvent event) {
        if (this.imageCheckGreen.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabGreen);
            this.imageCheckGreen.setVisible(true);
        }
    }

    @FXML
    private void menuBlueOnAction(ActionEvent event) {
        if (this.imageCheckBlue.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabBlue);
            this.imageCheckBlue.setVisible(true);
        }
    }

    @FXML
    private void menuCyanOnAction(ActionEvent event) {
        if (this.imageCheckCyan.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabCyan);
            this.imageCheckCyan.setVisible(true);
        }
    }

    @FXML
    private void menuMagentaOnAction(ActionEvent event) {
        if (this.imageCheckMagenta.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabMagenta);
            this.imageCheckMagenta.setVisible(true);
        }
    }

    @FXML
    private void menuYellowOnAction(ActionEvent event) {
        if (this.imageCheckYellow.isVisible() == false)
        {
            this.tabPaneTools.getTabs().add(this.tabYellow);
            this.imageCheckYellow.setVisible(true);
        }
    }

    @FXML
    private void redPopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - červená", this.redPane, this.tabRed, this.imageCheckRed);
    }

    @FXML
    private void redCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckRed, this.tabRed);
    }

    @FXML
    private void bluePopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - modrá", this.bluePane, this.tabBlue, this.imageCheckBlue);
    }

    @FXML
    private void blueCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckBlue, this.tabBlue);
    }

    @FXML
    private void greenPopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - zelená", this.greenPane, this.tabGreen, this.imageCheckGreen);
    }

    @FXML
    private void greenCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckGreen, this.tabGreen);
    }

    @FXML
    private void cyanPopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - azurová", this.cyanPane, this.tabCyan, this.imageCheckCyan);
    }

    @FXML
    private void cyanCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckCyan, this.tabCyan);
    }

    @FXML
    private void magentaPopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - purpurová", this.magentaPane, this.tabMagenta, this.imageCheckMagenta);
    }

    @FXML
    private void magentaCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckMagenta, this.tabMagenta);
    }

    @FXML
    private void yellowPopupOnAction(ActionEvent event) {
        this.popupOnAction("Barevnost - žlutá", this.yellowPane, this.tabYellow, this.imageCheckYellow);
    }

    @FXML
    private void yellowCloseOnAction(ActionEvent event) {
        this.closeTab(this.imageCheckYellow, this.tabYellow);
    }
}
