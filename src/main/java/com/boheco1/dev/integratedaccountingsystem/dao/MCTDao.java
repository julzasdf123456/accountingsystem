package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.MCT;
import com.boheco1.dev.integratedaccountingsystem.objects.MCTReleasings;
import com.boheco1.dev.integratedaccountingsystem.objects.Releasing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MCTDao {
    public static void create(MCT mct, List<Releasing> releasings) throws Exception {

        Connection conn = DB.getConnection();

        conn.setAutoCommit(false);

        PreparedStatement ps1 = conn.prepareStatement(
                "INSERT INTO MCT (mct_no, particulars, address, MIRSNo, createdAt, WorkOrderNo) " +
                        "VALUES (?,?,?,?,GETDATE(),?)");

        PreparedStatement ps2 = conn.prepareStatement(
                "UPDATE Releasing SET mct_no=? WHERE id=?");

        try {

            ps1.setString(1, mct.getMctNo());
            ps1.setString(2, mct.getParticulars());
            ps1.setString(3, mct.getAddress());
            ps1.setString(4, mct.getMirsNo());
            ps1.setString(5, mct.getWorkOrderNo());

            ps1.executeUpdate();

            for(Releasing releasing: releasings) {
                if(releasing.getMctNo()!=null) {
                    throw new SQLException("Releasing with StockID:" + releasing.getStockID()
                            + " on MIRS# " + releasing.getMirsID()
                            + " is already included in MCT# " + releasing.getMctNo());
                }
                ps2.setString(1, mct.getMctNo());
                ps2.setString(2, releasing.getId());
                ps2.executeUpdate();
            }

            conn.commit();
        }catch(SQLException ex) {
            ex.printStackTrace();
            conn.rollback();
        }

        ps1.close();
    }

    public static void update(MCT mct) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE MCT SET particulars=?, address=?, MIRSNo=?, WorkOrderNo=? " +
                        "WHERE mct_no=?");

        ps.setString(1, mct.getParticulars());
        ps.setString(2, mct.getAddress());
        ps.setString(3, mct.getMirsNo());
        ps.setString(4, mct.getWorkOrderNo());
        ps.setString(5, mct.getMctNo());

        ps.executeUpdate();

        ps.close();
    }

    public static MCT getMCT(String mctNo) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MCT WHERE mct_no=?");
        ps.setString(1, mctNo);

        ResultSet rs = ps.executeQuery();

        MCT mct=null;

        if(rs.next()) {
            mct = new MCT(
                    rs.getString("mct_no"),
                    rs.getString("particulars"),
                    rs.getString("address"),
                    rs.getString("MIRSNo"),
                    rs.getString("WorkOrderNo"),
                    rs.getDate("createdAt").toLocalDate()
            );
        }

        rs.close();

        ps.close();

        return mct;
    }

    public static MCTReleasings getMCTReleasing(String mctNo) throws Exception {
        MCTReleasings mctReleasings=new MCTReleasings();

        PreparedStatement ps1 = DB.getConnection().prepareStatement(
                "SELECT * FROM MCT WHERE mct_no=?");

        ps1.setString(1, mctNo);

        ResultSet rs1 = ps1.executeQuery();

        if(rs1.next()) {
            mctReleasings.setMct(new MCT(
                    rs1.getString("mct_no"),
                    rs1.getString("particulars"),
                    rs1.getString("address"),
                    rs1.getString("MIRSNo"),
                    rs1.getString("WorkOrderNo"),
                    rs1.getDate("createdAt").toLocalDate()
            ));
        }

        PreparedStatement ps2 = DB.getConnection().prepareStatement(
                "SELECT * FROM Releasing WHERE mct_no=?");
        ps2.setString(1, mctNo);

        ResultSet rs2 = ps2.executeQuery();

        List<Releasing> releasings = new ArrayList();

        while(rs2.next()) {
            Releasing r = new Releasing(
                    rs2.getString("id"),
                    rs2.getString("StockID"),
                    rs2.getString("MIRSID"),
                    rs2.getInt("Quantity"),
                    rs2.getDouble("Price"),
                    rs2.getString("UserID"),
                    rs2.getString("Status"),
                    rs2.getString("MR"),
                    rs2.getString("WorkOrderNo"),
                    rs2.getString("mct_no"));
            releasings.add(r);
        }

        mctReleasings.setReleasings(releasings);

        rs1.close();
        rs2.close();
        ps1.close();
        ps2.close();

        return mctReleasings;
    }

}
