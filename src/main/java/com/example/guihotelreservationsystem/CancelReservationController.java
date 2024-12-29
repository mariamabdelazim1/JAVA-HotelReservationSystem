package com.example.guihotelreservationsystem;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class CancelReservationController {
    @FXML
    private Button cancelButton;
    @FXML
    private TextField reservationTextField;
    @FXML
    private Button returnButton;
    @FXML
    private void initialize() {
        try {
            // Initialization code
        } catch (Exception e) {
            e.printStackTrace(); // Log initialization errors
        }
    }
    public void textFieldInput(ActionEvent e){
        cancelReservation(e);
    }



    public void cancelReservation(ActionEvent event) {
        int returnValue = 0;
        try {
            int cancelReservationID = Integer.parseInt(reservationTextField.getText());
            Reservation reservation = new Reservation(cancelReservationID);
            returnValue = reservation.cancelReservation();
        } catch (NumberFormatException e) {
            showAlertError("Please enter a valid numeric reservation ID.");
        }

        if (returnValue == 1) {
            showAlertInfo("This Reservation was already cancelled");
        } else if (returnValue == 2) {
            // Successfully cancelled, navigate to the success form
            showAlertInfo("Sucessfully Cancelled reservation number "+ reservationTextField.getText());
            // Close the current window after cancellation
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            stage.close();

        } else if (returnValue == 3) {
            showAlertError("Failed to cancel the reservation.");
        } else if (returnValue == 4) {
            showAlertError("Reservation was not found. PLease Enter a Valid ID!");
        } else {
            showAlertError("An Error Occurred");
        }
    }
    @FXML
    public void handleReturnButton(ActionEvent event) throws IOException {
        // Navigate back to old-customer.fxml
        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("old-customer.fxml"));
        Parent root = fxmlLoader.load();

        // Set the new scene
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    public void showAlertError(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public void showAlertInfo(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid Input");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

    }

}