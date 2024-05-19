package com.example.pos.VendorModule;

public class VendorBillProducts {
    private String billProductId = "billProductId";
    private String billId = "vBillId";
    private String code = "productCode";
    private String name = "productName";
    private String quantity = "quantity";
    private String purchasePrice = "purchasePrice";

    public VendorBillProducts(String billProductId, String billId, String code, String name, String quantity, String purchasePrice) {
        this.billProductId = billProductId;
        this.billId = billId;
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.purchasePrice = purchasePrice;
    }

    public String getBillProductId() {
        return billProductId;
    }

    public void setBillProductId(String billProductId) {
        this.billProductId = billProductId;
    }

    public String getBillId() {
        return billId;
    }

    public void setBillId(String billId) {
        this.billId = billId;
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

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(String purchasePrice) {
        this.purchasePrice = purchasePrice;
    }
}
