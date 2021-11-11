package com.boheco1.dev.integratedaccountingsystem.objects;

public class BankAccount {
    private String bankAccountNumber;
    private String bankDescription;
    private String accountCode;

    public BankAccount(String bankAccountNumber, String bankDescription, String accountCode) {
        this.bankAccountNumber = bankAccountNumber;
        this.bankDescription = bankDescription;
        this.accountCode = accountCode;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public void setBankAccountNumber(String bankAccountNumber) {
        this.bankAccountNumber = bankAccountNumber;
    }

    public String getBankDescription() {
        return bankDescription;
    }

    public void setBankDescription(String bankDescription) {
        this.bankDescription = bankDescription;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public void setAccountCode(String accountCode) {
        this.accountCode = accountCode;
    }
}
