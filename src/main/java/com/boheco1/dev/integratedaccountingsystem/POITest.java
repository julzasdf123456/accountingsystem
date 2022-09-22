package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class POITest {
    public static void main(String[] args) {
//        try {

//            Notifications tochecker = new Notifications("details", "Utility.NOTIF_MIRS_APROVAL", "ActiveUser.getUser().getEmployeeID()", "checkedEmployeeInfo", "mirs");
//            NotificationsDAO.create(tochecker);
//            String ref = IRDao.getReceivingReference("1658746896033-OQ0RM8L0P29W9RV",2022,9);
//            System.out.println(ref);
//        }catch(Exception ex) {
//            ex.printStackTrace();
//        }
        executePOITest();

    }


    public static void testMCT() throws Exception{
        MCT mct = new MCT("2022-89-001","Integral Testing Only", "Clarin, Bohol", "multiple","-", LocalDate.now());
        List<Releasing> releasings = new ArrayList();
        releasings.add(ReleasingDAO.get("1653508612559-63HHRS8YH4WKVXU"));
        releasings.add(ReleasingDAO.get("1653508612559-8jyHRS8YH4WKVXU"));
        releasings.add(ReleasingDAO.get("1653681369649-SPPXBW6APP2118Z"));

        MCTDao.create(mct, releasings);

        MCT mct1 = MCTDao.getMCT("2022-89-001");
        System.out.println(mct1.getMctNo() + " " + mct1.getParticulars() + " " + mct1.getAddress());

        MCTReleasings mctReleasings = MCTDao.getMCTReleasing("2022-89-001");
        System.out.println(mctReleasings.getMct().getMctNo() + " " + mctReleasings.getMct().getParticulars() + " " + mctReleasings.getMct().getAddress());
        System.out.println("Releasings...");
        for(Releasing releasing: mctReleasings.getReleasings()) {
            System.out.println(releasing.getStockID() + " " + releasing.getMctNo() + " " + releasing.getMirsID());
        }
    }

    public static void executePOITest() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sample Sheet");

        try {
            int year = 2022;
            int month = 9;
            List<IRItem> irItems = IRDao.generateReport(year,month);

            System.out.println("Creating Excel File...");

            int i=0;
            for(IRItem item: irItems) {
                Row row = sheet.createRow(i++);
                row.createCell(0).setCellValue(item.getCode());
                row.createCell(1).setCellValue(item.getDescription());
                row.createCell(2).setCellValue(item.getBeginningQty());
                row.createCell(3).setCellValue(item.getBeginningPrice());
                row.createCell(4).setCellValue(item.getBeginningAmount());
                row.createCell(5).setCellValue(item.getReceivedReference(year, month));
                row.createCell(6).setCellValue(item.getReceivedQty());
                row.createCell(7).setCellValue(item.getReceivedPrice()*item.getReceivedQty());
                row.createCell(8).setCellValue(item.getReturnedReference(year, month));
                row.createCell(9).setCellValue(item.getReturnedQty());
                row.createCell(10).setCellValue(item.getReturnedPrice());
                row.createCell(11).setCellValue(item.getReleasedQty());
                row.createCell(12).setCellValue(item.getReleasedPrice());
            }


            FileOutputStream out = new FileOutputStream("test.xlsx");
            workbook.write(out);
            workbook.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }

    }
}
