package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MrDAO {

    public static void add(MR mr) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MR (id, employeeId, dateOfMR, status, warehousePersonnelId, recommending, approvedBy, purpose) " +
                        "VALUES (?,?,?,?,?,?,?,?)");

        ps.setString(1, mr.getId());
        ps.setString(2, mr.getEmployeeId());
        ps.setDate(3, java.sql.Date.valueOf(mr.getDateOfMR()));
        ps.setString(4, mr.getStatus());
        ps.setString(5, mr.getWarehousePersonnelId());
        ps.setString(6, mr.getRecommending());
        ps.setString(7, mr.getApprovedBy());
        ps.setString(8, mr.getPurpose());
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

        MR mr = new MR(
                rs.getString("id"),
                rs.getString("employeeId"),
                rs.getString("warehousePersonnelId"),
                rs.getDate("dateOfMR").toLocalDate(),
                rs.getString("status"),
                rs.getString("recommending"),
                rs.getString("approvedBy"),
                rs.getString("purpose")
        );
        mr.setPurpose(rs.getString("purpose"));
        mr.setDateApproved(rs.getDate("dateApproved")!=null ? rs.getDate("dateApproved").toLocalDate(): null);
        mr.setDateRecommended(rs.getDate("dateRecommended")!=null ? rs.getDate("dateRecommended").toLocalDate(): null);
        mr.setDateReleased(rs.getDate("dateReleased")!=null ? rs.getDate("dateReleased").toLocalDate(): null);
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
            MR mr = new MR(
                    rs.getString("id"),
                    rs.getString("employeeId"),
                    rs.getString("warehousePersonnelId"),
                    rs.getDate("dateOfMR").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("recommending"),
                    rs.getString("approvedBy"),
                    rs.getString("purpose")
            );
            mr.setPurpose(rs.getString("purpose"));
            mr.setDateApproved(rs.getDate("dateApproved")!=null ? rs.getDate("dateApproved").toLocalDate(): null);
            mr.setDateRecommended(rs.getDate("dateRecommended")!=null ? rs.getDate("dateRecommended").toLocalDate(): null);
            mr.setDateReleased(rs.getDate("dateReleased")!=null ? rs.getDate("dateReleased").toLocalDate(): null);

            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
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
            MR mr = new MR(
                    rs.getString("id"),
                    rs.getString("employeeId"),
                    rs.getString("warehousePersonnelId"),
                    rs.getDate("dateOfMR").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("recommending"),
                    rs.getString("approvedBy"),
                    rs.getString("purpose")
            );
            mr.setPurpose(rs.getString("purpose"));
            mr.setDateApproved(rs.getDate("dateApproved")!=null ? rs.getDate("dateApproved").toLocalDate(): null);
            mr.setDateRecommended(rs.getDate("dateRecommended")!=null ? rs.getDate("dateRecommended").toLocalDate(): null);
            mr.setDateReleased(rs.getDate("dateReleased")!=null ? rs.getDate("dateReleased").toLocalDate(): null);
            mr.setEmployeeInfo(new EmployeeInfo(
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
            MR mr = new MR(
                    rs.getString("id"),
                    rs.getString("employeeId"),
                    rs.getString("warehousePersonnelId"),
                    rs.getDate("dateOfMR").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("recommending"),
                    rs.getString("approvedBy"),
                    rs.getString("purpose")
            );
            mr.setPurpose(rs.getString("purpose"));
            mr.setDateApproved(rs.getDate("dateApproved")!=null ? rs.getDate("dateApproved").toLocalDate(): null);
            mr.setDateRecommended(rs.getDate("dateRecommended")!=null ? rs.getDate("dateRecommended").toLocalDate(): null);
            mr.setDateReleased(rs.getDate("dateReleased")!=null ? rs.getDate("dateReleased").toLocalDate(): null);
            mr.setEmployeeInfo(new EmployeeInfo(
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
            MR mr = new MR(
                    rs.getString("id"),
                    rs.getString("employeeId"),
                    rs.getString("warehousePersonnelId"),
                    rs.getDate("dateOfMR").toLocalDate(),
                    rs.getString("status"),
                    rs.getString("recommending"),
                    rs.getString("approvedBy"),
                    rs.getString("purpose")
            );
            mr.setPurpose(rs.getString("purpose"));
            mr.setDateApproved(rs.getDate("dateApproved")!=null ? rs.getDate("dateApproved").toLocalDate(): null);
            mr.setDateRecommended(rs.getDate("dateRecommended")!=null ? rs.getDate("dateRecommended").toLocalDate(): null);
            mr.setDateReleased(rs.getDate("dateReleased")!=null ? rs.getDate("dateReleased").toLocalDate(): null);
            mr.setEmployeeInfo(new EmployeeInfo(
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
            mrs.add(mr);
        }

        rs.close();
        ps.close();

        return mrs;
    }

    public static List<MrItem> searchMRItems(String key, String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MRItem  "+
                "WHERE status=? AND (StockID LIKE ? OR ItemName LIKE ? OR Description LIKE ? OR mr_no LIKE ? OR PropertyNo LIKE ?) ORDER BY Description ASC, mr_no DESC");
        ps.setString(1, status);
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, "%" + key + "%");
        ps.setString(6, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );

            item.setItemName(rs.getString("ItemName"));
            item.setDescription(rs.getString("Description"));

            item.setRrNo(rs.getString("RRNo"));
            item.setStatus(rs.getString("Status"));
            item.setPrice(rs.getDouble("Price"));
            item.setUpdatedBy(rs.getString("updatedBy"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            mrItems.add(item);
        }

        rs.close();
        ps.close();

        return mrItems;
    }

    public static List<MrItem> getMRItems() throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MRItem  "+
                "WHERE status=? ORDER BY Description ASC, mr_no DESC");
        ps.setString(1, Utility.MR_ACTIVE);
        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );

            item.setItemName(rs.getString("ItemName"));
            item.setDescription(rs.getString("Description"));

            item.setRrNo(rs.getString("RRNo"));
            item.setStatus(rs.getString("Status"));
            item.setPrice(rs.getDouble("Price"));
            item.setUpdatedBy(rs.getString("updatedBy"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            mrItems.add(item);
        }

        rs.close();
        ps.close();

        return mrItems;
    }

    public static List<MrItem> getMRItemsOther(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM MRItem WHERE Description=? ORDER BY Description ASC, mr_no DESC");
        ps.setString(1, id);
        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );

            item.setItemName(rs.getString("ItemName"));
            item.setDescription(rs.getString("Description"));

            item.setRrNo(rs.getString("rrno"));
            item.setStatus(rs.getString("Status"));
            item.setPrice(rs.getDouble("Price"));
            item.setUpdatedBy(rs.getString("updatedBy"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            mrItems.add(item);
        }

        rs.close();
        ps.close();

        return mrItems;
    }

    public static List<MrItem> getMRItems(String stock_id, String status) throws Exception {
        String sql = "";
        PreparedStatement ps = null;
        if (status == null){
            sql = "SELECT MRItem.id, mr_no, MRItem.StockID, Qty, Stocks.Description, StockName, MRItem.ItemName, MRItem.Description, StockEntryLogs.RRNo, Remarks, dateOfReturned, MRItem.status, StockEntryLogs.Price, updatedBy, PropertyNo "+
                    "FROM MR INNER JOIN MRItem ON MR.id = MRItem.mr_no INNER JOIN Stocks ON MRItem.StockID = Stocks.id INNER JOIN StockEntryLogs ON StockEntryLogs.StockID = Stocks.id "+
                    "WHERE Stocks.id=? ORDER BY Stocks.Description ASC, mr_no DESC";
            ps = DB.getConnection().prepareStatement(sql);
            ps.setString(1, stock_id);
        }else{
            sql = "SELECT MRItem.id, mr_no, MRItem.StockID, Qty, Stocks.Description, StockName, MRItem.ItemName, MRItem.Description, StockEntryLogs.RRNo, Remarks, dateOfReturned, MRItem.status, StockEntryLogs.Price, updatedBy, PropertyNo "+
                    "FROM MR INNER JOIN MRItem ON MR.id = MRItem.mr_no INNER JOIN Stocks ON MRItem.StockID = Stocks.id INNER JOIN StockEntryLogs ON StockEntryLogs.StockID = Stocks.id "+
                    "WHERE MRItem.status=? AND Stocks.id=? ORDER BY Stocks.Description ASC, mr_no DESC";
            ps = DB.getConnection().prepareStatement(sql);
            ps.setString(1, status);
            ps.setString(2, stock_id);
        }

        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );
            item.setItemName(rs.getString("ItemName"));
            item.setDescription(rs.getString("Description"));
            item.setRrNo(rs.getString("RRNo"));
            item.setStatus(rs.getString("Status"));
            item.getStock().setPrice(rs.getDouble("Price"));
            item.setUpdatedBy(rs.getString("updatedBy"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            mrItems.add(item);
        }

        rs.close();
        ps.close();

        return mrItems;
    }

    public static void createItem(MR mr, MrItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MRItem (id, StockID, ItemName, Description, Qty, Remarks, mr_no, rrno, Status, Price, PropertyNo) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?)");

        if(item.getId()==null) item.setId(Utility.generateRandomId());

        ps.setString(1, item.getId());
        ps.setString(2, item.getStockID());
        ps.setString(3, item.getItemName());
        ps.setString(4, item.getDescription());
        ps.setInt(5, item.getQty());
        ps.setString(6, item.getRemarks());
        ps.setString(7, mr.getId());
        ps.setString(8, item.getRrNo());
        ps.setString(9, Utility.MR_ACTIVE);
        ps.setDouble(10, item.getPrice());
        ps.setString(11, item.getPropertyNo());
        ps.executeUpdate();

        ps.close();
        if (item.getStockID() != null) {
            Stock stock = item.getStock();
            StockDAO.deductStockQuantity(stock, item.getQty());
        }
    }

    public static MrItem getItem(String id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MRItem WHERE id=?");
        ps.setString(1, id);

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );
            if(item.getStockID()==null) {
                item.setItemName(rs.getString("ItemName"));
                item.setDescription(rs.getString("Description"));
            }
            item.setRrNo(rs.getString("rrno"));
            item.setStatus(rs.getString("Status"));
            item.setUpdatedBy(rs.getString("updatedBy"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            item.setPrice(rs.getDouble("Price"));
            return item;
        }

        ps.close();
        rs.close();

        return null;
    }

    public static List<MrItem> getItems(MR mr) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MRItem WHERE mr_no=?");
        ps.setString(1, mr.getId());

        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );
            if(item.getStockID()==null) {
                item.setItemName(rs.getString("ItemName"));
                item.setDescription(rs.getString("Description"));
            }
            item.setRrNo(rs.getString("rrno"));
            item.setStatus(rs.getString("Status"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            item.setPrice(rs.getDouble("Price"));
            mrItems.add(item);
        }

        rs.close();
        ps.close();

        return mrItems;
    }

    public static List<MrItem> searchAvailableMRItem(String key, String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT TOP 1 * FROM MRItem WHERE (ItemName LIKE ? OR Description LIKE ?) AND StockID IS NULL AND Status = ? AND description NOT IN (SELECT description FROM mritem WHERE status = 'active') ORDER BY ORDER BY mr_no DESC");

        ps.setString(1, "%"+key+"%");
        ps.setString(2, "%"+key+"%");
        ps.setString(3, status);

        ResultSet rs = ps.executeQuery();

        List<MrItem> mrItems = new ArrayList();

        while(rs.next()) {
            MrItem item = new MrItem(
                    rs.getString("id"),
                    rs.getString("mr_no"),
                    rs.getString("StockID"),
                    rs.getInt("Qty"),
                    rs.getString("Remarks")
            );
            item.setItemName(rs.getString("ItemName"));
            item.setDescription(rs.getString("Description"));
            item.setRrNo(rs.getString("rrno"));
            item.setStatus(rs.getString("Status"));
            item.setPropertyNo(rs.getString("PropertyNo"));
            item.setDateReturned(rs.getDate("dateOfReturned")!=null ? rs.getDate("dateOfReturned").toLocalDate(): null);
            item.setPrice(rs.getDouble("Price"));
            mrItems.add(item);
        }

        rs.close();
        ps.close();
        return mrItems;
    }

    public static void removeItem(MrItem mrItem) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "DELETE FROM MRItem WHERE id=?");
        ps.setString(1, mrItem.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static void returnMRItem(MrItem item, String remarks) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MRItem SET updatedBy=?, Remarks=?, Status=?, dateOfReturned=? " +
                        "WHERE id=?");
        ps.setString(1, ActiveUser.getUser().getId());
        ps.setString(2, remarks);
        ps.setString(3, Utility.MR_RETURNED);
        ps.setString(4, LocalDate.now().toString());
        ps.setString(5, item.getId());
        ps.executeUpdate();

        ps.close();

        if (item.getStockID() != null) {
            Stock stock = item.getStock();
            StockDAO.addStockQuantity(stock, item.getQty());
        }
    }
}
