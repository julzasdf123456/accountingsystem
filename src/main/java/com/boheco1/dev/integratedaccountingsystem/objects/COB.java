package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.util.List;

public class COB {
    private String cobId;
    private String activity;
    private double amount;
    private String status;
    private String appId;
    private String fsId;
    private EmployeeInfo prepared;
    private LocalDate datePrepared;
    private EmployeeInfo reviewed;
    private LocalDate dateReviewed;
    private EmployeeInfo approved;
    private LocalDate dateApproved;
    private List<COBItem> items;

    public COB(){}

    public COB(String cobId, String activity, double amount, String status, String appId, String fsId, EmployeeInfo prepared, LocalDate datePrepared, EmployeeInfo reviewed, LocalDate dateReviewed, EmployeeInfo approved, LocalDate dateApproved) {
        this.cobId = cobId;
        this.activity = activity;
        this.amount = amount;
        this.status = status;
        this.appId = appId;
        this.fsId = fsId;
        this.prepared = prepared;
        this.datePrepared = datePrepared;
        this.reviewed = reviewed;
        this.dateReviewed = dateReviewed;
        this.approved = approved;
        this.dateApproved = dateApproved;
    }

    public String getCobId() {
        return cobId;
    }

    public void setCobId(String cobId) {
        this.cobId = cobId;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getFsId() {
        return fsId;
    }

    public void setFsId(String fsId) {
        this.fsId = fsId;
    }

    public EmployeeInfo getPrepared() {
        return prepared;
    }

    public void setPrepared(EmployeeInfo prepared) {
        this.prepared = prepared;
    }

    public LocalDate getDatePrepared() {
        return datePrepared;
    }

    public void setDatePrepared(LocalDate datePrepared) {
        this.datePrepared = datePrepared;
    }

    public EmployeeInfo getReviewed() {
        return reviewed;
    }

    public void setReviewed(EmployeeInfo reviewed) {
        this.reviewed = reviewed;
    }

    public LocalDate getDateReviewed() {
        return dateReviewed;
    }

    public void setDateReviewed(LocalDate dateReviewed) {
        this.dateReviewed = dateReviewed;
    }

    public EmployeeInfo getApproved() {
        return approved;
    }

    public void setApproved(EmployeeInfo approved) {
        this.approved = approved;
    }

    public LocalDate getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDate dateApproved) {
        this.dateApproved = dateApproved;
    }

    public List<COBItem> getItems() {
        return items;
    }

    public void setItems(List<COBItem> items) {
        this.items = items;
    }

    public String getAppropriationStr() {
        return String.format("₱ %,.2f", this.amount);
    }
}
