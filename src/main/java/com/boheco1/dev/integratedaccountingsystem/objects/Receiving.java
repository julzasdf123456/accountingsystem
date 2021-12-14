package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class Receiving {
    private String rrNo;
    private LocalDate date;
    private String rvNo;
    private String blwbNo;
    private String carrier;
    private String drNo;
    private String poNo;
    private String supplierId;
    private String invoiceNo;

    public Receiving() {}

    public Receiving(String rrNo, LocalDate date, String rvNo, String blwbNo, String carrier, String drNo, String poNo, String supplierId, String invoiceNo) {
        this.rrNo = rrNo;
        this.date = date;
        this.rvNo = rvNo;
        this.blwbNo = blwbNo;
        this.carrier = carrier;
        this.drNo = drNo;
        this.poNo = poNo;
        this.supplierId = supplierId;
        this.invoiceNo = invoiceNo;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getRrNo() {
        return rrNo;
    }

    public void setRrNo(String rrNo) {
        this.rrNo = rrNo;
    }

    public String getRvNo() {
        return rvNo;
    }

    public void setRvNo(String rvNo) {
        this.rvNo = rvNo;
    }

    public String getBlwbNo() {
        return blwbNo;
    }

    public void setBlwbNo(String blwbNo) {
        this.blwbNo = blwbNo;
    }

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getDrNo() {
        return drNo;
    }

    public void setDrNo(String drNo) {
        this.drNo = drNo;
    }

    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }
}
