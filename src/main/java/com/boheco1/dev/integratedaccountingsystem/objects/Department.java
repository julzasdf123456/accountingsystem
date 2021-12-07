package com.boheco1.dev.integratedaccountingsystem.objects;

public class Department {
    private String departmentID;
    private String departmentName;
    private String departmentHeadID;

    public Department(String departmentID, String departmentName, String departmentHeadID) {
        this.departmentID = departmentID;
        this.departmentName = departmentName;
        this.departmentHeadID = departmentHeadID;
    }

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getDepartmentHeadID() {
        return departmentHeadID;
    }

    public void setDepartmentHeadID(String departmentHeadID) {
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
