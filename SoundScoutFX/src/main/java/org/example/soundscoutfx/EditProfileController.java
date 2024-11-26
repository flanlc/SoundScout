package org.example.soundscoutfx;

import com.sun.javafx.menu.CheckMenuItemBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import javafx.event.ActionEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class EditProfileController {
    @FXML
    TextField genreField;
    @FXML
    TextField videoField;
    @FXML
    AnchorPane anchorField;
    @FXML
    private CheckBox rockCheckBox, popCheckBox, rapCheckBox, rnbCheckBox, countryCheckBox, bluesCheckBox, electronicCheckBox, jazzCheckBox, indieCheckBox, alternativeCheckBox;
    @FXML
    private TextField rateField;

    /*
    sub-genre checkboxes for Rock
    @FXML
    private CheckBox punkRockCheckBox, rock60sCheckBox, rock70sCheckBox, rock80sCheckBox, rock90sCheckBox, rock2000sCheckBox, altRockCheckBox, rockNRollCheckBox, bluesRockCheckBox, southernRockCheckBox, hardRockCheckBox, psychedelicRockCheckBox, indieRockCheckBox;

    sub-genre checkboxes for Pop
    @FXML
    private CheckBox electropopCheckBox, synthPopCheckBox, folkPopCheckBox, teenPopCheckBox, radioPopCheckBox;

    sub-genre checkboxes for Rap
    @FXML
    private CheckBox hipHopCheckBox, trapCheckBox, oldSchoolRapCheckBox, gangstaRapCheckBox, rapRockCheckBox;

    sub-genre checkboxes for R&B
    @FXML
    private CheckBox soulCheckBox, funkCheckBox, contemporaryCheckBox, progressiveSoulCheckBox, alternativeCheckBox;

    sub-genre checkboxes for Country
    @FXML
    private CheckBox popCountryCheckBox, folkCountryCheckBox, altCountryCheckBox, outlawCountryCheckBox, honkyTonkCheckBox, bluegrassCheckBox, countryBluesCheckBox, bakersfieldSoundCheckBox;

    @FXML
    private VBox rockSubGenres, popSubGenres, rapSubGenres, rnbSubGenres, countrySubGenres;
*/

    private int userID;
    private String artistName;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    SoundScoutSQLHelper sql;

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
            //upload to Cloudinary
            this.sql.UploadToCloudinary(this.userID, filePath);
        }
    }

    /*
    toggle visibility of sub-genres
    @FXML
    private void toggleSubGenres(ActionEvent event) {
        if (event.getSource() == rockCheckBox) {
            rockSubGenres.setVisible(rockCheckBox.isSelected());
        } else if (event.getSource() == popCheckBox) {
            popSubGenres.setVisible(popCheckBox.isSelected());
        } else if (event.getSource() == rapCheckBox) {
            rapSubGenres.setVisible(rapCheckBox.isSelected());
        } else if (event.getSource() == rnbCheckBox) {
            rnbSubGenres.setVisible(rnbCheckBox.isSelected());
        } else if (event.getSource() == countryCheckBox) {
            countrySubGenres.setVisible(countryCheckBox.isSelected());
        }
    }
*/
    //collect selected genres (both main and sub-genres)
    public List<String> getSelectedGenres() {
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

        return selectedGenres;
    }

    private void showSuccessMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void returnToLoggedHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setWelcomeMessage(this.artistName, this.userID, this.lastName, this.email, this.city, this.zipCode);  //PASSES BACK the artist's name and ID
            loggedHomeController.setUserDetails(this.artistName, this.lastName, this.email, this.city, this.zipCode, this.userID);

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
    private Label successMessageLabel;

    @FXML
    private void handleUpdateGenre() {
        List<String> selectedGenres = getSelectedGenres();

        //checks if genres are selected before updating
        if (!selectedGenres.isEmpty()) {
            try {
                String genres = String.join(", ", selectedGenres);

                sql.updateArtistGenres(this.userID, genres);

                successMessageLabel.setText("Success! Genre has been updated!");
                successMessageLabel.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("Error updating the genres.");
            }
        } else {
            showErrorMessage("No genres selected.");
        }
    }

    @FXML
    private void handleUpdateRate() {
        double rate;
        try {
            rate = Double.parseDouble(rateField.getText());
            if (rate < 0) throw new NumberFormatException(); //makes sure rate is not negative
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid rate amount.");
            return;
        }

        try {
            sql.updateArtistRate(this.userID, rate);
            successMessageLabel.setText("Success! Rate has been updated!");
            successMessageLabel.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating the rate.");
        }
    }

    @FXML
    private void handleUpdateProfile() {
        if (videoField != null && videoField.getText().toLowerCase().contains("youtube")) {
            sql.UpdatePerformance(this.userID, videoField.getText());
            successMessageLabel.setText("Success! Performance URL has been updated!");
            successMessageLabel.setVisible(true);
        } else {
            showErrorMessage("Please enter a valid YouTube URL.");
        }
    }

    private void updateArtistLocation(String cityOrZipCode) {
        double[] coordinates = LocationHelper.getCoordinates(cityOrZipCode);
        try {
            sql.updateArtistLocation(this.userID, coordinates[0], coordinates[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating location.");
        }
    }

    /*@FXML
    private void handleSubmit() {
        List<String> selectedGenres = getSelectedGenres();

        double rate;
        try {
            rate = Double.parseDouble(rateField.getText());
            if (rate < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid rate amount.");
            return;
        }

        if the genres are selected, update the database
        if (!selectedGenres.isEmpty()) {
            try {
                String genres = String.join(", ", selectedGenres);
                sql.updateArtistGenres(this.userID, genres);
                sql.updateArtistRate(this.userID, rate);

                successMessageLabel.setText("Success! Profile has been updated!");
                successMessageLabel.setVisible(true);

            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("Error updating the profile.");
            }
        } else {
            showErrorMessage("No genres selected.");
        }

        if (videoField != null && videoField.getText().toLowerCase().contains("youtube")) {
            sql.UpdatePerformance(this.userID, videoField.getText());
        }
    }*/
}