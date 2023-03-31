package com.boheco1.dev.integratedaccountingsystem.objects;

import java.util.ArrayList;
import java.util.List;

public class BatchTransactionInfo {
    private TransactionHeader transactionHeader;
    private List<TransactionDetails> transactionDetailsList;


    public BatchTransactionInfo() {
        transactionDetailsList = new ArrayList<>();
    }

    public void add(TransactionDetails transactionDetails){
        transactionDetailsList.add(transactionDetails);
    }

    public TransactionHeader getTransactionHeader() {
        return transactionHeader;
    }

    public void setTransactionHeader(TransactionHeader transactionHeader) {
        this.transactionHeader = transactionHeader;
    }

    public List<TransactionDetails> getTransactionDetailsList() {
        return transactionDetailsList;
    }

}
