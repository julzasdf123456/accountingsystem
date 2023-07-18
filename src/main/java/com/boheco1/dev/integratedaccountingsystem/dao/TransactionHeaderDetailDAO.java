package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;


import java.sql.*;
import java.time.LocalDate;
import java.util.List;

import static com.boheco1.dev.integratedaccountingsystem.helpers.Utility.ERROR_MSG;

public class TransactionHeaderDetailDAO {
    private static PreparedStatement ps1;
    private static PreparedStatement ps2;
    private static PreparedStatement ps3 = null;
    private static PreparedStatement ps4 = null;
    public static void save(CRMQueue crmQueue, TransactionHeader transactionHeader, List<TransactionDetails> tds) throws SQLException, ClassNotFoundException {
        try {
            DB.getConnection().setAutoCommit(false);

            ps1 = DB.getConnection().prepareStatement(
                    "INSERT INTO TransactionHeader " +
                            "(Period, TransactionNumber, TransactionCode, " +
                            "AccountID, Source, Particulars, TransactionDate, " +
                            "Bank, ReferenceNo, Amount, EnteredBy, DateEntered, " +
                            "DateLastModified, UpdatedBy, Remarks, TIN, Name, Address, TransactionLog) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,GETDATE(),GETDATE(),?,?,?,?,?,?)");
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
            //ps1.setTimestamp(12, transactionHeader.getDateEntered()!=null ? Timestamp.valueOf(transactionHeader.getDateEntered()) : null);
            //ps1.setTimestamp(13, transactionHeader.getDateLastModified()!=null ? Timestamp.valueOf(transactionHeader.getDateLastModified()) : null);
            ps1.setString(12, transactionHeader.getUpdatedBy());
            ps1.setString(13, transactionHeader.getRemarks());
            ps1.setString(14, transactionHeader.getTinNo());
            ps1.setString(15, transactionHeader.getName());
            ps1.setString(16, transactionHeader.getAddress());
            ps1.setString(17, transactionHeader.getTransactionLog());

            ps2 =null;
            if(tds!=null && !tds.isEmpty()) {
                ps2 = DB.getConnection().prepareStatement(
                        "INSERT INTO TransactionDetails (" +
                                "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                                "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                                "BankID, Note, CheckNumber, Particulars) " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)");

                for (TransactionDetails td : tds) {
                    System.out.println(td.getParticulars());
                    if(td.getParticulars().contains("TIN") || td.getParticulars().contains("BUSINESS STYLE")) //check from supplier OR will not save the TIN and business style row in the table
                        continue;
                    ps2.setDate(1, Date.valueOf(td.getPeriod()));
                    ps2.setString(2, td.getTransactionNumber());
                    ps2.setString(3, td.getTransactionCode());
                    ps2.setDate(4, Date.valueOf(td.getTransactionDate()));
                    ps2.setInt(5, td.getSequenceNumber());
                    ps2.setString(6, td.getAccountCode());
                    ps2.setDouble(7, Math.abs(td.getDebit()));
                    ps2.setDouble(8, td.getCredit());
                    ps2.setDate(9, Date.valueOf(td.getOrDate()));
                    ps2.setString(10, td.getBankID());
                    ps2.setString(11, td.getNote());
                    ps2.setString(12, td.getCheckNumber());
                    ps2.setString(13, td.getParticulars());

                    ps2.addBatch();
                }
            }

            if(crmQueue!=null){//it will be null if transaction is for teller
                ps3 = DB.getConnection().prepareStatement(
                        "Delete from CRMDetails where ReferenceNo = ?");
                ps3.setString(1, crmQueue.getId());

                ps4 = DB.getConnection().prepareStatement(
                        "Delete from CRMQueue where id = ?");
                ps4.setString(1, crmQueue.getId());
            }

            ERROR_MSG = null;
            ps1.execute();
            if(ps2 != null)//is null if save teller transaction
                ps2.executeBatch();
            if(crmQueue!=null){
                ps3.execute();
                ps4.execute();
            }
            DB.getConnection().setAutoCommit(true);
            ps1.close();
            if(ps2 != null) //is null if save teller transaction
                ps2.close();
            if(crmQueue!=null){
                ps3.close();
                ps4.close();
            }
        } catch (Exception e){
            ERROR_MSG = "Saving from DAO, "+e.getMessage();
            DB.getConnection().rollback();
            DB.getConnection().setAutoCommit(true);
            ps1.close();
            if(ps2 != null) //is null if save teller transaction
                ps2.close();
            if(crmQueue!=null){
                ps3.close();
                ps4.close();
            }
            e.printStackTrace();
        }
    }


