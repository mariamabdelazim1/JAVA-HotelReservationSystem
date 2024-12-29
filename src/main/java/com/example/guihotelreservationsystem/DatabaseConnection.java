
package com.example.guihotelreservationsystem;
import java.sql.*;


public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/hotelreservationsystem"; // Your database URL
    private static final String USER = "root";   // Your database username
    private static final String PASSWORD = "123456"; // Your database password

    // Method to establish a connection to the database
    public static Connection connect() {
        Connection connection = null;
        try {
            //Step 1: Register JDBC driver (optional in newer JDBC versions)
            // Class.forName("com.mysql.cj.jdbc.Driver");

            // Step 2: Establish a connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to the database successfully.");
        } catch (SQLException e) {
            // Handle exceptions
            System.err.println("Connection failed: " + e.getMessage());
        }
        return connection;
    }

    // Method to close the resources (Connection, Statement, ResultSet)
    public static void closeResources(Connection connection, Statement statement, ResultSet resultSet) {
        try {
            if (resultSet != null) {
                resultSet.close(); // Close ResultSet
            }
            if (statement != null) {
                statement.close(); // Close Statement
            }
            if (connection != null) {
                connection.close(); // Close Connection
            }
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }


    }

}

