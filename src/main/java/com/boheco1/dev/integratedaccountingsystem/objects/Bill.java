package com.boheco1.dev.integratedaccountingsystem.objects;

import java.sql.Date;
import java.time.LocalDate;

public class Bill {
    private String billNo;
    private String billMonth;
    private LocalDate dueDate;
    private int daysDelayed;
    private double amountDue;
    private double surCharge;
    private double ch2306;
    private double ch2307;
    private double totalAmount;
    private LocalDate serviceDatefrom;
    private LocalDate serviceDateto;
    private LocalDate servicePeriodEnd;
    private ConsumerInfo consumer;
    private String consumerType;
    private double powerKWH;
    private double discount;
    public Bill(){}

    public Bill(String billNo, LocalDate from, LocalDate to, LocalDate dueDate,
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

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
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

    public void setSurCharge(double penalty) {
        System.out.println(this.consumerType);
        if (this.consumerType.equals("P") || this.consumerType.equals("S")) {
            this.surCharge = 0;
        }else {
            //Check the number of days delayed from due date
            if (this.getDaysDelayed() > 0) {
                //Exemption of penalties due to Odette
                if (this.getServicePeriodEnd().isEqual(LocalDate.of(2021, 12, 1))) {
                    this.surCharge = 0;
                //Penalty computation for Residential, BAPA, ECA
                } else if ((this.consumerType.equals("RM") || this.consumerType.equals("B") || this.consumerType.equals("E")) && this.getServicePeriodEnd().isAfter(LocalDate.of(2014, 8, 11))) {
                    this.surCharge = surCharge * 0.03;
                    this.surCharge += (this.surCharge * 0.12);
                //For Commercial Types
                } else {
                    if (this.getServicePeriodEnd().isAfter(LocalDate.of(2017, 4, 1))) {
                        if (this.getDaysDelayed() < 6) {
                            this.surCharge = 0;
                        } else if (this.getDaysDelayed() > 30) {
                            this.surCharge = this.getAmountDue() * 1.05;
                        } else {
                            this.surCharge = surCharge * 0.03;
                            this.surCharge += (this.surCharge * 0.12);
                        }
                    }
                }
            }
        }
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

    public void setTotalAmount() {
        this.totalAmount = this.getSurCharge() + this.getAmountDue() + this.getCh2306() + this.getCh2307();
    }

    public LocalDate getServiceDatefrom() {
        return serviceDatefrom;
    }

    public void setServiceDatefrom(LocalDate serviceDatefrom) {
        this.serviceDatefrom = serviceDatefrom;
    }

    public LocalDate getGetServiceDateto() {
        return serviceDateto;
    }

    public void setGetServiceDateto(LocalDate getServiceDateto) {
        this.serviceDateto = getServiceDateto;
    }

    public int getDaysDelayed() {
        return daysDelayed;
    }

    public void setDaysDelayed(int daysDelayed) {
        this.daysDelayed = daysDelayed;
    }

    public LocalDate getServicePeriodEnd() {
        return servicePeriodEnd;
    }

    public void setServicePeriodEnd(LocalDate servicePeriodEnd) {
        this.servicePeriodEnd = servicePeriodEnd;
    }

    public ConsumerInfo getConsumer() {
        return consumer;
    }

    public void setConsumer(ConsumerInfo consumer) {
        this.consumer = consumer;
    }

    public String getConsumerType() {
        return consumerType;
    }

    public void setConsumerType(String consumerType) {
        this.consumerType = consumerType;
    }

    public double getPowerKWH() {
        return powerKWH;
    }

    public void setPowerKWH(double powerKWH) {
        this.powerKWH = powerKWH;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}

