package com.example.pos;

import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class SceneController {

    public static Stage stage;

    static Scene scene;

    static Parent root;


    //used for switching Scenes
    public static void switchScene(String fxml, Event event, String title) throws IOException {
        root = FXMLLoader.load(com.example.pos.SceneController.class.getResource(fxml));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.resizableProperty().set(false);
        stage.setTitle(title);
        stage.show();


    }

}
