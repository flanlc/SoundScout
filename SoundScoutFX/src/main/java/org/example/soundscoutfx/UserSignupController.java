package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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
    private CheckBox showPasswordCheckBox;
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

        zipCodeField.setTextFormatter(new TextFormatter<>(change -> {
            String newText = change.getControlNewText();
            if (newText.matches("\\d{0,5}")) {
                return change;
            }
            return null;
        }));
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
        if (showPasswordCheckBox.isSelected()) {
            passwordTextField.setText(passwordField.getText());
            passwordTextField.setVisible(true);
            passwordTextField.setManaged(true);
            passwordField.setVisible(false);
            passwordField.setManaged(false);
        } else {
            passwordField.setText(passwordTextField.getText());
            passwordField.setVisible(true);
            passwordField.setManaged(true);
            passwordTextField.setVisible(false);
            passwordTextField.setManaged(false);
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

        String businessAddress = businessRadio.isSelected() ? businessAddressField.getText() : null;

        if (firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() || zipCode.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please fill in all required fields.");
            return;
        }
        if (businessRadio.isSelected() && (businessAddress == null || businessAddress.isEmpty())) {
            errorMessage.setText("Please provide the business address.");
            return;
        }
        if (zipCode.length() != 5) {
            errorMessage.setText("Please enter a valid 5-digit ZIP code.");
            zipCodeField.requestFocus();
            return;
        }
        if (!isValidEmail(email)) {
            errorMessage.setText("Please enter a valid email address.");
            emailField.requestFocus();
            return;
        }
        if (!isValidPassword(password)) {
            errorMessage.setText("Password must be at least 8 characters, including a number and a special character.");
            passwordField.requestFocus();
            return;
        }

        try {
            sqlHelper.establishConnection();
            if (sqlHelper.ifEmailExists(email)) {
                errorMessage.setText("Email is already in use. Please use a different email.");
                emailField.requestFocus();
                return;
            }
        } catch (Exception e) {
            errorMessage.setText("An error occurred while checking email availability.");
            e.printStackTrace();
            return;
        }

        String fullAddress = city + ", " + zipCode;
        double latitude = 0.0;
        double longitude = 0.0;

        try {
            double[] coordinates = LocationHelper.getCoordinates(fullAddress);
            latitude = coordinates[0];
            longitude = coordinates[1];
        } catch (Exception e) {
            errorMessage.setText("Unable to fetch coordinates for the provided location.");
            return;
        }

        try {
            sqlHelper.establishConnection();
            sqlHelper.CreateUser(firstName, lastName, accountType, city, zipCode, businessAddress, email, password);
            sqlHelper.updateUserLocation(sqlHelper.getLastInsertedUserID(), latitude, longitude);

            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
            successAlert.setTitle("Signup Successful");
            successAlert.setHeaderText("Account Created");
            successAlert.setContentText("Success! Your account has been created. You can now log in.");
            successAlert.showAndWait();

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
