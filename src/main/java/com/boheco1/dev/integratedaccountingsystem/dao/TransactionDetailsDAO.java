package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetailsDAO {
    public static void add(TransactionDetails td) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(2, td.getTransactionNumber());
        ps.setString(3, td.getTransactionCode());
        ps.setTimestamp(4, java.sql.Timestamp.valueOf(td.getTransactionDate()));
        ps.setInt(5, td.getSequenceNumber());
        ps.setString(6, td.getAccountCode());
        ps.setDouble(7, td.getDebit());
        ps.setDouble(8, td.getCredit());
        ps.setDate(9, java.sql.Date.valueOf(td.getOrDate()));
        ps.setString(10, td.getBankID());
        ps.setString(11, td.getNote());
        ps.setString(12, td.getCheckNumber());

        ps.executeUpdate();

        ps.close();
    }

    public static void add(List<TransactionDetails> tds) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

        DB.getConnection().setAutoCommit(false);

        for(TransactionDetails td: tds) {
            ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
            ps.setString(2, td.getTransactionNumber());
            ps.setString(3, td.getTransactionCode());
            ps.setTimestamp(4, java.sql.Timestamp.valueOf(td.getTransactionDate()));
            ps.setInt(5, td.getSequenceNumber());
            ps.setString(6, td.getAccountCode());
            ps.setDouble(7, td.getDebit());
            ps.setDouble(8, td.getCredit());
            ps.setDate(9, java.sql.Date.valueOf(td.getOrDate()));
            ps.setString(10, td.getBankID());
            ps.setString(11, td.getNote());
            ps.setString(12, td.getCheckNumber());

            ps.addBatch();
        }

        ps.executeBatch();
        DB.getConnection().setAutoCommit(true);

        ps.close();
    }

    public static TransactionDetails get(LocalDate period, String transactionNumber, String transactionCode, int accountSequence) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE period=? AND TransactionNumber=? " +
                        "AND TransactionCode=? AND AccountSequence=?");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionNumber);
        ps.setString(3, transactionCode);
        ps.setInt(4,accountSequence);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            TransactionDetails td = new TransactionDetails();
            td.setPeriod(rs.getDate("Period").toLocalDate());
            td.setTransactionNumber(rs.getString("TransactionNumber"));
            td.setTransactionCode(rs.getString("TransactionCode"));
            td.setTransactionDate(rs.getTimestamp("TransactionDate").toLocalDateTime());
            td.setSequenceNumber(rs.getInt("AccountSequence"));
            td.setAccountCode(rs.getString("AccountCode"));
            td.setDebit(rs.getDouble("Debit"));
            td.setCredit(rs.getDouble("Credit"));
            td.setOrDate(rs.getDate("ORDate").toLocalDate());
            td.setBankID(rs.getString("BankID"));
            td.setNote(rs.getString("Note"));
            td.setCheckNumber(rs.getString("CheckNumber"));

            rs.close();
            ps.close();

            return td;
        }else {
            rs.close();
            ps.close();
            return null;
        }
    }

    public static List<TransactionDetails> get(LocalDate period, String transactionNumber, String transactionCode) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE period=? AND TransactionNumber=? " +
                        "AND TransactionCode=?");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionNumber);
        ps.setString(3, transactionCode);

        ResultSet rs = ps.executeQuery();

        ArrayList<TransactionDetails> tds = new ArrayList<>();

        while(rs.next()) {
            TransactionDetails td = new TransactionDetails();
            td.setPeriod(rs.getDate("Period").toLocalDate());
            td.setTransactionNumber(rs.getString("TransactionNumber"));
            td.setTransactionCode(rs.getString("TransactionCode"));
            td.setTransactionDate(rs.getTimestamp("TransactionDate").toLocalDateTime());
            td.setSequenceNumber(rs.getInt("AccountSequence"));
            td.setAccountCode(rs.getString("AccountCode"));
            td.setDebit(rs.getDouble("Debit"));
            td.setCredit(rs.getDouble("Credit"));
            td.setOrDate(rs.getDate("ORDate").toLocalDate());
            td.setBankID(rs.getString("BankID"));
            td.setNote(rs.getString("Note"));
            td.setCheckNumber(rs.getString("CheckNumber"));
            tds.add(td);
        }

        rs.close();
        ps.close();

        return tds;
    }
}
