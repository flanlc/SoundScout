package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.event.ActionEvent;

import java.io.IOException;

public class HelloController {

    @FXML
    protected void onSignupClick(javafx.event.ActionEvent event) {
        navigateTo("signup-selection.fxml", "Select Signup Type", event);
    }

    @FXML
    protected void onLoginClick(javafx.event.ActionEvent event) {
        navigateTo("login.fxml", "Login", event);
    }

    @FXML
    protected void onGuestClick(ActionEvent event) {
        navigateToLoggedHome("Guest", event);
    }

    private void navigateToLoggedHome(String name, ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            //get the controller and set the welcome message
            LoggedHomeController controller = loader.getController();
            controller.setWelcomeMessage(name, 0); //passes 0 as the ID for a guest

            //get the stage from the event source (Button)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void navigateTo(String fxmlFile, String title, javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = fxmlLoader.load();

            //get the current stage from any node in the scene graph (the root node)
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

            //create a new scene with the loaded root and set it to the current stage
            Scene scene = new Scene(root);
            stage.setTitle(title);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}