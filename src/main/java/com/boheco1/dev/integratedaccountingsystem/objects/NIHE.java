package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class NIHE {
    private String orNumber;
    private String consumerName;
    private String consumerAddress;
    private LocalDate orDate;
    private double amount;
    private String amountView;

    public NIHE() {
    }

    public NIHE(String orNumber, String consumerName, String consumerAddress, LocalDateTime localDateTime, double amount) {
        this.orNumber = orNumber;
        this.consumerName = consumerName;
        this.consumerAddress = consumerAddress;
        this.orDate = localDateTime.toLocalDate();
        this.amount = amount;
        this.amountView = Utility.formatDecimal(amount);
    }

    public String getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(String orNumber) {
        this.orNumber = orNumber;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getConsumerAddress() {
        return consumerAddress;
    }

    public void setConsumerAddress(String consumerAddress) {
        this.consumerAddress = consumerAddress;
    }

    public LocalDate getOrDate() {
        return orDate;
    }

    public void setOrDate(LocalDate orDate) {
        this.orDate = orDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAmountView() {
        return amountView;
    }

    public void setAmountView(String amountView) {
        this.amountView = amountView;
    }
}
