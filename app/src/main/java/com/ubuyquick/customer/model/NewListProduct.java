package com.ubuyquick.customer.model;

public class NewListProduct {

    private String product_name;
    private String product_measure;
    private int product_quantity;
    private String product_id;
    private double product_mrp;

    public NewListProduct(String product_name, int product_quantity, String product_id, double product_mrp, String product_measure) {
        this.product_name = product_name;
        this.product_id = product_id;
        this.product_measure = product_measure;
        this.product_mrp = product_mrp;
        this.product_quantity = product_quantity;
    }

    public String getProductMeasure() {
        return product_measure;
    }

    public void setProductMeasure(String product_measure) {
        this.product_measure = product_measure;
    }

    public double getProductMrp() {
        return product_mrp;
    }

    public void setProductMrp(double product_mrp) {
        this.product_mrp = product_mrp;
    }

    public String getProductId() {
        return product_id;
    }

    public void setProductId(String product_id) {
        this.product_id = product_id;
    }

    public String getProductName() {
        return product_name;
    }

    public void setProductName(String product_name) {
        this.product_name = product_name;
    }

    public int getProductQuantity() {
        return product_quantity;
    }

    public void setProductQuantity(int product_quantity) {
        this.product_quantity = product_quantity;
    }

}
