package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

public class Teller {
    private String id;
    private String name;
    private String address;
    private String phone;
    private LocalDate date;
    private HashMap<String, List<ItemSummary>> DCRBreakDown;

    public Teller() {
    }

    public Teller(String id, String name, String address, String phone, LocalDate date, HashMap<String, List<ItemSummary>> DCRBreakDown) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.date=date;
        this.DCRBreakDown = DCRBreakDown;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public HashMap<String, List<ItemSummary>> getDCRBreakDown() {
        return DCRBreakDown;
    }

    public void setDCRBreakDown(HashMap<String, List<ItemSummary>> DCRBreakDown) {
        this.DCRBreakDown = DCRBreakDown;
    }
}
