package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.util.List;

public class RV {
    public static final String PENDING_CERTIFICATION = "Pending Certification";
    public static final String PENDING_REVISION = "Pending Revision";
    public static final String PENDING_RECOMMENDATION = "Pending Review";
    public static final String PENDING_APPROVAL = "Pending Approval";

    public static final String APPROVED = "Approved";
    private String rvNo;
    private String to;
    private String purpose;
    private LocalDate rvDate;
    private LocalDate dateRecommended;
    private LocalDate dateBudgeted;
    private LocalDate dateApproved;
    private String status;
    private String remarks;
    private EmployeeInfo requisitioner;
    private EmployeeInfo recommended;
    private EmployeeInfo budgetOfficer;
    private EmployeeInfo approved;
    private double amount;
    private List<RVItem> items;
    private int NoOfItems = 0;
    public RV() {}

    public RV(String no, String to, String purpose, double amount, String status, String remarks, EmployeeInfo r, EmployeeInfo m, EmployeeInfo b, EmployeeInfo gm, LocalDate rvDate, LocalDate dateRec, LocalDate dateBudg, LocalDate dateAppr) {
        this.rvNo = no;
        this.to = to;
        this.purpose = purpose;
        this.amount = amount;
        this.status = status;
        this.remarks = remarks;
        this.requisitioner = r;
        this.recommended = m;
        this.budgetOfficer = b;
        this.approved = gm;
        this.rvDate = rvDate;
        this.dateRecommended = dateRec;
        this.dateBudgeted = dateBudg;
        this.dateApproved = dateAppr;
    }

    public String getRvNo() {
        return rvNo;
    }

    public void setRvNo(String rvNo) {
        this.rvNo = rvNo;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public LocalDate getRvDate() {
        return rvDate;
    }

    public void setRvDate(LocalDate rvDate) {
        this.rvDate = rvDate;
    }

    public LocalDate getDateRecommended() {
        return dateRecommended;
    }

    public void setDateRecommended(LocalDate dateRecommended) {
        this.dateRecommended = dateRecommended;
    }

    public LocalDate getDateBudgeted() {
        return dateBudgeted;
    }

    public void setDateBudgeted(LocalDate dateBudgeted) {
        this.dateBudgeted = dateBudgeted;
    }

    public LocalDate getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDate dateApproved) {
        this.dateApproved = dateApproved;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeInfo getRequisitioner() {
        return requisitioner;
    }

    public void setRequisitioner(EmployeeInfo requisitioner) {
        this.requisitioner = requisitioner;
    }

    public EmployeeInfo getRecommended() {
        return recommended;
    }

    public void setRecommended(EmployeeInfo recommended) {
        this.recommended = recommended;
    }

    public EmployeeInfo getBudgetOfficer() {
        return budgetOfficer;
    }

    public void setBudgetOfficer(EmployeeInfo budgetOfficer) {
        this.budgetOfficer = budgetOfficer;
    }

    public EmployeeInfo getApproved() {
        return approved;
    }

    public void setApproved(EmployeeInfo approved) {
        this.approved = approved;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public List<RVItem> getItems() {
        return items;
    }

    public void setItems(List<RVItem> items) {
        this.items = items;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getNoOfItems() {
        return NoOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        NoOfItems = noOfItems;
    }
}
