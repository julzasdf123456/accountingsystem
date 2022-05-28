package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class MCT {
    private String mctNo;
    private String particulars;
    private String address;
    private String mirsNo;
    private String workOrderNo;
    private LocalDate createdAt;

    public MCT(String mctNo, String particulars, String address, String mirsNo, String workOrderNo, LocalDate createdAt) {
        this.mctNo = mctNo;
        this.particulars = particulars;
        this.address = address;
        this.mirsNo = mirsNo;
        this.workOrderNo = workOrderNo;
        this.createdAt = createdAt;
    }

    public String getMctNo() {
        return mctNo;
    }

    public void setMctNo(String mctNo) {
        this.mctNo = mctNo;
    }

    public String getParticulars() {
        return particulars;
    }

    public void setParticulars(String particulars) {
        this.particulars = particulars;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMirsNo() {
        return mirsNo;
    }

    public void setMirsNo(String mirsNo) {
        this.mirsNo = mirsNo;
    }

    public String getWorkOrderNo() {
        return workOrderNo;
    }

    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNo = workOrderNo;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }
}
