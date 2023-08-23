package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RVItemDAO {
    /**
     *
     * @param rv Parent RV of the items that will be fetched.
     * @return A List of RVItems
     * @throws Exception
     */
    public static List<RVItem> getItems(RV rv) throws Exception {

        String sql = "SELECT RVNo, RVItemId, ri.CItemId, ri.Sequence, COBId, ItemId, Description, Cost, ci.Remarks, Qty, RVQty, ISNULL((SELECT SUM(POQty) FROM COBItem c INNER JOIN RVItem r ON r.CItemId = c.CItemId INNER JOIN POItem p ON p.RVItemId = r.RVItemId WHERE r.CItemId = ci.CItemId), 0) AS POQty " +
                "FROM COBItem ci INNER JOIN RVItem ri ON ci.CItemId=ri.CItemId ";
        sql += "WHERE RVNo = ? ORDER BY ri.Sequence ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, rv.getRvNo());

        ResultSet rs = ps.executeQuery();

        List<RVItem> items = new ArrayList<>();

        while(rs.next()) {

            RVItem item = new RVItem();

            item.setRVItemId(rs.getInt("RVItemId"));
            item.setcItemId(rs.getString("CItemId"));
            item.setItemId(rs.getString("ItemId"));
            item.setDescription(rs.getString("Description"));
            item.setQty(rs.getInt("RVQty"));
            item.setCost(rs.getDouble("Cost"));
            item.setRemarks(rs.getString("Remarks"));
            item.setSequence(rs.getInt("Sequence"));
            item.setCobId(rs.getString("COBId"));
            item.setRVNo(rs.getString("RVNo"));
            item.setPoItemsQty(rs.getInt("POQty"));
            items.add(item);
        }

        return items;
    }

    public static void update(RVItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE RVItem SET RVQty=? WHERE RVItemId=?");

        ps.setInt(1, item.getQty());
        ps.setString(2, item.getcItemId());

        ps.executeUpdate();
    }

    /**
     * Create RV and items
     * @param rv RV object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void add(RV rv, RVItem item) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String sql = "UPDATE RequisitionVoucher SET Amount = Amount + ? WHERE RVNo =?;";

        String item_sql = "INSERT INTO RVItem (RVNo, RVQty, CItemId, Sequence) " +
                "VALUES (?, ?, ?, ?)";

        //Prepared statements
        PreparedStatement ps_rv = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Update RV
            ps_rv.setDouble(1, item.getAmount());
            ps_rv.setString(2, rv.getRvNo());
            ps_rv.executeUpdate();


            ps_item.setString(1, rv.getRvNo());
            ps_item.setInt(2, item.getQty());
            ps_item.setString(3, item.getcItemId());
            ps_item.setInt(4, item.getSequence());
            ps_item.executeUpdate();

            //Commit insert
            conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_rv.close();
        ps_item.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Delete RV item from revised RV
     * @param rv - RV reference
     * @param item - RV item reference to delete
     * @throws Exception
     */
    public static void delete(RV rv, RVItem item) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "DELETE FROM RVItem WHERE RVItemId=?;";
        String sql2 = "UPDATE RequisitionVoucher SET Amount = Amount - ? WHERE RVNo = ?;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql2);
        try {
            ps.setInt(1, item.getRVItemId());
            ps.executeUpdate();

            ps_cob.setDouble(1, item.getAmount());
            ps_cob.setString(2, rv.getRvNo());
            ps_cob.executeUpdate();
            //Commit insert
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            //If error, rollback
            conn.rollback();
        }
        //Close connections
        ps.close();
        ps_cob.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }
}
