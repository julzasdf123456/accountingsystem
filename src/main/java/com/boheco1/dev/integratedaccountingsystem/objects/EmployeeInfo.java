package com.boheco1.dev.integratedaccountingsystem.objects;

public class EmployeeInfo {
    private int id;
    private String employeeFirstName;
    private String employeeMidName;
    private String employeeLastName;
    private String employeeAddress;
    private String phone;
    private String designation;
    private int departmentID;

    public EmployeeInfo(int id, String employeeFirstName, String employeeMidName, String employeeLastName, String employeeAddress, String phone, String designation, int departmentID) {
        this.id = id;
        this.employeeFirstName = employeeFirstName;
        this.employeeMidName = employeeMidName;
        this.employeeLastName = employeeLastName;
        this.designation = designation;
        this.employeeAddress = employeeAddress;
        this.phone = phone;
        this.departmentID = departmentID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmployeeAddress() {
        return employeeAddress;
    }

    public void setEmployeeAddress(String employeeAddress) {
        this.employeeAddress = employeeAddress;
    }

    public int getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(int departmentID) {
        this.departmentID = departmentID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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
