package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;

import java.time.LocalDateTime;

public class MIRSItem {
    private String id;
    private String mirsID;
    private String stockID;
    private int quantity;
    private double price;
    private String remarks;
    private String particulars;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean additional=false;

    public MIRSItem(){}

    public MIRSItem(String id, String mirsID, String stockID, int quantity, double price, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt, boolean additional) {
        this.id = id;
        this.mirsID = mirsID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.additional = additional;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public boolean isAdditional() {
        return additional;
    }

    public void setAdditional(boolean additional) {
        this.additional = additional;
    }

    public String toString(){
        String display = "";
        try {
            display = this.quantity +" : "+ StockDAO.get(stockID).getDescription();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return display;
    }
}
