package org.example.soundscoutfx;

public class Profile {
    private int profileID;
    private int artistID;
    private String genere;
    private String profilePicture;
    private String featuredPerformance;
    private String activeStatus;

    public Profile(int profileID, int artistID, String genere, String profilePicture, String featuredPerformance, String activeStatus) {
        this.profileID = profileID;
        this.artistID = artistID;
        this.genere = genere;
        this.profilePicture = profilePicture;
        this.featuredPerformance = featuredPerformance;
        this.activeStatus = activeStatus;
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

    public String getGenere() {
        return genere;
    }

    public void setGenere(String genere) {
        this.genere = genere;
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