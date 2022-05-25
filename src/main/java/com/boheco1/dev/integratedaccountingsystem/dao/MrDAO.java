package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MR;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockEntryLog;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
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
            mr.setExtItem(stock.getDescription());
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
        mr.setRemarks(rs.getString("remarks"));
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

    public static List<EmployeeInfo> getEmployeesWithMR(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM EmployeeInfo WHERE EmployeeID IN (SELECT DISTINCT employeeId FROM MR) " +
                        "AND (EmployeeFirstName LIKE ? OR EmployeeMidName LIKE ? OR EmployeeLastName LIKE ? ) " +
                        "ORDER BY EmployeeLastName");

        ps.setString(1, '%'+ key+'%');
        ps.setString(2, '%'+ key+'%');
        ps.setString(3, '%'+ key+'%');

        ResultSet rs = ps.executeQuery();

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
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR WHERE employeeId=? ORDER BY dateOfMR DESC");
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
            mr.setRemarks(rs.getString("remarks"));
            mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);

            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }

    public static void returnMR(MR mr, String remarks) throws Exception {
        Stock stock = StockDAO.get(mr.getStockId());
        if (stock == null){
            stock = new Stock();
            stock.setId(mr.getStockId());
            stock.setQuantity(0);
            stock.setStockName(mr.getExtItem());
            stock.setDescription(mr.getExtItem());
            stock.setPrice(mr.getPrice());

            StockDAO.add(stock);
        }
        StockEntryLog entry = new StockEntryLog();
        entry.setSource("Returned");
        entry.setQuantity(mr.getQuantity());
        entry.setPrice(mr.getPrice());

        StockDAO.stockEntry(stock, entry);


        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MR SET status=?, remarks=?, dateOfReturn=?, stockID=? WHERE id=?");
        ps.setString(1, Utility.MR_RETURNED);
        ps.setString(2, remarks);
        ps.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
        ps.setString(4, stock.getId());
        ps.setString(5, mr.getId());
        ps.executeUpdate();

        ps.close();
    }

    public static int countMRs(String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT COUNT(id) AS 'count' FROM MR WHERE status=?");
        ps.setString(1, status);
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            return rs.getInt("count");
        }

        return 0;
    }

    public static int countEmployeesWithMRs() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT COUNT(employeeId) AS 'count' FROM EmployeeInfo WHERE employeeId IN (SELECT employeeId FROM MR WHERE status='active')");
        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            return rs.getInt("count");
        }

        return 0;
    }

    public static List<MR> getMRs(String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR INNER JOIN EmployeeInfo ON MR.EmployeeID = EmployeeInfo.EmployeeID " +
                "WHERE status=? ORDER BY extItem ASC");
        ps.setString(1, status);

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
            mr.setRemarks(rs.getString("remarks"));
            mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);
            mr.setEmployeeFirstName(rs.getString("employeeFirstName"));
            mr.setEmployeeLastName(rs.getString("employeeLastName"));
            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }

    public static List<MR> getMRsByDescription(String desc, String stockID) throws Exception {
        PreparedStatement ps = null;
        String key = desc;
        if (stockID == null){
            ps = DB.getConnection().prepareStatement("SELECT * FROM MR INNER JOIN EmployeeInfo ON MR.EmployeeID = EmployeeInfo.EmployeeID " +
                    "WHERE extItem=? ORDER BY dateOfMR DESC");
        }else{
            ps = DB.getConnection().prepareStatement("SELECT * FROM MR INNER JOIN EmployeeInfo ON MR.EmployeeID = EmployeeInfo.EmployeeID " +
                    "WHERE stockID=? ORDER BY dateOfMR DESC");
            key = stockID;
        }
        ps.setString(1, key);

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
            mr.setRemarks(rs.getString("remarks"));
            mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);
            mr.setEmployeeFirstName(rs.getString("employeeFirstName"));
            mr.setEmployeeLastName(rs.getString("employeeLastName"));
            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }

    public static List<MR> searchMRs(String key, String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MR INNER JOIN EmployeeInfo ON MR.EmployeeID = EmployeeInfo.EmployeeID " +
                "WHERE status=? AND (extItem LIKE ? OR employeeFirstName LIKE ? OR employeeLastName LIKE ? OR stockID LIKE ?) ORDER BY extItem ASC");
        ps.setString(1, status);
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, "%" + key + "%");
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
            mr.setRemarks(rs.getString("remarks"));
            mr.setDateOfReturn(rs.getDate("dateOfReturn")!=null ? rs.getDate("dateOfReturn").toLocalDate(): null);
            mr.setEmployeeFirstName(rs.getString("employeeFirstName"));
            mr.setEmployeeLastName(rs.getString("employeeLastName"));
            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }
}
