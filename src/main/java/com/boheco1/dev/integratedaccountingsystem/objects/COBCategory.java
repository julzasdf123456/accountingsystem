package com.boheco1.dev.integratedaccountingsystem.objects;

public class COBCategory extends COBType{
    private int stypeNo;
    private String category;

    public int getStypeNo() {
        return stypeNo;
    }

    public void setStypeNo(int stypeNo) {
        this.stypeNo = stypeNo;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString(){
        return this.getCategory();
    }
}
