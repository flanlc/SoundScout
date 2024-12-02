package org.example.soundscoutfx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;


public class PublicResDetailsController {
    @FXML
    Label nameLabel;
    @FXML
    Label dateLabel;
    @FXML
    Label timeLabel;
    @FXML
    Label durLabel;
    @FXML
    Label venueTypeLabel;
    @FXML
    Label AddressLabel;
    @FXML
    TextArea descArea;

    private String stageName;
    private String startTime;
    private String date;
    private String duration;
    private String venueType;
    private String address;
    private String description;

    public void Populate() {
        nameLabel.setText(stageName);
        dateLabel.setText(date);
        timeLabel.setText(startTime);
        durLabel.setText(duration);
        venueTypeLabel.setText(venueType);
        AddressLabel.setText(address);
        descArea.setText(description);
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
