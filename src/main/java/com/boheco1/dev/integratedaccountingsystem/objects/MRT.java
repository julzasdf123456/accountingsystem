package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.sql.SQLException;
import java.time.LocalDate;

public class MRT {
    private String id;
    private String returnedBy;
    private String receivedBy;
    private LocalDate dateOfReturned;
    private String mirsId;
    private String purpose;
    private String receivedEmployee;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReturnedBy() {
        return returnedBy;
    }

    public void setReturnedBy(String returnedBy) {
        this.returnedBy = returnedBy;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public LocalDate getDateOfReturned() {
        return dateOfReturned;
    }

    public void setDateOfReturned(LocalDate dateOfReturned) {
        this.dateOfReturned = dateOfReturned;
    }

    public MRT(String id, String returnedBy, String receivedBy, LocalDate dateOfReturned) {
        this.id = id;
        this.returnedBy = returnedBy;
        this.receivedBy = receivedBy;
        this.dateOfReturned = dateOfReturned;
        this.receivedEmployee = "";
        EmployeeInfo emp = null;
        try {
            emp = this.getEmployee();
            this.receivedEmployee = emp.getEmployeeLastName()+", "+emp.getEmployeeFirstName();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public String getMirsId() {
        return mirsId;
    }

    public void setMirsId(String mirsId) {
        this.mirsId = mirsId;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public EmployeeInfo getEmployee() throws Exception {
        return EmployeeDAO.getOne(this.receivedBy, DB.getConnection());
    }

    public String getReceivedEmployee() {
        return receivedEmployee;
    }

    public void setReceivedEmployee(String receivedEmployee) {
        this.receivedEmployee = receivedEmployee;
    }
}
