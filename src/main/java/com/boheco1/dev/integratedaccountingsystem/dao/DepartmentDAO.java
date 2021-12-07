package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Department;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDAO {
    public static List<Department> getAll(Connection conn) throws SQLException {
//        CallableStatement cs = conn.prepareCall(("{call Get_all_departments}"));
        PreparedStatement cs = conn.prepareStatement("SELECT * FROM Departments ORDER BY DepartmentName");

        ResultSet rs = cs.executeQuery();
        ArrayList<Department> departments = new ArrayList();

        while(rs.next()) {
            departments.add(new Department(
                    rs.getString("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getString("DepartmentHead")
            ));
        }

        return departments;
    }

    public static Department get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Departments WHERE DepartmentID=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        Department dept = null;

        if(rs.next()) {
            dept = new Department(
                    rs.getString("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getString("DepartmentHead")
            );
        }

        rs.close();
        ps.close();

        return dept;
    }
}
