package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSSignatory;
import com.boheco1.dev.integratedaccountingsystem.objects.User;

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
    public static List<MIRSSignatory> getAllMIRS(User val) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "select MIRSSignatories.*, MIRS.DateFiled, MIRS.Purpose from MIRS LEFT JOIN MIRSSignatories on MIRS.id = MIRSSignatories.MIRSID WHERE MIRSSignatories.user_id = ? AND MIRSSignatories.Status = ? AND MIRS.Status = ? ORDER BY DateFiled DESC");
        ps.setString(1, val.getId());
        ps.setString(2, Utility.PENDING);
        ps.setString(3, Utility.PENDING);

        ResultSet rs = ps.executeQuery();

        ArrayList<MIRSSignatory> mirsList = new ArrayList<>();

        while(rs.next()) {
            mirsList.add(new MIRSSignatory(
                    rs.getString("id"),
                    rs.getString("MIRSID"),
                    rs.getString("user_id"),
                    rs.getString("Status"),
                    rs.getString("Comments"),
                    rs.getTimestamp("CreatedAt").toLocalDateTime(),
                    rs.getTimestamp("UpdatedAt").toLocalDateTime(),
                    rs.getTimestamp("DateFiled").toLocalDateTime().toLocalDate(),
                    rs.getString("Purpose")));
        }

        rs.close();
        ps.close();

        return mirsList;
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

    public static int getSignatoryCount(String  id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT COUNT(id) as TotalCount FROM MIRSSignatories WHERE MIRSID=? AND (Status=? OR Status=?)");
        ps.setString(1, id);
        ps.setString(2, Utility.PENDING);
        ps.setString(3, Utility.REJECTED);

        ResultSet rs = ps.executeQuery();

        String count = "";

        while(rs.next()) {
            count = rs.getString("TotalCount");
        }

        rs.close();
        ps.close();

        return Integer.parseInt(count);
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
