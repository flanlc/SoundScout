package org.example.soundscoutfx;

public class Reservation {
    private int resID;
    private int artistID;

    private int userID;
    private String date;
    private String activeStatus;
    private String startTime;
    private double duration;
    private String venueType;
    private String address;
    private String description;

    public Reservation(int resID, int artistID, int userID, String date, String activeStatus, String startTime, double duration, String venueType, String address, String description) {
        this.resID = resID;
        this.artistID = artistID;
        this.userID = userID;
        this.date = date;
        this.activeStatus = activeStatus;
        this.startTime = startTime;
        this.duration = duration;
        this.venueType = venueType;
        this.address = address;
        this.description = description;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
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

    @Override
    public String toString() {
        return "Artist: " + artistID + ", User ID: " + userID + ", Date: " + date + ", Status: " + activeStatus;
    }
}
