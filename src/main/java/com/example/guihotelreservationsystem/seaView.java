package com.example.guihotelreservationsystem;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class seaView extends Room {
    public seaView (String capacity)
    {
        super(capacity);

    }
    @ Override
    public  double CalculatePriceAsync()
    {
        double price= super.CalculatePriceAsync();
        double roomPrice=0;
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;


        try {
            connection = DatabaseConnection.connect();  // Get connection
            statement = connection.createStatement();   // Create statement
            String sql = "SELECT * from room where room_id = "+this.RoomID;
            resultSet = statement.executeQuery(sql);    // Execute query

            while (resultSet.next())
            {
                roomPrice = resultSet.getInt("roomPrice");

                System.out.println("roomPrice: " + roomPrice);
            }

        }
        catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            DatabaseConnection.closeResources(connection, statement, resultSet); // Close resources
        }
        this.Price=price+roomPrice;
        System.out.println("totalPrice: " + this.Price);
        return Price;

    }
}
