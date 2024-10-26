package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;
import java.util.regex.Pattern;

public class UserSignupController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private RadioButton personalRadio;
    @FXML
    private RadioButton businessRadio;
    @FXML
    private TextField cityField;
    @FXML
    private TextField zipCodeField;
    @FXML
    private TextField businessAddressField;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private Label errorMessage;

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    public void initialize() {
        //create a ToggleGroup and assign it to both radio buttons
        ToggleGroup accountTypeGroup = new ToggleGroup();
        personalRadio.setToggleGroup(accountTypeGroup);
        businessRadio.setToggleGroup(accountTypeGroup);
    }

    @FXML
    private void handleBack() {
        navigateTo("signup-selection.fxml", "Select Signup Type");
    }

    @FXML
    protected void handleAccountType() {
        //show or hide the business address field based on the account type selection
        boolean isBusiness = businessRadio.isSelected();
        businessAddressField.setVisible(isBusiness);
    }

    @FXML
    protected void togglePasswordVisibility() {
        if (passwordField.isVisible()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordField.setVisible(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordTextField.setVisible(false);
        }
    }

    @FXML
    protected void handleSignup() {
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String accountType = businessRadio.isSelected() ? "Business" : "Personal";
        String city = cityField.getText();
        String zipCode = zipCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //business-specific information
        String businessAddress = businessRadio.isSelected() ? businessAddressField.getText() : null;

        //simple input validation
        if (firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() || zipCode.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please fill in all required fields.");
            return;
        }
        if (businessRadio.isSelected() && (businessAddress == null || businessAddress.isEmpty())) {
            errorMessage.setText("Please provide the business address.");
            return;
        }
        if (!isValidEmail(email)) {
            errorMessage.setText("Please enter a valid email address.");
            return;
        }
        if (!isValidPassword(password)) {
            errorMessage.setText("Password must be at least 8 characters, including a number and a special character.");
            return;
        }

        //construct for geocoding
        String fullAddress = city + ", " + zipCode;
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

        //call the SQL helper to create a new user
        try {
            sqlHelper.establishConnection();
            sqlHelper.CreateUser(firstName, lastName, accountType, city, zipCode, businessAddress, email, password);
            sqlHelper.updateUserLocation(sqlHelper.getLastInsertedUserID(), latitude, longitude);
            errorMessage.setText("Signup successful!");
            clearForm();
            navigateToLogin();
        } catch (Exception e) {
            errorMessage.setText("Signup failed: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        Pattern pattern = Pattern.compile(emailRegex);
        return pattern.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&*()_+\\-=\\[\\]{};':\"|,.<>/?]).{8,}$";
        Pattern pattern = Pattern.compile(passwordRegex);
        return pattern.matcher(password).matches();
    }

    //clear the form after successful signup
    private void clearForm() {
        firstNameField.clear();
        lastNameField.clear();
        cityField.clear();
        zipCodeField.clear();
        businessAddressField.clear();
        emailField.clear();
        passwordField.clear();
        personalRadio.setSelected(true);
        handleAccountType(); //reset visibility of business fields
    }

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

    private void navigateToLogin() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = fxmlLoader.load();
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
