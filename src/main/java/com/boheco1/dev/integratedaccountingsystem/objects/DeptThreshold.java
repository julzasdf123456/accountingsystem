package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.DeptThresholdDAO;

public class DeptThreshold {
    private String thresID;
    private double threshAmount;
    private String departmentID;
    private String appID;

    public DeptThreshold() {}

    public DeptThreshold(String thresID, double threshAmount, String departmentID, String appID) {
        this.thresID = thresID;
        this.threshAmount = threshAmount;
        this.departmentID = departmentID;
        this.appID = appID;
    }

    public String getThresID() {
        return thresID;
    }

    public void setThresID(String thresID) {
        this.thresID = thresID;
    }

    public double getThreshAmount() {
        return threshAmount;
    }

    public void setThreshAmount(double threshAmount) {
        this.threshAmount = threshAmount;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getDepartmentName() throws Exception {
        return DepartmentDAO.get(this.departmentID).getDepartmentName();
    }

    public String getThreshAmountStr() {
        return String.format("₱ %,.2f", threshAmount);
    }

    public String getDeptAppropriationsStr() throws Exception{
        return DeptThresholdDAO.getTotalAppropriationsStr(this);
    }

    public double getDeptAppropriations() throws Exception {
        return DeptThresholdDAO.getTotalAppropriations(this);
    }
}
