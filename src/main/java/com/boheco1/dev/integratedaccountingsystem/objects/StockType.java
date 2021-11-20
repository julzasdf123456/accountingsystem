package com.boheco1.dev.integratedaccountingsystem.objects;

public class StockType {
    private int id;
    private String stockType;
    private String unit;

    public StockType() {}

    public StockType(int id, String stockType, String unit) {
        this.id = id;
        this.stockType = stockType;
        this.unit = unit;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
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
