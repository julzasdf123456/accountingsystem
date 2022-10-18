package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
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
                        "BankID, Note, CheckNumber, Particulars) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(2, td.getTransactionNumber());
        ps.setString(3, td.getTransactionCode());
        ps.setDate(4, td.getTransactionDate() !=null ? java.sql.Date.valueOf(td.getTransactionDate()):null);
        ps.setInt(5, td.getSequenceNumber());
        ps.setString(6, td.getAccountCode());
        ps.setDouble(7, td.getDebit());
        ps.setDouble(8, td.getCredit());
        ps.setDate(9, td.getOrDate()!=null?java.sql.Date.valueOf(td.getOrDate()):null);
        ps.setString(10, td.getBankID());
        ps.setString(11, td.getNote());
        ps.setString(12, td.getCheckNumber());
        ps.setString(13,td.getParticulars());

        ps.executeUpdate();

        ps.close();
    }

    public static void add(List<TransactionDetails> tds) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber, Particulars) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

        DB.getConnection().setAutoCommit(false);

        for(TransactionDetails td: tds) {
            ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
            ps.setString(2, td.getTransactionNumber());
            ps.setString(3, td.getTransactionCode());
            ps.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
            ps.setInt(5, td.getSequenceNumber());
            ps.setString(6, td.getAccountCode());
            ps.setDouble(7, td.getDebit());
            ps.setDouble(8, td.getCredit());
            ps.setDate(9, java.sql.Date.valueOf(td.getOrDate()));
            ps.setString(10, td.getBankID());
            ps.setString(11, td.getNote());
            ps.setString(12, td.getCheckNumber());
            ps.setString(13,td.getParticulars());

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
            td.setTransactionDate(rs.getDate("TransactionDate").toLocalDate());
            td.setSequenceNumber(rs.getInt("AccountSequence"));
            td.setAccountCode(rs.getString("AccountCode"));
            td.setDebit(rs.getDouble("Debit"));
            td.setCredit(rs.getDouble("Credit"));
            td.setOrDate(rs.getDate("ORDate").toLocalDate());
            td.setBankID(rs.getString("BankID"));
            td.setNote(rs.getString("Note"));
            td.setCheckNumber(rs.getString("CheckNumber"));
            td.setParticulars(rs.getString("Particulars"));

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
            td.setTransactionDate(rs.getDate("TransactionDate")!=null?rs.getDate("TransactionDate").toLocalDate():null);
            td.setSequenceNumber(rs.getInt("AccountSequence"));
            td.setAccountCode(rs.getString("AccountCode"));
            td.setDebit(rs.getDouble("Debit"));
            td.setCredit(rs.getDouble("Credit"));
            td.setOrDate(rs.getDate("ORDate")!=null?rs.getDate("ORDate").toLocalDate():null);
            td.setBankID(rs.getString("BankID"));
            td.setNote(rs.getString("Note"));
            td.setCheckNumber(rs.getString("CheckNumber"));
            td.setParticulars(rs.getString("Particulars"));
            tds.add(td);
        }

        rs.close();
        ps.close();

        return tds;
    }

    public static int getNextSequenceNumber(LocalDate period, String transactionNumber) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT MAX(AccountSequence) FROM TransactionDetails td WHERE  Period=? AND TransactionCode ='BR' AND TransactionNumber = ? AND AccountSequence <> 999;");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();

        return rs.getInt(1) + 1;
    }

    public static double getTotalDebit(LocalDate period, String transactionNumber) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(td.Debit) FROM TransactionDetails td WHERE td.Period=? AND td.TransactionNumber=? AND td.TransactionCode='BR' AND td.Credit=0");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    public static void syncDebit(LocalDate period, String transactionNumber) throws Exception {
        PreparedStatement psx = DB.getConnection().prepareStatement("SELECT * FROM TransactionDetails td WHERE td.Period=? AND td.TransactionNumber=? AND td.TransactionCode='BR' AND td.Debit=0");
        psx.setDate(1, java.sql.Date.valueOf(period));
        psx.setString(2, transactionNumber);
        ResultSet rs = psx.executeQuery();

        if(rs.next()) {
            PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE TransactionDetails SET Credit=? WHERE Period=? AND TransactionNumber=? AND TransactionCode='BR' AND Debit=0 AND AccountSequence=999");
            ps.setDouble(1, getTotalDebit(period, transactionNumber));
            ps.setDate(2, java.sql.Date.valueOf(period));
            ps.setString(3, transactionNumber);
            ps.executeUpdate();
        }else {
            TransactionDetails td = new TransactionDetails();
            td.setPeriod(period);
            td.setTransactionNumber(transactionNumber);
            td.setTransactionCode("BR");
            td.setCredit(getTotalDebit(period, transactionNumber));
            td.setSequenceNumber(999);
            //BR or BRSub
            td.setAccountCode("12110201000");
            TransactionDetailsDAO.add(td);
        }
    }

    public static void delete(TransactionDetails td) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("DELETE FROM TransactionDetails " +
                "WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND AccountSequence=? AND Debit=? AND Particulars=?");
        ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(2, td.getTransactionNumber());
        ps.setString(3, td.getTransactionCode());
        ps.setInt(4, td.getSequenceNumber());
        ps.setDouble(5, td.getDebit());
        ps.setString(6, td.getParticulars());

        ps.executeUpdate();
        ps.close();
    }
}
