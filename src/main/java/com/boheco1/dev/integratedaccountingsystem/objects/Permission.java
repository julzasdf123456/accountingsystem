package com.boheco1.dev.integratedaccountingsystem.objects;

public class Permission {
    private String id;
    private String permission;
    private String remarks;

    public Permission(String id, String permission, String remarks) {
        this.id = id;
        this.permission = permission;
        this.remarks = remarks;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Permission) {
            Permission p = (Permission) obj;
            return p.getId()==this.id && p.getPermission().equalsIgnoreCase(this.permission);
        }
        return false;
    }

    @Override
    public String toString() {
        return this.permission;
    }
}
