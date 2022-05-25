package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class MR {
    private String id;
    private String employeeId;

    private String employeeFirstName;

    private String employeeLastName;

    private String warehousePersonnelId;
    private String extItem;
    private String stockId;
    private int quantity;
    private double price;
    private LocalDate dateOfMR;
    private String status; //active, lost, damaged, returned
    private LocalDate dateOfReturn;

    private String remarks;
    public MR() {
    }
    /**
     * Used for creating MR with items not taken from the warehouse
     */
    public MR(String employeeId, String warehousePersonnelId, String extItem, int quantity, double price, LocalDate dateOfMR) {
        this.employeeId = employeeId;
        this.warehousePersonnelId = warehousePersonnelId;
        this.extItem = extItem;
        this.quantity = quantity;
        this.price = price;
        this.dateOfMR = dateOfMR;
        this.status = "active";
    }

    /**
     * Used for creating MR with items from the warehouse
     */
    public MR(String employeeId, String warehousePersonnelId, String stockId, int quantity, LocalDate dateOfMR) {
        this.employeeId = employeeId;
        this.warehousePersonnelId = warehousePersonnelId;
        this.stockId = stockId;
        this.quantity = quantity;
        this.dateOfMR = dateOfMR;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWarehousePersonnelId() {
        return warehousePersonnelId;
    }

    public void setWarehousePersonnelId(String warehousePersonnelId) {
        this.warehousePersonnelId = warehousePersonnelId;
    }

    public String getExtItem() {
        return extItem;
    }

    public void setExtItem(String extItem) {
        this.extItem = extItem;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
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

    public LocalDate getDateOfMR() {
        return dateOfMR;
    }

    public void setDateOfMR(LocalDate dateOfMR) {
        this.dateOfMR = dateOfMR;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
    }

    public String getRemarks() {
        return remarks;
    }
    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }
}
