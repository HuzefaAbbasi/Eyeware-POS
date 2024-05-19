package com.example.pos.VendorModule;

public class Vendor {

    private String phoneNumber;
    private String vendorName;
    private Double credit;
    private String address;

    public Vendor(String phoneNumber, String vendorName, double credit, String address) {
        this.phoneNumber = phoneNumber;
        this.vendorName = vendorName;
        this.credit = credit;
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
