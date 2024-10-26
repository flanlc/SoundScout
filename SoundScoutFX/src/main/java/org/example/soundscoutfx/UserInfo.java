package org.example.soundscoutfx;

public class UserInfo {
    private int id;
    private String firstName;
    private String userType;
    private double latitude;
    private double longitude;
    private String city;

    public UserInfo(int id, String firstName, String userType) {
        this.id = id;
        this.firstName = firstName;
        this.userType = userType;
    }

    public UserInfo(int id, String firstName, String userType, double latitude, double longitude, String city) {
        this.id = id;
        this.firstName = firstName;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getUserType() {
        return userType;
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

    public String getCity() {
        return this.city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
