package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.IRItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Receiving;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class IRDao {
    public static List<IRItem> generateReport(int year, int month) throws Exception {
        ArrayList<IRItem> irItems = new ArrayList<>();

        ResultSet rs = DB.getConnection().createStatement().executeQuery(
                "SELECT " +
                        "stk.id," +
                        "stk.LocalCode, " +
                        "stk.NEACode, " +
                        "stk.Description, " +
                        "stk.Quantity, " +
                        "stk.Price," +
                        "(SELECT TOP 1 Price FROM StockHistory sh WHERE sh.StockID = stk.id AND month(date)=9 AND year(date)=2022 ORDER BY date DESC) AS BegPrice," +
                        "(SELECT SUM(Quantity) FROM StockEntryLogs AS sel WHERE sel.StockID=stk.id AND month(sel.CreatedAt)=9 AND year(sel.CreatedAt)=2022 GROUP BY sel.StockID) AS Received," +
                        "(SELECT AVG(Price) FROM StockEntryLogs AS sel WHERE sel.StockID=stk.id AND month(sel.CreatedAt)=9 AND year(sel.CreatedAt)=2022 GROUP BY sel.StockID) AS ReceivedPrice," +
                        "(SELECT SUM(Quantity) FROM Releasing rls WHERE rls.StockID=stk.id AND month(rls.CreatedAt)=9 AND year(rls.CreatedAt)=2022 AND rls.Status='released' GROUP BY rls.StockID) AS Released," +
                        "(SELECT AVG(Price) FROM Releasing rls WHERE rls.StockID=stk.id AND month(rls.CreatedAt)=9 AND year(rls.CreatedAt)=2022 AND rls.Status='released' GROUP BY rls.StockID) AS ReleasedPrice," +
                        "(SELECT SUM(mri.Quantity) FROM MRTItem mri INNER JOIN Releasing rls2 ON rls2.id=mri.releasing_id WHERE rls2.StockID=stk.id GROUP BY rls2.StockID) AS Returned," +
                        "(SELECT AVG(rls2.Price) FROM MRTItem mri INNER JOIN Releasing rls2 ON rls2.id=mri.releasing_id WHERE rls2.StockID=stk.id GROUP BY rls2.StockID) AS ReturnedPrice " +
                        "FROM Stocks stk"
        );

        while(rs.next()) {
            IRItem item = new IRItem();
            item.setStockId(rs.getString("id"));
            item.setLocalCode(rs.getString("LocalCode"));
            item.setNeaCode(rs.getString("NEACode"));
            item.setQuantity(rs.getDouble("Quantity"));
            item.setPrice(rs.getDouble("Price"));
            item.setQuantity(rs.getDouble("Quantity"));
            item.setReceivedQty(rs.getDouble("Received"));
            item.setReceivedPrice(rs.getDouble("ReceivedPrice"));
            item.setReleasedQty(rs.getDouble("Released"));
            item.setReleasedPrice(rs.getDouble("ReleasedPrice"));
            item.setReturnedQty(rs.getDouble("Returned"));
            item.setReturnedPrice(rs.getDouble("ReturnedPrice"));

            irItems.add(item);
        }

        return irItems;
    }

    public static String getReceivingReference(String stockID, int year, int month) throws Exception{
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "SELECT ri.RRNo FROM ReceivingItem ri " +
                        "INNER JOIN Receiving r ON r.RRNo =ri.RRNo " +
                        "WHERE ri.StockID=? " +
                        "AND month(r.[Date])=? AND year(r.[Date])=?");

        ps.setString(1, stockID);
        ps.setInt(2, month);
        ps.setInt(3,year);

        ResultSet rs = ps.executeQuery();

        System.out.println(stockID + " | " + year + " | " + month);

        if(rs.next()) {
            return rs.getString("RRNo");
        }else {
            return "No ref: " + year + ", " + month;
        }
    }
}
