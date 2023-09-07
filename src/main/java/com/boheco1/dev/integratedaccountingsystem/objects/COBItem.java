package com.boheco1.dev.integratedaccountingsystem.objects;

public class COBItem extends Item{
    public static String[] TYPES = {"Benefits and Allowances", "Representation and Other Allowances", "Salaries and Wages", "Travel Expenses", "Supplies and Materials", "Transportation and Related Expenses", "Others"};
    private String cItemId;
    private String remarks;
    private String description;
    private double cost = 0;
    private int qty = 0;
    private int sequence = 0;
    private int rvQty = 0;
    private int poItemsQty = 0;
    private int level = 1;
    private int noOfTimes = 1;
    private double qtr1 = 0;
    private double qtr2 = 0;
    private double qtr3 = 0;
    private double qtr4 = 0;
    private double amount=0;
    private String cobId;
    private COBItem parent;

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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public double getAmount() {
        return this.qty * this.cost * this.noOfTimes;
    }

    public String getAmountStr() {
        return String.format("₱ %,.2f", getAmount());
    }

    public String getQtr1Str() { return String.format("₱ %,.2f", qtr1); }
    public String getQtr2Str() { return String.format("₱ %,.2f", qtr2); }
    public String getQtr3Str() { return String.format("₱ %,.2f", qtr3); }
    public String getQtr4Str() { return String.format("₱ %,.2f", qtr4); }

    public int getNoOfTimes() {
        return noOfTimes;
    }

    public void setNoOfTimes(int noOfTimes) {
        this.noOfTimes = noOfTimes;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getRvQty() {
        return rvQty;
    }

    public void setRvQty(int rvQty) {
        this.rvQty = rvQty;
    }

    public int getRemaining(){
        return this.qty - this.rvQty;
    }

    public int getPoItemsQty() {
        return poItemsQty;
    }

    public void setPoItemsQty(int poQty) {
        this.poItemsQty = poQty;
    }

    public int getRemainingRV(){
        return this.qty - this.poItemsQty;
    }

    public COBItem getParent() {
        return parent;
    }

    public void setParent(COBItem parent) {
        this.parent = parent;
    }
}
