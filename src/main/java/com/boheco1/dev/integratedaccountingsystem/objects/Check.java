package com.boheco1.dev.integratedaccountingsystem.objects;

public class Check {
    private String bank;
    private String checkNo;
    private double amount;

    public Check() {}

    public Check(String bank, String check, double amount) {
        this.bank = bank;
        this.checkNo = check;
        this.amount = amount;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCheckNo() {
        return checkNo;
    }

    public void setCheckNo(String checkno) {
        this.checkNo = checkno;
    }

    public String toString(){
        return this.bank+", check no: "+this.checkNo+", ₱ "+this.amount;
    }
}
