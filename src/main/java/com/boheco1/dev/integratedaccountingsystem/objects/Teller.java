package com.boheco1.dev.integratedaccountingsystem.objects;

import java.util.HashMap;
import java.util.List;

public class Teller {
    private String name;
    private String address;
    private String phone;
    private HashMap<String, List<ItemSummary>> DCRBreakDown;

    public Teller() {
    }

    public Teller(String name, String address, String phone, HashMap<String, List<ItemSummary>> DCRBreakDown) {
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.DCRBreakDown = DCRBreakDown;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public HashMap<String, List<ItemSummary>> getDCRBreakDown() {
        return DCRBreakDown;
    }

    public void setDCRBreakDown(HashMap<String, List<ItemSummary>> DCRBreakDown) {
        this.DCRBreakDown = DCRBreakDown;
    }
}
