package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.time.LocalDateTime;

public class StockEntryLog {
    private String id;
    private String stockID;
    private double quantity;
    private String source;
    private double Price;
    private String userID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String rrNo;

    private Stock stock;
    private User user;

    public StockEntryLog() {}

    public StockEntryLog(String id, String stockID, double quantity, String source, double price, String userID, LocalDateTime createdAt, LocalDateTime updatedAt, String rrNo) {
        this.id = id;
        this.stockID = stockID;
        this.quantity = quantity;
        this.source = source;
        Price = price;
        this.userID = userID;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.rrNo = rrNo;
    }

    public String getRrNo() {
        return rrNo;
    }

    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
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

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
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

    public User getUser() throws Exception {
        if(user==null) {
            user = UserDAO.get(this.userID);
        }
        return user;
    }

    public Stock getStock() throws Exception {
        if(stock==null) {
            stock = StockDAO.get(stockID);
        }
        return stock;
    }
}
