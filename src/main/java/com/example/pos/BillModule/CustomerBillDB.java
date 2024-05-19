package com.example.pos.BillModule;

public class CustomerBillDB {
    private String code;
    private String name;
    private Integer quantity;
    private Double discount;
    private Double purchasePrice;
    private Double retailPrice;
    public CustomerBillDB(String code, String name, Integer quantity, Double discount, Double purchasePrice, Double retailPrice) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.discount = discount;
        this.purchasePrice = purchasePrice;
        this.retailPrice = retailPrice;
    }

    public String getCode() {
        return code;
    }

    public Double getDiscount() {
        return discount;
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

    public Double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(Double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public Double getRetailPrice() {
        return retailPrice;
    }
    public void setDiscount(Double discount) {
        this.discount = discount;
    }

    public void setRetailPrice(Double retailPrice) {
        this.retailPrice = retailPrice;
    }
}
