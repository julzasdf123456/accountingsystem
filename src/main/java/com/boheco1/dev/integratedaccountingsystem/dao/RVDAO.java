package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RVDAO {
    public static int countRv(String dept) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT count(RVNo) AS count FROM RequisitionVoucher a INNER JOIN employeeinfo b ON a.Requistioner = b.EmployeeID INNER JOIN departments c ON b.DepartmentId = c.DepartmentID\n" +
                "WHERE DepartmentName = '"+dept+"'");
        if(rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    /**
     * Create RV and items
     * @param rv RV object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void createRV(RV rv) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String rv_sql = "INSERT INTO RequisitionVoucher (RVNo, [To], Purpose, Amount, Status, Requistioner, RVDate) " +
                "VALUES (?, ?, ?, ROUND(?, 2), ?, ?, getdate())";

        String item_sql = "INSERT INTO RVItem (RVNo, RVQty, CItemId, Sequence) " +
                "VALUES (?, ?, ?, ?)";

        //Prepared statements
        PreparedStatement ps_rv = DB.getConnection().prepareStatement(rv_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Insert COB
            ps_rv.setString(1, rv.getRvNo());
            ps_rv.setString(2, rv.getTo());
            ps_rv.setString(3, rv.getPurpose());
            ps_rv.setDouble(4, rv.getAmount());
            ps_rv.setString(5, COB.PENDING_REVIEW);
            ps_rv.setString(6, ActiveUser.getUser().getEmployeeID());
            ps_rv.executeUpdate();

            //Insert cob items
            for (RVItem item : rv.getItems()) {
                ps_item.setString(1, rv.getRvNo());
                ps_item.setInt(2, item.getQty());
                ps_item.setString(3, item.getcItemId());
                ps_item.setInt(4, item.getSequence());
                ps_item.executeUpdate();

            }
            //Commit insert
            conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_rv.close();
        ps_item.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }
}
