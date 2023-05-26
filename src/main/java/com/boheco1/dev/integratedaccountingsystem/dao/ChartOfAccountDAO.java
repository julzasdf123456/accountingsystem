package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.ChartOfAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ChartOfAccountDAO {
    public static List<ChartOfAccount> search(String q) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM ChartOfAccounts WHERE Status = 'ACTIVE' AND (AccountCode LIKE '%" + q + "%' OR Description LIKE '%" + q + "%') ");

        ResultSet rs = ps.executeQuery();
        ArrayList<ChartOfAccount> result = new ArrayList<>();

        while(rs.next()) {
            ChartOfAccount ca = new ChartOfAccount(rs.getString("AccountCode"),
                    rs.getString("Description"),
                    rs.getString("AccountType"),
                    rs.getInt("LevelNumber"),
                    rs.getString("SummaryAccount"),
                    rs.getString("GLSL"),
                    rs.getString("OldAccountCode"),
                    rs.getString("NewDesciption"),
                    rs.getString("OldSummaryAccount"),
                    rs.getString("Status"));
            result.add(ca);
        }

        rs.close();
        ps.close();

        return result;
    }
}
