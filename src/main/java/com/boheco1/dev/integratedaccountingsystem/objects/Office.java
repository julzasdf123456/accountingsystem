package com.boheco1.dev.integratedaccountingsystem.objects;

public class Office {
    private String officeID;
    private String description;
    private String accronym;

    public Office(String officeID, String description, String accronym) {
        this.officeID = officeID;
        this.description = description;
        this.accronym = accronym;
    }

    public String getOfficeID() {
        return officeID;
    }

    public void setOfficeID(String officeID) {
        this.officeID = officeID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAccronym() {
        return accronym;
    }

    public void setAccronym(String accronym) {
        this.accronym = accronym;
    }
}
