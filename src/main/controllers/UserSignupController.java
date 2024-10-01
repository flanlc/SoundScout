package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

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
    private Label errorMessage;

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    protected void handleAccountType() {
        //show or hide business-related fields based on the account type selection
        boolean isBusiness = businessRadio.isSelected();
        businessAddressField.setVisible(isBusiness);
    }

    @FXML
    protected void handleSignup() {
        //collect form data
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        String accountType = businessRadio.isSelected() ? "Business" : "Personal";
        String city = cityField.getText();
        String zipCode = zipCodeField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();

        //gather the business-specific information
        String businessAddress = businessRadio.isSelected() ? businessAddressField.getText() : null;

        //basic input validation
        if (firstName.isEmpty() || lastName.isEmpty() || city.isEmpty() || zipCode.isEmpty() || email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Please fill in all required fields.");
            return;
        }

        //call the SQL helper to create a new user
        try {
            sqlHelper.establishConnection();
            sqlHelper.CreateUser(firstName, lastName, accountType, city, zipCode, businessAddress, email, password);
            errorMessage.setText("Signup successful!");
        } catch (Exception e) {
            errorMessage.setText("Signup failed: " + e.getMessage());
        }
    }
}
