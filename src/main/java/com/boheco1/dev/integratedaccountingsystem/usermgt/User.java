package com.boheco1.dev.integratedaccountingsystem.usermgt;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

public class User {
    private int id;
    private String userName;
    private String fullName;
    private String designation;
    private String phone;
    private String passwordHash;
    private String password;
    private List<Permission> permissions;
    private static final String KEY = "ubhc1@securityxc";

    public User(int id, String userName, String fullName, String designation, String phone) {
        this.id = id;
        this.userName = userName;
        this.fullName = fullName;
        this.designation = designation;
        this.phone = phone;
    }

    public User(int id, String userName, String fullName, String designation, String phone, String passwordHash) {
        this(id, userName, fullName, designation, phone);
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getPasswordHash() {
        return passwordHash;
    }

    @Override
    public String toString() {
        return this.fullName + " [" + this.userName + "]";
    }

}
