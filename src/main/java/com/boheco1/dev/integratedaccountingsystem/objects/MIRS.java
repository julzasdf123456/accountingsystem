package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class MIRS {
    private int id;
    private LocalDate dateFiled;
    private String purpose;
    private String details;
    private String status;
    private int requisitionerID;
    private int UserID;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MIRS(int id, LocalDate dateFiled, String purpose, String details, String status, int requisitionerID, int userID, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.dateFiled = dateFiled;
        this.purpose = purpose;
        this.details = details;
        this.status = status;
        this.requisitionerID = requisitionerID;
        UserID = userID;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getRequisitionerID() {
        return requisitionerID;
    }

    public void setRequisitionerID(int requisitionerID) {
        this.requisitionerID = requisitionerID;
    }

    public int getUserID() {
        return UserID;
    }

    public void setUserID(int userID) {
        UserID = userID;
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
}
