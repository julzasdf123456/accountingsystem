package com.boheco1.dev.integratedaccountingsystem.objects;

public class StockDescription {
    private String id;
    private String Description;

    public StockDescription(String id, String description) {
        this.id = id;
        Description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }
}
