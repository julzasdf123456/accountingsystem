package com.boheco1.dev.integratedaccountingsystem.objects;

import java.sql.Date;

public class Bill {

    private String billNo;
    private String billMonth;
    private Date dueDate;
    private double amountDue;
    private double surCharge;
    private double ch2306;
    private double ch2307;
    private double totalAmount;
    private Date serviceDatefrom;
    private Date serviceDateto;

    public Bill(){}

    public Bill(String billNo, Date from, Date to, Date dueDate,
                double amountDue){
        this.billNo = billNo;
        this.serviceDatefrom = from;
        this.serviceDateto = to;
        this.dueDate = dueDate;
        this.amountDue = amountDue;
    }

    public String getBillNo() {
        return billNo;
    }

    public void setBillNo(String billNo) {
        this.billNo = billNo;
    }

    public String getBillMonth() {
        return billMonth;
    }

    public void setBillMonth(String billMonth) {
        this.billMonth = billMonth;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public double getAmountDue() {
        return amountDue;
    }

    public void setAmountDue(double amountDue) {
        this.amountDue = amountDue;
    }

    public double getSurCharge() {
        return surCharge;
    }

    public void setSurCharge(double surCharge) {
        this.surCharge = surCharge;
    }

    public double getCh2306() {
        return ch2306;
    }

    public void setCh2306(double ch2306) {
        this.ch2306 = ch2306;
    }

    public double getCh2307() {
        return ch2307;
    }

    public void setCh2307(double ch2307) {
        this.ch2307 = ch2307;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getServiceDatefrom() {
        return serviceDatefrom;
    }

    public void setServiceDatefrom(Date serviceDatefrom) {
        this.serviceDatefrom = serviceDatefrom;
    }

    public Date getGetServiceDateto() {
        return serviceDateto;
    }

    public void setGetServiceDateto(Date getServiceDateto) {
        this.serviceDateto = getServiceDateto;
    }
}

