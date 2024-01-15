package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.ParticularsAccount;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ParticularsAccountDAO {
    public static List<ParticularsAccount> getAll() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT * FROM ParticularsAccount ORDER BY Particulars");

        ArrayList<ParticularsAccount> pas = new ArrayList<>();

        while(rs.next()) {
            pas.add(new ParticularsAccount(
                    rs.getString("Particulars"),
                    rs.getString("AccountCode"),
                    rs.getDouble("Amount")
            ));
        }

        return pas;
    }

    public static List<ParticularsAccount> getByType(String type) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM ParticularsAccount " +
                        "WHERE type LIKE '%" + type + "%' " +
                        "ORDER BY Particulars");

        ArrayList<ParticularsAccount> pas = new ArrayList<>();

        while(rs.next()) {
            pas.add(new ParticularsAccount(
                    rs.getString("Particulars"),
                    rs.getString("AccountCode"),
                    rs.getDouble("Amount")
            ));
        }

        return pas;
    }

    public static List<ParticularsAccount> get(String particulars) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM ParticularsAccount " +
                        "WHERE Particulars LIKE '%" + particulars + "%' OR AccountCode LIKE '%" + particulars + "%' " +
                        "ORDER BY Particulars");

        ArrayList<ParticularsAccount> pas = new ArrayList<>();

        while(rs.next()) {
            pas.add(new ParticularsAccount(
                    rs.getString("Particulars"),
                    rs.getString("AccountCode"),
                    rs.getDouble("Amount")
            ));
        }

        return pas;
    }

    public static List<ParticularsAccount> get(Connection con, String particulars) throws Exception {
        ResultSet rs = con.createStatement().executeQuery(
                "SELECT * FROM ParticularsAccount " +
                        "WHERE Particulars LIKE '%" + particulars + "%' OR AccountCode LIKE '%" + particulars + "%' " +
                        "ORDER BY Particulars");

        ArrayList<ParticularsAccount> pas = new ArrayList<>();

        while(rs.next()) {
            pas.add(new ParticularsAccount(
                    rs.getString("Particulars"),
                    rs.getString("AccountCode"),
                    rs.getDouble("Amount")
            ));
        }

        return pas;
    }

    public static ParticularsAccount getParticularsAccount(String particulars) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM ParticularsAccount " +
                        "WHERE Particulars LIKE '%" + particulars + "%' " +
                        "ORDER BY Particulars");

        ParticularsAccount result = null;

        if(rs.next()) {
            result = new ParticularsAccount(
                    rs.getString("Particulars"),
                    rs.getString("AccountCode"),
                    rs.getDouble("Amount")
            );
        }

        return result;
    }
}
