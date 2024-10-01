package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.ResultSet;
import java.sql.Statement;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorMessage;

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    protected void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        //validate the user input
        if (email.isEmpty() || password.isEmpty()) {
            errorMessage.setText("Both fields are required.");
            return;
        }

        //check the credentials in the database
        try {
            sqlHelper.establishConnection();
            if (validateCredentials(email, password)) {
                errorMessage.setText("Login successful!");
                //redirect to the main page
            } else {
                errorMessage.setText("Invalid email or password.");
            }
        } catch (Exception e) {
            errorMessage.setText("Login failed: " + e.getMessage());
        }
    }

    private boolean validateCredentials(String email, String password) throws Exception {
        String query = "SELECT * FROM Artist WHERE Email = ? AND Password = ?";
        try (PreparedStatement preparedStatement = sqlHelper.conn.prepareStatement(query)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();  //returns true if a match is found
        } catch (Exception e) {
            throw new RuntimeException("An error has occurred while checking credentials: " + e.getMessage());
        }
    }
}
