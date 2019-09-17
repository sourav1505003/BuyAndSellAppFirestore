package com.example.buyandsellapp.Models;

import android.net.Uri;

public class Product {
    private double price;
    private String productName;
    private String condition;
    private String uid;
    private  String category;
    private String imageuri;
    private int qty;

    public String getImageuri() {
        return imageuri;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getCondition() {
        return condition;
    }

    public String getUid() {
        return uid;
    }

    public String getProductName() {
        return productName;
    }

    public int getQty() {
        return qty;
    }

    public Product() {
    }

    public Product( String productName,double price, String condition, String uid, String category,String uri,int qty) {
        this.price = price;
        this.productName = productName;
        this.condition = condition;
        this.uid = uid;
        this.category = category;
        this.imageuri=uri;
        this.qty=qty;
    }
}
