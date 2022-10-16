package com.boheco1.dev.integratedaccountingsystem.objects;

import java.sql.Date;

public class PaidBill extends Bill{

    private double power;
    private double meter;
    private double pr;
    private double others;
    private String paymentType;
    private int orNumber;
    private String teller;
    private int dcrNumber;
    private Date postingDate;
    private int postingSequence;
    private double promptPayment;
    private double Surcharge;
    private double SLAdjustment;
    private double OtherDeduction;
    private Date ORDate;
    private double MDRefund;
    private double Form2306;
    private double Form2307;
    private double Amount2306;
    private double Amount2307;
    private double ServiceFee;
    private double  Others1;
    private double Others2;

    public PaidBill(){}

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

    public double getPr() {
        return pr;
    }

    public void setPr(double pr) {
        this.pr = pr;
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

    public int getDcrNumber() {
        return dcrNumber;
    }

    public void setDcrNumber(int dcrNumber) {
        this.dcrNumber = dcrNumber;
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

    public double getSurcharge() {
        return Surcharge;
    }

    public void setSurcharge(double surcharge) {
        Surcharge = surcharge;
    }

    public double getSLAdjustment() {
        return SLAdjustment;
    }

    public void setSLAdjustment(double SLAdjustment) {
        this.SLAdjustment = SLAdjustment;
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

    public double getMDRefund() {
        return MDRefund;
    }

    public void setMDRefund(double MDRefund) {
        this.MDRefund = MDRefund;
    }

    public double getForm2306() {
        return Form2306;
    }

    public void setForm2306(double form2306) {
        Form2306 = form2306;
    }

    public double getForm2307() {
        return Form2307;
    }

    public void setForm2307(double form2307) {
        Form2307 = form2307;
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

    public double getServiceFee() {
        return ServiceFee;
    }

    public void setServiceFee(double serviceFee) {
        ServiceFee = serviceFee;
    }

    public double getOthers1() {
        return Others1;
    }

    public void setOthers1(double others1) {
        Others1 = others1;
    }

    public double getOthers2() {
        return Others2;
    }

    public void setOthers2(double others2) {
        Others2 = others2;
    }
}
