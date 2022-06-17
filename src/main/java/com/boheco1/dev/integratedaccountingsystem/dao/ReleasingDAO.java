package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReleasingDAO {
    public static void add(Releasing releasing) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Releasing (StockID, MIRSID, Quantity, Price, UserID, Status, CreatedAt, UpdatedAt, id, MR, WorkOrderNo) " +
                        "VALUES (?,?,?,?,?,?,GETDATE(),GETDATE(),?, ?, ?)");

        releasing.setId(Utility.generateRandomId());

        ps.setString(1, releasing.getStockID());
        ps.setString(2, releasing.getMirsID());
        ps.setInt(3, releasing.getQuantity());
        ps.setDouble(4, releasing.getPrice());
        ps.setString(5, releasing.getUserID());
        ps.setString(6, releasing.getStatus());
        ps.setString(7, releasing.getId());
        ps.setString(8, releasing.getMR());
        ps.setString(9, releasing.getWorkOrderNo());

        ps.executeUpdate();

        ps.close();
    }

    public static boolean add(List<Releasing> releasingList, MCT mct, MIRS mirs) throws Exception {
        String query = "";
        String mct_number = null;
        if(mct != null) {
            query += "INSERT INTO MCT (mct_no, particulars, address, MIRSNo, createdAt, WorkOrderNo) " +
                    "VALUES ('" + mct.getMctNo() + "','" + mct.getParticulars() + "','" + mct.getAddress() + "','" + mct.getMirsNo() + "',GETDATE(),'" + mct.getWorkOrderNo() + "');\n";

            mct_number = mct.getMctNo();
        }

        for(Releasing releasing : releasingList){


            if(mct_number == null){
                query+="INSERT INTO Releasing (StockID, MIRSID, Quantity, Price, UserID, Status, CreatedAt, UpdatedAt, id) " +
                        "VALUES ('"+releasing.getStockID()+"', " +
                        "'"+releasing.getMirsID()+"', " +
                        "'"+releasing.getQuantity()+"', " +
                        "'"+releasing.getPrice()+"', " +
                        "'"+releasing.getUserID()+"', " +
                        "'"+releasing.getStatus()+"',GETDATE(),GETDATE()," +
                        "'"+Utility.generateRandomId()+"');\n";
            }else{
                query+="INSERT INTO Releasing (StockID, MIRSID, Quantity, Price, UserID, Status, CreatedAt, UpdatedAt, id, mct_no) " +
                        "VALUES ('"+releasing.getStockID()+"', " +
                        "'"+releasing.getMirsID()+"', " +
                        "'"+releasing.getQuantity()+"', " +
                        "'"+releasing.getPrice()+"', " +
                        "'"+releasing.getUserID()+"', " +
                        "'"+releasing.getStatus()+"',GETDATE(),GETDATE()," +
                        "'"+Utility.generateRandomId()+"', " +
                        "'"+mct_number+"');\n";
            }
        }

        query+="UPDATE MIRS SET " +
                        "Status='"+mirs.getStatus()+"', UpdatedAt=GETDATE() " +
                        "WHERE id='"+mirs.getId()+"';\n";

        //get all itemized from the global variable itemizedMirsItems
        for (ItemizedMirsItem i : Utility.getItemizedMirsItems().values()){
            query+="INSERT INTO itemizedMirsItem (id, MIRSItemID, brand, serial, remarks) " +
                    "VALUES ('"+i.getId()+"','"+i.getMirsItemID()+"','"+i.getBrand()+"','"+i.getSerial()+"','"+i.getRemarks()+"');\n";
        }

        System.out.println(query);
        Connection conn = DB.getConnection();

        try {
            conn.setAutoCommit(false);
            PreparedStatement ps = conn.prepareStatement(query);
            ps.executeUpdate();
            conn.commit();
            ps.close();
            conn.setAutoCommit(true);
            return true;
        } catch (SQLException e) {
            conn.rollback();
            conn.setAutoCommit(true);
            e.printStackTrace();
        }
        return false;
    }

    public static Releasing get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE id=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        Releasing releasing = null;

        if(rs.next()) {
            releasing = new Releasing(
                    rs.getString("id"),
                    rs.getString("StockID"),
                    rs.getString("MIRSID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("UserID"),
                    rs.getString("Status"),
                    rs.getString("MR"),
                    rs.getString("WorkOrderNo"),
                    rs.getString("mct_no")
            );
            releasing.setCreatedAt(rs.getTimestamp("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime():null);
            releasing.setUpdatedAt(rs.getTimestamp("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime():null);
        }

        rs.close();
        ps.close();

        return releasing;
    }

    public static List<Releasing> get(MIRS mirs, String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE MIRSID=? and status=?");
        ps.setString(1, mirs.getId());
        ps.setString(2, status);

        ResultSet rs = ps.executeQuery();

        ArrayList<Releasing> items = new ArrayList();
        while(rs.next()) {
            Releasing  releasing = new Releasing(
                    rs.getString("id"),
                    rs.getString("StockID"),
                    rs.getString("MIRSID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("UserID"),
                    rs.getString("Status"),
                    rs.getString("MR"),
                    rs.getString("WorkOrderNo"),
                    rs.getString("mct_no")
            );
            releasing.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
            releasing.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());

            items.add(releasing);
        }

        rs.close();
        ps.close();

        return items;
    }

    public static List<Releasing> getAllReleasedAndPartial(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE MIRSID=? and (status='"+Utility.RELEASED+"' OR status='"+Utility.PARTIAL_RELEASED+"') ");
        ps.setString(1, mirs.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<Releasing> items = new ArrayList();
        while(rs.next()) {
            Releasing  releasing = new Releasing(
                    rs.getString("id"),
                    rs.getString("StockID"),
                    rs.getString("MIRSID"),
                    rs.getInt("Quantity"),
                    rs.getDouble("Price"),
                    rs.getString("UserID"),
                    rs.getString("Status"),
                    rs.getString("MR"),
                    rs.getString("WorkOrderNo"),
                    rs.getString("mct_no")
            );
            releasing.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
            releasing.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());

            items.add(releasing);
        }

        rs.close();
        ps.close();

        return items;
    }

    public static void updateReleasedItem(Releasing item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Releasing SET Status=?, UpdatedAt=GETDATE() " +
                        "WHERE StockId=?");
        ps.setString(1, item.getStatus());
        ps.setString(2, item.getStockID());

        ps.executeUpdate();

        ps.close();
    }

}
