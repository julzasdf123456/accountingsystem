package com.boheco1.dev.integratedaccountingsystem.objects;

public class StockDescription {
    private String id;
    private String description;
    private int quantity;

    public StockDescription(String id, String description, int quantity) {
        this.id = id;
        this.description = description;
        this.quantity = quantity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
