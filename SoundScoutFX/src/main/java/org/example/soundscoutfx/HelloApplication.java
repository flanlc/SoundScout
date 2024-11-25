package org.example.soundscoutfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/org/example/soundscoutfx/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 750, 500);
        stage.setTitle("SoundScout");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();

        //sql.CreateArtist("Beyoncé", "Knowles", "Beyoncé", "1981-09-04", "1505 Hadley St", "77002", "Houston", "TX", "beyonce@example.com", "Password123");
    }
}