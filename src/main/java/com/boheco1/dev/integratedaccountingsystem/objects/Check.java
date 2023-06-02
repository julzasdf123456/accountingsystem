package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

public class Check {
    private String bank;
    private String checkNo;
    private double amount;
    private double originalAmount;

    public Check() {}

    public Check(String bank, String check, double amount) {
        this.bank = bank;
        this.checkNo = check;
        this.amount = amount;
        this.originalAmount = amount;
    }

    public Check(String bank, double amount) {
        this.bank = bank;
        this.amount = amount;
        this.originalAmount = amount;
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
        return this.bank+", ₱ "+ Utility.formatDecimal(this.amount);
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }
}
