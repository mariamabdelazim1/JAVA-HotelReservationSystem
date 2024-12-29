package com.example.guihotelreservationsystem;

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

import java.time.LocalDate;

import static java.sql.Types.NULL;

public class reservationDetailsController {
    LocalDate checkInDate;
    LocalDate checkOutDate;
    int numberOfRoomsValue;
    String inclusivity = "";
    private int count;
    private Customer customer;
    private Reservation reservation;
    private  double price;

    @FXML
    private Label welcomeText;
    @FXML
    private Button saveReservationDetails;
    @FXML
    private DatePicker checkInDatePicker;;
    @FXML
    private DatePicker checkOutDatePicker;;
    @FXML
    private Spinner<Integer> numberOfRooms;
    //SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
    //numberOfRooms.setValueFactory(valueFactory);
    @FXML
    private RadioButton HalfBoradRadioButton;
    @FXML RadioButton allInclusiveRadioButton;


    @FXML
    private void initialize() {
        // Initialize the Spinner value factory here
        if (numberOfRooms != null) {
            numberOfRooms.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        } else {
            System.err.println("numberOfRooms Spinner is not initialized!");
        }
    }


    private void showAlert(Alert.AlertType alertType, String title, String header, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public  void ReservationDetails(ActionEvent event)
    { try {
        // Get Check-in and Check-out dates
        checkInDate = checkInDatePicker.getValue();
        if (checkInDate == null)  // Make sure the value is not null
        {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Missing Dates", "Check-In Date is REQUIRED FIELD!.");
            return;// Stop execution if data are missing
        }
        checkOutDate = checkOutDatePicker.getValue();
        if (checkOutDate == null) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Missing Dates", "Check-Out Date is REQUIRED FIELD!.");
            return;
        }
        // Get the number of rooms from the spinner
        numberOfRoomsValue = (Integer) numberOfRooms.getValue(); //only works with objects
        if (numberOfRoomsValue == NULL || numberOfRoomsValue < 0)  // Make sure the value is not null
        {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid Number of Rooms", "Please select a valid number of rooms.");
            return;
        }

        if (HalfBoradRadioButton.isSelected())
            inclusivity = "Half Board";
        else if (allInclusiveRadioButton.isSelected())
            inclusivity = "All Inclusive";
        else {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Inclusivity Missing", "inclusivity is a REQUIRED FIELD!");
            return;
        }
        //showAlert(Alert.AlertType.INFORMATION, "Reservation Success", "Reservation Saved", "Your reservation has been saved successfully!");
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("roomChoice.fxml"));
        Parent root = fxmlLoader.load();
        roomChoiceController controller = fxmlLoader.getController();
        controller.setReservationDetails(checkInDate, checkOutDate, numberOfRoomsValue, inclusivity,count,reservation ,customer, price);
        // Scene scene = new Scene(fxmlLoader.load(), 781,  437);
        Scene scene = new Scene(root, 756,  437);
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setTitle("");
        stage.setScene(scene);
        stage.show();

    }
    catch(Exception e)
    {
        System.out.println(e.getMessage());
        e.printStackTrace();
    }
    finally {
        System.out.println("check in :"+checkInDate);
        System.out.println("check out :"+checkOutDate);
        //  System.out.println("number of rooms:  "+numberOfRoomsValue);
        System.out.println("Inclusivity :"+ inclusivity);
    }

    }
    public void setCustomerData (Customer customer)
    {
        this.customer= customer;
        System.out.println("customer info in reservation details controller  :"+ customer.getFName());
    }
}