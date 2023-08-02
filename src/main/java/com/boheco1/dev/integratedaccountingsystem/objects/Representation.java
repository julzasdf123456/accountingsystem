package com.boheco1.dev.integratedaccountingsystem.objects;

public class Representation extends COBItem{
    private int RId;
    private double representationAllowance = 0;
    private double reimbursableAllowance = 0;
    private double otherAllowance = 0;

    public Representation(){}

    public int getRId() {
        return RId;
    }

    public void setRId(int RId) {
        this.RId = RId;
    }

    public double getReimbursableAllowance() {
        return reimbursableAllowance;
    }

    public void setReimbursableAllowance(double reimbursableAllowance) {
        this.reimbursableAllowance = reimbursableAllowance;
    }

    public double getOtherAllowance() {
        return otherAllowance;
    }

    public void setOtherAllowance(double otherAllowance) {
        this.otherAllowance = otherAllowance;
    }

    public double getRepresentationAllowance() {
        return representationAllowance;
    }

    public void setRepresentationAllowance(double representationAllowance) {
        this.representationAllowance = representationAllowance;
    }
}
