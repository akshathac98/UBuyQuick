package com.ubuyquick.customer.model;

public class CreditNote {

    private String customer_name;
    private String shop_name;
    private boolean cleared;
    private String order_id;
    private double credit;
    private String id;

    public CreditNote(String customer_name, String shop_name, boolean cleared, String order_id, double credit) {
        this.customer_name = customer_name;
        this.shop_name = shop_name;
        this.cleared = cleared;
        this.order_id = order_id;
        this.credit = credit;
    }

    public String getShopName() {

        return shop_name;
    }

    public void setShopName(String shop_name) {
        this.shop_name = shop_name;
    }

    public boolean isCleared() {
        return cleared;
    }

    public void setCleared(boolean cleared) {
        this.cleared = cleared;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getCustomerName() {
        return customer_name;
    }

    public void setCustomerName(String customer_name) {
        this.customer_name = customer_name;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }
}
