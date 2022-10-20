package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class BillDAO {
    /**
     * Insert as PaidDetails
     * @param bill The bill details
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void addPaidBill(Bill bill) throws Exception{
        String sql = " INSERT INTO PaidBills (" +
                "AccountNumber, " +
                "BillNumber, " +
                "ServicePeriodEnd, " +
                "Power, " +
                "Meter, " +
                "PR, " +
                "Others, " +
                "NetAmount, " +
                "PaymentType, " +
                "Teller, " +
                "PromptPayment, " +
                "Surcharge, " +
                "SLAdjustment, " +
                "OtherDeduction, " +
                "MDRefund, " +
                "Form2306, " +
                "Form2307, " +
                "Amount2306, " +
                "Amount2307, " +
                "CashAmount, " +
                "CheckAmount, " +
                "Bank, " +
                "CheckNumber, " +
                "CheckExpiry) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        PaidBill paid = (PaidBill) bill;
        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);
        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setString(2, bill.getBillNo());
        ps.setDate(3, Date.valueOf(bill.getServicePeriodEnd()));
        ps.setDouble(4, bill.getPowerAmount());
        ps.setDouble(5, bill.getVat());
        ps.setDouble(6, bill.getTransformerRental());
        ps.setDouble(7, bill.getOtherCharges());
        ps.setDouble(8, bill.getTotalAmount());
        ps.setString(9, paid.getPaymentType());
        ps.setString(10, paid.getTeller());
        ps.setDouble(11, bill.getDiscount());
        ps.setDouble(12, bill.getSurCharge());
        ps.setDouble(13, bill.getSlAdjustment());
        ps.setDouble(14, bill.getOtherAdjustment());
        ps.setDouble(15, bill.getMdRefund());
        ps.setString(16, bill.getForm2306());
        ps.setString(17, bill.getForm2307());
        ps.setDouble(18, bill.getCh2306());
        ps.setDouble(19, bill.getCh2307());
        ps.setDouble(20, paid.getCashAmount());
        ps.setDouble(21, paid.getCheckAmount());
        ps.setString(22, paid.getBank());
        ps.setDouble(23, paid.getCheckAmount());
        ps.setDate(24, paid.getCheckExpiry());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Retrieves bills of customer based on Account Number (on Billing database)
     * @param consumerInfo The consumer account number
     * @param paid The bill status
     * @return A list of Bill
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Bill> getConsumerBills(ConsumerInfo consumerInfo, boolean paid) throws Exception {
        String sql = "SELECT " +
                "BillNumber, AccountNumber, ServicePeriodEnd, ServiceDateFrom, ServiceDateTo, DueDate, " +
                "(SELECT PowerNew FROM BillsForDCRRevision WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ?) AS PowerNew, " +
                "(SELECT KatasAmt FROM BillsForDCRRevision WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ?) AS KatasAmt, " +
                "DATEDIFF(day, DueDate, getdate()) AS daysDelayed, ISNULL(NetAmount,0) AS NetAmount, ISNULL(ConsumerType,'RM') AS ConsumerType, ISNULL(PowerKWH,0) AS PowerKWH, " +
                "ISNULL(Item2, 0) AS VATandTaxes, ISNULL(PR,0) AS TransformerRental, ISNULL(Others,0) AS OthersCharges, ISNULL(ACRM_TAFPPCA,0) AS ACRM_TAFPPCA, ISNULL(ACRM_TAFPPCA,0) AS ACRM_TAFPPCA, " +
                "ISNULL(DAA_GRAM,0) AS DAA_GRAM " +
                "FROM Bills " +
                "WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? " +
                "ORDER BY DueDate DESC";

        if (paid)
            sql = "SELECT * FROM BillsForDCRRevision WHERE BillNumber IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, consumerInfo.getAccountID());
        ps.setString(2, consumerInfo.getAccountID());
        ps.setString(3, consumerInfo.getAccountID());
        ps.setString(3, consumerInfo.getAccountID());

        ResultSet rs = ps.executeQuery();

        List<Bill> bills = new ArrayList<>();

        while(rs.next()) {
            String billNo = rs.getString("BillNumber");
            Bill bill = new Bill(
                    billNo,
                    rs.getDate("ServiceDateFrom").toLocalDate(),
                    rs.getDate("ServiceDateTo").toLocalDate(),
                    rs.getDate("DueDate").toLocalDate(),
                    rs.getDouble("NetAmount"));

            Date date = rs.getDate("ServicePeriodEnd");
            LocalDate billMonth = date.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
            bill.setBillMonth(formatter.format(billMonth));
            bill.setServicePeriodEnd(billMonth);
            bill.setConsumer(consumerInfo);
            bill.setPowerAmount(rs.getDouble("PowerNew"));
            bill.setKatas(rs.getDouble("KatasAmt"));
            int daysDelayed = rs.getInt("daysDelayed");
            double pkwh = rs.getDouble("PowerKWH");
            double vaTandTaxes = rs.getDouble("VATandTaxes");
            double transformerRental = rs.getDouble("TransformerRental");
            double othersCharges = rs.getDouble("OthersCharges");
            double acrm = rs.getDouble("ACRM_TAFPPCA");
            double daa = rs.getDouble("DAA_GRAM");
            bill.setConsumerType(rs.getString("ConsumerType"));
            bill.setPowerKWH(pkwh);
            bill.setTransformerRental(transformerRental);
            bill.setOtherCharges(othersCharges);
            bill.setAcrmVat(acrm);
            bill.setDAAVat(daa);
            bill.setVat(vaTandTaxes);
            bill.setDaysDelayed(daysDelayed);
            bill.setSurCharge(bill.computeSurCharge());
            bill.setSurChargeTax(bill.getSurCharge()*0.12);
            bill.computeTotalAmount();

            double amount = getMDRefund(bill);
            bill.setMdRefund(amount);
            bills.add(bill);
        }

        rs.close();
        ps.close();

        return bills;
    }

    /**
     * Retrieves the discounted amount of a bill
     * @param bill The current bill
     * @return amount The discounted amount
     * @throws Exception obligatory from DB.getConnection()
     */
    public static double getDiscount(Bill bill) throws Exception {
        String sql = "Select NetAmountLessCharges from BillsForDCRRevision where AccountNumber=? and ServicePeriodEnd=?";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setDate(2, Date.valueOf(bill.getServicePeriodEnd()));

        ResultSet rs = ps.executeQuery();

        double amount = 0;

        while(rs.next()) {
            amount = rs.getDouble("NetAmountLessCharges");
        }

        rs.close();
        ps.close();
        System.out.println(amount);
        return amount*.01;
    }

    /**
     * Retrieves the 5% amount of a bill
     * @param bill The current bill
     * @return amount The amount
     * @throws Exception obligatory from DB.getConnection()
     */
    public static double getForm2306(Bill bill) throws Exception {
        String sql = "Select Form2306 from BillsForDCRRevision where AccountNumber=? and ServicePeriodEnd=?";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setDate(2, Date.valueOf(bill.getServicePeriodEnd()));

        ResultSet rs = ps.executeQuery();

        double amount = 0;

        while(rs.next()) {
            amount = rs.getDouble("Form2306");
        }

        rs.close();
        ps.close();

        return amount;
    }

    /**
     * Retrieves the 2% amount of a bill
     * @param bill The current bill
     * @return amount The amount
     * @throws Exception obligatory from DB.getConnection()
     */
    public static double getForm2307(Bill bill) throws Exception {
        String sql = "Select Form2307 from BillsForDCRRevision where AccountNumber=? and ServicePeriodEnd=?";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setDate(2, Date.valueOf(bill.getServicePeriodEnd()));

        ResultSet rs = ps.executeQuery();

        double amount = 0;

        while(rs.next()) {
            amount = rs.getDouble("Form2307");
        }

        rs.close();
        ps.close();

        return amount;
    }

    /**
     * Retrieves the remaining MDRefund of the bill
     * @param bill The current bill
     * @return amount The amount
     * @throws Exception obligatory from DB.getConnection()
     */
    public static double getMDRefund(Bill bill) throws Exception {
        String sql = "SELECT ISNULL (SUM(MDRefund), 0) AS total, " +
                "ISNULL((SELECT Amount FROM MDRefund WHERE AccountNumber = ?),0) AS remaining " +
                "FROM PaidBills WHERE AccountNumber = ?";
        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setString(2, bill.getConsumer().getAccountID());

        ResultSet rs = ps.executeQuery();

        double refund = 0;

        while(rs.next()) {
            refund = rs.getDouble("remaining") - rs.getDouble("total");
        }

        rs.close();
        ps.close();

        return refund;
    }
}
