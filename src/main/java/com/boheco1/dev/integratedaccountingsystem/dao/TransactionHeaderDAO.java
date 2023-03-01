package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

    public static void updateRemarks(TransactionHeader th, String remarks) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE TransactionHeader SET remarks=? " +
                        "WHERE TransactionNumber=? AND TransactionCode=? AND Period=?"
        );
        ps.setString(1, remarks);
        ps.setString(2, th.getTransactionNumber());
        ps.setString(3, th.getTransactionCode());
        ps.setDate(4, java.sql.Date.valueOf(th.getPeriod()));

        ps.executeUpdate();
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

    public static void updateTransaction(String transactionNumber, String transactionCode, LocalDate period) throws Exception {
        User activeUser = ActiveUser.getUser();
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE TransactionHeader SET DateLastModified=?, UpdatedBy=? " +
                        "WHERE TransactionNumber=? AND TransactionCode=? AND Period=?"
        );

        ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
        ps.setString(2, activeUser.getUserName());
        ps.setString(3, transactionNumber);
        ps.setString(4, transactionCode);
        ps.setDate(5, java.sql.Date.valueOf(period));

        ps.executeUpdate();

        ps.close();
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

    public static TransactionHeader get(String enteredBy, LocalDate transactionDate) throws Exception {

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionHeader WHERE EnteredBy=? AND TransactionDate=?");
        ps.setString(1, enteredBy);
        ps.setDate(2, java.sql.Date.valueOf(transactionDate));

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
        }

        return null;

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

    public static int getNextORNumber() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT TransactionNumber FROM TransactionHeader WHERE TransactionCode='OR' OR TransactionCode='ORSub' ORDER BY TransactionDate DESC, TransactionNumber DESC");
        if(rs.next()) {
            return rs.getInt("TransactionNumber")+1;
        }else {
            return 0;
        }
    }

    public static boolean isAvailable(LocalDate period, String or_num, String office) throws SQLException, ClassNotFoundException {
        boolean found = false;
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionHeader WHERE Period = ? AND TransactionNumber = ? AND TransactionCode = ?");
        ps.setDate(1, Date.valueOf(period));
        ps.setString(2, or_num);
        ps.setString(3, office);

        ResultSet rs = ps.executeQuery();

        if(rs.next())
            found = true;

        rs.close();
        ps.close();

        return found;
    }

}
