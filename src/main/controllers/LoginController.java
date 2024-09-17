//handles the musician/user login process
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import models.Database;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Simulated database lookup (will later implement DB queries)
        if (Database.loginUser(username, password)) {
            System.out.println("Login successful, loading user dashboard...");
            // Transition to the user/musician dashboard
        } else {
            errorMessage.setText("Invalid username or password");
        }
    }
}
