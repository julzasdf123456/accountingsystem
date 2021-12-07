package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Signatory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;


public class SignatoryDAO {
    public static void add(Signatory sig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Signatories " +
                        "(Type, UserID, Rank, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,now(), now())", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, sig.getType());
        ps.setString(2, sig.getUserID());
        ps.setInt(3, sig.getRank());
        ps.setString(4, sig.getComments());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) sig.setId(rs.getString(1));

        rs.close();
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

}
