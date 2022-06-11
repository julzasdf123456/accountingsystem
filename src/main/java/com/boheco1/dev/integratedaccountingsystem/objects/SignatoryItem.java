package com.boheco1.dev.integratedaccountingsystem.objects;

public class SignatoryItem {
    private String id;
    private String mirsID;
    private String signatoryName;
    private String status;

    public SignatoryItem(String id, String mirsID, String signatoryName, String status) {
        this.id = id;
        this.mirsID = mirsID;
        this.signatoryName = signatoryName;
        this.status = status;
    }

    public String getId() {
        return this.id;
    }

    public String getMirsID() {
        return mirsID;
    }

    public void setMirsID(String mirsID) {
        this.mirsID = mirsID;
    }

    public String getSignatoryName() {
        return signatoryName;
    }

    public void setSignatoryName(String signatoryName) {
        this.signatoryName = signatoryName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
