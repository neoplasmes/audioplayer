package com.example.auidoplayer;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.*;

public class playTrackButton extends Label {

    protected String path;
    protected String name;
    protected String artist;

    public playTrackButton(File file, String id) throws IOException {
        path = file.getAbsolutePath();

        name = file.getName();


        //стиль
        super.setText(path + "\n" + name);
        super.setId(id);
        super.setMinHeight(50);
        super.setStyle("-fx-background-color: #BCCCA1; -fx-border-color: #000000");

        super.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playTrackButton.super.setStyle("-fx-background-color: #DCF0BD; -fx-border-color: #000000");
            }
        });

        super.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                playTrackButton.super.setStyle("-fx-background-color: #BCCCA1; -fx-border-color: #000000");
            }
        });

    }





}
