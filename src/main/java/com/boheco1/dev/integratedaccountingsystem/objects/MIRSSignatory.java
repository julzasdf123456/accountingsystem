package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MIRSSignatory {
    private String id;
    private String mirsID;
    private String userID;
    private String status;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private LocalDate mirsdateFiled;
    private String purpose;

    public MIRSSignatory(){}

    public MIRSSignatory(String id, String mirsID, String userID, String status, String comments, LocalDateTime createdAt, LocalDateTime updatedAt
    ) {
        this.id = id;
        this.mirsID = mirsID;
        this.userID = userID;
        this.status = status;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;

    }

    public MIRSSignatory(String id, String mirsID, String userID, String status, String comments, LocalDateTime createdAt, LocalDateTime updatedAt, LocalDate mirsdateFiled, String purpose) {
        this.id = id;
        this.mirsID = mirsID;
        this.userID = userID;
        this.status = status;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.mirsdateFiled = mirsdateFiled;
        this.purpose = purpose;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMirsID() {
        return mirsID;
    }

    public void setMirsID(String mirsID) {
        this.mirsID = mirsID;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
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

    public LocalDate getMirsdateFiled() {
        return mirsdateFiled;
    }

    public void setMirsdateFiled(LocalDate mirsdateFiled) {
        this.mirsdateFiled = mirsdateFiled;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }

    public String toString(){
        String result ="";
        try {
            EmployeeInfo temp = EmployeeDAO.getOne(userID, DB.getConnection());
            result = temp.getEmployeeFirstName()+" "+temp.getEmployeeLastName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toUpperCase();
    }
}
