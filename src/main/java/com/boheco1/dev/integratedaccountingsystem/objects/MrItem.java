package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;

import java.time.LocalDate;

public class MrItem {
    private String id;
    private String stockID;
    private String itemName;
    private String description;
    private double qty;
    private String remarks;
    private Stock stock;
    private String mrNo;
    private String rrNo;
    private String status;
    private double price;
    private String propertyNo;

    private LocalDate dateOfReturned;

    private String Status;

    private String updatedBy;

    public MrItem() { }

    public MrItem(String id, String mrNo, String stockID, int qty, String remarks) {
        this.id = id;
        this.mrNo = mrNo;
        this.stockID = stockID;
        this.qty = qty;
        this.remarks = remarks;
    }

    public MrItem(String id, String mrNo, String stockID, int qty, String remarks, String status) {
        this.id = id;
        this.mrNo = mrNo;
        this.stockID = stockID;
        this.qty = qty;
        this.remarks = remarks;
        this.status = status;
    }

    public MrItem(String id, String mrNo, String itemName, String description, int qty, String remarks, String status) {
        this.id = id;
        this.mrNo = mrNo;
        this.itemName = itemName;
        this.description = description;
        this.qty = qty;
        this.remarks = remarks;
        this.status = status;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateOfReturned() {
        return dateOfReturned;
    }

    public void setDateOfReturned(LocalDate dateOfReturned) {
        this.dateOfReturned = dateOfReturned;
    }

    public String getMrNo() {
        return mrNo;
    }

    public void setMrNo(String mrNo) {
        this.mrNo = mrNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStockID() {
        return stockID;
    }

    public void setStockID(String stockID) {
        this.stockID = stockID;
    }

    public double getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public Stock getStock() throws Exception {
        if(stock==null) {
            stock = StockDAO.get(this.stockID);
        }
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getRrNo() {
        return rrNo;
    }
    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
    }

    public LocalDate getDateReturned() {
        return dateOfReturned;
    }

    public void setDateReturned(LocalDate dateReturned) {
        this.dateOfReturned = dateReturned;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getPropertyNo() {
        return propertyNo;
    }

    public void setPropertyNo(String property_no) {
        this.propertyNo = property_no;
    }
}
