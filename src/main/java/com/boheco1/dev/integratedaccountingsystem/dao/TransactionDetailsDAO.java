package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetailsDAO {
    public static void add(TransactionDetails td) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber, Particulars, DepositedDate) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
        ps.setDate(14, td.getDepositedDate()==null ? null : java.sql.Date.valueOf(td.getDepositedDate()));

        ps.executeUpdate();

        ps.close();
    }

    public static void add(List<TransactionDetails> tds) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber, Particulars, DepositedDate) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
            ps.setDate(9, td.getOrDate()==null?null:java.sql.Date.valueOf(td.getOrDate()));
            ps.setString(10, td.getBankID());
            ps.setString(11, td.getNote());
            ps.setString(12, td.getCheckNumber());
            ps.setString(13,td.getParticulars());
            ps.setDate(14, td.getDepositedDate()==null ? null : java.sql.Date.valueOf(td.getDepositedDate()));

            ps.addBatch();
        }

        ps.executeBatch();
        DB.getConnection().setAutoCommit(true);

        ps.close();
    }
    private static PreparedStatement psAdd, psUpdate;
    public static void addUpdate(List<TransactionDetails> updateRecord, List<TransactionDetails> newRecord) throws Exception {
        try {
             psAdd = DB.getConnection().prepareStatement(
                    "INSERT INTO TransactionDetails (" +
                            "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                            "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                            "BankID, Note, CheckNumber, Particulars, DepositedDate) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

             psUpdate = DB.getConnection().prepareStatement(
                    "UPDATE TransactionDetails SET Credit = ? " +
                            "WHERE " +
                            "TransactionNumber = ? AND AccountCode = ? ;");

            DB.getConnection().setAutoCommit(false);
            int sequence = updateRecord.size()+1;
            for(TransactionDetails td: newRecord) {
                psAdd.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
                psAdd.setString(2, td.getTransactionNumber());
                psAdd.setString(3, td.getTransactionCode());
                psAdd.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
                psAdd.setInt(5, sequence++);
                psAdd.setString(6, td.getAccountCode());
                psAdd.setDouble(7, td.getDebit());
                psAdd.setDouble(8, td.getCredit());
                psAdd.setDate(9, td.getOrDate()==null?null:java.sql.Date.valueOf(td.getOrDate()));
                psAdd.setString(10, td.getBankID());
                psAdd.setString(11, td.getNote());
                psAdd.setString(12, td.getCheckNumber());
                psAdd.setString(13,td.getParticulars());
                psAdd.setDate(14, td.getDepositedDate()==null ? null : java.sql.Date.valueOf(td.getDepositedDate()));

                psAdd.addBatch();
            }

            for(TransactionDetails td: updateRecord) {
                psUpdate.setDouble(1, td.getCredit());
                psUpdate.setString(2, td.getTransactionNumber());
                psUpdate.setString(3, td.getAccountCode());
                psUpdate.addBatch();
            }


            psUpdate.executeBatch();
            psAdd.executeBatch();
            DB.getConnection().setAutoCommit(true);

            psAdd.close();
            psUpdate.close();
        } catch (Exception e){
            Utility.ERROR_MSG = e.getMessage();
            DB.getConnection().rollback();
            DB.getConnection().setAutoCommit(true);
            psAdd.close();
            psUpdate.close();
            e.printStackTrace();
        }
    }



    public static List<TransactionDetails> getDebitOnly(LocalDate period, String transactionNumber, String transactionCode) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE period=? AND TransactionNumber=? " +
                        "AND TransactionCode=? AND Debit>0 AND Credit=0");
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
            td.setDepositedDate(rs.getDate("DepositedDate")!=null ? rs.getDate("DepositedDate").toLocalDate() : null);
            tds.add(td);
        }

        rs.close();
        ps.close();

        return tds;
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
            td.setDepositedDate(rs.getDate("DepositedDate").toLocalDate());

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
            td.setDepositedDate(rs.getDate("DepositedDate")==null ? null : rs.getDate("DepositedDate").toLocalDate());
            tds.add(td);
        }

        rs.close();
        ps.close();

        return tds;
    }

    public static List<TransactionDetails> get(String transactionNumber) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE TransactionNumber=? AND TransactionCode = ?");
        ps.setString(1, transactionNumber);
        ps.setString(2,Utility.getTransactionCodeProperty());

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
            td.setDepositedDate(rs.getDate("DepositedDate")==null ? null : rs.getDate("DepositedDate").toLocalDate());
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
                "SELECT SUM(td.Debit) FROM TransactionDetails td WHERE td.Period=? AND td.TransactionNumber=? AND td.Credit=0");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    public static void syncDebit(LocalDate period, String transactionNumber, String transactionCode) throws Exception {
        PreparedStatement psx = DB.getConnection().prepareStatement("SELECT * FROM TransactionDetails td WHERE td.Period=? AND td.TransactionNumber=? AND td.TransactionCode=? AND td.Debit=0");
        psx.setDate(1, java.sql.Date.valueOf(period));
        psx.setString(2, transactionNumber);
        psx.setString(3, transactionCode);
        ResultSet rs = psx.executeQuery();

        if(rs.next()) {
            PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE TransactionDetails SET Credit=? WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND Debit=0 AND AccountSequence=999");
            ps.setDouble(1, getTotalDebit(period, transactionNumber));
            ps.setDate(2, java.sql.Date.valueOf(period));
            ps.setString(3, transactionNumber);
            ps.setString(4, transactionCode);
            ps.executeUpdate();
        }else {
            TransactionHeader th = TransactionHeaderDAO.get(transactionNumber, transactionCode);
            TransactionDetails td = new TransactionDetails();
            td.setPeriod(period);
            td.setTransactionNumber(transactionNumber);
            td.setTransactionCode(transactionCode);
            td.setCredit(getTotalDebit(period, transactionNumber));
            td.setSequenceNumber(999);
            td.setTransactionDate(th.getTransactionDate());
            //BR or BRSub
            td.setAccountCode(Utility.getAccountCodeProperty());
            TransactionDetailsDAO.add(td);
        }
    }

    public static void delete(TransactionDetails td) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("DELETE FROM TransactionDetails " +
                "WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND AccountSequence=? AND Debit=?");

        ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(2, td.getTransactionNumber());
        ps.setString(3, td.getTransactionCode());
        ps.setInt(4, td.getSequenceNumber());
        ps.setDouble(5, td.getDebit());

        ps.executeUpdate();

        ps.close();

    }
}
