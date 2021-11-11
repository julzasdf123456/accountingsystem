package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.Department;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    public static List<Department> getAll(Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall(("{call Get_all_departments}"));
        ResultSet rs = cs.executeQuery();
        ArrayList<Department> departments = new ArrayList();

        while(rs.next()) {
            departments.add(new Department(
                    rs.getInt("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getInt("DepartmentHead")
            ));
        }

        return departments;
    }
}
