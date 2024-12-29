package com.example.guihotelreservationsystem;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class invoiceController {
    private Reservation reservation;
    private Customer customer;
    private double price;
    private invoice invoice;
    private int numberRooms;

    @FXML
    private Label guestNameLabel;

    @FXML
    private Label reservationIdLabel;

    @FXML
    private Label numberOfRoomsLabel;

    @FXML
    private Label numberOfNightsLabel;

    @FXML
    private Label roomRateLabel;

    @FXML
    private Label taxesLabel;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Label invoiceIdLabel; // Added to display the invoice ID
    @FXML
    private Label Check_inLabel;
    @FXML
    private Label guestFnameLabel;

    /**
     * Sets the data for the invoice and updates the UI.
     *
     * @param customer    The customer associated with the reservation.
     * @param reservation The reservation details.
     * @param price       The base price of the reservation.
     * @param numberRooms The number of rooms booked.
     */
    public void setData(Customer customer, Reservation reservation, double price, int numberRooms) {
        this.customer = customer;
        this.reservation = reservation;
        this.price = price;
        this.numberRooms = numberRooms;
        invoice = new invoice(reservation);

        // Populate the UI label
       // invoiceIdLabel.setText(String.valueOf(invoice.getInvoiceId()));
        guestNameLabel.setText(customer.getFName() + " " + customer.getLName());
        reservationIdLabel.setText(String.valueOf(reservation.getReservationId()));
        numberOfRoomsLabel.setText(String.valueOf(numberRooms));
        numberOfNightsLabel.setText(String.valueOf(reservation.getCountDays()));
        roomRateLabel.setText("$" + price);
        Check_inLabel.setText(String.valueOf(reservation.getCheckInDate()));
        guestFnameLabel.setText(customer.getFName());

        // Initialize the invoice object
       // invoice = new invoice(reservation);

        // Calculate taxes and total price
        double taxes = invoice.calculateTaxes(price);
        double totalPrice = invoice.calculateFinalPrice(price);

        taxesLabel.setText("$" + taxes);
        totalPriceLabel.setText("$" + totalPrice);

        // Save the invoice to the database and update the invoice ID
        saveInvoiceToDatabase();
    }

    /**
     * Saves the invoice to the database and updates the UI with the generated invoice ID.
     */
    private void saveInvoiceToDatabase() {
        invoice.saveInvoiceToDatabase(
                () -> {
                    // Success callback: Update the UI with the generated invoice ID
                    invoiceIdLabel.setText(String.valueOf(invoice.getInvoiceId()));
                    System.out.println("Invoice saved successfully. ID: " + invoice.getInvoiceId());
                },
                () -> {
                    // Failure callback: Display an error message in the console
                    System.err.println("Failed to save the invoice to the database.");
                },
                price
        );
    }
}
