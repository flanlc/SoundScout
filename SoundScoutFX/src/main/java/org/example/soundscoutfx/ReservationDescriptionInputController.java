package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

public class ReservationDescriptionInputController {
    //FXML nodes
    @FXML
    private TextField addyField;
    @FXML
    private TextArea descBox;
    @FXML
    private RadioButton pubButton;
    @FXML
    private RadioButton privateButton;
    @FXML
    private Label dateDisplayLabel;
    @FXML
    private ComboBox<String> hourComboBox;
    @FXML
    private ComboBox<String> minuteComboBox;
    @FXML
    private ComboBox<String> ampmComboBox;
    @FXML
    private ComboBox<String> durationComboBox;

    //Member variables
    private String selectedDate;
    private List<LocalDate> reservationDates;
    private DashboardController dashboardController;

    /** Init. override */
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

    /** Creates reservation by populating object and calling sql method */
    @FXML
    public void SubmitReservation() {
        Session session = Session.getInstance();
        int userID = session.getUserID();
        int currentArtistID = session.getCurrentArtistID();
        String currentArtistStageName = session.getCurrentArtistStageName();

        if (userID == 0 || currentArtistID == 0 || currentArtistStageName == null) {
            ShowErrorMessage("Invalid session data. Please sign in again.");
            return;
        }

        String venueType = pubButton.isSelected() ? "Public" : privateButton.isSelected() ? "Private" : null;
        String startTime = hourComboBox.getValue() + ":" + minuteComboBox.getValue() + " " + ampmComboBox.getValue();
        String duration = durationComboBox.getValue();
        String address = addyField.getText().trim();
        String description = descBox.getText().trim();

        if (venueType == null) {
            ShowErrorMessage("Please select a venue type.");
            return;
        }
        if (address.isEmpty()) {
            ShowErrorMessage("Please provide an address.");
            return;
        }
        if (description.isEmpty()) {
            ShowErrorMessage("Please provide a description.");
            return;
        }

        try {
            Reservation reservation = new Reservation(
                    0, currentArtistID, userID, selectedDate, "Pending",
                    startTime, duration, venueType, address, description, currentArtistStageName
            );

            session.getSql().CreateNewReservation(reservation);

            LocalDate dateToAdd = LocalDate.parse(selectedDate);
            if (!reservationDates.contains(dateToAdd)) {
                reservationDates.add(dateToAdd);
            }

            if (dashboardController != null) {
                dashboardController.ReloadDashboard();
            }

            CloseWindow();
        } catch (Exception e) {
            e.printStackTrace();
            ShowErrorMessage("Failed to create reservation.");
        }
    }

    /** On cancel close */
    @FXML
    public void CancelReservation() {
        CloseWindow();
    }

    /** Display an error message */
    private void ShowErrorMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setSelectedDate(String selectedDate) {
        this.selectedDate = selectedDate;
    }

    public void setReservationDates(List<LocalDate> reservationDates) {
        this.reservationDates = reservationDates;
    }

    public void setDashboardController(DashboardController dashboardController) {
        this.dashboardController = dashboardController;
    }

    /** Closes window */
    private void CloseWindow() {
        Stage stage = (Stage) hourComboBox.getScene().getWindow();
        stage.close();
    }
}
