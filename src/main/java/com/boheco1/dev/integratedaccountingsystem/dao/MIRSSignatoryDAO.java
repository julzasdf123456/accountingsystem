package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MIRSSignatoryDAO {
    public static void add(MIRSSignatory msig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO MIRSignatories " +
                        "(MIRSID, SignatoriesID, Status, Comments, CreatedAt, UpdatedAt) " +
                        "VALUES " +
                        "(?,?,?,?,GETDATE(),GETDATE())", Statement.RETURN_GENERATED_KEYS);
        ps.setInt(1, msig.getMirsID());
        ps.setInt(2, msig.getSignatoryID());
        ps.setString(3, msig.getStatus());
        ps.setString(4, msig.getComments());

        ps.executeUpdate();

        ResultSet rs = ps.getGeneratedKeys();

        if(rs.next()) msig.setId(rs.getInt(1));

        rs.close();
        ps.close();
    }

    public static void update(MIRSSignatory msig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRSSignatories SET " +
                        "MIRSID=?, SignatoriesID=?, Status=?, Comments=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setInt(1, msig.getMirsID());
        ps.setInt(2, msig.getSignatoryID());
        ps.setString(3, msig.getStatus());
        ps.setString(4, msig.getComments());
        ps.setInt(5, msig.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static MIRSSignatory get(int id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRSSignatories WHERE id=?");
        ps.setInt(1, id);

        MIRSSignatory msig = null;

        ResultSet rs = ps.executeQuery();

        if(rs.next()) {
            msig = new MIRSSignatory(
                    rs.getInt("id"),
                    rs.getInt("MIRSID"),
                    rs.getInt("SignatoriesID"),
                    rs.getString("Status"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime()
            );
        }

        rs.close();
        ps.close();

        return msig;
    }

    public static List<MIRSSignatory> get(MIRS mirs) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MIRSSignatories WHERE MIRSID=?");
        ps.setInt(1, mirs.getId());

        ArrayList<MIRSSignatory> mirsSignatories = new ArrayList<>();

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            mirsSignatories.add(new MIRSSignatory(
                    rs.getInt("id"),
                    rs.getInt("MIRSID"),
                    rs.getInt("SignatoriesID"),
                    rs.getString("Status"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime()
            ));
        }

        ps.close();
        rs.close();

        return mirsSignatories;
    }
}
