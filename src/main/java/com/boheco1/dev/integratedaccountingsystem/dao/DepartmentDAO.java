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
                    rs.getInt("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getInt("DepartmentHead")
            ));
        }

        return departments;
    }

    public static Department get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Departments WHERE DepartmentID=?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        Department dept = null;

        if(rs.next()) {
            dept = new Department(
                    rs.getInt("DepartmentID"),
                    rs.getString("DepartmentName"),
                    rs.getInt("DepartmentHead")
            );
        }

        rs.close();
        ps.close();

        return dept;
    }
}
