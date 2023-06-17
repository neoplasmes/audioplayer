package com.example.auidoplayer;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.DoublePropertyBase;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.RotateEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;

public class CircleKnob extends Group {
    private Circle circle;
    private Circle pointer;
    private Arc progress;
    private Text text;
    private double radius;
    private double angle = 135;
    
    private DoubleProperty value = new DoublePropertyBase(0.0) {
        @Override
        public Object getBean() {
            return CircleKnob.this;
        }

        @Override
        public String getName() {
            return "value";
        }
    };
    public CircleKnob(double radius, String s) {
        this.radius = radius;

        circle = new Circle(this.radius);
        circle.setLayoutX(0);
        circle.setLayoutY(0);
        circle.setStyle("-fx-fill: #FFFFFF;");

        progress = new Arc();
        progress.setLayoutX(0);
        progress.setLayoutY(0);
        progress.setStyle("-fx-fill: #abebdd");
        progress.setType(ArcType.ROUND);
        progress.setRadiusX(this.radius * 1.08);
        progress.setRadiusY(this.radius * 1.08);
        progress.setStartAngle(-135);

        pointer = new Circle(this.radius / 10);
        setAngle(angle);
        pointer.setStyle("-fx-fill: #bababa;");


        text = new Text(s);
        text.setStyle(" -fx-font-family:\"Consolas\";-fx-font-size: " + (radius / 3) + "px;");
        StackPane tvarina = new StackPane();
        tvarina.setPrefWidth(radius);
        tvarina.setPrefHeight(radius);
        tvarina.setMinWidth(radius);
        tvarina.setMaxWidth(radius);
        tvarina.setMinHeight(radius);
        tvarina.setMaxHeight(radius);
        tvarina.setLayoutX(-tvarina.getPrefWidth()/2);
        tvarina.setLayoutY(-tvarina.getPrefHeight()/2);
        tvarina.getChildren().add(text);

        this.getChildren().addAll(progress, circle, pointer, tvarina);



        this.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            if(mouseEvent.getButton() == MouseButton.PRIMARY && mouseEvent.getClickCount() == 2){
                angle = 135;
                setAngle(angle);
            }
        });
        this.setOnScroll((ScrollEvent event) -> control(event));
    }

    private void setAngle(double ang){
        pointer.setLayoutY(this.radius / 10 * 7.5 * Math.sin(Math.toRadians(ang + 135)));
        pointer.setLayoutX(this.radius / 10 * 7.5 * Math.cos(Math.toRadians(ang + 135)));
        this.value.set(ang / 270);
        progress.setLength(-angle);
    }

    public double getValue(){
        return this.value.get();
    }
    
    public DoubleProperty valueProperty(){
        return this.value;
    }


    private void control(ScrollEvent event){
        if(event.getDeltaY() > 0 && angle < 270) {
            angle += 5;
            setAngle(angle);
        } else if (event.getDeltaY() < 0 && angle > 0) {
            angle -= 5;
            setAngle(angle);
        }
    }


}
