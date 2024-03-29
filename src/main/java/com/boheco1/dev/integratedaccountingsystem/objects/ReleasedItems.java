package com.boheco1.dev.integratedaccountingsystem.objects;

/**
 * Custom object for searching of released items in
 * MRT component
 */
public class ReleasedItems {
    private String id; //Releasing ID
    private String description;
    private String mctNo;
    private double price;
    private double quantity;
    private String stockID;
    private String code;
    private double balance;
    private String mirsNo;

    public ReleasedItems(String id, String description, String mctNo, double price, double quantity) {
        this.id = id;
        this.description = description;
        this.mctNo = mctNo;
        this.price = price;
        this.quantity = quantity;
    }

    public ReleasedItems(String id, String description, String mctNo, double price, double quantity, int balance) {
        this.id = id;
        this.description = description;
        this.mctNo = mctNo;
        this.price = price;
        this.quantity = quantity;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getMctNo() {
        return mctNo;
    }

    public double getPrice() {
        return price;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMirsNo() {
        return mirsNo;
    }

    public void setMirsNo(String mirsNo) {
        this.mirsNo = mirsNo;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}
