package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class Releasing {
    private String id;
    private String stockID;
    private String mirsID;
    private int quantity;
    private double price;
    private String userID;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Releasing(String id, String stockID, String mirsID, int quantity, double price, String userID, String status) {
        this.id = id;
        this.stockID = stockID;
        this.mirsID = mirsID;
        this.quantity = quantity;
        this.price = price;
        this.userID = userID;
        this.status = status;
    }

    public Releasing() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getMirsID() {
        return mirsID;
    }

    public void setMirsID(String mirsID) {
        this.mirsID = mirsID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
