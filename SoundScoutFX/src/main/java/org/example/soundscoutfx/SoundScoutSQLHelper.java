package org.example.soundscoutfx;

import java.sql.*;

public class SoundScoutSQLHelper {
    Connection conn;
    boolean status = false;

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

    protected void CreateArtist(String firstName, String lastName, String stageName, String DOB, String streetAddress, String zipCode, String city, String state, String email, String password) {
        if(!status) {
            System.out.println("ERROR: Connection Not Created");
            return;
        }

        //String of the Query for Inserting an Artist
        String query = "INSERT INTO Artist (FirstName, LastName, StageName, DOB, StreetAddress, ZipCode, City, State, Email, Password, JoinDate, ActiveStatus) " + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            //Using a prepared statement mitigated the risk of a sql injection attack
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

            String query = "INSERT INTO ArtistProfile (ArtistID, ActiveStatus) VALUES (?, ?);";

            PreparedStatement profileInsertStatement = conn.prepareStatement(query);
            profileInsertStatement.setInt(1, artistID);
            profileInsertStatement.setString(2, "Active");
            profileInsertStatement.execute();

        } catch (SQLException e) {
            System.out.println("SQL Error while retrieving ArtistID: " + e.getMessage());
            e.printStackTrace();
            return;
        }
    }
}
