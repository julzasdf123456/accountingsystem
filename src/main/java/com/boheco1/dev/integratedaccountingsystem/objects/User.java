package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.util.List;

public class User {
    private String id;
    private String userName;
    private String fullName;
    private String passwordHash;
    private String password;
    private String employeeID;
    private List<Permission> permissions;
    private static final String KEY = "ubhc1@securityxc";

    private EmployeeInfo employeeInfo;

    public User(String id, String employeeID, String userName, String fullName) {
        this.id = id;
        this.employeeID = employeeID;
        this.userName = userName;
        this.fullName = fullName;
    }

    public User(String id, String employeeID, String userName, String fullName, String passwordHash) {
        this(id, employeeID, userName, fullName);
        this.passwordHash = passwordHash;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public boolean can(String permission) {
        for(Permission p: permissions) {
            if( p.getPermission().equalsIgnoreCase(permission)) {
                return true;
            }
        }
        return false;
    }

    public EmployeeInfo getEmployeeInfo() throws Exception {

        if(employeeInfo==null) {
            employeeInfo = EmployeeDAO.getOne(this.employeeID, DB.getConnection());
        }

        return employeeInfo;
    }

    @Override
    public String toString() {
        return this.fullName + " [" + this.userName + "]";
    }

}
