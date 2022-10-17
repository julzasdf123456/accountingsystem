package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
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
    private double slAdjustment;
    private double otherAdjustment;
    private double katas;
    private double generationVat;
    private double transmissionVat;
    private double systemLossVat;
    private double distributionVat;
    private double DAAVat;
    private double acrmVat;
    private double otherVat;
    private double franchiseTax;
    private double businessTax;
    private double realPropertyTax;
    private double vatAndPassTax;

    public Bill(){}

    public Bill(String billNo, LocalDate from, LocalDate to, LocalDate dueDate, double amountDue){
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
        return this.surCharge;
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

    public double getSlAdjustment() {
        return slAdjustment;
    }

    public void setSlAdjustment(double slAdjustment) {
        this.slAdjustment = slAdjustment;
    }

    public double getOtherAdjustment() {
        return otherAdjustment;
    }

    public void setOtherAdjustment(double otherAdjustment) {
        this.otherAdjustment = otherAdjustment;
    }

    public double getKatas() {
        return katas;
    }

    public void setKatas(double katas) {
        this.katas = katas;
    }

    public double getGenerationVat() {
        return generationVat;
    }

    public void setGenerationVat(double generationVat) {
        this.generationVat = generationVat;
    }

    public double getTransmissionVat() {
        return transmissionVat;
    }

    public void setTransmissionVat(double transmissionVat) {
        this.transmissionVat = transmissionVat;
    }

    public double getSystemLossVat() {
        return systemLossVat;
    }

    public void setSystemLossVat(double systemLossVat) {
        this.systemLossVat = systemLossVat;
    }

    public double getDistributionVat() {
        return distributionVat;
    }

    public void setDistributionVat(double distributionVat) {
        this.distributionVat = distributionVat;
    }

    public double getDAAVat() {
        return DAAVat;
    }

    public void setDAAVat(double DAAVat) {
        this.DAAVat = DAAVat;
    }

    public double getAcrmVat() {
        return acrmVat;
    }

    public void setAcrmVat(double acrmVat) {
        this.acrmVat = acrmVat;
    }

    public double getOtherVat() {
        return otherVat;
    }

    public void setOtherVat(double otherVat) {
        this.otherVat = otherVat;
    }

    public double getFranchiseTax() {
        return franchiseTax;
    }

    public void setFranchiseTax(double franchiseTax) {
        this.franchiseTax = franchiseTax;
    }

    public double getBusinessTax() {
        return businessTax;
    }

    public void setBusinessTax(double businessTax) {
        this.businessTax = businessTax;
    }

    public double getRealPropertyTax() {
        return realPropertyTax;
    }

    public void setRealPropertyTax(double realPropertyTax) {
        this.realPropertyTax = realPropertyTax;
    }

    public double getVatAndPassTax() {
        return vatAndPassTax;
    }

    public void setVatAndPassTax(double vatAndPassTax) {
        this.vatAndPassTax = vatAndPassTax;
    }

    public double computeSurCharge(double penalty) {
        double amount = 0;
        if (this.consumerType.equals("P") || this.consumerType.equals("S")) {
            amount = 0;
        }else {
            //Check the number of days delayed from due date
            if (this.getDaysDelayed() > 0) {
                //Exemption of penalties due to Odette
                if (this.getServicePeriodEnd().isEqual(LocalDate.of(2021, 12, 1))) {
                    amount = 0;
                //Penalty computation for Residential, BAPA, ECA
                } else if ((this.consumerType.equals("RM") || this.consumerType.equals("B") || this.consumerType.equals("E"))
                        && this.getServicePeriodEnd().isAfter(LocalDate.of(2014, 8, 11))) {
                    amount = penalty * 0.03;
                    amount += (amount * 0.12);
                //For Commercial Types
                } else {
                    if (this.getServicePeriodEnd().isAfter(LocalDate.of(2017, 4, 1))) {
                        if (this.getDaysDelayed() < 6) {
                            amount = 0;
                        } else if (this.getDaysDelayed() > 30 && this.powerKWH > 1000) {
                            amount = penalty * 0.05;
                            amount += (amount * 0.12);
                        } else {
                            amount = penalty * 0.03;
                            amount += (amount * 0.12);
                        }
                    }
                }
                amount = Utility.round(amount, 2);
            }
        }
        return amount;
    }

    public boolean equals(Object o){
        if (o instanceof Bill){
            Bill bill = (Bill) o;
            if (bill.getBillNo().equals(this.getBillNo()))
                return true;
        }
        return false;
    }

    public void setTotalAmount() {
        this.totalAmount = Utility.round(this.getSurCharge() + this.getAmountDue() + this.getCh2306() + this.getCh2307(), 2);
    }
}

