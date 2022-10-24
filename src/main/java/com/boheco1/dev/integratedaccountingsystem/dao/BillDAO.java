package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.Check;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;

import java.sql.*;
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
        Connection conn = DB.getConnection("Billing");
        conn.setAutoCommit(false);

        String sql = "INSERT INTO PaidBills (" +
                "AccountNumber, " +
                "BillNumber, " +
                "ServicePeriodEnd, " +
                "Power, " +
                "Meter, " +
                "PR, " +
                "Others, " +
                "NetAmount, " +
                "PaymentType, " +
                "PostingSequence, "+
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
                "CheckAmount) " +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ROUND(?, 2), ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2));";
        String sql_check = "INSERT INTO CheckPayment (AccountNumber, ServicePeriodEnd, Bank, CheckNumber, Amount) VALUES(?, ?, ?, ?, ROUND(?, 2))";
        String sql_md = "UPDATE MDRefund SET Amount=ROUND(Amount-?, 2) WHERE AccountNumber=?";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);
        PreparedStatement ps_check = null;
        PreparedStatement ps_md = null;

        try {
            PaidBill paid = (PaidBill) bill;

            ps.setString(1, bill.getConsumer().getAccountID());
            ps.setString(2, bill.getBillNo());
            ps.setDate(3, Date.valueOf(bill.getServicePeriodEnd()));
            ps.setDouble(4, bill.getPowerAmount());
            ps.setDouble(5, bill.getVat());
            ps.setDouble(6, bill.getTransformerRental());
            ps.setDouble(7, bill.getOtherCharges());
            ps.setDouble(8, bill.getTotalAmount());
            ps.setString(9, paid.getPaymentType());
            ps.setInt(10, paid.getPostingSequence());
            ps.setString(11, paid.getTeller());
            ps.setDouble(12, bill.getDiscount());
            ps.setDouble(13, bill.getSurCharge());
            ps.setDouble(14, bill.getSlAdjustment());
            ps.setDouble(15, bill.getOtherAdjustment());
            ps.setDouble(16, bill.getMdRefund());
            ps.setString(17, bill.getForm2306());
            ps.setString(18, bill.getForm2307());
            ps.setDouble(19, bill.getCh2306());
            ps.setDouble(20, bill.getCh2307());
            ps.setDouble(21, paid.getCashAmount());
            ps.setDouble(22, paid.getCheckAmount());

            ps.executeUpdate();

            //Insert check payments
            for (Check c: paid.getChecks()){
                ps_check = DB.getConnection("Billing").prepareStatement(sql_check);
                ps_check.setString(1, paid.getConsumer().getAccountID());
                ps_check.setDate(2, Date.valueOf(paid.getServicePeriodEnd()));
                ps_check.setString(3, c.getBank());
                ps_check.setString(4, c.getCheckNo());
                ps_check.setDouble(5, c.getAmount());
                ps_check.executeUpdate();
            }

            //Update MDRefund amount in the MDRefund table
            if (bill.getMdRefund() > 0) {
                ps_md = DB.getConnection("Billing").prepareStatement(sql_md);
                ps_md.setDouble(1, bill.getMdRefund());
                ps_md.setString(2, bill.getConsumer().getAccountID());
                ps_md.executeUpdate();
            }
            conn.commit();
        }catch (SQLException ex){
            conn.rollback();
            throw new Exception(ex.getMessage());
        }

        ps.close();
        if (ps_check != null) ps_check.close();
        if (ps_md != null) ps_md.close();
        conn.setAutoCommit(true);
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
                "(SELECT PowerNew FROM BillsForDCRRevision a WHERE b.BillNumber = a.BillNumber) AS PowerNew, " +
                "(SELECT KatasAmt FROM BillsForDCRRevision c WHERE b.BillNumber = c.BillNumber) AS KatasAmt, " +
                "DATEDIFF(day, DueDate, getdate()) AS daysDelayed, ISNULL(NetAmount,0) AS NetAmount, ISNULL(ConsumerType,'RM') AS ConsumerType, ISNULL(PowerKWH,0) AS PowerKWH, " +
                "ISNULL(Item2, 0) AS VATandTaxes, ISNULL(PR,0) AS TransformerRental, ISNULL(Others,0) AS OthersCharges, ISNULL(ACRM_TAFPPCA,0) AS ACRM_TAFPPCA, ISNULL(DAA_GRAM,0) AS DAA_GRAM " +
                "FROM Bills b " +
                "WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? " +
                "ORDER BY DueDate DESC";

        if (paid)
            sql = "SELECT * FROM BillsForDCRRevision WHERE BillNumber IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, consumerInfo.getAccountID());

        ResultSet rs = ps.executeQuery();

        List<Bill> bills = new ArrayList<>();
        int i = 0;
        while(rs.next()) {
            String billNo = rs.getString("BillNumber");
            Bill bill = new PaidBill(
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
            PaidBill paidBill = (PaidBill) bill;
            paidBill.setPostingSequence(i);
            i++;
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
    /**
     * Retrieves paid bills from a specified date
     * @param year The posting year
     * @param month The posting month
     * @param day The posting day
     * @return list The list of paid bills
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Bill> getAllPaidBills(int year, int month, int day) throws Exception {
        String sql = "SELECT p.accountnumber, p.billnumber, power, p.meter, p.pr, p.others, p.netamount as amountpaid, PaymentType, ornumber, teller, dcrnumber, postingdate, " +
                "postingsequence, PromptPayment, surcharge, SLAdjustment, otherdeduction, " +
                "Amount2306, Amount2307, MDRefund, FORMAT(PostingDate,'hh:mm') as postingtime, " +
                "b.NetAmount as amountdue, ConsumerName, ConsumerAddress, TINNo, ContactNumber, Email, b.ConsumerType " +
                "FROM paidbills p INNER JOIN accountmaster a ON p.AccountNumber=a.AccountNumber INNER JOIN Bills b ON b.AccountNumber=a.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd " +
                "WHERE PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59' ORDER BY PostingDate ASC;";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ResultSet rs = ps.executeQuery();

        List<Bill> bills = new ArrayList<>();

        while(rs.next()) {
            String accountNo = rs.getString("AccountNumber");

            ConsumerInfo consumerInfo = new ConsumerInfo(
                    accountNo,
                    rs.getString("ConsumerName"),
                    rs.getString("ConsumerAddress"),
                    rs.getString("TINNo"),
                    rs.getString("Email"),
                    rs.getString("ContactNumber")
                    );

            PaidBill bill = new PaidBill();
            bill.setConsumerType(rs.getString("ConsumerType"));
            bill.setConsumer(consumerInfo);
            bill.setBillNo(rs.getString("BillNumber"));
            bill.setAmountDue(rs.getDouble("amountdue"));
            bill.setTotalAmount(rs.getDouble("amountpaid"));
            bill.setPower(rs.getDouble("power"));
            bill.setMeter(rs.getDouble("meter"));
            bill.setPr(rs.getDouble("PR"));
            bill.setOthers(rs.getDouble("Others"));
            bill.setPromptPayment(rs.getDouble("PromptPayment"));
            Bill b = bill;
            b.setMdRefund(rs.getDouble("MDRefund"));
            b.setSurCharge(rs.getDouble("surcharge"));
            bill.setSLAdjustment(rs.getDouble("SLAdjustment"));
            bill.setOtherDeduction(rs.getDouble("otherdeduction"));
            bill.setAmount2306(rs.getDouble("Amount2306"));
            bill.setAmount2307(rs.getDouble("Amount2307"));
            bill.setPostingDate(rs.getDate("postingdate"));
            bill.setPostingTime(rs.getString("postingtime"));
            bills.add(bill);
        }

        rs.close();
        ps.close();

        return bills;
    }
}
