package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
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
                "INSERT INTO MIRSSignatories " +
                        "(MIRSID, user_id, Status, Comments, CreatedAt, UpdatedAt,id) " +
                        "VALUES " +
                        "(?,?,?,?,GETDATE(),GETDATE(),?)");
        msig.setId(Utility.generateRandomId());
        ps.setString(1, msig.getMirsID());
        ps.setString(2, msig.getUserID());
        ps.setString(3, msig.getStatus());
        ps.setString(4, msig.getComments());
        ps.setString(5, msig.getId());

        ps.executeUpdate();

        ps.close();
    }

    public static void update(MIRSSignatory msig) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MIRSSignatories SET " +
                        "MIRSID=?, user_id=?, Status=?, Comments=?, UpdatedAt=GETDATE() " +
                        "WHERE id=?");
        ps.setString(1, msig.getMirsID());
        ps.setString(2, msig.getUserID());
        ps.setString(3, msig.getStatus());
        ps.setString(4, msig.getComments());
        ps.setString(5, msig.getId());

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
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("user_id"),
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
        ps.setString(1, mirs.getId());

        ArrayList<MIRSSignatory> mirsSignatories = new ArrayList<>();

        ResultSet rs = ps.executeQuery();

        while(rs.next()) {
            mirsSignatories.add(new MIRSSignatory(
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("user_id"),
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
