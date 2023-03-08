package com.boheco1.dev.integratedaccountingsystem.objects;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Properties;

public class TransactionHeader {
    private LocalDate period;
    private String transactionNumber;
    private String transactionCode; //OR, AR, CV, BR
    private String accountID;
    private String source;
    private String particulars;
    private LocalDate transactionDate; //date of payment, this should be based on database date not from the computer where   it was  transacted
    private String bank; //Bank reference no and amount for CV only.
    private String referenceNo; // Reference no is Check number
    private double amount;
    private String enteredBy; // refers to user ID of the entering user
    private LocalDateTime dateEntered;
    private LocalDateTime dateLastModified;
    private String updatedBy;//refers to user ID of the updating user
    private String remarks;

    private String tinNo;

    private String office; //refers to OfficeID in Offices table

    public String getOffice() {
        return office;
    }

    public void setOffice(String office) {
        this.office = office;
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

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getEnteredBy() {
        return enteredBy;
    }

    public void setEnteredBy(String enteredBy) {
        this.enteredBy = enteredBy;
    }

    public LocalDateTime getDateEntered() {
        return dateEntered;
    }

    public void setDateEntered(LocalDateTime dateEntered) {
        this.dateEntered = dateEntered;
    }

    public LocalDateTime getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(LocalDateTime dateLastModified) {
        this.dateLastModified = dateLastModified;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getTinNo() {
        return tinNo;
    }

    public void setTinNo(String tinNo) {
        this.tinNo = tinNo;
    }

    public static String getTransactionCodeProperty() throws Exception{
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("station").equalsIgnoreCase("main") ? "BR" : "BRSub";
    }
    public static String getORTransactionCodeProperty() throws Exception{
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("station").equalsIgnoreCase("main") ? "OR" : "ORSub";
    }
    public static String getAccountCodeProperty() throws Exception{
        Properties props = new Properties();
        InputStream is = new FileInputStream("application.properties");
        props.load(is);

        return props.getProperty("account_code");
    }
}
