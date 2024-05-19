package com.example.pos.BillModule;

import com.example.pos.UtilityClasses.Function;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Bill {
    private Integer billId;
    private String customerName;
    private String phoneNumber;
    private String billDate;
    private String deliveryDate;
    private Double total;
    private Double advance;
    private Double finalAmount;
    private Double balance;
    private Button viewButton;

    public Bill(Integer billId, String customerName, String phoneNumber, String billDate, String deliveryDate, Double total, Double advance, Double balance, Button viewButton) {
        this.billId = billId;
        this.customerName = customerName;
        this.phoneNumber = phoneNumber;
        this.billDate = billDate;
        this.deliveryDate = deliveryDate;
        this.total = total;
        this.advance = advance;
        this.balance = balance;
        viewButton.setStyle("-fx-background-color: white;  -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black; ");
        this.viewButton = viewButton;
    }

    public Bill(Integer billId, String billDate, String deliveryDate, Double total, Double advance, Double finalAmount, Double balance, Button viewButton) {
        this.billId = billId;
        this.billDate = billDate;
        this.deliveryDate = deliveryDate;
        this.total = total;
        this.advance = advance;
        this.finalAmount = finalAmount;
        this.balance = balance;
//        Image viewImage = new Image("D:\\Projects\\POS Software\\POS_V2.1 - Increment 3.1\\src\\main\\resources\\com\\example\\pos\\Images\\ViewBlack.png");
//        Image viewImage = new Image(Function.viewButtonImagePath);
//        ImageView imageView = new ImageView(viewImage);
//        imageView.setFitWidth(25);
//        imageView.setFitHeight(20);
//        viewButton.setGraphic ( imageView );
//        viewButton.setStyle("-fx-background-color: transparent; ");
        viewButton.setStyle("-fx-background-color: white;  -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black; ");
        this.viewButton = viewButton;
    }

    public Bill(Integer billId, String billDate, String deliveryDate, Double total, Double advance, Double balance) {
        this.billId = billId;
        this.billDate = billDate;
        this.deliveryDate = deliveryDate;
        this.total = total;
        this.advance = advance;
        this.balance = balance;
    }


    public Integer getBillId() {
        return billId;
    }

    public void setBillId(Integer billId) {
        this.billId = billId;
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

    public String getBillDate() {
        return billDate;
    }

    public void setBillDate(String billDate) {
        this.billDate = billDate;
    }

    public String getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(String deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getAdvance() {
        return advance;
    }

    public void setAdvance(Double advance) {
        this.advance = advance;
    }

    public Double getFinalAmount() {
        return finalAmount;
    }

    public void setFinalAmount(Double finalAmount) {
        this.finalAmount = finalAmount;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Button getViewButton() {return viewButton;}

    public void setViewButton(Button viewButton) {this.viewButton = viewButton;}

}
