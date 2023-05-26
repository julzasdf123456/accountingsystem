package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

import java.util.Arrays;
import java.util.List;

public class ORItemSummary {
    private String accountCode;
    private String description;
    private double amount;

    private String totalView;

    public ORItemSummary() {    }
    public ORItemSummary(String description) {
        this.description=description;
        totalView = "";
    }


    public ORItemSummary(String accountCode, String description, double amount) {
        this.accountCode = accountCode;
        this.description = description;
        this.amount = amount;

        if (this.amount == 0)
            this.setTotalView(" ");
        else if (this.amount > 0)
            this.setTotalView(Utility.formatDecimal(this.amount));
        else
            this.setTotalView("("+Utility.formatDecimal(Math.abs(this.amount))+")");

    }

    public ORItemSummary(String description, double amount) {
        this.description = description;
        this.amount = amount;
        this.setTotalView(Utility.formatDecimal(this.amount)+"");
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
        if (this.amount == 0)
            this.setTotalView(" ");
        else if (this.amount > 0)
            this.setTotalView(Utility.formatDecimal(this.amount));
        else
            this.setTotalView("("+Utility.formatDecimal(Math.abs(this.amount))+")");
    }

    public String getTotalView() {
        return totalView;
    }

    public void setTotalView(String totalView) {
        this.totalView = totalView;
    }
}
