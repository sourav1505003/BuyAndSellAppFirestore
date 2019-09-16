package com.example.buyandsellapp.Models;

public class Category {
    String name;
    int count;

    public Category() {
    }

    public Category(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
