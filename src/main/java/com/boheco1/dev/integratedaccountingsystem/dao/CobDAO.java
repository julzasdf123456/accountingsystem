package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.DeptThreshold;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CobDAO {
    public static List<COB> getAll(DeptThreshold dt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT c.* FROM COB c LEFT JOIN EmployeeInfo ei ON c.Prepared = ei.EmployeeID WHERE c.AppId = ? AND ei.DepartmentId =?");
        ps.setString(1, dt.getAppID());
        ps.setString(2, dt.getDepartmentID());
        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            cobs.add(new COB(
                    rs.getString("COBId"),
                    rs.getString("Activity"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("AppId"),
                    rs.getString("FSId"),
                    preparedBy,
                    rs.getDate("DatePrepared")==null? null: rs.getDate("DatePrepared").toLocalDate(),
                    reviewedBy,
                    rs.getDate("DateReviewed")==null? null: rs.getDate("DateReviewed").toLocalDate(),
                    approvedBy,
                    rs.getDate("DateApproved")==null? null: rs.getDate("DateApproved").toLocalDate()
            ));
        }

        return cobs;
    }
}
