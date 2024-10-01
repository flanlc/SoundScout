package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class ArtistSignupController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField stageNameField;
    @FXML
    private TextField dobField;
    @FXML
    private TextField streetAddressField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField cityField;
    @FXML
    private TextField stateField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessage;

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    protected void handleSignup() {
        //collecting the form data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String stageName = stageNameField.getText();
        String dob = dobField.getText();
        String streetAddress = streetAddressField.getText();
        String zipCode = zipCodeField.getText();
        String city = cityField.getText();
        String state = stateField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //validate the inputs
        if (firstName.isEmpty() || lastName.isEmpty() || stageName.isEmpty() || dob.isEmpty() ||
            streetAddress.isEmpty() || zipCode.isEmpty() || city.isEmpty() || state.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("All fields are required.");
            return;
        }

        //call the SQL helper to create artist
        try {
            sqlHelper.establishConnection();
            sqlHelper.CreateArtist(firstName, lastName, stageName, dob, streetAddress, zipCode, city, state, email, password);
            errorMessage.setText("Signup successful!");
        } catch (Exception e) {
            errorMessage.setText("Signup failed: " + e.getMessage());
        }
    }
}
