package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TransactionDetails {
    private LocalDate period;
    private String transactionNumber;
    private String transactionCode;
    private LocalDate transactionDate;
    private int sequenceNumber;
    private String accountCode;
    private double debit;
    private double credit;
    private LocalDate orDate;
    private String bankID;
    private String note;
    private String checkNumber;
    private String particulars;

    private String newParticularsLabel;

    private LocalDate depositedDate;

    public LocalDate getDepositedDate() {
        return depositedDate;
    }

    public void setDepositedDate(LocalDate depositedDate) {
        this.depositedDate = depositedDate;
    }

    public String getNewParticularsLabel() {
        return newParticularsLabel;
    }

    public void setNewParticularsLabel(String newParticularsLabel) {
        this.newParticularsLabel = newParticularsLabel;
    }

    public String getParticularsLabel(){
        if(newParticularsLabel==null)
            return particulars;
        else
            return newParticularsLabel;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

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

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
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

    public String getDebitView(){
        return debit>0 ? Utility.formatDecimal(debit):"-";
    }

    public String getCreditView(){
        return credit>0 ? Utility.formatDecimal(credit):"-";
    }

    public String getAmountView(){
        double amount;
        if(credit!=0)
            amount = credit;
        else
            amount = debit;

        if(amount > 0)
            return Utility.formatDecimal(amount);
        else if(amount == 0)
            return "0.00";
        else
            return "("+Utility.formatDecimal(Math.abs(amount))+")";
    }

    public double getAmount(){
        double amount;
        if(credit!=0)
            amount = credit;
        else
            amount = debit;

        return amount;
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

    public String getFormattedDebit() {
        return String.format("%,.2f", debit);
    }

    public String getFormattedARAmount() {
        if(this.credit>0) {
            return String.format("%,.2f", this.credit);
        }else {
            return String.format("(%,.2f)", this.debit);
        }
    }

    @Override
    public String toString() {
        return "TransactionDetails{" +
                "period=" + period +
                ", transactionNumber='" + transactionNumber + '\'' +
                ", transactionCode='" + transactionCode + '\'' +
                ", transactionDate=" + transactionDate +
                ", sequenceNumber=" + sequenceNumber +
                ", accountCode='" + accountCode + '\'' +
                ", debit=" + debit +
                ", credit=" + credit +
                ", orDate=" + orDate +
                ", bankID='" + bankID + '\'' +
                ", note='" + note + '\'' +
                ", checkNumber='" + checkNumber + '\'' +
                ", particulars='" + particulars + '\'' +
                ", newParticularsLabel='" + newParticularsLabel + '\'' +
                ", depositedDate=" + depositedDate +
                '}';
    }
}
