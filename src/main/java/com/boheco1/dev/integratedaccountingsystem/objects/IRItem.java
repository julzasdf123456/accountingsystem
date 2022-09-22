package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.IRDao;

public class IRItem {
    private String localCode;
    private String neaCode;
    private String description;
    private double quantity; //ending/current quantity
    private double price; //ending/current price
    private double beginningPrice;
    private double receivedQty;
    private double receivedPrice;

    public String getReceivedReference(int year, int month) throws Exception {
        if(this.getReceivedQty()>0) {
            return IRDao.getReceivingReference(this.getStockId(), year, month);
        }else {
            return null;
        }
    }

    public String getReturnedReference(int year, int month) {
        if(this.releasedQty>0) {
            return null;
        }else {
            return null;
        }
    }

    private double releasedQty;
    private double releasedPrice;
    private double returnedQty;
    private double returnedPrice;
    private String stockId;

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public double getBeginningQty() {
        return this.quantity+this.releasedQty-(this.returnedQty+receivedQty);
    }

    public double getBeginningPrice() {
        return beginningPrice==0 ? this.price : beginningPrice;
    }

    public double getBeginningAmount() {
        return getBeginningPrice()*getBeginningQty();
    }

    public void setBeginningPrice(double beginningPrice) {
        this.beginningPrice = beginningPrice;
    }

    public String getCode() {
        return localCode==null ? neaCode : localCode;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public String getNeaCode() {
        return neaCode;
    }

    public void setNeaCode(String neaCode) {
        this.neaCode = neaCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getReceivedQty() {
        return receivedQty;
    }

    public void setReceivedQty(double receivedQty) {
        this.receivedQty = receivedQty;
    }

    public double getReceivedPrice() {
        return receivedPrice;
    }

    public void setReceivedPrice(double receivedPrice) {
        this.receivedPrice = receivedPrice;
    }

    public double getReleasedQty() {
        return releasedQty;
    }

    public void setReleasedQty(double releasedQty) {
        this.releasedQty = releasedQty;
    }

    public double getReleasedPrice() {
        return releasedPrice;
    }

    public void setReleasedPrice(double releasedPrice) {
        this.releasedPrice = releasedPrice;
    }

    public double getReturnedQty() {
        return returnedQty;
    }

    public void setReturnedQty(double returnedQty) {
        this.returnedQty = returnedQty;
    }

    public double getReturnedPrice() {
        return returnedPrice;
    }

    public void setReturnedPrice(double returnedPrice) {
        this.returnedPrice = returnedPrice;
    }

}
