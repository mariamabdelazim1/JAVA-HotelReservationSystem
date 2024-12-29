package com.example.guihotelreservationsystem;

import java.sql.*;
import java.time.LocalDate;

public class Room {

    protected int RoomID;//database input get only
    protected String Capacity;//user input
    protected double Price; //  database input get only
    protected int reservationID;


    public void SetCapacity(String capacity)
    {
        this.Capacity=capacity;
    }



    public int GetRoomID(String roomType, LocalDate checkInDateInput) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int roomId = -1;

        try {
            // Step 1: Establish connection using the DatabaseConnection class
            connection = DatabaseConnection.connect();

            // Step 2: Query to find an available room first
            String availableRoomQuery = "SELECT DISTINCT room_id FROM room " +
                    "WHERE capacity = ? AND isAvailable = 1 AND roomType = ?";

            preparedStatement = connection.prepareStatement(availableRoomQuery);
            preparedStatement.setString(1, this.Capacity);
            preparedStatement.setString(2, roomType);

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                // Available room found
                roomId = resultSet.getInt("room_id");
                System.out.println("Room ID: " + roomId);
            } else {
                // No available rooms, check reservations
                System.out.println("No available room found. Checking reservations...");

                // Close previous resources
                resultSet.close();
                preparedStatement.close();

                // Step 3: Query to check reservations with check_out_date < checkInDateInput
                String reservationQuery = "SELECT r.room_id " +
                        "FROM room r JOIN reservation res ON r.reservation_id = res.reservation_id " +
                        "WHERE r.roomType = ? AND r.capacity = ? " +
                        "AND res.checkOutDate < ?";

                preparedStatement = connection.prepareStatement(reservationQuery);
                preparedStatement.setString(1, roomType);
                preparedStatement.setString(2, this.Capacity);
                preparedStatement.setDate(3, Date.valueOf(checkInDateInput));

                resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    // Room found where reservation ends before check-in date
                    roomId = resultSet.getInt("room_id");
                    System.out.println("Room available after reservation ends. Room ID: " + roomId);
                } else {
                    System.out.println("No rooms will be available based on your check-in date.");
                }
            }
        } catch (SQLException e) {
            // Handle SQL exception
            e.printStackTrace();
        } finally {
            // Step 4: Close resources
            DatabaseConnection.closeResources(connection, preparedStatement, resultSet);
        }

        this.RoomID = roomId;
        return RoomID;
    }





    // desc: async func calculates the price of room based on capacity only + override method for child class
    public double CalculatePriceAsync()
    {
        Thread t2= new Thread (new calculatePriceTask());
        t2.start();
        return Price;
    }

    //non-static class to perform the async operation of calc price of room
    private class calculatePriceTask implements Runnable {
        public calculatePriceTask(){

        }
        @Override
        public void run(){
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            double price=0;

            try {
                connection = DatabaseConnection.connect();  // Get connection
                statement = connection.createStatement();   // Create statement
                String sql = "SELECT * from room where room_id = "+RoomID;
                resultSet = statement.executeQuery(sql);    // Execute query

                while (resultSet.next())
                {
                    price = resultSet.getInt("price");

                    System.out.println("price: " + price);
                }

            }
            catch (SQLException e) {
                e.printStackTrace();
            }
            finally {
                DatabaseConnection.closeResources(connection, statement, resultSet); // Close resources
            }
            Price=price;


        }

    }

    // desc: return capacity givenn by the user
    public String getCapacity()
    {
        return this.Capacity;
    }

    //desc: async func to set reservation in room table
    public void asyncSetReservationId(int reservationId)
    {
        Thread t2= new Thread( new setReservationIDTask(reservationId));
        t2.start();

    }


    //desc: non-static class implements from runnable to do the async operation
    private class setReservationIDTask implements Runnable
    {
        private int reservationId;
        public setReservationIDTask(int reservationId){
            this.reservationId=reservationId;
        }
        @Override
        public void run()
        {  Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;

            try {
                // Step 1: Establish connection using the DatabaseConnection class
                connection = DatabaseConnection.connect();

                // Step 2: Create a Statement
                statement = connection.createStatement();

                // Step 3: SQL query to get room details
                String sql = "UPDATE room SET reservation_id = " + reservationId + " WHERE room_id = " + RoomID;
                String sql2= "UPDATE room SET  isAvailable = 0 WHERE room_id = " + RoomID;
                System.out.println(RoomID);
                // Step 4: Execute the query and get the result set
                int test= statement.executeUpdate(sql);
                if (test>0)
                {
                    System.out.println("successfull setting of reservation ");
                    reservationID=reservationId;
                }
                else
                    System.out.println("unsuccessfull");

                int test2= statement.executeUpdate(sql2);
                if (test2>0)
                {
                    System.out.println("successfull setting of isAvaible with 0 ");
                    reservationID=reservationId;
                }
                else
                    System.out.println("unsuccessfull setting isAvaiavle ");




            } catch (SQLException e) {
                // Handle SQLException if any occurs
                e.printStackTrace();
            } finally {
                // Step 6: Close resources using the DatabaseConnection class
                DatabaseConnection.closeResources(connection, statement, resultSet);
            }


        }

    }
    // desc: constructor to get the data needed for room class
    public Room(String capacity)
    {
        this.SetCapacity(capacity);

    }

    //desc:  Async func to update isAvaiable =1 and reservation_ID null
    public void CancelReservation(int reservationID)
    {
        Thread t4= new Thread(new CancelReservationTask(reservationID) );
        t4.start();
    }

    private class CancelReservationTask implements Runnable
    {
        private  int reservationId;
        public CancelReservationTask (int reservationID)
        {
            this.reservationId =reservationID;
        }
        @Override
        public void run()
        {
            Connection connection = null;
            Statement statement = null;
            ResultSet resultSet = null;
            int roomId=0;

            try {
                // Step 1: Establish connection using the DatabaseConnection class
                connection = DatabaseConnection.connect();

                // Step 2: Create a Statement
                statement = connection.createStatement();

                // Step 3: SQL query to get room details
                String sql1 = "UPDATE room SET isAvailable = 1  WHERE reservation_id = " + reservationId;
                int test= statement.executeUpdate(sql1);
                if (test>0)
                {
                    System.out.println("successfull setting isAvaiable with 1 ");
                }
                else
                    System.out.println("unsuccessfull isAvaiable with 1 ");

                String sql3= "Select * from room where reservation_id ="+ reservationId;
                resultSet = statement.executeQuery(sql3);

                // Step 5: Process the result set
                if (resultSet.next())
                {
                    roomId = resultSet.getInt("room_id");  // Replace wString roomType = resultSet.getString("room_type");  // Replace with your actual column name
                    System.out.println("Room ID: " + roomId +"  cancel reservation : "+ reservationId);
                }




                String sql2 = "UPDATE room SET reservation_id = NULL WHERE room_id = " + roomId;
                ;

                // Step 4: Execute the query and get the result set
                int test1= statement.executeUpdate(sql2);
                if (test1>0)
                {
                    System.out.println("successfull deleting   reservationid ");
                }
                else
                    System.out.println("unsuccessfull deleting   reservationid");




            } catch (SQLException e) {
                // Handle SQLException if any occurs
                e.printStackTrace();
            } finally {
                // Step 6: Close resources using the DatabaseConnection class
                DatabaseConnection.closeResources(connection, statement, resultSet);
            }


        }

    }



    //desc:Cancel reservation alr created  update isAvaiable =1 and reservation_ID null
    public  static void cancelReservation2(int reservationID)
    {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        int roomId=0;

        try {
            // Step 1: Establish connection using the DatabaseConnection class
            connection = DatabaseConnection.connect();

            // Step 2: Create a Statement
            statement = connection.createStatement();

            // Step 3: SQL query to get room details
            String sql1 = "UPDATE room SET isAvailable = 1  WHERE reservation_id = " + reservationID;
            int test= statement.executeUpdate(sql1);
            if (test>0)
            {
                System.out.println("successfull setting isAvaiable with 1 ");
            }
            else
                System.out.println("unsuccessfull isAvaiable with 1 ");

            String sql3= "Select * from room where reservation_id ="+reservationID;
            resultSet = statement.executeQuery(sql3);

            // Step 5: Process the result set
            if (resultSet.next())
            {
                roomId = resultSet.getInt("room_id");
                System.out.println("Room ID: " + roomId +"  cancel reservation : "+reservationID );
            }




            String sql2 = "UPDATE room SET reservation_id = NULL WHERE room_id = " + roomId;
            ;

            // Step 4: Execute the query and get the result set
            int test1= statement.executeUpdate(sql2);
            if (test1>0)
            {
                System.out.println("successfull deleting   reservationid ");
            }
            else
                System.out.println("unsuccessfull deleting   reservationid");




        } catch (SQLException e) {
            // Handle SQLException if any occurs
            e.printStackTrace();
        } finally {
            // Step 6: Close resources using the DatabaseConnection class
            DatabaseConnection.closeResources(connection, statement, resultSet);
        }
    }


    //desc: overloaded method - getting room_id from class
    public int getRoomID(){
        return this.RoomID;
    }


}