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
    private ComboBox<String> stateComboBox; //dropdown for states
    @FXML
    private TextField otherStateField; //for states not listed
    @FXML
    private TextField zipCodeField;
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

        //adds a listener to the stateComboBox to show the "otherStateField" if "Other" is selected
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

        //validation and SQL interaction here
        if (firstName.isEmpty() || lastName.isEmpty() || stageName.isEmpty() ||
                dob.isEmpty() || streetAddress.isEmpty() || city.isEmpty() ||
                state.isEmpty() || zipCode.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("All fields are required.");
            return;
        }

        try {
            sqlHelper.establishConnection();
            sqlHelper.CreateArtist(firstName, lastName, stageName, dob, streetAddress, zipCode, city, state, email, password);
            errorMessage.setText("Signup successful!");

            //set a delay before navigating to the login screen to show messages
            PauseTransition delay = new PauseTransition(Duration.seconds(2)); //2 second delay
            delay.setOnFinished(event -> navigateToLogin());
            delay.play();
        } catch (Exception e) {
            errorMessage.setText("Signup failed: " + e.getMessage());
        }
    }

    private void navigateToLogin() {
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
    }

    private void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) emailField.getScene().getWindow(); //can adjust if needed
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
