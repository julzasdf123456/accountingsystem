package com.boheco1.dev.integratedaccountingsystem.objects;

public class SupplierInfo {
    private String accountID;
    private String companyName;
    private String companyAddress;
    private String TINNo;
    private String contactPerson;
    private String zipCode;
    private String phoneNo;
    private String mobileNo;
    private String emailAddress;
    private String faxNo;
    private String taxType;

    public SupplierInfo(String accountID, String companyName, String companyAddress, String TINNo, String contactPerson, String zipCode, String phoneNo, String mobileNo, String emailAddress, String faxNo, String taxType) {
        this.accountID = accountID;
        this.companyName = companyName;
        this.companyAddress = companyAddress;
        this.TINNo = TINNo;
        this.contactPerson = contactPerson;
        this.zipCode = zipCode;
        this.phoneNo = phoneNo;
        this.mobileNo = mobileNo;
        this.emailAddress = emailAddress;
        this.faxNo = faxNo;
        this.taxType = taxType;
    }

    public String getAccountID() {
        return accountID;
    }

    public void setAccountID(String accountID) {
        this.accountID = accountID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getTINNo() {
        return TINNo;
    }

    public void setTINNo(String TINNo) {
        this.TINNo = TINNo;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFaxNo() {
        return faxNo;
    }

    public void setFaxNo(String faxNo) {
        this.faxNo = faxNo;
    }

    public String getTaxType() {
        return taxType;
    }

    public void setTaxType(String taxType) {
        this.taxType = taxType;
    }
}
