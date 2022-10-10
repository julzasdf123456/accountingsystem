package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDetails {
    private LocalDate period;
    private String transactionNumber;
    private String transactionCode;
    private LocalDateTime transactionDate;
    private int sequenceNumber;
    private String accountCode;
    private double debit;
    private double credit;
    private LocalDate orDate;
//    private LocalDate orDateTo;
    private String bankID;
    private String note;
    private String checkNumber;

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCheckNumber() {
        return checkNumber;
    }

    public void setCheckNumber(String checkNumber) {
        this.checkNumber = checkNumber;
    }

    public LocalDate getPeriod() {
        return period;
    }

    public void setPeriod(LocalDate period) {
        this.period = period;
    }

    public String getTransactionNumber() {
        return transactionNumber;
    }

    public void setTransactionNumber(String transactionNumber) {
        this.transactionNumber = transactionNumber;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public double getDebit() {
        return debit;
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public LocalDate getOrDate() {
        return orDate;
    }

    public void setOrDate(LocalDate orDate) {
        this.orDate = orDate;
    }

//    public LocalDate getOrDateTo() {
//        return orDateTo;
//    }
//
//    public void setOrDateTo(LocalDate orDateTo) {
//        this.orDateTo = orDateTo;
//    }

    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
    }
}
