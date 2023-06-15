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

    final int p = 9;
    public trackButton(File file, double width){
        path = file.getAbsolutePath();

        String style = getClass().getResource("trackbutton.css").toString();

        name = file.getName();

        //стиль названия
        label = new Label();
        label.setText(name);

        label.setMinHeight(width / p);
        label.setMaxHeight(width / p);

        label.setMinWidth(width / p * (p-1));
        label.setMaxWidth(width / p * (p-1));
        label.getStylesheets().add(style);



        //стиль кнопки
        deleteButton = new Button();
        //deleteButton.setText("del");

        deleteButton.setMinHeight(width / p);
        deleteButton.setMaxHeight(width / p);

        deleteButton.setMinWidth(width / p);
        deleteButton.setMaxWidth(width / p);

        deleteButton.getStylesheets().add(style);

        //стиль всей панельки
        this.getStyleClass().add("hbox-style");
        this.getStylesheets().add(style);


        this.setOnMouseEntered(mouseEvent -> {
            this.getStyleClass().add("hbox-style-hover");
        });

        this.setOnMouseExited(mouseEvent -> {
            this.getStyleClass().remove("hbox-style-hover");
        });

        this.getChildren().addAll(label, deleteButton);
    }
}
