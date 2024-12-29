package com.example.guihotelreservationsystem;

import com.example.guihotelreservationsystem.mainClass;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

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
import java.sql.SQLException;
import java.time.LocalDate;

public class capacityController {
    Customer customer;
    private  double price;

    private int count = 0;
    private String capacity;
    @FXML
    private Button singleButton;
    @FXML
    private Button doubleButton;
    @FXML
    private Button tripleButton;
    @FXML
    private Button suiteButton;
    @FXML
    private Button returnButton;
    private String roomType;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int numberOfRoomsValue;
    private String inclusivity;
    Reservation reservation;


    public void setRoomDetails(String roomType, LocalDate checkInDate, LocalDate checkOutDate, int numberOfRoomsValue, String inclusivity, int count, Reservation reservation, Customer customer, double price) {
        this.roomType = roomType;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.numberOfRoomsValue = numberOfRoomsValue;
        this.inclusivity = inclusivity;
        this.count = count;
        this.customer=customer;
        this.reservation = reservation;
        this.price= price;
        // Debugging: Print received values
        System.out.println("Room Type: " + roomType);
        System.out.println("Check-in: " + checkInDate);
        System.out.println("Check-out: " + checkOutDate);
        System.out.println("Number of Rooms: " + numberOfRoomsValue);
        System.out.println("Inclusivity: " + inclusivity);
        System.out.println("customer info in capacity  :"+ customer.getFName());
        System.out.println("price in capacity controller"+ price);
    }

    public void setCapacity(ActionEvent event) throws IOException {
        Button clickedButton = (Button) event.getSource();

        // Check which button was clicked
        if (clickedButton.getId().equals("singleButton")) {
            System.out.println("single clicked");
            capacity = "single";

        } else if (clickedButton.getId().equals("doubleButton")) {
            System.out.println("doubleButton clicked");
            capacity = "double";
        } else if (clickedButton.getId().equals("tripleButton")) {
            System.out.println("tripleButton clicked");
            capacity = "triple";
        } else {
            System.out.println("suiteButton clicked");
            capacity = "suite";
        }
        saveRoomDetails();
        if (count < numberOfRoomsValue - 1) {
            System.out.println("entered if in capacity controller");
            //saveRoomDetails();
            System.out.println("before incrementing " + count);
            count++;
            System.out.println("after incrementing count = " + count);
            navigateToRoomChoice(event, reservation);
        } else {
            System.out.println("entered else in capacity controller");
            navigateToInvoice(event);
        }


    }
    @FXML
    public void handleReturnButton(ActionEvent event) throws IOException {
        // Load the roomChoice.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("roomChoice.fxml"));
        Parent root = fxmlLoader.load();

        // Pass data if needed
        roomChoiceController controller = fxmlLoader.getController();
        controller.setReservationDetails(checkInDate, checkOutDate, numberOfRoomsValue, inclusivity, count, reservation, customer, price);

        // Set the scene and display it
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    private void saveRoomDetails() {
        Room r1;

        if (roomType.equals("sea view"))
            r1 = new seaView(capacity);

        else if (roomType.equals("pool view"))
            r1 = new poolView(capacity);
        else
            r1 = new gardenView(capacity);
        //     price+=((r1.CalculatePriceAsync()+getInclusivityRate(inclusivity))*reservation.getCountDays());

        int available= r1.GetRoomID(roomType,checkInDate);
        if (available<0)
        {
            showAlert(Alert.AlertType.ERROR, "Unavailable Room!", "", "Room is not available. Please choose another room type!");
            return;
        }


        if (count == 0) {
            reservation = new Reservation(inclusivity, checkInDate, checkOutDate, r1);
            reservation.saveToDatabase();
            System.out.println("reservation id in capacity controller: "+reservation.getReservationId());
            //customer.saveToDatabase();
            reservation.setCustomer(customer);
            r1.asyncSetReservationId(reservation.getReservationId());
        } else
            r1.asyncSetReservationId(reservation.getReservationId());
        System.out.println("calculatePrice asyn: "+r1.CalculatePriceAsync());
        System.out.println("inclusivty : "+getInclusivityRate(inclusivity));
        System.out.println("reservation count days:"+reservation.getCountDays());
        price+=((r1.CalculatePriceAsync()+getInclusivityRate(inclusivity))*reservation.getCountDays());
    }

    private void navigateToRoomChoice(ActionEvent event, Reservation reservation) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("roomChoice.fxml"));
        Parent root = fxmlLoader.load();
        roomChoiceController controller = fxmlLoader.getController();
        controller.setReservationDetails(checkInDate, checkOutDate, numberOfRoomsValue, inclusivity, count, reservation ,customer, price);

        Scene scene = new Scene(root, 781, 500);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    private void navigateToReservationDetails(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("reservation.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 550, 550);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    public int getInclusivityRate (String inclusivity)
    {
        if (inclusivity =="All Inclusive" )
            return 100;

        else
            return 50;


    }
    private void navigateToInvoice(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("Invoice.fxml"));
        Parent root = fxmlLoader.load();
        invoiceController controller = fxmlLoader.getController();
        controller.setData(customer,reservation,price,numberOfRoomsValue);

        Scene scene = new Scene(root);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
}