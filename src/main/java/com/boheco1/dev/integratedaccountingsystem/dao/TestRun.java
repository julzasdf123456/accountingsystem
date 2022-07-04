package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.NumberGenerator;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.sql.PreparedStatement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestRun {
    public static void main(String[] args) {
        try {
            List<StockDescription> list = new ArrayList<>();
            list = StockDAO.searchDescription("BLADE");
            for(StockDescription sd: list) {
                System.out.println(sd.getId() + " : " + sd.getDescription());
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void generateSupplierIDs() throws Exception {
        List<SupplierInfo> suppliers = SupplierDAO.getAll();
        PreparedStatement ps = DB.getConnection().prepareStatement(
                "UPDATE SupplierInfo SET SupplierID=? WHERE AccountID=?");

        for(SupplierInfo supplier: suppliers) {
            String id = Utility.generateRandomId();
            ps.setString(1, id);
            ps.setString(2, supplier.getAccountID());
            ps.addBatch();
            System.out.println("Generated " + id + " for " + supplier.getAccountID());
        }

        ps.executeBatch();
    }

    private static void testInventory() {
        try {
            List<Stock> critical = StockDAO.getCritical();
            int criticalCount = StockDAO.countCritical();
            System.out.println("There are " + criticalCount + " critical stocks.");
            for(Stock stock: critical) {
                System.out.println("[" + stock.getId() + "] " + stock.getBrand() + " " + stock.getModel() + ": " + stock.getQuantity());
            }

            int trashedCount = StockDAO.countTrashed();
            List<Stock> trashed = StockDAO.getTrashed();
            System.out.println("There are " + trashedCount + " stocks in the trash");
            for(Stock stock: trashed) {
                System.out.println("[" + stock.getId() + "] " + stock.getBrand() + " " + stock.getModel() + ": " + stock.getQuantity());
            }

            ActiveUser.setUser(UserDAO.get("1000"));

            Stock stock = StockDAO.get("4");
            StockDAO.stockEntry(stock, new StockEntryLog("",stock.getId(),60,"Purchases",126.70,"",null,null,"") );

            List<StockEntryLog> logs = StockDAO.getEntryLogs(stock);
            for(StockEntryLog log: logs) {
                System.out.println(log.getId() + ": " + log.getQuantity());
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }


}
