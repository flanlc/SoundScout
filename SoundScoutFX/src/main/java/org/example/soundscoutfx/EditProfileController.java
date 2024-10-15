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
    SoundScoutSQLHelper sql;
    @FXML
    AnchorPane anchorField;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setConnection(SoundScoutSQLHelper sql) {
        this.sql = sql;
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
    private void returnToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);
            dashboardController.sql = this.sql;

            Stage stage = (Stage) anchorField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSubmit() {
        if(genreField != null) {

        }
        if(videoField != null && videoField.getText().toLowerCase().contains("youtube")) {
            sql.UpdatePerformance(this.userID, videoField.getText());
        }
    }


}