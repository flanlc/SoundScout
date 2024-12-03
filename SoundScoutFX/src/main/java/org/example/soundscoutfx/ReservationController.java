package org.example.soundscoutfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.collections.FXCollections;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Reservation fxml controller class */
public class ReservationController {
    //member variables
    private int artistID;
    private int userID;
    private String userName;
    private String userType;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    private SoundScoutSQLHelper sql = new SoundScoutSQLHelper();
    private int reservationID = 0;
    private String activeStatus = null;
    List<Reservation> activeResList = new ArrayList<>();
    List<Reservation> pendingResList = new ArrayList<>();
    List<Reservation> cancelledResList = new ArrayList<>();
    ObservableList<Reservation> reservationsList;
    private String resDate;
    private int globalSelectedIndex;

    //fxml nodes
    @FXML
    Button cancelButton;
    @FXML
    Button approveButton;
    @FXML
    protected ListView<Reservation> listView;

    /** Populates user detail member variables */
    public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        this.userID = userID;
        this.userName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
    }

    /** Handles reservation cancel */
    @FXML
    public void SubmitCancel() {
        if(Objects.equals(this.activeStatus, "Cancelled")) {
            System.out.println("ERROR: Reservation has already been Cancelled");
            return;
        }

        //parses list and cancels desired reservation
        List<Reservation> toRemove = new ArrayList<>();
        for (Reservation reservation : activeResList) {
            if (reservation.getResID() == this.reservationID) {
                toRemove.add(reservation);
                reservation.setActiveStatus("Cancelled");
            }
        }

        for (Reservation reservation : pendingResList) {
            if (reservation.getResID() == this.reservationID) {
                toRemove.add(reservation);
                reservation.setActiveStatus("Cancelled");
            }
        }

        activeResList.removeAll(toRemove);
        pendingResList.removeAll(toRemove);
        cancelledResList.addAll(toRemove);

        sql.CancelReservation(this.reservationID);

        //changes listview based on filter
        if (listView.getItems().equals(FXCollections.observableArrayList(activeResList))) {
            reservationsList = FXCollections.observableArrayList(activeResList);
        } else if (listView.getItems().equals(FXCollections.observableArrayList(pendingResList))) {
            reservationsList = FXCollections.observableArrayList(pendingResList);
        }

        ObservableList<Reservation> items = listView.getItems();
        items.removeAll(toRemove);
        listView.getItems();
    }

    /** Handles pending reservation approval */
    @FXML
    public void SubmitApprove() {
        if (Objects.equals(this.activeStatus, "Active")) {
            System.out.println("ERROR: Reservation has already been activated.");
            return;
        }

        List<Reservation> toApprove = new ArrayList<>();
        for (Reservation reservation : pendingResList) {
            if (reservation.getResID() == this.reservationID) {
                toApprove.add(reservation);
                reservation.setActiveStatus("Active");
            }
        }

        sql.UpdateReservation(this.reservationID);

        activeResList.addAll(toApprove);
        pendingResList.removeAll(toApprove);

        if (listView.getItems().equals(FXCollections.observableArrayList(pendingResList))) {
            listView.setItems(FXCollections.observableArrayList(pendingResList));
        } else if (listView.getItems().equals(FXCollections.observableArrayList(activeResList))) {
            listView.setItems(FXCollections.observableArrayList(activeResList));
        }

        ObservableList<Reservation> items = listView.getItems();
        items.removeAll(toApprove);
        listView.getItems();
    }

    /** Creates sql connection and calls populate method */
    @FXML
    public void initializeReservations() {
        // Establish SQL connection
        sql = new SoundScoutSQLHelper();
        sql.establishConnection();

        PopulateListView();
        FilterToActive();
    }

    /** Handles listview mouse click */
    @FXML
    public void HandleMouseClick(MouseEvent event) {
        ObservableList<Reservation> items = listView.getItems();
        int selectedIndex = listView.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < items.size()) {
            globalSelectedIndex = selectedIndex;
            Reservation selectedRes = items.get(selectedIndex);
            this.reservationID = selectedRes.getResID();
            this.activeStatus = selectedRes.getActiveStatus();
            this.resDate = selectedRes.getDate();
        }
    }

    /** Populates reservation data */
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

    /** Filters listview to active reservations */
    @FXML
    private void FilterToActive() {
        reservationsList = FXCollections.observableArrayList(activeResList);
        listView.setItems(reservationsList);
        if(userType.equals("Artist")) {
            approveButton.setVisible(false);
            cancelButton.setVisible(true);
        }
        else {
            approveButton.setVisible(false);
            cancelButton.setVisible(false);
        }
    }

    /** Filters listview to pending reservations */
    @FXML
    private void FilterToPending() {
        reservationsList = FXCollections.observableArrayList(pendingResList);
        listView.setItems(reservationsList);

        if(userType.equals("Artist")) {
            approveButton.setVisible(true);
            cancelButton.setVisible(true);
        }
        else {
            approveButton.setVisible(false);
            cancelButton.setVisible(false);
        }
    }

    /** Filters listview to cancelled reservations */
    @FXML
    private void FilterToCancelled() {
        reservationsList = FXCollections.observableArrayList(cancelledResList);
        listView.setItems(reservationsList);
        approveButton.setVisible(false);
        cancelButton.setVisible(false);
    }

    /** Navigates to dashboard fxml */
    @FXML
    private void NavigateToDashboard() {
        System.out.println("Dashboard change");
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.setUserID(this.userID);
            dashboardController.SetArtistID(this.artistID);
            dashboardController.setUserType(this.userType);
            dashboardController.setUserDetails(this.userName, this.lastName, this.email, this.city, this.zipCode, this.userID);

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Displays reservation description popup */
    @FXML
    private void DisplayReservationDescription() {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ReservationDescription.fxml"));
            Parent root = loader.load();

            ReservationDescriptionViewController resControl = loader.getController();

            Reservation selectedReservation = this.listView.getItems().get(globalSelectedIndex);

            resControl.setArtistName(selectedReservation.getStageName());
            resControl.setResDate(this.resDate);
            resControl.setTime(selectedReservation.getStartTime());
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

    //getters and setters
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

    public void SetUserID(int userID) {
        this.userID = userID;
    }

    public void SetUserType(String userType) {
        this.userType = userType;
    }

    public void SetArtistID(int artistID) {
        this.artistID = artistID;
    }




}