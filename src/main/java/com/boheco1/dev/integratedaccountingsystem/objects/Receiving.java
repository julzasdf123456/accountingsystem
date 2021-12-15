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
    private String receivedBy;
    private String receivedOrigBy;
    private String verifiedBy;
    private String postedBinCardBy;

    public Receiving() {}

    public Receiving(String rrNo, LocalDate date, String rvNo, String blwbNo, String carrier, String drNo, String poNo, String supplierId, String invoiceNo, String receivedBy, String receivedOrigBy, String verifiedBy) {
        this.rrNo = rrNo;
        this.date = date;
        this.rvNo = rvNo;
        this.blwbNo = blwbNo;
        this.carrier = carrier;
        this.drNo = drNo;
        this.poNo = poNo;
        this.supplierId = supplierId;
        this.invoiceNo = invoiceNo;
        this.receivedBy = receivedBy;
        this.receivedOrigBy = receivedOrigBy;
        this.verifiedBy = verifiedBy;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getReceivedOrigBy() {
        return receivedOrigBy;
    }

    public void setReceivedOrigBy(String receivedOrigBy) {
        this.receivedOrigBy = receivedOrigBy;
    }

    public String getVerifiedBy() {
        return verifiedBy;
    }

    public void setVerifiedBy(String verifiedBy) {
        this.verifiedBy = verifiedBy;
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

    public String getPostedBinCardBy() {
        return postedBinCardBy;
    }

    public void setPostedBinCardBy(String postedBinCardBy) {
        this.postedBinCardBy = postedBinCardBy;
    }
}
