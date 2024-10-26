package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.util.Duration;
import javafx.animation.PauseTransition;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class ArtistSignupController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField stageNameField;
    @FXML
    private DatePicker dobPicker;
    @FXML
    private TextField streetAddressField;
    @FXML
    private TextField cityField;
    @FXML
    private ComboBox<String> stateComboBox;
    @FXML
    private TextField otherStateField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField rateField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessage;

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    public void initialize() {
        stateComboBox.getItems().addAll(Arrays.asList(
                "Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado",
                "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho",
                "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana",
                "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota",
                "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire",
                "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota",
                "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island",
                "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah",
                "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming", "Other"
        ));

        stateComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            otherStateField.setVisible("Other".equals(newValue));
        });
    }

    @FXML
    private void handleBack() {
        navigateTo("signup-selection.fxml", "Select Signup Type");
    }

    @FXML
    protected void handleSignup() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String stageName = stageNameField.getText();
        String dob = dobPicker.getValue() != null ? dobPicker.getValue().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
        String streetAddress = streetAddressField.getText();
        String city = cityField.getText();
        String state = "Other".equals(stateComboBox.getValue()) ? otherStateField.getText() : stateComboBox.getValue();
        String zipCode = zipCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        double rate = 0;
        try {
            rate = Double.parseDouble(rateField.getText());
            if (rate < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            errorMessage.setText("Please enter a valid rate.");
            return;
        }

        if (firstName.isEmpty() || lastName.isEmpty() || stageName.isEmpty() ||
                dob.isEmpty() || streetAddress.isEmpty() || city.isEmpty() ||
                state.isEmpty() || zipCode.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("All fields are required.");
            return;
        }

        //geocoding
        String fullAddress = streetAddress + ", " + city + ", " + state + " " + zipCode;
        double latitude = 0.0;
        double longitude = 0.0;

        //grab coordinates
        try {
            double[] coordinates = LocationHelper.getCoordinates(fullAddress);
            latitude = coordinates[0];
            longitude = coordinates[1];
        } catch (Exception e) {
            errorMessage.setText("Unable to fetch coordinates for the provided location.");
            return;
        }

        int newArtistID = 0;
        try {
            sqlHelper.establishConnection();
            newArtistID = sqlHelper.CreateArtist(firstName, lastName, stageName, dob, streetAddress, zipCode, city, state, rate, email, password);
            sqlHelper.updateArtistLocation(newArtistID, latitude, longitude);
            errorMessage.setText("Signup successful!");

            PauseTransition delay = new PauseTransition(Duration.seconds(2)); //2 seconds
            //delay.setOnFinished(event -> navigateToGenreSelection());
            delay.play();
        } catch (Exception e) {
            errorMessage.setText("Signup failed: " + e.getMessage());
        }

        navigateToGenreSelection(newArtistID);
    }

    private void navigateToGenreSelection(int artistID) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("genre-selection.fxml"));
            Parent root = loader.load();
            GenreSelectionController genreController = loader.getController();
            genreController.setArtistID(artistID);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Select Your Genre");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }*/

    private void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
