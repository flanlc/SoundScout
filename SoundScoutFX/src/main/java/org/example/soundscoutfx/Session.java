package org.example.soundscoutfx;

public class Session {
    //member variables
    static Session session;
    private int userID;
    private String userName;
    private String userType;
    private String lastName;
    private String email;
    private String city;
    private String zipCode;
    private int currentArtistID;
    private String currentArtistStageName;
    private SoundScoutSQLHelper sql;

    public Session() {

        this.userID = 0;
        this.userName = null;
        this.userType = null;
        this.lastName = null;
        this.email = null;
        this.city = null;
        this.zipCode = null;
        this.currentArtistID = 0;
        this.currentArtistStageName = null;
        this.sql = new SoundScoutSQLHelper();
        sql.establishConnection();
    }

    public Session(int userID, String userName, String userType, String lastName, String email, String city, String zipCode, SoundScoutSQLHelper sql) {
        this.userID = userID;
        this.userName = userName;
        this.userType = userType;
        this.lastName = lastName;
        this.email = email;
        this.city = city;
        this.zipCode = zipCode;
        this.sql = sql;
        sql.establishConnection();
    }

    //returns new session or existing
    public static Session getInstance() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    //clears session
    public void clearSession() {
        userID = 0;
        userName = null;
        userType = null;
        lastName = null;
        email = null;
        city = null;
        zipCode = null;
        currentArtistID = 0;
        currentArtistStageName = null;
    }

    public static Session getSession() {
        return session;
    }

    public static void setSession(Session session) {
        Session.session = session;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public SoundScoutSQLHelper getSql() {
        return sql;
    }

    public void setSql(SoundScoutSQLHelper sql) {
        this.sql = sql;
    }

    public int getCurrentArtistID() {
        return currentArtistID;
    }

    public void setCurrentArtistID(int currentArtistID) {
        this.currentArtistID = currentArtistID;
    }

    public String getCurrentArtistStageName() {
        return currentArtistStageName;
    }

    public void setCurrentArtistStageName(String currentArtistStageName) {
        this.currentArtistStageName = currentArtistStageName;
    }
}
