package com.ubuyquick.customer.model;

public class Shop {

    private boolean quick_delivery;
    private String pincode;
    private String shop_id;
    private String shop_name;
    private String shop_timings;
    private String shop_image_url;
    private String shop_address;
    private String shop_vendor;
    private String shop_spec;
    private double minimum_order;

    public Shop(boolean quick_delivery, String pincode, String shop_id, String shop_name, String shop_timings, String shop_image_url, String shop_address, String shop_vendor, String shop_spec, double minimum_order) {
        this.quick_delivery = quick_delivery;
        this.pincode = pincode;
        this.shop_id = shop_id;
        this.minimum_order = minimum_order;
        this.shop_name = shop_name;
        this.shop_spec = shop_spec;
        this.shop_timings = shop_timings;
        this.shop_image_url = shop_image_url;
        this.shop_address = shop_address;
        this.shop_vendor = shop_vendor;
    }

    public double getMinimumOrder() {
        return minimum_order;
    }

    public void setMinimumOrder(double minimum_order) {
        this.minimum_order = minimum_order;
    }

    public String getShopSpec() {
        return shop_spec;
    }

    public void setShopSpec(String shop_spec) {
        this.shop_spec = shop_spec;
    }

    public String getShopVendor() {
        return shop_vendor;
    }

    public void setShopVendor(String shop_vendor) {
        this.shop_vendor = shop_vendor;
    }

    public String getShopAddress() {
        return shop_address;
    }

    public void setShopAddress(String shop_address) {
        this.shop_address = shop_address;
    }

    public boolean getQuickDelivery() {
        return quick_delivery;
    }

    public void setQuickDelivery(boolean quick_delivery) {
        this.quick_delivery = quick_delivery;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getShopId() {
        return shop_id;
    }

    public void setShopId(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getShopName() {
        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public String getShopTimings() {
        return shop_timings;
    }

    public void setShopTimings(String shop_timings) {
        this.shop_timings = shop_timings;
    }

    public String getShopImageUrl() {
        return shop_image_url;
    }

    public void setShopImageUrl(String shop_image_url) {
        this.shop_image_url = shop_image_url;
    }
}
