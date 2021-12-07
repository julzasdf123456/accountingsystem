package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestRun {
    public static void main(String[] args) {
//        try {
//            List<Stock> inventory1 = StockDAO.getInventory(LocalDate.of(2021,11,20), LocalDate.of(2021,11,22));
//            List<Stock> inventory2 = StockDAO.getInventory(LocalDate.of(2021,11,20), LocalDate.of(2021,11,22),1,50);
//
//            System.out.println(inventory1.size());
//            System.out.println(inventory2.size());
//        }catch(Exception ex) {
//            ex.printStackTrace();
//        }
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

            ActiveUser.setUser(UserDAO.get(14));

            Stock stock = StockDAO.get(4);
            StockDAO.stockEntry(stock, new StockEntryLog(-1,stock.getId(),60,"Purchases",126.70,-1,null,null) );

            List<StockEntryLog> logs = StockDAO.getEntryLogs(stock);
            for(StockEntryLog log: logs) {
                System.out.println(log.getId() + ": " + log.getQuantity());
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void testMIRS() throws Exception {
        MIRS mirs = new MIRS(-1, LocalDate.now(), "Testing only", "NO details" ,
                "Pending",1,14, LocalDateTime.now(), LocalDateTime.now());

        MirsDAO.create(mirs);

        System.out.println("MIRS Created with ID: " + mirs.getId());

        mirs.setDetails("Details updated.");
        MirsDAO.update(mirs);

        MIRS mirsCopy = MirsDAO.getMIRS(mirs.getId());

        System.out.println("MIRS updated. " + mirsCopy.getDetails());

        ArrayList<MIRSItem> items = new ArrayList<>();
        items.add(new MIRSItem(-1,-1,4,100,100, "No comment", null, null ));
        items.add(new MIRSItem(-1,-1,5,200,200, "No comment", null, null ));

        System.out.println("Adding two items in a list..");
        MirsDAO.addMIRSItems(mirs, items);
        System.out.println("done.");

        MIRSItem mirsItem = new MIRSItem(-1,-1,6,300,300, "No comment", null, null );

        System.out.println("Adding one item..");
        MirsDAO.addMIRSItem(mirs, mirsItem);
        System.out.println("done");

        System.out.println("Removing 1 Item..");
        MirsDAO.removeItem(mirsItem);
        System.out.println("done.");

        List<MIRSItem> itemsList = MirsDAO.getItems(mirs);

        System.out.println("MIRS Items:");
        for(MIRSItem item: itemsList) {
            System.out.println("MIRS ITEM ID: " + item.getId() + " Stock ID:" + item.getStockID());
        }
    }
}