    public static void save(List<BatchTransactionInfo> batch) throws SQLException, ClassNotFoundException {

        try{
            DB.getConnection().setAutoCommit(false);
            ps1 = DB.getConnection().prepareStatement(
                    "INSERT INTO TransactionHeader " +
                            "(Period, TransactionNumber, TransactionCode, " +
                            "AccountID, Source, Particulars, TransactionDate, " +
                            "Bank, ReferenceNo, Amount, EnteredBy, DateEntered, " +
                            "DateLastModified, UpdatedBy, Remarks, TIN, Name, Address, TransactionLog) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,GETDATE(),GETDATE(),?,?,?,?,?,?);");

            ps2 = DB.getConnection().prepareStatement(
                    "INSERT INTO TransactionDetails (" +
                            "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                            "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                            "BankID, Note, CheckNumber, Particulars) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
            for(BatchTransactionInfo info : batch){
                TransactionHeader transactionHeader = info.getTransactionHeader();
                List<TransactionDetails> transactionDetails = info.getTransactionDetailsList();

                ps1.setDate(1, (transactionHeader.getPeriod() != null) ? Date.valueOf(transactionHeader.getPeriod()) : null);
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
                ps1.setString(12, transactionHeader.getUpdatedBy());
                ps1.setString(13, transactionHeader.getRemarks());
                ps1.setString(14, transactionHeader.getTinNo());
                ps1.setString(15, transactionHeader.getName());
                ps1.setString(16, transactionHeader.getAddress());
                ps1.setString(17, transactionHeader.getTransactionLog());
                ps1.addBatch();

                for(TransactionDetails td : transactionDetails){
                    ps2.setDate(1, (td.getPeriod() != null) ? Date.valueOf(td.getPeriod()) : null);
                    ps2.setString(2, td.getTransactionNumber());
                    ps2.setString(3, td.getTransactionCode());
                    ps2.setDate(4, Date.valueOf(td.getTransactionDate()));
                    ps2.setInt(5, td.getSequenceNumber());
                    ps2.setString(6, td.getAccountCode());
                    ps2.setDouble(7, Math.abs(td.getDebit()));
                    ps2.setDouble(8, td.getCredit());
                    ps2.setDate(9, Date.valueOf(td.getOrDate()));
                    ps2.setString(10, td.getBankID());
                    ps2.setString(11, td.getNote());
                    ps2.setString(12, td.getCheckNumber());
                    ps2.setString(13, td.getParticulars());
                    ps2.addBatch();
                }
            }
            ERROR_MSG = null;
            ps1.executeBatch();
            ps2.executeBatch();
            DB.getConnection().setAutoCommit(true);
            ps1.close();
            ps2.close();
        } catch (Exception e){
            ERROR_MSG = e.getMessage();
            try {
                DB.getConnection().rollback();
                DB.getConnection().setAutoCommit(true);
                ps1.close();
                ps2.close();
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }

    }

    public static void cancelOR(TransactionHeader transactionHeader) throws SQLException, ClassNotFoundException {
        try{
            DB.getConnection().setAutoCommit(false);
            ps1 = DB.getConnection().prepareStatement(
                    "UPDATE TransactionHeader SET Amount = '0', Name = '"+Utility.CANCELLED+"', DateLastModified = GETDATE(), UpdatedBy = ?, Remarks = ? " +
                            "WHERE " +
                            "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;" +
                            "DELETE FROM TransactionDetails WHERE Period = ? AND TransactionNumber = ? AND TransactionCode = ?;");
            ps1.setString(1, ActiveUser.getUser().getUserName());
            ps1.setString(2, transactionHeader.getRemarks());
            ps1.setDate(3, java.sql.Date.valueOf(transactionHeader.getPeriod()));
            ps1.setString(4, transactionHeader.getTransactionNumber());
            ps1.setString(5, transactionHeader.getTransactionCode());
            ps1.setDate(6, java.sql.Date.valueOf(transactionHeader.getPeriod()));
            ps1.setString(7, transactionHeader.getTransactionNumber());
            ps1.setString(8, transactionHeader.getTransactionCode());
            ps1.executeUpdate();

           /* ps1 = DB.getConnection().prepareStatement(
                    "DELETE FROM TransactionDetails WHERE " +
                            "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;");
            ps1.setDate(1, java.sql.Date.valueOf(transactionHeader.getPeriod()));
            ps1.setString(2, transactionHeader.getTransactionNumber());
            ps1.setString(3, transactionHeader.getTransactionCode());
            ps1.addBatch();*/

            ERROR_MSG = null;
            ps1.executeUpdate();
            DB.getConnection().setAutoCommit(true);
            ps1.close();
            AlertDialogBuilder.messgeDialog("System Message", "Transaction successful.",
                    Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        } catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while process request."+ e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            ERROR_MSG = e.getMessage();
            try {
                DB.getConnection().rollback();
                DB.getConnection().setAutoCommit(true);
                ps1.close();
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
}
