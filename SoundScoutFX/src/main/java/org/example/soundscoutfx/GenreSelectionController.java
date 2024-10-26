package org.example.soundscoutfx;

import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GenreSelectionController {
    @FXML
    private CheckBox rockCheckBox, popCheckBox, rapCheckBox, rnbCheckBox, countryCheckBox, bluesCheckBox, electronicCheckBox, jazzCheckBox, indieCheckBox, alternativeCheckBox;
    private int artistID;

    private SoundScoutSQLHelper sql = new SoundScoutSQLHelper();

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    @FXML
    private Label successMessageLabel;

    @FXML
    private AnchorPane anchorField;

    @FXML
    private void finalizeSignup() {
        List<String> selectedGenres = new ArrayList<>();

        if (rockCheckBox.isSelected()) selectedGenres.add("Rock");
        if (popCheckBox.isSelected()) selectedGenres.add("Pop");
        if (rapCheckBox.isSelected()) selectedGenres.add("Rap");
        if (rnbCheckBox.isSelected()) selectedGenres.add("RnB");
        if (countryCheckBox.isSelected()) selectedGenres.add("Country");
        if (bluesCheckBox.isSelected()) selectedGenres.add("Blues");
        if (electronicCheckBox.isSelected()) selectedGenres.add("Electronic");
        if (jazzCheckBox.isSelected()) selectedGenres.add("Jazz");
        if (indieCheckBox.isSelected()) selectedGenres.add("Indie");
        if (alternativeCheckBox.isSelected()) selectedGenres.add("Alternative");

        try {
            sql.establishConnection();
            String genres = String.join(", ", selectedGenres);
            sql.updateArtistGenres(artistID, genres);

            successMessageLabel.setText("Genres updated successfully!");
            successMessageLabel.setVisible(true);

            PauseTransition delay = new PauseTransition(Duration.seconds(2));
            delay.setOnFinished(event -> navigateToLogin());
            delay.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void navigateToLogin() {
        try {
            Stage stage = (Stage) anchorField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
            Parent root = loader.load();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}