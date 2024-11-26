package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import org.controlsfx.control.tableview2.filter.filtereditor.SouthFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DashboardController {
    SoundScoutSQLHelper sql;
    private int userID;
    private int currentArtistID;

    private String artistName;
    private String userName;
    private String userType;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    private List<Reservation> reservationStringList;
    private List<LocalDate> reservationDates = new ArrayList<>();
    private DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private String selectedDate;

    private String startTime = "";
    private String duration = "";
    private String venueType = "";
    private String address = "";
    private String description = "";

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label welcomeLabel;

    @FXML
    DatePicker datePicker = new DatePicker();

    @FXML
    private TextField searchField;

    @FXML
    private ListView<Artist> searchResultsList;

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

    @FXML
    private Label reserveTitle;

    private final SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @FXML
    public void initialize() {
        if (webView != null) {
            WebEngine engine = webView.getEngine();
        }

        sql = new SoundScoutSQLHelper();
        sql.establishConnection();
        sql.testConnection();

        reservationStringList = sql.getAllReservations();

        for (Reservation reservation : reservationStringList) {
            LocalDate tempDate = LocalDate.parse(reservation.getDate(), dateFormatter);
            reservationDates.add(tempDate);
        }

        searchResultsList.setVisible(false);

        searchResultsList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Artist artist, boolean empty) {
                super.updateItem(artist, empty);
                if (empty || artist == null) {
                    setText(null);
                } else {
                    String displayText = String.format("%s | Genre: %s | Rate: $%.2f | City: %s",
                            artist.getStageName(),
                            artist.getProfile().getGenre(),
                            artist.getProfile().getRate(),
                            artist.getCity());
                    setText(displayText);
                }
            }
        });
    }


    public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        this.userID = userID;
        this.userName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
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

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Access Denied");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void navigateToProfile() {
        //if Guest account
        if (this.userID == 0) {
            showErrorMessage("You must sign in to access this feature.");
            return; //can't proceed
        }

        try {
            FXMLLoader loader;

            //determine which profile page to load based on user type
            if ("Artist".equalsIgnoreCase(userType)) {
                loader = new FXMLLoader(getClass().getResource("edit-profile.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("edit-user-profile.fxml"));
            }

            Parent root = loader.load();

            if ("Artist".equalsIgnoreCase(userType)) {
                EditProfileController editProfileController = loader.getController();
                editProfileController.setConnection(this.sqlHelper);
                editProfileController.setArtistDetails(this.artistName, this.userID);
            } else {
                EditUserProfileController editUserProfileController = loader.getController();
                editUserProfileController.setConnection(this.sqlHelper);
                editUserProfileController.setUserDetails(this.userName, this.lastName, this.email, this.city, this.zipCode, this.userID);
            }

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
        System.out.println("Artist Name (First Name): " + userName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.setWelcomeMessage(this.userName, this.userID);
            loggedHomeController.setUserDetails(this.userName, this.lastName, this.email, this.city, this.zipCode, this.userID);

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
        currentArtistID = (artist.getId());
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

        reservationDates.clear();
        for (Reservation reservation : reservationStringList) {
            if (reservation.getArtistID() == currentArtistID) {
                LocalDate tempDate = LocalDate.parse(reservation.getDate(), dateFormatter);
                reservationDates.add(tempDate);
            }
        }

        setCalendarReservations();

        reserveTitle.setVisible(true);
    }

    public void clearArtistDetails() {
        nameField.setText("");
        joinDateField.setText("");
        genreField.setText("");
        rateField.setText("");
        locationField.setText("");
        imgView.setImage(null);
        webView.getEngine().load(null);
        reserveTitle.setVisible(false); // Hide the title
    }


    //https://docs.oracle.com/javase/8/javafx/user-interface-tutorial/date-picker.htm#CCHHJBEA Implementing a Day Cell Factory to Disable Some Days
    public void setCalendarReservations() {
        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);

                        if (!empty && item != null && reservationDates.contains(item) || Objects.requireNonNull(item).isBefore(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: grey; -fx-text-fill: white;");
                        }
                    }
                };
            }
        };

        datePicker.setDayCellFactory(dayCellFactory);

    }

    @FXML
    private void NavigateToReservations() {
        if (this.userID == 0) {
            showErrorMessage("You must sign in to access this feature.");
            return; //can't proceed
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Reservations.fxml"));
            Parent root = loader.load();

            ReservationController reservationController = loader.getController();

            if (Objects.equals(userType, "Artist")) {
                reservationController.SetUserType(this.userType);
                reservationController.SetArtistID(this.currentArtistID);
            } else if (Objects.equals(userType, "User")) {
                reservationController.SetUserType(this.userType);
                reservationController.SetUserID(this.userID);
                reservationController.setUserDetails(this.userName, this.lastName, this.email, this.city, this.zipCode, this.userID);
            }

            reservationController.initializeReservations();


            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void SetArtistID(int artistID) {
        this.currentArtistID = artistID;
    }

    @FXML
    void DisplayReservationPopup() {
        try {
            if (userID != 0) {
                try {
                    selectedDate = String.valueOf(datePicker.getValue());
                } catch (Exception e) {
                    System.out.println("ERROR: DATE HAS NOT BEEN SELECTED");
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("InputReservationInfo.fxml"));
                Parent root = loader.load();

                ReservationDescriptionInputController resControl = loader.getController();

                resControl.setSql(sql);
                resControl.setSelectedDate(selectedDate);
                resControl.setCurrentArtistID(currentArtistID);
                resControl.setCurrentArtistStageName(artistName);
                resControl.setUserID(userID);
                resControl.setReservationDates(reservationDates);
                resControl.setDashboardController(this);
                resControl.SetLabel();

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void ReloadDashboard() {
        reservationStringList = sql.getAllReservations();
        reservationDates.clear();

        for (Reservation reservation : reservationStringList) {
            LocalDate tempDate = LocalDate.parse(reservation.getDate(), dateFormatter);
            if (!reservationDates.contains(tempDate)) {
                reservationDates.add(tempDate);
            }
        }

        setCalendarReservations();
    }

}

