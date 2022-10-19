package com.boheco1.dev.integratedaccountingsystem.objects;

public class CRMQueue {
    String id;
    String consumerName;
    String consumerAddress;
    String transactionPurpose;
    String source;
    String sourseId;
    double subTotal;
    double VAT;
    double total;

    public CRMQueue(){}

    public CRMQueue(String id, String consumerName, String consumerAddress, String transactionPurpose, String source, String sourseId, double subTotal, double VAT, double total) {
        this.id = id;
        this.consumerName = consumerName;
        this.consumerAddress = consumerAddress;
        this.transactionPurpose = transactionPurpose;
        this.source = source;
        this.sourseId = sourseId;
        this.subTotal = subTotal;
        this.VAT = VAT;
        this.total = total;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getTransactionPurpose() {
        return transactionPurpose;
    }

    public void setTransactionPurpose(String transactionPurpose) {
        this.transactionPurpose = transactionPurpose;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSourseId() {
        return sourseId;
    }

    public void setSourseId(String sourseId) {
        this.sourseId = sourseId;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getVAT() {
        return VAT;
    }

    public void setVAT(double VAT) {
        this.VAT = VAT;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "CRMQueue{" +
                "id='" + id + '\'' +
                ", consumerName='" + consumerName + '\'' +
                ", consumerAddress='" + consumerAddress + '\'' +
                ", transactionPurpose='" + transactionPurpose + '\'' +
                ", source='" + source + '\'' +
                ", sourseId='" + sourseId + '\'' +
                ", subTotal=" + subTotal +
                ", VAT=" + VAT +
                ", total=" + total +
                '}';
    }
}
