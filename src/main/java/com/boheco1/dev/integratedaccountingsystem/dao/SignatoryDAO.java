package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Signatory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;


public class SignatoryDAO {
    public static void add(Signatory sig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO Signatories " +
                        "(Type, UserID, Rank, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,now(), now())", Statement.RETURN_GENERATED_KEYS);
        ps.setString(1, sig.getType());
        ps.setInt(2, sig.getUserID());
        ps.setInt(3, sig.getRank());
        ps.setString(4, sig.getComments());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) sig.setId(rs.getInt(1));

        rs.close();
        ps.close();
    }

    public static void update(Signatory sig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE Signatories SET " +
                        "Type=?, UserID=?, Rank=?, Comments=?, UpdatedAt=NOW() " +
                        "WHERE id=?");

        ps.setString(1, sig.getType());
        ps.setInt(2, sig.getUserID());
        ps.setInt(3, sig.getRank());
        ps.setString(4, sig.getComments());
        ps.setInt(5, sig.getId());

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
                    rs.getInt("id"),
                    rs.getString("Type"),
                    rs.getInt("UserID"),
                    rs.getInt("Rank"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime());

        }

        rs.close();
        ps.close();

        return sig;
    }
}
