package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
     * Returns an RV based on the given RV number
     * @param rvno - RV number
     * @return
     * @throws Exception
     */
    public static RV get(String rvno) throws Exception {
        String sql = "SELECT RVNo" +
                "      ,[To]" +
                "      ,Purpose" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,Requistioner" +
                "      ,RVDate" +
                "      ,Recommended" +
                "      ,DateRecommended" +
                "      ,BudgetOfficer" +
                "      ,DateBudgeted" +
                "      ,Approved" +
                "      ,DateApproved" +
                "  FROM RequisitionVoucher " +
                "  WHERE RVNo = ?;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, rvno);
        ResultSet rs = ps.executeQuery();

        RV rv = null;

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Requistioner"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Recommended"), DB.getConnection());
            EmployeeInfo budgeted = EmployeeDAO.getOne(rs.getString("BudgetOfficer"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            rv = new RV(
                    rs.getString("RVNo"),
                    rs.getString("To"),
                    rs.getString("Purpose"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    preparedBy,
                    reviewedBy,
                    budgeted,
                    approvedBy,
                    rs.getDate("RVDate")==null? null: rs.getDate("RVDate").toLocalDate(),
                    rs.getDate("DateRecommended")==null? null: rs.getDate("DateRecommended").toLocalDate(),
                    rs.getDate("DateBudgeted")==null? null: rs.getDate("DateBudgeted").toLocalDate(),
                    rs.getDate("DateApproved")==null? null: rs.getDate("DateApproved").toLocalDate()
            );
        }

        return rv;
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
                "VALUES (?, ?, ?, ROUND(?, 2), ?, ?, ?)";

        String item_sql = "INSERT INTO RVItem (RVNo, RVQty, CItemId, Sequence) " +
                "VALUES (?, ?, ?, ?)";

        //Prepared statements
        PreparedStatement ps_rv = DB.getConnection().prepareStatement(rv_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Insert rv
            ps_rv.setString(1, rv.getRvNo());
            ps_rv.setString(2, rv.getTo());
            ps_rv.setString(3, rv.getPurpose());
            ps_rv.setDouble(4, rv.getAmount());
            ps_rv.setString(5, RV.PENDING_RECOMMENDATION);
            ps_rv.setString(6, ActiveUser.getUser().getEmployeeID());
            ps_rv.setDate(7, java.sql.Date.valueOf(rv.getRvDate()));
            ps_rv.executeUpdate();

            //Insert rv items
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

    /**
     * Approve RV (GM)
     * @param rv RV object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void approveRV(RV rv) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE RequisitionVoucher SET Approved = ?, Status = ?, DateApproved = getdate() WHERE RVNo=?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, RV.APPROVED);
        ps.setString(3, rv.getRvNo());
        ps.executeUpdate();
    }

    /**
     * Recommend RV (Dept. Head)
     * @param rv RV object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void recommendRV(RV rv) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE RequisitionVoucher SET Recommended = ?, Status = ?, DateRecommended = getdate() WHERE RVNo =?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, RV.PENDING_CERTIFICATION);
        ps.setString(3, rv.getRvNo());
        ps.executeUpdate();
    }

    /**
     * Review RV (Budget Officer)
     * @param rv RV object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void certifyRV(RV rv) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE RequisitionVoucher SET BudgetOfficer = ?, Status = ?, DateBudgeted = getdate() WHERE RVNo =?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, RV.PENDING_APPROVAL);
        ps.setString(3, rv.getRvNo());
        ps.executeUpdate();
    }

    /**
     * Revise RV (by Budget Officer/Dept. Head)
     * @param rv RV object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void reviseRV(RV rv) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE RequisitionVoucher SET Status = ?, Remarks = ?, Approved = NULL, Recommended = NULL, BudgetOfficer = NULL, DateRecommended = NULL, DateBudgeted = NULL, DateApproved = NULL WHERE RVNo = ?");
        ps.setString(1, RV.PENDING_REVISION);
        ps.setString(2, rv.getRemarks());
        ps.setString(3, rv.getRvNo());
        ps.executeUpdate();
    }

    /**
     * Submit revised RV by preparer
     * @param rv RV object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void submitRevisedRV(RV rv) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE RequisitionVoucher SET Status = ?, [To] = ?, Purpose = ?, Requistioner = ?, Remarks = NULL, Approved = NULL, Recommended = NULL, BudgetOfficer = NULL, DateRecommended = NULL, DateBudgeted = NULL, DateApproved = NULL, RVDate = ? WHERE RVNo = ?");
        ps.setString(1, RV.PENDING_RECOMMENDATION);
        ps.setString(2, rv.getTo());
        ps.setString(3, rv.getPurpose());
        ps.setString(4, ActiveUser.getUser().getEmployeeID());
        ps.setDate(5, java.sql.Date.valueOf(rv.getRvDate()));
        ps.setString(6, rv.getRvNo());
        ps.executeUpdate();
    }

    /**
     * Returns a list of all RVs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param year - Year of RV
     * @param dept - Department who prepared the RV
     * @param status - RV status
     * @return list of RVs
     * @throws Exception
     */
    public static List<RV> getAll(String year, Department dept, String status) throws Exception {
        String sql = "SELECT RVNo" +
                "      ,[To]" +
                "      ,Purpose" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,Requistioner" +
                "      ,RVDate" +
                "      ,Recommended" +
                "      ,DateRecommended" +
                "      ,BudgetOfficer" +
                "      ,DateBudgeted" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,(SELECT COUNT(RVItemId) FROM RVItem b WHERE r.RVNo = b.RVNo) AS NoItems" +
                "  FROM RequisitionVoucher r INNER JOIN EmployeeInfo ei ON ei.EmployeeID = r.Requistioner INNER JOIN Departments d ON ei.DepartmentId = d.DepartmentID" +
                "  WHERE RVDate LIKE ? AND d.DepartmentId =? AND Status = ?" +
                "  ORDER BY RVDate DESC, RVNo ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, "%"+year+"%");
        ps.setString(2, dept.getDepartmentID());
        ps.setString(3, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<RV> items = new ArrayList<>();

        RV rv = null;

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Requistioner"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Recommended"), DB.getConnection());
            EmployeeInfo budgeted = EmployeeDAO.getOne(rs.getString("BudgetOfficer"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            rv = new RV(
                    rs.getString("RVNo"),
                    rs.getString("To"),
                    rs.getString("Purpose"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    preparedBy,
                    reviewedBy,
                    budgeted,
                    approvedBy,
                    rs.getDate("RVDate")==null? null: rs.getDate("RVDate").toLocalDate(),
                    rs.getDate("DateRecommended")==null? null: rs.getDate("DateRecommended").toLocalDate(),
                    rs.getDate("DateBudgeted")==null? null: rs.getDate("DateBudgeted").toLocalDate(),
                    rs.getDate("DateApproved")==null? null: rs.getDate("DateApproved").toLocalDate()
            );
            rv.setNoOfItems(rs.getInt("NoItems"));
            items.add(rv);
        }
        return items;
    }

    /**
     * Returns a list of all RVs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param year - Year of RV
     * @param status - RV status
     * @return list of RVs
     * @throws Exception
     */
    public static List<RV> getAll(String year, String status) throws Exception {
        String sql = "SELECT RVNo" +
                "      ,[To]" +
                "      ,Purpose" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,Requistioner" +
                "      ,RVDate" +
                "      ,Recommended" +
                "      ,DateRecommended" +
                "      ,BudgetOfficer" +
                "      ,DateBudgeted" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,(SELECT COUNT(RVItemId) FROM RVItem b WHERE r.RVNo = b.RVNo) AS NoItems" +
                "  FROM RequisitionVoucher r" +
                "  WHERE RVDate LIKE ? AND Status = ?" +
                "  ORDER BY RVDate DESC, RVNo ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, "%"+year+"%");
        ps.setString(2, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<RV> items = new ArrayList<>();

        RV rv = null;

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Requistioner"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Recommended"), DB.getConnection());
            EmployeeInfo budgeted = EmployeeDAO.getOne(rs.getString("BudgetOfficer"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            rv = new RV(
                    rs.getString("RVNo"),
                    rs.getString("To"),
                    rs.getString("Purpose"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    preparedBy,
                    reviewedBy,
                    budgeted,
                    approvedBy,
                    rs.getDate("RVDate")==null? null: rs.getDate("RVDate").toLocalDate(),
                    rs.getDate("DateRecommended")==null? null: rs.getDate("DateRecommended").toLocalDate(),
                    rs.getDate("DateBudgeted")==null? null: rs.getDate("DateBudgeted").toLocalDate(),
                    rs.getDate("DateApproved")==null? null: rs.getDate("DateApproved").toLocalDate()
            );
            rv.setNoOfItems(rs.getInt("NoItems"));
            items.add(rv);
        }
        return items;
    }
}
