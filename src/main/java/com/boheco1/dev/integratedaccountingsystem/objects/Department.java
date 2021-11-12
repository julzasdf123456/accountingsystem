package com.boheco1.dev.integratedaccountingsystem.objects;

public class Department {
    private int departmentID;
    private String departmentName;
    private int departmentHeadID;

    public Department(int departmentID, String departmentName, int departmentHeadID) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.departmentHeadID = departmentHeadID;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public int getDepartmentHeadID() {
        return departmentHeadID;
    }

    public void setDepartmentHeadID(int departmentHeadID) {
        this.departmentHeadID = departmentHeadID;
    }

    @Override
    public String toString() {
        return this.departmentName;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Department && ((Department)obj).getDepartmentID()==departmentID);
    }
}
