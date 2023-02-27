package com.boheco1.dev.integratedaccountingsystem.objects;

import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

public class Teller {
    private String username;
    private String name;
    private String address;
    private String phone;
    private LocalDate date;
    private String orNumber;
    private String issuedTo;
    private HashMap<String, List<ItemSummary>> DCRBreakDown;

    private ObservableList<ItemSummary> orBreakDown;

    public Teller() {
    }

    public Teller(String username, String name, String address, String phone, LocalDate date, HashMap<String, List<ItemSummary>> DCRBreakDown) {
        this.username = username;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.date=date;
        this.DCRBreakDown = DCRBreakDown;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public String getOrNumber() {
        return orNumber;
    }

    public void setOrNumber(String orNumber) {
        this.orNumber = orNumber;
    }

    public String getIssuedTo() {
        return issuedTo;
    }

    public void setIssuedTo(String issuedTo) {
        this.issuedTo = issuedTo;
    }

    public ObservableList<ItemSummary> getOrBreakDown() {
        return orBreakDown;
    }

    public void setOrBreakDown(ObservableList<ItemSummary> orBreakDown) {
        this.orBreakDown = orBreakDown;
    }
}
