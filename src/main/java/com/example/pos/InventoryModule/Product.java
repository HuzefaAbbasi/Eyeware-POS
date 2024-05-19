package com.example.pos.InventoryModule;


import javafx.scene.control.Button;

public class Product {

    private String category;
    private String productCode;
    private String productName;
    private Integer stock;
    private Double purchasePrice;
    private Double minimumRetailPrice;
    private Double discount;
    private Button updateButton;
    private Button removeButton;

    public Product(String category, String productCode, String productName, Integer stock, Double minimumRetailPrice, Double discount) {
        this.category = category;
        this.productCode = productCode;
        this.productName = productName;
        this.stock = stock;
        this.minimumRetailPrice = minimumRetailPrice;
        this.discount = discount;
    }

    public Product(String category, String productCode, String productName, Integer stock, Double purchasePrice, Double minimumRetailPrice,Double discount, Button updateButton, Button removeButton) {
        this.category = category;
        this.productCode = productCode;
        this.productName = productName;
        this.stock = stock;
        this.purchasePrice = purchasePrice;
        this.minimumRetailPrice = minimumRetailPrice;
        this.discount = discount;
        updateButton.setStyle("-fx-background-color: white;  -fx-background-radius: 10;-fx-border-radius: 10; -fx-border-color: black; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "    -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 16; -fx-text-fill: black; ");
        this.updateButton=updateButton;
        removeButton.setStyle("-fx-background-color: white;  -fx-background-radius: 15;-fx-border-radius: 15; -fx-pref-width: 25; -fx-pref-height: 20; " +
                "   -fx-padding: 5; -fx-font-family: Verdana; -fx-font-size: 22; -fx-text-fill: black;");
        this.removeButton=removeButton;

    }


    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getMinimumRetailPrice() {
        return minimumRetailPrice;
    }

    public void setMinimumRetailPrice(Double minimumRetailPrice) {
        this.minimumRetailPrice = minimumRetailPrice;
    }

    public Double getDiscount() {
        return discount;
    }

    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public Button getUpdateButton() {
        return updateButton;
    }

    public void setUpdateButton(Button updateButton) {
        this.updateButton = updateButton;
    }

    public Button getRemoveButton() {
        return removeButton;
    }

    public void setRemoveButton(Button removeButton) {
        this.removeButton = removeButton;
    }
}
