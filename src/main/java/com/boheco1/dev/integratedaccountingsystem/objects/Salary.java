package com.boheco1.dev.integratedaccountingsystem.objects;

public class Salary extends COBItem{
    private String salId;
    private double longetivity;
    private double sSSPhilH;
	private double CashGift;
	private double Bonus13;
    public Salary(){}

    public String getSalId() {
        return salId;
    }

    public void setSalId(String salId) {
        this.salId = salId;
    }

    public double getLongetivity() {
        return longetivity;
    }

    public void setLongetivity(double longetivity) {
        this.longetivity = longetivity;
    }

    public double getsSSPhilH() {
        return sSSPhilH;
    }

    public void setsSSPhilH(double sSSPhilH) {
        this.sSSPhilH = sSSPhilH;
    }

    public double getCashGift() {
        return CashGift;
    }

    public void setCashGift(double cashGift) {
        CashGift = cashGift;
    }

    public double getBonus13() {
        return Bonus13;
    }

    public void setBonus13(double bonus13) {
        Bonus13 = bonus13;
    }

    public double getAnnualTotal(){
        //Basic Salary * 12 months
        return getCost() * 12;
    }
    public double getOvertime(){
        //5% of Annual Total Basic Salary
        return 0.05 * getAnnualTotal();
    }
}
