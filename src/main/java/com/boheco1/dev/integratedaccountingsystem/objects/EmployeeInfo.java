package com.boheco1.dev.integratedaccountingsystem.objects;

public class EmployeeInfo {
    private String id;
    private String employeeFirstName;
    private String employeeMidName;
    private String employeeLastName;
    private String designation;

    public EmployeeInfo(String id, String employeeFirstName, String employeeMidName, String employeeLastName, String designation) {
        this.id = id;
        this.employeeFirstName = employeeFirstName;
        this.employeeMidName = employeeMidName;
        this.employeeLastName = employeeLastName;
        this.designation = designation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmployeeFirstName() {
        return employeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        this.employeeFirstName = employeeFirstName;
    }

    public String getEmployeeMidName() {
        return employeeMidName;
    }

    public void setEmployeeMidName(String employeeMidName) {
        this.employeeMidName = employeeMidName;
    }

    public String getEmployeeLastName() {
        return employeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        this.employeeLastName = employeeLastName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }
}
