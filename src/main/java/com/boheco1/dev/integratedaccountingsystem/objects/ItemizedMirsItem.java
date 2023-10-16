package com.boheco1.dev.integratedaccountingsystem.objects;

public class ItemizedMirsItem {
    private String id;
    private String stockID;
    private String mirsItemID;
    private String serial;
    private String brand;
    private String remarks;

    private String requestedStockId;
    public ItemizedMirsItem(){}

    public ItemizedMirsItem(String id, String stockID, String mirsItemID, String serial, String brand, String remarks) {
        this.id = id;
        this.stockID = stockID;
        this.mirsItemID = mirsItemID;
        this.serial = serial;
        this.brand = brand;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMirsItemID() {
        return mirsItemID;
    }

    public void setMirsItemID(String mirsItemID) {
        this.mirsItemID = mirsItemID;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getRequestedStockId() {
        return requestedStockId;
    }

    public void setRequestedStockId(String requestedStockId) {
        this.requestedStockId = requestedStockId;
    }
}
