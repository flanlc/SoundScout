package org.example.soundscoutfx;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.SQLException;

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

    @FXML
    private TextField locationField;

    @FXML
    private void initialize() {
        Session session = Session.getInstance();

        firstNameField.setText(session.getUserName());
        lastNameField.setText(session.getLastName());
        emailField.setText(session.getEmail());
        cityField.setText(session.getCity());
        zipCodeField.setText(session.getZipCode());
    }

    @FXML
    private void handleUpdateLocation() {
        String location = locationField.getText();
        if (location == null || location.isEmpty()) {
            showErrorMessage("Please enter a valid location.");
            return;
        }

        double[] coordinates = LocationHelper.getCoordinates(location);
        double latitude = coordinates[0];
        double longitude = coordinates[1];

        try {
            Session session = Session.getInstance();
            session.getSql().updateUserLocation(session.getUserID(), latitude, longitude);
            showSuccessMessage("Location updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating location.");
        }
    }



    @FXML
    private void handleSave() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String email = emailField.getText();
        String city = cityField.getText();
        String zipCode = zipCodeField.getText();

        try {
            Session session = Session.getInstance();
            SoundScoutSQLHelper sqlHelper = session.getSql();

            if (firstName != null && !firstName.isEmpty()) {
                sqlHelper.updateSingleField("FirstName", firstName, session.getUserID());
                session.setUserName(firstName);
            }
            if (lastName != null && !lastName.isEmpty()) {
                sqlHelper.updateSingleField("LastName", lastName, session.getUserID());
                session.setLastName(lastName);
            }
            if (email != null && !email.isEmpty()) {
                sqlHelper.updateSingleField("Email", email, session.getUserID());
                session.setEmail(email);
            }
            if (city != null && !city.isEmpty()) {
                sqlHelper.updateSingleField("City", city, session.getUserID());
                session.setCity(city);
            }
            if (zipCode != null && !zipCode.isEmpty()) {
                sqlHelper.updateSingleField("ZipCode", zipCode, session.getUserID());
                session.setZipCode(zipCode);
            }

            setSuccessMessage("Profile has been updated successfully!", "green");

            PauseTransition pause = new PauseTransition(Duration.seconds(1));
            pause.setOnFinished(event -> navigateToHome());
            pause.play();

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

            Session session = Session.getInstance();
            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setWelcomeMessage(
                    session.getUserName(),
                    session.getUserID(),
                    session.getLastName(),
                    session.getEmail(),
                    session.getCity(),
                    session.getZipCode()
            );

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
        navigateToHome();
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
