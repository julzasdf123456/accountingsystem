package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
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
    public static List<Bill> getAllPaidBills(int year, int month, int day, String teller) throws Exception {

        String sql = "SELECT p.accountnumber, p.billnumber, power, p.meter, p.pr, p.others, p.netamount as amountpaid, PaymentType, ornumber, teller, dcrnumber, postingdate, cashAmount, checkAmount, " +
                "postingsequence, PromptPayment, surcharge, SLAdjustment, otherdeduction, ISNULL(SeniorCitizenDiscount,0) AS SeniorCitizenDiscount, ISNULL(item7,0) AS KatasNgVAT, ISNULL(Item16,0) AS Item16, ISNULL(Item17,0) AS Item17, " +
                "ISNULL(b.Item1,0) AS GenVatFeb21, ISNULL(DAA_VAT,0) AS DAA_VAT, ISNULL(ACRM_VAT, 0) AS ACRM_VAT, ISNULL(SLVAT, 0) AS SLVAT, ISNULL(GenerationVAT,0) AS GenerationVAT, ISNULL(TransmissionVAT, 0) AS TransmissionVAT, " +
                "Amount2306, Amount2307, MDRefund, FORMAT(PostingDate,'hh:mm') as postingtime, ISNULL(PowerKWH,0) AS PowerKWH, ISNULL(FBHCAmt,0) AS FBHCAmt, ISNULL(Item2, 0) AS VATandTaxes, ISNULL(Item3, 0) AS Item3, ISNULL(Item4, 0) AS Item4, " +
                "b.NetAmount as amountdue, ConsumerName, ConsumerAddress, TINNo, ContactNumber, Email, b.ConsumerType " +
                "FROM paidbills p INNER JOIN accountmaster a ON p.AccountNumber=a.AccountNumber INNER JOIN Bills b ON b.AccountNumber=a.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd " +
                "INNER JOIN BillsExtension c ON b.AccountNumber=c.AccountNumber AND b.ServicePeriodEnd=c.ServicePeriodEnd "+
                "WHERE PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59' ORDER BY PostingDate ASC;";

        if (teller != null)
            sql = "SELECT p.accountnumber, p.billnumber, power, p.meter, p.pr, p.others, p.netamount as amountpaid, PaymentType, ornumber, teller, dcrnumber, postingdate, cashAmount, checkAmount, " +
                    "postingsequence, PromptPayment, surcharge, SLAdjustment, otherdeduction, ISNULL(SeniorCitizenDiscount,0) AS SeniorCitizenDiscount, ISNULL(item7,0) AS KatasNgVAT, ISNULL(Item16,0) AS Item16, ISNULL(Item17,0) AS Item17, " +
                    "ISNULL(b.Item1,0) AS GenVatFeb21, ISNULL(DAA_VAT,0) AS DAA_VAT, ISNULL(ACRM_VAT, 0) AS ACRM_VAT, ISNULL(SLVAT, 0) AS SLVAT, ISNULL(GenerationVAT,0) AS GenerationVAT, ISNULL(TransmissionVAT, 0) AS TransmissionVAT, " +
                    "Amount2306, Amount2307, MDRefund, FORMAT(PostingDate,'hh:mm') as postingtime, ISNULL(PowerKWH,0) AS PowerKWH, ISNULL(FBHCAmt,0) AS FBHCAmt, ISNULL(Item2, 0) AS VATandTaxes, ISNULL(Item3, 0) AS Item3, ISNULL(Item4, 0) AS Item4, " +
                    "b.NetAmount as amountdue, ConsumerName, ConsumerAddress, TINNo, ContactNumber, Email, b.ConsumerType " +
                    "FROM paidbills p INNER JOIN accountmaster a ON p.AccountNumber=a.AccountNumber INNER JOIN Bills b ON b.AccountNumber=a.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd " +
                    "INNER JOIN BillsExtension c ON b.AccountNumber=c.AccountNumber AND b.ServicePeriodEnd=c.ServicePeriodEnd "+
                    "WHERE PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59' AND Teller = ? ORDER BY PostingDate ASC;";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        if (teller != null)
            ps.setString(1, teller);

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
            b.setPowerKWH(rs.getDouble("PowerKWH"));
            b.setGenerationVat(rs.getDouble("GenerationVAT"));
            b.setScDiscount(rs.getDouble("SeniorCitizenDiscount"));
            b.setKatasNgVat(rs.getDouble("KatasNgVAT"));
            b.setTransmissionVat(rs.getDouble("TransmissionVAT"));
            b.setDAAVat(rs.getDouble("DAA_VAT"));
            b.setAcrmVat(rs.getDouble("ACRM_VAT"));
            b.setSystemLossVat(rs.getDouble("SLVAT"));
            b.setGenVatFeb21(rs.getDouble("GenVatFeb21"));
            b.setFbhcAmt(rs.getDouble("FBHCAmt"));
            b.setItem2(rs.getDouble("VATandTaxes"));
            b.setItem3(rs.getDouble("Item3"));
            b.setItem4(rs.getDouble("Item4"));
            b.setItem16(rs.getDouble("Item16"));
            b.setItem17(rs.getDouble("Item17"));
            b.setArGen(b.getGenerationVat()+b.getSystemLossVat()+b.getDAAVat()+b.getAcrmVat()+b.getGenVatFeb21());
            b.setArTran(b.getTransmissionVat());
            b.setMdRefund(rs.getDouble("MDRefund"));
            b.setSurCharge(rs.getDouble("surcharge"));
            bill.setSLAdjustment(rs.getDouble("SLAdjustment"));
            bill.setOtherDeduction(rs.getDouble("otherdeduction"));
            bill.setAmount2306(rs.getDouble("Amount2306"));
            bill.setAmount2307(rs.getDouble("Amount2307"));
            bill.setPostingDate(rs.getDate("postingdate"));
            bill.setPostingTime(rs.getString("postingtime"));
            bill.setCashAmount(rs.getDouble("cashAmount") == 0 ? rs.getDouble("amountpaid") : rs.getDouble("cashAmount"));
            bill.setCheckAmount(rs.getDouble("checkAmount"));
            bills.add(bill);
        }

        rs.close();
        ps.close();

        return bills;
    }

    /**
     * Retrieves paid bills from a specified date
     * @param year The posting year
     * @param month The posting month
     * @param day The posting day
     * @return list The list of paid bills
     * @throws Exception obligatory from DB.getConnection()
     */
    public static HashMap<String, List<ItemSummary>> getDCRBreakDown(int year, int month, int day, String teller) throws Exception{
        String sql = "SELECT "+
            "SUM(p.power) AS Energy, "+
            "SUM(p.meter) AS Meter, "+
            "SUM(p.pr) AS TransformerRental, "+
            "SUM(p.others) AS Others, "+
            "SUM(p.PromptPayment) AS PPD, "+
            "SUM(p.surcharge) AS Surcharge, "+
            "(SUM(ISNULL(b.Item2, 0)) + (SUM(p.surcharge) * 0.12) - SUM(ISNULL(b.FBHCAmt, 0)) - SUM(ISNULL(x.Item17, 0)) - SUM(ISNULL(x.Item16, 0)) - SUM(ISNULL(x.TransmissionVAT, 0)) - " +
            "(SUM(ISNULL(x.GenerationVAT,0)) + SUM(ISNULL(x.SLVAT, 0)) + SUM(ISNULL(b.DAA_VAT, 0)) + SUM(ISNULL(b.ACRM_VAT, 0)) + SUM(ISNULL(b.Item1, 0)))) AS Evat, "+
            "SUM(p.SLAdjustment) AS SLAdj, "+
            "SUM(p.otherdeduction) AS OtherDeduction, "+
            "SUM(ISNULL(p.Amount2306, 0)) AS Amount2306, "+
            "SUM(ISNULL(p.Amount2307, 0)) AS Amount2307, "+
            "SUM(ISNULL(p.MDRefund, 0)) AS MDRefund, "+
            "SUM(ISNULL(b.PowerKWH, 0)) AS PowerKWH, "+
            "SUM(p.netamount) AS AmountPaid, "+
            "SUM(ISNULL(p.cashAmount, 0)) AS CashAmount, "+
            "SUM(ISNULL(p.checkAmount, 0)) AS CheckAmount, "+
            "SUM(ISNULL(b.SeniorCitizenDiscount,0)) AS SeniorCitizenDiscount, "+
            "SUM(b.NetAmount) AS AmountDue, "+
            "SUM(ISNULL(x.TransmissionVAT, 0)) AS ARVATTrans, "+
            "(SUM(ISNULL(x.GenerationVAT,0)) + SUM(ISNULL(x.SLVAT, 0)) + SUM(ISNULL(b.DAA_VAT, 0)) + SUM(ISNULL(b.ACRM_VAT, 0)) + SUM(ISNULL(b.Item1, 0))) AS ARVATGen, "+
            "SUM(ISNULL(x.item7, 0)) AS KatasNgVAT "+
            "FROM paidbills p INNER JOIN accountmaster a ON p.AccountNumber=a.AccountNumber INNER JOIN Bills b ON b.AccountNumber=a.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd "+
            "INNER JOIN BillsExtension x ON b.AccountNumber=x.AccountNumber AND b.ServicePeriodEnd=x.ServicePeriodEnd "+
            "WHERE PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59'";

        if (teller != null)
            sql += " AND TELLER = ?";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        if (teller != null)
            ps.setString(1, teller);

        ResultSet rs = ps.executeQuery();

        List<ItemSummary> dcrBreakdown = new ArrayList<>();
        List<ItemSummary> paymentBreakdown = new ArrayList<>();
        List<ItemSummary> misc = new ArrayList<>();

        while(rs.next()) {
            double kwh = rs.getDouble("PowerKWH");
            double energy = rs.getDouble("Energy");
            double tr = rs.getDouble("TransformerRental");
            double others = rs.getDouble("Others");
            double surcharge = rs.getDouble("Surcharge");

            double slAdj = rs.getDouble("SLAdj");
            double ppD = rs.getDouble("PPD");
            double katasNgVAT = rs.getDouble("KatasNgVAT");
            double otherDeduction = rs.getDouble("OtherDeduction");
            double mdRefund = rs.getDouble("MDRefund");
            double scDiscount = rs.getDouble("SeniorCitizenDiscount");
            double amount2307 = rs.getDouble("Amount2307");
            double amount2306 = rs.getDouble("Amount2306");

            double arTrans = rs.getDouble("ARVATTrans");
            double arGen = rs.getDouble("ARVATGen");

            double evat = rs.getDouble("evat");

            double total = (energy + tr + others + surcharge + evat) + (-slAdj - ppD - katasNgVAT - otherDeduction - mdRefund - scDiscount - amount2307 - amount2306) + arTrans + arGen;
            double totalPaid = rs.getDouble("AmountPaid");
            double amountDue = rs.getDouble("AmountDue");
            double totalCash = rs.getDouble("CashAmount") == 0 ? totalPaid : rs.getDouble("CashAmount");
            double totalCheck = rs.getDouble("CheckAmount");
            double totalPayments = totalCash + totalCheck;

            ItemSummary energyItem = new ItemSummary("Energy", energy);
            ItemSummary trItem = new ItemSummary("TSF/TR", tr);
            ItemSummary othersItem = new ItemSummary("Others", others);
            ItemSummary surChargeItem = new ItemSummary("Surcharge", surcharge);
            ItemSummary evatItem = new ItemSummary("Evat", evat);
            ItemSummary slAdjItem = new ItemSummary("S/L Adjustments", slAdj);
            ItemSummary ppdItem = new ItemSummary("PPD", ppD);
            ItemSummary katasvatItem = new ItemSummary("Katas Ng VAT", katasNgVAT);
            ItemSummary otherDeductionsItem = new ItemSummary("Other Deductions", otherDeduction);
            ItemSummary mdRefundItem = new ItemSummary("MD Refund", mdRefund);
            ItemSummary scDiscountItem = new ItemSummary("SC Discount", scDiscount);
            ItemSummary ch2307Item = new ItemSummary("2307 (2%)", amount2307);
            ItemSummary ch2306Item = new ItemSummary("2307 (5%)", amount2306);
            ItemSummary arVATTransItem = new ItemSummary("ARVAT - Trans", arTrans);
            ItemSummary arVATGenItem = new ItemSummary("ARVAT - Gen", arGen);
            ItemSummary totalItem = new ItemSummary("Grand Total", total);

            dcrBreakdown.add(energyItem);
            dcrBreakdown.add(trItem);
            dcrBreakdown.add(othersItem);
            dcrBreakdown.add(surChargeItem);
            dcrBreakdown.add(evatItem);
            dcrBreakdown.add(slAdjItem);
            dcrBreakdown.add(ppdItem);
            dcrBreakdown.add(katasvatItem);
            dcrBreakdown.add(otherDeductionsItem);
            dcrBreakdown.add(mdRefundItem);
            dcrBreakdown.add(scDiscountItem);
            dcrBreakdown.add(ch2307Item);
            dcrBreakdown.add(ch2306Item);
            dcrBreakdown.add(arVATTransItem);
            dcrBreakdown.add(arVATGenItem);

            ItemSummary kwhItem = new ItemSummary("KWH", kwh);
            ItemSummary totalDueItem = new ItemSummary("Amount Due", amountDue);
            ItemSummary totalPaidBillsItem = new ItemSummary("Total Payments", totalPaid);

            misc.add(kwhItem);
            misc.add(totalItem);
            misc.add(totalPaidBillsItem);
            misc.add(totalDueItem);

            ItemSummary cashPaymentsItem = new ItemSummary("Cash", totalCash);
            ItemSummary checkPaymentsItem = new ItemSummary("Check", totalCheck);
            ItemSummary totalPaymentsItem = new ItemSummary("Total", totalPayments);
            paymentBreakdown.add(cashPaymentsItem);
            paymentBreakdown.add(checkPaymentsItem);
            paymentBreakdown.add(totalPaymentsItem);
        }

        HashMap<String, List<ItemSummary>> results = new HashMap<>();
        results.put("Breakdown", dcrBreakdown);
        results.put("Payments", paymentBreakdown);
        results.put("Misc", misc);

        rs.close();
        ps.close();

        return results;
    }
}
