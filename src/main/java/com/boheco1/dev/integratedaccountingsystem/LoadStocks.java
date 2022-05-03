package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.*;

public class LoadStocks {
    public static void main(String[] args) {
        String path = "C:\\Users\\Benjie Lenteria\\Documents\\Stock-Items.csv";
        File file = new File(path);

        try {
            CSVReader csvReader = new CSVReader(new FileReader(file));
            String[] tempArr;
            int count = 0;

            StringBuilder errors = new StringBuilder();

            while( (tempArr = csvReader.readNext()) != null ) {
                try {
                    Stock s = new Stock(tempArr[0], tempArr[1], Double.parseDouble(tempArr[2]));
                    StockDAO.add(s);
                    System.out.println(tempArr[0] + " | " + tempArr[1] + " | " + tempArr[2]);
                    count++;
                }catch(NumberFormatException exn) {
                    errors.append(exn.getMessage() + "\n");
                } catch (Exception e) {
                    errors.append(e.getMessage() + "\n");
                }
            }

            System.out.println("Done. " + count + " records processed.");
            System.out.println("Errors encountered...");
            System.out.println(errors);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (CsvValidationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
