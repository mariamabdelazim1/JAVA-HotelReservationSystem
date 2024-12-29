package com.example.guihotelreservationsystem;

import javafx.application.Platform;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class invoice {
    private int invoiceId;
    private int reservationId;
    private double finalPrice;
    private double taxAmount;

    private Reservation reservation;

    // Constructor
    public invoice(Reservation reservation) {
        this.reservation = reservation;
        this.reservationId = reservation.getReservationId();
    }

    // Getters
    public int getInvoiceId() {
        return this.invoiceId;
    }

    public double getTaxAmount() {
        return this.taxAmount;
    }

    public double getFinalPrice() {
        return this.finalPrice;
    }

    // Calculate taxes (14% tax rate)
    public double calculateTaxes(double basePrice) {
        this.taxAmount = basePrice * 0.14; // Example tax rate: 14%
        return this.taxAmount;
    }

    // Calculate final price (base price + taxes)
    public double calculateFinalPrice(double basePrice) {
        this.finalPrice = basePrice + calculateTaxes(basePrice);
        return this.finalPrice;
    }

    /**
     * Save the invoice to the database.
     * @param onSuccess A callback executed if the operation succeeds.
     * @param onFailure A callback executed if the operation fails.
     * @param basePrice The base price for the reservation.
     */
    public void saveInvoiceToDatabase(Runnable onSuccess, Runnable onFailure, double basePrice) {
        // Calculate tax and final price
        calculateFinalPrice(basePrice);

        new Thread(() -> {
            String insertQuery = "INSERT INTO Invoice (reservation_id, taxes, finalprice) VALUES (?, ?, ?)";
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                // Establish a connection to the database
                connection = DatabaseConnection.connect();

                if (connection != null) {
                    // Prepare the SQL statement
                    preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                    preparedStatement.setInt(1, this.reservationId);
                    preparedStatement.setDouble(2, this.taxAmount);
                    preparedStatement.setDouble(3, this.finalPrice);

                    // Execute the statement
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        // Retrieve the generated invoice ID
                        ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                        if (generatedKeys.next()) {
                            this.invoiceId = generatedKeys.getInt(1);
                            System.out.println("Invoice saved successfully with ID: " + this.invoiceId);

                            // Execute the success callback on the JavaFX thread
                            if (onSuccess != null) {
                                Platform.runLater(onSuccess);
                            }
                        }
                    } else {
                        System.err.println("Failed to insert the invoice into the database.");
                        // Execute the failure callback on the JavaFX thread
                        if (onFailure != null) {
                            Platform.runLater(onFailure);
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("SQL error while saving invoice: " + e.getMessage());
                // Execute the failure callback on the JavaFX thread
                if (onFailure != null) {
                    Platform.runLater(onFailure);
                }
            } finally {
                // Close resources
                DatabaseConnection.closeResources(connection, preparedStatement, null);
            }
        }).start();
    }



    public void setData(Customer customer, Reservation reservation, double price, int numberOfRoomsValue) {
    }
}
