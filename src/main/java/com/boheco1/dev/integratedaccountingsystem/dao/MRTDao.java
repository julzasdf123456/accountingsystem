package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MRTDao {

    public static void create(MRT mrt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MRT (id, dateOfReturned, returnedBy, receivedBy) " +
                        "VALUES (?,?,?,?)");

        if(mrt.getId()==null) mrt.setId(Utility.generateRandomId());

        ps.setString(1, mrt.getId());
        ps.setDate(2, Date.valueOf(mrt.getDateOfReturned()));
        ps.setString(3, mrt.getReturnedBy());
        ps.setString(4, mrt.getReceivedBy());

        ps.executeUpdate();

        ps.close();
    }

    public static void addItems(MRT mrt, List<MRTItem> items) throws Exception {
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Insert MRTItem
        PreparedStatement ps1 = conn.prepareStatement("INSERT INTO MRTItem (id, releasing_id, mrt_id, quantity) " +
                "VALUES (?,?,?,?)");

        //Update Releasing
        //PreparedStatement ps2 = conn.prepareStatement(
        //        "UPDATE Releasing SET Quantity=(Quantity-?) WHERE id=?");

        //Insert StockEntryLogs
        PreparedStatement ps3 = conn.prepareStatement(
                "INSERT INTO StockEntryLogs (id, StockID, Quantity, Source, Price, UserID, CreatedAt, UpdatedAt) " +
                        "VALUES (?,?,?,?,?,?,?,?)");

        //Update Quantity in Stocks
        PreparedStatement ps4 = conn.prepareStatement(
                "UPDATE Stocks SET Quantity = (Quantity + ?) WHERE id=?");

        for(MRTItem item: items) {

            Releasing releasing = ReleasingDAO.get(item.getReleasingID());

            try {
                //Insert MRTItem
                ps1.setString(1, item.getId()==null ? Utility.generateRandomId() : item.getId());
                ps1.setString(2, item.getReleasingID());
                ps1.setString(3, mrt.getId());
                ps1.setInt(4, item.getQuantity());
                ps1.executeUpdate();

                //Update Quantity in Releasing
//                ps2.setInt(1, item.getQuantity());
//                ps2.setString(2, item.getReleasingID());
//                ps2.executeUpdate();

                //Insert StockEntryLogs
                ps3.setString(1, Utility.generateRandomId());
                ps3.setString(2, releasing.getStockID());
                ps3.setInt(3, item.getQuantity());
                ps3.setString(4, "Returned");
                ps3.setDouble(5, releasing.getPrice());
                ps3.setString(6, ActiveUser.getUser().getId());
                ps3.setDate(7, Date.valueOf(LocalDate.now()));
                ps3.setDate(8, Date.valueOf(LocalDate.now()));
                ps3.executeUpdate();

                //Update Quantity in Stocks
                ps4.setInt(1, item.getQuantity());
                ps4.setString(2, releasing.getStockID());
                //System.out.println("Item Quantity: " + item.getQuantity());
                //System.out.println("releasing.getStockID" + releasing.getStockID());
                ps4.executeUpdate();
            }catch(SQLException ex) {
                conn.rollback();
                throw new Exception(ex.getMessage());
            }
        }
        conn.commit();

        ps1.close();
        //ps2.close();
        ps3.close();
        ps4.close();
        conn.setAutoCommit(true);
    }

    public static List<ReleasedItems> searchReleasedItems(String searchKey) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT NEACode, LocalCode, AcctgCode, r.id, s.Description, r.mct_no, r.Price, r.Quantity, r.Quantity - (SELECT COALESCE(SUM(quantity),0) FROM MRTItem WHERE releasing_id = r.id) as Balance, s.id as stockID " +
                        "FROM Releasing r \n" +
                        "INNER JOIN Stocks s ON s.id = r.StockID \n" +
                        "INNER JOIN MIRSItems mi ON mi.StockID = r.StockID \n" +
                        "INNER JOIN MIRS m ON m.id = mi.MIRSID \n" +
                        "WHERE r.Quantity > 0 AND mct_no IS NOT NULL AND (address LIKE ? OR details LIKE ? \n" +
                        "OR s.Description LIKE ?) \n" +
                        "ORDER BY r.CreatedAt DESC;");
        ps.setString(1, "%" + searchKey + "%");
        ps.setString(2, "%" + searchKey + "%");
        ps.setString(3, "%" + searchKey + "%");

        ResultSet rs = ps.executeQuery();

        List<ReleasedItems> items = new ArrayList();

        while(rs.next()) {
            ReleasedItems item = new ReleasedItems(
                    rs.getString("id"),
                    rs.getString("Description"),
                    rs.getString("mct_no"),
                    rs.getDouble("price"),
                    rs.getInt("Quantity"),
                    rs.getInt("Balance")
            );
            item.setStockID(rs.getString("stockID"));
            String neaCode = rs.getString("NEACode");
            if (neaCode != null && neaCode.length() != 0) {
                item.setCode(neaCode);
            }else{
                item.setCode(rs.getString("LocalCode"));
            }
            items.add(item);
        }

        rs.close();
        return items;
    }
}
