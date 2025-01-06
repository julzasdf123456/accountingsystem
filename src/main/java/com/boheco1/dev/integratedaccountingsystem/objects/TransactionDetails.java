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
    private Double debit;
    private Double credit;
    private LocalDate orDate;
    private String bankID;
    private String note;
    private String checkNumber;
    private String particulars;

    private String specification;

    private String newParticularsLabel;

    private boolean editable;
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

    public String getSpecification() {
        return specification;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public void setSpecification(String specification) {
        this.specification = specification;
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
        if (debit != null) {
            return debit;
        } else {
            return 0;
        }
    }

    public void setDebit(double debit) {
        this.debit = debit;
    }

    public double getCredit() {
        if (credit != null) {
            return credit;
        } else {
            return 0;
        }
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
        if(credit != 0)
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
        if(credit != null && credit > 0) {
            return Utility.formatNumberTwoDecimal(credit);
        }else {
            return "(" + Utility.formatNumberTwoDecimal(debit) + ")";
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
