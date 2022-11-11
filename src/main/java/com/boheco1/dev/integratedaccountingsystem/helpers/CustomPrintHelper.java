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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class CustomPrintHelper extends Task {

    private Node node;
    private Paper paper;
    private PaidBill bill;

    public CustomPrintHelper(Node node, String page, double width, double height){
        this.node = node;
        this.paper = PrintHelper.createPaper(page, width, height, Units.INCH);
    }

    public CustomPrintHelper(String page, double width, double height, PaidBill bill){
        this.paper = PrintHelper.createPaper(page, width, height, Units.INCH);
        this.bill = bill;
    }

    @Override
    protected Object call() throws Exception {
        print();
        return null;
    }

    public void print() throws Exception{
        prepareDocument();

        Printer printer = Printer.getDefaultPrinter();
        System.out.println(printer.getName());
        PrinterJob job = PrinterJob.createPrinterJob();

        PageLayout layout = printer.createPageLayout(this.paper, PageOrientation.PORTRAIT,  10, 10, 0, 0);
        job.getJobSettings().setPageLayout(layout);
        Paper papers = layout.getPaper();

        System.out.println("Paper width: "+papers.getWidth());
        System.out.println("Paper height:" + papers.getHeight());
        System.out.println("Printable width: "+layout.getPrintableWidth());
        System.out.println("Printable height: "+layout.getPrintableHeight());
        System.out.println("Left margin: "+layout.getLeftMargin());
        System.out.println("Right margin: "+layout.getPrintableHeight());

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

    public void prepareDocument(){

       /*S String meter_no = "1231312";
        String type = "Type: ";
        String consumer = "Juan Dela Cruz";
        String billno = "1234567890";
        String address = "Clarin, Bohol";
        String billmonth = "November 2022";
        String kwhUsed = "123.5";
        String amount = "1500";
        String vat = "500";
        String due = "12/9/2022";
        String dueDate = "Due Date: "+due;
        String surcharge = "50";
        String amountDue = "1550";
        String teller = "engel";
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        String receivedDate = dateFormat2.format(new Date()).toString(); */

        String meter_no = bill.getConsumer().getAccountID();
        String type = "Type: "+bill.getConsumerType();
        String consumer = bill.getConsumer().getConsumerName();
        String billno = bill.getBillNo();
        String address = bill.getConsumer().getConsumerAddress();
        String billmonth = bill.getBillMonth();
        String kwhUsed = bill.getPowerKWH()+"";
        String amount = bill.getAmountDue()+"";
        String vat = (bill.getVat() + bill.getSurChargeTax())+"";
        String due = bill.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String dueDate = "Due Date: "+due;
        String surcharge = bill.getSurCharge()+"";
        String amountDue = bill.getTotalAmount()+"";
        String teller = bill.getTeller();
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        String receivedDate = dateFormat2.format(new Date()).toString();

        System.out.println("Meter: "+meter_no);
        System.out.println(type);
        System.out.println("Consumer: "+consumer);
        System.out.println("Address: "+address);
        System.out.println("Bill Number: "+billno);
        System.out.println("Billing Month: "+billmonth);
        System.out.println(dueDate);
        System.out.println("KWH Used: "+kwhUsed);
        System.out.println("Net Amount: "+amount);
        System.out.println("VAT: "+vat);
        System.out.println("Surcharge: "+surcharge);
        System.out.println("Amount Due: "+amountDue);
        System.out.println("Teller: "+teller);
        System.out.println("Date Received: "+receivedDate);
        System.out.println("Cash Amount: "+bill.getCashAmount());
        System.out.println("Check Amount: "+bill.getCheckAmount());
        System.out.println("=============================");
        System.out.println("");


        VBox container = new VBox();
        container.setPadding(new Insets(50, 0, 0, 0));

        double width = 580;
        double height = 216;

        container.setMinWidth(width);
        container.setMinHeight(height);
        container.setMaxWidth(width);
        container.setMaxHeight(height);
        container.setPrefWidth(width);
        container.setPrefHeight(height);

        HBox copyContainer = new HBox();

        VBox left = new VBox();
        VBox right = new VBox();

        left.setMinWidth(width/2);
        right.setMinWidth(width/2);
        left.setMaxWidth(width/2);
        right.setMaxWidth(width/2);
        left.setPrefWidth(width/2);
        right.setPrefWidth(width/2);

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

        teller_grid.setMinWidth(width/2);
        consumer_grid.setMinWidth(width/2);
        teller_grid.setMaxWidth(width/2);
        consumer_grid.setMaxWidth(width/2);
        teller_grid.setPrefWidth(width/2);
        consumer_grid.setPrefWidth(width/2);

        Label spacer1 = new Label(" ");
        spacer1.setStyle("-fx-font-size: 10px");
        Label spacer2 = new Label(" ");
        Label spacer3 = new Label(" ");
        Label spacer4 = new Label(" ");
        Label spacer5 = new Label(" ");
        Label spacer6 = new Label(" ");
        Label spacer8 = new Label(" ");
        spacer8.setStyle("-fx-font-size: 10px");
        Label spacer9 = new Label(" ");
        Label spacer10 = new Label(" ");
        Label spacer11 = new Label(" ");
        Label spacer12 = new Label(" ");
        Label spacer13 = new Label(" ");


        Label tel_meter_lbl = new Label(meter_no);
        Label tel_type_lbl = new Label(type);
        Label tel_consumer_lbl = new Label(consumer);
        Label tel_bill_lbl = new Label(billno);
        Label tel_addr_lbl = new Label(address);
        Label tel_billmonth_lbl = new Label(billmonth);
        Label tel_kwh_lbl = new Label(kwhUsed);
        Label tel_amount_lbl = new Label(amount);

        Label tel_surcharge = new Label("Surcharge");

        Label tel_vat_lbl = new Label(vat);
        Label tel_surcharge_lbl = new Label(surcharge);
        Label tel_duedate_lbl = new Label(dueDate);
        Label tel_amountDue_lbl = new Label(amountDue);
        Label tel_teller_lbl = new Label(teller);
        Label tel_receivedDate_lbl = new Label(receivedDate);

        //grid.add(Node, colIndex, rowIndex, colSpan, rowSpan):
        teller_grid.add(tel_meter_lbl,0,0,2,1);
        teller_grid.add(tel_type_lbl, 2, 0,1,1);

        teller_grid.add(tel_consumer_lbl, 0, 1, 2,1);
        teller_grid.add(tel_bill_lbl, 2, 1, 1,1);

        teller_grid.add(tel_addr_lbl, 0, 2, 3,1);

        teller_grid.add(spacer1, 0, 3, 3,1);
        //teller_grid.add(spacer2, 0, 4, 3,1);

        teller_grid.add(tel_billmonth_lbl, 0, 4, 1,1);
        teller_grid.add(tel_kwh_lbl, 1, 4, 1,1);
        teller_grid.add(tel_amount_lbl, 2, 4, 1,1);

        teller_grid.add(spacer3, 0, 5, 2,1);
        teller_grid.add(tel_vat_lbl, 2, 5, 1,1);

        teller_grid.add(spacer4, 0, 6, 3,1);

        teller_grid.add(spacer5, 0, 7, 1,1);
        teller_grid.add(tel_surcharge, 1, 7, 1,1);
        teller_grid.add(tel_surcharge_lbl, 2, 7, 1,1);

        teller_grid.add(tel_duedate_lbl, 0, 8, 2,1);
        teller_grid.add(tel_amountDue_lbl, 2, 8, 1,1);

        teller_grid.add(spacer6, 0, 9, 3,1);

        teller_grid.add(tel_teller_lbl, 0, 10, 2,1);
        teller_grid.add(tel_receivedDate_lbl, 2, 10, 1,1);

        Label con_meter_lbl = new Label(meter_no);
        Label con_type_lbl = new Label(type);
        Label con_consumer_lbl = new Label(consumer);
        Label con_bill_lbl = new Label(billno);
        Label con_addr_lbl = new Label(address);

        Label con_surcharge = new Label("Surcharge");

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

        consumer_grid.add(spacer8, 0, 3, 3,1);
        //consumer_grid.add(spacer9, 0, 4, 3 ,1);

        consumer_grid.add(con_billmonth_lbl, 0, 4, 1,1);
        consumer_grid.add(con_kwh_lbl, 1, 4, 1,1);
        consumer_grid.add(con_amount_lbl, 2, 4, 1,1);

        consumer_grid.add(spacer10, 0, 5, 2,1);
        consumer_grid.add(con_vat_lbl, 2, 5, 1,1);

        consumer_grid.add(spacer11, 0, 6, 3,1);

        consumer_grid.add(spacer12, 0, 7, 1,1);
        consumer_grid.add(con_surcharge, 1, 7, 1,1);
        consumer_grid.add(con_surcharge_lbl, 2, 7, 1,1);

        consumer_grid.add(con_duedate_lbl, 0, 8, 2,1);
        consumer_grid.add(con_amountDue_lbl, 2, 8, 1,1);

        consumer_grid.add(spacer13, 0, 9,3,1);

        consumer_grid.add(con_teller_lbl, 0, 10, 2,1);
        consumer_grid.add(con_receivedDate_lbl, 2, 10, 1,1);

        left.getChildren().add(teller_grid);
        right.getChildren().add(consumer_grid);

        //Add left and right containers to main node container
        copyContainer.getChildren().addAll(left, right);
        container.getChildren().add(copyContainer);
        container.setStyle("-fx-font-size: 9px");
        this.node = container;
    }
}
