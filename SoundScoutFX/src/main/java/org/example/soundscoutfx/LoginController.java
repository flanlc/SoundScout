package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField passwordTextField;
    @FXML
    private CheckBox showPasswordCheckBox;
    @FXML
    private Label errorMessage;

    private final SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    protected void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        setErrorMessage("", "");

        if (email.isEmpty() || password.isEmpty()) {
            setErrorMessage("Email and password are required.", "red");
            return;
        }

        try {
            sqlHelper.establishConnection();
            UserInfo userInfo = sqlHelper.verifyUserCredentials(email, password);

            if (userInfo != null) {
                setErrorMessage("Login successful!", "green");

                Session session = Session.getInstance();
                session.setUserID(userInfo.getId());
                session.setUserName(userInfo.getFirstName());
                session.setUserType(userInfo.getUserType());
                session.setLastName(userInfo.getLastName());
                session.setEmail(userInfo.getEmail());
                session.setCity(userInfo.getCity());
                session.setZipCode(userInfo.getZipCode());
                session.setSql(sqlHelper);

                if ("Artist".equals(userInfo.getUserType())) {
                    session.setCurrentArtistID(userInfo.getId());
                }

                new Thread(() -> {
                    try {
                        Thread.sleep(1000);
                        javafx.application.Platform.runLater(this::navigateToLoggedHome);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                setErrorMessage("Invalid email or password.", "red");
            }
        } catch (Exception e) {
            setErrorMessage("Login failed: " + e.getMessage(), "red");
        }
    }

    private void setErrorMessage(String message, String color) {
        errorMessage.setText(message);
        if (!color.isEmpty()) {
            errorMessage.setStyle("-fx-text-fill: " + color + ";");
        }
    }

    @FXML
    private void togglePasswordVisibility() {
        boolean showPassword = showPasswordCheckBox.isSelected();
        passwordTextField.setText(passwordField.getText());
        passwordTextField.setVisible(showPassword);
        passwordTextField.setManaged(showPassword);
        passwordField.setVisible(!showPassword);
        passwordField.setManaged(!showPassword);
    }

    @FXML
    private void handleSignupRedirect() {
        navigateTo("signup-selection.fxml", "Signup");
    }

    @FXML
    private void handleBack() {
        navigateTo("hello-view.fxml", "Home");
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

    private void navigateToLoggedHome() {
        try {
            Session session = Session.getInstance();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController controller = loader.getController();
            controller.setWelcomeMessage(
                    session.getUserName(),
                    session.getUserID(),
                    session.getLastName(),
                    session.getEmail(),
                    session.getCity(),
                    session.getZipCode()
            );

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
