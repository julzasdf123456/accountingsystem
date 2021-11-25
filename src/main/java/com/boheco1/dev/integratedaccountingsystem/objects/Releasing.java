package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class Releasing {
    private int id;
    private int stockID;
    private int mirsID;
    private int quantity;
    private double price;
    private int userID;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Releasing(int id, int stockID, int mirsID, int quantity, double price, int userID, String status) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStockID() {
        return stockID;
    }

    public void setStockID(int stockID) {
        this.stockID = stockID;
    }

    public int getMirsID() {
        return mirsID;
    }

    public void setMirsID(int mirsID) {
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
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
