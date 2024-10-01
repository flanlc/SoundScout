package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SplashController {

    @FXML
    protected void handleLogin() throws IOException {
        //navigate to the login page
        Stage stage = (Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();
        Scene loginScene = new Scene(FXMLLoader.load(getClass().getResource("login.fxml")));
        stage.setScene(loginScene);
        stage.setTitle("Login");
    }

    @FXML
    protected void handleSignup() throws IOException {
        //navigate to the user type selection page (artist or user signup)
        Stage stage = (Stage) ((javafx.scene.Node)event.getSource()).getScene().getWindow();
        Scene signupTypeScene = new Scene(FXMLLoader.load(getClass().getResource("user-type.fxml")));
        stage.setScene(signupTypeScene);
        stage.setTitle("Signup");
    }
}
