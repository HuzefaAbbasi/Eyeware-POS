package com.example.pos.BillModule;

import javafx.scene.control.Button;

public class CustomerBillTable {
    private String code;
    private String name;
    private Integer quantity;
    private Integer returnedQuantity;
    private Double retailPrice;
    private Double purchasePrice;
    private Double discountPercentage;
    private boolean isOBProduct;
    private Double discount;
    private Double totalPrice;
    private Button editButton;
    private Button removeButton;


    public CustomerBillTable(String code, String name, Integer quantity, Double retailPrice, Double purchasePrice, boolean isOBProduct, Double totalPrice) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.purchasePrice = purchasePrice;
        this.isOBProduct = isOBProduct;
        this.totalPrice = totalPrice;
    }

    public CustomerBillTable(String name, Integer quantity, Double retailPrice, Double purchasePrice, Double discountPercentage, boolean isOBProduct, Double totalPrice) {
        this.name = name;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.purchasePrice = purchasePrice;
        this.discountPercentage = discountPercentage;
        this.isOBProduct = isOBProduct;
        this.totalPrice = totalPrice;
    }

    public CustomerBillTable(String code, String name, Integer quantity, Double retailPrice, Double purchasePrice, Double discountPercentage, Double discount, boolean isOBProduct, Double totalPrice, Button editButton, Button removeButton) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.purchasePrice = purchasePrice;
        this.discountPercentage = discountPercentage;
        this.discount = discount;
        this.isOBProduct = isOBProduct;
        this.totalPrice = totalPrice;
//        Image editImage = new Image(Function.editButtonImagePath);
//        ImageView imageView = new ImageView(editImage);
//        imageView.setFitWidth(25);
//        imageView.setFitHeight(20);
//        editButton.setGraphic ( imageView );
//        editButton.setStyle("-fx-background-color: transparent; ");
        editButton.setStyle("-fx-background-color: white; -fx-background-radius: 10;-fx-border-radius: 10; -fx-border-color: black; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 16; -fx-text-fill: black; ");
        this.editButton = editButton;
//        Image removeImage = new Image("D:\\Projects\\POS Software\\POS_V2.1 - Increment 3.1\\src\\main\\resources\\com\\example\\pos\\Images\\TrashBlack.png");
//        Image removeImage = new Image(Function.deleteButtonImagePath);
//        ImageView imageView1 = new ImageView(removeImage);
//        imageView1.setFitWidth(25);
//        imageView1.setFitHeight(20);
//        removeButton.setGraphic ( imageView1 );
//        removeButton.setStyle("-fx-background-color: transparent; ");
        removeButton.setStyle(" -fx-background-color: white; -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "   -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black;");
        this.removeButton = removeButton;
    }


    public CustomerBillTable(String code, String name, Integer quantity, Double retailPrice, Double purchasePrice, Double discountPercentage, Double discount, boolean isOBProduct, Double totalPrice) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.retailPrice = retailPrice;
        this.purchasePrice = purchasePrice;
        this.discountPercentage = discountPercentage;
        this.discount = discount;
        this.isOBProduct = isOBProduct;
        this.totalPrice = totalPrice;
    }

    public CustomerBillTable(String code, String name, Integer quantity, Integer returnedQuantity , Double retailPrice, Double purchasePrice, Double discountPercentage, Double discount, boolean isOBProduct, Double totalPrice) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.returnedQuantity = returnedQuantity;
        this.retailPrice = retailPrice;
        this.purchasePrice = purchasePrice;
        this.discountPercentage = discountPercentage;
        this.discount = discount;
        this.isOBProduct = isOBProduct;
        this.totalPrice = totalPrice;
    }

    public Double getDiscountPercentage() {
        return discountPercentage;
    }

    public void setDiscountPercentage(Double discountPercentage) {
        this.discountPercentage = discountPercentage;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Integer getReturnedQuantity() {
        return returnedQuantity;
    }

    public void setReturnedQuantity(Integer returnedQuantity) {
        this.returnedQuantity = returnedQuantity;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Button getEditButton() {
        return editButton;
    }

    public void setEditButton(Button editButton) {
        this.editButton = editButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(Button removeButton) {
        this.removeButton = removeButton;
    }

    public boolean isOBProduct() {
        return isOBProduct;
    }

    public void setOBProduct(boolean OBProduct) {
        isOBProduct = OBProduct;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
