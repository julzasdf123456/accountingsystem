package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class BalanceForward {
    private LocalDateTime period;
    private String accountCode;
    private double balanceBeginning;
    private double debits;
    private double credits;
    private double endingBalance;

    public BalanceForward(LocalDateTime period, String accountCode, double balanceBeginning, double debits, double credits, double endingBalance) {
        this.period = period;
        this.accountCode = accountCode;
        this.balanceBeginning = balanceBeginning;
        this.debits = debits;
        this.credits = credits;
        this.endingBalance = endingBalance;
    }

    public LocalDateTime getPeriod() {
        return period;
    }

    public void setPeriod(LocalDateTime period) {
        this.period = period;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public double getBalanceBeginning() {
        return balanceBeginning;
    }

    public void setBalanceBeginning(double balanceBeginning) {
        this.balanceBeginning = balanceBeginning;
    }

    public double getDebits() {
        return debits;
    }

    public void setDebits(double debits) {
        this.debits = debits;
    }

    public double getCredits() {
        return credits;
    }

    public void setCredits(double credits) {
        this.credits = credits;
    }

    public double getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(double endingBalance) {
        this.endingBalance = endingBalance;
    }
}
