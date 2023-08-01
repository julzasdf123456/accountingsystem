package com.boheco1.dev.integratedaccountingsystem.objects;

public class Salary extends COBItem{
    private String salId;
    private double longetivity;
    private double sSSPhilH;
	private double CashGift;
	private double Bonus13;
    public static String PER_DAY = "Per Day";
    public static String SG = "SG";

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

    public double getTotalLongetivity(){
        return getLongetivity() * getQty();
    }

    public double getAnnualLongetivity(){
        return getLongetivity() * getQty() * 12;
    }

    public void setLongetivity(double longetivity) {
        this.longetivity = longetivity;
    }

    public double getsSSPhilH() {
        return sSSPhilH;
    }

    public double getTotalSSS(){
        return getsSSPhilH() * getQty();
    }

    public double getAnnualSSS(){
        return getsSSPhilH() * getQty() * 12;
    }

    public void setsSSPhilH(double sSSPhilH) {
        this.sSSPhilH = sSSPhilH;
    }

    public double getCashGift() {
        return CashGift;
    }

    public double getTotalCashGift(){
        return getCashGift() * getQty();
    }

    public void setCashGift(double cashGift) {
        CashGift = cashGift;
    }

    public double getBonus13() {
        return Bonus13;
    }

    public double getTotalBonus(){
        return getBonus13() * getQty();
    }

    public void setBonus13(double bonus13) {
        Bonus13 = bonus13;
    }

    public double getTotalSalary(){
        return getCost() * getQty();
    }

    public double getAnnualTotal(){
        return getCost() * 12 * getQty();
    }

    public double getOvertime(){
        //5% of Annual Total Basic Salary
        return 0.05 * getAnnualTotal();
    }

    @Override
    public double getAmount(){
        return getAnnualTotal()
                + getAnnualSSS()
                + getAnnualLongetivity()
                + getTotalSSS()
                + getTotalBonus()
                + getTotalCashGift()
                + getTotalLongetivity()
                + getOvertime();
    }
}
