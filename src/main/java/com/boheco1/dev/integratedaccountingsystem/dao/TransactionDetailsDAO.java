package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TransactionDetailsDAO {
    public static Connection connection,connection1;
    public static void add(TransactionDetails td) throws Exception {
        Connection cxn = DB.getConnection();
        cxn.setAutoCommit(true);
        PreparedStatement ps = cxn.prepareStatement(
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

        // added by jcgaudicos for B1Accounting
        Connection con = DB.getConnection2();
        con.setAutoCommit(true);
        PreparedStatement ps1 = con.prepareStatement(
                "INSERT INTO TransactionDetails (" +
                        "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                        "Sequence, AccountCode, Debit, Credit) " +
                        "VALUES (?,?,?,?,?,?,?,?)");
        ps1.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps1.setString(2, td.getTransactionNumber());
        ps1.setString(3, td.getTransactionCode());
        ps1.setDate(4, td.getTransactionDate() !=null ? java.sql.Date.valueOf(td.getTransactionDate()):null);
        ps1.setInt(5, td.getSequenceNumber());
        ps1.setString(6, td.getAccountCode());
        ps1.setString(7, Utility.formatNumberTwoDecimal(td.getDebit()));
        ps1.setString(8, Utility.formatNumberTwoDecimal(td.getCredit()));

        ps1.executeUpdate();

        ps1.close();

        //end

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


        //added by jcgaudicos
                PreparedStatement ps1 = DB.getConnection2().prepareStatement(
                        "INSERT INTO TransactionDetails (" +
                                "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                                "Sequence, AccountCode, Debit, Credit) " +
                                "VALUES (?,?,?,?,?,?,?,?)");

                DB.getConnection2().setAutoCommit(false);

                for(TransactionDetails td1: tds) {
                    ps1.setDate(1, java.sql.Date.valueOf(td1.getPeriod()));
                    ps1.setString(2, td1.getTransactionNumber());
                    ps1.setString(3, td1.getTransactionCode());
                    ps1.setDate(4, java.sql.Date.valueOf(td1.getTransactionDate()));
                    ps1.setInt(5, td1.getSequenceNumber());
                    ps1.setString(6, td1.getAccountCode());
                    ps1.setString(7, Utility.formatNumberTwoDecimal(td1.getDebit()));
                    ps1.setString(8, Utility.formatNumberTwoDecimal(td1.getCredit()));


                    ps1.addBatch();
                }

                ps1.executeBatch();
                ps1.getConnection().setAutoCommit(true);

                ps1.close();

        //end

    }
    private static PreparedStatement psAdd, psUpdate, psUpdateTransHeader,psAdd1, psUpdate1, psUpdateTransHeader1;
    public static void addUpdate(TransactionHeader transactionHeader, List<TransactionDetails> updateRecord, List<TransactionDetails> newRecord) throws Exception {
        try {
            connection = DB.getConnection();
            connection.setAutoCommit(true);
            psUpdateTransHeader = connection.prepareStatement(
                    "UPDATE TransactionHeader SET DateLastModified = GETDATE(), UpdatedBy = ? " +
                            "WHERE " +
                            "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;");
            psUpdateTransHeader.setString(1, ActiveUser.getUser().getUserName());
            psUpdateTransHeader.setDate(2, java.sql.Date.valueOf(transactionHeader.getPeriod()));
            psUpdateTransHeader.setString(3, transactionHeader.getTransactionNumber());
            psUpdateTransHeader.setString(4, transactionHeader.getTransactionCode());

            PreparedStatement psAdd = connection.prepareStatement(
                    "INSERT INTO TransactionDetails (" +
                            "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                            "AccountSequence, AccountCode, Debit, Credit, ORDate, " +
                            "BankID, Note, CheckNumber, Particulars, DepositedDate) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            PreparedStatement psDelete = connection.prepareStatement("Delete From TransactionDetails WHERE TransactionNumber='" + transactionHeader.getTransactionNumber() + "' and Period='"+ java.sql.Date.valueOf(transactionHeader.getPeriod()) +"'  and TransactionCode='"+ transactionHeader.getTransactionCode()  +"' ");

            psUpdateTransHeader.execute();
            psUpdateTransHeader.close();
            psDelete.execute();
            psDelete.close();
            int sequence = updateRecord.size()+2;

            TransactionDetails univTd;
            if (newRecord != null && !newRecord.isEmpty()) {
                univTd = newRecord.get(0);
            } else {
                if (updateRecord != null && !updateRecord.isEmpty()) {
                    univTd = updateRecord.get(0);
                } else {
                    univTd = null;
                }
            }

            double totalAmnt = 0.0;

            for (TransactionDetails td : newRecord) {
                psAdd.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
                psAdd.setString(2, td.getTransactionNumber());
                psAdd.setString(3, td.getTransactionCode());
                psAdd.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
                psAdd.setInt(5, sequence);
                psAdd.setString(6, td.getAccountCode());
                psAdd.setDouble(7, Math.abs(td.getDebit()));
                psAdd.setDouble(8, td.getCredit());
                psAdd.setDate(9, td.getOrDate() == null ? null : java.sql.Date.valueOf(td.getOrDate()));
                psAdd.setString(10, td.getBankID());
                psAdd.setString(11, td.getNote());
                psAdd.setString(12, td.getCheckNumber());
                psAdd.setString(13, td.getParticulars());
                psAdd.setDate(14, td.getDepositedDate() == null ? null : java.sql.Date.valueOf(td.getDepositedDate()));
                System.out.println(td);

                totalAmnt += td.getAmount();

                psAdd.execute();
                psAdd.clearParameters();

                sequence++;
            }

            for(TransactionDetails td: updateRecord) {
                psAdd.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
                psAdd.setString(2, td.getTransactionNumber());
                psAdd.setString(3, td.getTransactionCode());
                psAdd.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
                psAdd.setInt(5, sequence);
                psAdd.setString(6, td.getAccountCode());
                psAdd.setDouble(7, Math.abs(td.getDebit()));
                psAdd.setDouble(8, td.getCredit());
                psAdd.setDate(9, td.getOrDate()==null?null:java.sql.Date.valueOf(td.getOrDate()));
                psAdd.setString(10, td.getBankID());
                psAdd.setString(11, td.getNote());
                psAdd.setString(12, td.getCheckNumber());
                psAdd.setString(13,td.getParticulars());
                psAdd.setDate(14, td.getDepositedDate()==null ? null : java.sql.Date.valueOf(td.getDepositedDate()));
                System.out.println(td);

                totalAmnt += td.getAmount();

//                    psAdd.addBatch();
                psAdd.execute();
                psAdd.clearParameters();

                sequence++;
            }

            // INSERT GRAND TOTAL
            if (univTd != null) {
                psAdd.setDate(1, java.sql.Date.valueOf(univTd.getPeriod()));
                psAdd.setString(2, univTd.getTransactionNumber());
                psAdd.setString(3, univTd.getTransactionCode());
                psAdd.setDate(4, java.sql.Date.valueOf(univTd.getTransactionDate()));
                psAdd.setInt(5, sequence);
                psAdd.setString(6, Utility.getAccountCodeProperty());
                psAdd.setDouble(7, Math.abs(totalAmnt));
                psAdd.setDouble(8, 0);
                psAdd.setDate(9, univTd.getOrDate() == null ? null : java.sql.Date.valueOf(univTd.getOrDate()));
                psAdd.setString(10, univTd.getBankID());
                psAdd.setString(11, univTd.getNote());
                psAdd.setString(12, univTd.getCheckNumber());
                psAdd.setString(13, "Grand Total");
                psAdd.setDate(14, univTd.getDepositedDate() == null ? null : java.sql.Date.valueOf(univTd.getDepositedDate()));
                System.out.println(univTd);

                psAdd.execute();
                psAdd.clearParameters();
            }


            psAdd.close();

            PreparedStatement psDeleteZero = connection.prepareStatement("Delete From TransactionDetails WHERE TransactionNumber='" + transactionHeader.getTransactionNumber() + "'  and Period='"+ java.sql.Date.valueOf(transactionHeader.getPeriod()) +"'  and TransactionCode='"+ transactionHeader.getTransactionCode()  +"'  AND Debit=0 AND Credit=0");

            psDeleteZero.execute();
            psDeleteZero.close();

            //added by jcgaudicos for B1Accounting

                    connection1 = DB.getConnection2();
                    connection1.setAutoCommit(true);
                    psUpdateTransHeader1 = connection1.prepareStatement(
                            "UPDATE TransactionMaster SET DateLastChanged = GETDATE(), ChangedBy = ? " +
                                    "WHERE " +
                                    "Period = ? AND TransactionNumber = ? AND TransactionCode = ? ;");
                    psUpdateTransHeader1.setString(1, ActiveUser.getUser().getUserName());
                    psUpdateTransHeader1.setDate(2, java.sql.Date.valueOf(transactionHeader.getPeriod()));
                    psUpdateTransHeader1.setString(3, transactionHeader.getTransactionNumber());
                    psUpdateTransHeader1.setString(4, transactionHeader.getTransactionCode());

                    PreparedStatement psAdd1 = connection1.prepareStatement(
                            "INSERT INTO TransactionDetails (" +
                                    "Period, TransactionNumber, TransactionCode, TransactionDate, " +
                                    "Sequence, AccountCode, Debit, Credit) " +
                                    "VALUES (?,?,?,?,?,?,?,?)");

                    PreparedStatement psDelete1 = connection1.prepareStatement("Delete From TransactionDetails WHERE TransactionNumber='" + transactionHeader.getTransactionNumber() + "' and Period='"+ java.sql.Date.valueOf(transactionHeader.getPeriod()) +"'  and TransactionCode='"+ transactionHeader.getTransactionCode()  +"' ");

                    psUpdateTransHeader1.execute();
                    psUpdateTransHeader1.close();
                    psDelete1.execute();
                    psDelete1.close();
                    int sequence1 = updateRecord.size()+2;

                    TransactionDetails univTd1;
                    if (newRecord != null && !newRecord.isEmpty()) {
                        univTd1 = newRecord.get(0);
                    } else {
                        if (updateRecord != null && !updateRecord.isEmpty()) {
                            univTd1 = updateRecord.get(0);
                        } else {
                            univTd1 = null;
                        }
                    }

                    double totalAmnt1 = 0.0;

                    for (TransactionDetails td : newRecord) {
                        psAdd1.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
                        psAdd1.setString(2, td.getTransactionNumber());
                        psAdd1.setString(3, td.getTransactionCode());
                        psAdd1.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
                        psAdd1.setInt(5, sequence1);
                        psAdd1.setString(6, td.getAccountCode());
                        psAdd1.setString(7, Utility.formatNumberTwoDecimal(td.getDebit()));
                        psAdd1.setString(8, Utility.formatNumberTwoDecimal(td.getCredit()));

                        System.out.println(td);

                        totalAmnt1 += td.getAmount();

                        psAdd1.execute();
                        psAdd1.clearParameters();

                        sequence1++;
                    }

                    for(TransactionDetails td: updateRecord) {
                        psAdd1.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
                        psAdd1.setString(2, td.getTransactionNumber());
                        psAdd1.setString(3, td.getTransactionCode());
                        psAdd1.setDate(4, java.sql.Date.valueOf(td.getTransactionDate()));
                        psAdd1.setInt(5, sequence1);
                        psAdd1.setString(6, td.getAccountCode());
                        psAdd1.setString(7, Utility.formatNumberTwoDecimal(td.getDebit()));
                        psAdd1.setString(8, Utility.formatNumberTwoDecimal(td.getCredit()));

                        System.out.println(td);

                        totalAmnt1 += td.getAmount();

        //                    psAdd.addBatch();
                        psAdd1.execute();
                        psAdd1.clearParameters();

                        sequence1++;
                    }

                    // INSERT GRAND TOTAL
                    if (univTd != null) {
                        psAdd1.setDate(1, java.sql.Date.valueOf(univTd.getPeriod()));
                        psAdd1.setString(2, univTd.getTransactionNumber());
                        psAdd1.setString(3, univTd.getTransactionCode());
                        psAdd1.setDate(4, java.sql.Date.valueOf(univTd.getTransactionDate()));
                        psAdd1.setInt(5, sequence);
                        psAdd1.setString(6, Utility.getAccountCodeProperty());
                        psAdd1.setString(7, Utility.formatNumberTwoDecimal(totalAmnt1));
                        psAdd1.setDouble(8, 0);

                        System.out.println(univTd);

                        psAdd1.execute();
                        psAdd1.clearParameters();
                    }


                    psAdd1.close();

                    PreparedStatement psDeleteZero1 = connection1.prepareStatement("Delete From TransactionDetails WHERE TransactionNumber='" + transactionHeader.getTransactionNumber() + "'  and Period='"+ java.sql.Date.valueOf(transactionHeader.getPeriod()) +"'  and TransactionCode='"+ transactionHeader.getTransactionCode()  +"'  AND Debit=0 AND Credit=0");

                    psDeleteZero1.execute();
                    psDeleteZero1.close();

            //End




        } catch (Exception e){
            Utility.ERROR_MSG = e.getMessage();

            e.printStackTrace();
        }
    }



    public static List<TransactionDetails> getAR(LocalDate transactionDate, String transactionNumber) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE TransactionDate=? AND TransactionNumber=? " +
                        "AND TransactionCode='AR' AND AccountSequence!=999");
        ps.setDate(1, java.sql.Date.valueOf(transactionDate));
        ps.setString(2, transactionNumber);

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

    public static List<TransactionDetails> get(LocalDate date, String transactionCode, User userId, String tellerID) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                    "Select * from TransactionDetails INNER JOIN TransactionHeader ON TransactionHeader.TransactionNumber = TransactionDetails.TransactionNumber WHERE " +
                            "TransactionHeader.EnteredBy = ? AND " +
                            "TransactionHeader.Source = 'EmployeeInfo' AND " +
                            "TransactionHeader.TransactionCode ='OR' AND " +
                            "TransactionDetails.TransactionDate = ? AND " +
                            "TransactionDetails.TransactionCode = ? AND " +
                            "TransactionHeader.AccountID = ? " +
                            "ORDER by AccountSequence ASC");

        ps.setString(1, userId.getUserName());
        ps.setDate(2, java.sql.Date.valueOf(date));
        ps.setString(3, transactionCode);
        ps.setString(4, tellerID);

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
            td.setDebit(rs.getDouble("Debit")*-1);
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

    public static List<TransactionDetails> get(String transactionNumber, String transactionCode, LocalDate transactionDate) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM TransactionDetails WHERE TransactionNumber=? AND TransactionCode = ? AND TransactionDate=?  AND Particulars!='Grand Total'");
        ps.setString(1, transactionNumber);
        ps.setString(2, transactionCode);
        ps.setDate(3, java.sql.Date.valueOf(transactionDate));

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
            td.setDebit(rs.getDouble("Debit")*-1);
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

    public static int getNextSequenceNumber(LocalDate period, String transactionNumber,String transactionCode) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT MAX(AccountSequence) FROM TransactionDetails td WHERE  Period=? AND TransactionCode =? AND TransactionNumber = ? AND AccountSequence <> 999;");
        ps.setDate(1, java.sql.Date.valueOf(period));
        ps.setString(2, transactionCode);
        ps.setString(3, transactionNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();

        return rs.getInt(1) + 1;
    }

    public static double getTotalDebit(String transactionNumber) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(td.Debit) FROM TransactionDetails td WHERE td.TransactionCode=? AND td.TransactionNumber=? AND td.Credit=0");
        ps.setString(1, TransactionHeader.getBRTransactionCodeProperty());
        ps.setString(2, transactionNumber);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getDouble(1);
    }

    public static void syncDebit(LocalDate period, String transactionNumber, String transactionCode) throws Exception {
        Connection cxn = DB.getConnection();
        cxn.setAutoCommit(true);
        PreparedStatement psx = cxn.prepareStatement("SELECT * FROM TransactionDetails td WHERE td.Period=? AND td.TransactionNumber=? AND td.TransactionCode=? AND td.Debit=0");
        psx.setDate(1, java.sql.Date.valueOf(period));
        psx.setString(2, transactionNumber);
        psx.setString(3, transactionCode);
        ResultSet rs = psx.executeQuery();



        if(rs.next()) {
            PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE TransactionDetails SET Credit=? WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND Debit=0 AND AccountSequence=999");
            ps.setDouble(1, getTotalDebit(transactionNumber));
            ps.setDate(2, java.sql.Date.valueOf(period));
            ps.setString(3, transactionNumber);
            ps.setString(4, transactionCode);
            ps.executeUpdate();
            ps.close();


            //added by jccgaudicos for B1Accounting
            PreparedStatement ps1 = DB.getConnection2().prepareStatement("UPDATE TransactionDetails SET Credit=? WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND Debit=0 AND Sequence=999");
            ps1.setDouble(1, getTotalDebit(transactionNumber));
            ps1.setDate(2, java.sql.Date.valueOf(period));
            ps1.setString(3, transactionNumber);
            ps1.setString(4, transactionCode);
            ps1.executeUpdate();
            ps1.close();


        }else {
            TransactionHeader th = TransactionHeaderDAO.get(transactionNumber, transactionCode);
            System.out.println(transactionNumber + " - " + transactionCode);
            TransactionDetails td = new TransactionDetails();
            td.setPeriod(period);
            td.setTransactionNumber(transactionNumber);
            td.setTransactionCode(transactionCode);
            td.setCredit(getTotalDebit(transactionNumber));
            td.setSequenceNumber(999);
            td.setTransactionDate(th.getTransactionDate());
            //BR or BRSub
            td.setAccountCode(Utility.getAccountCodeProperty());
            TransactionDetailsDAO.add(td);
        }


    }

    public static void syncAR(TransactionHeader th, double amount) throws Exception {
        Connection cxn = DB.getConnection();
        cxn.setAutoCommit(true);

        //Delete current debit entry from TransactionDetails if exists...
        PreparedStatement psx = cxn.prepareStatement("DELETE FROM TransactionDetails WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND AccountSequence=?");
        psx.setDate(1, java.sql.Date.valueOf(th.getPeriod()));
        psx.setString(2, th.getTransactionNumber());
        psx.setString(3, th.getTransactionCode());
        psx.setInt(4, 999);
        psx.executeUpdate();
        psx.close();

        //added by jcgaudicos for B1Accounting
        Connection con = DB.getConnection2();
        con.setAutoCommit(true);

        PreparedStatement ps = con.prepareStatement("DELETE FROM TransactionDetails WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND Sequence=?");
        ps.setDate(1, java.sql.Date.valueOf(th.getPeriod()));
        ps.setString(2, th.getTransactionNumber());
        ps.setString(3, th.getTransactionCode());
        ps.setInt(4, 999);
        ps.executeUpdate();
        ps.close();


        //Insert new Debit entry to TransactionDetails...
        TransactionDetails td = new TransactionDetails();
        td.setPeriod(th.getPeriod());
        td.setTransactionNumber(th.getTransactionNumber());
        td.setTransactionCode(th.getTransactionCode());
        td.setDebit(amount);
        td.setSequenceNumber(999);
        td.setTransactionDate(th.getTransactionDate());
        td.setAccountCode(Utility.getAccountCodeProperty());
        TransactionDetailsDAO.add(td);
    }

    public static void delete(TransactionDetails td) throws Exception {
        DB.getConnection().setAutoCommit(true);
        PreparedStatement ps = DB.getConnection().prepareStatement("DELETE FROM TransactionDetails " +
                "WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND AccountSequence=? AND Debit=?");

        ps.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(2, td.getTransactionNumber());
        ps.setString(3, td.getTransactionCode());
        ps.setInt(4, td.getSequenceNumber());
        ps.setDouble(5, td.getDebit());

        ps.executeUpdate();

        ps.close();

        //added by jcgaudicos for B1Accounting
        DB.getConnection2().setAutoCommit(true);
        PreparedStatement ps1 = DB.getConnection2().prepareStatement("DELETE FROM TransactionDetails " +
                "WHERE Period=? AND TransactionNumber=? AND TransactionCode=? AND Sequence=? AND Debit=?");

        ps1.setDate(1, java.sql.Date.valueOf(td.getPeriod()));
        ps1.setString(2, td.getTransactionNumber());
        ps1.setString(3, td.getTransactionCode());
        ps1.setInt(4, td.getSequenceNumber());
        ps1.setDouble(5, td.getDebit());

        ps1.executeUpdate();

        ps1.close();

    }

    public static void deleteByTransactionHeader(TransactionHeader th) throws Exception {
        DB.getConnection().setAutoCommit(true);
        PreparedStatement ps = DB.getConnection().prepareStatement("DELETE FROM TransactionDetails WHERE Period=? AND TransactionNumber=? AND TransactionCode=?");
        ps.setDate(1, java.sql.Date.valueOf(th.getPeriod()));
        ps.setString(2, th.getTransactionNumber());
        ps.setString(3, th.getTransactionCode());
        ps.executeUpdate();
        ps.close();


        //added by jcgaudicos for B1Accounting
        DB.getConnection2().setAutoCommit(true);
        PreparedStatement ps1 = DB.getConnection2().prepareStatement("DELETE FROM TransactionDetails WHERE Period=? AND TransactionNumber=? AND TransactionCode=?");
        ps1.setDate(1, java.sql.Date.valueOf(th.getPeriod()));
        ps1.setString(2, th.getTransactionNumber());
        ps1.setString(3, th.getTransactionCode());
        ps1.executeUpdate();
        ps1.close();
    }

    public static void updateBR(TransactionDetails td, BankRemittance br) throws Exception {
        DB.getConnection().setAutoCommit(true);
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE TransactionDetails SET " +
                "DepositedDate=?, BankID=?, AccountCode=?, CheckNumber=?, Debit=? " +
                "WHERE Period=? AND TransactionNumber=? AND AccountSequence=? AND TransactionCode=?");
        ps.setDate(1, java.sql.Date.valueOf(br.getDepositedDate()));
        ps.setString(2, br.getBankAccount().getId());
        ps.setString(3, br.getAccountCode());
        ps.setString(4, br.getCheckNumber());
        ps.setDouble(5, br.getAmount());
        ps.setDate(6, java.sql.Date.valueOf(td.getPeriod()));
        ps.setString(7, td.getTransactionNumber());
        ps.setInt(8, td.getSequenceNumber());
        //ps.setString(9, "BR");
        ps.setString(9, td.getTransactionCode());



        ps.executeUpdate();

        ps.close();

        //added by jcgaudicos for B1Accounting
            DB.getConnection2().setAutoCommit(true);
            PreparedStatement ps1 = DB.getConnection2().prepareStatement("UPDATE TransactionDetails SET " +
                    "AccountCode=?, Debit=? " +
                    "WHERE Period=? AND TransactionNumber=? AND Sequence=? AND TransactionCode=?");



            ps1.setString(1, br.getAccountCode());
            ps1.setString(2, Utility.formatNumberTwoDecimal(br.getAmount()));
            ps1.setDate(3, java.sql.Date.valueOf(td.getPeriod()));
            ps1.setString(4, td.getTransactionNumber());
            ps1.setInt(5, td.getSequenceNumber());
            //ps1.setString(6, "BR");
            ps1.setString(6,td.getTransactionCode());

            ps1.executeUpdate();

            ps1.close();

    }
}
