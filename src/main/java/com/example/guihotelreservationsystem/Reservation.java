package com.example.guihotelreservationsystem;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

public class Reservation {
    private int reservationId;// finished setting it to database and getting it from database
    private String inclusivity;//finished setting it to database and returning it
    private LocalDate dateOfCreation;//finished setting it to database and returning it
    private LocalDate checkInDate;// finshed setting it to database and returning it
    private LocalDate checkOutDate;//finshed setting it to database and returning it
    private Room room;
    public Customer customer;
    private boolean isCancelled;

    public Reservation(String inclusivity,LocalDate checkInDate,LocalDate checkOutDate,Room room) {
        this.inclusivity = inclusivity;
        this.dateOfCreation = LocalDate.now();
        this.checkInDate=checkInDate;
        this.checkOutDate=checkOutDate;
        this.room=room;
    }
    public Reservation (int reservationId){
        this.reservationId=reservationId;
    }
    public void saveToDatabase() {

        String insertQuery = "INSERT INTO reservation (inclusivity, dateCreated, checkInDate, checkOutDate) VALUES (?, ?, ?, ?)";
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;
        try {
            connection = DatabaseConnection.connect();
            if (connection != null) {
                preparedStatement = connection.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS);
                preparedStatement.setString(1, inclusivity);
                preparedStatement.setDate(2, java.sql.Date.valueOf(dateOfCreation));
                preparedStatement.setDate(3, java.sql.Date.valueOf(checkInDate));
                preparedStatement.setDate(4, java.sql.Date.valueOf(checkOutDate));

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    generatedKeys = preparedStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        this.reservationId = generatedKeys.getInt(1);
                    }
                    System.out.println("Reservation added successfully.");



                } else {
                    System.out.println("Failed to add the reservation.");
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
        } finally {
            DatabaseConnection.closeResources(connection, preparedStatement, null);
        }



    }

    public void setCustomer(Customer customer) {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Runnable saveTask = () -> {String updateQuery = "UPDATE reservation SET customer_id = ? WHERE reservation_id = ?";
            Connection connection = null;
            PreparedStatement preparedStatement = null;

            try {
                // Establish a database connection
                connection = DatabaseConnection.connect();

                if (connection != null) {
                    // Prepare the SQL query
                    preparedStatement = connection.prepareStatement(updateQuery);
                    preparedStatement.setInt(1, customer.getCustomerID()); // Set the customerId
                    preparedStatement.setInt(2, this.reservationId);       // Use the reservationId to identify the row

                    // Execute the query
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Customer ID set successfully for reservation ID: " + this.reservationId);
                    } else {
                        System.out.println("No reservation found with ID: " + this.reservationId);
                    }
                }
            } catch (SQLException e) {
                System.err.println("SQL error: " + e.getMessage());
            } finally {
                // Close resources
                DatabaseConnection.closeResources(connection, preparedStatement, null);
            }
        };
        // Submit the task for execution
        executorService.execute(saveTask);


        executorService.shutdown();

    }



    public double getPriceInclusivity(){
        double price=room.CalculatePriceAsync();
        ;
        if (inclusivity =="All Inclusive" ){
            price+=100;
        }
        else{
            price+=50;
        }
        System.out.println("price in reservation is "+price);
        return price;
    }
    public int cancelReservation() {
        AtomicInteger returnValue = new AtomicInteger();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Callable<Integer> saveTask = () -> {
            String selectQuery = "SELECT isCancelled FROM reservation WHERE reservation_id = ?";
            String updateQuery = "UPDATE reservation SET isCancelled = 1 WHERE reservation_id = ?";
            Connection connection = null;
            PreparedStatement selectStatement = null;
            PreparedStatement updateStatement = null;
            ResultSet resultSet = null;

            try {
                // Establish connection
                connection = DatabaseConnection.connect();

                if (connection != null) {
                    // Check if the reservation is already cancelled
                    selectStatement = connection.prepareStatement(selectQuery);
                    selectStatement.setInt(1, reservationId);
                    resultSet = selectStatement.executeQuery();

                    if (resultSet.next()) {
                        int isCancelled = resultSet.getInt("isCancelled");

                        if (isCancelled == 1) {
                            //System.out.println("Reservation is already cancelled.");
                            returnValue.set(1);
                        } else {
                            // Update the isCancelled column to 1
                            updateStatement = connection.prepareStatement(updateQuery);
                            updateStatement.setInt(1, reservationId);
                            int rowsAffected = updateStatement.executeUpdate();

                            if (rowsAffected > 0) {
                                //System.out.println("Reservation cancelled successfully.");
                                returnValue.set(2);
                            } else {
                                //System.out.println("Failed to cancel the reservation.");
                                returnValue.set(3);
                            }
                        }
                    } else {
                        //System.out.println("Reservation not found.");
                        returnValue.set(4);
                    }
                }
            } catch (SQLException e) {
                //System.err.println("SQL error: " + e.getMessage());
                returnValue.set(3);
            } finally {
                // Close resources
                DatabaseConnection.closeResources(connection, selectStatement, resultSet);
                DatabaseConnection.closeResources(null, updateStatement, null);
            }
            Room.cancelReservation2(reservationId);
            return returnValue.get();
        };
        // Submit the task for execution
        Future<Integer> future = executorService.submit(saveTask);

        try {
            // Wait for the result and return the value
            return future.get();  // This will block until the result is available
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            return 3;  // Return error code in case of failure
        } finally {
            executorService.shutdown();  // Shut down the executor
        }

    }
    public void setRoomReservationId(){
        room.asyncSetReservationId(reservationId);
    }


    public long getCountDays(){
        return  ChronoUnit.DAYS.between(checkInDate, checkOutDate)+1;
    }

    public int getReservationId(){
        return reservationId;
    }
    public String getInclusivity(){
        return inclusivity;
    }
    public LocalDate  getDateOfCreation(){
        return dateOfCreation;
    }
    public LocalDate getCheckInDate(){
        return checkInDate;
    }
    public LocalDate getCheckOutDate(){
        return checkOutDate;
    }
    public Room getRoom (){
        return room;
    }
//    public int getCustomerID(){
//      return customer.getCustomerID();
//    }


    // Getters and setters for other fields (optional)
}

