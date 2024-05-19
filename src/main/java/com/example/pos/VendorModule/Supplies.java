package com.example.pos.VendorModule;

public class Supplies {
    private Integer vendorId;
    private String productCode;

    public Supplies(Integer vendorId, String productCode) {
        this.vendorId = vendorId;
        this.productCode = productCode;
    }

    public Integer getVendorId() {
        return vendorId;
    }

    public void setVendorId(Integer vendorId) {
        this.vendorId = vendorId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
}
