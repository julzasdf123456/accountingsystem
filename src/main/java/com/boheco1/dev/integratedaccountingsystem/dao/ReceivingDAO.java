package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Receiving;
import com.boheco1.dev.integratedaccountingsystem.objects.ReceivingItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReceivingDAO {
    public static void add(Receiving receiving) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO Receiving " +
                "(RRNo, Date, RVNo, BLWBNo, Carrier, DRNo, PONo, SupplierID, InvoiceNo, ReceivedBy, ReceivedOrigBy, VerifiedBy) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        String rrno = generateRRNo();
        ps.setString(1, rrno);
        ps.setDate(2, Date.valueOf(receiving.getDate()));
        ps.setString(3, receiving.getRvNo());
        ps.setString(4, receiving.getBlwbNo());
        ps.setString(5, receiving.getCarrier());
        ps.setString(6, receiving.getDrNo());
        ps.setString(7, receiving.getPoNo());
        ps.setString(8, receiving.getSupplierId());
        ps.setString(9, receiving.getInvoiceNo());
        ps.setString(10, receiving.getReceivedBy());
        ps.setString(11, receiving.getReceivedOrigBy());
        ps.setString(12, receiving.getVerifiedBy());

        ps.executeUpdate();

        receiving.setRrNo(rrno);

        ps.close();
    }

    public static Receiving get(String rrNo) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("SELECT * FROM Receiving WHERE RRNo=?");
        ps.setString(1, rrNo);
        ResultSet rs = ps.executeQuery();

        Receiving receiving = null;

        if(rs.next()) {
            receiving = new Receiving(
                    rs.getString("RRNo"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getString("RVNo"),
                    rs.getString("BLWBNo"),
                    rs.getString("Carrier"),
                    rs.getString("DRNo"),
                    rs.getString("PONo"),
                    rs.getString("SupplierID"),
                    rs.getString("InvoiceNo"),
                    rs.getString("ReceivedBy"),
                    rs.getString("ReceivedOrigBy"),
                    rs.getString("VerifiedBy")
            );
        }

        rs.close();

        return receiving;
    }

    public static ReceivingItem getItem(String rrno, String stock_id) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM ReceivingItem WHERE RRNo = ? AND StockID = ?");
        ps.setString(1, rrno);
        ps.setString(2, stock_id);

        ResultSet rs = ps.executeQuery();

        ReceivingItem item = new ReceivingItem();
        while(rs.next()) {
            item.setRrNo(rrno);
            item.setStockId(rs.getString("StockID"));
            item.setUnitCost(rs.getDouble("UnitCost"));
            item.setQtyDelivered(rs.getInt("QtyDelivered"));
            item.setQtyAccepted(rs.getInt("QtyAccepted"));
        }
        rs.close();
        return item;
    }

    public static void update(Receiving receiving) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement("UPDATE Receiving " +
                "SET Date=?, RVNo=?, BLWBNo=?, Carrier=?, DRNo=?, PONo=?, SupplierID=?, " +
                "InvoiceNo=?, ReceivedBy=?, ReceivedOrigBy=?, VerifiedBy=? " +
                "WHERE RRNo=?");

        ps.setDate(1, Date.valueOf(receiving.getDate()));
        ps.setString(2, receiving.getRvNo());
        ps.setString(3, receiving.getBlwbNo());
        ps.setString(4, receiving.getCarrier());
        ps.setString(5, receiving.getDrNo());
        ps.setString(6, receiving.getPoNo());
        ps.setString(7, receiving.getSupplierId());
        ps.setString(8, receiving.getInvoiceNo());
        ps.setString(9, receiving.getReceivedBy());
        ps.setString(10, receiving.getReceivedOrigBy());
        ps.setString(11, receiving.getVerifiedBy());
        ps.setString(12, receiving.getRrNo());

        ps.executeUpdate();

        ps.close();
    }

    public static List<Receiving> getByReportNo(String rrno) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Receiving " +
                        "WHERE RRNo LIKE ? ORDER BY Date ASC");
        ps.setString(1, "%"+rrno+"%");


        ResultSet rs = ps.executeQuery();

        ArrayList<Receiving> receivings = new ArrayList<>();

        while(rs.next()) {
            receivings.add(new Receiving(
                    rs.getString("RRNo"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getString("RVNo"),
                    rs.getString("BLWBNo"),
                    rs.getString("Carrier"),
                    rs.getString("DRNo"),
                    rs.getString("PONo"),
                    rs.getString("SupplierID"),
                    rs.getString("InvoiceNo"),
                    rs.getString("ReceivedBy"),
                    rs.getString("ReceivedOrigBy"),
                    rs.getString("VerifiedBy")
            ));
        }

        rs.close();

        return receivings;
    }

    public static List<Receiving> getByDateRange(LocalDate from, LocalDate to) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT * FROM Receiving " +
                "WHERE Date BETWEEN ? AND ? ORDER BY Date ASC");
        ps.setDate(1, Date.valueOf(from));
        ps.setDate(2, Date.valueOf(to));

        ResultSet rs = ps.executeQuery();

        ArrayList<Receiving> receivings = new ArrayList<>();

        while(rs.next()) {
            receivings.add(new Receiving(
                    rs.getString("RRNo"),
                    rs.getDate("Date").toLocalDate(),
                    rs.getString("RVNo"),
                    rs.getString("BLWBNo"),
                    rs.getString("Carrier"),
                    rs.getString("DRNo"),
                    rs.getString("PONo"),
                    rs.getString("SupplierID"),
                    rs.getString("InvoiceNo"),
                    rs.getString("ReceivedBy"),
                    rs.getString("ReceivedOrigBy"),
                    rs.getString("VerifiedBy")
            ));
        }

        rs.close();

        return receivings;
    }

    public static void addItems(String RRNo, List<ReceivingItem> items) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "INSERT INTO ReceivingItem " +
                        "(RRNo, StockID, QtyDelivered, QtyAccepted, UnitCost) " +
                        "VALUES (?,?,?,?,?)");

        ps.setString(1, RRNo);
        for(ReceivingItem item: items) {
            ps.setString(2, item.getStockId());
            ps.setInt(3, item.getQtyDelivered());
            ps.setInt(4, item.getQtyAccepted());
            ps.setDouble(5, item.getUnitCost());

            ps.addBatch();
        }

        ps.executeBatch();

        ps.close();
    }

    public static void addItems(Receiving receiving, List<ReceivingItem> items) throws Exception {
        Connection conn = DB.getConnection();
        conn.setAutoCommit(false);
        //Add Receiving statement
        PreparedStatement ps = DB.getConnection().prepareStatement("INSERT INTO Receiving " +
                "(RRNo, Date, RVNo, BLWBNo, Carrier, DRNo, PONo, SupplierID, InvoiceNo, ReceivedBy, ReceivedOrigBy, VerifiedBy) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?)");
        //Add Receiving item statement
        PreparedStatement ps2 = conn.prepareStatement(
                "INSERT INTO ReceivingItem " +
                        "(RRNo, StockID, QtyDelivered, QtyAccepted, UnitCost) " +
                        "VALUES (?,?,?,?,?)");

        PreparedStatement ps3 = conn.prepareStatement("UPDATE Stocks SET Price=?, Quantity=Quantity+? WHERE id=?");

        PreparedStatement ps4 = conn.prepareStatement(
                "INSERT INTO StockHistory (id, StockID, date, price, updatedBy) " +
                        "VALUES (?,?,?,?,?)");

        PreparedStatement ps5 = conn.prepareStatement(
                "INSERT INTO StockEntryLogs " +
                        "(StockID, Quantity, Source, Price, UserID, CreatedAt, UpdatedAt, id, RRNo) " +
                        "VALUES " +
                        "(?,?,?,?,?,GETDATE(),GETDATE(), ?, ?)");
        try {
            //Insert Receiving
            String rrno = generateRRNo();
            ps.setString(1, rrno);
            ps.setDate(2, Date.valueOf(receiving.getDate()));
            ps.setString(3, receiving.getRvNo());
            ps.setString(4, receiving.getBlwbNo());
            ps.setString(5, receiving.getCarrier());
            ps.setString(6, receiving.getDrNo());
            ps.setString(7, receiving.getPoNo());
            ps.setString(8, receiving.getSupplierId());
            ps.setString(9, receiving.getInvoiceNo());
            ps.setString(10, receiving.getReceivedBy());
            ps.setString(11, receiving.getReceivedOrigBy());
            ps.setString(12, receiving.getVerifiedBy());

            ps.executeUpdate();

            //Set RRNO
            receiving.setRrNo(rrno);

            //Set RRNO to item
            ps2.setString(1, rrno);
            boolean createHistory = false;
            for (ReceivingItem item : items) {
                Stock stock = StockDAO.get(item.getStockId());

                //Insert Receiving item
                ps2.setString(2, item.getStockId());
                ps2.setInt(3, item.getQtyDelivered());
                ps2.setInt(4, item.getQtyAccepted());
                ps2.setDouble(5, item.getUnitCost());
                ps2.addBatch();

                //Update quantity and prices in Stocks
                ps3.setDouble(1, item.getUnitCost());
                ps3.setInt(2, item.getQtyAccepted());
                ps3.setString(3, item.getStockId());
                ps3.addBatch();

                if (stock.getPrice() != item.getUnitCost()) {
                    //Insert Stock History
                    ps4.setString(1, Utility.generateRandomId());
                    ps4.setString(2, item.getStockId());
                    ps4.setDate(3, java.sql.Date.valueOf(LocalDate.now()));
                    ps4.setDouble(4, stock.getPrice());
                    ps4.setString(5, ActiveUser.getUser().getId());
                    ps4.addBatch();
                    createHistory = true;
                }

                //Insert Stock Entry Log
                ps5.setString(1, item.getStockId());
                ps5.setInt(2, item.getQtyAccepted());
                ps5.setString(3, "Purchased");
                ps5.setDouble(4, item.getUnitCost());
                ps5.setString(5, ActiveUser.getUser().getId());
                ps5.setString(6, Utility.generateRandomId());
                ps5.setString(7, rrno);
                ps5.addBatch();
            }
            ps2.executeBatch();
            ps3.executeBatch();
            if (createHistory)
                ps4.executeBatch();
            ps5.executeBatch();
            conn.commit();
        }catch(SQLException ex) {
            conn.rollback();
            throw new Exception(ex.getMessage());
        }
        ps.close();
        ps2.close();
        ps3.close();
        ps4.close();
        ps5.close();
        conn.setAutoCommit(true);
    }

    private static String generateRRNo() throws Exception {
        LocalDate today = LocalDate.now();
        int year = today.getYear();
        String newRRNo = null;

         PreparedStatement ps = DB.getConnection().prepareStatement("SELECT RRNo FROM Receiving " +
                 "WHERE RRNo LIKE ? ORDER BY RRNo DESC");
         ps.setString(1, year + "%");
         ResultSet rs = ps.executeQuery();
         if(rs.next()) {
             String rrNo = rs.getString("RRNo");
             String parsed = rrNo.split("-")[1];
             try {
                 int serial = Integer.parseInt(parsed) + 1;
                 String no = serial+"";
                 if (no.length() == 1){
                     no = "000"+serial;
                 }else if (no.length() == 2) {
                     no = "00"+serial;
                 }else if (no.length() == 3) {
                     no = "0"+serial;
                 }
                 return year + "-" + no;
             }catch(NumberFormatException ex) {
                 return year + "-" + "0001";
             }
         }

         rs.close();

         return year + "-" + "0001";
    }

    public static List<Stock> getReceivingItems(String rrno) throws Exception {
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT Stocks.id, Stocks.StockName, Stocks.Description, Stocks.Unit, Stocks.LocalCode, Stocks.AcctgCode, Stocks.NEACode, " +
                        "ReceivingItem.QtyDelivered, ReceivingItem.QtyAccepted, ReceivingItem.UnitCost " +
                        "FROM Stocks LEFT JOIN ReceivingItem " +
                        "ON ReceivingItem.StockID=Stocks.id " +
                        "WHERE RRNo = ? ORDER BY Stocks.StockName ASC");
        ps.setString(1, rrno);

        ResultSet rs = ps.executeQuery();

        ArrayList<Stock> stocks = new ArrayList<>();

        while(rs.next()) {
            Stock stock = new Stock();
            stock.setId(rs.getString("id"));
            stock.setStockName(rs.getString("StockName"));
            stock.setDescription(rs.getString("Description"));
            stock.setUnit(rs.getString("Unit"));
            stock.setLocalCode(rs.getString("LocalCode"));
            stock.setAcctgCode(rs.getString("AcctgCode"));
            stock.setNeaCode(rs.getString("NEACode"));

            ReceivingItem item = new ReceivingItem();
            item.setRrNo(rrno);
            item.setStockId(rs.getString("id"));
            item.setUnitCost(rs.getDouble("UnitCost"));
            item.setQtyDelivered(rs.getInt("QtyDelivered"));
            item.setQtyAccepted(rs.getInt("QtyAccepted"));

            stock.setReceivingItem(item);
            stocks.add(stock);
        }

        rs.close();

        return stocks;
    }
}
