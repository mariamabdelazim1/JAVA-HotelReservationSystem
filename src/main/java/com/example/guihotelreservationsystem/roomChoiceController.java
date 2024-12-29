package com.example.guihotelreservationsystem;


import javafx.fxml.FXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Import Parent class
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;

import static java.sql.Types.NULL;


public class roomChoiceController    {
    private Customer customer;
    @FXML
    private Button seaViewButton;
    @FXML
    private Button poolViewButton;
    @FXML
    private int count ;
    private Button gardenViewButton;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfRoomsValue;
    private String inclusivity;
    private String roomType;
    private Reservation reservation;
    private  double price;
    // Setter to receive data from reservationDetailsController
    public void setReservationDetails(LocalDate checkInDate, LocalDate checkOutDate, int numberOfRoomsValue, String inclusivity ,int count, Reservation reservation, Customer customer, double price) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRoomsValue = numberOfRoomsValue;
        this.inclusivity = inclusivity;
        this.count=count;
        this.reservation=reservation;
        this.customer=customer;
        this.price= price;
        //this.count=count;
        // Debugging: Print received values
        System.out.println("Check-in: " + this.checkInDate);
        System.out.println("Check-out: " + this.checkOutDate);
        System.out.println("Number of Rooms: " + this.numberOfRoomsValue);
        System.out.println("Inclusivity: " + this.inclusivity);
        System.out.println("customer info in room choice :"+ customer.getFName());
        System.out.println("room choice price: "+ price);
    };
    public void saveRoomType(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();

        // Check which button was clicked
        if (clickedButton.getId().equals("seaViewButton")) {
            System.out.println("seaViewButton clicked");
            roomType="sea view";

        } else if (clickedButton.getId().equals("poolViewButton")) {
            System.out.println("poolViewButton clicked");
            roomType="pool view";
        } else if (clickedButton.getId().equals("gardenViewButton")) {
            System.out.println("gardenViewButton clicked");
            roomType="garden view";
        }
        navigateToCapacity(event);

    }
    private void navigateToCapacity(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("capacity.fxml"));
        Parent root = fxmlLoader.load();
        capacityController controller = fxmlLoader.getController();
        controller.setRoomDetails(roomType, checkInDate, checkOutDate, numberOfRoomsValue, inclusivity,count,reservation,customer, price);
        if(reservation !=null)
            System.out.println("reservation in roomcontroller reservationid is " + reservation.getReservationId());
        Scene scene = new Scene(root, 823, 548);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}