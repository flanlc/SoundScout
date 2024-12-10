package org.example.soundscoutfx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Reservation fxml controller class */
public class ReservationController {
    //member variables
    private int reservationID = 0;
    private String activeStatus = null;
    private String resDate;
    private int globalSelectedIndex;
    private final List<Reservation> activeResList = new ArrayList<>();
    private final List<Reservation> pendingResList = new ArrayList<>();
    private final List<Reservation> cancelledResList = new ArrayList<>();
    private ObservableList<Reservation> reservationsList;
    private final SoundScoutSQLHelper sql = new SoundScoutSQLHelper();

    //fxml nodes
    @FXML
    private Button cancelButton;
    @FXML
    private Button approveButton;
    @FXML
    protected ListView<Reservation> listView;

    @FXML
    public void initialize() {
        InitializeReservations();
    }

    /** Handles reservation cancel */
    @FXML
    public void SubmitCancel() {
        if (Objects.equals(this.activeStatus, "Cancelled")) {
            System.out.println("ERROR: Reservation has already been Cancelled");
            return;
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Cancel Reservation");
        confirmationDialog.setContentText("Are you sure you want to cancel this reservation?");

        ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationDialog.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                performCancellation();
            } else {
                System.out.println("Cancellation aborted by user.");
            }
        });
    }

    private void performCancellation() {
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

        if (listView.getItems().equals(FXCollections.observableArrayList(activeResList))) {
            reservationsList = FXCollections.observableArrayList(activeResList);
        } else if (listView.getItems().equals(FXCollections.observableArrayList(pendingResList))) {
            reservationsList = FXCollections.observableArrayList(pendingResList);
        }

        ObservableList<Reservation> items = listView.getItems();
        items.removeAll(toRemove);
        listView.refresh();
    }

    /** Handles pending reservation approval */
    @FXML
    public void SubmitApprove() {
        if (Objects.equals(this.activeStatus, "Active")) {
            System.out.println("ERROR: Reservation has already been activated.");
            return;
        }

        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Confirmation");
        confirmationDialog.setHeaderText("Confirm Reservation");
        confirmationDialog.setContentText("Are you sure you want to confirm this reservation?");

        ButtonType confirmButton = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmationDialog.getButtonTypes().setAll(confirmButton, cancelButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == confirmButton) {
                performApproval();
            } else {
                System.out.println("Approval aborted by user.");
            }
        });
    }

    private void performApproval() {
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
        listView.refresh();
    }

    /** Creates sql connection and calls populate method */
    @FXML
    public void InitializeReservations() {
        // Establish SQL connection
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
        Session session = Session.getInstance();
        String userType = session.getUserType();
        int userID = session.getUserID();
        int artistID = session.getCurrentArtistID();

        List<Reservation> allReservations = sql.getAllReservations();
        for (Reservation reservation : allReservations) {
            if ("User".equals(userType) && reservation.getUserID() != userID) {
                continue;
            }
            if ("Artist".equals(userType) && reservation.getArtistID() != artistID) {
                continue;
            }

            if (Objects.equals(reservation.getActiveStatus(), "Active")) {
                activeResList.add(reservation);
            } else if (Objects.equals(reservation.getActiveStatus(), "Pending")) {
                pendingResList.add(reservation);
            } else if (Objects.equals(reservation.getActiveStatus(), "Cancelled")) {
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
        if ("Artist".equals(Session.getInstance().getUserType())) {
            approveButton.setVisible(false);
            cancelButton.setVisible(true);
        } else {
            approveButton.setVisible(false);
            cancelButton.setVisible(true);
        }
    }

    /** Filters listview to pending reservations */
    @FXML
    private void FilterToPending() {
        reservationsList = FXCollections.observableArrayList(pendingResList);
        listView.setItems(reservationsList);

        if ("Artist".equals(Session.getInstance().getUserType())) {
            approveButton.setVisible(true);
            cancelButton.setVisible(true);
        } else {
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

    /** Navigates to the dashboard fxml */
    @FXML
    private void NavigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
            Parent root = loader.load();

            DashboardController dashboardController = loader.getController();
            dashboardController.ClearDetails();

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /** Load home page */
    @FXML
    private void NavigateToHome() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("logged-home.fxml"));
            Parent root = loader.load();

            LoggedHomeController loggedHomeController = loader.getController();
            loggedHomeController.initialize();

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Home");
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
}
