package com.example.buyandsellapp.Models;

public class ListProduct {
    String productID;
    private int qty;

    public ListProduct() {
    }

    public ListProduct(String productID, int qty) {
        this.productID = productID;
        this.qty = qty;
    }

    public int getQty() {
        return qty;
    }

    public String getProductID() {
        return productID;
    }
}
