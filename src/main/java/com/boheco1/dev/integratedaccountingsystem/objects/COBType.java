package com.boheco1.dev.integratedaccountingsystem.objects;

public class COBType {
    private int typeNo;
    private String type;

    public int getTypeNo() {
        return typeNo;
    }

    public void setTypeNo(int typeNo) {
        this.typeNo = typeNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString(){
        return this.type;
    }
}
