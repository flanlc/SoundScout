package org.example.soundscoutfx;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;

import java.util.List;
import java.util.Map;
import java.io.IOException;

/** SQL Helper class */
public class SoundScoutSQLHelper {
    Connection conn;
    boolean status = false;
    ArrayList<Artist> artistArrayList = new ArrayList<Artist>();

    /** Establishes sql database connection */
    public Connection establishConnection() {
        {
            try {
                conn = DriverManager.getConnection("jdbc:sqlserver://soundscout.database.windows.net:1433;database=SoundScout;user=SoundScoutAdmin@soundscout;password=SoundScout!!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
                status = true;
                return conn;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** Tests sql connection */
    public void testConnection() {
        Connection con;
        con = establishConnection();

        if (con != null) {
            System.out.println("Works");
        } else {
            System.out.println("ERROR: Connection Not Created");
        }
    }

    /** Verifies user credentials in SQL server */
    protected UserInfo verifyUserCredentials(String email, String password) {
        String query = "SELECT ArtistID AS ID, FirstName, LastName, Email, City, ZipCode, 'Artist' AS UserType " +
                "FROM Artist WHERE Email = ? AND Password = ? " +
                "UNION " +
                "SELECT UserID AS ID, FirstName, LastName, Email, City, ZipCode, 'User' AS UserType " +
                "FROM Users WHERE Email = ? AND Password = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int id = resultSet.getInt("ID");
                String firstName = resultSet.getString("FirstName");
                String lastName = resultSet.getString("LastName");
                String city = resultSet.getString("City");
                String zipCode = resultSet.getString("ZipCode");
                String userType = resultSet.getString("UserType");
                return new UserInfo(id, firstName, lastName, email, city, zipCode, userType);
            } else {
                //if no matching user found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /** Creates new artist in SQL */
    protected int CreateArtist(String firstName, String lastName, String stageName, String DOB, String streetAddress, String zipCode, String city, String state, double rate, String email, String password) {
        if (!status) {
            System.out.println("ERROR: Connection Not Created");
            return -1;
        }

        String query = "INSERT INTO Artist (FirstName, LastName, StageName, DOB, StreetAddress, ZipCode, City, State, Email, Password, JoinDate, ActiveStatus) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, GETDATE(), 'Active')";

        try {
            PreparedStatement artistInsertStatement = conn.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
            artistInsertStatement.setString(1, firstName);
            artistInsertStatement.setString(2, lastName);
            artistInsertStatement.setString(3, stageName);
            artistInsertStatement.setString(4, DOB);
            artistInsertStatement.setString(5, streetAddress);
            artistInsertStatement.setString(6, zipCode);
            artistInsertStatement.setString(7, city);
            artistInsertStatement.setString(8, state);
            artistInsertStatement.setString(9, email);
            artistInsertStatement.setString(10, password);

            int affectedRows = artistInsertStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating artist failed, no rows affected.");
            }

            try (ResultSet generatedKeys = artistInsertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newArtistID = generatedKeys.getInt(1);
                    CreateNewProfile(newArtistID, rate);
                    return newArtistID;
                } else {
                    throw new SQLException("Creating artist failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error while creating artist: " + e.getMessage(), e);
        }
    }

    /** Creates new user in SQL */
    protected void CreateUser(String firstName, String lastName, String accountType, String city, String zipCode, String businessAddress, String email, String password) {
        if (!status) {
            System.out.println("ERROR: Connection Not Created");
            return;
        }

        String query;
        if ("Business".equals(accountType)) {
            //query for BUSINESS users (YES BusinessAddress)
            query = "INSERT INTO Users (FirstName, LastName, AccountType, BusinessAddress, City, ZipCode, Email, Password, JoinDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, GETDATE())";
        } else {
            //query for PERSONAL users (NO BusinessAddress)
            query = "INSERT INTO Users (FirstName, LastName, AccountType, City, ZipCode, Email, Password, JoinDate) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())";
        }

        try {
            PreparedStatement userInsertStatement = conn.prepareStatement(query);
            userInsertStatement.setString(1, firstName);
            userInsertStatement.setString(2, lastName);
            userInsertStatement.setString(3, accountType);

            if ("Business".equals(accountType)) {
                //set the parameters in the correct order for Business users
                userInsertStatement.setString(4, businessAddress);
                userInsertStatement.setString(5, city);
                userInsertStatement.setString(6, zipCode);
                userInsertStatement.setString(7, email);
                userInsertStatement.setString(8, password);
            } else {
                //set the parameters in the correct order for Personal users
                userInsertStatement.setString(4, city);
                userInsertStatement.setString(5, zipCode);
                userInsertStatement.setString(6, email);
                userInsertStatement.setString(7, password);
            }

            userInsertStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error while inserting user: " + e.getMessage());
        }
    }

    /** Updates user profile within SQL */
    public void updateUserProfile(int userID, String firstName, String lastName, String email, String city, String zipCode) throws SQLException {
        String updateQuery = "UPDATE Users SET FirstName = ?, LastName = ?, Email = ?, City = ?, ZipCode = ? WHERE UserID = ?";

        try (PreparedStatement statement = conn.prepareStatement(updateQuery)) {
            statement.setString(1, firstName);
            statement.setString(2, lastName);
            statement.setString(3, email);
            statement.setString(4, city);
            statement.setString(5, zipCode);
            statement.setInt(6, userID);
            statement.executeUpdate();
        }
    }

    public void updateSingleField(String fieldName, String fieldValue, int userID) throws SQLException {
        String query = "UPDATE Users SET " + fieldName + " = ? WHERE UserID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, fieldValue);
            statement.setInt(2, userID);
            statement.executeUpdate();
        }
    }

    /** Uploads user image to cloudinary, returns url to users sql row in the profile table */
    protected void UploadToCloudinary(int artistID, String filePath) {
        Dotenv dotenv = Dotenv.load();
        Cloudinary cloudinary = new Cloudinary(dotenv.get("CLOUDINARY_URL"));
        System.out.println(cloudinary.config.cloudName);

        String uploadURL = null;
        String sqlQuery = "UPDATE ArtistProfile SET ProfilePicture = ? WHERE ArtistID = ?;";

        Map<String, Object> params1 = ObjectUtils.asMap(
                "use_filename", true,
                "unique_filename", false,
                "overwrite", true
        );

        try {
            Map<String, Object> uploadResult = cloudinary.uploader().upload(filePath, params1);
            uploadURL = (String) uploadResult.get("url");
        } catch (IOException e) {
            throw new RuntimeException("Error Uploading", e);
        }

        try (PreparedStatement artistUpdateStatement = conn.prepareStatement(sqlQuery)) {
            artistUpdateStatement.setString(1, uploadURL);
            artistUpdateStatement.setInt(2, artistID);

            artistUpdateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error Updating", e);
        }
    }

    /** Updates user performance youtube video url */
    protected void UpdatePerformance(int artistID, String uploadURL) {
        String sqlQuery = "UPDATE ArtistProfile SET FeaturedPerformance = ? WHERE ArtistID = ?;";

        try (PreparedStatement artistUpdateStatement = conn.prepareStatement(sqlQuery)) {
            artistUpdateStatement.setString(1, uploadURL);
            artistUpdateStatement.setInt(2, artistID);

            artistUpdateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Creates new profile in sql */
    protected void CreateNewProfile(int newArtistID, double rate) {
        int artistID = -1;

        String getArtistIDQuery = "SELECT MAX(ArtistID) FROM Artist";
        try (Statement statement = conn.createStatement();
             ResultSet resultSet = statement.executeQuery(getArtistIDQuery)) {

            if (resultSet.next()) {
                artistID = resultSet.getInt(1);
            } else {
                System.out.println("ERROR: No artist found.");
                return;
            }

            String defaultPolicy = "Cancellations must be made at least 24 hours before the scheduled event. No refunds available if cancellation is made within 24 hours of event.";
            String query = "INSERT INTO ArtistProfile (ArtistID, ProfilePicture, ActiveStatus, Rate, CancellationPolicy) VALUES (?, ?, ?, ?, ?);";

            PreparedStatement profileInsertStatement = conn.prepareStatement(query);
            profileInsertStatement.setInt(1, artistID);
            profileInsertStatement.setString(2, "http://res.cloudinary.com/dbvmjemlj/image/upload/v1728860327/profile-icon-design-free-vector.jpg");
            profileInsertStatement.setString(3, "Active");
            profileInsertStatement.setDouble(4, rate);
            profileInsertStatement.setString(5, defaultPolicy);
            profileInsertStatement.execute();

        } catch (SQLException e) {
            System.out.println("SQL Error while retrieving ArtistID: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }

    protected ArrayList<Artist> GetDBArtistsProfiles() {
        String getArtistQuery = "SELECT * FROM Artist Where ActiveStatus = 'Active';";
        String getProfileQuery = "SELECT * FROM ArtistProfile WHERE ArtistID = ?;";
        Statement statement = null;
        PreparedStatement profileStatement = null;
        Artist artist;
        Profile profile;
        try {
            statement = conn.createStatement();
            profileStatement = conn.prepareStatement(getProfileQuery);
            ResultSet rs = statement.executeQuery(getArtistQuery);
            while (rs.next()) {
                int id = rs.getInt("ArtistID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String stageName = rs.getString("StageName");
                String DOB = rs.getString("DOB");
                String address = rs.getString("StreetAddress");
                String zipCode = rs.getString("ZipCode");
                String city = rs.getString("City");
                String state = rs.getString("State");
                String email = rs.getString("Email");
                String password = rs.getString("Password");
                String joinDate = rs.getString("JoinDate");

                profileStatement.setInt(1, id);
                ResultSet profileSet = profileStatement.executeQuery();

                if (profileSet.next()) {
                    int pID = profileSet.getInt("ProfileID");
                    String genre = profileSet.getString("Genre");
                    String profilePicture = profileSet.getString("ProfilePicture");
                    String featuredPerformance = profileSet.getString("FeaturedPerformance");
                    String activeStatus = profileSet.getString("ActiveStatus");
                    Double rate = profileSet.getDouble("Rate");
                    Double latitude = profileSet.getDouble("Latitude");
                    Double longitude = profileSet.getDouble("Longitude");

                    profile = new Profile(pID, id, genre, profilePicture, featuredPerformance, activeStatus, rate, latitude, longitude);

                } else {
                    profile = null;
                }

                artist = new Artist(id, firstName, lastName, stageName, DOB, address, zipCode, city, state, email, password, joinDate, profile);
                artistArrayList.add(artist);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (statement != null) statement.close();
            if (profileStatement != null) profileStatement.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return artistArrayList;
    }

    /** Updates artists selected rate within sql server */
    public void updateArtistRate(int artistID, double rate) throws SQLException {
        String query = "UPDATE ArtistProfile SET Rate = ? WHERE ArtistID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setDouble(1, rate);
            statement.setInt(2, artistID);
            statement.executeUpdate();
        }
    }

    public ObservableList<Artist> searchArtists(String searchText) {
        ObservableList<Artist> searchResults = FXCollections.observableArrayList();

        if (conn == null) {
            establishConnection();
        }

        String query = "SELECT a.ArtistID, a.FirstName, a.LastName, a.StageName, ap.Genre, a.City, ap.ProfilePicture, ap.FeaturedPerformance, a.JoinDate, ap.ActiveStatus, ap.Rate, ap.Latitude, ap.Longitude " +
                "FROM Artist a " +
                "JOIN ArtistProfile ap ON a.ArtistID = ap.ArtistID " +
                "WHERE (a.FirstName LIKE ? OR a.LastName LIKE ? OR a.StageName LIKE ? OR ap.Genre LIKE ?) " +
                "AND a.ActiveStatus = 'Active' AND ap.ActiveStatus = 'Active'";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            String searchPattern = "%" + searchText + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("ArtistID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String stageName = rs.getString("StageName");
                String genre = rs.getString("Genre");
                String city = rs.getString("City");
                String profilePicture = rs.getString("ProfilePicture");
                String featuredPerformance = rs.getString("FeaturedPerformance");
                String joinDate = rs.getString("JoinDate");
                String activeStatus = rs.getString("ActiveStatus");
                Double rate = rs.getDouble("Rate");
                Double latitude = rs.getDouble("latitude");
                Double longitude = rs.getDouble("Longitude");

                //create a Profile object with video and profile picture
                Profile profile = new Profile(0, id, genre, profilePicture, featuredPerformance, activeStatus, rate, latitude, longitude);

                //create an Artist object with the retrieved data
                Artist artist = new Artist(id, firstName, lastName, stageName, null, null, null, city, null, null, null, joinDate, profile);

                searchResults.add(artist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return searchResults;
    }

    /** Updates artists selected genre within sql server */
    public void updateArtistGenres(int artistID, String genres) throws SQLException {
        String query = "UPDATE ArtistProfile SET Genre = ? WHERE ArtistID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, genres);
            statement.setInt(2, artistID);
            statement.executeUpdate();
        }
    }

    /** Updates artists location within sql server */
    public void updateArtistLocation(int artistID, double latitude, double longitude) throws SQLException {
        String query = "UPDATE ArtistProfile SET Latitude = ?, Longitude = ? WHERE ArtistID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setDouble(1, latitude);
            statement.setDouble(2, longitude);
            statement.setInt(3, artistID);
            statement.executeUpdate();
        }
    }

    /** Updates user location within sql server */
    public void updateUserLocation(int userID, double latitude, double longitude) throws SQLException {
        String query = "UPDATE Users SET Latitude = ?, Longitude = ? WHERE UserID = ?";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setDouble(1, latitude);
            statement.setDouble(2, longitude);
            statement.setInt(3, userID);
            statement.executeUpdate();
        }
    }

    /** Gets user latitude */
    public double getUserLatitude(int userID) {
        try (PreparedStatement statement = conn.prepareStatement("SELECT Latitude FROM Users WHERE UserID = ?")) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("Latitude");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /** Gets user longitude */
    public double getUserLongitude(int userID) {
        try (PreparedStatement statement = conn.prepareStatement("SELECT Longitude FROM Users WHERE UserID = ?")) {
            statement.setInt(1, userID);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("Longitude");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /** Gets all users in sql server */
    public List<UserInfo> getAllUsers() {
        List<UserInfo> users = new ArrayList<>();
        String query = "SELECT UserID, FirstName, LastName, City, ZipCode, Latitude, Longitude FROM Users";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("UserID");
                String firstName = rs.getString("FirstName");
                //String userType = rs.getString("UserType");
                /*String lastName = rs.getString("LastName");
                String city = rs.getString("City");
                String zipCode = rs.getString("ZipCode");*/
                String city = rs.getString("City");
                double latitude = rs.getDouble("Latitude");
                double longitude = rs.getDouble("Longitude");

                UserInfo user = new UserInfo(id, firstName, latitude, longitude, city);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    public int getLastInsertedUserID() {
        String query = "SELECT MAX(UserID) AS LastUserID FROM Users";
        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("LastUserID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; //if something went wrong
    }

    /** Updates artists selected rate within sql server */
    public List<Artist> getAllArtists() {
        List<Artist> artists = new ArrayList<>();
        String query = "SELECT a.ArtistID, a.FirstName, a.LastName, a.StageName, a.City, a.State, a.ZipCode, " +
                "ap.Genre, ap.Rate, ap.Latitude, ap.Longitude, ap.ProfilePicture, ap.FeaturedPerformance, ap.ActiveStatus " +
                "FROM Artist a " +
                "JOIN ArtistProfile ap ON a.ArtistID = ap.ArtistID";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                int id = rs.getInt("ArtistID");
                String firstName = rs.getString("FirstName");
                String lastName = rs.getString("LastName");
                String stageName = rs.getString("StageName");
                String city = rs.getString("City");
                String state = rs.getString("State");
                String zipCode = rs.getString("ZipCode");
                String genres = rs.getString("Genre");
                double rate = rs.getDouble("Rate");
                double latitude = rs.getDouble("Latitude");
                double longitude = rs.getDouble("Longitude");
                String profilePicture = rs.getString("ProfilePicture");
                String featuredPerformance = rs.getString("FeaturedPerformance");
                String activeStatus = rs.getString("ActiveStatus");

                Profile profile = new Profile(id, id, genres, profilePicture, featuredPerformance, activeStatus, rate, latitude, longitude);

                Artist artist = new Artist(id, firstName, lastName, stageName, city, state, zipCode, profile);
                artists.add(artist);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return artists;
    }

    /** Checks if artist is available */
    public boolean isArtistAvailable(int artistId, List<LocalDate> selectedDates) {
        if (selectedDates.isEmpty()) {
            return true;  // No dates are selected, so artist should be considered available
        }

        String query = "SELECT COUNT(*) FROM Reservation WHERE ArtistID = ? AND bookingDate = ? AND status = 'Active'";
        try (PreparedStatement statement = conn.prepareStatement(query)) {
            for (LocalDate date : selectedDates) {
                statement.setInt(1, artistId);
                statement.setDate(2, Date.valueOf(date));
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next() && resultSet.getInt(1) > 0) {
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    /** Gets all reservations shown in the reservation table */
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();
        String Query = "SELECT * FROM VW_Reservations";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(Query)) {
            //Used to change from 24hr format to 12hr format in est time
            DateTimeFormatter input = DateTimeFormatter.ofPattern("HH:mm:ss.SSSSSSS");
            DateTimeFormatter output = DateTimeFormatter.ofPattern("hh:mm a");

            while (rs.next()) {
                int resID = rs.getInt("ReservationID");
                int ArtistID = rs.getInt("ArtistID");
                int userID = rs.getInt("UserID");
                String day = rs.getString("bookingDate");
                String status = rs.getString("status");
                String startTime = rs.getString("startTime");
                if (startTime != null && !startTime.isEmpty()) {
                    LocalTime time = LocalTime.parse(startTime, input);
                    startTime = time.format(output);
                }
                String duration = rs.getString("duration");
                String venueType = rs.getString("venueType");
                String address = rs.getString("Address");
                String description = rs.getString("Description");
                String stageName = rs.getString("StageName");

                Reservation reservation = new Reservation(resID, ArtistID, userID, day, status, startTime, duration, venueType, address, description, stageName);
                reservations.add(reservation);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return reservations;
    }

    /** Creates new reservation in sql table */
    protected void CreateNewReservation(Reservation reservation) {
        if(reservation.getArtistID() == 0) {
            System.out.println("ERROR: No artist selected");
            return;
        }

        String query = "INSERT INTO Reservation (ArtistID, UserID, BookingDate, Status, Starttime, duration, venuetype, address, description) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);";

        PreparedStatement profileInsertStatement = null;
        try {
            profileInsertStatement = conn.prepareStatement(query);
            profileInsertStatement.setInt(1, reservation.getArtistID());
            profileInsertStatement.setInt(2, reservation.getUserID());
            profileInsertStatement.setString(3, reservation.getDate());
            profileInsertStatement.setString(4, reservation.getActiveStatus());
            profileInsertStatement.setString(5, reservation.getStartTime());
            profileInsertStatement.setString(6, reservation.getDuration());
            profileInsertStatement.setString(7, reservation.getVenueType());
            profileInsertStatement.setString(8, reservation.getAddress());
            profileInsertStatement.setString(9, reservation.getDescription());
            profileInsertStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Cancels reservation in sql server by changing status column to cancelled */
    public void CancelReservation(int reservationID) {
        String query = "UPDATE Reservation SET status = 'Cancelled' WHERE ReservationID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, reservationID);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Updates pending reservation to active */
    public void UpdateReservation(int reservationID) {
        String query = "UPDATE Reservation SET status = 'Active' WHERE ReservationID = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setInt(1, reservationID);
            statement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /** Updates artist bio within sql */
    public void updateArtistBio(int userID, String bio) throws SQLException {
        String query = "UPDATE ArtistProfile SET bio = ? WHERE artistID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, bio);
            stmt.setInt(2, userID);
            stmt.executeUpdate();
        }
    }

    /** Gets artist bio from sql */
    public String getArtistBio(int artistID) throws SQLException {
        String query = "SELECT bio FROM ArtistProfile WHERE artistID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, artistID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("bio");
                }
            }
        }
        return null;
    }

    /** gets artist cancellation policy from sql */
    public String[] getArtistCancellationPolicy(int artistID) throws SQLException {
        String query = "SELECT CancellationPolicy, FORMAT(PolicyUpdatedAt, 'yyyy-MM-dd''T''HH:mm:ss') AS PolicyUpdatedAtFormatted " +
                "FROM ArtistProfile WHERE ArtistID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, artistID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String cancellationPolicy = rs.getString("CancellationPolicy");
                    return new String[]{cancellationPolicy, rs.getString("PolicyUpdatedAtFormatted")};
                }
            }
        }
        return new String[]{"No cancellation policy available.", null};
    }

    /** Updates artist cancellation policy within sql */
    public void updateArtistCancellationPolicy(int artistID, String newPolicy) throws SQLException {
        String query = "UPDATE ArtistProfile SET CancellationPolicy = ?, PolicyUpdatedAt = GETDATE() WHERE ArtistID = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPolicy);
            stmt.setInt(2, artistID);
            stmt.executeUpdate();
        }
    }


}