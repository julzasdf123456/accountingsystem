package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class PODAO {
    public static int countPo(String dept) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT count(PONo) AS count FROM PurchaseOrder a INNER JOIN employeeinfo b ON a.Prepared = b.EmployeeID INNER JOIN departments c ON b.DepartmentId = c.DepartmentID\n" +
                "WHERE DepartmentName = '"+dept+"'");
        if(rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    /**
     * Returns a PO based on the given PO number
     * @param no - RV number
     * @return
     * @throws Exception
     */
    public static PurchaseOrder get(String no) throws Exception {
        String sql = "SELECT PONo" +
                "      ,PODate" +
                "      ,[To]" +
                "      ,Address" +
                "      ,Contact" +
                "      ,Terms" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,DateBoard" +
                "      ,DateAccepted" +
                "      ,GeneralManager" +
                "      ,Prepared" +
                "  FROM PurchaseOrder " +
                "  WHERE PONo = ?;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, no);
        ResultSet rs = ps.executeQuery();

        PurchaseOrder po = null;

        while(rs.next()) {
            EmployeeInfo prepared = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo gm = EmployeeDAO.getOne(rs.getString("GeneralManager"), DB.getConnection());
            po = new PurchaseOrder(
                    rs.getString("PONo"),
                    rs.getDate("PODate")==null? null: rs.getDate("PODate").toLocalDate(),
                    rs.getString("To"),
                    rs.getString("Address"),
                    rs.getString("Contact"),
                    rs.getString("Terms"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    rs.getDate("DateBoard")==null? null: rs.getDate("DateBoard").toLocalDate(),
                    rs.getDate("DateAccepted")==null? null: rs.getDate("DateAccepted").toLocalDate(),
                    gm,
                    prepared
            );
        }

        return po;
    }

    /**
     * Create PO and items
     * @param po PO object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void createPO(PurchaseOrder po) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String po_sql = "INSERT INTO PurchaseOrder (PONo, [To], Address, Contact, Terms, Amount, Status, Prepared, PODate) " +
                "VALUES (?, ?, ?, ?, ?, ROUND(?, 2), ?, ?, getdate())";

        String item_sql = "INSERT INTO POItem (PONo, POQty, POPrice, Details, Sequence, RVItemId) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        //Prepared statements
        PreparedStatement ps_po = DB.getConnection().prepareStatement(po_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Insert PO
            ps_po.setString(1, po.getPoNo());
            ps_po.setString(2, po.getTo());
            ps_po.setString(3, po.getAddress());
            ps_po.setString(4, po.getContact());
            ps_po.setString(5, po.getTerms());
            ps_po.setDouble(6, po.getAmount());
            ps_po.setString(7, PurchaseOrder.PENDING_APPROVAL);
            ps_po.setString(8, ActiveUser.getUser().getEmployeeID());
            ps_po.executeUpdate();

            //Insert PO items
            for (POItem item : po.getItems()) {
                ps_item.setString(1, po.getPoNo());
                ps_item.setInt(2, item.getPOQty());
                ps_item.setDouble(3, item.getPOPrice());
                ps_item.setString(4, item.getDetails());
                ps_item.setInt(5, item.getSequence());
                ps_item.setInt(6, item.getRVItemId());
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
        ps_po.close();
        ps_item.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Approve PO (GM)
     * @param po PO object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void approvePO(PurchaseOrder po) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE PurchaseOrder SET GeneralManager = ?, Status = ?, Remarks = NULL WHERE PONo=?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, PurchaseOrder.APPROVED);
        ps.setString(3, po.getPoNo());
        ps.executeUpdate();
    }

    /**
     * Revise PO (by GeneralManager)
     * @param po PO object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void revisePO(PurchaseOrder po) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE PurchaseOrder SET Status = ?, Remarks = ?, GeneralManager = NULL WHERE PONo = ?");
        ps.setString(1, PurchaseOrder.PENDING_REVISION);
        ps.setString(2, po.getRemarks());
        ps.setString(3, po.getPoNo());
        ps.executeUpdate();
    }

    /**
     * Submit revised PO by preparer
     * @param po PO object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void submitRevisedPO(PurchaseOrder po) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE PurchaseOrder SET Status = ?, [To] = ?, Address = ?, Contact = ?, Terms = ?, Amount = ?, Prepared = ?, Remarks = NULL, GeneralManager = NULL, DateBoard = NULL, DateAccepted = NULL WHERE PONo = ?");
        ps.setString(1, PurchaseOrder.PENDING_APPROVAL);
        ps.setString(2, po.getTo());
        ps.setString(3, po.getAddress());
        ps.setString(4, po.getContact());
        ps.setString(5, po.getTerms());
        ps.setDouble(6, po.getAmount());
        ps.setString(7, ActiveUser.getUser().getEmployeeID());
        ps.setString(8, po.getPoNo());
        ps.executeUpdate();
    }

    /**
     * Returns a list of all POs from a department having a particular status e.g. Pending Approval
     * @param year - Year of PO
     * @param dept - Department who prepared the PO
     * @param status - PO status
     * @return list of POs
     * @throws Exception
     */
    public static List<PurchaseOrder> getAll(String year, Department dept, String status) throws Exception {
        String sql = "SELECT PONo" +
                "      ,PODate" +
                "      ,[To]" +
                "      ,p.Address" +
                "      ,Contact" +
                "      ,Terms" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,DateBoard" +
                "      ,DateAccepted" +
                "      ,GeneralManager" +
                "      ,Prepared" +
                "      ,(SELECT COUNT(POItemId) FROM POItem b WHERE p.PONo = b.PONo) AS NoItems" +
                "  FROM PurchaseOrder p INNER JOIN EmployeeInfo ei ON ei.EmployeeID = p.Prepared INNER JOIN Departments d ON ei.DepartmentId = d.DepartmentID" +
                "  WHERE PODate LIKE ? AND d.DepartmentId =? AND Status = ?" +
                "  ORDER BY PODate DESC, PONo ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, "%"+year+"%");
        ps.setString(2, dept.getDepartmentID());
        ps.setString(3, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<PurchaseOrder> items = new ArrayList<>();

        PurchaseOrder po = null;

        while(rs.next()) {
            EmployeeInfo prepared = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo gm = EmployeeDAO.getOne(rs.getString("GeneralManager"), DB.getConnection());
            po = new PurchaseOrder(
                    rs.getString("PONo"),
                    rs.getDate("PODate")==null? null: rs.getDate("PODate").toLocalDate(),
                    rs.getString("To"),
                    rs.getString("Address"),
                    rs.getString("Contact"),
                    rs.getString("Terms"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    rs.getDate("DateBoard")==null? null: rs.getDate("DateBoard").toLocalDate(),
                    rs.getDate("DateAccepted")==null? null: rs.getDate("DateAccepted").toLocalDate(),
                    gm,
                    prepared
            );
            po.setNoOfItems(rs.getInt("NoItems"));
            items.add(po);
        }
        return items;
    }

    /**
     * Returns a list of all POs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param year - Year of PO
     * @param status - PO status
     * @return list of POs
     * @throws Exception
     */
    public static List<PurchaseOrder> getAll(String year, String status) throws Exception {
        String sql = "SELECT PONo" +
                "      ,PODate" +
                "      ,[To]" +
                "      ,p.Address" +
                "      ,Contact" +
                "      ,Terms" +
                "      ,Amount" +
                "      ,Status" +
                "      ,Remarks" +
                "      ,DateBoard" +
                "      ,DateAccepted" +
                "      ,GeneralManager" +
                "      ,Prepared" +
                "      ,(SELECT COUNT(POItemId) FROM POItem b WHERE p.PONo = b.PONo) AS NoItems" +
                "  FROM PurchaseOrder p " +
                "  WHERE PODate LIKE ? AND Status = ?" +
                "  ORDER BY PODate DESC, PONo ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, "%"+year+"%");
        ps.setString(2, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<PurchaseOrder> items = new ArrayList<>();

        PurchaseOrder po = null;

        while(rs.next()) {
            EmployeeInfo prepared = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo gm = EmployeeDAO.getOne(rs.getString("GeneralManager"), DB.getConnection());
            po = new PurchaseOrder(
                    rs.getString("PONo"),
                    rs.getDate("PODate")==null? null: rs.getDate("PODate").toLocalDate(),
                    rs.getString("To"),
                    rs.getString("Address"),
                    rs.getString("Contact"),
                    rs.getString("Terms"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("Remarks"),
                    rs.getDate("DateBoard")==null? null: rs.getDate("DateBoard").toLocalDate(),
                    rs.getDate("DateAccepted")==null? null: rs.getDate("DateAccepted").toLocalDate(),
                    gm,
                    prepared
            );
            po.setNoOfItems(rs.getInt("NoItems"));
            items.add(po);
        }
        return items;
    }
}
