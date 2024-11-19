package org.example.soundscoutfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ReservationController {
    private int artistID;
    private int userID;
    private String userType;
    private SoundScoutSQLHelper sql = new SoundScoutSQLHelper();
    private int reservationID = 0;
    private String activeStatus = null;
    List<Reservation> activeResList = new ArrayList<>();
    List<Reservation> pendingResList = new ArrayList<>();
    List<Reservation> cancelledResList = new ArrayList<>();
    ObservableList<Reservation> reservationsList;

    private String resDate;


    @FXML
    public void SubmitCancel() {
        if(Objects.equals(this.activeStatus, "Cancelled")) {
            System.out.println("ERROR: Reservation has already been Cancelled");
            return;
        }
        
        List<Reservation> toRemove = new ArrayList<>();
        for (Reservation reservation : activeResList) {
            if (reservation.getResID() == this.reservationID) {
                toRemove.add(reservation);
            }
        }

        activeResList.removeAll(toRemove);
        cancelledResList.addAll(toRemove);

        sql.CancelReservation(this.reservationID);

        ObservableList<Reservation> items = listView.getItems();
        items.removeAll(toRemove);
    }

    public void SetUserID(int userID) {
        this.userID = userID;
    }

    public void SetUserType(String userType) {
        this.userType = userType;
    }

    public void SetArtistID(int artistID) {
        this.artistID = artistID;
    }

    @FXML
    protected ListView<Reservation> listView;

    @FXML
    public void initializeReservations() {
        // Establish SQL connection
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();

        PopulateListView();

    }

    @FXML
    public void HandleMouseClick(MouseEvent event) {
        ObservableList<Reservation> items = listView.getItems();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            Reservation selectedRes = items.get(selectedIndex);
            this.reservationID = selectedRes.getResID();
            this.activeStatus = selectedRes.getActiveStatus();
            this.resDate = selectedRes.getDate();
        }
    }


    public void PopulateListView() {
        List<Reservation> allReservations = sql.getAllReservations();
        for (Reservation reservation : allReservations) {
            if (!("User".equals(this.userType) && reservation.getUserID() == this.userID) && !("Artist".equals(userType) && reservation.getArtistID() == this.artistID)) {
                continue;
            }

            if(Objects.equals(reservation.getActiveStatus(), "Active")) {
                activeResList.add(reservation);
            } else if(Objects.equals(reservation.getActiveStatus(), "Pending")) {
                pendingResList.add(reservation);
            } else if(Objects.equals(reservation.getActiveStatus(), "Cancelled")) {
                cancelledResList.add(reservation);
            }

        }
        reservationsList = FXCollections.observableArrayList(activeResList);
        listView.setItems(reservationsList);
    }

    @FXML
    private void FilterToActive() {
        reservationsList = FXCollections.observableArrayList(activeResList);
        listView.setItems(reservationsList);
    }

    @FXML
    private void FilterToPending() {
        reservationsList = FXCollections.observableArrayList(pendingResList);
        listView.setItems(reservationsList);
    }

    @FXML
    private void FilterToCancelled() {
        reservationsList = FXCollections.observableArrayList(cancelledResList);
        listView.setItems(reservationsList);
    }

    @FXML
    private void NavigateToDashboard() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);
            dashboardController.SetArtistID(this.artistID);
            dashboardController.setUserType(this.userType);

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void DisplayReservationDescription() {
        Popup ReservationDesc = new Popup();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ReservationDescription.fxml"));
        ReservationDescriptionView resControl = loader.getController();

        resControl.setArtistName("Pop Smoke");
        resControl.setResDate(this.resDate);
        resControl.setTime("4:44");
        resControl.setDuration("30 Hours");
        resControl.setAddress("Tomato Town");
        resControl.setDescription("testing description on" + this.artistID);
    }




}


