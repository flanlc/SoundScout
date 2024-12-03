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
        Session session = Session.getInstance();
        session.clearSession();
        session.setUserID(0);
        session.setUserName("Guest");
        session.setUserType("Guest");
        navigateToLoggedHome(event);
    }

    private void navigateToLoggedHome(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController controller = loader.getController();
            Session session = Session.getInstance();
            controller.setWelcomeMessage(
                    session.getUserName(),
                    session.getUserID(),
                    session.getLastName(),
                    session.getEmail(),
                    session.getCity(),
                    session.getZipCode()
            );

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