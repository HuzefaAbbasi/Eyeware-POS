package com.example.pos.CustomerModule;

import javafx.scene.control.Button;

public class Customer {
    private String customerName;
    private String phoneNumber;
    private Double credit;
    private String address;
    private Button viewButton;

    public Customer(String customerName, String phoneNumber, Double credit, String address) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.credit = credit;
        this.address = address;
    }

    public Customer(String customerName, String phoneNumber, Double credit, String address, Button viewButton) {
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.credit = credit;
        this.address = address;
        viewButton.setStyle("-fx-background-color: white;  -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black; ");
        this.viewButton = viewButton;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Double getCredit() {
        return credit;
    }

    public void setCredit(Double credit) {
        this.credit = credit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Button getViewButton() {
        return viewButton;
    }

    public void setViewButton(Button viewButton) {
        this.viewButton = viewButton;
    }
}
