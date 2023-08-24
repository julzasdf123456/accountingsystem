package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.POItem;
import com.boheco1.dev.integratedaccountingsystem.objects.PurchaseOrder;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class POItemDAO {
    /**
     *
     * @param po Parent PO of the items that will be fetched.
     * @return A List of POItems
     * @throws Exception
     */
    public static List<POItem> getItems(PurchaseOrder po) throws Exception {

        String sql = "SELECT PONo, POItemId, RVNo, ri.RVItemId, ri.CItemId, pi.Sequence, ItemId, Description, POQty, POPrice, ci.Remarks, Details " +
                "FROM COBItem ci INNER JOIN RVItem ri ON ci.CItemId=ri.CItemId INNER JOIN POItem pi ON pi.RVItemId=ri.RVItemId ";
        sql += "WHERE PONo = ? ORDER BY pi.Sequence ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, po.getPoNo());

        ResultSet rs = ps.executeQuery();

        List<POItem> items = new ArrayList<>();

        while(rs.next()) {

            POItem item = new POItem();
            item.setPOItemId(rs.getInt("POItemId"));
            item.setPONo(rs.getString("PONo"));
            item.setRVItemId(rs.getInt("RVItemId"));
            item.setcItemId(rs.getString("CItemId"));
            item.setItemId(rs.getString("ItemId"));
            item.setDetails(rs.getString("Details"));
            item.setDescription(rs.getString("Description"));
            item.setPOQty(rs.getInt("POQty"));
            item.setPOPrice(rs.getDouble("POPrice"));
            item.setRemarks(rs.getString("Remarks"));
            item.setSequence(rs.getInt("Sequence"));
            item.setRVNo(rs.getString("RVNo"));
            items.add(item);
        }

        return items;
    }

    public static void update(POItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE POItem SET POQty=? WHERE POItemId=?");

        ps.setInt(1, item.getPOQty());
        ps.setString(2, item.getcItemId());

        ps.executeUpdate();
    }

    /**
     * Create PO and items
     * @param po PO object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void add(PurchaseOrder po, POItem item) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String sql = "UPDATE PurchaseOrder SET Amount = Amount + ? WHERE PONo =?;";

        String item_sql = "INSERT INTO POItem (PONo, POQty, POPrice, Details, RVItemId, Sequence) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        //Prepared statements
        PreparedStatement ps_rv = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Update RV
            ps_rv.setDouble(1, item.getAmount());
            ps_rv.setString(2, po.getPoNo());
            ps_rv.executeUpdate();

            ps_item.setString(1, po.getPoNo());
            ps_item.setInt(2, item.getPOQty());
            ps_item.setDouble(3, item.getPOPrice());
            ps_item.setString(4, item.getDetails());
            ps_item.setInt(5, item.getRVItemId());
            ps_item.setInt(6, item.getSequence());
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
     * Delete PO item from revised PO
     * @param po - PO reference
     * @param item - PO item reference to delete
     * @throws Exception
     */
    public static void delete(PurchaseOrder po, POItem item) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "DELETE FROM POItem WHERE POItemId=?;";
        String sql2 = "UPDATE PurchaseOrder SET Amount = Amount - ? WHERE PONo = ?;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql2);
        try {
            ps.setInt(1, item.getPOItemId());
            ps.executeUpdate();

            ps_cob.setDouble(1, item.getAmount());
            ps_cob.setString(2, po.getPoNo());
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
