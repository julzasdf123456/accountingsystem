package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class MRT {
    private String id;
    private String returnedBy;
    private String receivedBy;
    private LocalDate dateOfReturned;

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
    }
}
