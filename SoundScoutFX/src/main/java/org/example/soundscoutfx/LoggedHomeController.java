package org.example.soundscoutfx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class LoggedHomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button editProfileButton;

    private int userID;
    private String userType;
    private String artistName;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;

    public void setWelcomeMessage(String firstName, int userID) {
        System.out.println("setWelcomeMessage called with: " + firstName + ", ID: " + userID);

        this.artistName = firstName;
        this.userID = userID;
        welcomeLabel.setText("Welcome, " + firstName + "! Your ID is: " + userID);

    //hide the "Edit Artist Profile" button if the user is a guest (ID = 0)
        if (userID == 0) {
            editProfileButton.setVisible(false);
        } else {
            editProfileButton.setVisible(true);
        }
    }


    public void setUserType(String userType) {
        this.userType = userType;
        configureButtonBasedOnUserType();
    }

    public void configureButtonBasedOnUserType() {
        if ("Artist".equalsIgnoreCase(userType)) {
            editProfileButton.setText("Edit Artist Profile");
        } else {
            editProfileButton.setText("Edit User Profile");
        }
    }

    @FXML
    protected void handleLogout() {
        navigateTo("hello-view.fxml", "SoundScout");
    }

    @FXML
    protected void handleAvailableArtistsClick() {
        navigateTo("Dashboard.fxml", "Available Artists");
    }

    @FXML
    protected void navigateToEditProfile() {
        try {
            FXMLLoader loader;

            //determine which profile page to load based on user type
            if ("Artist".equalsIgnoreCase(userType)) {
                loader = new FXMLLoader(getClass().getResource("edit-profile.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("edit-user-profile.fxml"));
            }

            Parent root = loader.load();

            if ("Artist".equalsIgnoreCase(userType)) {
                EditProfileController editProfileController = loader.getController();
                editProfileController.setArtistDetails(this.artistName, this.userID);
            } else {
                EditUserProfileController editUserProfileController = loader.getController();
                editUserProfileController.setUserDetails(this.artistName, this.lastName, this.email, this.city, this.zipCode, this.userID);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public void setUserID(int userID) {
        this.userID = userID;
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

    public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        System.out.println("setUserDetails called with: " + firstName + ", " + lastName + ", " + email + ", " + city + ", " + zipCode + ", ID: " + userID);

        this.artistName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
        this.userID = userID;

        welcomeLabel.setText("Welcome, " + firstName + "! Your ID is: " + userID);
    }



    @FXML
    private void navigateToDashboard() {
        try {
            //System.out.println("Navigating to Dashboard with userType: " + this.userType);  //debug statement

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);
            dashboardController.setUserName(this.artistName);
            dashboardController.setUserType(this.userType);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
