package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
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
     * @param bills List of bills
     * @param change change to deposit
     * @param deposit deposit change
     * @param account paidbill account to deposit
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void addPaidBill(List<Bill> bills, double change, boolean deposit, PaidBill account) throws Exception{
        Connection conn = DB.getConnection(Utility.DB_BILLING);
        conn.setAutoCommit(false);

        String sql = "INSERT INTO PaidBills (" +
                "PostingDate, "+
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
                "DepositAmount, " +
                "CashAmount, " +
                "CheckAmount) " +
                " VALUES (SYSDATETIME(), ?, ?, ?, ?, ?, ?, ?, ROUND(?, 2), ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2));";

        String sql_check = "INSERT INTO CheckPayment (AccountNumber, ServicePeriodEnd, Bank, RefNo, Amount) VALUES(?, ?, ?, ?, ROUND(?, 2))";
        String sql_md = "UPDATE MDRefund SET Amount=ROUND(Amount-?, 2) WHERE AccountNumber=?";
        String sql_deposit = "INSERT INTO OtherCharges (AccountNumber, ";
        boolean depUpdate = false;
        String id = "";
        if (account != null)
            id = account.getConsumer().getAccountID();
        HashMap<String, Double> deposits = verifyDeposit(id);

        if (deposits == null){
            sql_deposit += "QCLoanAmount, QCMonths) VALUES (?, ?, ?)";
        }else{
            depUpdate = true;
            sql_deposit = "UPDATE OtherCharges SET ";
            if (deposits.get("QCLoanAmount") != 0) {
                if (deposits.get("QCAmount") != 0) {
                    if (deposits.get("EPAmount") != 0) {
                        sql_deposit += "PCAmount = ?, PCMonths = ? ";
                    }else{
                        sql_deposit += "EPAmount = ?, EPMonths = ? ";
                    }
                }else{
                    sql_deposit += "QCAmount = ?, QCMonths = ? ";
                }
            }else{
                sql_deposit += "QCLoanAmount = ?, QCMonths = ? ";
            }
            sql_deposit += "WHERE AccountNumber = ?";
        }
        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);
        PreparedStatement ps_check = null;
        PreparedStatement ps_md = null;
        PreparedStatement ps_dep = null;

        int postingSequence = 1;
        int count = 1;

        for (Bill bill : bills) {

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
                ps.setInt(10, postingSequence);
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

                if (deposit && paid.equals(account))
                    ps.setDouble(21, change);
                else
                    ps.setDouble(21, 0);

                ps.setDouble(22, paid.getCashAmount());
                ps.setDouble(23, paid.getCheckAmount());

                ps.executeUpdate();

                //Update MDRefund if it is credited in bill
                if (bill.getMdRefund() > 0) {
                    ps_md = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql_md);
                    ps_md.setDouble(1, bill.getMdRefund());
                    ps_md.setString(2, bill.getConsumer().getAccountID());
                    ps_md.executeUpdate();
                }

                //Insert check payments
                for (Check c : paid.getChecks()) {
                    ps_check = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql_check);
                    ps_check.setString(1, paid.getConsumer().getAccountID());
                    ps_check.setDate(2, Date.valueOf(paid.getServicePeriodEnd()));
                    ps_check.setString(3, c.getBank());
                    ps_check.setString(4, Utility.generateRandomId()); //Generate unique id as refno(pk)
                    ps_check.setDouble(5, c.getOriginalAmount());
                    ps_check.executeUpdate();
                }

                if (deposit && count == 1){
                    ps_dep = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql_deposit);
                    if (!depUpdate) {
                        ps_dep.setString(1, account.getConsumer().getAccountID());
                        ps_dep.setDouble(2, -change);
                        ps_dep.setInt(3, 1);
                    }else{
                        ps_dep.setDouble(1, -change);
                        ps_dep.setInt(2, 1);
                        ps_dep.setString(3, account.getConsumer().getAccountID());
                    }
                    ps_dep.executeUpdate();
                }

                //Update KatasBalance in KatasData?
                postingSequence += 1;
                count += 1;
            } catch (SQLException ex) {
                ex.printStackTrace();
                conn.rollback();
                throw new Exception(ex.getMessage());
            }
        }
        conn.commit();

        ps.close();
        if (ps_check != null) ps_check.close();
        if (ps_md != null) ps_md.close();
        if (ps_dep != null) ps_dep.close();
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
                "BillNumber, AccountNumber, ServicePeriodEnd, ServiceDateFrom, ServiceDateTo, DueDate, ISNULL(PR,0) AS PR, (SELECT computemode FROM AccountMaster WHERE AccountNumber = ?) AS ComputeMode, " +
                "ISNULL((SELECT PowerNew FROM BillsForDCRRevision a WHERE b.AccountNumber = a.AccountNumber AND b.ServicePeriodEnd = a.ServicePeriodEnd), 0) AS PowerNew, " +
                "ISNULL((SELECT KatasBalance FROM KatasData c WHERE b.AccountNumber = c.AccountNumber AND b.ServicePeriodEnd = c.ServicePeriodEnd), 0) AS KatasAmt, " +
                "DATEDIFF(day, DueDate, getdate()) AS daysDelayed, ISNULL(NetAmount,0) AS NetAmount, ISNULL(NetMeteringNetAmount, 0) AS NetMeterAmount, " +
                "ISNULL(ConsumerType,'RM') AS ConsumerType, ISNULL(PowerKWH,0) AS PowerKWH, ISNULL(withPenalty, 1) AS withPenalty, " +
                "ISNULL(Item2, 0) AS VATandTaxes, ISNULL(PR,0) AS TransformerRental, ISNULL(Others,0) AS OthersCharges, ISNULL(ACRM_TAFPPCA,0) AS ACRM_TAFPPCA, ISNULL(DAA_GRAM,0) AS DAA_GRAM " +
                "FROM Bills b ";
        if (paid)
            sql += "WHERE b.ServicePeriodEnd IN (SELECT PaidBills.ServicePeriodEnd FROM PaidBills WHERE AccountNumber = ?) AND AccountNumber = ? ";
        else
            sql += "WHERE b.ServicePeriodEnd NOT IN (SELECT PaidBills.ServicePeriodEnd FROM PaidBills WHERE AccountNumber = ?) AND AccountNumber = ? ";

        sql += "ORDER BY b.ServicePeriodEnd";

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

        ps.setString(1, consumerInfo.getAccountID());
        ps.setString(2, consumerInfo.getAccountID());
        ps.setString(3, consumerInfo.getAccountID());

        ResultSet rs = ps.executeQuery();

        List<Bill> bills = new ArrayList<>();
        while(rs.next()) {
            String billNo = rs.getString("BillNumber");

            Bill bill = new PaidBill(
                    billNo,
                    rs.getDate("ServiceDateFrom").toLocalDate(),
                    rs.getDate("ServiceDateTo").toLocalDate(),
                    rs.getDate("DueDate").toLocalDate(),
                    rs.getDouble("NetAmount"));
            //Set ComputeMode
            bill.setComputeMode(rs.getString("ComputeMode"));
            //Set NetMeteringAmount for NetMetered compute mode
            bill.setNetMeteredAmount(rs.getDouble("NetMeterAmount"));
            //apply other deductions when account is netmetering (based on NetMetered value in the ComputeMode of AccountMaster), set net amount as netmeter amount
            if (bill.getComputeMode().equals(Bill.NETMETERED)) {
                bill.setOtherAdjustment(rs.getDouble("NetAmount") - rs.getDouble("NetMeterAmount"));
            }
            Date date = rs.getDate("ServicePeriodEnd");
            LocalDate billMonth = date.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
            bill.setWithPenalty(rs.getBoolean("withPenalty"));
            bill.setBillMonth(formatter.format(billMonth));
            bill.setServicePeriodEnd(billMonth);
            bill.setConsumer(consumerInfo);
            bill.setPowerAmount(rs.getDouble("PowerNew"));
            bill.setKatas(rs.getDouble("KatasAmt"));

            bill.setConsumerType(rs.getString("ConsumerType"));
            bill.setPowerKWH(rs.getDouble("PowerKWH"));
            bill.setTransformerRental(rs.getDouble("TransformerRental"));
            bill.setOtherCharges(rs.getDouble("OthersCharges"));
            bill.setPr(rs.getDouble("PR"));
            bill.setAcrmVat(rs.getDouble("ACRM_TAFPPCA"));
            bill.setDAAVat(rs.getDouble("DAA_GRAM"));
            bill.setVat(rs.getDouble("VATandTaxes"));
            bill.setDaysDelayed(rs.getInt("daysDelayed"));
            bill.setAddCharges(bill.getPr()+bill.getOtherCharges());
            //Disable surcharge computation when set in the Bills table withPenalty value of 1
            if (bill.isWithPenalty()) {
                bill.setSurCharge(Utility.round(bill.computeSurCharge(), 2));
                bill.setSurChargeTax(Utility.round(bill.getSurCharge() * 0.12, 2));
            }else{
                bill.setSurCharge(0);
                bill.setSurChargeTax(0);
            }
            //Automatic PPD for BAPA, ECA, I, CL, CS with 1000kwh within due date payment
            if ((bill.getConsumerType().equals("B")
                    || bill.getConsumerType().equals("E")
                    || bill.getConsumerType().equals("I")
                    || ( (bill.getConsumerType().equals("CL") || bill.getConsumerType().equals("CS")) && bill.getPowerKWH() >= 1000) )
                    && bill.getDaysDelayed() <= 0) {
                double ppd = 0;
                try {
                    ppd = BillDAO.getDiscount(bill);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                bill.setDiscount(ppd);
            }
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
     * Retrieves all bills of customer based on Account Number (on Billing database)
     * @param consumerInfo The consumer account number
     * @return A list of Bill (paid or unpaid)
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<BillStanding> getConsumerBills(ConsumerInfo consumerInfo) throws Exception {
        List<BillStanding> bills = new ArrayList<>();
        String sql = "SELECT ServicePeriodEnd, BillNumber, ConsumerType, DCRNumber, PaymentStatus, DueDate, " +
                "(SELECT Teller FROM PaidBills a WHERE a.AccountNumber=c.AccountNumber AND a.ServicePeriodEnd = c.ServicePeriodEnd) AS Teller, " +
                "(SELECT PostingDate FROM PaidBills a WHERE a.AccountNumber=c.AccountNumber AND a.ServicePeriodEnd = c.ServicePeriodEnd) AS DatePaid, " +
                "(SELECT NetAmount FROM PaidBills a WHERE a.AccountNumber=c.AccountNumber AND a.ServicePeriodEnd = c.ServicePeriodEnd) AS PaidAmount, " +
                "NetAmount as BillAmount " +
                "FROM ConsumerBillsStanding c WHERE AccountNumber = ? ORDER BY ServicePeriodEnd DESC";
        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

        ps.setString(1, consumerInfo.getAccountID());
        ResultSet rs = ps.executeQuery();
        while(rs.next()) {
            BillStanding b = new BillStanding();
            b.setServicePeriodEnd(rs.getDate("ServicePeriodEnd").toLocalDate());
            b.setDueDate(rs.getDate("DueDate").toLocalDate());
            b.setBillNo(rs.getString("BillNumber"));
            b.setConsumerType(rs.getString("ConsumerType"));
            b.setDcrNumber(rs.getString("DCRNumber"));
            b.setStatus(rs.getString("PaymentStatus"));
            b.setTeller(rs.getString("Teller"));
            b.setPostingDate(rs.getDate("DatePaid"));
            b.setTotalAmount(rs.getDouble("PaidAmount"));
            b.setAmountDue(rs.getDouble("BillAmount"));
            bills.add(b);
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

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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
        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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

        String sql = "SELECT pbx.BillNumber, pbx.AccountNumber, pbx.ConsumerName, pbx.TotalAmount, pbx.ConsumerType, pbx.Period, pbx.GenerationVAT, pbx.TransmissionVAT, b.DueDate, pbx.Teller, " +
                "(SELECT ConsumerAddress FROM AccountMaster am WHERE am.AccountNumber = pbx.AccountNumber) AS ConsumerAddress, " +
                "pbx.OthersVAT, pbx.DistributionVAT, pbx.SLVAT, pbx.Item3, pbx.Item2, pbx.SLAdjustment, pbx.PromptPayment, pbx.Surcharge, pbx.NetAmount, pbx.Teller, pbx.ORNumber, pbx.DCRNumber, pbx.ServicePeriodEnd, " +
                "pbx.PostingDate, pbx.PostingSequence, pbx.CurrentBills, pbx.Within30Days, pbx.Over30Days, pbx.PR, pbx.Others, pbx.Powerkwh, pbx.KatasAMount, pbx.OtherDeduction, " +
                "pbx.MDRefund, pbx.NetAmountLessMDRefund, pbx.SeniorCitizenDiscount, pbx.GroupTag, pbx.Amount2306, pbx.Amount2307, b.FBHCAmt AS FranchiseTax, x.Item16 AS BusinessTax, " +
                "x.Item17 AS RealPropertyTax, b.DAA_VAT, b.ACRM_VAT, b.Item1 as GenVatFeb21, FORMAT(pbx.PostingDate,'hh:mm') as PostingTime, ISNULL(CashAmount, pbx.NetAmount) AS CashAmount, ISNULL(CheckAmount, 0) AS CheckAmount, ISNULL(DepositAmount, 0) AS DepositAmount, ISNULL(withPenalty, 1) AS withPenalty " +
                "FROM PaidBillsWithRoute pbx INNER JOIN Bills b ON pbx.AccountNumber=b.AccountNumber AND pbx.ServicePeriodEnd=b.ServicePeriodEnd " +
                "INNER JOIN PaidBills p ON p.AccountNumber=b.AccountNumber AND p.ServicePeriodEnd=b.ServicePeriodEnd " +
                "INNER JOIN BillsExtension x ON  pbx.AccountNumber=x.AccountNumber AND pbx.ServicePeriodEnd=x.ServicePeriodEnd " +
                "WHERE pbx.PostingDate>='"+year+"-"+month+"-"+day+" 00:00:00' AND pbx.PostingDate<='"+year+"-"+month+"-"+day+" 23:59:59'";

        if (teller != null)
            sql += " AND pbx.Teller = ? ";

        sql += "ORDER BY pbx.PostingDate, pbx.PostingSequence";

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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
                   "",
                    "",
                    ""
            );
            PaidBill bill = new PaidBill();
            Date date = rs.getDate("ServicePeriodEnd");
            LocalDate billMonth = date.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");
            bill.setWithPenalty(rs.getBoolean("withPenalty"));
            bill.setBillMonth(formatter.format(billMonth));
            bill.setVat(rs.getDouble("Item2"));
            bill.setDueDate(rs.getDate("DueDate").toLocalDate());
            bill.setTeller(rs.getString("Teller"));
            bill.setConsumerType(rs.getString("ConsumerType"));
            bill.setConsumer(consumerInfo);
            bill.setBillNo(rs.getString("BillNumber"));
            bill.setAmountDue(rs.getDouble("TotalAmount"));
            bill.setTotalAmount(rs.getDouble("NetAmount"));
            bill.setPr(rs.getDouble("PR"));
            bill.setOthers(rs.getDouble("Others"));
            bill.setPromptPayment(rs.getDouble("PromptPayment"));
            bill.setOtherDeduction(rs.getDouble("OtherDeduction"));
            bill.setDcrNumber(rs.getString("DCRNumber"));
            bill.setPostingSequence(rs.getInt("PostingSequence"));
            bill.setAmount2306(rs.getDouble("Amount2306"));
            bill.setAmount2307(rs.getDouble("Amount2307"));
            bill.setPostingDate(rs.getDate("PostingDate"));
            bill.setPostingTime(rs.getString("PostingTime"));
            bill.setCashAmount(rs.getDouble("CashAmount"));
            bill.setCheckAmount(rs.getDouble("CheckAmount"));
            bill.setDeposit(rs.getDouble("DepositAmount"));
            Bill b = bill;
            b.setPeriod(rs.getString("Period"));
            b.setPowerKWH(rs.getDouble("Powerkwh"));
            b.setGenerationVat(rs.getDouble("GenerationVAT"));
            b.setScDiscount(rs.getDouble("SeniorCitizenDiscount"));
            b.setKatasNgVat(rs.getDouble("KatasAMount"));
            b.setTransmissionVat(rs.getDouble("TransmissionVAT"));
            b.setDAAVat(rs.getDouble("DAA_VAT"));
            b.setAcrmVat(rs.getDouble("ACRM_VAT"));
            b.setSystemLossVat(rs.getDouble("SLVAT"));
            b.setGenVatFeb21(rs.getDouble("GenVatFeb21"));
            b.setFbhcAmt(rs.getDouble("FranchiseTax"));
            b.setItem2(rs.getDouble("Item2"));
            b.setItem3(rs.getDouble("Item3"));
            b.setItem16(rs.getDouble("BusinessTax"));
            b.setItem17(rs.getDouble("RealPropertyTax"));
            b.setArGen(b.getGenerationVat()+b.getSystemLossVat()+b.getDAAVat()+b.getAcrmVat()+b.getGenVatFeb21());
            b.setArTran(b.getTransmissionVat());
            b.setMdRefund(rs.getDouble("MDRefund"));
            b.setSurCharge(rs.getDouble("Surcharge"));
            if (b.getSurCharge() > 0) b.setSurChargeTax((b.getSurCharge()*0.12));
            b.setSlAdjustment(rs.getDouble("SLAdjustment"));
            b.setServicePeriodEnd(rs.getDate("ServicePeriodEnd").toLocalDate());
            bills.add(bill);
        }

        rs.close();
        ps.close();

        return bills;
    }

    /**
     * Retrieves DCR Breakdown from a specified date
     * @param year The posting year
     * @param month The posting month
     * @param day The posting day
     * @param teller The posting day
     * @return list The list of dcr breakdown
     * @throws Exception obligatory from DB.getConnection()
     */
    public static HashMap<String, List<ItemSummary>> getDCRBreakDown(int year, int month, int day, String teller) throws Exception{
        String sql = "SELECT "+
        "( "+
        "        (SUM(ISNULL(p.CurrentBills,0)) + SUM(ISNULL(p.Within30Days,0)) + SUM(ISNULL(p.Over30Days,0))) "+
        "        - (SUM(IIF(p.ConsumerType = 'R', ISNULL(p.surcharge*0.12,0), 0))) "+
        "        - (SUM(ISNULL(p.pr,0)) + SUM(ISNULL(p.others,0))) "+
        "        - SUM(ISNULL(p.surcharge,0)) "+
        "        - SUM(ISNULL(p.Item2, 0)) "+
        "        + (SUM(ISNULL(p.SLAdjustment,0)) + SUM(ISNULL(p.PromptPayment,0))) "+
        "        + SUM(ISNULL(p.otherdeduction, 0)) "+
        "        - SUM(ISNULL(p.SeniorCitizenDiscount,0)) "+
        "        + SUM(IIF(p.ConsumerType = 'E', ISNULL(p.others,0), 0)) "+
        "        + SUM(IIF(p.ConsumerType = 'B', ISNULL(p.others,0), 0)) "+
        "        + SUM(ISNULL(p.Amount2306, 0)) "+
        "        + SUM(ISNULL(p.Amount2307, 0)) "+
        "        + SUM(ISNULL(b.FBHCAmt, 0)) "+
        "        + SUM(ISNULL(x.Item16, 0)) "+
        "        + SUM(ISNULL(x.Item17, 0))  "+
        "	) AS Energy, "+
        "SUM(ISNULL(p.pr,0)) AS TransformerRental, "+
        "SUM(ISNULL(p.others,0)) AS Others, "+
        "SUM(ISNULL(p.PromptPayment,0)) AS PPD, "+
        "SUM(ISNULL(p.surcharge,0)) AS Surcharge, "+
        "(SUM(ISNULL(p.Item2, 0)) "+
        "        + (SUM(IIF(p.ConsumerType = 'R', p.surcharge*0.12, 0))) "+
        "        - SUM(ISNULL(b.FBHCAmt, 0)) "+
        "        - SUM(ISNULL(x.Item17, 0)) "+
        "        - SUM(ISNULL(x.Item16, 0)) "+
        "        - SUM(IIF(p.Period >'201502', (ISNULL(p.TransmissionVAT, 0)), 0)) "+
        "        - "+
        "        ( "+
        "                SUM(IIF(p.Period >'201502', (ISNULL(p.GenerationVAT,0)), 0)) "+
        "                        + SUM(IIF(p.Period >'201503', (ISNULL(p.SLVAT,0)), 0)) "+
        "                        + SUM(IIF(p.Period >'201503', (ISNULL(b.DAA_VAT,0)), 0)) "+
        "                        + SUM(IIF(p.Period >'201503', (ISNULL(b.ACRM_VAT,0)), 0)) "+
        "                        + SUM(IIF(p.Period >'202111', (ISNULL(b.Item1,0)), 0)) "+
        "        ) "+
        ") AS Evat, "+

        "SUM(ISNULL(p.SLAdjustment,0)) AS SLAdj, "+
        "SUM(ISNULL(p.otherdeduction,0)) AS OtherDeduction, "+
        "SUM(ISNULL(p.Amount2306, 0)) AS Amount2306, "+
        "SUM(ISNULL(p.Amount2307, 0)) AS Amount2307, "+
        "SUM(ISNULL(p.MDRefund, 0)) AS MDRefund, "+
        "SUM(ISNULL(p.PowerKWH, 0)) AS PowerKWH, "+
        "SUM(ISNULL(p.NetAmount,0)) AS AmountPaid, "+
        "SUM(ISNULL(pb.cashAmount, p.NetAmount)) AS CashAmount, "+
        "SUM(ISNULL(pb.checkAmount, 0)) AS CheckAmount, "+
        "SUM(ISNULL(p.SeniorCitizenDiscount,0)) AS SeniorCitizenDiscount, "+
        "SUM(ISNULL(b.NetAmount,0)) AS AmountDue, "+
        "SUM(IIF(p.Period >'201502', (ISNULL(p.TransmissionVAT, 0)), 0)) AS ARVATTrans, "+
        "(SUM(IIF(p.Period >'201502', (ISNULL(p.GenerationVAT,0)), 0)) "+
        "        + SUM(IIF(p.Period >'201503', (ISNULL(p.SLVAT,0)), 0)) "+
        "        + SUM(IIF(p.Period >'201503', (ISNULL(b.DAA_VAT,0)), 0)) "+
        "        + SUM(IIF(p.Period >'201503', (ISNULL(b.ACRM_VAT,0)), 0)) "+
        "        + SUM(IIF(p.Period >'202111', (ISNULL(b.Item1,0)), 0))) AS ARVATGen, "+
        "SUM(ISNULL(p.KatasAMount, 0)) AS KatasNgVAT "+
        "FROM Bills b "+
        "INNER JOIN BillsExtension x ON b.AccountNumber=x.AccountNumber AND b.ServicePeriodEnd=x.ServicePeriodEnd "+
        "INNER JOIN PaidBillsWithRoute p ON b.AccountNumber=p.AccountNumber AND b.ServicePeriodEnd=p.ServicePeriodEnd "+
        "INNER JOIN PaidBills pb ON b.AccountNumber=pb.AccountNumber AND b.ServicePeriodEnd=pb.ServicePeriodEnd "+
        "WHERE p.PostingDate >= '"+year+"-"+month+"-"+day+" 00:00:00' AND p.PostingDate <= '"+year+"-"+month+"-"+day+" 23:59:59'";

        if (teller != null)
            sql += " AND p.TELLER = ?";

        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);

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

            double evat = rs.getDouble("Evat");

            double total = (energy + tr + others + surcharge + evat) + (-slAdj - ppD - katasNgVAT - otherDeduction - mdRefund - scDiscount - amount2307 - amount2306) + arTrans + arGen;
            double totalPaid = rs.getDouble("AmountPaid");
            double amountDue = rs.getDouble("AmountDue");
            double totalCash = rs.getDouble("CashAmount");
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
            dcrBreakdown.add(ch2306Item);
            dcrBreakdown.add(ch2307Item);
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

    /**
     * OR Posting (batch update) the list of paid bill transactions (from a teller/collector in given date)
     * @param bills The list of paid bills
     * @param dcrNumber The OR Number/OR Number
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void postBills(List<Bill> bills, String dcrNumber) throws Exception {

        Connection conn = DB.getConnection(Utility.DB_BILLING);
        conn.setAutoCommit(false);

        String sql = "";

        for (Bill bill : bills) {
            sql += "UPDATE PaidBills SET DCRNumber = '"+dcrNumber+"', ORNumber = '"+dcrNumber+"' " +
                    "WHERE AccountNumber = '"+bill.getConsumer().getAccountID()+"' AND ServicePeriodEnd = '"+bill.getServicePeriodEnd().toString()+"';";

        }
        PreparedStatement ps = conn.prepareStatement(sql);
        try {
            ps.executeUpdate();
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            conn.rollback();
        }
        ps.close();
        conn.setAutoCommit(true);
    }
    /**
     * Cancel a PaidBill from Paid Bills
     * @param bill The Paid bill
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void cancelBill(PaidBill bill) throws Exception {
        cancelCheck(bill);
        String sql = "DELETE FROM PaidBills WHERE AccountNumber = '"+bill.getConsumer().getAccountID()+"' AND ServicePeriodEnd = '"+bill.getServicePeriodEnd().toString()+"';";
        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
    }
    /**
     * Cancel Check Payments for cancelling Paid Bills
     * @param bill The Paid bill
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void cancelCheck(PaidBill bill) throws Exception {
        String sql = "DELETE FROM CheckPayment WHERE AccountNumber = '"+bill.getConsumer().getAccountID()+"' AND ServicePeriodEnd = '"+bill.getServicePeriodEnd().toString()+"';";
        PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);
        ps.executeUpdate();
        ps.close();
    }

    /**
     * Check if a deposit is made from AddCharges table
     * @param accountNumber The account
     * @return HashMap
     * @throws Exception obligatory from DB.getConnection()
     */
    public static HashMap verifyDeposit(String accountNumber) {
        HashMap<String, Double> result = null;
        try {

            String sql = "SELECT AccountNumber, ISNULL(QCLoanAmount, 0) AS QCLoanAmount, ISNULL(QCAmount, 0) AS QCAmount, ISNULL(QCMonths, 0) AS QCMonths, ISNULL(EPAmount, 0) AS EPAmount, ISNULL(EPMonths, 0) AS EPMonths, ISNULL(PCAmount, 0) AS PCAmount, ISNULL(PCMonths, 0) AS PCMonths " +
                    "FROM OtherCharges WHERE AccountNumber = '" + accountNumber + "';";
            PreparedStatement ps = DB.getConnection(Utility.DB_BILLING).prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                result = new HashMap();
                result.put("QCLoanAmount", rs.getDouble("QCLoanAmount"));
                result.put("QCAmount", rs.getDouble("QCAmount"));
                result.put("QCMonths", rs.getDouble("QCMonths"));
                result.put("EPAmount", rs.getDouble("EPAmount"));
                result.put("EPMonths", rs.getDouble("EPMonths"));
                result.put("PCAmount", rs.getDouble("PCAmount"));
                result.put("PCMonths", rs.getDouble("PCMonths"));
            }
            ps.close();
        }catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }
}
