//handles the main/home screen, takes users to Login or Signup
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LandingController {

    @FXML
    private Button loginButton;

    @FXML
    private Button signupButton;

    @FXML
    private void handleLogin() {
        //load login page
        System.out.println("Loading login page...");
        //***page transitions needed later***
    }

    @FXML
    private void handleSignup() {
        //load signup page
        System.out.println("Loading signup page...");
    }
}
