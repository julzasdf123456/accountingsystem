package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.CRMQueue;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
     * Retrieves a ConsumerInfo based on Account Number (on Billing database, ConsumerInquiry view)
     * @param key The reference number, consumer name, consumer address
     * @return A ConsumerInfo
     * @throws Exception obligatory from DB.getConnection()
     */
    public static CRMQueue getConsumerRecordFromCRM(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM CRMQueue WHERE ConsumerName LIKE ? OR ConsumerAddress LIKE ? ");

        ps.setString(1, '%'+ key+'%');
        ps.setString(2, '%'+ key+'%');

        ResultSet rs = ps.executeQuery();

        CRMQueue record = null;

        while(rs.next()) {
            record = new CRMQueue(
                    rs.getString("id"),
                    rs.getString("ConsumerName"),
                    rs.getString("ConsumerAddress"),
                    rs.getString("TransactionPurpose"),
                    rs.getString("Source"),
                    rs.getString("SourceId"),
                    rs.getDouble("SubTotal"),
                    rs.getDouble("VAT"),
                    rs.getDouble("Total")
            );
        }

        rs.close();
        ps.close();

        return record;
    }

    /**
     * Retrieves a list of CRMQueue as a search result based on a search Key (on Accounting database, CRMQueue table)
     * @param key The search key
     * @return A list of CRMQueue that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<CRMQueue> getConsumerRecordFromCRMList(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM CRMQueue WHERE ConsumerName LIKE ? OR ConsumerAddress LIKE ? ");

        ps.setString(1, '%'+ key+'%');
        ps.setString(2, '%'+ key+'%');

        ResultSet rs = ps.executeQuery();

        List<CRMQueue> list = new ArrayList<>();
        while(rs.next()) {
            CRMQueue record = new CRMQueue(
                    rs.getString("id"),
                    rs.getString("ConsumerName"),
                    rs.getString("ConsumerAddress"),
                    rs.getString("TransactionPurpose"),
                    rs.getString("Source"),
                    rs.getString("SourceId"),
                    rs.getDouble("SubTotal"),
                    rs.getDouble("VAT"),
                    rs.getDouble("Total"));
            list.add(record);
        }

        rs.close();
        ps.close();

        return list;
    }
}
