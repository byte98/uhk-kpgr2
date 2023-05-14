package cz.uhk.fim.skodaji1.kpgr2.jsgmp;

import cz.uhk.fim.skodaji1.kpgr2.jsgmp.view.FXMLMainWindow;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.stage.WindowEvent;
import jfxtras.styles.jmetro.JMetro;
import jfxtras.styles.jmetro.Style;

/**
 * Main class of whole program
 */
public class JSGMP extends Application {

    /**
     * Main scene of whole program
     */
    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(JSGMP.class.getResource("fxml/FXMLMainWindow.fxml"));
        scene = new Scene(fxmlLoader.load());
        FXMLMainWindow controller = (FXMLMainWindow)fxmlLoader.getController();
        controller.setPrimaryStage(stage);
        stage.setScene(scene);
        JMetro jmetro = new JMetro(scene, Style.DARK);
        stage.show();
        stage.setOnCloseRequest((WindowEvent t) -> {
            Platform.exit();
        });
    }

    public static void main(String[] args) {
        JSGMP.launch(args);
    }

}