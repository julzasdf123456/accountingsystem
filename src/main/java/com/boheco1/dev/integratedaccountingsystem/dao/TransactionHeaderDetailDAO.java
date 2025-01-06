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
    private static PreparedStatement ps5=null;

    private static PreparedStatement ps6=null;

    public static void save(CRMQueue crmQueue, TransactionHeader transactionHeader, List<TransactionDetails> tds) throws SQLException, ClassNotFoundException {
        try {
            DB.getConnection().setAutoCommit(false);
            DB.getConnection2().setAutoCommit(false);

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

            //added by jcgaudicos saving the same transaction to TransactionMaster and TransactionDetails in B1Accounting
            //Start
            ps5 = DB.getConnection2().prepareStatement(
                    "INSERT INTO TransactionMaster " +
                            "(Period, TransactionCode, TransactionNumber, " +
                            "TransactionDate, Particulars, EnteredBy, " +
                            "Save01, Save02, Save05, Save26, Save79,Save84,Save85,Save86) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
            ps5.setDate(1, Date.valueOf(transactionHeader.getPeriod()));
            ps5.setString(2, transactionHeader.getTransactionCode());
            ps5.setString(3, transactionHeader.getTransactionNumber());
            ps5.setDate(4, Date.valueOf(transactionHeader.getTransactionDate()));
            ps5.setString(5, transactionHeader.getName());
            ps5.setString(6, transactionHeader.getEnteredBy());
            ps5.setString(7, transactionHeader.getName());
            ps5.setString(8, transactionHeader.getAddress());
            ps5.setString(9, transactionHeader.getTinNo());
            ps5.setDouble(10, transactionHeader.getAmount());
            ps5.setDouble(11, transactionHeader.getAmount());
            ps5.setString(12, "");
            ps5.setString(13, "");
            ps5.setString(14, "");

            ps6 =null;
            if(tds!=null && !tds.isEmpty()) {
                ps6 = DB.getConnection2().prepareStatement(
                        "INSERT INTO TransactionDetails (" +
                                "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                                "Sequence, AccountCode, Debit, Credit) " +
                                "VALUES (?,?,?,?,?,?,?,?)");

                for (TransactionDetails td : tds) {
                    if(td.getParticulars().contains("TIN") || td.getParticulars().contains("BUSINESS STYLE")) //check from supplier OR will not save the TIN and business style row in the table
                        continue;
                    ps6.setDate(1, Date.valueOf(td.getPeriod()));
                    ps6.setString(2, td.getTransactionNumber());
                    ps6.setString(3, td.getTransactionCode());
                    ps6.setDate(4, Date.valueOf(td.getTransactionDate()));
                    ps6.setInt(5, td.getSequenceNumber());
                    ps6.setString(6, td.getAccountCode());
                    ps6.setString(7, Utility.formatNumberTwoDecimal(td.getDebit()) );
                    ps6.setString(8, Utility.formatNumberTwoDecimal(td.getCredit()));

                    ps6.addBatch();
                }
            }

            //End



            ERROR_MSG = null;
            ps1.execute();
           // ps5.execute();
            System.out.println(ps5.execute());
            if(ps2 != null)//is null if save teller transaction
                ps2.executeBatch();
                ps6.executeBatch();
            if(crmQueue!=null){
                ps3.execute();
                ps4.execute();
            }
            DB.getConnection().setAutoCommit(true);
            DB.getConnection2().setAutoCommit(true);
            ps1.close();
           ps5.close();

            if(ps2 != null) //is null if save teller transaction
                ps2.close();
               ps6.close();
            if(crmQueue!=null){
                ps3.close();
                ps4.close();
            }


        } catch (Exception e){
            ERROR_MSG = "Saving from DAO, "+e.getMessage();
            DB.getConnection().rollback();
            DB.getConnection().setAutoCommit(true);
            DB.getConnection2().rollback();
            DB.getConnection2().setAutoCommit(true);
            ps1.close();
            ps5.close();
            if(ps2 != null) //is null if save teller transaction
                ps2.close();
                ps6.close();
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

           //added by jcgaudicos for B1Accounting
            ps5=null;
            ps6=null;
                DB.getConnection2().setAutoCommit(false);
                ps5 = DB.getConnection2().prepareStatement(
                        "INSERT INTO TransactionMaster " +
                                "(Period, TransactionNumber, TransactionCode, " +
                                "Particulars, TransactionDate, " +
                                "Save01, Save02, save05, " +
                                "Save26, Save79,EnteredBy,Save84,Save85,Save86 " +
                                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

                ps6 = DB.getConnection2().prepareStatement(
                        "INSERT INTO TransactionDetails (" +
                                "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                                "Sequence, AccountCode, Debit, Credit) " +
                                "VALUES (?,?,?,?,?,?,?,?);");
                for(BatchTransactionInfo info : batch){
                    TransactionHeader transactionHeader = info.getTransactionHeader();
                    List<TransactionDetails> transactionDetails = info.getTransactionDetailsList();

                    ps5.setDate(1, (transactionHeader.getPeriod() != null) ? Date.valueOf(transactionHeader.getPeriod()) : null);
                    ps5.setString(2, transactionHeader.getTransactionNumber());
                    ps5.setString(3, transactionHeader.getTransactionCode());
                    ps5.setString(4, transactionHeader.getParticulars());
                    ps5.setDate(5, Date.valueOf(transactionHeader.getTransactionDate()));
                    ps5.setString(6, transactionHeader.getName());
                    ps5.setString(7, transactionHeader.getAddress());
                    ps5.setString(8, transactionHeader.getTinNo());
                    ps5.setDouble(9, transactionHeader.getAmount());
                    ps5.setDouble(10, transactionHeader.getAmount());
                    ps5.setString(11, transactionHeader.getEnteredBy());
                    ps5.setString(12, "");
                    ps5.setString(13, "");
                    ps5.setString(14, "");

                    ps5.addBatch();

                    for(TransactionDetails td : transactionDetails){
                        ps6.setDate(1, (td.getPeriod() != null) ? Date.valueOf(td.getPeriod()) : null);
                        ps6.setString(2, td.getTransactionNumber());
                        ps6.setString(3, td.getTransactionCode());
                        ps6.setDate(4, Date.valueOf(td.getTransactionDate()));
                        ps6.setInt(5, td.getSequenceNumber());
                        ps6.setString(6, td.getAccountCode());
                        ps6.setString(7, Utility.formatNumberTwoDecimal(td.getDebit()) );
                        ps6.setString(8, Utility.formatNumberTwoDecimal(td.getCredit()) );

                        ps6.addBatch();
                    }
                }
           //end



            ERROR_MSG = null;
            ps1.executeBatch();
            ps2.executeBatch();
            ps5.executeBatch();
            ps6.executeBatch();
            DB.getConnection().setAutoCommit(true);
            DB.getConnection2().setAutoCommit(true);
            ps1.close();
            ps2.close();
            ps5.close();
            ps6.close();
        } catch (Exception e){
            ERROR_MSG = e.getMessage();
            try {
                DB.getConnection().rollback();
                DB.getConnection().setAutoCommit(true);
                ps1.close();
                ps2.close();
                DB.getConnection2().rollback();
                DB.getConnection2().setAutoCommit(true);
                ps5.close();
                ps6.close();
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


            //added by jcgaudicos for B1Accounting
                DB.getConnection2().setAutoCommit(false);
                ps5 = DB.getConnection2().prepareStatement(
                        "UPDATE TransactionMaster SET Save26 = '0',Save79 = '0', Particulars = '"+Utility.CANCELLED+"', Save01 = '"+Utility.CANCELLED+"', DateLastChanged = GETDATE(), ChangedBy = ? " +
                                "WHERE " +
                                "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;" +
                                "DELETE FROM TransactionDetails WHERE Period = ? AND TransactionNumber = ? AND TransactionCode = ?;");
                ps5.setString(1, ActiveUser.getUser().getUserName());
                ps5.setDate(2, java.sql.Date.valueOf(transactionHeader.getPeriod()));
                ps5.setString(3, transactionHeader.getTransactionNumber());
                ps5.setString(4, transactionHeader.getTransactionCode());
                ps5.setDate(5, java.sql.Date.valueOf(transactionHeader.getPeriod()));
                ps5.setString(6, transactionHeader.getTransactionNumber());
                ps5.setString(7, transactionHeader.getTransactionCode());
                ps5.executeUpdate();
            //end


           /* ps1 = DB.getConnection().prepareStatement(
                    "DELETE FROM TransactionDetails WHERE " +
                            "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;");
            ps1.setDate(1, java.sql.Date.valueOf(transactionHeader.getPeriod()));
            ps1.setString(2, transactionHeader.getTransactionNumber());
            ps1.setString(3, transactionHeader.getTransactionCode());
            ps1.addBatch();*/

            ERROR_MSG = null;
            ps1.executeUpdate();
            ps5.executeUpdate();
            DB.getConnection().setAutoCommit(true);
            DB.getConnection2().setAutoCommit(true);
            ps1.close();
            ps5.close();
            AlertDialogBuilder.messgeDialog("System Message", "Transaction successful.",
                    Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        } catch (Exception e){
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while process request."+ e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            ERROR_MSG = e.getMessage();
            try {
                DB.getConnection().rollback();
                DB.getConnection().setAutoCommit(true);
                DB.getConnection2().rollback();
                DB.getConnection2().setAutoCommit(true);
                ps1.close();
                ps5.close();
            } catch (SQLException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
            e.printStackTrace();
        }
    }
}
