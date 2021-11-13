package com.boheco1.dev.integratedaccountingsystem.dao;

import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRSItem;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TestRun {
    public static void main(String[] args) {
        try {
            testMIRS();
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
