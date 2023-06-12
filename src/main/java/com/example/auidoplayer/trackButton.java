package com.example.auidoplayer;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import java.io.File;
import java.util.List;

public class trackButton extends HBox {

    protected String path;
    protected String name;
    protected String artist;

    Button deleteButton;
    Label label;


    public trackButton(File file, String id, double width){
        path = file.getAbsolutePath();

        name = file.getName();


        //стиль названия
        label = new Label();
        label.setText(name);

        label.setMinHeight(40);
        label.setMaxHeight(40);

        label.setMinWidth(width / 8 * 7);
        label.setMaxWidth(width / 8 * 7);

        //стиль кнопки
        deleteButton = new Button();
        deleteButton.setText("del");

        deleteButton.setMinHeight(40);
        deleteButton.setMaxHeight(40);

        deleteButton.setMinWidth(width / 8);
        deleteButton.setMaxWidth(width / 8);

        //стиль всей панельки
        this.setId(id);
        this.setStyle("-fx-border-color: #000000");

        this.getChildren().addAll(label, deleteButton);
    }
}
