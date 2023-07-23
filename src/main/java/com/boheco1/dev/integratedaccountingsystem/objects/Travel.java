package com.boheco1.dev.integratedaccountingsystem.objects;

public class Travel extends COBItem{
    private String trId;
    private int NoOfDays = 0;
    private double transport = 0;
    private double lodging = 0;
    private double registration = 0;
    private double incidental = 0;
    private String Mode;

    public Travel(){}

    public String getTrId() {
        return trId;
    }

    public void setTrId(String trId) {
        this.trId = trId;
    }

    public int getNoOfDays() {
        return NoOfDays;
    }

    public void setNoOfDays(int noOfDays) {
        NoOfDays = noOfDays;
    }

    public double getTransport() {
        return transport;
    }

    public void setTransport(double transport) {
        this.transport = transport;
    }

    public double getLodging() {
        return lodging;
    }

    public void setLodging(double lodging) {
        this.lodging = lodging;
    }

    public double getRegistration() {
        return registration;
    }

    public void setRegistration(double registration) {
        this.registration = registration;
    }

    public double getIncidental() {
        return incidental;
    }

    public void setIncidental(double incidental) {
        this.incidental = incidental;
    }

    public String getMode() {
        return Mode;
    }

    public void setMode(String mode) {
        Mode = mode;
    }

    public double getTotalTransport() {
        //Rate * No. of Persons * No. of Times/Travel
        return getTransport() * getQty() * getNoOfTimes();
    }

    public double getTotalLodging() {
        //Rate * No. of Days * No. of Persons * No. of Times/Travel
        return getLodging() * getNoOfDays() * getQty() * getNoOfTimes();
    }

    public double getTotalIncidental() {
        //Rate * No. of Days * No. of Persons * No. of Times/Travel
        return getIncidental() * getNoOfDays() * getQty() * getNoOfTimes();
    }

    public double getTotalTravel(){
        //Rate per diem * No. of days * No. of Person * No. of times
        return this.getCost() * getNoOfDays() * getQty() * getNoOfTimes();
    }

    public double getTotalRegistration(){
        //Rate per diem * No. of Person * No. of times
        return this.getRegistration() * getQty() * getNoOfTimes();
    }

    @Override
    public double getAmount(){
        //Rate per diem * No. of days * No. of Person * No. of times
        return getTotalTravel()
                + getTotalTransport()
                + getTotalLodging()
                + getTotalRegistration()
                + getTotalIncidental();
    }
}
