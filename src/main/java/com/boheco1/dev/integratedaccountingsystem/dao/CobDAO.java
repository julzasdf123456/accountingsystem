package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CobDAO {
    public static List<COB> getAll(DeptThreshold dt) throws Exception {
        String sql = "SELECT COBId" +
                "      ,Activity" +
                "      ,Amount" +
                "      ,Status, Remarks" +
                "      ,AppId" +
                "      ,FSId" +
                "      ,Prepared" +
                "      ,DatePrepared" +
                "      ,Reviewed" +
                "      ,DateReviewed" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,cobtype" +
                "  ,(SELECT COUNT(CItemId) FROM COBItem b WHERE c.COBId = b.COBId) AS NoItems" +
                "  FROM COB c LEFT JOIN EmployeeInfo ei ON c.Prepared = ei.EmployeeID WHERE c.AppId = ? AND ei.DepartmentId =?" +
                "  ORDER BY DatePrepared DESC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, dt.getAppID());
        ps.setString(2, dt.getDepartmentID());
        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            COB cob = new COB(
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
            );
            cob.setFundSource(new FundSource(rs.getString("FSId"), rs.getString("Source")));
            cob.setType(rs.getString("CobType"));
            cob.setNoOfItems(rs.getInt("NoItems"));
            cob.setRemarks(rs.getString("Remarks"));

            cobs.add(cob);
        }

        return cobs;
    }
    /**
     * Returns a list of all COBs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param year - Year of preparation in the APP
     * @param dept - Department who prepared the COB
     * @param status - COB status
     * @return list of COBs
     * @throws Exception
     */
    public static List<COB> getAll(String year, Department dept, String status) throws Exception {
        String sql = "SELECT COBId" +
                "      ,Activity" +
                "      ,Amount" +
                "      ,Status, Remarks" +
                "      ,a.AppId" +
                "      ,FSId, (SELECT Source FROM FundSource fs WHERE c.FSId = fs.FSId) AS source" +
                "      ,Prepared" +
                "      ,DatePrepared" +
                "      ,Reviewed" +
                "      ,DateReviewed" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,cobtype" +
                "  ,(SELECT COUNT(CItemId) FROM COBItem b WHERE c.COBId = b.COBId) AS NoItems" +
                "  FROM COB c INNER JOIN App a ON c.AppId = a.AppId INNER JOIN EmployeeInfo ei ON ei.EmployeeID = c.Prepared INNER JOIN Departments d ON ei.DepartmentId = d.DepartmentID" +
                "  WHERE Year = ? AND d.DepartmentId =? AND Status = ?" +
                "  ORDER BY DatePrepared DESC, COBId ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, year);
        ps.setString(2, dept.getDepartmentID());
        ps.setString(3, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            COB cob = new COB(
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
            );
            cob.setFundSource(new FundSource(rs.getString("FSId"), rs.getString("Source")));
            cob.setType(rs.getString("CobType"));
            cob.setNoOfItems(rs.getInt("NoItems"));
            cob.setRemarks(rs.getString("Remarks"));
            cobs.add(cob);
        }

        return cobs;
    }

    /**
     * Returns a list of all COBs from every department having a particular status e.g. Pending Approval (for budget officer)
     * @param year - Year of preparation in the APP
     * @param status - COB status
     * @return
     * @throws Exception
     */
    public static List<COB> getAll(String year, String status) throws Exception {
        String sql = "SELECT COBId" +
                "      ,Activity" +
                "      ,Amount" +
                "      ,Status, Remarks " +
                "      ,a.AppId" +
                "      ,FSId, (SELECT Source FROM FundSource fs WHERE c.FSId = fs.FSId) AS source" +
                "      ,Prepared" +
                "      ,DatePrepared" +
                "      ,Reviewed" +
                "      ,DateReviewed" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,cobtype" +
                "  ,(SELECT COUNT(CItemId) FROM COBItem b WHERE c.COBId = b.COBId) AS NoItems" +
                "  FROM COB c INNER JOIN App a ON c.AppId = a.AppId" +
                "  WHERE Year = ? AND Status = ?" +
                "  ORDER BY DatePrepared DESC, COBId ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, year);
        ps.setString(2, status);
        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            COB cob = new COB(
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
            );
            cob.setFundSource(new FundSource(rs.getString("FSId"), rs.getString("Source")));
            cob.setType(rs.getString("CobType"));
            cob.setNoOfItems(rs.getInt("NoItems"));
            cob.setRemarks(rs.getString("Remarks"));
            cobs.add(cob);
        }

        return cobs;
    }

    /**
     * Returns a list of all COBs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param dept - Department who prepared the COB
     * @param status - COB status
     * @return list of COBs
     * @throws Exception
     */
    public static List<COB> getAll(Department dept, String status) throws Exception {
        String sql = "SELECT COBId" +
                "      ,Activity" +
                "      ,Amount" +
                "      ,Status, Remarks" +
                "      ,a.AppId" +
                "      ,FSId, (SELECT Source FROM FundSource fs WHERE c.FSId = fs.FSId) AS source" +
                "      ,Prepared" +
                "      ,DatePrepared" +
                "      ,Reviewed" +
                "      ,DateReviewed" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,cobtype" +
                "  ,(SELECT COUNT(CItemId) FROM COBItem b WHERE c.COBId = b.COBId) AS NoItems" +
                "  FROM COB c INNER JOIN App a ON c.AppId = a.AppId INNER JOIN EmployeeInfo ei ON ei.EmployeeID = c.Prepared INNER JOIN Departments d ON ei.DepartmentId = d.DepartmentID" +
                "  WHERE d.DepartmentId =? AND Status = ?";


        if (status.equals(COB.APPROVED)){
            sql += "  ORDER BY DateApproved DESC, COBId ASC;";
        }else{
            sql += "  ORDER BY DatePrepared DESC, COBId ASC;";
        }

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, dept.getDepartmentID());
        ps.setString(2, status);

        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            COB cob = new COB(
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
            );
            cob.setFundSource(new FundSource(rs.getString("FSId"), rs.getString("Source")));
            cob.setType(rs.getString("CobType"));
            cob.setNoOfItems(rs.getInt("NoItems"));
            cob.setRemarks(rs.getString("Remarks"));
            cobs.add(cob);
        }

        return cobs;
    }

    /**
     * Returns a list of all COBs from a department having a particular status e.g. Pending Approval (for users except the budget officer)
     * @param dept - Department who prepared the COB
     * @param status - COB status
     * @param type - COB type
     * @return list of COBs
     * @throws Exception
     */
    public static List<COB> getAll(Department dept, String status, String type) throws Exception {
        String sql = "SELECT COBId" +
                "      ,Activity" +
                "      ,Amount" +
                "      ,Status, Remarks" +
                "      ,a.AppId" +
                "      ,FSId, (SELECT Source FROM FundSource fs WHERE c.FSId = fs.FSId) AS source" +
                "      ,Prepared" +
                "      ,DatePrepared" +
                "      ,Reviewed" +
                "      ,DateReviewed" +
                "      ,Approved" +
                "      ,DateApproved" +
                "      ,cobtype" +
                "  ,(SELECT COUNT(CItemId) FROM COBItem b WHERE c.COBId = b.COBId) AS NoItems" +
                "  FROM COB c INNER JOIN App a ON c.AppId = a.AppId INNER JOIN EmployeeInfo ei ON ei.EmployeeID = c.Prepared INNER JOIN Departments d ON ei.DepartmentId = d.DepartmentID" +
                "  WHERE d.DepartmentId =? AND Status = ? AND cobtype = ?";


        if (status.equals(COB.APPROVED)){
            sql += "  ORDER BY DateApproved DESC, COBId ASC;";
        }else{
            sql += "  ORDER BY DatePrepared DESC, COBId ASC;";
        }

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, dept.getDepartmentID());
        ps.setString(2, status);
        ps.setString(3, type);

        ResultSet rs = ps.executeQuery();

        ArrayList<COB> cobs = new ArrayList<>();

        while(rs.next()) {
            EmployeeInfo preparedBy = EmployeeDAO.getOne(rs.getString("Prepared"), DB.getConnection());
            EmployeeInfo reviewedBy = EmployeeDAO.getOne(rs.getString("Reviewed"), DB.getConnection());
            EmployeeInfo approvedBy = EmployeeDAO.getOne(rs.getString("Approved"), DB.getConnection());
            COB cob = new COB(
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
            );
            cob.setFundSource(new FundSource(rs.getString("FSId"), rs.getString("Source")));
            cob.setType(rs.getString("CobType"));
            cob.setNoOfItems(rs.getInt("NoItems"));
            cob.setRemarks(rs.getString("Remarks"));
            cobs.add(cob);
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
        String cob_sql = "INSERT INTO COB (AppId, COBId, COBType, FSId, Activity, Amount, Status, Prepared, DatePrepared) " +
                         "VALUES (?, ?, ?, ?, ?, ROUND(?, 2), ?, ?, getdate())";

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4, Sequence, NoOfTimes, Level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?, ?)";

        String subtype = "";
        if (cob.getType().equals(COBItem.TYPES[1])){
            subtype = "INSERT INTO Representation (reprnAllowance, reimbursableAllowance, otherAllowance, CItemId) " +
                    "VALUES (ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getType().equals(COBItem.TYPES[2])) {
            subtype = "INSERT INTO Salaries(Longetivity, SSSPhilH, Overtime, CashGift, Bonus13, CItemId) " +
                    "VALUES(ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getType().equals(COBItem.TYPES[3])){
            subtype = "INSERT INTO Travel (NoOfDays, Transport, Lodging, Registration, Incidental, Mode, CItemId) " +
                    "VALUES(?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?)";
        }

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(cob_sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);
        PreparedStatement ps_sub = DB.getConnection().prepareStatement(subtype);

        //Transact
        try {
            //Insert COB
            ps_cob.setString(1, cob.getAppId());
            ps_cob.setString(2, cob.getCobId());
            ps_cob.setString(3, cob.getType());
            ps_cob.setString(4, cob.getFsId());
            ps_cob.setString(5, cob.getActivity());
            ps_cob.setDouble(6, cob.getAmount());
            ps_cob.setString(7, COB.PENDING_REVIEW);
            ps_cob.setString(8, ActiveUser.getUser().getEmployeeID());
            ps_cob.executeUpdate();

            //Insert cob items
            for (COBItem item : cob.getItems()) {
                ps_item.setString(1, item.getcItemId());
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
                ps_item.setInt(12, item.getSequence());
                ps_item.setInt(13, item.getNoOfTimes());
                ps_item.setInt(14, item.getLevel());
                ps_item.executeUpdate();

                //Insert also to child table if COB Item is representation, Salaries, Travels
                //Representation
                if (cob.getType().equals(COBItem.TYPES[1])){
                    Representation r = (Representation) item;
                    ps_sub.setDouble(1, r.getRepresentationAllowance());
                    ps_sub.setDouble(2, r.getReimbursableAllowance());
                    ps_sub.setDouble(3, r.getOtherAllowance());
                    ps_sub.setString(4, item.getcItemId());
                    ps_sub.executeUpdate();
                //Salaries
                }else if (cob.getType().equals(COBItem.TYPES[2])) {
                    Salary s = (Salary) item;
                    ps_sub.setDouble(1, s.getLongetivity());
                    ps_sub.setDouble(2, s.getsSSPhilH());
                    ps_sub.setDouble(3, s.getOvertime());
                    ps_sub.setDouble(4, s.getCashGift());
                    ps_sub.setDouble(5, s.getBonus13());
                    ps_sub.setString(6, item.getcItemId());
                    ps_sub.executeUpdate();
                //Travels/Seminars
                }else if (cob.getType().equals(COBItem.TYPES[3])){
                    Travel t = (Travel) item;
                    ps_sub.setInt(1, t.getNoOfDays());
                    ps_sub.setDouble(2, t.getTransport());
                    ps_sub.setDouble(3, t.getLodging());
                    ps_sub.setDouble(4, t.getRegistration());
                    ps_sub.setDouble(5, t.getIncidental());
                    ps_sub.setString(6, t.getMode());
                    ps_sub.setString(7, item.getcItemId());
                    ps_sub.executeUpdate();
                }
            }
            //Commit insert
            conn.commit();
        }catch (SQLException e){
            e.printStackTrace();
            //If error, rollback
            conn.rollback();
        }

        //Close connections
        ps_cob.close();
        ps_item.close();
        ps_sub.close();

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

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4, Sequence) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";

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
                ps_item.setString(1, item.getcItemId());
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
                ps_item.setInt(12, item.getSequence());
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

    /**
     * Revise COB (Budget Officer/Dept. Head)
     * @param cob COB object
     * @return void
     * @throws Exception obligatory from DB.getConnection()
     */
    public static void reviseCob(COB cob) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE COB SET Status = ?, Remarks = ?, Approved = NULL, Reviewed = NULL, DateReviewed = NULL, DateApproved = NULL WHERE COBId = ?");
        ps.setString(1, COB.PENDING_REVISION);
        ps.setString(2, cob.getRemarks());
        ps.setString(3, cob.getCobId());
        ps.executeUpdate();
    }

    /**
     * Count a department's COB
     * @param dept the department
     * @return integer
     * @throws Exception obligatory from DB.getConnection()
     */
    public static int countCob(String dept) throws Exception {
        ResultSet rs = DB.getConnection().createStatement().executeQuery("SELECT count(cobid) AS count FROM cob a INNER JOIN employeeinfo b ON a.Prepared = b.EmployeeID INNER JOIN departments c ON b.DepartmentId = c.DepartmentID\n" +
                "WHERE DepartmentName = '"+dept+"'");
        if(rs.next()) {
            return rs.getInt("count");
        }
        return 0;
    }

    public static void resetAmount(COB cob) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE COB set Amount=? WHERE COBId=?");
        ps.setDouble(1, cob.getTotal());
        ps.setString(2, cob.getCobId());
        ps.executeUpdate();
    }


}
