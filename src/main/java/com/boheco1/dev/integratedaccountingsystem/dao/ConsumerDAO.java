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
                "SELECT * FROM AccountMaster WHERE ConsumerName LIKE ? OR AccountNumber LIKE ? " +
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
     * @param accountNo The consumer account number
     * @param paid The bill status
     * @return A list of Bill
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Bill> getConsumerBills(String accountNo, boolean paid) throws Exception {
        String sql = "SELECT * FROM BillsInquiry WHERE BillNumber NOT IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        if (paid)
            sql = "SELECT * FROM BillsInquiry WHERE BillNumber IN (SELECT BillNumber FROM PaidBills) AND AccountNumber = ? ORDER BY DueDate DESC";

        PreparedStatement ps = DB.getConnection("Billing").prepareStatement(sql);

        ps.setString(1, accountNo);

        ResultSet rs = ps.executeQuery();

        List<Bill> bills = new ArrayList<>();

        while(rs.next()) {
            Bill bill = new Bill(
                    rs.getString("BillNumber"),
                    rs.getDate("ServiceDateFrom"),
                    rs.getDate("ServiceDateTo"),
                    rs.getDate("DueDate"),
                    rs.getDouble("NetAmount"));
            Date date = rs.getDate("ServicePeriodEnd");
            LocalDate billMonth = date.toLocalDate();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM YYYY");

            bill.setBillMonth(formatter.format(billMonth));
            bills.add(bill);
        }

        rs.close();
        ps.close();

        return bills;
    }
}
