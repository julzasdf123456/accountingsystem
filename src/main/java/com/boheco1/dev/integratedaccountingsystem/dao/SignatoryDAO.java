package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.Signatory;
import com.boheco1.dev.integratedaccountingsystem.objects.SignatoryItem;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


public class SignatoryDAO {
    public static void add(Signatory sig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Signatories " +
                        "(Type, UserID, Rank, Comments, CreatedAt, UpdatedAt, id) " +
                        "VALUES " +
                        "(?,?,?,?,now(), now(), ?)");

        sig.setId(Utility.generateRandomId());

        ps.setString(1, sig.getType());
        ps.setString(2, sig.getUserID());
        ps.setInt(3, sig.getRank());
        ps.setString(4, sig.getComments());
        ps.setString(5, sig.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static void update(Signatory sig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Signatories SET " +
                        "Type=?, UserID=?, Rank=?, Comments=?, UpdatedAt=NOW() " +
                        "WHERE id=?");

        ps.setString(1, sig.getType());
        ps.setString(2, sig.getUserID());
        ps.setInt(3, sig.getRank());
        ps.setString(4, sig.getComments());
        ps.setString(5, sig.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static Signatory get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Signatories WHERE id=?");
        ps.setInt(1, id);

        ResultSet rs = ps.executeQuery();

        Signatory sig = null;

        if(rs.next()) {
            sig = new Signatory(
                    rs.getString("id"),
                    rs.getString("Type"),
                    rs.getString("UserID"),
                    rs.getInt("Rank"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime());

        }

        rs.close();
        ps.close();

        return sig;
    }

    /**
     * Retrieves a list of Signatory as a search result based on a search Key
     * @param key The search key
     * @return A list of Signatory that qualifies with the search key
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<Signatory> getSignatories(String key) throws Exception  {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM signatories WHERE  type = ? ORDER BY rank");
        ps.setString(1, key );

        ResultSet rs = ps.executeQuery();
        //int id, String type, int userID, int rank, String comments, LocalDateTime createdAt, LocalDateTime updatedAt
        ArrayList<Signatory> signatories = new ArrayList<>();
        while(rs.next()) {
            signatories.add(new Signatory(
                    rs.getString("id"),
                    rs.getString("Type"),
                    rs.getString("UserID"),
                    rs.getInt("Rank"),
                    rs.getString("Comments"),
                    rs.getDate("CreatedAt")!=null ? rs.getTimestamp("CreatedAt").toLocalDateTime() : null,
                    rs.getDate("UpdatedAt")!=null ? rs.getTimestamp("UpdatedAt").toLocalDateTime() : null
            ));
        }

        rs.close();
        ps.close();

        return signatories;
    }

    /**
     * Retrieves a list of MIRSSignatories into a SignatoryItem for table display
     * @param status for filtering the status of the MIRSSignatory
     * @return A list of SignatoryItems
     * @throws Exception obligatory from DB.getConnection()
     */
    public static List<SignatoryItem> getSignatoryItems(String status) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT m.id, m.MIRSID, ei.EmployeeFirstName , ei.EmployeeLastName, m.Status FROM MIRSSignatories m \n" +
                        "INNER JOIN EmployeeInfo ei ON ei.EmployeeID = m.user_id \n" +
                        "WHERE m.Status = ? ORDER BY m.CreatedAt DESC");
        ps.setString(1, status);

        List<SignatoryItem> items = new ArrayList();

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            items.add(new SignatoryItem(
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("EmployeeLastName") + ", " + rs.getString("EmployeeFirstName"),
                    rs.getString("Status")
            ));
        }

        rs.close();
        ps.close();

        return items;
    }

    public static void transferSignatory(String mirsSignatoryID, String userID) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRSSignatories SET user_id=? WHERE id=?");
        ps.setString(1, userID);
        ps.setString(2, mirsSignatoryID);
        ps.executeUpdate();

        ps.close();
    }

}
