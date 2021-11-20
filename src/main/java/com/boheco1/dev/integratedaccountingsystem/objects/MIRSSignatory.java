package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDateTime;

public class MIRSSignatory {
    private int id;
    private int mirsID;
    private int userID;
    private String status;
    private String comments;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public MIRSSignatory(){}

    public MIRSSignatory(int id, int mirsID, int userID, String status, String comments, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.mirsID = mirsID;
        this.userID = userID;
        this.status = status;
        this.comments = comments;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMirsID() {
        return mirsID;
    }

    public void setMirsID(int mirsID) {
        this.mirsID = mirsID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
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
}
