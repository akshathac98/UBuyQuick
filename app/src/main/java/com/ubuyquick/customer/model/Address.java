package com.ubuyquick.customer.model;

public class Address {

    private String address_id;
    private String name;
    private String contact;
    private String building;
    private String address1;
    private String address2;
    private String pincode;

    public Address(String address_id, String name, String contact, String building, String address1, String address2, String pincode) {
        this.address_id = address_id;
        this.name = name;
        this.contact = contact;
        this.building = building;
        this.address1 = address1;
        this.address2 = address2;
        this.pincode = pincode;
    }

    public String getAddressId() {
        return address_id;
    }

    public void setAddressId(String address_id) {
        this.address_id = address_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
}
