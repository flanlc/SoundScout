package org.example.soundscoutfx;

/** Artist class for artists creating profiles */
public class Artist {
    //member variables
    private int id;
    private String firstName;
    private String lastName;
    private String stageName;
    private String DOB;
    private String address;
    private String zipCode;
    private String city;
    private String state;
    private String email;
    private String password;
    private String joinDate;
    private Profile profile;
    private double latitude;
    private double longitude;

    /** Parameterized Constructor */
    public Artist(int id, String firstName, String stageName, String lastName, String DOB, String address, String zipCode, String city, String state, String email, String password) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stageName = stageName;
        this.DOB = DOB;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.email = email;
        this.password = password;
    }

    public Artist(int id, String firstName, String lastName, String stageName, String dob, String address, String zipCode, String city, String state, String email, String password, String joinDate, Profile profile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stageName = stageName;
        this.DOB = dob;
        this.address = address;
        this.zipCode = zipCode;
        this.city = city;
        this.state = state;
        this.email = email;
        this.password = password;
        this.joinDate = joinDate;
        this.profile = profile;
    }

    public Artist(int id, String firstName, String lastName, String stageName, String genre, String city, String profilePicture, String joinDate, Profile profile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stageName = stageName;
        this.profile = profile;
        this.city = city;
        this.joinDate = joinDate;
        this.profile.getGenre();
        this.profile.setProfilePicture(profilePicture);
    }

    public Artist(int id, String firstName, String lastName, String stageName, String city, String state, String zipCode, Profile profile) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.stageName = stageName;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.profile = profile;
    }

    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDOB() {
        return DOB;
    }

    public void setDOB(String DOB) {
        this.DOB = DOB;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) { this.zipCode = zipCode; }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getJoinDate() {
        return joinDate;
    }

    public void setJoinDate(String joinDate) {
        this.joinDate = joinDate;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public double getLatitude() { return latitude; }

    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }

    /** To String overide for string representation of Artist */
    @Override
    public String toString() {
        return "Artist{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", stageName='" + stageName + '\'' +
                ", DOB='" + DOB + '\'' +
                ", address='" + address + '\'' +
                ", zipCode='" + zipCode + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}