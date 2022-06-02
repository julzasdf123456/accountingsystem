package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Receiving;
import com.boheco1.dev.integratedaccountingsystem.objects.ReceivingItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                "SELECT Stocks.id, Stocks.StockName, Stocks.Description, Stocks.Unit, Stocks.LocalCode, Stocks.AcctgCode, " +
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
