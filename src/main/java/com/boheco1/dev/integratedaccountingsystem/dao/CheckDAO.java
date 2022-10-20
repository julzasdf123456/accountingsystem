package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Check;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CheckDAO {

    /**
     * Retrieves list of check payments
     * @param bill The paid bill reference
     * @return a list of check payments
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Check> getCheckPayments(PaidBill bill) throws Exception {
        PreparedStatement ps = DB.getConnection("Billing").prepareStatement("SELECT * FROM CheckPayment WHERE AccountNumber = ? AND ServicePeriodEnd = ?");

        ps.setString(1, bill.getConsumer().getAccountID());
        ps.setDate(2, Date.valueOf(bill.getServicePeriodEnd()));

        ResultSet rs = ps.executeQuery();

        List<Check> list = new ArrayList<>();

        while(rs.next()) {
            Check c = new Check(rs.getString("Bank"), rs.getString("CheckNumber"), rs.getDouble("Amount"));
            list.add(c);
        }

        rs.close();
        ps.close();

        return list;
    }
}
