package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.example.soundscoutfx.Session.session;

public class DashboardController {

    // JFX nodes
    @FXML
    private DatePicker datePicker;
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
    @FXML
    private TextArea artistBioTextArea;

    // Member variables
    private final SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();
    private List<LocalDate> reservationDates = new ArrayList<>();

    @FXML
    public void initialize() {
        sqlHelper.establishConnection();
        searchResultsList.setVisible(false);

        searchResultsList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Artist artist, boolean empty) {
                super.updateItem(artist, empty);
                if (empty || artist == null) {
                    setText(null);
                } else {
                    setText(String.format("%s | Genre: %s | Rate: $%.2f | City: %s",
                            artist.getStageName(),
                            artist.getProfile().getGenre(),
                            artist.getProfile().getRate(),
                            artist.getCity()));
                }
            }
        });
    }

    /** Handles search functionality */
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            searchResultsList.setVisible(false);
            return;
        }

        ObservableList<Artist> searchResults = FXCollections.observableArrayList(sqlHelper.searchArtists(searchText));
        updateArtistList(searchResults);
    }

    /** Updates the artist list */
    private void updateArtistList(ObservableList<Artist> artists) {
        searchResultsList.getItems().clear();
        if (artists != null && !artists.isEmpty()) {
            searchResultsList.getItems().addAll(artists);
            searchResultsList.setVisible(true);
        } else {
            searchResultsList.setVisible(false);
        }
    }

    /** Handles artist selection */
    @FXML
    private void handleArtistSelect(MouseEvent event) {
        Artist selectedArtist = searchResultsList.getSelectionModel().getSelectedItem();
        if (selectedArtist != null) {
            setArtistDetails(selectedArtist);
        }
    }

    /** Sets artist details in the dashboard */
    public void setArtistDetails(Artist artist) {
        int currentArtistID = artist.getId();
        nameField.setText(artist.getStageName());
        joinDateField.setText(artist.getJoinDate());
        genreField.setText(artist.getProfile().getGenre());
        rateField.setText(String.valueOf(artist.getProfile().getRate()));
        locationField.setText(artist.getCity());

        try {
            String artistBio = sqlHelper.getArtistBio(currentArtistID);
            artistBioTextArea.setText(artistBio != null ? artistBio : "No bio listed");
        } catch (SQLException e) {
            artistBioTextArea.setText("Error loading bio.");
        }

        String profilePicture = artist.getProfile().getProfilePicture();
        Image defaultImage = new Image("https://asset.cloudinary.com/dbvmjemlj/3b6de659993c8001449604d2985bcf4f");
        if (profilePicture != null && !profilePicture.isEmpty()) {
            Image image = new Image(profilePicture, false);
            imgView.setImage(image.isError() ? defaultImage : image);
        } else {
            imgView.setImage(defaultImage);
        }

        String video = artist.getProfile().getFeaturedPerformance();
        if (video != null && !video.isEmpty()) {
            webView.getEngine().load(video.replace("watch?v=", "embed/"));
        } else {
            webView.getEngine().load(null);
        }

        reservationDates.clear();
        for (Reservation reservation : sqlHelper.getAllReservations()) {
            if (reservation.getArtistID() == currentArtistID) {
                reservationDates.add(LocalDate.parse(reservation.getDate()));
            }
        }

        reserveTitle.setVisible(true);
        searchResultsList.setVisible(false);
    }

    /** Handles navigation to reservations */
    @FXML
    private void NavigateToReservations() {
        Session session = Session.getInstance();
        if (IsGuestUser()) {
            showErrorMessage("You must sign in to access this feature.");
            return;
        }

        Navigate("Reservations.fxml", "Reservations");
    }

    @FXML
    private void navigateToProfile() {
        if (IsGuestUser()) {
            showErrorMessage("You must sign in to access this feature.");
            return;
        }
        Navigate("edit-profile.fxml", "Edit Profile");
    }

    @FXML
    private void NavigateToPublicEvents() {
        if (IsGuestUser()) {
            showErrorMessage("You must sign in to access public events.");
            return;
        }
        Navigate("PublicEvents.fxml", "Public Events");
    }

    @FXML
    private void navigateToHome() {
        Navigate("logged-home.fxml", "Home");
    }

    /** Displays an error message */
    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void ReloadDashboard() {
        searchResultsList.refresh();
        reserveTitle.setText("Updated Reservations");
    }

    /** General navigation method to remove redundancy */
    private void Navigate(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String formatDateTime(String rawDateTime) {
        if (rawDateTime == null || rawDateTime.isEmpty()) {
            return "Unknown";
        }
        try {
            LocalDateTime utcDateTime = LocalDateTime.parse(rawDateTime);
            ZonedDateTime localDateTime = utcDateTime.atZone(ZoneId.of("UTC"))
                    .withZoneSameInstant(ZoneId.systemDefault());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a");
            return localDateTime.format(formatter);
        } catch (Exception e) {
            e.printStackTrace();
            return "Invalid Date";
        }
    }

    @FXML
    private void viewCancellationPolicy() {
        Session session = Session.getInstance();
        int currentArtistID = session.getCurrentArtistID();

        if (currentArtistID == 0) {
            showErrorMessage("You must select an artist to view the cancellation policy.");
            return;
        }

        try {
            String[] policyInfo = sqlHelper.getArtistCancellationPolicy(currentArtistID);
            String policy = policyInfo[0];
            String updatedAt = policyInfo[1];

            if (policy == null || policy.isEmpty()) {
                showErrorMessage("No cancellation policy found for this artist.");
                return;
            }

            String formattedDateTime = formatDateTime(updatedAt);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("cancellation-policy-popup.fxml"));
            Parent root = loader.load();

            CancellationPolicyPopupController popupController = loader.getController();
            popupController.setPolicyData(policy, formattedDateTime);

            Stage popupStage = new Stage();
            popupStage.setTitle("Cancellation Policy");
            popupStage.setScene(new Scene(root));
            popupStage.showAndWait();

        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showErrorMessage("Failed to retrieve the cancellation policy.");
        }
    }

    @FXML
    void DisplayReservationPopup() {
        Session session = Session.getInstance();
        int userID = session.getUserID();
        int currentArtistID = session.getCurrentArtistID();
        String currentArtistStageName = session.getCurrentArtistStageName();

        if (userID == 0) {
            showErrorMessage("You must sign in to access this feature.");
            return;
        }

        if (currentArtistID == 0 || currentArtistStageName == null) {
            showErrorMessage("You must select an artist to continue.");
            return;
        }

        try {
            String selectedDate = String.valueOf(datePicker.getValue());
            if (selectedDate == null || selectedDate.equals("null")) {
                showErrorMessage("You must select a date to continue.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InputReservationInfo.fxml"));
            Parent root = loader.load();

            ReservationDescriptionInputController resControl = loader.getController();
            resControl.setSelectedDate(selectedDate);
            resControl.setReservationDates(reservationDates);
            resControl.setDashboardController(this);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void ClearDetails() {
        nameField.setText("");
        joinDateField.setText("");
        genreField.setText("");
        rateField.setText("");
        locationField.setText("");
        artistBioTextArea.setText("");
        imgView.setImage(null);
        imgView.setVisible(false);
        webView.getEngine().load(null);
        webView.setVisible(false);
        reserveTitle.setVisible(false);
        reservationDates.clear();
    }

    private boolean IsGuestUser() {
        Session session = Session.getInstance();
        return "Guest".equalsIgnoreCase(session.getUserType());
    }

}
