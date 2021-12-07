package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Releasing;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class ReleasingDAO {
    public static void add(Releasing releasing) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Releasing (StockID, MIRSID, Quantity, Price, UserID, Status, CreatedAt, UpdatedAt) " +
                        "VALUES (?,?,?,?,?,?,GETDATE(),GETDATE())", Statement.RETURN_GENERATED_KEYS);

        ps.setString(1, releasing.getStockID());
        ps.setString(2, releasing.getMirsID());
        ps.setInt(3, releasing.getQuantity());
        ps.setDouble(4, releasing.getPrice());
        ps.setString(5, releasing.getUserID());
        ps.setString(6, releasing.getStatus());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) {
            releasing.setId(rs.getString(1));
        }

        rs.close();
        ps.close();
    }

    public static Releasing get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE id=?");
        ps.setInt(1, id);

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
                    rs.getString("Status")
            );
            releasing.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
            releasing.setUpdatedAt(rs.getTimestamp("UpdatedAt").toLocalDateTime());
        }

        rs.close();
        ps.close();

        return releasing;
    }

    
}
