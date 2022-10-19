package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ConsumerDAO {

    /**
     * Retrieves a list of ConsumerInfo as a search result based on a search Key (on Billing database, ConsumerInquiry view)
     * @param key The search key
     * @return A list of ConsumerInfo that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<ConsumerInfo> getConsumerRecords(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(
                "SELECT TOP 10 * FROM AccountMaster WHERE ConsumerName LIKE ? OR AccountNumber LIKE ? " +
                        "ORDER BY ConsumerName");
        ps.setString(1, '%'+ key+'%');
        ps.setString(2, '%'+ key+'%');

        ResultSet rs = ps.executeQuery();

        List<ConsumerInfo> list = new ArrayList<>();
        while(rs.next()) {
            ConsumerInfo record = new ConsumerInfo(
                    rs.getString("AccountNumber"),
                    rs.getString("ConsumerName"),
                    rs.getString("ConsumerAddress"),
                    rs.getString("TINNo"),
                    rs.getString("Email"),
                    rs.getString("ContactNumber")
            );

            record.setMeterNumber(rs.getString("MeterNumber"));
            record.setAccountType(rs.getString("ConsumerType"));
            record.setAccountStatus(rs.getString("AccountStatus"));

            list.add(record);
        }

        rs.close();
        ps.close();

        return list;
    }

    /**
     * Retrieves a ConsumerInfo based on Account Number (on Billing database, ConsumerInquiry view)
     * @param accountNo The consumer account number
     * @return A ConsumerInfo
     * @throws Exception obligatory from DB.getConnection()
     */
    public static ConsumerInfo getConsumerRecord(String accountNo) throws Exception {
        PreparedStatement ps = DB.getConnection("Billing").prepareStatement("SELECT * FROM AccountMaster WHERE AccountNumber = ?");

        ps.setString(1, accountNo);

        ResultSet rs = ps.executeQuery();

        ConsumerInfo record = null;

        while(rs.next()) {
             record = new ConsumerInfo(
                     rs.getString("AccountNumber"),
                     rs.getString("ConsumerName"),
                     rs.getString("ConsumerAddress"),
                     rs.getString("TINNo"),
                     rs.getString("Email"),
                     rs.getString("ContactNumber")
            );

            record.setMeterNumber(rs.getString("MeterNumber"));
            record.setAccountType(rs.getString("ConsumerType"));
            record.setAccountStatus(rs.getString("AccountStatus"));
        }

        rs.close();
        ps.close();

        return record;
    }

    /**
     * Retrieves bills of customer based on Account Number (on Billing database)
     * @param consumerInfo The consumer account number
     * @param paid The bill status
     * @return A list of Bill
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Bill> getConsumerBills(ConsumerInfo consumerInfo, boolean paid) throws Exception {
        String sql = "SELECT * FROM BillsInquiry WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        if (paid)
            sql = "SELECT * FROM BillsInquiry WHERE BillNumber IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, consumerInfo.getAccountID());

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

            String charge = "Select ServicePeriodEnd,isnull(NetAmount,0) AS NetAmount, "+
                    "DATEDIFF(day, DueDate, getdate()) AS daysDelayed, "+
                    "ISNULL(ConsumerType,'RM') as ConsumerType, "+
                    "ISNULL(PowerKWH,0) as PowerKWH, "+
                    "ISNULL(Item2,0) as VATandTaxes, "+
                    "isnull(PR,0) as TransformerRental, "+
                    "isnull(Others,0) as OthersCharges, "+
                    "isnull(ACRM_TAFPPCA,0) as ACRM_TAFPPCA, "+
                    "isnull(DAA_GRAM,0) as DAA_GRAM "+
                    "from bills where BillNumber=? and ServicePeriodEnd not in (Select ServicePeriodEnd from PaidBills where BillNumber=?) order by ServicePeriodEnd";
            PreparedStatement ps_charge = DB.getConnection("Billing").prepareStatement(charge);

            ps_charge.setString(1, billNo);
            ps_charge.setString(2, billNo);

            ResultSet rs2 = ps_charge.executeQuery();
            //Compute the surcharge
            while(rs2.next()) {
                bill.setConsumerType(rs2.getString("ConsumerType"));
                double pkwh = rs2.getDouble("PowerKWH");
                bill.setPowerKWH(pkwh);
                int daysDelayed = rs2.getInt("daysDelayed");
                double netAmount = rs2.getDouble("NetAmount");

                if (netAmount != bill.getAmountDue()) {
                    throw new Exception("NetAmounts from Bills and BillsInquiry does not match!");
                }
                double vat = rs2.getDouble("VATandTaxes");
                double transformerRental = rs2.getDouble("TransformerRental");
                double othersCharges = rs2.getDouble("OthersCharges");
                double acrm = rs2.getDouble("ACRM_TAFPPCA");
                double daa = rs2.getDouble("DAA_GRAM");
                bill.setDaysDelayed(daysDelayed);
                double penalty = (netAmount - (vat + transformerRental + othersCharges + acrm + daa));
//                bill.setSurCharge(bill.computeSurCharge(penalty));
//                bill.setTotalAmount();
            }
            bills.add(bill);

            ps_charge.close();
            ps_charge.close();
        }

        rs.close();
        ps.close();

        return bills;
    }
}
