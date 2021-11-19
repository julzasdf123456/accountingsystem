package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class MIRSItem {
    private int id;
    private int mirsID;
    private int stockID;
    private int quantity;
    private double price;
    private String remarks;
    private String particulars;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MIRSItem(){}

    public MIRSItem(int id, int mirsID, int stockID, int quantity, double price, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.mirsID = mirsID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMirsID() {
        return mirsID;
    }

    public void setMirsID(int mirsID) {
        this.mirsID = mirsID;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
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
