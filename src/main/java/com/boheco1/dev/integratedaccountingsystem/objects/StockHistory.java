package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class StockHistory {
    private String id;
    private String stockID;
    private LocalDate date;
    private double price;
    private String updatedBy;

    public StockHistory(String id, String stockID, LocalDate date, double price, String updatedBy) {
        this.id = id;
        this.stockID = stockID;
        this.date = date;
        this.price = price;
        this.updatedBy = updatedBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
