package org.example.soundscoutfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class LoggedHomeController {

    @FXML
    private Label welcomeLabel;

    private int userID;
    private String userType;

    public void setWelcomeMessage(String name, int id) {
        welcomeLabel.setText("Welcome, " + name + "! Your ID is: " + id);
    }

    @FXML
    protected void handleLogout() {
        navigateTo("hello-view.fxml", "SoundScout");
    }

    @FXML
    protected void handleAvailableArtistsClick() {
        navigateTo("Dashboard.fxml", "Available Artists");
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    //set the user type
    public void setUserType(String userType) {
        this.userType = userType;
    }

    //use the userID and userType for other functions (user-specific data, etc)
    public void loadUserContent() {
        if ("Artist".equals(userType)) {
            //artist-specific content
        } else {
            //content specific to any other user
        }
    }

    private void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToDashboard() {
        try {
            //load Dashboard.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);

            //switch to new scene
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
