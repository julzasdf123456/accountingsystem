package com.boheco1.dev.integratedaccountingsystem;


import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.sql.Connection;

public class LoadStockFromExcel {

    public static void main(String[] args) {
        JFileChooser jf = new JFileChooser();

        jf.showOpenDialog(null);

        File file = jf.getSelectedFile();

        if(file==null) {
            System.err.println("No file.");
            return;
        }

        Connection conn=null;

        try {

            conn = DB.getConnection();
            FileInputStream ios = new FileInputStream(file);

            Workbook workbook = new XSSFWorkbook(ios);

            /**
             * 0NEA Code | 1Description | 2Price | 3Local Desc | 4Brand | 5Local Code | 6Accounting Code | 7Item Code | 8Qty
             */

            Sheet sheet1 = workbook.getSheet("Sheet1");

            int index = 0;
            Cell cell = null;
            Row row = sheet1.getRow(index++);


            conn.setAutoCommit(false);

            while(row != null) {
                row = sheet1.getRow(index++);

                if(row==null) break;

                Stock stock = new Stock();

                Cell descriptionCell = row.getCell(1);
                if(descriptionCell!=null) stock.setDescription(descriptionCell.getStringCellValue());

                Cell neaCodeCell = row.getCell(0);
                if(neaCodeCell!=null) stock.setNeaCode(neaCodeCell.getStringCellValue());

                Cell priceCell = row.getCell(2);
                if(priceCell!=null) stock.setPrice(getDoubleFromCell(priceCell));

                Cell brandCell = row.getCell(4);
                if(brandCell!=null) stock.setBrand(brandCell.getStringCellValue());

                Cell localCodeCell = row.getCell(5);
                if(localCodeCell!=null) stock.setLocalCode(localCodeCell.getStringCellValue());

                Cell accountingCodeCell = row.getCell(6);
                if(accountingCodeCell!=null) stock.setAcctgCode(accountingCodeCell.getStringCellValue());

                Cell qtyCell = row.getCell(8);
                if(qtyCell != null) stock.setQuantity( getIntFromCell(qtyCell) );

                StockDAO.add(stock);
                System.out.println(stock.getNeaCode() + " " + stock.getDescription() + ", " + stock.getPrice());
            }

            conn.commit();

            conn.setAutoCommit(true);

            System.out.println("Last Index: " + index);
        }catch(Exception ex) {
            try {
                conn.rollback();
            }catch(Exception ex2) {
                ex2.printStackTrace();
            }
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
