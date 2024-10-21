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
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.IOException;

public class DashboardController {
    SoundScoutSQLHelper sql;
    private int userID;
    private String artistName;
    private String userName;

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    private String userType;

    public void setUserType(String userType) {
        this.userType = userType;
    }

    @FXML
    public void initialize() {
        //establish SQL connection
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();
        sql.testConnection();

        //populate observable list with artist list data
        ObservableList<Artist> artistList = FXCollections.observableArrayList(sql.GetDBArtistsProfiles());
        listView.setItems(artistList);
    }

    @FXML
    private ListView<Artist> listView;

    @FXML
    private TextField nameField;

    @FXML
    private TextField joinDateField;

    @FXML
    private TextField idField;

    @FXML
    private TextField genreField;

    @FXML
    private ImageView imgView;

    @FXML
    private MediaView mediaView;

    @FXML
    private WebView webView;

    /** Handles list selection when user clicks an artist; all other fields become populated with the artist's information. */
    @FXML
    public void handleMouseClick(MouseEvent event) {
        ObservableList<Artist> items = listView.getItems();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        Image defaultImage = new Image("https://asset.cloudinary.com/dbvmjemlj/3b6de659993c8001449604d2985bcf4f");

        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            Artist selectedArtist = items.get(selectedIndex);
            nameField.setText(selectedArtist.getStageName());
            joinDateField.setText(selectedArtist.getJoinDate());
            genreField.setText(selectedArtist.getProfile().getGenere());
            String profilePicture = selectedArtist.getProfile().getProfilePicture();
            String video = selectedArtist.getProfile().getFeaturedPerformance();

            if (profilePicture != null && !profilePicture.isEmpty()) {
                Image image = new Image(profilePicture, false);
                if (image.isError()) {
                    System.out.println("Error loading image: " + image.getException());
                } else {
                    imgView.setImage(image);
                }

                imgView.setImage(image);
            } else {
                imgView.setImage(defaultImage);
            }

            if (video != null && !video.isEmpty()) {
                String youtubeURL = video.replace("watch?v=", "embed/");
                webView.getEngine().load(youtubeURL);
            } else {
                webView.getEngine().load(null);
            }
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
            Stage stage = (Stage) listView.getScene().getWindow();
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

            loggedHomeController.setUserType(this.userType);

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}