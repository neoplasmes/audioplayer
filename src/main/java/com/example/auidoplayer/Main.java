package com.example.auidoplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

import java.io.IOException;
import java.util.Objects;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {


        FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("css.fxml")));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        stage.setTitle("Detroit team");
        stage.setScene(scene);
        stage.show();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent arg0) {

                Platform.exit();
                Controller controller = loader.getController();
                try {
                    controller.savePlaylist();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                System.exit(0);
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
