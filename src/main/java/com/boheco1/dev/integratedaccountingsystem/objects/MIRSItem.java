package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;

import java.time.LocalDateTime;

public class MIRSItem extends  ItemizedMirsItem{
    private String id;
    private String mirsID;
    private String stockID;
    private double quantity;
    private double price;
    private String remarks;
    private String particulars;
    private String unit;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isAdditional;
    private boolean isSelected;




    public MIRSItem(){}

    public MIRSItem(String id, String mirsID, String stockID, double quantity, double price, String remarks, LocalDateTime createdAt, LocalDateTime updatedAt, boolean isAdditional) {
        this.id = id;
        this.mirsID = mirsID;
        this.stockID = stockID;
        this.quantity = quantity;
        this.price = price;
        this.remarks = remarks;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isAdditional = isAdditional;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
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
        return isAdditional;
    }

    public void setAdditional(boolean additional) {
        isAdditional = additional;
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

    //use for table checkbox on releasing
    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


    //temporary store the actual item stock id when releasing
    private String actualStockId;

    public String getActualStockId() {
        return actualStockId;
    }

    public void setActualStockId(String actualStockId) {
        this.actualStockId = actualStockId;
    }

}
