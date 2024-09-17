//SQLite database connection class
package models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Database {

    private static final String URL = "jdbc:sqlite:soundscout.db";

