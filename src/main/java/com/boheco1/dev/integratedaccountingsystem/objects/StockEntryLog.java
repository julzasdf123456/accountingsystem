package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class StockEntryLog {
    private int id;
    private int stockID;
    private int quantity;
    private String source;
    private double Price;
    private int userID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StockEntryLog(int id, int stockID, int quantity, String source, double price, int userID, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.stockID = stockID;
        this.quantity = quantity;
        this.source = source;
        Price = price;
        this.userID = userID;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
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
