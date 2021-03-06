package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.time.LocalDate;

public class MR {
    private String id;
    private String employeeId;

    private EmployeeInfo employeeInfo;

    private String warehousePersonnelId;
    private LocalDate dateOfMR;
    private String status;
    private String purpose;
    private LocalDate dateRecommended;
    private LocalDate dateApproved;
    private LocalDate dateReleased;
    private String recommending;

    private String approvedBy;
    public MR() {
    }

    public String getRecommending() {
        return recommending;
    }

    public void setRecommending(String recommending) {
        this.recommending = recommending;
    }

    public String getApprovedBy() {
        return approvedBy;
    }

    public void setApprovedBy(String approvedBy) {
        this.approvedBy = approvedBy;
    }

    public MR(String id, String employeeId, String warehousePersonnelId, LocalDate dateOfMR, String status, String recommending, String approvedBy, String purpose) {
        this.id = id;
        this.employeeId = employeeId;
        this.warehousePersonnelId = warehousePersonnelId;
        this.dateOfMR = dateOfMR;
        this.status = status;
        this.recommending = recommending;
        this.approvedBy = approvedBy;
        this.purpose = purpose;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getWarehousePersonnelId() {
        return warehousePersonnelId;
    }

    public void setWarehousePersonnelId(String warehousePersonnelId) {
        this.warehousePersonnelId = warehousePersonnelId;
    }

    public LocalDate getDateOfMR() {
        return dateOfMR;
    }

    public void setDateOfMR(LocalDate dateOfMR) {
        this.dateOfMR = dateOfMR;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public EmployeeInfo getEmployeeInfo() throws Exception{
        if(employeeInfo==null) {
            this.employeeInfo = EmployeeDAO.getOne(employeeId, DB.getConnection());
        }
        return employeeInfo;
    }

    public void setEmployeeInfo(EmployeeInfo employeeInfo) {
        this.employeeInfo = employeeInfo;
    }

    public void insertItem(MrItem item) throws Exception {
        MrDAO.createItem(this, item);
    }

    public LocalDate getDateRecommended() {
        return dateRecommended;
    }

    public void setDateRecommended(LocalDate dateRecommended) {
        this.dateRecommended = dateRecommended;
    }

    public LocalDate getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDate dateApproved) {
        this.dateApproved = dateApproved;
    }

    public LocalDate getDateReleased() {
        return dateReleased;
    }

    public void setDateReleased(LocalDate dateReleased) {
        this.dateReleased = dateReleased;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String toString(){
        return this.id;
    }
}
