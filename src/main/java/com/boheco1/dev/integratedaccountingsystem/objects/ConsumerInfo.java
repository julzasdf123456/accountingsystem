package com.boheco1.dev.integratedaccountingsystem.objects;

public class ConsumerInfo {
    private String accountID;
    private String consumerName;
    private String consumerAddress;
    private String TINNo;
    private String emailAdd;
    private String contactNo;
    private String meterNumber;
    private String accountType;
    private String accountStatus;

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

    public String getMeterNumber() {
        return meterNumber;
    }

    public void setMeterNumber(String meterNumber) {
        this.meterNumber = meterNumber;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        if (accountType.equals("R")) {
            this.accountType = "Residential";
        }else if (accountType.equals("B")) {
            this.accountType = "BAPA";
        }else if (accountType.equals("E")) {
            this.accountType = "ECA";
        }else if (accountType.equals("P")) {
            this.accountType = "Public Building";
        }
        else if (accountType.equals("S")) {
            this.accountType = "Street Lights";
        }

        else{
            this.accountType = "Commercial";
        }
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
