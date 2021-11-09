package com.boheco1.dev.integratedaccountingsystem.objects;

public class ParticularsAccount {
    private String particulars;
    private String accountCode;
    private double amount;

    public ParticularsAccount(String particulars, String accountCode, double amount) {
        this.particulars = particulars;
        this.accountCode = accountCode;
        this.amount = amount;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
