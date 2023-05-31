package com.boheco1.dev.integratedaccountingsystem.objects;

public class TransactionDefinition {
    private String transactionCode;
    private String description;

    public TransactionDefinition(String transactionCode, String description) {
        this.transactionCode = transactionCode;
        this.description = description;
    }

    public String getTransactionCode() {
        return transactionCode;
    }

    public void setTransactionCode(String transactionCode) {
        this.transactionCode = transactionCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String toString(){
        return transactionCode;
    }
}
