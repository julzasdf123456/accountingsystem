package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CobItemDAO {
    /**
     *
     * @param cob Parent COB of the items that will be fetched.
     * @return A List of COBItems
     * @throws Exception
     */
    public static List<COBItem> getItems(COB cob) throws Exception {

        String sql = "SELECT COBId, CItemId, Sequence, Level, ItemId, Description, Cost, Remarks, Qty, ISNULL((SELECT SUM(RVQty) FROM RVItem r WHERE r.CItemId = ci.CItemId), 0) AS RVQty, NoOfTimes, (Cost * Qty * NoOfTimes) AS Total, Qtr1, Qtr2, Qtr3, Qtr4 " +
                "FROM COBItem ci ";

        //Benefits/Allowances
        if (cob.getCategory().getCategory().equals(COBItem.TYPES[0])){

        //Representation
        } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])) {
            sql = "SELECT COBId, Rid, ci.CItemId, Sequence, Level, ItemId, Description, Cost, Remarks, Qty, NoOfTimes, (Cost * Qty * NoOfTimes) AS Total, reprnAllowance, reimbursableAllowance, otherAllowance " +
                    "FROM COBItem ci INNER JOIN Representation c ON ci.CItemId = c.CItemId ";
        //Salaries
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
            sql = "SELECT COBId, SalId, ci.CItemId, Sequence, Level, ItemId, Description AS Position, Cost AS BasicSalary, Remarks, Qty AS Persons, NoOfTimes, " +
                    "Longetivity, SSSPhilH, CashGift, Bonus13, " +
                    "(Cost * Qty * NoOfTimes * 12) As AnnualTotal " +
                    "FROM COBItem ci INNER JOIN Salaries s ON ci.CItemId = s.CItemId ";
        //Travels/Seminars
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])) {
            sql = "SELECT COBId, TrId, ci.CItemId, Sequence, Level, ItemId, Description, " +
                    "Cost AS RatePerDiem, (Cost * NoOfDays * Qty * NoOfTimes)  AS TravelCost, " +
                    "Remarks, Qty AS Persons, NoOfDays, NoOfTimes AS TimesPerYear, " +
                    "Transport, (Transport * Qty * NoOfTimes) AS TotalTransport, " +
                    "Lodging, (Lodging * NoOfDays * Qty * NoOfTimes) AS TotalLodging, " +
                    "Incidental, (Incidental * NoOfDays * Qty * NoOfTimes) AS TotalIncidental, " +
                    "Registration, (Registration * Qty * NoOfTimes) AS TotalRegistration, " +
                    "((Cost * NoOfDays * Qty * NoOfTimes) " +
                    "+ (Transport * Qty * NoOfTimes) " +
                    "+ (Lodging * NoOfDays * Qty * NoOfTimes) " +
                    "+ (Registration * Qty * NoOfTimes) " +
                    "+ (Incidental * NoOfDays * Qty * NoOfTimes)) AS TotalAmount, " +
                    "Mode, " +
                    "Qtr1, Qtr2, Qtr3, Qtr4, (Qtr1 + Qtr2 + Qtr3 + Qtr4) AS Total " +
                    "FROM COBItem ci INNER JOIN Travel t ON ci.CItemId = t.CItemId ";
        //Supplies/Materials
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[4])) {

        //Transportation
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[5])) {

        //Others
        }else{

        }
        sql += "WHERE COBId = ? ORDER BY Sequence ASC;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setString(1, cob.getCobId());

        ResultSet rs = ps.executeQuery();

        List<COBItem> items = new ArrayList<>();

        while(rs.next()) {

            COBItem item = new COBItem();

            //Representation
            if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])) {
                Representation repr = new Representation();
                repr.setRId(rs.getInt("RId"));
                repr.setcItemId(rs.getString("CItemId"));
                repr.setItemId(rs.getString("ItemId"));
                repr.setDescription(rs.getString("Description"));
                repr.setQty(rs.getInt("Qty"));
                repr.setCost(rs.getDouble("Cost"));
                repr.setRepresentationAllowance(rs.getDouble("reprnAllowance"));
                repr.setReimbursableAllowance(rs.getDouble("reimbursableAllowance"));
                repr.setOtherAllowance(rs.getDouble("otherAllowance"));
                repr.setRemarks(rs.getString("Remarks"));
                repr.setSequence(rs.getInt("Sequence"));
                repr.setNoOfTimes(rs.getInt("NoOfTimes"));
                repr.setLevel(rs.getInt("Level"));
                repr.setCobId(rs.getString("COBId"));
                item = repr;
            //Salaries
            }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
                Salary sal = new Salary();
                sal.setSalId(rs.getInt("SalId"));
                sal.setcItemId(rs.getString("CItemId"));
                sal.setItemId(rs.getString("ItemId"));
                sal.setDescription(rs.getString("Position"));
                sal.setQty(rs.getInt("Persons"));
                sal.setCost(rs.getDouble("BasicSalary"));
                sal.setRemarks(rs.getString("Remarks"));
                sal.setSequence(rs.getInt("Sequence"));
                sal.setNoOfTimes(rs.getInt("NoOfTimes"));
                sal.setCashGift(rs.getDouble("CashGift"));
                sal.setLongetivity(rs.getDouble("Longetivity"));
                sal.setsSSPhilH(rs.getDouble("SSSPhilH"));
                sal.setBonus13(rs.getDouble("Bonus13"));
                sal.setLevel(rs.getInt("Level"));
                sal.setCobId(rs.getString("COBId"));
                item = sal;
            //Travels/Seminars
            }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])) {

                Travel trav = new Travel();
                trav.setTrId(rs.getInt("TrId"));
                trav.setcItemId(rs.getString("CItemId"));
                trav.setSequence(rs.getInt("Sequence"));
                trav.setItemId(rs.getString("ItemId"));
                trav.setDescription(rs.getString("Description"));
                trav.setCost(rs.getDouble("RatePerDiem"));
                trav.setRemarks(rs.getString("Remarks"));
                trav.setQty(rs.getInt("Persons"));
                trav.setLodging(rs.getDouble("Lodging"));
                trav.setTransport(rs.getDouble("Transport"));
                trav.setIncidental(rs.getDouble("Incidental"));
                trav.setRegistration(rs.getDouble("Registration"));
                trav.setNoOfDays(rs.getInt("NoOfDays"));
                trav.setNoOfTimes(rs.getInt("TimesPerYear"));
                trav.setMode(rs.getString("Mode"));
                trav.setQtr1(rs.getDouble("Qtr1"));
                trav.setQtr2(rs.getDouble("Qtr2"));
                trav.setQtr3(rs.getDouble("Qtr3"));
                trav.setQtr4(rs.getDouble("Qtr4"));
                trav.setLevel(rs.getInt("Level"));
                trav.setCobId(rs.getString("COBId"));
                item = trav;
            //Others
            }else{
                item = new COBItem();
                item.setCobId(rs.getString("COBId"));
                item.setcItemId(rs.getString("CItemId"));
                item.setItemId(rs.getString("ItemId"));
                item.setDescription(rs.getString("Description"));
                item.setQty(rs.getInt("Qty"));
                item.setCost(rs.getDouble("Cost"));
                item.setQtr1(rs.getDouble("Qtr1"));
                item.setQtr2(rs.getDouble("Qtr2"));
                item.setQtr3(rs.getDouble("Qtr3"));
                item.setQtr4(rs.getDouble("Qtr4"));
                item.setRemarks(rs.getString("Remarks"));
                item.setSequence(rs.getInt("Sequence"));
                item.setNoOfTimes(rs.getInt("NoOfTimes"));
                item.setLevel(rs.getInt("Level"));
                item.setRvQty(rs.getInt("RVQty"));
            }

            items.add(item);
        }

        return items;
    }

    public static void update(COBItem item) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE COBItem SET Description=?, Qty=?, Cost=?, Qtr1=?, Qtr2=?, Qtr3=?, Qtr4=?, ItemId=?, Remarks=? WHERE CItemId=?");

        ps.setString(1, item.getDescription());
        ps.setInt(2, item.getQty());
        ps.setDouble(3, item.getCost());
        ps.setDouble(4, item.getQtr1());
        ps.setDouble(5, item.getQtr2());
        ps.setDouble(6, item.getQtr3());
        ps.setDouble(7, item.getQtr4());
        ps.setString(8, item.getItemId());
        ps.setString(9, item.getRemarks());
        ps.setString(10, item.getcItemId());

        ps.executeUpdate();
    }

    /**
     * Delete COB item from revised COB
     * @param cob - COB reference
     * @param item - COB item reference to delete
     * @throws Exception
     */
    public static void delete(COB cob, COBItem item) throws Exception {
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "DELETE FROM COBItem WHERE CItemId=?;";
        String sql2 = "UPDATE COB SET Amount = Amount - ? WHERE CobID=?;";

        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql2);
        try {
            ps.setString(1, item.getcItemId());
            ps.executeUpdate();

            ps_cob.setDouble(1, item.getAmount());
            ps_cob.setString(2, cob.getCobId());
            ps_cob.executeUpdate();
            //Commit insert
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            //If error, rollback
            conn.rollback();
        }
        //Close connections
        ps.close();
        ps_cob.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Change the level of COB item from revised COB
     * @param item - COB item reference to delete
     * @throws Exception
     */
    public static void changeLevel(COBItem item, int level) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE COBItem SET Level = ?, Parent = ? WHERE CItemId=?");
        String parent = null;
        if (item.getParent() != null)
            parent = item.getParent().getcItemId();
        ps.setInt(1, level);
        ps.setString(2, parent);
        ps.setString(3, item.getcItemId());
        ps.executeUpdate();
    }

    /**
     * Change the quarter amount of a COB item
     * @param item - COB item reference
     * @throws Exception
     */
    public static void changeQtrAmount(COBItem item) throws Exception {
        String sql = "UPDATE COBItem SET Qtr1 = ?, Qtr2 = ?, Qtr3 = ?, Qtr4 =? WHERE CItemId=?";
        PreparedStatement ps = DB.getConnection().prepareStatement(sql);
        ps.setDouble(1, item.getQtr1());
        ps.setDouble(2, item.getQtr2());
        ps.setDouble(3, item.getQtr3());
        ps.setDouble(4, item.getQtr4());
        ps.setString(5, item.getcItemId());
        ps.executeUpdate();
    }

    /**
     * Adds a COB item from an existing COB
     * @param cob - COB reference
     * @param item - COB item to add
     * @throws Exception
     */
    public static void add(COB cob, COBItem item) throws Exception{
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "UPDATE COB SET Amount = Amount + ? WHERE CobID=?;";

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4, Sequence, NoOfTimes, Level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?, ?)";

        String subtype = "";
        if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])){
            subtype = "INSERT INTO Representation (reprnAllowance, reimbursableAllowance, otherAllowance, CItemId) " +
                    "VALUES (ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
            subtype = "INSERT INTO Salaries(Longetivity, SSSPhilH, Overtime, CashGift, Bonus13, CItemId) " +
                    "VALUES(ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])){
            subtype = "INSERT INTO Travel (NoOfDays, Transport, Lodging, Registration, Incidental, Mode, CItemId) " +
                    "VALUES(?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?)";
        }

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);
        PreparedStatement ps_sub = DB.getConnection().prepareStatement(subtype);

        try {
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
            if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])) {
                Representation r = (Representation) item;
                ps_sub.setDouble(1, r.getRepresentationAllowance());
                ps_sub.setDouble(2, r.getReimbursableAllowance());
                ps_sub.setDouble(3, r.getOtherAllowance());
                ps_sub.setString(4, item.getcItemId());
                ps_sub.executeUpdate();
                //Salaries
            } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
                Salary s = (Salary) item;
                ps_sub.setDouble(1, s.getLongetivity());
                ps_sub.setDouble(2, s.getsSSPhilH());
                ps_sub.setDouble(3, s.getOvertime());
                ps_sub.setDouble(4, s.getCashGift());
                ps_sub.setDouble(5, s.getBonus13());
                ps_sub.setString(6, item.getcItemId());
                ps_sub.executeUpdate();
                //Travels/Seminars
            } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])) {
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

            ps_cob.setDouble(1, item.getAmount());
            ps_cob.setString(2, cob.getCobId());
            ps_cob.executeUpdate();

            //Commit insert
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            //If error, rollback
            conn.rollback();
        }
        //Close connections
        ps_item.close();
        ps_sub.close();
        ps_cob.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Update a COB item from an existing COB
     * @param cob - COB reference
     * @param item - COB item to add
     * @param oldAmount - prev. item amount
     * @throws Exception
     */
    public static void update(COB cob, COBItem item, double oldAmount) throws Exception{
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "UPDATE COB SET Amount = Amount + ? WHERE CobID=?;";

        String item_sql = "UPDATE COBItem SET ItemId=?, COBId=?, Qty=?, Remarks=?, Description=?, Cost=ROUND(?, 2), Qtr1=ROUND(?, 2), Qtr2=ROUND(?, 2), Qtr3=ROUND(?, 2), Qtr4=ROUND(?, 2), Sequence=?, NoOfTimes=?, Level=? WHERE CItemId=?";
        String subtype = "";
        if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])){
            subtype = "UPDATE Representation SET reprnAllowance = (ROUND(?, 2), reimbursableAllowance = (ROUND(?, 2), otherAllowance = (ROUND(?, 2) WHERE RId=?";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
            subtype = "UPDATE Salaries SET Longetivity = (ROUND(?, 2), SSSPhilH = (ROUND(?, 2), Overtime = (ROUND(?, 2), CashGift = (ROUND(?, 2), Bonus13 = (ROUND(?, 2) WHERE SalId=?";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])){
            subtype = "UPDATE Travel SET NoOfDays = ?, Transport = ROUND(?, 2), Lodging = ROUND(?, 2), Registration = ROUND(?, 2), Incidental = ROUND(?, 2), Mode = ? WHERE TrId=?";
        }

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);
        PreparedStatement ps_sub = DB.getConnection().prepareStatement(subtype);

        try {
            ps_item.setString(1, item.getItemId());
            ps_item.setString(2, cob.getCobId());
            ps_item.setInt(3, item.getQty());
            ps_item.setString(4, item.getRemarks());
            ps_item.setString(5, item.getDescription());
            ps_item.setDouble(6, item.getCost());
            ps_item.setDouble(7, item.getQtr1());
            ps_item.setDouble(8, item.getQtr2());
            ps_item.setDouble(9, item.getQtr3());
            ps_item.setDouble(10, item.getQtr4());
            ps_item.setInt(11, item.getSequence());
            ps_item.setInt(12, item.getNoOfTimes());
            ps_item.setInt(13, item.getLevel());
            ps_item.setString(14, item.getcItemId());
            ps_item.executeUpdate();

            //Insert also to child table if COB Item is representation, Salaries, Travels
            //Representation
            if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])) {
                Representation r = (Representation) item;
                ps_sub.setDouble(1, r.getRepresentationAllowance());
                ps_sub.setDouble(2, r.getReimbursableAllowance());
                ps_sub.setDouble(3, r.getOtherAllowance());
                ps_sub.setInt(4, r.getRId());
                ps_sub.executeUpdate();
            //Salaries
            } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
                Salary s = (Salary) item;
                ps_sub.setDouble(1, s.getLongetivity());
                ps_sub.setDouble(2, s.getsSSPhilH());
                ps_sub.setDouble(3, s.getOvertime());
                ps_sub.setDouble(4, s.getCashGift());
                ps_sub.setDouble(5, s.getBonus13());
                ps_sub.setInt(6, s.getSalId());
                ps_sub.executeUpdate();
            //Travels/Seminars
            } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])) {
                Travel t = (Travel) item;
                ps_sub.setInt(1, t.getNoOfDays());
                ps_sub.setDouble(2, t.getTransport());
                ps_sub.setDouble(3, t.getLodging());
                ps_sub.setDouble(4, t.getRegistration());
                ps_sub.setDouble(5, t.getIncidental());
                ps_sub.setString(6, t.getMode());
                ps_sub.setInt(7, t.getTrId());
                ps_sub.executeUpdate();
            }
            ps_cob.setDouble(1, item.getAmount() - oldAmount);
            ps_cob.setString(2, cob.getCobId());
            ps_cob.executeUpdate();

            //Commit insert
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            //If error, rollback
            conn.rollback();
        }
        //Close connections
        ps_item.close();
        ps_sub.close();
        ps_cob.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }

    /**
     * Adds a list of COB items from an existing COB (during import)
     * @param cob - COB reference
     * @param items - List of COB items to add
     * @throws Exception
     */
    public static void add(COB cob, List<COBItem> items) throws Exception{
        //Set autocommit to false
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);

        String sql = "UPDATE COB SET Amount = Amount + ? WHERE CobID=?;";

        String item_sql = "INSERT INTO COBItem (CItemId, ItemId, COBId, Qty, Remarks, Description, Cost, Qtr1, Qtr2, Qtr3, Qtr4, Sequence, NoOfTimes, Level) " +
                "VALUES (?, ?, ?, ?, ?, ?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?, ?)";

        String subtype = "";
        if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])){
            subtype = "INSERT INTO Representation (reprnAllowance, reimbursableAllowance, otherAllowance, CItemId) " +
                    "VALUES (ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
            subtype = "INSERT INTO Salaries(Longetivity, SSSPhilH, Overtime, CashGift, Bonus13, CItemId) " +
                    "VALUES(ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?)";
        }else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])){
            subtype = "INSERT INTO Travel (NoOfDays, Transport, Lodging, Registration, Incidental, Mode, CItemId) " +
                    "VALUES(?, ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ROUND(?, 2), ?, ?)";
        }

        //Prepared statements
        PreparedStatement ps_cob = DB.getConnection().prepareStatement(sql);
        PreparedStatement ps_item = DB.getConnection().prepareStatement(item_sql);
        PreparedStatement ps_sub = DB.getConnection().prepareStatement(subtype);

        try {
            for (COBItem item : items) {
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
                if (cob.getCategory().getCategory().equals(COBItem.TYPES[1])) {
                    Representation r = (Representation) item;
                    ps_sub.setDouble(1, r.getRepresentationAllowance());
                    ps_sub.setDouble(2, r.getReimbursableAllowance());
                    ps_sub.setDouble(3, r.getOtherAllowance());
                    ps_sub.setString(4, item.getcItemId());
                    ps_sub.executeUpdate();
                    //Salaries
                } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[2])) {
                    Salary s = (Salary) item;
                    ps_sub.setDouble(1, s.getLongetivity());
                    ps_sub.setDouble(2, s.getsSSPhilH());
                    ps_sub.setDouble(3, s.getOvertime());
                    ps_sub.setDouble(4, s.getCashGift());
                    ps_sub.setDouble(5, s.getBonus13());
                    ps_sub.setString(6, item.getcItemId());
                    ps_sub.executeUpdate();
                    //Travels/Seminars
                } else if (cob.getCategory().getCategory().equals(COBItem.TYPES[3])) {
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

                ps_cob.setDouble(1, item.getAmount());
                ps_cob.setString(2, cob.getCobId());
                ps_cob.executeUpdate();
            }
            //Commit insert
            conn.commit();
        }catch (SQLException se){
            se.printStackTrace();
            //If error, rollback
            conn.rollback();
        }
        //Close connections
        ps_item.close();
        ps_sub.close();
        ps_cob.close();

        //Set autocommit to true
        conn.setAutoCommit(true);
    }
}
