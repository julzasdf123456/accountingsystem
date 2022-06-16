package com.boheco1.dev.integratedaccountingsystem.objects;

public class ItemizedMirsItem {
    private String id;
    private String mirsItemID;
    private String serial;
    private String brand;
    private String remarks;

    public ItemizedMirsItem(String id, String mirsItemID, String serial, String brand, String remarks) {
        this.id = id;
        this.mirsItemID = mirsItemID;
        this.serial = serial;
        this.brand = brand;
        this.remarks = remarks;
    }

    public String getId() {
        return id;
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
}
