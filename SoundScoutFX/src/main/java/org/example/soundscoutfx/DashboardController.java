package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class DashboardController {
    SoundScoutSQLHelper sql;
    private int userID;
    private String artistName;
    private String userName;
    private String userType;

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Artist> searchResultsList;

    @FXML
    private ListView<Artist> listView;

    @FXML
    private Text nameField;

    @FXML
    private Text joinDateField;

    @FXML
    private Text genreField;

    @FXML
    private Text rateField;

    @FXML
    private Text locationField;

    @FXML
    private ImageView imgView;

    @FXML
    private WebView webView;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @FXML
    public void initialize() {
        if (webView != null) {
            WebEngine engine = webView.getEngine();
        }

        //establish SQL connection
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();
        sql.testConnection();

        searchResultsList.setVisible(false);
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            searchResultsList.setVisible(false);
            return;
        }

        ObservableList<Artist> searchResults = FXCollections.observableArrayList(sql.searchArtists(searchText));
        updateArtistList(searchResults);
    }

    private void updateArtistList(ObservableList<Artist> artists) {
        searchResultsList.getItems().clear();
        if (artists != null && !artists.isEmpty()) {
            searchResultsList.getItems().addAll(artists);
            searchResultsList.setVisible(true);
        } else {
            searchResultsList.setVisible(false);
        }
    }

    @FXML
    private void handleArtistSelect(MouseEvent event) {
        Artist selectedArtist = searchResultsList.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            setArtistDetails(selectedArtist);
        }
    }

    @FXML
    private void navigateToProfile() {
        try {
            //load edit-profile.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit-profile.fxml"));
            Parent root = loader.load();

            EditProfileController editProfileController = loader.getController();
            editProfileController.setUserID(this.userID);
            editProfileController.setConnection(this.sql);

            //switch to new scene
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void navigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setWelcomeMessage(this.userName, this.userID);
            loggedHomeController.setUserDetails(this.userName, null, null, null, null, this.userID);

            loggedHomeController.setUserType(this.userType);

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setArtistDetails(Artist artist) {
        //populate the fields in the Dashboard with the selected artist's details
        nameField.setText(artist.getStageName());
        joinDateField.setText(artist.getJoinDate());
        genreField.setText(artist.getProfile().getGenre());
        rateField.setText(String.valueOf(artist.getProfile().getRate()));
        searchField.clear();
        searchResultsList.setVisible(false);

        String locationText = artist.getCity() + ", " + artist.getZipCode();
        locationField.setText(locationText);

        String profilePicture = artist.getProfile().getProfilePicture();
        String video = artist.getProfile().getFeaturedPerformance();

        //load profile picture
        Image defaultImage = new Image("https://asset.cloudinary.com/dbvmjemlj/3b6de659993c8001449604d2985bcf4f");
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Image image = new Image(profilePicture, false);
            imgView.setImage(image.isError() ? defaultImage : image);
        } else {
            imgView.setImage(defaultImage);
        }

        //load video
        if (video != null && !video.isEmpty()) {
            String youtubeURL = video.replace("watch?v=", "embed/");
            webView.getEngine().load(youtubeURL);
        } else {
            webView.getEngine().load(null);
        }
    }

}