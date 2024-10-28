package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LoggedHomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private Button editProfileButton;
    @FXML
    private TextField searchField;
    @FXML
    private ListView<Artist> searchResultsList;
    @FXML
    private CheckBox rockGenreCheckBox, popGenreCheckBox, rapGenreCheckBox, rnbCheckBox, jazzGenreCheckBox, countryCheckBox, bluesCheckBox, electronicCheckBox, indieCheckBox, alternativeCheckBox;
    @FXML
    private TextField maxPriceField;
    @FXML
    private Slider distanceSlider;
    @FXML
    private Label distanceLabel;
    @FXML
    private Button applyFiltersButton;
    @FXML
    private VBox filterPane;

    @FXML
    private void toggleFilterPane() {
        filterPane.setVisible(!filterPane.isVisible());
    }

    private SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    public void initialize() {
        //confirm the connection is established
        sqlHelper = new SoundScoutSQLHelper();
        sqlHelper.establishConnection();
        //updateExistingUserLocations();
        //updateExistingArtistLocations();

        distanceSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() == 0) {
                distanceLabel.setText("None");
            } else {
                distanceLabel.setText(newVal.intValue() + " miles");
            }
        });

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

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            searchResultsList.setVisible(false);
            return;
        }

        ObservableList<Artist> searchResults = sqlHelper.searchArtists(searchText);

        if (!searchResults.isEmpty()) {
            searchResultsList.setItems(searchResults);
            searchResultsList.setVisible(true);
        } else {
            searchResultsList.setVisible(false);
        }
    }

    @FXML
    private void handleArtistSelect(MouseEvent event) {
        Artist selectedArtist = searchResultsList.getSelectionModel().getSelectedItem();

        if (selectedArtist != null) {
            // Debug statement to check selected artist's data
            System.out.println("Selected Artist: " + selectedArtist.toString());

            // Navigate to artist profile with selected artist's details
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();

                // Set the artist details in the Dashboard
                dashboardController.setArtistDetails(selectedArtist);

                // Pass the logged-in user's information to the Dashboard
                dashboardController.setUserID(this.userID);
                dashboardController.setUserName(this.artistName);
                dashboardController.setUserType(this.userType);

                Stage stage = (Stage) searchField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Artist Profile");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int userID;
    private String userType;
    private String artistName;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;

    public void setWelcomeMessage(String firstName, int userID) {
        System.out.println("setWelcomeMessage called with: " + firstName + ", ID: " + userID);

        this.artistName = firstName;
        this.userID = userID;
        welcomeLabel.setText("Welcome, " + firstName + "! Your ID is: " + userID);

    //hide the "Edit Artist Profile" button if the user is a guest (ID = 0)
        if (userID == 0) {
            editProfileButton.setVisible(false);
        } else {
            editProfileButton.setVisible(true);
        }
    }

    private void showErrorMessage(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }


    public void setUserType(String userType) {
        this.userType = userType;
        configureButtonBasedOnUserType();
    }

    public void configureButtonBasedOnUserType() {
        if ("Artist".equalsIgnoreCase(userType)) {
            editProfileButton.setText("Edit Artist Profile");
        } else {
            editProfileButton.setText("Edit User Profile");
        }
    }

    @FXML
    protected void handleLogout() {
        navigateTo("hello-view.fxml", "SoundScout");
    }

    @FXML
    protected void handleAvailableArtistsClick() {
        navigateTo("Dashboard.fxml", "Available Artists");
    }

    @FXML
    protected void navigateToEditProfile() {
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
                editUserProfileController.setUserDetails(this.artistName, this.lastName, this.email, this.city, this.zipCode, this.userID);
            }

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    private void navigateTo(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        System.out.println("setUserDetails called with: " + firstName + ", " + lastName + ", " + email + ", " + city + ", " + zipCode + ", ID: " + userID);

        this.artistName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
        this.userID = userID;

        welcomeLabel.setText("Welcome, " + firstName + "! Your ID is: " + userID);
    }

    @FXML
    private void navigateToDashboard() {
        try {
            //System.out.println("Navigating to Dashboard with userType: " + this.userType);  //debug statement

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);
            dashboardController.setUserName(this.artistName);
            dashboardController.setUserType(this.userType);

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateArtistList(List<Artist> artists) {
        //clear existing items in the list
        searchResultsList.getItems().clear();

        //add new filtered artists
        if (artists != null && !artists.isEmpty()) {
            searchResultsList.getItems().addAll(artists);
            searchResultsList.setVisible(true);
        } else {
            searchResultsList.setVisible(false); //hide if no results
        }
    }

    private void filterArtists(List<String> genres, double maxPrice, double distance) {
        List<Artist> allArtists = sqlHelper.getAllArtists();

        System.out.println("All Artists Retrieved: " + allArtists.size());

        boolean isLocationFilterActive = (distance > 0);

        List<Artist> filteredArtists = allArtists.stream()
                .filter(artist -> {
                    boolean matchesGenre = genres.isEmpty() ||
                            genres.stream().anyMatch(genre -> {
                                String artistGenre = artist.getProfile().getGenre();
                                System.out.println("Artist Genre: " + artistGenre + ", Selected Genre: " + genre);
                                return artistGenre != null && artistGenre.contains(genre);
                            });

                    boolean matchesPrice = artist.getProfile().getRate() <= maxPrice;

                    boolean matchesLocation = !isLocationFilterActive || isWithinDistance(artist, distance);

                    return matchesGenre && matchesPrice && matchesLocation;
                })
                .collect(Collectors.toList());

        System.out.println("Filtered Artists Count: " + filteredArtists.size());

        updateArtistList(filteredArtists);
    }

    private boolean isWithinDistance(Artist artist, double distance) {
        //if distance slider is set to 0, ignore distance filtering
        if (distance == 0) {
            System.out.println("Distance Filter is set to 'None'. Skipping distance filtering.");
            return true; //consider all locations valid
        }

        double userLatitude = sqlHelper.getUserLatitude(this.userID);
        double userLongitude = sqlHelper.getUserLongitude(this.userID);

        double artistLatitude = artist.getProfile().getLatitude();
        double artistLongitude = artist.getProfile().getLongitude();

        if (userLatitude == 0.0 && userLongitude == 0.0) {
            System.out.println("User location not set properly. Ignoring distance filter.");
            return true;
        }

        double calculatedDistance = LocationHelper.calculateDistance(userLatitude, userLongitude, artistLatitude, artistLongitude);

        System.out.println("User Location: (" + userLatitude + ", " + userLongitude + ")");
        System.out.println("Artist Location: (" + artistLatitude + ", " + artistLongitude + ")");
        System.out.println("Calculated Distance to Artist: " + calculatedDistance + " miles");

        return calculatedDistance <= distance;
    }

    /*private double getUserLatitude() {
        return sqlHelper.getUserLatitude(this.userID);
    }

    private double getUserLongitude() {
        return sqlHelper.getUserLongitude(this.userID);
    }

    private void updateExistingArtistLocations() {
        List<Artist> artists = sqlHelper.getAllArtists();
        for (Artist artist : artists) {
            try {
                String city = artist.getCity();
                String state = artist.getState();
                String zipCode = artist.getZipCode();

                if (city == null || city.trim().isEmpty() ||
                        state == null || state.trim().isEmpty() ||
                        zipCode == null || zipCode.trim().isEmpty()) {
                    continue;
                }

                String location = city + ", " + state + " " + zipCode;

                double[] coordinates = LocationHelper.getCoordinates(location);

                if (coordinates[0] != 0.0 && coordinates[1] != 0.0) {
                    artist.getProfile().setLatitude(coordinates[0]);
                    artist.getProfile().setLongitude(coordinates[1]);

                    sqlHelper.updateArtistLocation(artist.getId(), coordinates[0], coordinates[1]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void updateExistingUserLocations() {
        List<UserInfo> users = sqlHelper.getAllUsers();
        for (UserInfo user : users) {
            try {
                String location = user.getCity();
                double[] coordinates = LocationHelper.getCoordinates(location);

                if (coordinates[0] != 0.0 && coordinates[1] != 0.0) {
                    user.setLatitude(coordinates[0]);
                    user.setLongitude(coordinates[1]);
                    sqlHelper.updateUserLocation(user.getId(), user.getLatitude(), user.getLongitude());
                }
            } catch (SQLException e) {
                System.err.println("SQL Error while updating user location for user ID: " + user.getId());
                e.printStackTrace();
            } catch (Exception e) {
                System.err.println("General error while updating user location for user ID: " + user.getId());
                e.printStackTrace();
            }
        }
    }
*/
    @FXML
    private void handleApplyFilters() {
        List<String> selectedGenres = new ArrayList<>();
        if (rockGenreCheckBox.isSelected()) selectedGenres.add("Rock");
        if (popGenreCheckBox.isSelected()) selectedGenres.add("Pop");
        if (rapGenreCheckBox.isSelected()) selectedGenres.add("Rap");
        if (jazzGenreCheckBox.isSelected()) selectedGenres.add("Jazz");
        if (rnbCheckBox.isSelected()) selectedGenres.add("RnB");
        if (countryCheckBox.isSelected()) selectedGenres.add("Country");
        if (bluesCheckBox.isSelected()) selectedGenres.add("Blues");
        if (electronicCheckBox.isSelected()) selectedGenres.add("Electronic");
        if (indieCheckBox.isSelected()) selectedGenres.add("Indie");
        if (alternativeCheckBox.isSelected()) selectedGenres.add("Alternative");

        double maxPrice = Double.MAX_VALUE;
        if (!maxPriceField.getText().isEmpty()) {
            try {
                maxPrice = Double.parseDouble(maxPriceField.getText());
            } catch (NumberFormatException e) {
                showErrorMessage("Please enter a valid price.");
                return;
            }
        }

        //gets distance ONLY if the slider is moved
        double selectedDistance = distanceSlider.getValue();

        //calls the filter method
        filterArtists(selectedGenres, maxPrice, selectedDistance);
    }
}