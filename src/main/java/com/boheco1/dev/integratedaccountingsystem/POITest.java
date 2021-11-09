package com.boheco1.dev.integratedaccountingsystem;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;

public class POITest {
    public static void main(String[] args) {
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
