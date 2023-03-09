package com.boheco1.dev.integratedaccountingsystem.objects;

import com.itextpdf.text.pdf.AcroFields;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class ORContent {
    private String address;
    private LocalDate date;
    private String orNumber;
    private String issuedTo;
    private String issuedBy;
    private double total;
    private ObservableList<ORItemSummary> tellerCollection;
    private ObservableList<CRMDetails> customerCollection;

    public ORContent() {    }

    public ORContent(String address, LocalDate date, String orNumber, String issuedTo, String issuedBy, double total) {
        this.address = address;
        this.date = date;
        this.orNumber = orNumber;
        this.issuedTo = issuedTo;
        this.issuedBy = issuedBy;
        this.total = total;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
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

    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public ObservableList<ORItemSummary> getTellerCollection() {
        return tellerCollection;
    }

    public void setTellerCollection(ObservableList<ORItemSummary> tellerCollection) {
        this.tellerCollection = tellerCollection;
    }

    public ObservableList<CRMDetails> getCustomerCollection() {
        return customerCollection;
    }

    public void setCustomerCollection(ObservableList<CRMDetails> customerCollection) {
        this.customerCollection = customerCollection;
    }

    public void setBreakdown(ObservableList observableList){
        if(observableList instanceof ItemSummary){
            setTellerCollection(observableList);
        }else{
            setCustomerCollection(observableList);
        }

    }
}
