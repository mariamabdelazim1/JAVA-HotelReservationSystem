package com.example.guihotelreservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class mainClass extends Application {

    @Override
    public void start(Stage stage) {
        try {
            // Load the FXML file for the form
            FXMLLoader fxmlLoader = new FXMLLoader(mainClass.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 887 ,743);
            stage.setTitle("Hello!");
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }
}