package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class ReservationDescriptionInputController {
    @FXML
    TextField timeField;
    @FXML
    TextField durationField;
    @FXML
    TextField addyField;
    @FXML
    TextArea descBox;
    @FXML
    RadioButton pubButton;
    @FXML
    RadioButton privateButton;
    @FXML
    Label dateDisplayLabel;

    private int userID;
    private int currentArtistID;
    private String currentArtistStageName;
    private String selectedDate;
    private SoundScoutSQLHelper sql;
    private List<LocalDate> reservationDates;

    private DashboardController dashboardController;

    @FXML
    public void initialize() {
        ToggleGroup venueGroup = new ToggleGroup();
        pubButton.setToggleGroup(venueGroup);
        privateButton.setToggleGroup(venueGroup);
    }

    public void SetLabel() {
        dateDisplayLabel.setText("Selected Date: " + selectedDate);
    }

    @FXML
    public void SubmitReservation() {
        String venueType = "";
        String startTime = timeField.getText();
        String duration = durationField.getText();
        String address = addyField.getText();
        String description = descBox.getText();

        if(pubButton.isSelected()) {
            venueType = "Public";
        } else if (privateButton.isSelected()) {
            venueType = "Private";
        }

        Reservation reservation = new Reservation(0, this.currentArtistID, this.userID, selectedDate, "Pending", startTime, duration, venueType, address, description, currentArtistStageName);
        sql.CreateNewReservation(reservation);

        reservationDates.add(LocalDate.parse(selectedDate));

        if (dashboardController != null) {
            dashboardController.ReloadDashboard();
        }

        ((Stage) timeField.getScene().getWindow()).close();
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public void setCurrentArtistID(int currentArtistID) {
        this.currentArtistID = currentArtistID;
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setSql(SoundScoutSQLHelper sql) {
        this.sql = sql;
    }


    public void setReservationDates(List<LocalDate> reservationDates) {
        this.reservationDates = reservationDates;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    public String getCurrentArtistStageName() {
        return currentArtistStageName;
    }

    public void setCurrentArtistStageName(String currentArtistStageName) {
        this.currentArtistStageName = currentArtistStageName;
    }
}
