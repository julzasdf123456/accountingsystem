package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UpdateStockFromExcelFile {
    public static void main(String[] args) {
        try {
            JFileChooser jf = new JFileChooser();
            jf.showOpenDialog(null);

            File file = jf.getSelectedFile();

            FileInputStream ios = new FileInputStream(file);

            Workbook workbook = new HSSFWorkbook(ios);

            Sheet firstSheet = workbook.getSheet("JUN.22");

            Connection conn = DB.getConnection();

            PreparedStatement ps = conn.prepareStatement(
                    "UPDATE Stocks SET Quantity=?, Price=? WHERE Description=?");


            PreparedStatement pscheck = conn.prepareStatement("SELECT * FROM Stocks WHERE Description=?");

            int notFound = 0;
            int found = 0;

            //check excel against database
            for(int i=12; i<=294; i++) {
                Row row = firstSheet.getRow(i);
                Cell cCell = row.getCell(2);

                pscheck.setString(1, cCell.getStringCellValue());

                ResultSet rs = pscheck.executeQuery();

                if(rs.next()) {
                    found++;
                }else {
                    notFound++;
                }
            }

            System.out.println("Found: " + found);
            System.out.println("Not Found: " + notFound);

            //Update the database.
//            conn.setAutoCommit(false);
//
//            for(int i=12; i<=294; i++) {
//                Row row = firstSheet.getRow(i);
//                Cell cCell = row.getCell(2);
//                Cell oCell = row.getCell(14);
//                Cell pCell = row.getCell(15);
//
//                ps.setInt(1, getIntFromCell(oCell));
//                ps.setDouble(2, getDoubleFromCell(pCell));
//                ps.setString(3, cCell.getStringCellValue());
//                ps.executeUpdate();
//
//            }
//            conn.commit();
//            conn.setAutoCommit(true);

            System.out.println("Done");
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private static int getIntFromCell(Cell cell) {
        try {
            return (int)cell.getNumericCellValue();
        }catch(Exception ex) {
            return 0;
        }
    }

    private static double getDoubleFromCell(Cell cell) {
        try {
            return cell.getNumericCellValue();
        }catch(Exception ex) {
            return 0.0;
        }
    }

}
