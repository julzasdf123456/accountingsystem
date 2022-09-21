package com.boheco1.dev.integratedaccountingsystem.objects;

public class IRItem {
    private String codeNumber;
    private String itemDescription;
    private float begQty;
    private float begCost;
    private float begAmount;
    private String receiptRef;
    private float receiptQty;
    private float receiptAmount;
    private String returnsRef;
    private float returnsQty;
    private float returnsAmount;
    private int chargesQty;
    private float chargesAmount;
    private float endingQty;
    private float endingCost;
    private float endingAmount;

    public String getCodeNumber() {
        return codeNumber;
    }

    public void setCodeNumber(String codeNumber) {
        this.codeNumber = codeNumber;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public void setItemDescription(String itemDescription) {
        this.itemDescription = itemDescription;
    }

    public float getBegQty() {
        return begQty;
    }

    public void setBegQty(float begQty) {
        this.begQty = begQty;
    }

    public float getBegCost() {
        return begCost;
    }

    public void setBegCost(float begCost) {
        this.begCost = begCost;
    }

    public float getBegAmount() {
        return begAmount;
    }

    public void setBegAmount(float begAmount) {
        this.begAmount = begAmount;
    }

    public String getReceiptRef() {
        return receiptRef;
    }

    public void setReceiptRef(String receiptRef) {
        this.receiptRef = receiptRef;
    }

    public float getReceiptQty() {
        return receiptQty;
    }

    public void setReceiptQty(float receiptQty) {
        this.receiptQty = receiptQty;
    }

    public float getReceiptAmount() {
        return receiptAmount;
    }

    public void setReceiptAmount(float receiptAmount) {
        this.receiptAmount = receiptAmount;
    }

    public String getReturnsRef() {
        return returnsRef;
    }

    public void setReturnsRef(String returnsRef) {
        this.returnsRef = returnsRef;
    }

    public float getReturnsQty() {
        return returnsQty;
    }

    public void setReturnsQty(float returnsQty) {
        this.returnsQty = returnsQty;
    }

    public float getReturnsAmount() {
        return returnsAmount;
    }

    public void setReturnsAmount(float returnsAmount) {
        this.returnsAmount = returnsAmount;
    }

    public int getChargesQty() {
        return chargesQty;
    }

    public void setChargesQty(int chargesQty) {
        this.chargesQty = chargesQty;
    }

    public float getChargesAmount() {
        return chargesAmount;
    }

    public void setChargesAmount(float chargesAmount) {
        this.chargesAmount = chargesAmount;
    }

    public float getEndingQty() {
        return endingQty;
    }

    public void setEndingQty(float endingQty) {
        this.endingQty = endingQty;
    }

    public float getEndingCost() {
        return endingCost;
    }

    public void setEndingCost(float endingCost) {
        this.endingCost = endingCost;
    }

    public float getEndingAmount() {
        return endingAmount;
    }

    public void setEndingAmount(float endingAmount) {
        this.endingAmount = endingAmount;
    }
}
