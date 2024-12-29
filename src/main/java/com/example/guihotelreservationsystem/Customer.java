package com.example.guihotelreservationsystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Customer {

    private String Fname;
    private String Lname;
    private int age;
    private String email;
    private int customerID;
    private int customernationatID;
    private String password;
    // Constructor to initialize a Customer object with data from the user
    public Customer(String Fname, String Lname, int age, String email, int customernationatID,String password) {
        this.Fname = Fname;
        this.Lname = Lname;
        if (age >= 18) {
            this.age = age;
        } else {
            throw new IllegalArgumentException("Age must be 18 or older.");
        }
        this.email = email;
        this.customernationatID = customernationatID;
        this.password=password;
    }

    // Constructor to initialize a Customer object from the database using customerID
    public Customer(int customerID) {
        this.customerID = customerID;
        loadFromDatabase();
    }


    // Setters and Getters
    public void setAge(int age) {
        if (age >= 18) {
            this.age = age;
        } else {
            System.out.println("Error: Age must be 18 or older.");
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setCustomernationalID(int customernationalID) {
        this.customernationatID = customernationalID;
    }

    public String getFName() {
        return Fname;
    }

    public String getLName() {
        return Lname;
    }

    public int getAge() {
        return age;
    }

    public String getEmail() {
        return email;
    }

    public int getCustomerID() {
        return customerID;
    }

    public int getCustomernationatID() {
        return customernationatID;
    }

    // Method to save customer to the database
    public void saveToDatabase(Runnable onSuccess, Runnable onFailure) {
        new Thread(() -> {
            try (Connection connection = DatabaseConnection.connect()) {
                String query = "INSERT INTO Customer (Fname, Lname, age, email, customernationalID,password) VALUES (?, ?, ?, ?, ?,?)";
                // Prepare the statement with RETURN_GENERATED_KEYS to retrieve the auto-generated ID
                PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

                // Set the values for the prepared statement
                stmt.setString(1, this.Fname);
                stmt.setString(2, this.Lname);
                stmt.setInt(3, this.age);
                stmt.setString(4, this.email);
                stmt.setInt(5, this.customernationatID);
                stmt.setString(6, this.password);

                // Execute the update
                int affectedRows = stmt.executeUpdate();

                if (affectedRows > 0) {
                    // Retrieve the generated keys
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            this.customerID = generatedKeys.getInt(1); // Fetch the generated ID
                            System.out.println("Customer saved to database successfully with ID: " + this.customerID);
                        } else {
                            System.err.println("Failed to retrieve generated customerID.");
                        }
                    }
                    // Trigger the success callback
                    if (onSuccess != null) {
                        onSuccess.run();
                    }
                } else {
                    System.err.println("Failed to add the customer to the database.");
                    if (onFailure != null) {
                        onFailure.run();
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                // Trigger the failure callback in case of an exception
                if (onFailure != null) {
                    onFailure.run();
                }
            }
        }).start(); // Start the thread
    }

    public static boolean checkCustomerCredentials(int customerId, String password) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean isValid = false;

        try {
            // Establish connection
            connection = DatabaseConnection.connect();

            // SQL query to check customer ID and password
            String sql = "SELECT * FROM customer WHERE customer_id = ? AND password = ?";
            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setInt(1, customerId);
            preparedStatement.setString(2, password);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // If a record exists, the credentials are valid
            isValid = resultSet.next();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseConnection.closeResources(connection, preparedStatement, resultSet);
        }

        return isValid;
    }


    // Method to load customer data from the database based on customerID
    public void loadFromDatabase() {
        Connection connection = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;
        try {
            // Step 1: Establish connection using the DatabaseConnection class
            connection = DatabaseConnection.connect();

            // Step 2: SQL query to retrieve customer data based on customerID
            String query = "SELECT Fname, Lname, age, email, customernationalID, password FROM Customer WHERE customer_ID = ?";

            // Step 3: Prepare the statement
            stmt = connection.prepareStatement(query);
            stmt.setInt(1, this.customerID);

            // Step 4: Execute the query
            resultSet = stmt.executeQuery();

            // Step 5: If data is found, set the fields
            if (resultSet.next()) {
                this.Fname = resultSet.getString("Fname");
                this.Lname = resultSet.getString("Lname");
                this.age = resultSet.getInt("age");
                this.email = resultSet.getString("email");
                this.customernationatID = resultSet.getInt("customernationalID");
                this.password = resultSet.getString("password");
                System.out.println("Customer data loaded successfully." );
            } else {
                System.out.println("No customer found with the provided customerID.");
            }

        } catch (SQLException e) {
            // Handle SQLException if any occurs
            System.err.println("Failed to load customer data from database: " + e.getMessage());
        } finally {
            // Step 6: Close resources using the DatabaseConnection class
            DatabaseConnection.closeResources(connection, stmt, resultSet);
        }
    }
}
