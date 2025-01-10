package projet.java.reconnaissancefaciale1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {
    private static final String DB_URL = "jdbc:sqlite:gestion_access.db";

    public static Connection connection() {
        try {
            // Establish a connection to the SQLite database
            Connection conn = DriverManager.getConnection(DB_URL);
            System.out.println("Connection to SQLite has been established.");
            return conn;
        } catch (SQLException e) {
            // Handle a ny SQL exceptions
            throw new RuntimeException("Error while connecting to SQLite: " + e.getMessage(), e);
        }
    }
}
