package com.example.guihotelreservationsystem;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent; // Import Parent class
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloController {
    @FXML
    private Button OldCustomerButton;
    @FXML
    private Button NewCustomerButton;
    public void OpenNewCustomerForm(ActionEvent event){
        try{
            FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("CustomerForm.fxml"));
            Parent root =fxmlLoader.load();
            Stage s1=new Stage();
            //Scene scene = new Scene(fxmlLoader.load(), 570, 874); // Adjust dimensions as needed

            // Set the stage properties
            s1.setTitle("Customer Form");
            s1.setScene(new Scene(root,570,706));

            s1.show();
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error opening customer form", e);
        }
    }

    public void OpenCustomerForm(ActionEvent event) {
        try {
            // Load the FXML file for the new form
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("old-customer.fxml"));
            Parent root = fxmlLoader.load();

            // Create a new Stage for the Customer Form
            Stage stage = new Stage();
            stage.setTitle("Old Customer Form");

            // Set the Scene using the loaded root (FXML layout)
            stage.setScene(new Scene(root, 488, 612));

            stage.show();

            // Close the current window
            Stage currentStage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            currentStage.close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error opening customer form", e);
        }
    }


}