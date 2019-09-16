package com.example.buyandsellapp.Models;

import java.time.LocalDateTime;
import java.util.Date;

public class CartItem {

    private Date timestamp;
    private boolean status;

    public CartItem() {
    }

    public CartItem(Date timestamp, boolean status) {
        this.timestamp = timestamp;
        this.status = status;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public boolean isStatus() {
        return status;
    }
}
