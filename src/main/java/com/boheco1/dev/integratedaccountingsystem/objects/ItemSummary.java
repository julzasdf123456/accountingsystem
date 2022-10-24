package com.boheco1.dev.integratedaccountingsystem.objects;

public class ItemSummary {

    private String description;
    private double total;

    public ItemSummary(){}

    public ItemSummary(String desc, double total){
        this.description = desc;
        this.total = total;
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
}
