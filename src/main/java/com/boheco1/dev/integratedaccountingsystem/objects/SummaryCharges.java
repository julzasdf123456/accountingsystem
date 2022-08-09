package com.boheco1.dev.integratedaccountingsystem.objects;

public class SummaryCharges {

    public  SummaryCharges(){}

    String id; String nEACode; String localCode; String acctgCode; String description; double price; int quantity; int issued; int received; int returned; int balance;

    public SummaryCharges(String id, String nEACode, String localCode, String acctgCode, String description, double price, int quantity, int issued, int received, int returned, int balance) {
        this.id = id;
        this.nEACode = nEACode;
        this.localCode = localCode;
        this.acctgCode = acctgCode;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.issued = issued;
        this.received = received;
        this.returned = returned;
        this.balance = balance;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getnEACode() {
        return nEACode;
    }

    public void setnEACode(String nEACode) {
        this.nEACode = nEACode;
    }

    public String getLocalCode() {
        return localCode;
    }

    public void setLocalCode(String localCode) {
        this.localCode = localCode;
    }

    public String getAcctgCode() {
        return acctgCode;
    }

    public void setAcctgCode(String acctgCode) {
        this.acctgCode = acctgCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getIssued() {
        return issued;
    }

    public void setIssued(int issued) {
        this.issued = issued;
    }

    public int getReceived() {
        return received;
    }

    public void setReceived(int received) {
        this.received = received;
    }

    public int getReturned() {
        return returned;
    }

    public void setReturned(int returned) {
        this.returned = returned;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
