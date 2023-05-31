package com.boheco1.dev.integratedaccountingsystem.objects;

public class APP {
    private String appId;
    private String year;
    private boolean isOpen;
    private String boardRes;

    private double totalBudget;

    public APP(){}

    public APP(String appId, String year, String boardRes, boolean isOpen, double totalBudget){
        this.appId = appId;
        this.year = year;
        this.boardRes = boardRes;
        this.isOpen = isOpen;
        this.totalBudget = totalBudget;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public String getBoardRes() {
        return boardRes;
    }

    public void setBoardRes(String boardRes) {
        this.boardRes = boardRes;
    }

    public void setTotalBudget(Double totalBudget) { this.totalBudget = totalBudget; }

    public double getTotalBudget() { return totalBudget; }

    public String getIsOpenString() {
        return this.isOpen ? "Yes" : "No";
    }

    public String getFormattedTotalBudget() {
        return String.format("₱ %,.2f", totalBudget);
    }
}
