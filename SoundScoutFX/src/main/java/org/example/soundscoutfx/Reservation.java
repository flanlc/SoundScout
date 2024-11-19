package org.example.soundscoutfx;

public class Reservation {
    private int resID;
    private int artistID;

    private int userID;
    private String date;
    private String activeStatus;

    public Reservation(int resID, int artistID, int userID, String date, String activeStatus) {
        this.resID = resID;
        this.artistID = artistID;
        this.userID = userID;
        this.date = date;
        this.activeStatus = activeStatus;
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

    @Override
    public String toString() {
        return "Artist: " + artistID + ", User ID: " + userID + ", Date: " + date + ", Status: " + activeStatus;
    }
}
