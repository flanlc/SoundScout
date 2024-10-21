package org.example.soundscoutfx;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class EditUserProfileController {

    @FXML
    private TextField firstNameField;

    @FXML
    private TextField lastNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField cityField;

    @FXML
    private TextField zipCodeField;

    @FXML
    private Label successMessage;

    private int userID;
    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();
    private String firstName;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;

   public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {

        this.userID = userID;
        this.firstName = firstName; // Set the user's first name as userName
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;


        firstNameField.setText(firstName);
        lastNameField.setText(lastName);
        emailField.setText(email);
        cityField.setText(city);
        zipCodeField.setText(zipCode);

        //System.out.println("First name set to: " + this.firstName); //debug statement
    }

    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String city = cityField.getText();
        String zipCode = zipCodeField.getText();

        try {
            sqlHelper.establishConnection();
            //only update non-empty fields
            if (firstName != null && !firstName.isEmpty()) {
                sqlHelper.updateSingleField("FirstName", firstName, userID);
            }
            if (lastName != null && !lastName.isEmpty()) {
                sqlHelper.updateSingleField("LastName", lastName, userID);
            }
            if (email != null && !email.isEmpty()) {
                sqlHelper.updateSingleField("Email", email, userID);
            }
            if (city != null && !city.isEmpty()) {
                sqlHelper.updateSingleField("City", city, userID);
            }
            if (zipCode != null && !zipCode.isEmpty()) {
                sqlHelper.updateSingleField("ZipCode", zipCode, userID);
            }

            successMessage.setText("Profile has been updated successfully!");

            new Thread(() -> {
                try {
                    Thread.sleep(1000); //one second delay
                    javafx.application.Platform.runLater(this::navigateToHome);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            setSuccessMessage("Error updating profile: " + e.getMessage(), "red");
        }
    }

    private void setSuccessMessage(String message, String color) {
        successMessage.setText(message);
        successMessage.setStyle("-fx-text-fill: " + color + ";");
    }

    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            //pass user info back to LoggedHomeController
            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setUserDetails(firstNameField.getText(), lastNameField.getText(), emailField.getText(), cityField.getText(), zipCodeField.getText(), userID);

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBackButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            //pass userName and userID back to logged-home
            loggedHomeController.setWelcomeMessage(this.firstName, this.userID);
            loggedHomeController.setUserType("User");

            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
