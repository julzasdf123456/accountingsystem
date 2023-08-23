package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PurchaseOrder {
    public static final String PENDING_APPROVAL = "Pending Approval";
    public static final String APPROVED = "Approved";
    public static final String PENDING_REVISION = "Pending Revision";
    private String poNo;
    private LocalDate poDate;
    private String to;
    private String address;
    private String contact;
    private String terms;
    private double amount;
    private String status;
    private String remarks;
    private LocalDate dateBoard;
    private LocalDate dateAccepted;
    private LocalDate dateApproved;
    private EmployeeInfo generalManager;
    private EmployeeInfo preparer;
    private List<POItem> items = new ArrayList<POItem>();
    private int noOfItems = 0;
    public PurchaseOrder(){}

    public PurchaseOrder(String no, LocalDate date, String to, String address, String contact, String terms, double amount, String status, String remarks, LocalDate board, LocalDate accepted, EmployeeInfo gm, EmployeeInfo preparer){
        this.poNo = no;
        this.poDate = date;
        this.to = to;
        this.address = address;
        this.contact = contact;
        this.terms = terms;
        this.amount = amount;
        this.status = status;
        this.remarks = remarks;
        this.dateBoard = board;
        this.dateAccepted = accepted;
        this.generalManager = gm;
        this.preparer = preparer;
    }
    public String getPoNo() {
        return poNo;
    }

    public void setPoNo(String poNo) {
        this.poNo = poNo;
    }

    public LocalDate getPoDate() {
        return poDate;
    }

    public void setPoDate(LocalDate poDate) {
        this.poDate = poDate;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getTerms() {
        return terms;
    }

    public void setTerms(String terms) {
        this.terms = terms;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDate getDateBoard() {
        return dateBoard;
    }

    public void setDateBoard(LocalDate dateBoard) {
        dateBoard = dateBoard;
    }

    public LocalDate getDateAccepted() {
        return dateAccepted;
    }

    public void setDateAccepted(LocalDate dateAccepted) {
        dateAccepted = dateAccepted;
    }

    public EmployeeInfo getGeneralManager() {
        return generalManager;
    }

    public void setGeneralManager(EmployeeInfo generalManager) {
        this.generalManager = generalManager;
    }

    public EmployeeInfo getPreparer() {
        return preparer;
    }

    public void setPreparer(EmployeeInfo preparer) {
        this.preparer = preparer;
    }

    public List<POItem> getItems() {
        return items;
    }

    public void setItems(List<POItem> items) {
        this.items = items;
    }

    public LocalDate getDateApproved() {
        return dateApproved;
    }

    public void setDateApproved(LocalDate dateApproved) {
        this.dateApproved = dateApproved;
    }

    public int getNoOfItems() {
        return noOfItems;
    }

    public void setNoOfItems(int noOfItems) {
        this.noOfItems = noOfItems;
    }
}
