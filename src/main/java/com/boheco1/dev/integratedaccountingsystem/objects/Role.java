package com.boheco1.dev.integratedaccountingsystem.objects;

public class Role {
    private String id;
    private String role;
    private String description;

    public Role(String id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return this.role;
    }
}
