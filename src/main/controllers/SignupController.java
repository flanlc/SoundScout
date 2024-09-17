//handles the musician/user signup process
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Database;

public class SignupController {

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> accountType;

    @FXML
    private PasswordField passwordField;

    @FXML
    private void handleSignup() {
        String name = nameField.getText();
        String type = accountType.getValue();
        String password = passwordField.getText();

        // Register the user in the database
        if (Database.registerUser(name, password, type)) {
            System.out.println("Signup successful, redirecting to login...");
            // Redirect to login or user dashboard
        } else {
            System.out.println("Signup failed.");
        }
    }
}
