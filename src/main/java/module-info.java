module com.example.auidoplayer {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.ikonli.javafx;
    requires javafx.media;
    requires json.simple;

    opens com.example.auidoplayer to javafx.fxml;
    exports com.example.auidoplayer;
}