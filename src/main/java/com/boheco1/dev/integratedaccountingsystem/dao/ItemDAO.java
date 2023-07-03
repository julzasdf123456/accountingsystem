package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Item;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ItemDAO {
    public static List<Item> searchItems(String q) throws Exception {
        ArrayList<Item> items = new ArrayList<>();

        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Item WHERE Particulars LIKE ?");
        ps.setString(1, "%"+q+"%");
        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            Item item = new Item();
            item.setItemId(rs.getString("ItemId"));
            item.setParticulars(rs.getString("Particulars"));
            item.setPrice(rs.getDouble("Price"));
            item.setQtyOnHand(rs.getInt("QtyOnHand"));
            item.setUnit(rs.getString("Unit"));
            item.setScopeId(rs.getString("ScopeId"));
            items.add(item);
        }
        return items;
    }
}
