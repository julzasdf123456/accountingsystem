package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.util.List;

public class RV {
    private String rvNo;
    private String to;
    private String purpose;
    private LocalDate rvDate;
    private LocalDate dateRecommended;
    private LocalDate dateBudgeted;
    private LocalDate dateApproved;
    private String status;
    private EmployeeInfo requisitioner;
    private EmployeeInfo recommended;
    private EmployeeInfo budgetOfficer;
    private EmployeeInfo approved;
    private double amount;
    private List<RVItem> items;

    public RV() {}

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
}
