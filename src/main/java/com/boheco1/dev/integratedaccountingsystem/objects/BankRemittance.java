package com.boheco1.dev.integratedaccountingsystem.objects;


import java.time.LocalDate;

public class BankRemittance {
    private LocalDate orDateFrom;
    private LocalDate orDateTo;
    private String description;
    private String accountNumber;
    private String checkNumber;
    private double amount;

    public BankRemittance(LocalDate orDateFrom, LocalDate orDateTo, String description, String accountNumber, String checkNumber, double amount) {
        this.orDateFrom = orDateFrom;
        this.orDateTo = orDateTo;
        this.description = description;
        this.accountNumber = accountNumber;
        this.checkNumber = checkNumber;
        this.amount = amount;
    }

    public BankRemittance() {
        this.orDateFrom = null;
        this.orDateTo = null;
        this.description = "";
        this.accountNumber = "";
        this.checkNumber = "";
        this.amount = 0.0;
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

    public void setOrDateTo(LocalDate orDateTo) {
        this.orDateTo = orDateTo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
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
}
