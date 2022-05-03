package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockHistory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDate;

public class StockHistoryDAO {
    public static void create(StockHistory stockHistory) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO StockHistory (id, date, price, updatedBy) " +
                        "VALUES (?,?,?,?)");

        if(stockHistory.getId()==null) stockHistory.setId(Utility.generateRandomId());

        ps.setString(1, stockHistory.getStockID());
        ps.setDate(2, java.sql.Date.valueOf(stockHistory.getDate()));
        ps.setDouble(3, stockHistory.getPrice());
        ps.setString(4, stockHistory.getUpdatedBy());

        ps.executeUpdate();

        ps.close();
    }

    public static List<StockHistory> getStockHistory(String stockID) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM StockHistory WHERE stockID = ? " +
                        "ORDER BY date DESC");
        ps.setString(1, stockID);

        List<StockHistory> stockHistory = new ArrayList<>();

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            stockHistory.add(new StockHistory(
                    rs.getString("id"),
                    rs.getString("stockID"),
                    rs.getDate("date").toLocalDate(),
                    rs.getDouble("price"),
                    rs.getString("updatedBy")
            ));
        }

        rs.close();
        ps.close();

        return stockHistory;
    }
}
