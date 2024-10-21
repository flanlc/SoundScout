package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class EditProfileController {
    @FXML
    TextField genreField;
    @FXML
    TextField videoField;
    private int userID;
    private String artistName;
    SoundScoutSQLHelper sql;
    @FXML
    AnchorPane anchorField;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setConnection(SoundScoutSQLHelper sql) {
        this.sql = sql;
    }

    public void setArtistDetails(String artistName, int userID) {
        this.artistName = artistName;
        this.userID = userID;
    }

    /* code for this method was created with code originally written in Professor Hoskeys CSC 211 class **/
    public void handlePhoto() {
        String filePath = null;

        FileChooser fileOpener = new FileChooser();
        FileChooser.ExtensionFilter filter = new FileChooser.ExtensionFilter("Image Files (*.jpg, *.png)", "*.jpg", "*.png");
        fileOpener.getExtensionFilters().add(filter);

        File current = null;
        try {
            current = new File(new File(".").getCanonicalPath());
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        fileOpener.setInitialDirectory(current);
        File selectedFile = fileOpener.showOpenDialog(null);

        if (selectedFile != null) {
            filePath = selectedFile.getAbsolutePath();
        } else {
            System.out.println("No file selected.");
            return;
        }

        if (filePath != null) {
            // Upload to Cloudinary
            this.sql.UploadToCloudinary(this.userID, filePath);
        }
    }

    @FXML
    private void returnToLoggedHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setWelcomeMessage(this.artistName, this.userID);  // Pass back the artist's name and ID
            loggedHomeController.setUserType("Artist");

            Stage stage = (Stage) anchorField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleSubmit() {
        if (genreField != null) {

        }
        if (videoField != null && videoField.getText().toLowerCase().contains("youtube")) {
            sql.UpdatePerformance(this.userID, videoField.getText());
        }
    }
}