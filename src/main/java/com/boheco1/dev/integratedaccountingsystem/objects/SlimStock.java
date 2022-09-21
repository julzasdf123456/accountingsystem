package com.boheco1.dev.integratedaccountingsystem.objects;

import java.util.Objects;

public class SlimStock {

    private String id;
    private String StockName;
    private String Model;
    private String Brand;
    private String Description;
    private double Quantity;
    private String Unit;
    private double Price;
    private String RRNo;
    private String Code;
    private String localDescription;

    public SlimStock(){}

    public SlimStock(String id, String stockName, String model, String brand) {
        this.id = id;
        StockName = stockName;
        Model = model;
        Brand = brand;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        this.Description = description;
    }

    public double getQuantity() {
        return Quantity;
    }

    public void setQuantity(double quantity) {
        this.Quantity = quantity;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        this.Unit = unit;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        this.Price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SlimStock slimStock = (SlimStock) o;
        return id == slimStock.id && StockName.equals(slimStock.StockName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, StockName);
    }

    public String getRRNo() {
        return RRNo;
    }

    public void setRRNo(String RRNo) {
        this.RRNo = RRNo;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    @Override
    public String toString() {
        return Brand;
    }

    public String getLocalDescription() {
        return localDescription;
    }

    public void setLocalDescription(String localDescription) {
        this.localDescription = localDescription;
    }
}
