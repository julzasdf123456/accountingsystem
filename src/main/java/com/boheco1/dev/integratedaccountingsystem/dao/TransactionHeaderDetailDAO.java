package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.microsoft.sqlserver.jdbc.SQLServerException;

import java.sql.*;
import java.util.List;

public class TransactionHeaderDetailDAO {
    public static String save(TransactionHeader transactionHeader, List<TransactionDetails> tds) throws SQLException, ClassNotFoundException {

        DB.getConnection().setAutoCommit(false);

        PreparedStatement ps1 = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionHeader " +
                        "(Period, TransactionNumber, TransactionCode, " +
                        "AccountID, Source, Particulars, TransactionDate, " +
                        "Bank, ReferenceNo, Amount, EnteredBy, DateEntered, " +
                        "DateLastModified, UpdatedBy, Remarks) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        ps1.setDate(1, Date.valueOf(transactionHeader.getPeriod()));
        ps1.setString(2, transactionHeader.getTransactionNumber());
        ps1.setString(3, transactionHeader.getTransactionCode());
        ps1.setString(4, transactionHeader.getAccountID());
        ps1.setString(5,transactionHeader.getSource());
        ps1.setString(6, transactionHeader.getParticulars());
        ps1.setDate(7, Date.valueOf(transactionHeader.getTransactionDate()));
        ps1.setString(8, transactionHeader.getBank());
        ps1.setString(9, transactionHeader.getReferenceNo());
        ps1.setDouble(10, transactionHeader.getAmount());
        ps1.setString(11, transactionHeader.getEnteredBy());
        ps1.setTimestamp(12, transactionHeader.getDateEntered()!=null ? Timestamp.valueOf(transactionHeader.getDateEntered()) : null);
        ps1.setTimestamp(13, transactionHeader.getDateLastModified()!=null ? Timestamp.valueOf(transactionHeader.getDateLastModified()) : null);
        ps1.setString(14, transactionHeader.getUpdatedBy());
        ps1.setString(15, transactionHeader.getRemarks());

        PreparedStatement ps2 = DB.getConnection().prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                        "BankID, Note, CheckNumber, Particulars) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

        for(TransactionDetails td: tds) {
            ps2.setDate(1, Date.valueOf(td.getPeriod()));
            ps2.setString(2, td.getTransactionNumber());
            ps2.setString(3, td.getTransactionCode());
            ps2.setDate(4, Date.valueOf(td.getTransactionDate()));
            ps2.setInt(5, td.getSequenceNumber());
            ps2.setString(6, td.getAccountCode());
            ps2.setDouble(7, td.getDebit());
            ps2.setDouble(8, td.getCredit());
            ps2.setDate(9, Date.valueOf(td.getOrDate()));
            ps2.setString(10, td.getBankID());
            ps2.setString(11, td.getNote());
            ps2.setString(12, td.getCheckNumber());
            ps2.setString(13,td.getParticulars());

            ps2.addBatch();
        }
        String msg="";
        try {
            ps1.executeUpdate();
            ps2.executeBatch();
            DB.getConnection().setAutoCommit(true);
            ps1.close();
            ps2.close();
        } catch (Exception e){
            DB.getConnection().rollback();
            DB.getConnection().setAutoCommit(true);
            e.printStackTrace();
            msg = e.getMessage();
        }
        return msg;
    }
}
