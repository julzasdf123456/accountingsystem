package com.boheco1.dev.integratedaccountingsystem.objects;



import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class IEOP {
    private String orNumber;
    private String consumerName;
    private String consumerAddress;
    private LocalDate orDate;
    private double sales;
    private double wtax;
    private double netCash;

    private String salesView;
    private String wtaxView;
    private String netCashView;

    public IEOP() {
    }

    public IEOP(String orNumber, String consumerName, String consumerAddress, LocalDateTime localDateTime, double sales, double wtax, double netCash) {
        this.orNumber = orNumber;
        this.consumerName = consumerName;
        this.consumerAddress = consumerAddress;
        this.orDate = localDateTime.toLocalDate();
        this.sales = sales;
        this.wtax = wtax;
        this.netCash = netCash;
        this.salesView = Utility.formatDecimal(sales);
        this.wtaxView = Utility.formatDecimal(wtax);
        this.netCashView = Utility.formatDecimal(netCash);
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

    public double getSales() {
        return sales;
    }

    public void setSales(double sales) {
        this.sales = sales;
    }

    public double getWtax() {
        return wtax;
    }

    public void setWtax(double wtax) {
        this.wtax = wtax;
    }

    public double getNetCash() {
        return netCash;
    }

    public void setNetCash(double netCash) {
        this.netCash = netCash;
    }

    public String getSalesView() {
        return salesView;
    }

    public void setSalesView(String salesView) {
        this.salesView = salesView;
    }

    public String getWtaxView() {
        return wtaxView;
    }

    public void setWtaxView(String wtaxView) {
        this.wtaxView = wtaxView;
    }

    public String getNetCashView() {
        return netCashView;
    }

    public void setNetCashView(String netCashView) {
        this.netCashView = netCashView;
    }
}
