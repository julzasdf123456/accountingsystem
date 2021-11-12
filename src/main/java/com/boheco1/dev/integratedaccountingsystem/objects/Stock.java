package com.boheco1.dev.integratedaccountingsystem.objects;


import java.time.LocalDate;
import java.time.LocalDateTime;

public class Stock {
    private int id;
    private String stockName;
    private String description;
    private int serialNumber;
    private String brand;
    private String model;
    private LocalDate manufacturingDate;
    private LocalDate validityDate;
    private int typeID;
    private String unit;
    private int quantity;
    private double price;
    private String neaCode;
    private boolean isTrashed;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime trashedAt;
    private int userIDCreated;
    private int userIDUpdated;
    private int userIDTrashed;

    public Stock(String stockName, int quantity,double price){
        this.stockName = stockName;
        this.quantity = quantity;
        this.price = price;
    }

    public Stock(int id, String stockName, String description, int serialNumber, String brand, String model, LocalDate manufacturingDate, LocalDate validityDate, int typeID, String unit, int quantity, double price, String neaCode, boolean isTrashed, String comments, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime trashedAt, int userIDCreated, int userIDUpdated, int userIDTrashed) {
        this.id = id;
        this.stockName = stockName;
        this.description = description;
        this.serialNumber = serialNumber;
        this.brand = brand;
        this.model = model;
        this.manufacturingDate = manufacturingDate;
        this.validityDate = validityDate;
        this.typeID = typeID;
        this.unit = unit;
        this.quantity = quantity;
        this.price = price;
        this.neaCode = neaCode;
        this.isTrashed = isTrashed;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.trashedAt = trashedAt;
        this.userIDCreated = userIDCreated;
        this.userIDUpdated = userIDUpdated;
        this.userIDTrashed = userIDTrashed;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getStockName() {
        return stockName;
    }

    public void setStockName(String stockName) {
        this.stockName = stockName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public LocalDate getManufacturingDate() {
        return manufacturingDate;
    }

    public void setManufacturingDate(LocalDate manufacturingDate) {
        this.manufacturingDate = manufacturingDate;
    }

    public LocalDate getValidityDate() {
        return validityDate;
    }

    public void setValidityDate(LocalDate validityDate) {
        this.validityDate = validityDate;
    }

    public int getTypeID() {
        return typeID;
    }

    public void setTypeID(int typeID) {
        this.typeID = typeID;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getNeaCode() {
        return neaCode;
    }

    public void setNeaCode(String neaCode) {
        this.neaCode = neaCode;
    }

    public boolean isTrashed() {
        return isTrashed;
    }

    public void setTrashed(boolean trashed) {
        isTrashed = trashed;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public LocalDateTime getTrashedAt() {
        return trashedAt;
    }

    public void setTrashedAt(LocalDateTime trashedAt) {
        this.trashedAt = trashedAt;
    }

    public int getUserIDCreated() {
        return userIDCreated;
    }

    public void setUserIDCreated(int userIDCreated) {
        this.userIDCreated = userIDCreated;
    }

    public int getUserIDUpdated() {
        return userIDUpdated;
    }

    public void setUserIDUpdated(int userIDUpdated) {
        this.userIDUpdated = userIDUpdated;
    }

    public int getUserIDTrashed() {
        return userIDTrashed;
    }

    public void setUserIDTrashed(int userIDTrashed) {
        this.userIDTrashed = userIDTrashed;
    }


}
