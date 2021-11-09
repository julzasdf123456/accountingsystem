package com.boheco1.dev.integratedaccountingsystem.objects;

public class ConsumerInfo {
    private String accountID;
    private String consumerName;
    private String consumerAddress;
    private String TINNo;
    private String emailAdd;
    private String contactNo;

    public ConsumerInfo(String accountID, String consumerName, String consumerAddress, String TINNo, String emailAdd, String contactNo) {
        this.accountID = accountID;
        this.consumerName = consumerName;
        this.consumerAddress = consumerAddress;
        this.TINNo = TINNo;
        this.emailAdd = emailAdd;
        this.contactNo = contactNo;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getConsumerName() {
        return consumerName;
    }

    public void setConsumerName(String consumerName) {
        this.consumerName = consumerName;
    }

    public String getConsumerAddress() {
        return consumerAddress;
    }

    public void setConsumerAddress(String consumerAddress) {
        this.consumerAddress = consumerAddress;
    }

    public String getTINNo() {
        return TINNo;
    }

    public void setTINNo(String TINNo) {
        this.TINNo = TINNo;
    }

    public String getEmailAdd() {
        return emailAdd;
    }

    public void setEmailAdd(String emailAdd) {
        this.emailAdd = emailAdd;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }
}
