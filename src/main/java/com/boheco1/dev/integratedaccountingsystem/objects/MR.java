package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.time.LocalDate;

public class MR {
    private String id;
    private String employeeId;

    private EmployeeInfo employeeInfo;

    private String warehousePersonnelId;
    private LocalDate dateOfMR;
    private String status; //active, lost, damaged, returned
    private LocalDate dateOfReturn;

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

    public MR(String id, String employeeId, String warehousePersonnelId, LocalDate dateOfMR, String status, String recommending, String approvedBy) {
        this.id = id;
        this.employeeId = employeeId;
        this.warehousePersonnelId = warehousePersonnelId;
        this.dateOfMR = dateOfMR;
        this.status = status;
        this.recommending = recommending;
        this.approvedBy = approvedBy;
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

    public LocalDate getDateOfReturn() {
        return dateOfReturn;
    }

    public void setDateOfReturn(LocalDate dateOfReturn) {
        this.dateOfReturn = dateOfReturn;
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
}
