package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class AppDAO {
    public static ArrayList<APP> getAll() throws Exception {
        ArrayList<APP> apps = new ArrayList();

        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT * FROM APP ORDER BY Year DESC");

        while(rs.next()) {
            apps.add(new APP(
                    rs.getString("AppId"),
                    rs.getString("Year"),
                    rs.getString("BoardRes"),
                    rs.getBoolean("isOpen"),
                    rs.getDouble("TotalBudget")
            ));
        }

        return apps;
    }

    public static void create(APP app) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO APP (AppId, Year, IsOpen, BoardRes, TotalBudget) " +
                "VALUES (?,?,?,?,?)");
        String rndKey = Utility.generateRandomId();
        ps.setString(1, rndKey);
        ps.setString(2,app.getYear());
        ps.setBoolean(3, true);
        ps.setString(4, app.getBoardRes());
        ps.setDouble(5, app.getTotalBudget());
        ps.executeUpdate();

        app.setAppId(rndKey);
    }

    public static APP get(String id) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT * FROM APP WHERE AppId='" + id + "'");
        if(rs.next()) {
            return new APP(
                    rs.getString("AppId"),
                    rs.getString("Year"),
                    rs.getString("BoardRes"),
                    rs.getBoolean("isOpen"),
                    rs.getDouble("TotalBudget")
            );
        }
        return null;
    }

    public static void update(APP app) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE APP SET Year=?, BoardRes=?, TotalBudget=? WHERE AppId=?");
        ps.setString(1, app.getYear());
        ps.setString(2, app.getBoardRes());
        ps.setDouble(3, app.getTotalBudget());
        ps.setString(4, app.getAppId());
        ps.executeUpdate();
    }
}
