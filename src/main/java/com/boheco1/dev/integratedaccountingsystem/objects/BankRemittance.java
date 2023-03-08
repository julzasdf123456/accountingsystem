package com.boheco1.dev.integratedaccountingsystem.objects;


import com.jfoenix.controls.JFXButton;

import java.time.LocalDate;

public class BankRemittance {
    private LocalDate period;
    private LocalDate orDateFrom;
    private LocalDate orDateTo;
    private String checkNumber;
    private double amount;
    private BankAccount bankAccount;
    private LocalDate depositedDate;

    private String accountCode;

    private TransactionDetails transactionDetails;


    public BankRemittance(LocalDate period, LocalDate orDateFrom, LocalDate orDateTo, String checkNumber, double amount, BankAccount bankAccount, String accountCode) {
        this.period = period;
        this.orDateFrom = orDateFrom;
        this.orDateTo = orDateTo;
        this.checkNumber = checkNumber;
        this.amount = amount;
        this.bankAccount = bankAccount;
        this.accountCode = accountCode;
    }

    public BankRemittance() {
        this.period = null;
        this.orDateFrom = null;
        this.orDateTo = null;
        this.bankAccount = null;
        this.checkNumber = "";
        this.amount = 0.0;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public LocalDate getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(LocalDate depositedDate) {
        this.depositedDate = depositedDate;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public LocalDate getOrDateFrom() {
        return orDateFrom;
    }

    public void setOrDateFrom(LocalDate orDateFrom) {
        this.orDateFrom = orDateFrom;
    }

    public LocalDate getOrDateTo() {
        return orDateTo;
    }

    public TransactionDetails getTransactionDetails() {
        return transactionDetails;
    }

    public void setTransactionDetails(TransactionDetails transactionDetails) {
        this.transactionDetails = transactionDetails;
    }

    public void setOrDateTo(LocalDate orDateTo) {
        this.orDateTo = orDateTo;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getFormattedAmount() {
        return String.format("%,.2f", amount);
    }

    public BankAccount getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(BankAccount bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getDescription() {
        return bankAccount.getBankDescription();
    }

    public String getAccountNumber() {
        return bankAccount.getBankAccountNumber();
    }
}
