package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;

public class EmployeeInfo {
    private String id;
    private String employeeFirstName;
    private String employeeMidName;
    private String employeeLastName;
    private String employeeSuffix;
    private String employeeAddress;
    private String phone;
    private String designation;
    private String signatoryLevel;
    private String departmentID;

    private String fullName;
    private String departmentName;

    public EmployeeInfo(){}

    public EmployeeInfo(String id, String employeeFirstName, String employeeMidName, String employeeLastName,String employeeSuffix, String employeeAddress, String phone, String designation, String signatoryLevel, String departmentID) throws Exception {
        this.id = id;
        this.employeeFirstName = employeeFirstName;
        this.employeeMidName = employeeMidName;
        this.employeeLastName = employeeLastName;
        this.employeeSuffix = employeeSuffix;
        this.designation = designation;
        this.employeeAddress = employeeAddress;
        this.phone = phone;
        this.signatoryLevel = signatoryLevel;
        this.departmentID = departmentID;
        this.fullName = getFullName();
    }

    public String getSignatoryLevel() {
        return signatoryLevel;
    }

    public void setSignatoryLevel(String signatoryLevel) {
        this.signatoryLevel = signatoryLevel;
    }

    public String getEmployeeSuffix() {
        return employeeSuffix;
    }

    public void setEmployeeSuffix(String employeeSuffix) {
        this.employeeSuffix = employeeSuffix;
    }

    public String getFullName() {
        StringBuffer fullName = new StringBuffer();

        if(employeeLastName!=null && !employeeLastName.isEmpty()) fullName.append(employeeLastName).append(", ");
        if(employeeSuffix!=null && !employeeSuffix.isEmpty()) fullName.append(" ").append(employeeSuffix).append(", ");
        if(employeeFirstName!=null && !employeeFirstName.isEmpty()) fullName.append(employeeFirstName);
        if(employeeMidName!=null && !employeeMidName.isEmpty()) fullName.append(" ").append(employeeMidName.charAt(0)).append(".");

        return fullName.toString();
    }

    public String getDepartmentName() throws Exception {
        Department dept = DepartmentDAO.get(departmentID);
        return dept!=null ? dept.getDepartmentName() : "-";
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

    public String getDepartmentID() {
        return departmentID;
    }

    public void setDepartmentID(String departmentID) {
        this.departmentID = departmentID;
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

    public Department getDepartment() throws Exception {
        return DepartmentDAO.get(departmentID);
    }

    public String getSignatoryNameFormat(){
        StringBuffer fullName = new StringBuffer();

        if(employeeFirstName!=null && !employeeFirstName.isEmpty()) fullName.append(employeeFirstName).append(" ");
        if(employeeMidName!=null && !employeeMidName.isEmpty()) fullName.append(" ").append(employeeMidName.charAt(0)).append(". ");
        if(employeeLastName!=null && !employeeLastName.isEmpty()) fullName.append(employeeLastName).append(" ");
        if(employeeSuffix!=null && !employeeSuffix.isEmpty()) fullName.append(employeeSuffix);

        return fullName.toString().toUpperCase();
    }

    @Override
    public String toString() {
        return this.getFullName();
    }
}
