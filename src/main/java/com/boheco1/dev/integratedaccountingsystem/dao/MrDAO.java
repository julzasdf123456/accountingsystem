package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MR;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MrDAO {

    public static void add(MR mr) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MR (id, employeeId, warehousePersonnelId, extItem, stockID, quantity, price, dateOfMR, status) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)");

        mr.setId(Utility.generateRandomId());

        if(mr.getStockId()!=null) {
            Stock stock = StockDAO.get(mr.getStockId());
            mr.setPrice(stock!=null ? stock.getPrice() : null);
            mr.setExtItem(stock.getStockName());
        }

        ps.setString(1, mr.getId());
        ps.setString(2, mr.getEmployeeId());
        ps.setString(3, mr.getWarehousePersonnelId());
        ps.setString(4, mr.getExtItem());
        ps.setString(5, mr.getStockId());
        ps.setInt(6, mr.getQuantity());
        ps.setDouble(7, mr.getPrice());
        ps.setDate(8, java.sql.Date.valueOf(mr.getDateOfMR()));
        ps.setString(9, "active");

        ps.executeUpdate();

        ps.close();

    }

    public static MR get(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR WHERE id=?");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        if(!rs.next()) {
            return null;
        }

        MR mr = new MR();
        mr.setId(rs.getString("id"));
        mr.setEmployeeId(rs.getString("employeeId"));
        mr.setWarehousePersonnelId(rs.getString("warehousePersonnelId"));
        mr.setExtItem(rs.getString("extItem"));
        mr.setStockId(rs.getString("stockID"));
        mr.setQuantity(rs.getInt("quantity"));
        mr.setPrice(rs.getDouble("price"));
        mr.setDateOfMR(rs.getDate("dateOfMR").toLocalDate());
        mr.setStatus(rs.getString("status"));
        mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);

        rs.close();
        ps.close();

        return mr;
    }

    public static List<EmployeeInfo> getEmployeesWithMR() throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT * FROM EmployeeInfo WHERE EmployeeID IN (SELECT DISTINCT employeeId FROM MR)");

        ArrayList<EmployeeInfo> employees = new ArrayList();

        while(rs.next()) {
            employees.add(new EmployeeInfo(
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

        return employees;
    }

    public static List<MR> getMRsOfEmployee(EmployeeInfo employeeInfo) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR WHERE employeeId=?");
        ps.setString(1, employeeInfo.getId());

        ResultSet rs = ps.executeQuery();

        ArrayList<MR> mrs = new ArrayList();

        while(rs.next()) {
            MR mr = new MR();
            mr.setId(rs.getString("id"));
            mr.setEmployeeId(rs.getString("employeeId"));
            mr.setWarehousePersonnelId(rs.getString("warehousePersonnelId"));
            mr.setExtItem(rs.getString("extItem"));
            mr.setStockId(rs.getString("stockID"));
            mr.setQuantity(rs.getInt("quantity"));
            mr.setPrice(rs.getDouble("price"));
            mr.setDateOfMR(rs.getDate("dateOfMR").toLocalDate());
            mr.setStatus(rs.getString("status"));
            mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);

            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }
}
