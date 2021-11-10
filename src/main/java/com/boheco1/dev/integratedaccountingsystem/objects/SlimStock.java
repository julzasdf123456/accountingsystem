package com.boheco1.dev.integratedaccountingsystem.objects;

public class SlimStock {
    private int id;
    private String StockName;
    private String Model;
    private String Brand;

    public SlimStock(int id, String stockName, String model, String brand) {
        this.id = id;
        StockName = stockName;
        Model = model;
        Brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockName() {
        return StockName;
    }

    public void setStockName(String stockName) {
        StockName = stockName;
    }

    public String getModel() {
        return Model;
    }

    public void setModel(String model) {
        Model = model;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }
}
