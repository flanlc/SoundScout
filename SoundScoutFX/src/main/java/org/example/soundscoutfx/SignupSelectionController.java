package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class SignupSelectionController {

    @FXML
    protected void handleArtistSignup(ActionEvent event) {
        //navigate to artist signup screen
        navigateTo("artist-signup.fxml", "Artist Signup", event);
    }

    @FXML
    protected void handleUserSignup(ActionEvent event) {
        //navigate to user signup screen
        navigateTo("user-signup.fxml", "User Signup", event);
    }

    @FXML
    private void handleLoginRedirect(ActionEvent event) {
        //navigate to login
        navigateTo("login.fxml", "Login", event);
    }

    @FXML
    private void handleBack(ActionEvent event) {
        //navigate backwards to go back to splash page
        navigateTo("hello-view.fxml", "Home", event);
    }

    private void navigateTo(String fxmlFile, String title, ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            //get current stage from the event source
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            //set new scene and title
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
