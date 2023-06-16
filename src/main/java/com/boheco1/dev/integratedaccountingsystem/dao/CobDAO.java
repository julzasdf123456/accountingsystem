package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CobDAO {
    public static List<COB> getAll(DeptThreshold dt) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT c.* FROM COB c LEFT JOIN EmployeeInfo ei ON c.Prepared = ei.EmployeeID WHERE c.AppId = ? AND ei.DepartmentId =?");
        ps.setString(1, dt.getAppID());
        ps.setString(2, dt.getDepartmentID());
        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            cobs.add(new COB(
                    rs.getString("COBId"),
                    rs.getString("Activity"),
                    rs.getDouble("Amount"),
                    rs.getString("Status"),
                    rs.getString("AppId"),
                    rs.getString("FSId"),
                    preparedBy,
                    rs.getDate("DatePrepared")==null? null: rs.getDate("DatePrepared").toLocalDate(),
                    reviewedBy,
                    rs.getDate("DateReviewed")==null? null: rs.getDate("DateReviewed").toLocalDate(),
                    approvedBy,
                    rs.getDate("DateApproved")==null? null: rs.getDate("DateApproved").toLocalDate()
            ));
        }

        return cobs;
    }

    /**
     * Create COB and items
     * @param cob COB object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void createCOB(COB cob) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String cob_sql = "INSERT INTO COB (AppId, COBId, FSId, Activity, Amount, Status, Prepared, DatePrepared) " +
                         "VALUES (?, ?, ?, ?, ROUND(?, 2), ?, ?, getdate())";

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2))";

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(cob_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Insert COB
            ps_cob.setString(1, cob.getAppId());
            ps_cob.setString(2, cob.getCobId());
            ps_cob.setString(3, cob.getFsId());
            ps_cob.setString(4, cob.getActivity());
            ps_cob.setDouble(5, cob.getAmount());
            ps_cob.setString(6, COB.PENDING_REVIEW);
            ps_cob.setString(7, ActiveUser.getUser().getEmployeeID());
            ps_cob.setDate(8, java.sql.Date.valueOf(cob.getDatePrepared()));
            ps_cob.executeUpdate();

            //Insert cob items
            for (COBItem item : cob.getItems()) {
                String rndKey = Utility.generateRandomId();
                ps_item.setString(1, rndKey);
                ps_item.setString(2, item.getItemId());
                ps_item.setString(3, cob.getCobId());
                ps_item.setInt(4, item.getQty());
                ps_item.setString(5, item.getRemarks());
                ps_item.setString(6, item.getDescription());
                ps_item.setDouble(7, item.getCost());
                ps_item.setDouble(8, item.getQtr1());
                ps_item.setDouble(9, item.getQtr2());
                ps_item.setDouble(10, item.getQtr3());
                ps_item.setDouble(11, item.getQtr4());
                ps_item.executeUpdate();
            }
            //Commit insert
            conn.commit();
        }catch (SQLException e){
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_cob.close();
        ps_item.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Update COB and items
     * @param cob COB object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void updateCob(COB cob) throws Exception{
        //Set autocommit false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String cob_sql = "UPDATE COB SET FSId = ?, Activity = ?, Amount = ROUND(?, 2), Status = ?, Prepared = ?, DatePrepared = getdate() WHERE COBId = ?";

        String del_sql = "DELETE FROM COBItem WHERE COBId = ?";

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2))";

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(cob_sql);
        PreparedStatement ps_del = DB.getConnection().prepareStatement(del_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);

        //Transact
        try {
            //Update COB
            ps_cob.setString(1, cob.getFsId());
            ps_cob.setString(2, cob.getActivity());
            ps_cob.setDouble(3, cob.getAmount());
            ps_cob.setString(4, COB.PENDING_REVIEW);
            ps_cob.setString(5, ActiveUser.getUser().getEmployeeID());
            ps_cob.setDate(6, java.sql.Date.valueOf(cob.getDatePrepared()));
            ps_cob.setString(7, cob.getCobId());
            ps_cob.executeUpdate();

            //Delete old cob items
            ps_del.setString(1, cob.getCobId());
            ps_del.executeUpdate();

            //Insert current cob items
            for (COBItem item : cob.getItems()) {
                ps_item.setString(1, item.getItemId());
                ps_item.setInt(2, item.getQty());
                ps_item.setString(3, item.getRemarks());
                ps_item.setString(4, item.getDescription());
                ps_item.setDouble(5, item.getCost());
                ps_item.setDouble(6, item.getQtr1());
                ps_item.setDouble(7, item.getQtr2());
                ps_item.setDouble(8, item.getQtr3());
                ps_item.setDouble(9, item.getQtr4());
                ps_item.setString(10, item.getcItemId());
                ps_item.executeUpdate();
            }

            //Commit
            conn.commit();
        }catch (SQLException e){
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_cob.close();
        ps_del.close();
        ps_item.close();

        //Set autocommit true
        conn.setAutoCommit(true);
    }

    /**
     * Delete COB and items (Only when not reviewed/approved)
     * @param cob COB object with items list
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void deleteCob(COB cob) throws Exception{
        //Set autocommit false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        //Queries
        String del_sql = "DELETE FROM COBItem WHERE COBId = ?";
        String del_cob_sql = "DELETE FROM COB WHERE COBId = ?";

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(del_cob_sql);
        PreparedStatement ps_del = DB.getConnection().prepareStatement(del_sql);

        //Transact
        try {
            //Delete old cob items
            ps_del.setString(1, cob.getCobId());
            ps_del.executeUpdate();

            //Delete cob
            ps_cob.setString(1, cob.getCobId());
            ps_cob.executeUpdate();

            //Commit
            conn.commit();
        }catch (SQLException e){
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_cob.close();
        ps_del.close();

        //Set autocommit true
        conn.setAutoCommit(true);
    }

    /**
     * Approve COB (Budget Officer)
     * @param cob COB object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void approveCob(COB cob) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE COB SET Approved = ?, Status = ?, DateApproved = getdate() WHERE COBId=?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, COB.APPROVED);
        ps.setString(3, cob.getCobId());
        ps.executeUpdate();
    }

    /**
     * Review COB (Budget Officer/Dept. Head)
     * @param cob COB object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void reviewCob(COB cob) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE COB SET Reviewed = ?, Status = ?, DateReviewed = getdate() WHERE COBId=?");
        ps.setString(1, ActiveUser.getUser().getEmployeeID());
        ps.setString(2, COB.PENDING_APPROVAL);
        ps.setString(3, cob.getCobId());
        ps.executeUpdate();
    }
}
