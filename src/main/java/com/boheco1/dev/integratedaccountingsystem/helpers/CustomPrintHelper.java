package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class CustomPrintHelper extends Task {

    private Node node;
    private Paper paper;

    public CustomPrintHelper(Node node, String page, double width, double height){
        this.node = node;
        this.paper = PrintHelper.createPaper(page, width, height, Units.INCH);
    }

    public CustomPrintHelper(String page, double width, double height){
        this.paper = PrintHelper.createPaper(page, width, height, Units.INCH);
    }

    @Override
    protected Object call() throws Exception {
        print();
        return null;
    }

    public void print() throws Exception{
        //Printer printer = Printer.getDefaultPrinter();

        PrinterJob job = PrinterJob.createPrinterJob();
        Printer printer = job.getPrinter();
        PageLayout layout = printer.createPageLayout(paper, PageOrientation.PORTRAIT, Printer.MarginType.EQUAL);

        JobSettings jobSettings = job.getJobSettings();
        jobSettings.setPageLayout(layout);

        if (job != null) {
            boolean printed = job.printPage(node);
            if (printed) {
                job.endJob();
            } else {
                throw new Exception("Printer error! Printing failed!");
            }
        } else {
            throw new Exception("Printer error! Could not create a printer job!");
        }
    }

    public void prepareOEBR(PaidBill bill){

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
        container.setStyle("-fx-font-size: 9px");
        this.node = container;
    }
}
