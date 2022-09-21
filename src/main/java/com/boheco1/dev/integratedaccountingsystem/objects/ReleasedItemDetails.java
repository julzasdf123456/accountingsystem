package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;

import java.time.LocalDateTime;

public class ReleasedItemDetails {
    private String itemDescription;
    private String mirsItemId;
    private String mirsID;
    private String stockID;
    private double quantity;
    private double price;
    private String status;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int remaining;
    private int actualReleased;

    public ReleasedItemDetails(String itemDescription, String mirsItemId, String mirsID, String stockID, int quantity, double price, String status, LocalDateTime createdAt, LocalDateTime updatedAt) {
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

    public double getQuantity() {
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

    public double getRemaining() throws Exception {
        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setMirsID(mirsID);
        mirsItem.setStockID(stockID);
        mirsItem.setQuantity(quantity);
        return Math.max(MirsDAO.getBalance(mirsItem), 0);
    }

    public double getActualReleased() throws Exception {
        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setMirsID(mirsID);
        mirsItem.setStockID(stockID);
        mirsItem.setQuantity(quantity);

        return Math.abs(MirsDAO.getBalance(mirsItem)-quantity);

    }
}
