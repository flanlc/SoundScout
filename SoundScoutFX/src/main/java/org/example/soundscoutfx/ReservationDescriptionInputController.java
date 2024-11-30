package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
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
    @FXML
    private ComboBox<String> hourComboBox;
    @FXML
    private ComboBox<String> minuteComboBox;
    @FXML
    private ComboBox<String> ampmComboBox;
    @FXML
    private ComboBox<String> durationComboBox;

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

        hourComboBox.getItems().addAll("01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12");

        minuteComboBox.getItems().addAll("00", "15", "30", "45");

        ampmComboBox.getItems().addAll("AM", "PM");

        hourComboBox.setValue("01");
        minuteComboBox.setValue("00");
        ampmComboBox.setValue("AM");

        durationComboBox.getItems().addAll(
                "0.25", "0.50", "0.75", "1.00", "1.25", "1.50", "1.75", "2.00",
                "2.25", "2.50", "2.75", "3.00", "3.25", "3.50", "3.75", "4.00",
                "4.25", "4.50", "4.75", "5.00", "5.25", "5.50", "5.75", "6.00",
                "6.25", "6.50", "6.75", "7.00", "7.25", "7.50", "7.75", "8.00"
        );

    }

    public void SetLabel() {
        dateDisplayLabel.setText("Selected Date: " + selectedDate);
    }

    @FXML
    public void SubmitReservation() {
        String venueType = pubButton.isSelected() ? "Public" : "Private";
        String startTime = hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " + ampmComboBox.getValue();
        String duration = durationComboBox.getValue();
        String address = addyField.getText();
        String description = descBox.getText();

        if(pubButton.isSelected()) {
            venueType = "Public";
        } else if (privateButton.isSelected()) {
            venueType = "Private";
        }

        Reservation reservation = new Reservation(0, this.currentArtistID, this.userID, selectedDate, "Pending", startTime, duration, venueType, address, description, currentArtistStageName);
        sql.CreateNewReservation(reservation);

        LocalDate dateToAdd = LocalDate.parse(selectedDate);
        if (!reservationDates.contains(dateToAdd)) {
            reservationDates.add(dateToAdd);
        }

        if (dashboardController != null) {
            dashboardController.ReloadDashboard();
        }

        ((Stage) hourComboBox.getScene().getWindow()).close();
    }

    @FXML
    public void CancelReservation() {
        Stage stage = (Stage) hourComboBox.getScene().getWindow();
        stage.close();
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
