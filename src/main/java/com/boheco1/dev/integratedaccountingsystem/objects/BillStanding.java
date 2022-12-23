package com.boheco1.dev.integratedaccountingsystem.objects;

import java.time.LocalDate;

public class BillStanding extends PaidBill{

    private String status;

    public BillStanding(){}

    public BillStanding(String billNo, LocalDate from, LocalDate to, LocalDate dueDate, double amountDue){
        super(billNo, from, to, dueDate, amountDue);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
