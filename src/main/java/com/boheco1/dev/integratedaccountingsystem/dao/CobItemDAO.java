package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CobItemDAO {
    /**
     *
     * @param cob Parent COB of the items that will be fetched.
     * @return A List of COBItems
     * @throws Exception
     */
    public static List<COBItem> getItems(COB cob) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM COBItem WHERE COBId=?");
        ps.setString(1, cob.getCobId());

        ResultSet rs = ps.executeQuery();

        List<COBItem> items = new ArrayList<>();

        while(rs.next()) {
            COBItem item = new COBItem();
            item.setcItemId(rs.getString("CItemId"));
            item.setDescription(rs.getString("Description"));
            item.setQty(rs.getInt("Qty"));
            item.setCost(rs.getDouble("Cost"));
            item.setItemId(rs.getString("ItemId"));
            item.setQtr1(rs.getDouble("Qtr1"));
            item.setQtr2(rs.getDouble("Qtr2"));
            item.setQtr3(rs.getDouble("Qtr3"));
            item.setQtr4(rs.getDouble("Qtr4"));
            item.setRemarks(rs.getString("Remarks"));

            items.add(item);
        }

        return items;
    }

    public static void update(COBItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE COBItem SET Description=?, Qty=?, Cost=?, Qtr1=?, Qtr2=?, Qtr3=?, Qtr4=?, ItemId=?, Remarks=? WHERE CItemId=?");

        ps.setString(1, item.getDescription());
        ps.setInt(2, item.getQty());
        ps.setDouble(3, item.getCost());
        ps.setDouble(4, item.getQtr1());
        ps.setDouble(5, item.getQtr2());
        ps.setDouble(6, item.getQtr3());
        ps.setDouble(7, item.getQtr4());
        ps.setString(8, item.getItemId());
        ps.setString(9, item.getRemarks());
        ps.setString(10, item.getcItemId());

        ps.executeUpdate();
    }
}
