package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDetails {
    private LocalDateTime period;
    private String transactionNumber;
    private String transactionCode;
    private LocalDateTime transactionDate;
    private int sequenceNumber;
    private String accountCode;
    private double debit;
    private double credit;
    private LocalDate orDateFrom;
    private LocalDate orDateTo;
    private String bankID;

    public TransactionDetails(LocalDateTime period, String transactionNumber, String transactionCode, LocalDateTime transactionDate, int sequenceNumber, String accountCode, double debit, double credit, LocalDate orDateFrom, LocalDate orDateTo, String bankID) {
        this.period = period;
        this.transactionNumber = transactionNumber;
        this.transactionCode = transactionCode;
        this.transactionDate = transactionDate;
        this.sequenceNumber = sequenceNumber;
        this.accountCode = accountCode;
        this.debit = debit;
        this.credit = credit;
        this.orDateFrom = orDateFrom;
        this.orDateTo = orDateTo;
        this.bankID = bankID;
    }

    public LocalDateTime getPeriod() {
        return period;
    }

    public void setPeriod(LocalDateTime period) {
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

    public String getBankID() {
        return bankID;
    }

    public void setBankID(String bankID) {
        this.bankID = bankID;
    }
}
