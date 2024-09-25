package org.example.soundscoutfx;

import java.sql.*;

public class SoundScoutSQLHelper {
    Connection conn;

    public Connection establishConnection() {
        {
            try {
                conn = DriverManager.getConnection("jdbc:sqlserver://soundscout.database.windows.net:1433;database=SoundScout;user=SoundScoutAdmin@soundscout;password=SoundScout!!;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;");
                return conn;
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void testConnection() {
        Connection con;

        con = establishConnection();

        try {
            Statement statement = con.createStatement();
            ResultSet results = statement.executeQuery("Select * From dbo.Customer;");

            while(results.next()) {
                System.out.println(results.getString("FirstName"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);

        }
    }
}
