package com.boheco1.dev.integratedaccountingsystem.objects;

public class ReceivingItem {
    private String rrNo;
    private String stockId;
    private double qtyDelivered;
    private double qtyAccepted;
    private double unitCost;

    public ReceivingItem(String rrNo, String stockId, double qtyDelivered, double qtyAccepted, double unitCost) {
        this.rrNo = rrNo;
        this.stockId = stockId;
        this.qtyDelivered = qtyDelivered;
        this.qtyAccepted = qtyAccepted;
        this.unitCost = unitCost;
    }

    public ReceivingItem() {}

    public String getRrNo() {
        return rrNo;
    }

    public double getUnitCost() {
        return unitCost;
    }

    public void setUnitCost(double unitCost) {
        this.unitCost = unitCost;
    }

    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
    }

    public String getStockId() {
        return stockId;
    }

    public void setStockId(String stockId) {
        this.stockId = stockId;
    }

    public double getQtyDelivered() {
        return qtyDelivered;
    }

    public void setQtyDelivered(int qtyDelivered) {
        this.qtyDelivered = qtyDelivered;
    }

    public double getQtyAccepted() {
        return qtyAccepted;
    }

    public void setQtyAccepted(int qtyAccepted) {
        this.qtyAccepted = qtyAccepted;
    }

    public double getReturned() {
        return qtyDelivered-qtyAccepted;
    }
}
