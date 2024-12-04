package org.example.soundscoutfx;

import javafx.collections.ObservableList;
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
import java.time.LocalDate;
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
    private DatePicker availabilityDatePicker;

    private List<LocalDate> selectedDates = new ArrayList<>();
    private final SoundScoutSQLHelper sqlHelper = new SoundScoutSQLHelper();

    @FXML
    private void toggleFilterPane() {
        filterPane.setVisible(!filterPane.isVisible());
    }

    @FXML
    public void initialize() {
        Session session = Session.getInstance();

        sqlHelper.establishConnection();
        setWelcomeMessage(session.getUserName(), session.getUserID(), session.getLastName(), session.getEmail(), session.getCity(), session.getZipCode());
        //removed second button
        //configureEditProfileButton(session.getUserType());
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
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                Parent root = loader.load();

                DashboardController dashboardController = loader.getController();

                dashboardController.setArtistDetails(selectedArtist);

                Stage stage = (Stage) searchField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Artist Profile");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void setWelcomeMessage(String userName, int userID, String lastName, String email, String city, String zipCode) {
        welcomeLabel.setText("Welcome, " + userName + "! Your ID is: " + userID);

        /*removed second button
        if (userID == 0) {
            editProfileButton.setVisible(false);
        } else {
            editProfileButton.setVisible(true);
        }*/

    }

    /* removed second button
    private void configureEditProfileButton(String userType) {
        if ("Artist".equalsIgnoreCase(userType)) {
            editProfileButton.setText("Edit Artist Profile");
        } else {
            editProfileButton.setText("Edit User Profile");
        }
    }*/

    @FXML
    protected void handleLogout() {
        Session.getInstance().clearSession();
        navigateTo("hello-view.fxml", "SoundScout");
    }

    @FXML
    private void navigateToEditProfile() {
        Session session = Session.getInstance();

        if (session.getUserID() == 0) {
            showErrorMessage("You must sign in to access this feature.");
            return;
        }

        try {
            FXMLLoader loader;
            if ("Artist".equalsIgnoreCase(session.getUserType())) {
                loader = new FXMLLoader(getClass().getResource("edit-profile.fxml"));
            } else {
                loader = new FXMLLoader(getClass().getResource("edit-user-profile.fxml"));
            }

            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Edit Profile");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    private void NavigateToPublicEvents() {
        navigateTo("PublicEvents.fxml", "Public Events");
    }

    @FXML
    private void navigateToHome() {
        Stage currentStage = (Stage) welcomeLabel.getScene().getWindow();

        if (currentStage.getTitle().equals("Home")) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Already Here");
            alert.setHeaderText(null);
            alert.setContentText("You are already on the homepage!");
            alert.showAndWait();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController homeController = loader.getController();
            Session session = Session.getInstance();
            homeController.setWelcomeMessage(
                    session.getUserName(),
                    session.getUserID(),
                    session.getLastName(),
                    session.getEmail(),
                    session.getCity(),
                    session.getZipCode()
            );

            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Logged Home");
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void NavigateToReservations() {
        Session session = Session.getInstance();

        if (session.getUserID() == 0) {
            showErrorMessage("You must sign in to access this feature.");
            return;
        }

        navigateTo("Reservations.fxml", "Reservations");
    }


    @FXML
    private void handleAvailabilitySelect() {
        LocalDate selectedDate = availabilityDatePicker.getValue();
        if (selectedDate != null) {
            if (selectedDates.contains(selectedDate)) {
                selectedDates.remove(selectedDate);
            } else {
                selectedDates.add(selectedDate);
            }
            handleApplyFilters();
        }
    }

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

        double selectedDistance = distanceSlider.getValue();

        filterArtists(selectedGenres, maxPrice, selectedDistance);
    }

    private void filterArtists(List<String> genres, double maxPrice, double distance) {
        List<Artist> allArtists = sqlHelper.getAllArtists();

        boolean isLocationFilterActive = (distance > 0);

        List<Artist> filteredArtists = allArtists.stream()
                .filter(artist -> {
                    boolean matchesGenre = genres.isEmpty() ||
                            genres.stream().anyMatch(genre -> {
                                String artistGenre = artist.getProfile().getGenre();
                                return artistGenre != null && artistGenre.contains(genre);
                            });

                    boolean matchesPrice = artist.getProfile().getRate() <= maxPrice;

                    boolean matchesLocation = !isLocationFilterActive || isWithinDistance(artist, distance);

                    boolean isAvailableOnDates = selectedDates.isEmpty() ||
                            sqlHelper.isArtistAvailable(artist.getId(), selectedDates);

                    return matchesGenre && matchesPrice && matchesLocation && isAvailableOnDates;
                })
                .collect(Collectors.toList());

        updateArtistList(filteredArtists);
    }

    private boolean isWithinDistance(Artist artist, double distance) {
        if (distance == 0) {
            return true;
        }

        Session session = Session.getInstance();
        double userLatitude = sqlHelper.getUserLatitude(session.getUserID());
        double userLongitude = sqlHelper.getUserLongitude(session.getUserID());

        double artistLatitude = artist.getProfile().getLatitude();
        double artistLongitude = artist.getProfile().getLongitude();

        if (userLatitude == 0.0 && userLongitude == 0.0) {
            return true;
        }

        double calculatedDistance = LocationHelper.calculateDistance(userLatitude, userLongitude, artistLatitude, artistLongitude);

        return calculatedDistance <= distance;
    }

    /* removed the button
    @FXML
    private void navigateToDashboard() {
        try {
            Session session = Session.getInstance();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();

            Artist selectedArtist = searchResultsList.getSelectionModel().getSelectedItem();
            if (selectedArtist != null) {
                dashboardController.setArtistDetails(selectedArtist);
            } else if (session.getCurrentArtistID() != 0) {
                Artist artist = sqlHelper.GetArtistByID(session.getCurrentArtistID());
                if (artist != null) {
                    dashboardController.setArtistDetails(artist);
                } else {
                    dashboardController.ClearDetails();
                }
            } else {
                dashboardController.ClearDetails();
            }


            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
     */

    private void updateArtistList(List<Artist> artists) {
        searchResultsList.getItems().clear();

        if (artists != null && !artists.isEmpty()) {
            searchResultsList.getItems().addAll(artists);
            searchResultsList.setVisible(true);
        } else {
            searchResultsList.setVisible(false);
        }
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
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void updateExistingUserLocations() {
        try {
            Session session = Session.getInstance();

            String city = session.getCity();
            int userID = session.getUserID();

            if (city == null || city.isEmpty()) {
                System.err.println("City information is missing in the session.");
                return;
            }

            double[] coordinates = LocationHelper.getCoordinates(city);

            if (coordinates[0] != 0.0 && coordinates[1] != 0.0) {
                session.setLatitude(coordinates[0]);
                session.setLongitude(coordinates[1]);
                sqlHelper.updateUserLocation(userID, coordinates[0], coordinates[1]);

                System.out.println("User location updated successfully for user ID: " + userID);
            } else {
                System.err.println("Coordinates could not be resolved for the city: " + city);
            }
        } catch (SQLException e) {
            System.err.println("SQL Error while updating user location.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error occurred while updating user location.");
            e.printStackTrace();
        }
    }

}