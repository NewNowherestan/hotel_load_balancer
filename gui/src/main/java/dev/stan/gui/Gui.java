package dev.stan.gui;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JavaFX App
 */
public class Gui extends Application {
    private static final Logger logger = LoggerFactory.getLogger(Gui.class);
    private static Scene scene;
    private static StatusController controller;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        new Thread(Application::launch).start();
    }


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = loadFXML("status");
        scene = new Scene(loader.getRoot(), 640, 480);
        stage.setScene(scene);
        stage.show();

        controller = loader.getController();

        // Add event handler for window close request
        stage.setOnCloseRequest((WindowEvent event) -> {
            Platform.exit();
            System.exit(0);
        });
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml).getRoot());
    }

    public static void printLog(String message) {
        if (controller != null) {
            controller.appendLogMessage(message);
        }
    }

    public static void plotIO(List<String[]> io) {
        if (controller != null) {
            controller.updateIOView(io);
        }
    }

    public static void updateSysParams(List<String[]> statusParams) {
        if (controller != null) {
            controller.updateSysParams(statusParams);

        }

    }

    private static FXMLLoader loadFXML(String fxml) throws IOException {
        FXMLLoader loader = new FXMLLoader(Gui.class.getResource("fxml/" + fxml + ".fxml"));
        Parent res = loader.load();
        logger.info("Loading FXML: " + loader.getLocation());
        return loader;
    }
}