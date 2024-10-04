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
import java.util.ArrayList;

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
        launch();

        ArrayList<Artist> artistArrayList = new ArrayList<Artist>();
        SoundScoutSQLHelper sql = new SoundScoutSQLHelper();
        sql.testConnection();

        artistArrayList = sql.GetDBArtistsProfiles();
        for(int i = 0; i < artistArrayList.size(); i++) {
            System.out.println(artistArrayList.get(i).toString());
        }

        //sql.CreateArtist("Beyoncé", "Knowles", "Beyoncé", "1981-09-04", "1505 Hadley St", "77002", "Houston", "TX", "beyonce@example.com", "Password123");
        //sql.CreateArtist("Elvis", "Presley", "Elvis", "1935-01-08", "3764 Elvis Presley Blvd", "38116", "Memphis", "TN", "elvispresley@example.com", "Password123");
        //sql.CreateArtist("Adele", "Adkins", "Adele", "1988-05-05", "25 Kensington Park Rd", "W11 3BY", "London", "England", "adele@example.com", "Password123");
        //sql.CreateArtist("Taylor", "Swift", "Taylor Swift", "1989-12-13", "13 Northumberland Ave", "37205", "Nashville", "TN", "taylorswift@example.com", "Password123");
        //sql.CreateArtist("Freddie", "Mercury", "Freddie Mercury", "1946-09-05", "1 Logan Place", "W8 6QN", "London", "England", "freddiemercury@example.com", "Password123");

        System.exit(0);
    }
}
