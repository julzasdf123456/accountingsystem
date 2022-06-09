package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
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
                "INSERT INTO MIRS (DateFiled, Purpose, Details, Status, RequisitionerID, UserID, id, CreatedAt, UpdatedAt, address, applicant) " +
                        "VALUES " +
                        "(?,?,?,?,?,?,?,GETDATE(), GETDATE(),?,?)");

        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setString(5, mirs.getRequisitionerID());
        ps.setString(6, mirs.getUserID());
        ps.setString(7, mirs.getId());
        ps.setString(8, mirs.getAddress());
        ps.setString(9, mirs.getApplicant());

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
                        "DateFiled=?, Purpose=?, Details=?, Status=?, RequisitionerID=?, UpdatedAt=GETDATE(), address=?, applicant=?, WorkOrderNo = ? " +
                        "WHERE id=?");
        ps.setDate(1, Date.valueOf(mirs.getDateFiled()));
        ps.setString(2, mirs.getPurpose());
        ps.setString(3, mirs.getDetails());
        ps.setString(4, mirs.getStatus());
        ps.setString(5, mirs.getRequisitionerID());
        ps.setString(6, mirs.getAddress());
        ps.setString(7, mirs.getApplicant());
        ps.setString(8, mirs.getWorkOrderNo());
        ps.setString(9, mirs.getId());

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
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt, id) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE(), ?)");

        item.setId(Utility.generateRandomId());

        ps.setString(1, mirs.getId());
        ps.setString(2, item.getStockID());
        ps.setInt(3, item.getQuantity());
        ps.setDouble(4, item.getPrice());
        ps.setString(5, item.getRemarks());
        ps.setString(6, item.getId());

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
                "INSERT INTO MIRSItems (MIRSID, StockID, Quantity, Price, Comments, CreatedAt, UpdatedAt, id) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE(), ?)");

        for(MIRSItem item: items) {
            ps.setString(1, mirs.getId());
            ps.setString(2, item.getStockID());
            ps.setInt(3, item.getQuantity());
            ps.setDouble(4, item.getPrice());
            ps.setString(5, item.getRemarks());
            ps.setString(6, Utility.generateRandomId());

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
        ps.setString(1, item.getStockID());
        ps.setInt(2, item.getQuantity());
        ps.setDouble(3, item.getPrice());
        ps.setString(4, item.getRemarks());

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
        ps.setString(1, item.getId());

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
            ps.setString(1, item.getId());
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
    public static MIRS getMIRS(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE id=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            MIRS mirs = new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
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
        ps.setString(1, mirs.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<MIRSItem> items = new ArrayList();

        while(rs.next()) {
            items.add(new MIRSItem(
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("StockID"),
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

    /**
     * Retrieves a group of MIRSItems that belong to a single MIRS record which are not yet released
     * @param mirs the MIRS File from which the the MIRSItems belong
     * @return List of MIRSItem
     * @throws Exception
     */
    public static List<MIRSItem> getUnreleasedItems(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRSItems m WHERE m.MIRSID=? AND m.StockID NOT IN " +
                        "(SELECT StockID FROM Releasing r WHERE r.MIRSID=? AND r.status='"+Utility.RELEASED+"');");

        ps.setString(1, mirs.getId());
        ps.setString(2, mirs.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<MIRSItem> items = new ArrayList();

        while(rs.next()) {
            items.add(new MIRSItem(
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("StockID"),
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

    public static List<MIRS> getAllPending() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE Status='pending' ORDER BY CreatedAt");
        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> pending = new ArrayList<>();

        while(rs.next()) {
            pending.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return pending;
    }

    public static List<MIRS> getByMonthYear(String my) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE CONCAT(YEAR(DateFiled),'-',MONTH(DateFiled)) = ? ORDER BY CreatedAt");
        ps.setString(1,my);
        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> result = new ArrayList<>();

        while(rs.next()) {
            result.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return result;
    }

    public static int countPending() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(id) AS 'count' FROM MIRS WHERE Status='pending'");
        ResultSet rs = ps.executeQuery();

        int count = 0;

        if(rs.next()) {
            count = rs.getInt("count");
        }

        rs.close();
        ps.close();

        return count;
    }

    public static int countMIRSByStatus(String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(id) AS 'count' FROM MIRS WHERE Status=?");
        ps.setString(1, status);

        ResultSet rs = ps.executeQuery();
        int count = 0;

        if(rs.next()) {
            count = rs.getInt("count");
        }

        rs.close();
        ps.close();

        return count;
    }

    public static List<MIRS> getMIRSByStatus(String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE status=? ORDER BY CreatedAt");
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> approved = new ArrayList<>();

        while(rs.next()) {
            approved.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return approved;
    }

    /**
     * Fetch all MIRS
     * @return
     * @throws Exception
     */
    public static List<MIRS> getAllMIRS() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS ORDER BY DateFiled DESC");
        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> mirsList = new ArrayList<>();

        while(rs.next()) {
            mirsList.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return mirsList;
    }

    public static List<MIRS> searchMIRS(String key, String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE (id LIKE ? OR Purpose LIKE ? OR Address LIKE ? OR Applicant LIKE ?) AND Status = ? ORDER BY DateFiled DESC");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, status);


        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> mirsList = new ArrayList<>();

        while(rs.next()) {
            mirsList.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return mirsList;
    }

    public static List<MIRS> searchMIRS(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE (id LIKE ? OR Purpose LIKE ? OR Address LIKE ? OR Applicant LIKE ?) ORDER BY DateFiled DESC");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        ArrayList<MIRS> mirsList = new ArrayList<>();

        while(rs.next()) {
            mirsList.add(new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            ));
        }

        rs.close();
        ps.close();

        return mirsList;
    }

    public static List<MIRS> getByDateFiled(LocalDate date) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE DateFiled = ? ORDER BY CreatedAt");
        ps.setDate(1, java.sql.Date.valueOf(date));

        ResultSet rs = ps.executeQuery();

        List<MIRS> mirs = new ArrayList();

        while(rs.next()) {
            mirs.add(
                new MIRS(
                        rs.getString("id"),
                        rs.getDate("DateFiled").toLocalDate(),
                        rs.getString("Purpose"),
                        rs.getString("Details"),
                        rs.getString("Status"),
                        rs.getString("RequisitionerID"),
                        rs.getString("UserID"),
                        rs.getTimestamp("CreatedAt").toLocalDateTime(),
                        rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                        rs.getString("address"),
                        rs.getString("applicant"),
                        rs.getString("WorkOrderNo")
                )
            );
        }
        return mirs;
    }

    public static List<MIRS> getByDateFiledBetween(LocalDate from, LocalDate to) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRS WHERE DateFiled BETWEEN ? AND ? ORDER BY CreatedAt");
        ps.setDate(1, java.sql.Date.valueOf(from));
        ps.setDate(2, java.sql.Date.valueOf(to));

        ResultSet rs = ps.executeQuery();

        List<MIRS> mirs = new ArrayList();

        while(rs.next()) {
            mirs.add(
                    new MIRS(
                            rs.getString("id"),
                            rs.getDate("DateFiled").toLocalDate(),
                            rs.getString("Purpose"),
                            rs.getString("Details"),
                            rs.getString("Status"),
                            rs.getString("RequisitionerID"),
                            rs.getString("UserID"),
                            rs.getTimestamp("CreatedAt").toLocalDateTime(),
                            rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                            rs.getString("address"),
                            rs.getString("applicant"),
                            rs.getString("WorkOrderNo")
                    )
            );
        }
        return mirs;
    }

    public static  List<ReleasedItemDetails> getReleasedMIRSItems(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT DISTINCT Stocks.Description, MIRSItems.*, Releasing.Status " +
                        "FROM MIRSItems " +
                        "LEFT JOIN Stocks ON Stocks.id = MIRSItems.StockID " +
                        "LEFT JOIN Releasing ON (Releasing.MIRSID=MIRSItems.MIRSID AND Releasing.StockID=MIRSItems.StockID) " +
                        "WHERE MIRSItems.MIRSID=?;");

        ps.setString(1, mirs.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<ReleasedItemDetails> releasedItemDetails = new ArrayList();

        while(rs.next()) {
            releasedItemDetails.add(new ReleasedItemDetails(
                    rs.getString("Description"),
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("StockID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("Status"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime()
            ));
        }

        rs.close();

        ps.close();

        return releasedItemDetails;
    }

    public static int getBalance(MIRSItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(Quantity) FROM Releasing r WHERE r.StockID = ? AND MIRSID = ?;");

        ps.setString(1, item.getStockID());
        ps.setString(2, item.getMirsID());

        ResultSet rs = ps.executeQuery();

        int releasedCount = 0;

        if(rs.next()) {
            releasedCount = rs.getInt(1);
        }

        return item.getQuantity() - releasedCount;
    }

    public static List<UnchargedMIRSReleases> getUnchargedMIRSReleases() throws Exception {
        List<UnchargedMIRSReleases> unchargedMIRSReleases=new ArrayList();

        /*ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM dbo.MIRS m WHERE m.id IN " +
                        "(SELECT r.MIRSID FROM Releasing r WHERE r.status='released' AND mct_no IS NULL);");

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT r.*, s.Description, s.Price FROM Releasing r " +
                        "LEFT JOIN Stocks s ON s.id = r.StockID " +
                        "WHERE r.MIRSID = ? AND r.Status = 'released' AND mct_no IS NULL;");*/

        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM dbo.MIRS m WHERE m.id IN " +
                        "(SELECT r.MIRSID FROM Releasing r WHERE (r.status='"+Utility.RELEASED+"' OR r.status='"+Utility.PARTIAL_RELEASED+"') AND mct_no IS NULL);");

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT r.*, s.Description, s.Price FROM Releasing r " +
                        "LEFT JOIN Stocks s ON s.id = r.StockID " +
                        "WHERE r.MIRSID = ? AND (r.status='"+Utility.RELEASED+"' OR r.status='"+Utility.PARTIAL_RELEASED+"') AND mct_no IS NULL;");

        while(rs.next()) {
            MIRS mirs = new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            );

            List<UnchargedItemDetails> items = new ArrayList();

            ps.setString(1, mirs.getId());
            ResultSet rs2 = ps.executeQuery();

            while(rs2.next()) {
                items.add(
                        new UnchargedItemDetails(
                                rs2.getString("id"),
                                rs2.getString("Description"),
                                rs2.getDouble("Price"),
                                rs2.getInt("Quantity")
                        )
                );
            }

            rs2.close();

            unchargedMIRSReleases.add(new UnchargedMIRSReleases(mirs, items));

        }

        ps.close();
        rs.close();

        return unchargedMIRSReleases;
    }

    public static List<UnchargedMIRSReleases> getChargedMIRSReleases() throws Exception {
        List<UnchargedMIRSReleases> unchargedMIRSReleases=new ArrayList();

        /*ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM dbo.MIRS m WHERE m.id IN " +
                        "(SELECT r.MIRSID FROM Releasing r WHERE r.status='released' AND mct_no IS NULL);");

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT r.*, s.Description, s.Price FROM Releasing r " +
                        "LEFT JOIN Stocks s ON s.id = r.StockID " +
                        "WHERE r.MIRSID = ? AND r.Status = 'released' AND mct_no IS NULL;");*/

        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM dbo.MIRS m WHERE m.id IN " +
                        "(SELECT r.MIRSID FROM Releasing r WHERE (r.status='"+Utility.RELEASED+"' OR r.status='"+Utility.PARTIAL_RELEASED+"') AND mct_no IS NOT NULL);");

        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT r.*, s.Description, s.Price FROM Releasing r " +
                        "LEFT JOIN Stocks s ON s.id = r.StockID " +
                        "WHERE r.MIRSID = ? AND (r.status='"+Utility.RELEASED+"' OR r.status='"+Utility.PARTIAL_RELEASED+"') AND mct_no IS NOT NULL;");

        while(rs.next()) {
            MIRS mirs = new MIRS(
                    rs.getString("id"),
                    rs.getDate("DateFiled").toLocalDate(),
                    rs.getString("Purpose"),
                    rs.getString("Details"),
                    rs.getString("Status"),
                    rs.getString("RequisitionerID"),
                    rs.getString("UserID"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getString("address"),
                    rs.getString("applicant"),
                    rs.getString("WorkOrderNo")
            );

            List<UnchargedItemDetails> items = new ArrayList();

            ps.setString(1, mirs.getId());
            ResultSet rs2 = ps.executeQuery();

            while(rs2.next()) {
                items.add(
                        new UnchargedItemDetails(
                                rs2.getString("id"),
                                rs2.getString("Description"),
                                rs2.getDouble("Price"),
                                rs2.getInt("Quantity")
                        )
                );
            }

            rs2.close();

            unchargedMIRSReleases.add(new UnchargedMIRSReleases(mirs, items));

        }

        ps.close();
        rs.close();

        return unchargedMIRSReleases;
    }


}
