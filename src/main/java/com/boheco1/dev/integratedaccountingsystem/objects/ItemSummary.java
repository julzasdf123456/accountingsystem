package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;

import java.util.Arrays;
import java.util.List;

public class ItemSummary {

    private String description;
    private double total;
    private String totalView;

    private double VAT;
    private double subTotal;

    public ItemSummary(){}

    public ItemSummary(String desc, double total){
        this.description = desc;
        this.total = total;

        String[] ars = {"S/L Adjustments", "PPD", "Katas Ng VAT", "Other Deductions", "MD Refund", "SC Discount", "2307 (2%)", "2307 (5%)"};
        List<String> negVal = Arrays.asList(ars);

        if (negVal.contains(desc)) {
            if (this.total != 0)
                this.setTotalView("("+Utility.formatDecimal(this.total)+")");
            else
                this.setTotalView(" ");
        }else{
            if (this.total == 0)
                this.setTotalView(" ");
            else if (this.total > 0)
                this.setTotalView(Utility.formatDecimal(this.total));
            else
                this.setTotalView("("+Utility.formatDecimal(this.total*-1)+")");
        }
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getTotalView() {
        return totalView;
    }

    public void setTotalView(String totalView) {
        this.totalView = totalView;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }
}
