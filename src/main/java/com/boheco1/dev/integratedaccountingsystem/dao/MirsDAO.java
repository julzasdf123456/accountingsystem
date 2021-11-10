package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

public class MirsDAO {
    public static void create(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRS (DateFiled, Purpose, Details, Status, RequisitionerID, UserID, CreatedAt, UpdateAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,?,now(), now())");
        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setInt(5, mirs.getRequisitionerID());
        ps.setInt(6, mirs.getUserID());

        ps.executeUpdate();

        ps.close();

    }

    public static void update(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRS SET " +
                        "DateFiled=?, Purpose=?, Details=?, Status=?, RequisitionerID=?, UpdatedAt=NOW() " +
                        "WHERE id=?");
        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setInt(5, mirs.getRequisitionerID());

        ps.executeUpdate();

        ps.close();
    }

    public static void addMIRSItem(MIRS mirs, MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,NOW(),NOW())");
        ps.setInt(1, mirs.getId());
        ps.setInt(2, item.getStockID());
        ps.setInt(3, item.getQuantity());
        ps.setDouble(4, item.getPrice());
        ps.setString(5, item.getComments());

        ps.executeUpdate();

        ps.close();
    }

    public static void addMIRSItems(MIRS mirs, List<MIRSItem> items) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,NOW(),NOW())");

        for(MIRSItem item: items) {
            ps.setInt(1, mirs.getId());
            ps.setInt(2, item.getStockID());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            ps.setString(5, item.getComments());

            ps.addBatch();
        }

        ps.executeBatch();

        ps.close();
    }

    public static void updateMIRSItem(MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRSItems SET StockID=?, Quantity=?, Price=?, Comments=?, UpdatedAt=NOW() " +
                        "WHERE id=?");
        ps.setInt(1, item.getStockID());
        ps.setInt(2, item.getQuantity());
        ps.setDouble(3, item.getPrice());
        ps.setString(4, item.getComments());

        ps.executeUpdate();

        ps.close();
    }

    public static void removeItem(MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "DELETE FROM MIRSItems WHERE id=?");
        ps.setInt(1, item.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static void removeItems(List<MIRSItem> items) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "DELETE FROM MIRSItems WHERE id=?");

        for(MIRSItem item: items) {
            ps.setInt(1, item.getId());
            ps.addBatch();
        }

        ps.executeBatch();

        ps.close();
    }
}
