package com.example.guihotelreservationsystem;





import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.util.concurrent.CompletableFuture;


public class CustomerFormController {
    int age;
    private Customer customer;

    @FXML
    private TextField fnameField, lnameField, emailField,customernationalIDField;
    @FXML
    private PasswordField passwordTextField;
    @FXML
    private Spinner<Integer> ageSpinner;
    SpinnerValueFactory<Integer> svf =new SpinnerValueFactory.IntegerSpinnerValueFactory(18,100,1);
    @FXML
    private void initialize(){
        ageSpinner.setValueFactory(svf);
    }


    @FXML
    protected void onSaveButtonClick(ActionEvent event) {
        boolean isValid = validateInput(); // Validate inputs before proceeding

        if (!isValid) {
            return; // Stop further execution if validation fails
        }

        try {
            // Retrieve input values
            String fname = fnameField.getText();

            // System.out.println("fname"+ fname);
            String lname = lnameField.getText();


            String email = emailField.getText();

            String password =passwordTextField.getText();



            age = (Integer) ageSpinner.getValue();

            int customerID = (Integer.parseInt(customernationalIDField.getText()));


            // Create and save customer
            customer = new Customer(fname, lname, age, email, customerID,password);
            //  customer.saveToDatabase();
            //int customerid= customer.getCustomerID();
            // showAlert(Alert.AlertType.INFORMATION, "Success", "Customer saved successfully! Customer id is "+customer.getCustomerID() );
            customer.saveToDatabase(() -> {
                // Success callback
                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Customer saved successfully! Customer ID is " + customer.getCustomerID());

                    // Open reservation details form
                    try {
                        FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("reservationDetails.fxml"));
                        Parent root = fxmlLoader.load();
                        reservationDetailsController controller = fxmlLoader.getController();
                        controller.setCustomerData(customer);

                        Stage stage = new Stage();
                        stage.setTitle("Reservation Form");
                        stage.setScene(new Scene(root, 550, 550));
                        stage.show();
                        Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
                        currentStage.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to open reservation form: " + e.getMessage());
                    }
                });
            }, () -> {
                // Failure callback
                Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Failed to save customer to database."));
            });
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    protected void onClearButtonClick() {
        clearFields();
        resetFieldStyles(); // Reset field styles when the user clears the form
    }

    private void clearFields() {
        fnameField.clear();
        lnameField.clear();
        emailField.clear();
        ageSpinner.getValueFactory().setValue(18); // Reset to default age
    }

    private boolean validateInput() {
        resetFieldStyles(); // Reset styles before validation
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();

        // Validate first name
        if (fnameField.getText().trim().isEmpty()) {
            setErrorStyle(fnameField);
            errorMessage.append("- First Name cannot be empty.\n");
            isValid = false;
        }

        // Validate last name
        if (lnameField.getText().trim().isEmpty()) {
            setErrorStyle(lnameField);
            errorMessage.append("- Last Name cannot be empty.\n");
            isValid = false;
        }
        // Spinner<Integer> ageSpinner = new Spinner<>(18, 100, 18); // Min: 18, Max: 100, Initial: 18

        // Validate age
        age =(Integer) ageSpinner.getValue();
        System.out.print("age"+age);// Use Integer instead of int
        if ( age < 18) {// Handle null and check age

            setErrorStyle((Region) ageSpinner.getEditor()); // Highlight spinner editor
            errorMessage.append("- Age must be 18 or older.\n");
            isValid = false;
        }
        // validate Customer id
        String customerIDText = customernationalIDField.getText().trim();
        if (customerIDText.isEmpty()) {
            setErrorStyle(customernationalIDField);
            showAlert(Alert.AlertType.ERROR, "Validation Error", "- Customer National ID cannot be empty.");
            isValid = false;
        }

        // Validate email
        String email = emailField.getText().trim();
        if (!email.matches("^[\\w-_.]+@[\\w]+\\.com$")) { // Basic email validation
            setErrorStyle(emailField);
            errorMessage.append("- Email must be in format 'example@domain.com'.\n");
            isValid = false;
        }
        if (passwordTextField.getText().isEmpty()) {
            setErrorStyle(passwordTextField);
            showAlert(Alert.AlertType.ERROR, "Validation Error", "- password cannot be empty.");
            isValid = false;
        }
        // Show error message if any validation fails
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", errorMessage.toString());
        }

        return  isValid;
    }

    private void resetFieldStyles() {
        // Reset all fields to default style
        fnameField.setStyle("");
        lnameField.setStyle("");
        emailField.setStyle("");
        ageSpinner.getEditor().setStyle(""); // Reset spinner editor style
    }

    private void setErrorStyle(Region field) {
        // Highlight the field with a red border
        field.setStyle("-fx-border-color: red; -fx-border-width: 2;");
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }


}