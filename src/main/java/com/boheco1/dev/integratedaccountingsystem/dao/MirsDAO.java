package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MirsDAO {

    /**
     * Creates a new MIRS record
     * @param mirs The MIRS file to be created
     * @throws Exception
     */
    public static void create(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRS (DateFiled, Purpose, Details, Status, RequisitionerID, UserID, CreatedAt, UpdateAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,?,GETDATE(), GETDATE())");
        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setInt(5, mirs.getRequisitionerID());
        ps.setInt(6, mirs.getUserID());

        ps.executeUpdate();

        ps.close();

    }

    /**
     * Updates an existing MIRS record
     * @param mirs The MIRS file to be updated
     * @throws Exception
     */
    public static void update(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRS SET " +
                        "DateFiled=?, Purpose=?, Details=?, Status=?, RequisitionerID=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setInt(5, mirs.getRequisitionerID());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Inserts a single MIRSItem in the MIRSItems table
     * @param mirs The MIRS file for which an item will be added
     * @param item The MIRSItem that will be added to the MIRS file
     * @throws Exception
     */
    public static void addMIRSItem(MIRS mirs, MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE())");
        ps.setInt(1, mirs.getId());
        ps.setInt(2, item.getStockID());
        ps.setInt(3, item.getQuantity());
        ps.setDouble(4, item.getPrice());
        ps.setString(5, item.getComments());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Inserts a group of MIRSItems in to the MIRSItems table
     * @param mirs The MIRS file for which the items will be added
     * @param items The list of MIRSItem that will be added to the MIRS file
     * @throws Exception
     */
    public static void addMIRSItems(MIRS mirs, List<MIRSItem> items) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE())");

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

    /**
     * Updates an existing MIRSItem
     * @param item The MIRSItem that will be updated
     * @throws Exception
     */
    public static void updateMIRSItem(MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRSItems SET StockID=?, Quantity=?, Price=?, Comments=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setInt(1, item.getStockID());
        ps.setInt(2, item.getQuantity());
        ps.setDouble(3, item.getPrice());
        ps.setString(4, item.getComments());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Removes a single MIRSItem
     * @param item The MIRSItem that will be removed
     * @throws Exception
     */
    public static void removeItem(MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "DELETE FROM MIRSItems WHERE id=?");
        ps.setInt(1, item.getId());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Removes a group of records from the MIRSItem table
     * @param items The List of MIRSItem that will removed
     * @throws Exception
     */
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

    /**
     * Retrieves a single record from the MIRS table based on the id
     * @param id The id of the MIRS record that will be retrieved
     * @return MIRS object
     * @throws Exception
     */
    public static MIRS getMIRS(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE id=?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            MIRS mirs = new MIRS(
                    rs.getInt("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getInt("RequesitionerID"),
                    rs.getInt("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime()
            );

            rs.close();
            ps.close();

            return mirs;
        }

        rs.close();
        ps.close();

        return null;
    }

    /**
     * Retrieves a group of MIRSItems that belong to a single MIRS record
     * @param mirs the MIRS File from which the the MIRSItems belong
     * @return List of MIRSItem
     * @throws Exception
     */
    public static List<MIRSItem> getItems(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRSItems WHERE MIRSID=?");
        ps.setInt(1, mirs.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<MIRSItem> items = new ArrayList();

        while(rs.next()) {
            items.add(new MIRSItem(
                    rs.getInt("id"),
                    rs.getInt("MIRSID"),
                    rs.getInt("StockID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime()
            ));
        }

        rs.close();
        ps.close();

        return items;
    }
}
