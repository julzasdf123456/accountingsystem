package com.boheco1.dev.integratedaccountingsystem.objects;

public class MRTItem {
    private String id;
    private String Code;
    private String AcctCode;
    private String Description;
    private String Unit;
    private Double price;
    private String releasingID;
    private String mrtID;
    private double quantity;
    private String stockID;

    private double amount;

    public MRTItem() {}

    public MRTItem(String id, String releasingID, String mrtID, double quantity) {
        this.id = id;
        this.releasingID = releasingID;
        this.mrtID = mrtID;
        this.quantity = quantity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReleasingID() {
        return releasingID;
    }

    public void setReleasingID(String releasingID) {
        this.releasingID = releasingID;
    }

    public String getMrtID() {
        return mrtID;
    }

    public void setMrtID(String mrtID) {
        this.mrtID = mrtID;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getAcctCode() {
        return AcctCode;
    }

    public void setAcctCode(String acctCode) {
        AcctCode = acctCode;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
