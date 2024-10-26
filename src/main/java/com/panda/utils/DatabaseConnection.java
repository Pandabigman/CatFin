package com.panda.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

//import io.github.cdimascio.dotenv.Dotenv;


public class DatabaseConnection {
    private static Connection connection;

    public static Connection getConnection() {
        Dotenv dotenv = Dotenv.configure().load();

        String dbUrl = dotenv.get("DB_URL");
        String dbUser = dotenv.get("DB_USER");
        String dbPassword = dotenv.get("DB_PASSWORD");
        if (connection == null) {
            try {
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                //System.out.println("Connected to the database!");
            } catch (SQLException e) {
                e.printStackTrace();
                // Handle connection error (e.g., throw a custom exception)
            }
        }
        return connection;
    }
}