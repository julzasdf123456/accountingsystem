package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.ParticularsAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDefinition;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TransactionDefinitionDao {

    public static List<TransactionDefinition> get() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT * FROM TransactionDefinition ORDER BY TransactionCode");

        ArrayList<TransactionDefinition> result = new ArrayList<>();

        while(rs.next()) {
            result.add(new TransactionDefinition(
                    rs.getString("TransactionCode"),
                    rs.getString("Description")
            ));
        }

        return result;
    }
}
