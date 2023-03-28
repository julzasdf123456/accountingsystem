package com.boheco1.dev.integratedaccountingsystem.objects;

import com.itextpdf.text.pdf.AcroFields;
import javafx.beans.binding.ObjectExpression;
import javafx.collections.ObservableList;

import java.time.LocalDate;
import java.util.List;

public class ORContent {
    private String address;
    private LocalDate date;
    private String orNumber;
    private String issuedTo;
    private String issuedBy;
    private double total;
    private ObservableList<ORItemSummary> tellerCollection;

    private ObservableList<ORItemSummary> supplierItems;
    private ObservableList<CRMDetails> customerCollection;

    private CRMQueue crmQueue;
    private TransactionHeader transactionHeader;
    private List<TransactionDetails> tds;

    public ORContent(TransactionHeader transactionHeader, List<TransactionDetails> tds) {
        this.transactionHeader = transactionHeader;
        this.tds = tds;
    }

    public ORContent(CRMQueue crmQueue, TransactionHeader transactionHeader, List<TransactionDetails> tds) {
        this.crmQueue = crmQueue;
        this.transactionHeader = transactionHeader;
        this.tds = tds;
    }

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

    public ObservableList<ORItemSummary> getSupplierItems() {
        return supplierItems;
    }

    public void setSupplierItems(ObservableList<ORItemSummary> supplierItems) {
        this.supplierItems = supplierItems;
    }

    public void setBreakdown(ObservableList observableList){
        if(observableList instanceof ItemSummary){
            setTellerCollection(observableList);
        }else{
            setCustomerCollection(observableList);
        }

    }

    public CRMQueue getCrmQueue() {
        return crmQueue;
    }

    public void setCrmQueue(CRMQueue crmQueue) {
        this.crmQueue = crmQueue;
    }

    public TransactionHeader getTransactionHeader() {
        return transactionHeader;
    }

    public void setTransactionHeader(TransactionHeader transactionHeader) {
        this.transactionHeader = transactionHeader;
    }

    public List<TransactionDetails> getTds() {
        return tds;
    }

    public void setTds(List<TransactionDetails> tds) {
        this.tds = tds;
    }
}
