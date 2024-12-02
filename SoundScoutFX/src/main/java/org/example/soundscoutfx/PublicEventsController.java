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

public class PublicEventsController {
    SoundScoutSQLHelper sql;

    ObservableList<Reservation> reservationObservableList;
    @FXML
    private ListView<Reservation> eventsView;
    @FXML
    TextField searchField;
    private int globalSelectedIndex = -1;


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

    @FXML
    public void SetIndex() {
        globalSelectedIndex = eventsView.getSelectionModel().getSelectedIndex();
        System.out.println(globalSelectedIndex);
    }
}

