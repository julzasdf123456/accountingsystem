package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MIRS {
    private String id;
    private LocalDate dateFiled;
    private String purpose;
    private String details;
    private String status;
    private String requisitionerID;
    private String userID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private User user;
    private EmployeeInfo requisitioner;

    private String address;

    private String applicant;

    private String workOrderNo;

    public MIRS(){}

    public String getWorkOrderNo() {
        return workOrderNo;
    }

    public void setWorkOrderNo(String workOrderNo) {
        this.workOrderNo = workOrderNo;
    }

    public MIRS(String id, LocalDate dateFiled, String purpose, String details, String status, String requisitionerID, String userID, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dateFiled = dateFiled;
        this.purpose = purpose;
        this.details = details;
        this.status = status;
        this.requisitionerID = requisitionerID;
        this.userID = userID;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public MIRS(String id, LocalDate dateFiled, String purpose, String details, String status, String requisitionerID, String userID, LocalDateTime createdAt, LocalDateTime updatedAt, String address, String applicant, String workOrderNo) {
        this.id = id;
        this.dateFiled = dateFiled;
        this.purpose = purpose;
        this.details = details;
        this.status = status;
        this.requisitionerID = requisitionerID;
        this.userID = userID;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.address = address;
        this.applicant = applicant;
        this.workOrderNo = workOrderNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getApplicant() {
        return applicant;
    }

    public void setApplicant(String applicant) {
        this.applicant = applicant;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDate getDateFiled() {
        return dateFiled;
    }

    public void setDateFiled(LocalDate dateFiled) {
        this.dateFiled = dateFiled;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRequisitionerID() {
        return requisitionerID;
    }

    public void setRequisitionerID(String requisitionerID) {
        this.requisitionerID = requisitionerID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() throws Exception {
        if(user==null) {
            user = UserDAO.get(userID);
        }
        return user;
    }

    public EmployeeInfo getRequisitioner() throws Exception {
        if(requisitioner==null) {
            requisitioner = UserDAO.get(requisitionerID).getEmployeeInfo();
        }
        return requisitioner;
    }
}
