package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

public class ReservationDescriptionView {
    protected String artistName;
    protected String resDate;
    protected String time;
    protected String duration;
    protected String venueType;
    protected String address;
    protected String description;

    @FXML
    private Label nameField;
    @FXML
    private Label dateField;
    @FXML
    private Label timeField;
    @FXML
    private Label durField;
    @FXML
    private Label venueField;
    @FXML
    private Label addyField;
    @FXML
    private TextArea descriptionBox;

    public void Populate() {
        nameField.setText(artistName);
        dateField.setText(resDate);
        timeField.setText(time);
        durField.setText(duration);
        venueField.setText(venueType);
        addyField.setText(address);
        descriptionBox.setText(description);
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getResDate() {
        return resDate;
    }

    public void setResDate(String resDate) {
        this.resDate = resDate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
