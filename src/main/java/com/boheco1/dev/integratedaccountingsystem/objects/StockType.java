package com.boheco1.dev.integratedaccountingsystem.objects;

public class StockType {
    private int id;
    private String stockType;

    public StockType(int id, String stockType) {
        this.id = id;
        this.stockType = stockType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockType() {
        return stockType;
    }

    public void setStockType(String stockType) {
        this.stockType = stockType;
    }
}
