package com.boheco1.dev.integratedaccountingsystem.objects;

public class COBItem extends Item{
    private String cItemId;
    private String remarks;
    private String description;
    private double cost;

    private int qty;

    private double qtr1;
    private double qtr2;
    private double qtr3;
    private double qtr4;

    private String cobId;

    public COBItem(){}

    public String getcItemId() {
        return cItemId;
    }

    public void setcItemId(String cItemId) {
        this.cItemId = cItemId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public double getQtr1() {
        return qtr1;
    }

    public void setQtr1(double qtr1) {
        this.qtr1 = qtr1;
    }

    public double getQtr2() {
        return qtr2;
    }

    public void setQtr2(double qtr2) {
        this.qtr2 = qtr2;
    }

    public double getQtr3() {
        return qtr3;
    }

    public void setQtr3(double qtr3) {
        this.qtr3 = qtr3;
    }

    public double getQtr4() {
        return qtr4;
    }

    public void setQtr4(double qtr4) {
        this.qtr4 = qtr4;
    }

    public String getCobId() {
        return cobId;
    }

    public void setCobId(String cobId) {
        this.cobId = cobId;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
