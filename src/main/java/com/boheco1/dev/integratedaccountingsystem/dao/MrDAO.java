package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MR;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class MrDAO {

    public static void add(MR mr) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MR (id, employeeId, warehousePersonnelId, extItem, stockID, quantity, price, dateOfMR, status) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)");

        mr.setId(Utility.generateRandomId());

        if(mr.getStockId()!=null) {
            Stock stock = StockDAO.get(mr.getStockId());
            mr.setPrice(stock!=null ? stock.getPrice() : null);
            mr.setExtItem(stock.getStockName());
        }

        ps.setString(1, mr.getId());
        ps.setString(2, mr.getEmployeeId());
        ps.setString(3, mr.getWarehousePersonnelId());
        ps.setString(4, mr.getExtItem());
        ps.setString(5, mr.getStockId());
        ps.setInt(6, mr.getQuantity());
        ps.setDouble(7, mr.getPrice());
        ps.setDate(8, java.sql.Date.valueOf(mr.getDateOfMR()));
        ps.setString(9, "active");

        ps.executeUpdate();

        ps.close();

    }

    public static MR get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {
            return null;
        }

        MR mr = new MR();
        mr.setId(rs.getString("id"));
        mr.setEmployeeId(rs.getString("employeeId"));
        mr.setWarehousePersonnelId(rs.getString("warehousePersonnelId"));
        mr.setExtItem(rs.getString("extItem"));
        mr.setStockId(rs.getString("stockID"));
        mr.setQuantity(rs.getInt("quantity"));
        mr.setPrice(rs.getDouble("price"));
        mr.setDateOfMR(rs.getDate("dateOfMR").toLocalDate());
        mr.setStatus(rs.getString("status"));
        mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);

        return mr;
    }
}
