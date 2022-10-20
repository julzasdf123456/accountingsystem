package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.CRMDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CRMDAO {
    /**
     * Retrieves a CRMDetails based on ID Number
     * @param id The CRMQueue id number
     * @return A ConsumerInfo
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<CRMDetails> getConsumerTransaction(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM CRMDetails WHERE ReferenceNo = ?");

        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        List<CRMDetails> list = new ArrayList<>();

        while(rs.next()) {
            CRMDetails c = new CRMDetails(
                    rs.getString("id"),
                    rs.getString("ReferenceNo"),
                    rs.getString("Particular"),
                    rs.getString("GLCode"),
                    rs.getDouble("SubTotal"),
                    rs.getDouble("VAT"),
                    rs.getDouble("Total")
            );
            list.add(c);
        }

        rs.close();
        ps.close();

        return list;
    }
}
