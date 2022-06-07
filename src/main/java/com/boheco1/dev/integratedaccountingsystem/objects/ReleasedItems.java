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
    private int quantity;

    public ReleasedItems(String id, String description, String mctNo, double price, int quantity) {
        this.id = id;
        this.description = description;
        this.mctNo = mctNo;
        this.price = price;
        this.quantity = quantity;
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

    public int getQuantity() {
        return quantity;
    }
}
