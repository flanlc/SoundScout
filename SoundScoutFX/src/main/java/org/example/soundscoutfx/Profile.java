package org.example.soundscoutfx;

public class Profile {
    private int profileID;
    private int artistID;
    private String genre;
    private String profilePicture;
    private String featuredPerformance;
    private String activeStatus;
    private double rate;
    private double latitude;
    private double longitude;

    public Profile(int profileID, int artistID, String genre, String profilePicture, String featuredPerformance, String activeStatus, double rate, double latitude, double longitude) {
        this.profileID = profileID;
        this.artistID = artistID;
        this.genre = genre;
        this.profilePicture = profilePicture;
        this.featuredPerformance = featuredPerformance;
        this.activeStatus = activeStatus;
        this.rate = rate;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getProfileID() {
        return profileID;
    }

    public void setProfileID(int profileID) {
        this.profileID = profileID;
    }

    public int getArtistID() {
        return artistID;
    }

    public void setArtistID(int artistID) {
        this.artistID = artistID;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFeaturedPerformance() {
        return featuredPerformance;
    }

    public void setFeaturedPerformance(String featuredPerformance) {
        this.featuredPerformance = featuredPerformance;
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }


}