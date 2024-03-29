package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MRTDao {

    public static void create(MRT mrt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MRT (id, dateOfReturned, returnedBy, receivedBy) " +
                        "VALUES (?,?,?,?)");

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
                ps1.setDouble(4, item.getQuantity());
                ps1.executeUpdate();

                //Update Quantity in Releasing
//                ps2.setInt(1, item.getQuantity());
//                ps2.setString(2, item.getReleasingID());
//                ps2.executeUpdate();

                //Insert StockEntryLogs
                ps3.setString(1, Utility.generateRandomId());
                ps3.setString(2, releasing.getStockID());
                ps3.setDouble(3, item.getQuantity());
                ps3.setString(4, "Returned");
                ps3.setDouble(5, releasing.getPrice());
                ps3.setString(6, ActiveUser.getUser().getId());
                ps3.setDate(7, Date.valueOf(LocalDate.now()));
                ps3.setDate(8, Date.valueOf(LocalDate.now()));
                ps3.executeUpdate();

                //Update Quantity in Stocks
                ps4.setDouble(1, item.getQuantity());
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

    public static MRT getMRT(String mrtno)throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MRT WHERE id = ?");


        ps.setString(1, mrtno);


        ResultSet rs = ps.executeQuery();
        MRT mrt = null;
        while(rs.next()) {
            mrt = new MRT(rs.getString("id"), rs.getString("returnedBy"), rs.getString("receivedBy"), rs.getDate("dateOfReturned").toLocalDate());
        }
        rs.close();
        return mrt;
    }

    public static List<MRT> getAllMRT()throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT DISTINCT mt.id, r.MIRSID, returnedBy, mt.receivedBy, dateOfReturned, purpose \n" +
                        "FROM MRT mt INNER JOIN MRTItem mti ON mt.id = mti.mrt_id \n" +
                        "INNER JOIN Releasing r ON r.id = mti.releasing_id \n" +
                        "INNER JOIN MIRS mrs ON mrs.id = r.MIRSID \n "+
                        "ORDER BY dateOfReturned DESC");
        ResultSet rs = ps.executeQuery();
        List<MRT> items = new ArrayList();
        while(rs.next()) {
            MRT mrt = new MRT(rs.getString("id"), rs.getString("returnedBy"), rs.getString("receivedBy"), rs.getDate("dateOfReturned").toLocalDate());
            mrt.setMirsId(rs.getString("MIRSID"));
            mrt.setPurpose(rs.getString("purpose"));
            items.add(mrt);
        }
        rs.close();
        return items;
    }

    public static List<MRT> searchMRT(String key)throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT DISTINCT mt.id, r.MIRSID, returnedBy, mt.receivedBy, dateOfReturned, purpose \n" +
                        "FROM MRT mt INNER JOIN MRTItem mti ON mt.id = mti.mrt_id \n" +
                        "INNER JOIN Releasing r ON r.id = mti.releasing_id \n" +
                        "INNER JOIN MIRS mrs ON mrs.id = r.MIRSID\n "+
                        "WHERE mt.id LIKE ? OR returnedBy LIKE ? OR r.MIRSID LIKE ? OR purpose LIKE ? ORDER BY dateOfReturned DESC");

        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        List<MRT> items = new ArrayList();
        while(rs.next()) {
            MRT mrt = new MRT(rs.getString("id"), rs.getString("returnedBy"), rs.getString("receivedBy"), rs.getDate("dateOfReturned").toLocalDate());
            mrt.setMirsId(rs.getString("MIRSID"));
            mrt.setPurpose(rs.getString("purpose"));
            items.add(mrt);
        }
        rs.close();
        return items;
    }

    public static List<MRTItem> getItems(String mrtno) throws Exception {
        List<MRTItem> items = new ArrayList();

        String sql = "SELECT AcctgCode, NEACode, LocalCode, Description, r.Price, Unit, mti.quantity "+
                "FROM Stocks s INNER JOIN Releasing r ON r.StockID = s.id "+
                "INNER JOIN MRTItem mti ON mti.releasing_id = r.id "+
                "INNER JOIN MRT mrt ON mrt.id = mti.mrt_id "+
                "WHERE mti.mrt_id = ?";
        PreparedStatement ps = DB.getConnection().prepareStatement(sql);

        ps.setString(1, mrtno);

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            MRTItem item = new MRTItem();
            item.setAcctCode(rs.getString("AcctgCode"));
            String neaCode = rs.getString("NEACode");
            if (neaCode != null && neaCode.length() != 0) {
                item.setCode(neaCode);
            }else{
                item.setCode(rs.getString("LocalCode"));
            }
            item.setDescription(rs.getString("Description"));
            item.setPrice(Utility.round(rs.getDouble("Price"),2));
            item.setQuantity(rs.getDouble("quantity"));
            item.setUnit(rs.getString("Unit"));
            item.setAmount(Utility.round(item.getPrice()*item.getQuantity(),2));
            items.add(item);
        }

        rs.close();

        return items;
    }

    public static double getTotal(String mrtno) throws Exception {
        String sql = "SELECT  SUM(r.Price * mti.quantity) as TOTAL\n" +
                "                FROM Releasing r INNER JOIN MRTItem mti ON mti.releasing_id = r.id \n" +
                "                WHERE mti.mrt_id = ?";
        PreparedStatement ps = DB.getConnection().prepareStatement(sql);

        ps.setString(1, mrtno);

        ResultSet rs = ps.executeQuery();
        double amount = 0;
        while(rs.next()) {
            amount = Utility.round(rs.getDouble("TOTAL"), 2);
        }

        rs.close();

        return amount;
    }

    public static List<ReleasedItems> searchReleasedItems(String searchKey) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT NEACode, LocalCode, AcctgCode, m.id as MIRSNo, r.id, s.Description, r.mct_no, r.Price, r.Quantity, r.Quantity - (SELECT COALESCE(SUM(quantity),0) FROM MRTItem WHERE releasing_id = r.id) as Balance, s.id as stockID " +
                        "FROM Releasing r \n" +
                        "INNER JOIN Stocks s ON s.id = r.StockID \n" +
                        "INNER JOIN MIRSItems mi ON mi.StockID = r.StockID \n" +
                        "INNER JOIN MIRS m ON m.id = mi.MIRSID \n" +
                        "WHERE r.Quantity > 0 AND mct_no IS NOT NULL AND m.id = ?\n" +
                        "ORDER BY r.CreatedAt DESC;");
        ps.setString(1, searchKey);

        ResultSet rs = ps.executeQuery();

        List<ReleasedItems> items = new ArrayList();

        while(rs.next()) {
            ReleasedItems item = new ReleasedItems(
                    rs.getString("id"),
                    rs.getString("Description"),
                    rs.getString("mct_no"),
                    rs.getDouble("price"),
                    rs.getDouble("Quantity"),
                    rs.getInt("Balance")
            );
            item.setStockID(rs.getString("stockID"));
            String neaCode = rs.getString("NEACode");
            if (neaCode != null && neaCode.length() != 0) {
                item.setCode(neaCode);
            }else{
                item.setCode(rs.getString("LocalCode"));
            }
            item.setMirsNo(rs.getString("MIRSNo"));
            items.add(item);
        }

        rs.close();
        return items;
    }
}
