package org.example.soundscoutfx;

public class UserInfo {
    private int id;
    private String firstName;
    private String userType; // "Artist" or "User"

    public UserInfo(int id, String firstName, String userType) {
        this.id = id;
        this.firstName = firstName;
        this.userType = userType;
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
}
