package com.boheco1.dev.integratedaccountingsystem.objects;

public class MRTItem {
    private String id;
    private String releasingID;
    private String mrtID;
    private int quantity;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReleasingID() {
        return releasingID;
    }

    public void setReleasingID(String releasingID) {
        this.releasingID = releasingID;
    }

    public String getMrtID() {
        return mrtID;
    }

    public void setMrtID(String mrtID) {
        this.mrtID = mrtID;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public MRTItem(String id, String releasingID, String mrtID, int quantity) {
        this.id = id;
        this.releasingID = releasingID;
        this.mrtID = mrtID;
        this.quantity = quantity;
    }
}
