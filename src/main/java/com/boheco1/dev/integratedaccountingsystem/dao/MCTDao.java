package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.NumberGenerator;
import com.boheco1.dev.integratedaccountingsystem.objects.MCT;
import com.boheco1.dev.integratedaccountingsystem.objects.MCTReleasings;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.Releasing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MCTDao {
    public static String create(MCT mct, List<Releasing> releasings) throws Exception {

        String error = null;
        Connection conn = DB.getConnection();

        conn.setAutoCommit(false);

        PreparedStatement ps1 = conn.prepareStatement(
                "INSERT INTO MCT (mct_no, particulars, address, MIRSNo, createdAt, WorkOrderNo) " +
                        "VALUES (?,?,?,?,GETDATE(),?)");
        ps1.setString(1, mct.getMctNo());
        ps1.setString(2, mct.getParticulars());
        ps1.setString(3, mct.getAddress());
        ps1.setString(4, mct.getMirsNo());
        ps1.setString(5, mct.getWorkOrderNo());

        PreparedStatement ps2 = conn.prepareStatement(
                "UPDATE Releasing SET mct_no=? WHERE id=?");
        for(Releasing releasing: releasings) {
            ps2.setString(1, mct.getMctNo());
            ps2.setString(2, releasing.getId());
            ps2.addBatch();
        }

        try {
            ps1.executeUpdate();
            ps2.executeBatch();
            conn.setAutoCommit(true);

            ps1.close();
            ps2.close();
            return error;
        }catch(SQLException ex) {
            ps1.close();
            ps2.close();
            conn.rollback();
            conn.setAutoCommit(true);
            ex.printStackTrace();
            if(ex.getMessage().contains("duplicate key value")){
                mct.setMctNo(NumberGenerator.mctNumber());
                create(mct, releasings);
            }
            return ex.getMessage();
        }
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

    public static List<MCT> getAllMCT(String key) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM MCT WHERE " +
                        "mct_no LIKE ? OR " +
                        "particulars LIKE ? OR " +
                        "address LIKE ? OR " +
                        "MIRSNo LIKE ? OR " +
                        "WorkOrderNo LIKE ?");
        ps.setString(1, "%" + key + "%");
        ps.setString(2, "%" + key + "%");
        ps.setString(3, "%" + key + "%");
        ps.setString(4, "%" + key + "%");
        ps.setString(5, "%" + key + "%");

        ResultSet rs = ps.executeQuery();

        ArrayList<MCT> result = new ArrayList<>();

        while(rs.next()) {
            result.add(new MCT(
                    rs.getString("mct_no"),
                    rs.getString("particulars"),
                    rs.getString("address"),
                    rs.getString("MIRSNo"),
                    rs.getString("WorkOrderNo"),
                    rs.getDate("createdAt").toLocalDate()
            ));
        }

        rs.close();
        ps.close();

        return result;
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
                "  SELECT StockID, MIRSID, SUM(Quantity) AS qty FROM Releasing WHERE mct_no=? GROUP BY StockID, MIRSID ");
        ps2.setString(1, mctNo);

        ResultSet rs2 = ps2.executeQuery();

        List<Releasing> releasings = new ArrayList();

        while(rs2.next()) {
            Releasing r = new Releasing();
            r.setStockID(rs2.getString("StockID"));
            r.setMirsID(rs2.getString("MIRSID"));
            r.setQuantity(rs2.getDouble("qty"));
                    /*rs2.getString("id"),
                    rs2.getString("StockID"),
                    rs2.getString("MIRSID"),
                    rs2.getInt("Quantity"),
                    rs2.getDouble("Price"),
                    rs2.getString("UserID"),
                    rs2.getString("Status"),
                    rs2.getString("WorkOrderNo"),
                    rs2.getString("mct_no"));*/
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
