package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class ReleasedItemDetails {
    private String itemDescription;
    private String mirsItemId;
    private String mirsID;
    private String stockID;
    private int quantity;
    private double price;
    private String status;
    private String particulars;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String workOrderNo;

    public ReleasedItemDetails(String itemDescription, String mirsItemId, String mirsID, String stockID, int quantity, double price, String status, LocalDateTime createdAt, LocalDateTime updatedAt, String workOrderNo) {
        this.itemDescription = itemDescription;
        this.mirsItemId = mirsItemId;
        this.mirsID = mirsID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.status = status;
        this.unit = unit;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.workOrderNo = workOrderNo;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public String getMirsItemId() {
        return mirsItemId;
    }

    public void setMirsItemId(String mirsItemId) {
        this.mirsItemId = mirsItemId;
    }

    public String getMirsID() {
        return mirsID;
    }

    public void setMirsID(String mirsID) {
        this.mirsID = mirsID;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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

    public String getWorkOrderNo() {
        return workOrderNo;
    }

    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNo = workOrderNo;
    }
}
