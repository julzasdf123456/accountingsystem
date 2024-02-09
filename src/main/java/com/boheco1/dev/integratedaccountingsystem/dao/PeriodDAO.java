package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Period;

import javax.xml.transform.Result;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class PeriodDAO {
    public static void generate(int year) throws Exception {
        DB.getConnection().setAutoCommit(false);
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Periods (Period, Status) VALUES (?,?)");
        for(int i=1; i<=12; i++) {
            Period p = PeriodDAO.get(year, i);
            if(p!=null) continue;
            java.sql.Date dt = java.sql.Date.valueOf(year + "-" + i + "-01");
            ps.setDate(1, dt);
            ps.setString(2, "open");
            ps.addBatch();
        }

        ps.executeBatch();
        ps.close();
        DB.getConnection().setAutoCommit(true);
    }

    public static ArrayList<Period> get(int year) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Periods WHERE YEAR(Period)=?");
        ps.setInt(1, year);

        ResultSet rs = ps.executeQuery();

        ArrayList<Period> periods = new ArrayList<>();

        while(rs.next()) {
            periods.add(new Period(
                    rs.getTimestamp("Period").toLocalDateTime(),
                    rs.getString("Status"),
                    rs.getString("LockedBy"),
                    rs.getTimestamp("DateLocked")!=null ? rs.getTimestamp("DateLocked").toLocalDateTime() : null,
                    rs.getString("UnlockedBy"),
                    rs.getTimestamp("DateUnlocked")!=null ? rs.getTimestamp("DateUnlocked").toLocalDateTime() : null
            ));
        }

        rs.close();
        ps.close();

        return periods;
    }

    public static Period get(int year, int month) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Periods WHERE YEAR(Period)=? AND MONTH(Period)=?");
        ps.setInt(1, year);
        ps.setInt(2, month);
        ResultSet rs = ps.executeQuery();

        Period p = null;

        if(rs.next()) {
            p = new Period(
                    rs.getTimestamp("Period").toLocalDateTime(),
                    rs.getString("Status"),
                    rs.getString("LockedBy"),
                    rs.getTimestamp("DateLocked")!=null ? rs.getTimestamp("DateLocked").toLocalDateTime() : null,
                    rs.getString("UnlockedBy"),
                    rs.getTimestamp("DateUnlocked")!=null ? rs.getTimestamp("DateUnlocked").toLocalDateTime() : null
            );

        }

        rs.close();
        ps.close();

        return p;
    }

    public static void setLocked(Period p, boolean locked) throws Exception {
        DB.getConnection().setAutoCommit(true);
        PreparedStatement ps = null;

        if(locked) {
            ps = DB.getConnection().prepareStatement(
                    "UPDATE Periods SET status=?, LockedBy=?, DateLocked=SYSDATETIME() WHERE YEAR(Period)=? AND MONTH(Period)=?");
        }else {
            ps = DB.getConnection().prepareStatement(
                    "UPDATE Periods SET status=?, UnLockedBy=?, DateUnlocked=SYSDATETIME() WHERE YEAR(Period)=? AND MONTH(Period)=?");
        }

        ps.setString(1, locked ? "closed" : "open");
        ps.setString(2, ActiveUser.getUser().getUserName());
        ps.setInt(3, p.getPeriod().getYear());
        ps.setInt(4, p.getPeriod().getMonthValue());
        ps.executeUpdate();

        ps.close();
    }

    public static boolean isLocked(java.sql.Timestamp timestamp) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT status FROM Periods WHERE Period=?");
        ps.setTimestamp(1, timestamp);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) return rs.getString("status").equals("open");

        rs.close();
        ps.close();

        return false;
    }

    public static boolean isLocked(int year, int month) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT status FROM Periods WHERE YEAR(Period)=? AND MONTH(Period)=?");
        ps.setInt(1, year);
        ps.setInt(2, month);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) return rs.getString("status").equals("open");

        rs.close();
        ps.close();

        return false;
    }

    public static boolean isLocked(LocalDateTime localDateTime) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT status FROM Periods WHERE YEAR(Period)=? AND MONTH(Period)=?");
        int year = localDateTime.getYear();
        int month = localDateTime.getMonthValue();
        ps.setInt(1, year);
        ps.setInt(2, month);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) return rs.getString("status").equals("open");

        rs.close();
        ps.close();

        return false;
    }
}
