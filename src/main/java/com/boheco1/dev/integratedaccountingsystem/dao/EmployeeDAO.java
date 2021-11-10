package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;

import java.sql.*;

public class EmployeeDAO {

    public static void addEmployee(EmployeeInfo employee, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{? = call Add_employee (?,?,?,?,?,?,?)}");
        cs.registerOutParameter(1, Types.INTEGER);
        cs.setString(2, employee.getEmployeeFirstName());
        cs.setString(3, employee.getEmployeeMidName());
        cs.setString(4, employee.getEmployeeLastName());
        cs.setString(6, employee.getEmployeeAddress());
        cs.setString(5, employee.getPhone());
        cs.setString(7, employee.getDesignation());
        cs.setInt(8, employee.getDepartmentID());

        cs.executeUpdate();

        int id = cs.getInt(1);

        employee.setId(id);
    }

    public static EmployeeInfo getOne(int id, Connection conn) throws SQLException {
        CallableStatement cs = conn.prepareCall("{call Get_employee (?)}");
        cs.setInt(1, id);
        ResultSet rs = cs.executeQuery();

        if(rs.next()) {
            return new EmployeeInfo(
                    rs.getInt("EmployeeID"),
                    rs.getString("EmployeeFirstName"),
                    rs.getString("EmployeeMidName"),
                    rs.getString("EmployeeLastName"),
                    rs.getString("Address"),
                    rs.getString("Phone"),
                    rs.getString("Designation"),
                    rs.getInt("DepartmentID")
            );
        }else {
            return null;
        }
    }
}
