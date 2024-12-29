package com.example.guihotelreservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class OldCustomerController {
    @FXML
    private Button CancelAReservation;
    @FXML
    private Button checkCustomerIdButton;
    @FXML
    TextField customerIdTextField;
    @FXML
    PasswordField passwordTextField;

    private Customer customer;
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);  // No header
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void checkCustomerId(javafx.event.ActionEvent actionEvent) {
        // Check if the password field is empty
        if (passwordTextField.getText().isEmpty()) {
            showAlert("Error", "Password cannot be empty. Please enter your password.");
            return; // Exit the method if the password is not entered
        }

        try {
            // Get the customer ID from the input field
            int customerId = Integer.parseInt(customerIdTextField.getText());

            // Get the password from the password text field
            String password = passwordTextField.getText();

            // Check if the customer exists and the password matches
            if (Customer.checkCustomerCredentials(customerId, password)) {
                // Customer found and password matched, create a Customer object
                customerIdTextField.setDisable(true);
                passwordTextField.setDisable(true);

                // Load customer details from the database
                this.customer = new Customer(customerId);
                customer.loadFromDatabase();

                // Display success alert with customer details
                showAlert("Customer Verified",
                        "Customer with ID " + customerId + " was verified successfully.\n" +
                                "Name: " + customer.getFName() + " " + customer.getLName() + "\n" +
                                "Email: " + customer.getEmail());
            } else {
                // Invalid customer ID or password
                showAlert("Verification Failed",
                        "Invalid customer ID or password. Please try again.");
            }
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Please enter a valid customer ID.");
        }
    }



    public void CancelReservation(ActionEvent event) {
        if (customerIdTextField.isDisable())
        {
            try {
                // Load the FXML file for the new form

                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("CancelReservation.fxml"));
                Parent root = fxmlLoader.load();

                // Create a new Stage for the Customer Form
                Stage stage = new Stage();
                stage.setTitle("Cancel Reservation");

                // Set the Scene using the loaded root (FXML layout)
                stage.setScene(new Scene(root, 330, 500));

                stage.show();

                // Close the current window
                Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                currentStage.close();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error opening customer form", e);
            }
        }
        else{
            showAlert("Error","Please enter a correct customer ID or password ");
        }
    }
    public void newReservation(ActionEvent event) {
        if (customerIdTextField.isDisable()){
            try {
                // Load the FXML file for the new form
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("reservationDetails.fxml"));
                Parent root = fxmlLoader.load();
                reservationDetailsController controller = fxmlLoader.getController();
                System.out.println("customer in oldcustomar is " + customer.getCustomerID());
                controller.setCustomerData(customer);

                Scene scene = new Scene(root, 550, 550);
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(scene);
                stage.setTitle("Reservation Details");

                // Set the Scene using the loaded root (FXML layout)
                //stage.setScene(new Scene(root, 550, 550));

                stage.show();

                // Close the current window
                //Stage currentStage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();

                //currentStage.close();

            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Error opening reservation form", e);
            }
        }
        else{
            showAlert("Error","Please enter a correct customer ID or password");
        }

    }
}




