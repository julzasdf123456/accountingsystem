package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        String sql = "SELECT RVItemId, ri.CItemId, ri.Sequence, ItemId, Description, Cost, ci.Remarks, RVQty " +
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
            items.add(item);
        }

        return items;
    }
}
