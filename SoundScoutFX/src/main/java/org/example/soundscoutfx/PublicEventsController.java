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

    //fxml nodes
    @FXML
    private ListView<Reservation> eventsView;
    @FXML
    TextField searchField;


    /** Initialize method */
    @FXML
    public void initialize() {
        SoundScoutSQLHelper sql = Session.getInstance().getSql();
        List<Reservation> tempReservations = sql.getAllReservations();

        List<Reservation> filteredReservations = tempReservations.stream()
                .filter(reservation -> "Active".equals(reservation.getActiveStatus()) &&
                        "Public".equals(reservation.getVenueType()))
                .toList();

        reservationObservableList = FXCollections.observableList(filteredReservations);

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

        ObservableList<Reservation> filteredReservations = reservationObservableList.filtered(reservation ->
                reservation.getStageName().toUpperCase().contains(searchText.toUpperCase()) &&
                        reservation.getActiveStatus().equals("Active") &&
                        reservation.getVenueType().equals("Public")
        );

        eventsView.setItems(filteredReservations);
    }

    /** Displays event description popup */
    @FXML
    private void DisplayEventDescription() {
        try {
            if (globalSelectedIndex == -1) {
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("PublicResDetails.fxml"));
            Parent root = loader.load();

            PublicResDetailsController resDetailsController = loader.getController();

            Reservation selectedReservation = eventsView.getItems().get(globalSelectedIndex);

            resDetailsController.setStageName(selectedReservation.getStageName());
            resDetailsController.setDate(selectedReservation.getDate());
            resDetailsController.setStartTime(selectedReservation.getStartTime());
            resDetailsController.setDuration(selectedReservation.getDuration());
            resDetailsController.setVenueType(selectedReservation.getVenueType());
            resDetailsController.setAddress(selectedReservation.getAddress());
            resDetailsController.setDescription(selectedReservation.getDescription());
            resDetailsController.Populate();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Upon listview selection update variable with index location of selected event */
    @FXML
    public void SetIndex() {
        globalSelectedIndex = eventsView.getSelectionModel().getSelectedIndex();
    }

    /** Load home page */
    @FXML
    private void NavigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.initialize();

            Stage stage = (Stage) searchField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}