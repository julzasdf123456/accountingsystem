package com.boheco1.dev.integratedaccountingsystem.objects;

public class CRMDetails {
    String id;
    String referenceNo;
    String particulars;
    String glCode;
    double subTotal;
    double VAT;
    double total;
    public CRMDetails(){}
    public CRMDetails(String id, String referenceNo, String particulars, String glCode, double subTotal, double VAT, double total) {
        this.id = id;
        this.referenceNo = referenceNo;
        this.particulars = particulars;
        this.glCode = glCode;
        this.subTotal = subTotal;
        this.VAT = VAT;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReferenceNo() {
        return referenceNo;
    }

    public void setReferenceNo(String referenceNo) {
        this.referenceNo = referenceNo;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getGlCode() {
        return glCode;
    }

    public void setGlCode(String glCode) {
        this.glCode = glCode;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CRMDetails{" +
                "id='" + id + '\'' +
                ", referenceNo='" + referenceNo + '\'' +
                ", particulars='" + particulars + '\'' +
                ", glCode='" + glCode + '\'' +
                ", subTotal=" + subTotal +
                ", VAT=" + VAT +
                ", total=" + total +
                '}';
    }
}
