package com.boheco1.dev.integratedaccountingsystem.objects;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PaidBill extends Bill{

    private double power;
    private double meter;
    private double others;
    private String paymentType;
    private int orNumber;
    private String teller;
    private String dcrNumber;

    private String InvoiceNumber;
    private Date postingDate;
    private String postingTime;
    private int postingSequence;
    private double promptPayment;
    private double OtherDeduction;
    private Date ORDate;
    private double Amount2306;
    private double Amount2307;
    private double cashAmount;
    private double checkAmount;
    private double deposit;
    private List<Check> checks;

    public PaidBill(){}

    public PaidBill(String billNo, LocalDate from, LocalDate to, LocalDate dueDate, double amountDue){
        super(billNo, from, to, dueDate, amountDue);
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }

    public double getMeter() {
        return meter;
    }

    public void setMeter(double meter) {
        this.meter = meter;
    }

    public double getOthers() {
        return others;
    }

    public void setOthers(double others) {
        this.others = others;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public int getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(int orNumber) {
        this.orNumber = orNumber;
    }

    public String getTeller() {
        return teller;
    }

    public void setTeller(String teller) {
        this.teller = teller;
    }

    public String getDcrNumber() {
        return dcrNumber;
    }

    public String getInvoiceNumber() {
        return InvoiceNumber;
    }

    public void setDcrNumber(String dcrNumber) {
        this.dcrNumber = dcrNumber;
    }

    public void setInvoiceNumber(String Number) {
        this.InvoiceNumber = Number;
    }

    public Date getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(Date postingDate) {
        this.postingDate = postingDate;
    }

    public int getPostingSequence() {
        return postingSequence;
    }

    public void setPostingSequence(int postingSequence) {
        this.postingSequence = postingSequence;
    }

    public double getPromptPayment() {
        return promptPayment;
    }

    public void setPromptPayment(double promptPayment) {
        this.promptPayment = promptPayment;
    }

    public double getOtherDeduction() {
        return OtherDeduction;
    }

    public void setOtherDeduction(double otherDeduction) {
        OtherDeduction = otherDeduction;
    }

    public Date getORDate() {
        return ORDate;
    }

    public void setORDate(Date ORDate) {
        this.ORDate = ORDate;
    }

    public double getAmount2306() {
        return Amount2306;
    }

    public void setAmount2306(double amount2306) {
        Amount2306 = amount2306;
    }

    public double getAmount2307() {
        return Amount2307;
    }

    public void setAmount2307(double amount2307) {
        Amount2307 = amount2307;
    }

    public double getCashAmount() {
        return cashAmount;
    }

    public void setCashAmount(double cashAmount) {
        this.cashAmount = cashAmount;
    }

    public double getCheckAmount() {
        return checkAmount;
    }

    public void setCheckAmount(double checkAmount) {
        this.checkAmount = checkAmount;
    }

    public List<Check> getChecks() {
        return checks;
    }

    public void setChecks(List<Check> checks) {
        this.checks = checks;
    }

    public String getPostingTime() {
        return postingTime;
    }

    public void setPostingTime(String postingTime) {
        this.postingTime = postingTime;
    }

    public double getDeposit() {
        return deposit;
    }

    public void setDeposit(double deposit) {
        this.deposit = deposit;
    }

    @Override
    public String toString() {
        return "Account: "+ getConsumer().getAccountID() + " - Billing: " + getServicePeriodEnd().format(DateTimeFormatter.ofPattern("M/d/yyyy"));
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof Bill){
            Bill bill = (Bill) o;
            if (bill.getBillNo().equals(this.getBillNo())
                    && bill.getConsumer().getAccountID().equals(this.getConsumer().getAccountID()))
                return true;
        }
        return false;
    }

}
