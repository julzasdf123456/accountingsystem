package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Receiving;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceivingDAO {
    public static void add(Receiving receiving) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO Receiving " +
                "(RRNo, Date, RVNo, BLWBNo, Carrier, DRNo, PONo, SupplierID, InvoiceNo, ReceivedBy, ReceivedOrigBy, VerifiedBy) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");

        ps.setString(1, generateRRNo());
        ps.setDate(2, Date.valueOf(receiving.getDate()));
        ps.setString(3, receiving.getRvNo());
        ps.setString(4, receiving.getBlwbNo());
        ps.setString(5, receiving.getCarrier());
        ps.setString(6, receiving.getDrNo());
        ps.setString(7, receiving.getPoNo());
        ps.setString(8, receiving.getSupplierId());
        ps.setString(9, receiving.getInvoiceNo());
        ps.setString(10, receiving.getReceivedBy());
        ps.setString(11, receiving.getReceivedOrigBy());
        ps.setString(12, receiving.getVerifiedBy());

        ps.executeUpdate();

        ps.close();
    }

    public static Receiving get(String rrNo) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Receiving WHERE RRNo=?");
        ps.setString(1, rrNo);
        ResultSet rs = ps.executeQuery();

        Receiving receiving = null;

        if(rs.next()) {
            receiving = new Receiving(
                    rs.getString("RRNo"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getString("RVNo"),
                    rs.getString("BLWBNo"),
                    rs.getString("Carrier"),
                    rs.getString("DRNo"),
                    rs.getString("PONo"),
                    rs.getString("SupplierID"),
                    rs.getString("InvoiceNo"),
                    rs.getString("ReceivedBy"),
                    rs.getString("ReceivedOrigBy"),
                    rs.getString("VerifiedBy")
            );
        }

        rs.close();

        return receiving;
    }

    public static List<Receiving> getByDateRange(LocalDate from, LocalDate to) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Receiving " +
                "WHERE Date BETWEEN ? AND ? ORDER BY Date ASC");
        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));

        ResultSet rs = ps.executeQuery();

        ArrayList<Receiving> receivings = new ArrayList<>();

        while(rs.next()) {
            receivings.add(new Receiving(
                    rs.getString("RRNo"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getString("RVNo"),
                    rs.getString("BLWBNo"),
                    rs.getString("Carrier"),
                    rs.getString("DRNo"),
                    rs.getString("PONo"),
                    rs.getString("SupplierID"),
                    rs.getString("InvoiceNo"),
                    rs.getString("ReceivedBy"),
                    rs.getString("ReceivedOrigBy"),
                    rs.getString("VerifiedBy")
            ));
        }

        rs.close();

        return receivings;
    }

    private static String generateRRNo() throws Exception {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String newRRNo = null;

         PreparedStatement ps = DB.getConnection().prepareStatement("SELECT RRNo FROM Receiving " +
                 "WHERE RRNo LIKE '?%' ORDER BY RRNo DESC");
         ps.setInt(1, year);
         ResultSet rs = ps.executeQuery();
         if(rs.next()) {
             String rrNo = rs.getString("RRNo");
             String parsed = rrNo.substring(rrNo.indexOf('-'));
             try {
                 int serial = Integer.parseInt(parsed);
                 return year + "-" + ++serial;
             }catch(NumberFormatException ex) {
                 return year + "0001";
             }
         }

         rs.close();

         return year + "0001";
    }
}
