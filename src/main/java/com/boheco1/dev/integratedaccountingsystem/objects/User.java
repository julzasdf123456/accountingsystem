package com.boheco1.dev.integratedaccountingsystem.objects;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;

import java.util.List;

public class User {
    private String id;
    private String userName;
    private String passwordHash;
    private String password;
    private List<Permission> permissions;
    private static final String KEY = "ubhc1@securityxc";

    private EmployeeInfo employeeInfo;

    public User(String id, String userName) {
        this.id = id;
        this.userName = userName;
    }

    public User(String id, String userName, String passwordHash) {
        this(id, userName);
        this.passwordHash = passwordHash;
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
            employeeInfo = EmployeeDAO.getOne(this.id, DB.getConnection());
        }

        return employeeInfo;
    }

    public String getEmployeeID() {
        return this.id;
    }

    public String getFullName() {
        try {
            return getEmployeeInfo().getFullName();
        }catch(Exception ex) {
            return "";
        }
    }

    @Override
    public String toString() {
        try {
            return getEmployeeInfo().getFullName();
        }catch(Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }

}
