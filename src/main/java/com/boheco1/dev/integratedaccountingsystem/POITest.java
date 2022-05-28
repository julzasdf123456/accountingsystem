package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.MCTDao;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ReleasingDAO;
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
        try {
           testMCT();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
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

    private static void testMR() {
        MR mr = new MR("1639087755070-3I2VEZOH4MWTMP8",
                "3000",
                "Rubber Knife",
                5,
                212.50f,
                LocalDate.of(2020,4,22));

        MR mr2 = new MR("1639087755070-3I2VEZOH4MWTMP8",
                "2000",
                "1cct8DZOMpP3TSjTQh97XSIIUtPGRp",
                2,
                LocalDate.of(2020,4,21));

        try {
            MrDAO.add(mr);
            System.out.println("First MR created.");
            MrDAO.add(mr2);
            System.out.println("Second MR created.");
            MR mr3 = MrDAO.get(mr2.getId());

            System.out.println("ID: " + mr3.getId() + " Item: " + mr3.getExtItem());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void executePOITest() {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Sample Sheet");
        Object[][] data = {
                {"Name", "Address","Phone"},
                {"Bennie Saturno", "Clarin, Bohol", "123456789"},
                {"Eric Matti", "Tubigon, Bohol", "143447589"},
                {"Sarah Labatti", "Calape, Bohol", "636525417"},
        };

        System.out.println("Creating Excel File...");

        for(int i=0; i<data.length; i++) {
            Row row = sheet.createRow(i);
            for(int j=0; j<3; j++) {
                Cell cell = row.createCell(j);
                cell.setCellValue((String)data[i][j]);
            }
        }

        try {
            FileOutputStream out = new FileOutputStream("test.xlsx");
            workbook.write(out);
            workbook.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
