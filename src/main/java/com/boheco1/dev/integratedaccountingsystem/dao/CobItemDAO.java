package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import org.apache.poi.ss.formula.functions.T;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

        String sql = "SELECT CItemId, Sequence, Level, ItemId, Description, Cost, Remarks, Qty, NoOfTimes, (Cost * Qty * NoOfTimes) AS Total, Qtr1, Qtr2, Qtr3, Qtr4 " +
                "FROM COBItem ci ";

        //Benefits/Allowances
        if (cob.getType().equals(COBItem.TYPES[0])){

        //Representation
        } else if (cob.getType().equals(COBItem.TYPES[1])) {
            sql = "SELECT ci.CItemId, Sequence, Level, ItemId, Description, Cost, Remarks, Qty, NoOfTimes, (Cost * Qty * NoOfTimes) AS Total, reprnAllowance, reimbursableAllowance, otherAllowance " +
                    "FROM COBItem ci INNER JOIN Representation c ON ci.CItemId = c.CItemId ";
        //Salaries
        }else if (cob.getType().equals(COBItem.TYPES[2])) {
            sql = "SELECT ci.CItemId, Sequence, Level, ItemId, Description AS Position, Cost AS BasicSalary, Remarks, Qty AS Persons, NoOfTimes, " +
                    "Longetivity, SSSPhilH, CashGift, Bonus13, " +
                    "(Cost * Qty * NoOfTimes * 12) As AnnualTotal " +
                    "FROM COBItem ci INNER JOIN Salaries s ON ci.CItemId = s.CItemId ";
        //Travels/Seminars
        }else if (cob.getType().equals(COBItem.TYPES[3])) {
            sql = "SELECT ci.CItemId, Sequence, Level, ItemId, Description, " +
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
        }else if (cob.getType().equals(COBItem.TYPES[4])) {

        //Transportation
        }else if (cob.getType().equals(COBItem.TYPES[5])) {

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
            if (cob.getType().equals(COBItem.TYPES[1])) {
                Representation repr = new Representation();
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
                item = repr;
            //Salaries
            }else if (cob.getType().equals(COBItem.TYPES[2])) {
                Salary sal = new Salary();

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
                item = sal;
            //Travels/Seminars
            }else if (cob.getType().equals(COBItem.TYPES[3])) {

                Travel trav = new Travel();

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
                item = trav;
            //Others
            }else{
                item = new COBItem();
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
}
