package org.example.soundscoutfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        //launch();

        SoundScoutSQLHelper sql = new SoundScoutSQLHelper();
        sql.testConnection();

        //sql.CreateArtist("Beyoncé", "Knowles", "Beyoncé", "1981-09-04", "1505 Hadley St", "77002", "Houston", "TX", "beyonce@example.com", "Password123");
        //sql.CreateArtist("Elvis", "Presley", "Elvis", "1935-01-08", "3764 Elvis Presley Blvd", "38116", "Memphis", "TN", "elvispresley@example.com", "Password123");
        //sql.CreateArtist("Adele", "Adkins", "Adele", "1988-05-05", "25 Kensington Park Rd", "W11 3BY", "London", "England", "adele@example.com", "Password123");
        //sql.CreateArtist("Taylor", "Swift", "Taylor Swift", "1989-12-13", "13 Northumberland Ave", "37205", "Nashville", "TN", "taylorswift@example.com", "Password123");
        //sql.CreateArtist("Freddie", "Mercury", "Freddie Mercury", "1946-09-05", "1 Logan Place", "W8 6QN", "London", "England", "freddiemercury@example.com", "Password123");

        System.exit(0);
    }
}
