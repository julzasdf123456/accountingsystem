package com.boheco1.dev.integratedaccountingsystem.objects;

public class OROtherInfo {
    private String paymentMode;
    private String bankAccountCode;
    private String tin;
    private String orNumber;
    private String remarks;

    public OROtherInfo(String paymentMode, String bankAccountCode, String tin, String orNumber, String remarks) {
        this.paymentMode = paymentMode;
        this.bankAccountCode = bankAccountCode;
        this.tin = tin;
        this.orNumber = orNumber;
        this.remarks = remarks;
    }

    public OROtherInfo(String paymentMode, String tin, String orNumber, String remarks) {
        this.paymentMode = paymentMode;
        this.tin = tin;
        this.orNumber = orNumber;
        this.remarks = remarks;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(String orNumber) {
        this.orNumber = orNumber;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getBankAccountCode() {
        return bankAccountCode;
    }

    public void setBankAccountCode(String bankAccountCode) {
        this.bankAccountCode = bankAccountCode;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }
}
