module org.example.soundscoutfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires javafx.media;
    requires java.desktop;
    //requires cloudinary.taglib;
    requires cloudinary.core;
    requires dotenv.java;

    opens org.example.soundscoutfx to javafx.fxml;
    exports org.example.soundscoutfx;
}