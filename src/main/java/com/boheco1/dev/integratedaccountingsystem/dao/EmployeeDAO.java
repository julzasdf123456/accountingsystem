package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EmployeeDAO {

    public static void addEmployee(EmployeeInfo employee, Connection conn) throws SQLException {
//        CallableStatement cs = conn.prepareCall("{? = call Add_employee (?,?,?,?,?,?,?)}");
        PreparedStatement cs = conn.prepareStatement(
                "INSERT INTO EmployeeInfo (EmployeeFirstName, EmployeeMidName, EmployeeLastName, EmployeeSuffix, Address, Phone, Designation, DepartmentID, SignatoryLevel, EmployeeID) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?)");

        employee.setId(Utility.generateRandomId());
        cs.setString(1, employee.getEmployeeFirstName());
        cs.setString(2, employee.getEmployeeMidName());
        cs.setString(3, employee.getEmployeeLastName());
        cs.setString(4, employee.getEmployeeSuffix());
        cs.setString(5, employee.getEmployeeAddress());
        cs.setString(6, employee.getPhone());
        cs.setString(7, employee.getDesignation());
        cs.setString(8, employee.getDepartmentID());
        cs.setString(9, employee.getSignatoryLevel());
        cs.setString(10, employee.getId() );

        cs.executeUpdate();

        cs.close();
    }

    public static EmployeeInfo getOne(String id, Connection conn) throws Exception {
        PreparedStatement cs = conn.prepareStatement(
                "SELECT * FROM EmployeeInfo WHERE EmployeeID=?");
        cs.setString(1, id);
        ResultSet rs = cs.executeQuery();

        if(rs.next()) {
            return new EmployeeInfo(
                    rs.getString("EmployeeID"),
                    rs.getString("EmployeeFirstName"),
                    rs.getString("EmployeeMidName"),
                    rs.getString("EmployeeLastName"),
                    rs.getString("EmployeeSuffix"),
                    rs.getString("Address"),
                    rs.getString("Phone"),
                    rs.getString("Designation"),
                    rs.getString("SignatoryLevel"),
                    rs.getString("DepartmentID")
            );
        }else {
            return null;
        }
    }

    public static List<EmployeeInfo> getAll(Connection conn) throws Exception
    {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM EmployeeInfo " +
                        "ORDER BY EmployeeLastName, EmployeeFirstName");
        ResultSet rs = ps.executeQuery();

        ArrayList<EmployeeInfo> employeeInfos = new ArrayList<>();

        while(rs.next()) {
            employeeInfos.add(new EmployeeInfo(
                    rs.getString("EmployeeID"),
                    rs.getString("EmployeeFirstName"),
                    rs.getString("EmployeeMidName"),
                    rs.getString("EmployeeLastName"),
                    rs.getString("EmployeeSuffix"),
                    rs.getString("Address"),
                    rs.getString("Phone"),
                    rs.getString("Designation"),
                    rs.getString("SignatoryLevel"),
                    rs.getString("DepartmentID")
            ));
        }

        rs.close();
        ps.close();

        return employeeInfos;
    }

    public static void update(EmployeeInfo employeeInfo) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE EmployeeInfo SET " +
                        "EmployeeFirstName=?, EmployeeMidName=?, EmployeeLastName=?, EmployeeSuffix=?, " +
                        "Address=?, Designation=?, DepartmentId=?, Phone=?, SignatoryLevel=? " +
                        "WHERE EmployeeID=?");
        ps.setString(1, employeeInfo.getEmployeeFirstName());
        ps.setString(2, employeeInfo.getEmployeeMidName());
        ps.setString(3, employeeInfo.getEmployeeLastName());
        ps.setString(4, employeeInfo.getEmployeeSuffix());
        ps.setString(5, employeeInfo.getEmployeeAddress());
        ps.setString(6, employeeInfo.getDesignation());
        ps.setString(7, employeeInfo.getDepartmentID());
        ps.setString(8, employeeInfo.getPhone());
        ps.setString(9, employeeInfo.getSignatoryLevel());
        ps.setString(10, employeeInfo.getId());

        ps.executeUpdate();

        ps.close();
    }

    /**
     * Retrieves a list of EmployeeInfo as a search result based on a search Key
     * @param key The search key
     * @return A list of EmployeeInfo that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<EmployeeInfo> getEmployeeInfo(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "Select EmployeeID, EmployeeFirstName, EmployeeMidName, EmployeeLastName, Designation FROM EmployeeInfo " +
                        "WHERE (EmployeeFirstName LIKE ? OR EmployeeMidName LIKE ? OR EmployeeLastName LIKE ? ) " +
                        "ORDER BY EmployeeLastName");
        ps.setString(1, '%'+ key+'%');
        ps.setString(2, '%'+ key+'%');
        ps.setString(3, '%'+ key+'%');

        ResultSet rs = ps.executeQuery();

        ArrayList<EmployeeInfo> list = new ArrayList<>();
        while(rs.next()) {
            EmployeeInfo employeeInfo = new EmployeeInfo();
            employeeInfo.setId(rs.getString("EmployeeID"));
            employeeInfo.setEmployeeFirstName(rs.getString("EmployeeFirstName"));
            employeeInfo.setEmployeeMidName(rs.getString("EmployeeMidName"));
            employeeInfo.setEmployeeLastName(rs.getString("EmployeeLastName"));
            employeeInfo.setDesignation(rs.getString("Designation"));
            list.add(employeeInfo);
        }

        rs.close();
        ps.close();

        return list;
    }

}
