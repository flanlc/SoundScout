package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/** Public Events FXML controller class */
public class PublicEventsController {
    //member variables
    SoundScoutSQLHelper sql;
    ObservableList<Reservation> reservationObservableList;
    private int globalSelectedIndex = -1;

    private String userName;
    private int userID;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    private String userType;

    //fxml nodes
    @FXML
    private ListView<Reservation> eventsView;
    @FXML
    TextField searchField;


    /** Initialize method */
    @FXML
    public void initialize() {
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();
        List<Reservation> tempRes = sql.getAllReservations();
        reservationObservableList = FXCollections.observableList(tempRes);

        eventsView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation reservation, boolean empty) {
                super.updateItem(reservation, empty);
                if (empty || reservation == null) {
                    setText(null);
                } else {
                    String displayText = String.format("%s | Date: %s",
                            reservation.getStageName(),
                            reservation.getDate());
                    setText(displayText);
                }
            }
        });

        eventsView.setItems(reservationObservableList);
    }

    /** Handles search feature */
    @FXML
    private void HandleSearch() {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            eventsView.setItems(reservationObservableList);
            return;
        }

        //filters artist list based on search text
        ObservableList<Reservation> filteredArtistsList = reservationObservableList.filtered(reservation ->
                reservation.getStageName().toUpperCase().contains(searchText.toUpperCase()) &&
                        reservation.getActiveStatus().equals("Active") &&
                        reservation.getVenueType().equals("Public")
        );

        eventsView.setItems(filteredArtistsList);
    }

    /** Displays event description popup */
    @FXML
    private void DisplayEventDescription() {

        try {
            if(globalSelectedIndex == -1) {
                return; //returns if no event selected
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PublicResDetails.fxml"));
            Parent root = loader.load();

            PublicResDetailsController resControl = loader.getController();

            Reservation selectedReservation = eventsView.getItems().get(globalSelectedIndex);

            resControl.setStageName(selectedReservation.getStageName());
            resControl.setDate(selectedReservation.getDate());
            resControl.setStartTime(selectedReservation.getStartTime());
            resControl.setDuration(selectedReservation.getDuration());
            resControl.setVenueType(selectedReservation.getVenueType());
            resControl.setAddress(selectedReservation.getAddress());
            resControl.setDescription(selectedReservation.getDescription());

            resControl.Populate();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /** Upon listview selection update variable with index location of selected event */
    @FXML
    public void SetIndex() {
        globalSelectedIndex = eventsView.getSelectionModel().getSelectedIndex();
        System.out.println(globalSelectedIndex);
    }

    /** Load home page */
    @FXML
    private void NavigateToHome() {
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

    /** Populates User Details */
    public void SetUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        this.userName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
        this.userID = userID;
    }
}

