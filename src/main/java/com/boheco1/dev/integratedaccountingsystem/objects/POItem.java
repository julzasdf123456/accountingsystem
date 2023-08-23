package com.boheco1.dev.integratedaccountingsystem.objects;

public class POItem extends RVItem{
    private String details;
    private double POPrice;
    private int POQty;
    private String PONo;
    private int POItemId;
    private int no;
    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getPOPrice() {
        return POPrice;
    }

    public void setPOPrice(double POPrice) {
        this.POPrice = POPrice;
    }

    public int getPOQty() {
        return POQty;
    }

    public void setPOQty(int POQty) {
        this.POQty = POQty;
    }

    public String getPONo() {
        return PONo;
    }

    public void setPONo(String PONo) {
        this.PONo = PONo;
    }

    public int getPOItemId() {
        return POItemId;
    }

    public void setPOItemId(int POItemId) {
        this.POItemId = POItemId;
    }

    @Override
    public double getAmount(){
        return this.getPOPrice() * this.getPOQty();
    }

    public int getNo() {
        return no;
    }

    public void setNo(int no) {
        this.no = no;
    }
}
