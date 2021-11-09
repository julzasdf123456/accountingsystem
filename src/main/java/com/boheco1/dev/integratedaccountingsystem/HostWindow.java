package com.boheco1.dev.integratedaccountingsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class HostWindow extends Application {

    private static Scene scene;

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("login_controller.fxml"));
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight()-60);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        stage.setTitle("Integrated Accounting System");
        stage.setScene(scene);
        stage.show();
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HostWindow.class.getResource( fxml + ".fxml"));
        return fxmlLoader.load();
    }

    /**
     * Call this method in all other controllers to switch scenes
     * You can create another setRoot(fxml, params, params[]...) method if you want to pass other parameter such as userid, etc.
     * @param fxml
     * @throws IOException
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    public static void main(String[] args) {
        launch();
    }
}