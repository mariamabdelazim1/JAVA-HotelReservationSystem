module com.example.guihotelreservationsystem {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;

    opens com.example.guihotelreservationsystem to javafx.fxml;
    exports com.example.guihotelreservationsystem;
}