package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.ParticularsAccount;

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
}
