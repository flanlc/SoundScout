package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

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
    @FXML
    private TextArea bioField;
    @FXML
    private Label charCountLabel;
    @FXML
    private TextArea cancellationPolicyField;

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

    @FXML
    public void initialize() {
        bioField.textProperty().addListener((observable, oldValue, newValue) -> {
            int currentLength = newValue.length();
            charCountLabel.setText(currentLength + "/150 Characters");

            if (currentLength > 150) {
                bioField.setText(oldValue);
                charCountLabel.setText("150/150 Characters");
            }
        });

        Session session = Session.getInstance();
        if (session.getSql() != null && session.getUserID() > 0) {
            loadBio();
            loadCancellationPolicy();
            loadRate();
            loadGenres();
        }
    }


    public void setArtistDetails(String artistName, int userID) {
        Session session = Session.getInstance();
        session.setUserName(artistName);
        session.setUserID(userID);
        loadBio();
        loadCancellationPolicy();
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
            Session session = Session.getInstance();
            session.getSql().UploadToCloudinary(session.getUserID(), filePath);
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
            Session session = Session.getInstance();

            loggedHomeController.setWelcomeMessage(
                    session.getUserName(),
                    session.getUserID(),
                    session.getLastName(),
                    session.getEmail(),
                    session.getCity(),
                    session.getZipCode()
            );

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

        if (!selectedGenres.isEmpty()) {
            try {
                String genres = String.join(", ", selectedGenres);

                Session session = Session.getInstance();
                session.getSql().updateArtistGenres(session.getUserID(), genres);

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
            if (rate < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showErrorMessage("Please enter a valid rate amount.");
            return;
        }

        try {
            Session session = Session.getInstance();
            session.getSql().updateArtistRate(session.getUserID(), rate);

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
            Session session = Session.getInstance();
            session.getSql().UpdatePerformance(session.getUserID(), videoField.getText());
            successMessageLabel.setText("Success! Performance URL has been updated!");
            successMessageLabel.setVisible(true);
        } else {
            showErrorMessage("Please enter a valid YouTube URL.");
        }
    }

    private void updateArtistLocation(String cityOrZipCode) {
        double[] coordinates = LocationHelper.getCoordinates(cityOrZipCode);
        try {
            Session session = Session.getInstance();
            session.getSql().updateArtistLocation(session.getUserID(), coordinates[0], coordinates[1]);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating location.");
        }
    }

    @FXML
    public void handleUpdateBio() {
        String bio = bioField.getText();

        if (bio.length() > 150) {
            showErrorMessage("Bio must be 150 characters or less.");
            return;
        }

        try {
            Session session = Session.getInstance();
            session.getSql().updateArtistBio(session.getUserID(), bio);

            showSuccessMessage("Bio updated successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error updating bio.");
        }
    }

    @FXML
    private void loadBio() {
        Session session = Session.getInstance();
        if (session.getSql() == null) {
            System.out.println("Error: SQL Helper is not initialized.");
            return;
        }

        try {
            String bio = session.getSql().getArtistBio(session.getUserID());
            if (bio != null) {
                bioField.setText(bio);
            } else {
                bioField.setText("");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading bio.");
        }
    }

    @FXML
    private void loadRate() {
        try {
            Session session = Session.getInstance();
            double rate = session.getSql().getArtistRate(session.getUserID());
            rateField.setText(String.valueOf(rate)); // Set the rate in the TextField
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading rate.");
        }
    }

    @FXML
    private void loadGenres() {
        try {
            Session session = Session.getInstance();
            String genres = session.getSql().getArtistGenres(session.getUserID()); // Get genres as a comma-separated string
            if (genres != null && !genres.isEmpty()) {
                String[] genreArray = genres.split(",\\s*"); // Split by comma and optional spaces

                for (String genre : genreArray) {
                    switch (genre.trim().toLowerCase()) {
                        case "rock":
                            rockCheckBox.setSelected(true);
                            break;
                        case "pop":
                            popCheckBox.setSelected(true);
                            break;
                        case "rap":
                            rapCheckBox.setSelected(true);
                            break;
                        case "rnb":
                            rnbCheckBox.setSelected(true);
                            break;
                        case "country":
                            countryCheckBox.setSelected(true);
                            break;
                        case "blues":
                            bluesCheckBox.setSelected(true);
                            break;
                        case "electronic":
                            electronicCheckBox.setSelected(true);
                            break;
                        case "jazz":
                            jazzCheckBox.setSelected(true);
                            break;
                        case "indie":
                            indieCheckBox.setSelected(true);
                            break;
                        case "alternative":
                            alternativeCheckBox.setSelected(true);
                            break;
                        default:
                            System.out.println("Unknown genre: " + genre);
                            break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading genres.");
        }
    }



    /*@FXML
    private void openCancellationPolicyEditor() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Edit Cancellation Policy");
        dialog.setHeaderText("Update your cancellation policy");
        dialog.setContentText("Enter your new policy:");

        dialog.showAndWait().ifPresent(newPolicy -> {
            try {
                sql.updateArtistCancellationPolicy(this.userID, newPolicy);
                showSuccessMessage("Cancellation policy updated successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorMessage("Failed to update the cancellation policy.");
            }
        });
    }*/

    @FXML
    private void handleUpdateCancellationPolicy() {
        String newPolicy = cancellationPolicyField.getText();
        try {
            Session session = Session.getInstance();
            session.getSql().updateArtistCancellationPolicy(session.getUserID(), newPolicy);
            showSuccessMessage("Cancellation policy updated successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Failed to update the cancellation policy.");
        }
    }

    @FXML
    private void loadCancellationPolicy() {
        try {
            Session session = Session.getInstance();
            String[] policyInfo = session.getSql().getArtistCancellationPolicy(session.getUserID());
            cancellationPolicyField.setText(policyInfo != null ? policyInfo[0] : "");
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorMessage("Error loading cancellation policy.");
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