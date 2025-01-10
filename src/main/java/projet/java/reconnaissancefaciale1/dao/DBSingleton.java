package projet.java.reconnaissancefaciale1.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static java.lang.Class.forName;

public class DBSingleton {
    private static Connection conn;
    private static final String DB_URL = "jdbc:sqlite:gestion_access.db";

    static {
        try {
            Class.forName("org.sqlite.JDBC");
             conn = DriverManager.getConnection(DB_URL);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la connexion à la base de données.");
        }
    }
    public static Connection getConnection() {
        return conn;
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
