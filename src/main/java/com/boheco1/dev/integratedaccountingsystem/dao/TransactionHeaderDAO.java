package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionHeaderDAO {
    public static void add(TransactionHeader transactionHeader) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionHeader " +
                        "(Period, TransactionNumber, TransactionCode, " +
                        "AccountID, Source, Particulars, TransactionDate, " +
                        "Bank, ReferenceNo, Amount, EnteredBy, DateEntered, " +
                        "DateLastModified, UpdatedBy, Remarks) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setDate(1, java.sql.Date.valueOf(transactionHeader.getPeriod()));
        ps.setString(2, transactionHeader.getTransactionNumber());
        ps.setString(3, transactionHeader.getTransactionCode());
        ps.setString(4, transactionHeader.getAccountID());
        ps.setString(5,transactionHeader.getSource());
        ps.setString(6, transactionHeader.getParticulars());
        ps.setDate(7, java.sql.Date.valueOf(transactionHeader.getTransactionDate()));
        ps.setString(8, transactionHeader.getBank());
        ps.setString(9, transactionHeader.getReferenceNo());
        ps.setDouble(10, transactionHeader.getAmount());
        ps.setString(11, transactionHeader.getEnteredBy());
        ps.setTimestamp(12, transactionHeader.getDateEntered()!=null ? java.sql.Timestamp.valueOf(transactionHeader.getDateEntered()) : null);
        ps.setTimestamp(13, transactionHeader.getDateLastModified()!=null ? java.sql.Timestamp.valueOf(transactionHeader.getDateLastModified()) : null);
        ps.setString(14, transactionHeader.getUpdatedBy());
        ps.setString(15, transactionHeader.getRemarks());

        ps.executeUpdate();
        ps.close();
    }

    public static void update(TransactionHeader th) throws Exception {

    }

    public static List<TransactionHeader> searchByPayee(String nameOfPayee) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionHeader WHERE Particulars LIKE ?");
        ps.setString(1, nameOfPayee);
        ResultSet rs = ps.executeQuery();

        ArrayList<TransactionHeader> tas = new ArrayList<>();

        while(rs.next()) {
            TransactionHeader th = new TransactionHeader();
            th.setPeriod(rs.getDate("Period").toLocalDate());
            th.setTransactionNumber(rs.getString("TransactionNumber"));
            th.setTransactionCode(rs.getString("TransactionCode"));
            th.setAccountID(rs.getString("AccountID"));
            th.setSource(rs.getString("Source"));
            th.setParticulars(rs.getString("Particulars"));
            th.setTransactionDate(rs.getDate("TransactionDate").toLocalDate());
            th.setBank(rs.getString("Bank"));
            th.setReferenceNo(rs.getString("ReferenceNo"));
            th.setAmount(rs.getDouble("Amount"));
            th.setEnteredBy(rs.getString("EnteredBy"));
            th.setDateEntered( rs.getTimestamp("DateEntered") !=null ? rs.getTimestamp("DateEntered").toLocalDateTime() : null);
            th.setDateLastModified(rs.getTimestamp("DateLastModified") !=null ? rs.getTimestamp("DateLastModified").toLocalDateTime() : null);
            th.setUpdatedBy(rs.getString("UpdatedBy"));
            th.setRemarks(rs.getString("Remarks"));

            tas.add(th);
        }

        return tas;
    }

    public static TransactionHeader get(String transactionNumber, String transactionCode) throws Exception {

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionHeader WHERE TransactionNumber=? AND TransactionCode=?");
        ps.setString(1, transactionNumber);
        ps.setString(2, transactionCode);

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            TransactionHeader th = new TransactionHeader();
            th.setPeriod(rs.getDate("Period").toLocalDate());
            th.setTransactionNumber(rs.getString("TransactionNumber"));
            th.setTransactionCode(rs.getString("TransactionCode"));
            th.setAccountID(rs.getString("AccountID"));
            th.setSource(rs.getString("Source"));
            th.setParticulars(rs.getString("Particulars"));
            th.setTransactionDate(rs.getDate("TransactionDate").toLocalDate());
            th.setBank(rs.getString("Bank"));
            th.setReferenceNo(rs.getString("ReferenceNo"));
            th.setAmount(rs.getDouble("Amount"));
            th.setEnteredBy(rs.getString("EnteredBy"));
            th.setDateEntered( rs.getTimestamp("DateEntered") !=null ? rs.getTimestamp("DateEntered").toLocalDateTime() : null);
            th.setDateLastModified(rs.getTimestamp("DateLastModified") !=null ? rs.getTimestamp("DateLastModified").toLocalDateTime() : null);
            th.setUpdatedBy(rs.getString("UpdatedBy"));
            th.setRemarks(rs.getString("Remarks"));

            return th;
        }else {
            return null;
        }
    }

    public static TransactionHeader get(String transactionNumber, String transactionCode, LocalDate transactionDate) throws Exception {

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionHeader WHERE TransactionNumber=? AND TransactionCode=? AND TransactionDate=?");
        ps.setString(1, transactionNumber);
        ps.setString(2, transactionCode);
        ps.setDate(3, java.sql.Date.valueOf(transactionDate));

        ResultSet rs = ps.executeQuery();

        if(rs.next()){
            TransactionHeader th = new TransactionHeader();
            th.setPeriod(rs.getDate("Period").toLocalDate());
            th.setTransactionNumber(rs.getString("TransactionNumber"));
            th.setTransactionCode(rs.getString("TransactionCode"));
            th.setAccountID(rs.getString("AccountID"));
            th.setSource(rs.getString("Source"));
            th.setParticulars(rs.getString("Particulars"));
            th.setTransactionDate(rs.getDate("TransactionDate").toLocalDate());
            th.setBank(rs.getString("Bank"));
            th.setReferenceNo(rs.getString("ReferenceNo"));
            th.setAmount(rs.getDouble("Amount"));
            th.setEnteredBy(rs.getString("EnteredBy"));
            th.setDateEntered( rs.getTimestamp("DateEntered") !=null ? rs.getTimestamp("DateEntered").toLocalDateTime() : null);
            th.setDateLastModified(rs.getTimestamp("DateLastModified") !=null ? rs.getTimestamp("DateLastModified").toLocalDateTime() : null);
            th.setUpdatedBy(rs.getString("UpdatedBy"));
            th.setRemarks(rs.getString("Remarks"));

            return th;
        }else {
            return null;
        }
    }

    public static int getNextARNumber() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT TransactionNumber FROM TransactionHeader WHERE TransactionCode='AR' ORDER BY TransactionDate DESC, TransactionNumber DESC");
        if(rs.next()) {
            return rs.getInt("TransactionNumber")+1;
        }else {
            return 0;
        }
    }

}
