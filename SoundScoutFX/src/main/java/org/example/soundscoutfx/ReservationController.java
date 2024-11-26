package org.example.soundscoutfx;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
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
    private String userName;
    private String userType;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    private SoundScoutSQLHelper sql = new SoundScoutSQLHelper();
    private int reservationID = 0;
    List<Reservation> resList = new ArrayList<>();

    public void setUserDetails(String firstName, String lastName, String email, String city, String zipCode, int userID) {
        this.userID = userID;
        this.userName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
    }

    @FXML
    public void SubmitCancel() {
        List<Reservation> toRemove = new ArrayList<>();
        for (Reservation reservation : resList) {
            if (reservation.getResID() == this.reservationID) {
                toRemove.add(reservation);
            }
        }

        resList.removeAll(toRemove);

        sql.CancelReservation(this.reservationID);

        ObservableList<Reservation> items = listView.getItems();
        items.removeAll(toRemove);
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
            Reservation selectedArtist = items.get(selectedIndex);
            this.reservationID = selectedArtist.getResID();
        }
    }


    public void PopulateListView() {
        List<Reservation> allReservations = sql.getAllReservations();
        for (Reservation reservation : allReservations) {
            if ("User".equals(this.userType) && reservation.getUserID() == this.userID) {
                resList.add(reservation);
            } else if ("Artist".equals(userType) && reservation.getArtistID() == this.artistID) {
                resList.add(reservation);
            }
        }
        ObservableList<Reservation> reservationsList = FXCollections.observableArrayList(resList);
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
            dashboardController.setUserDetails(this.userName, this.lastName, this.email, this.city, this.zipCode, this.userID);

            Stage stage = (Stage) listView.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


