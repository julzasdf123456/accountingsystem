package com.boheco1.dev.integratedaccountingsystem.objects;

public class SupplierInfo {
    private String supplierID;
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
    private String taxType;//VAT, Non-VAT
    private String supplierNature; //1. Manufacturing 2. Utilities(Electricity, Water, Gas, Etc.) 3. Construction 4. Transportation & Storage 5. Accommodation & Food Services Activities 6. Wholesale/Retail Trade 7. IT & Communication 8. Consultancy/Professional/Management Services 9. Education 10. Rental 11.Other Service Activities
    private String notes;
    private String status;

    public SupplierInfo(
            String supplierID,
            String accountID,
            String companyName,
            String companyAddress,
            String TINNo,
            String contactPerson,
            String zipCode,
            String phoneNo,
            String mobileNo,
            String emailAddress,
            String faxNo,
            String taxType,
            String supplierNature,
            String notes,
            String status) {
        this.supplierID=supplierID;
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
        this.supplierNature=supplierNature;
        this.notes=notes;
        this.status=status;
    }

    public String getSupplierNature() {
        return supplierNature;
    }

    public void setSupplierNature(String supplierNature) {
        this.supplierNature = supplierNature;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public String getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(String supplierID) {
        this.supplierID = supplierID;
    }
}
