package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public class DeptThresholdDAO {
    public static DeptThreshold get(String id) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT * FROM DeptThreshold WHERE ThreshId='" + id + "'");
        if(rs.next()) {
            return new DeptThreshold(
                    rs.getString("ThreshId"),
                    rs.getDouble("ThreshAmount"),
                    rs.getString("DepartmentId"),
                    rs.getString("AppId")
            );
        }

        return null;
    }

    public static void create(DeptThreshold dt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO DeptThreshold (ThreshId, ThreshAmount, DepartmentId, AppId) " +
                "VALUES (?,?,?,?)");
        ps.setString(1, Utility.generateRandomId());
        ps.setDouble(2, dt.getThreshAmount());
        ps.setString(3, dt.getDepartmentID());
        ps.setString(4, dt.getAppID());
        ps.executeUpdate();
    }

    public static void create(List<DeptThreshold> thresholds) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO DeptThreshold (ThreshId, ThreshAmount, DepartmentId, AppId) " +
                "VALUES (?,?,?,?)");

        for(DeptThreshold dt: thresholds) {
            ps.setString(1, Utility.generateRandomId());
            ps.setDouble(2, dt.getThreshAmount());
            ps.setString(3, dt.getDepartmentID());
            ps.setString(4, dt.getAppID());
            ps.addBatch();
        }

        ps.executeBatch();
    }

    public static DeptThreshold find(String appId, String departmentId) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM DeptThreshold WHERE AppId=? AND DepartmentId=?");
        ps.setString(1, appId);
        ps.setString(2, departmentId);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return new DeptThreshold(
                    rs.getString("ThreshId"),
                    rs.getDouble("ThreshAmount"),
                    rs.getString("DepartmentId"),
                    rs.getString("AppId")
            );
        }

        return null;
    }

    public static void updateAmount(DeptThreshold dt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE DeptThreshold SET ThreshAmount=? WHERE ThreshId=?");
        ps.setDouble(1, dt.getThreshAmount());
        ps.setString(2, dt.getThresID());
        ps.executeUpdate();
    }

    public static double getTotalAppropriations(DeptThreshold deptThreshold) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT SUM(c.Amount) FROM COB c LEFT JOIN EmployeeInfo ei ON c.Prepared = ei.EmployeeID \n" +
                "WHERE c.AppId = ? AND ei.DepartmentId = ?;");
        ps.setString(1, deptThreshold.getAppID());
        ps.setString(2, deptThreshold.getDepartmentID());

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            return rs.getDouble(1);
        }

        return 0;
    }

    public static String getTotalAppropriationsStr(DeptThreshold deptThreshold) throws Exception {
        return String.format("₱ %,.2f", getTotalAppropriations(deptThreshold));
    }
}
