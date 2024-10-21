package org.example.soundscoutfx;

import java.sql.*;
import java.util.ArrayList;
import org.example.soundscoutfx.UserInfo;
import com.cloudinary.*;
import com.cloudinary.utils.ObjectUtils;
import io.github.cdimascio.dotenv.Dotenv;
import java.util.Map;
import java.io.IOException;

public class SoundScoutSQLHelper {
    Connection conn;
    boolean status = false;
    ArrayList<Artist> artistArrayList = new ArrayList<Artist>();

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

    public void testConnection() {
        Connection con;
        con = establishConnection();

        if(con != null) {
            System.out.println("Works");
        }
        else {
            System.out.println("ERROR: Connection Not Created");
        }
    }

    protected UserInfo verifyUserCredentials(String email, String password) {
        String query = "SELECT ArtistID AS ID, FirstName, 'Artist' AS UserType FROM Artist WHERE Email = ? AND Password = ? " +
                "UNION " +
                "SELECT UserID AS ID, FirstName, 'User' AS UserType FROM Users WHERE Email = ? AND Password = ?";

        try (PreparedStatement statement = conn.prepareStatement(query)) {
            statement.setString(1, email);
            statement.setString(2, password);
            statement.setString(3, email);
            statement.setString(4, password);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                //login successful, create and return a UserInfo object
                int id = resultSet.getInt("ID");
                String firstName = resultSet.getString("FirstName");
                String userType = resultSet.getString("UserType");
                return new UserInfo(id, firstName, userType);
            } else {
                //if no matching user found
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }


    protected void CreateArtist(String firstName, String lastName, String stageName, String DOB, String streetAddress, String zipCode, String city, String state, String email, String password) {
        if(!status) {
            System.out.println("ERROR: Connection Not Created");
            return;
        }

        //string of the query for Inserting an Artist
        String query = "INSERT INTO Artist (FirstName, LastName, StageName, DOB, StreetAddress, ZipCode, City, State, Email, Password, JoinDate, ActiveStatus) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            //using a prepared statement mitigated the risk of a sql injection attack
            PreparedStatement artistInsertStatement = conn.prepareStatement(query);
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
            artistInsertStatement.setString(11, String.valueOf(java.time.LocalDateTime.now()));
            artistInsertStatement.setString(12, "Active");
            artistInsertStatement.execute();
            CreateNewProfile();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

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
                userInsertStatement.setString(4, businessAddress); // BusinessAddress
                userInsertStatement.setString(5, city);            // City
                userInsertStatement.setString(6, zipCode);         // ZipCode
                userInsertStatement.setString(7, email);           // Email
                userInsertStatement.setString(8, password);        // Password
            } else {
                //set the parameters in the correct order for Personal users
                userInsertStatement.setString(4, city);            // City
                userInsertStatement.setString(5, zipCode);         // ZipCode
                userInsertStatement.setString(6, email);           // Email
                userInsertStatement.setString(7, password);        // Password
            }

            userInsertStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error while inserting user: " + e.getMessage());
        }
    }

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

    /* code for this method was created following the guide on https://cloudinary.com/documentation/java_quickstart **/
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



    protected void CreateNewProfile() {
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

            String query = "INSERT INTO ArtistProfile (ArtistID, ProfilePicture, ActiveStatus) VALUES (?, ?, ?);";

            PreparedStatement profileInsertStatement = conn.prepareStatement(query);
            profileInsertStatement.setInt(1, artistID);
            profileInsertStatement.setString(2, "http://res.cloudinary.com/dbvmjemlj/image/upload/v1728860327/profile-icon-design-free-vector.jpg");
            profileInsertStatement.setString(3, "Active");
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
            while(rs.next()) {
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

                if(profileSet.next()) {
                    int pID = profileSet.getInt("ProfileID");
                    String genre = profileSet.getString("Genre");
                    String profilePicture = profileSet.getString("ProfilePicture");
                    String featuredPerformance = profileSet.getString("FeaturedPerformance");
                    String activeStatus = profileSet.getString("ActiveStatus");

                    profile = new Profile(pID, id, genre, profilePicture, featuredPerformance, activeStatus);
                } else {
                    profile = null;
                }

                artist = new Artist(id,firstName,lastName,stageName,DOB,address, zipCode, city,state,email,password,joinDate, profile);
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

}