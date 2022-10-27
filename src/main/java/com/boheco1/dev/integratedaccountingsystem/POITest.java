package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.CustomPrintHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import pl.allegro.finance.tradukisto.MoneyConverters;
import pl.allegro.finance.tradukisto.ValueConverters;

import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class POITest extends Application {
    public static void main(String[] args) {
        printOEBR();
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
                row.createCell(5).setCellValue(item.getReceivedReference());
                row.createCell(6).setCellValue(item.getReceivedQty());
                row.createCell(7).setCellValue(item.getReceivedPrice()*item.getReceivedQty());
                row.createCell(8).setCellValue(item.getReturnedReference());
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

    @Override
    public void start(Stage stage) {

        String meter_no = "Meter no:";
        String type = "Type: RM";
        String consumer = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxy";
        String billno = "Bill Number";
        String address = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxy";
        String billmonth = "Bill Month";
        String kwhUsed = "KWH Used";
        String amount = "Bill Amount";
        String vat = "Vat";
        String dueDate = "Due Date";
        String surcharge = "Surcharge";
        String amountDue = "Amount Due";
        String teller = "Teller";
        String receivedDate = "Received date";

        VBox container = new VBox();
        container.setPadding(new Insets(25, 0, 0, 0));
        container.setPrefWidth(612);
        container.setPrefHeight(237.6);

        HBox copyContainer = new HBox();

        VBox left = new VBox();
        VBox right = new VBox();

        HBox.setHgrow(left, Priority.ALWAYS);
        HBox.setHgrow(right, Priority.ALWAYS);

        left.setMaxWidth(Double.MAX_VALUE);
        right.setMaxWidth(Double.MAX_VALUE);

        GridPane teller_grid = new GridPane();
        GridPane consumer_grid = new GridPane();

        ColumnConstraints column1 = new ColumnConstraints();
        column1.setPercentWidth(33.3);

        ColumnConstraints column2 = new ColumnConstraints();
        column2.setPercentWidth(33.3);

        ColumnConstraints column3 = new ColumnConstraints();
        column3.setPercentWidth(33.4);

        teller_grid.getColumnConstraints().addAll(column1,column2, column3);
        consumer_grid.getColumnConstraints().addAll(column1,column2);

        teller_grid.setMaxWidth(Double.MAX_VALUE);
        consumer_grid.setMaxWidth(Double.MAX_VALUE);

        Label spacer1 = new Label(" ");
        Label spacer2 = new Label(" ");
        Label spacer3 = new Label(" ");
        Label spacer4 = new Label(" ");
        Label spacer5 = new Label(" ");
        Label spacer6 = new Label(" ");
        Label spacer7 = new Label(" ");
        Label spacer8 = new Label(" ");
        Label spacer9 = new Label(" ");
        Label spacer10 = new Label(" ");
        Label spacer11 = new Label(" ");
        Label spacer12 = new Label(" ");
        Label spacer13 = new Label(" ");
        Label spacer14 = new Label(" ");


        Label surcharge_lbl = new Label("Surcharge");

        Label tel_meter_lbl = new Label(meter_no);
        Label tel_type_lbl = new Label(type);
        Label tel_consumer_lbl = new Label(consumer);
        Label tel_bill_lbl = new Label(billno);
        Label tel_addr_lbl = new Label(address);
        Label tel_billmonth_lbl = new Label(billmonth);
        Label tel_kwh_lbl = new Label(kwhUsed);
        Label tel_amount_lbl = new Label(amount);

        Label tel_vat_lbl = new Label(vat);
        Label tel_surcharge_lbl = new Label(surcharge);
        Label tel_duedate_lbl = new Label(dueDate);
        Label tel_amountDue_lbl = new Label(amountDue);
        Label tel_teller_lbl = new Label(teller);
        Label tel_receivedDate_lbl = new Label(receivedDate);

        // page.add(Node, colIndex, rowIndex, colSpan, rowSpan):
        teller_grid.add(tel_meter_lbl,0,0,2,1);
        teller_grid.add(tel_type_lbl, 2, 0,1,1);
        teller_grid.add(tel_consumer_lbl, 0, 1, 2,1);
        teller_grid.add(tel_bill_lbl, 2, 1, 1,1);

        teller_grid.add(tel_addr_lbl, 0, 2, 3,1);

        teller_grid.add(spacer1, 1, 2, 3,1);
        teller_grid.add(spacer2, 0, 3, 3,1);

        teller_grid.add(tel_billmonth_lbl, 0, 4, 1,1);
        teller_grid.add(tel_kwh_lbl, 1, 4, 1,1);
        teller_grid.add(tel_amount_lbl, 2, 4, 1,1);

        teller_grid.add(spacer3, 0, 5, 2,1);
        teller_grid.add(tel_vat_lbl, 2, 5, 1,1);

        teller_grid.add(spacer4, 0, 6, 3,1);

        teller_grid.add(spacer5, 0, 7, 1,1);
        teller_grid.add(surcharge_lbl, 1, 7, 1,1);
        teller_grid.add(tel_surcharge_lbl, 2, 7, 1,1);

        teller_grid.add(tel_duedate_lbl, 0, 8, 2,1);
        teller_grid.add(tel_amountDue_lbl, 2, 8, 1,1);

        teller_grid.add(spacer6, 0, 9, 3,1);
        teller_grid.add(spacer7, 0, 10, 3,1);

        teller_grid.add(tel_teller_lbl, 0, 11, 2,1);
        teller_grid.add(tel_receivedDate_lbl, 2, 11, 1,1);

        Label con_meter_lbl = new Label(meter_no);
        Label con_type_lbl = new Label(type);
        Label con_consumer_lbl = new Label(consumer);
        Label con_bill_lbl = new Label(billno);
        Label con_addr_lbl = new Label(address);

        Label con_billmonth_lbl = new Label(billmonth);
        Label con_kwh_lbl = new Label(kwhUsed);
        Label con_amount_lbl = new Label(amount);

        Label con_vat_lbl = new Label(vat);
        Label con_surcharge_lbl = new Label(surcharge);
        Label con_duedate_lbl = new Label(dueDate);
        Label con_amountDue_lbl = new Label(amountDue);
        Label con_teller_lbl = new Label(teller);
        Label con_receivedDate_lbl = new Label(receivedDate);


        consumer_grid.add(con_meter_lbl, 0, 0, 2 , 1);
        consumer_grid.add(con_type_lbl, 2, 0, 1,1);
        consumer_grid.add(con_consumer_lbl, 0, 1, 2,1);
        consumer_grid.add(con_bill_lbl, 2, 1, 1,1);
        consumer_grid.add(con_addr_lbl, 0, 2, 3,1);

        consumer_grid.add(spacer8, 1, 2, 3,1);
        consumer_grid.add(spacer9, 0, 3, 3 ,1);

        consumer_grid.add(con_billmonth_lbl, 0, 4, 1,1);
        consumer_grid.add(con_kwh_lbl, 1, 4, 1,1);
        consumer_grid.add(con_amount_lbl, 2, 4, 1,1);

        consumer_grid.add(spacer10, 0, 5, 2,1);
        consumer_grid.add(con_vat_lbl, 2, 5, 1,1);

        consumer_grid.add(spacer11, 0, 6, 3,1);

        consumer_grid.add(spacer12, 0, 7, 1,1);
        consumer_grid.add(surcharge_lbl, 1, 7, 1,1);
        consumer_grid.add(con_surcharge_lbl, 2, 7, 1,1);

        consumer_grid.add(con_duedate_lbl, 0, 8, 2,1);
        consumer_grid.add(con_amountDue_lbl, 2, 8, 1,1);

        consumer_grid.add(spacer13, 0, 9, 3,1);
        consumer_grid.add(spacer14, 0, 10, 3,1);

        consumer_grid.add(con_teller_lbl, 0, 11, 2,1);
        consumer_grid.add(con_receivedDate_lbl, 2, 11, 1,1);

        left.getChildren().add(teller_grid);
        right.getChildren().add(consumer_grid);

        //Add left and right containers to main node container
        copyContainer.getChildren().addAll(left, right);
        container.getChildren().add(copyContainer);

        //Creating a scene object
        Scene scene = new Scene(container);

        //Setting title to the Stage
        stage.setTitle("Grid Pane Example");

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.show();
    }

    public static void printOEBR(){
        CustomPrintHelper print = new CustomPrintHelper("OEBR", 8.5, 3.3);
        print.prepareOEBR(new PaidBill());
        print.setOnFailed(e->{
            System.out.println("Error!");
        });
        print.setOnSucceeded(e->{
            System.out.println("Successful");
        });
        new Thread(print).start();
    }
}
