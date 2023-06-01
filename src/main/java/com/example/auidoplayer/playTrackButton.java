package com.example.auidoplayer;

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

public class playTrackButton extends Label {

    protected String path;
    protected String name;
    protected String artist;

    public playTrackButton(File file, String id){
        path = file.getAbsolutePath();

        name = file.getName();


        //стиль
        super.setText(name);
        super.setId(id);
        super.setMinHeight(50);
        super.setStyle("-fx-border-color: #000000");

    }





}
