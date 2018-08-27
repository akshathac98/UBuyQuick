package com.ubuyquick.customer.model;

public class ListProduct {

    private String product_name;
    private String product_measure;
    private double product_mrp;
    private int product_quantity;
    private String product_id;
    private boolean selected = false;

    public ListProduct(String product_name, double product_mrp, int product_quantity, String product_id, String product_measure) {
        this.product_name = product_name;
        this.product_mrp = product_mrp;
        this.product_measure = product_measure;
        this.product_quantity = product_quantity;
        this.product_id = product_id;
        this.selected = false;
    }

    public String getProductMeasure() {
        return product_measure;
    }

    public void setProductMeasure(String product_measure) {
        this.product_measure = product_measure;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public double getProductMrp() {
        return product_mrp;
    }

    public void setProductMrp(double product_mrp) {
        this.product_mrp = product_mrp;
    }

    public int getProductQuantity() {
        return product_quantity;
    }

    public void setProductQuantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String product_id) {
        this.product_id = product_id;
    }
}
