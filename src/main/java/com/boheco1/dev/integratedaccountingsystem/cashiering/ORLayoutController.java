package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;

import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;



public class ORLayoutController implements Initializable, ObjectTransaction {
    @FXML
    private Pane printArea;
    private Node node;

    @FXML
    private Label cusName1, cusName2, address1, address2, cashier1, cashier2, date1, date2, inWord1, inWord2, orNum1, orNum2, ref1, ref2, desc1, desc2, amount1, amount2, totalAmount1, totalAmount2, totalAmount3, totalAmount4;

    private ORContent orContent = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Anchor pane Size for OR
        //Width 581
        //height 380

        String pattern = "M/d/yyyy";
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
        orContent = Utility.getOrContent();
        //HashMap<String, List<ItemSummary>> breakdown = tellerInfo.getDCRBreakDown();
        //List<ItemSummary> result = breakdown.get("Breakdown");

        //List<ItemSummary> misc = breakdown.get("Misc");
        //double collectionFromTeller = Double.parseDouble(String.format("%.2f",misc.get(1).getTotal()));


        String description = "", amount = "";
        if(orContent.getTellerCollection()!=null){
            for (ItemSummary a : orContent.getTellerCollection()) {
                if (a.getTotal() > 0) {
                    description += a.getDescription() + "\n";
                    amount += Utility.formatDecimal(a.getTotal()) + "\n";
                }
            }
        }else if(orContent.getCustomerCollection() != null) {
            for (CRMDetails a : orContent.getCustomerCollection()) {
                if (a.getTotal() > 0) {
                    description += a.getParticulars() + "\n";
                    amount += Utility.formatDecimal(a.getTotal()) + "\n";
                }
            }
        }
        date1.setText(orContent.getDate().format(dateFormatter));
        date2.setText(orContent.getDate().format(dateFormatter));
        cusName1.setText(orContent.getIssuedTo());
        cusName2.setText(orContent.getIssuedTo());
        address1.setText(orContent.getAddress());
        address2.setText(orContent.getAddress());
        orNum1.setText(orContent.getOrNumber());
        orNum2.setText(orContent.getOrNumber());
        inWord1.setText(Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£",""));
        inWord2.setText(Utility.doubleAmountToWords(orContent.getTotal()).replaceAll("£",""));
        totalAmount1.setText(Utility.formatDecimal(orContent.getTotal()));
        totalAmount2.setText(Utility.formatDecimal(orContent.getTotal()));
        totalAmount3.setText(Utility.formatDecimal(orContent.getTotal()));
        totalAmount4.setText(Utility.formatDecimal(orContent.getTotal()));
        ref1.setText("");
        ref2.setText("");
        desc1.setText(description);
        desc2.setText(description);
        amount1.setText(amount);
        amount2.setText(amount);

        try {
            EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
            //cashier1.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            //cashier2.setText(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            cashier1.setText(orContent.getIssuedBy());
            cashier2.setText(orContent.getIssuedBy());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Node node = printArea;
        Paper paper = PrintHelper.createPaper("OR", 18, 6, Units.INCH);
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(paper,
                PageOrientation.PORTRAIT, 10, 10, 0, 0);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(pageLayout, node);
            if (success) {
                System.out.println("PRINTING FINISHED");
                job.endJob();
            }
        }
    }

    private void print() throws Exception {
        //Anchor pane Size for OR
        //Width 581
        //height 380
       //Node node = printArea;
        /*Paper paper;
        Printer printer = Printer.getDefaultPrinter();
        PageLayout pageLayout = printer.createPageLayout(Paper.A4,
                PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean success = job.printPage(pageLayout, node);
            if (success) {
                System.out.println("PRINTING FINISHED");
                job.endJob();
            }
        }*/

        // paper = PrintHelper.createPaper("OR", 18, 3, Units.INCH);
        Printer printer = Printer.getDefaultPrinter();
        System.out.println(printer.getName());
        PrinterJob job = PrinterJob.createPrinterJob();

        PageLayout layout = printer.createPageLayout(Paper.A4, PageOrientation.PORTRAIT,  10, 10, 0, 0);
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


        String meter_no = "12345677";
        String type = "Type: RM";
        String consumer = "Juan Dela Cruz";
        String billno = "02-023- 2323";
        String address = "Tubigon Bohol";
        String billmonth = "November 2022";
        String kwhUsed = "158";
        String amount = "1500";
        String vat = "300";
        String due = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String dueDate = "Due Date: "+due;
        String surcharge = "50";
        String amountDue = "600";
        String teller = "engel";
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy hh:mm aa");
        //String receivedDate = dateFormat2.format(new Date()).toString();

       /* String meter_no = bill.getConsumer().getAccountID();
        String type = "Type: "+bill.getConsumerType();
        String consumer = bill.getConsumer().getConsumerName();
        String billno = bill.getBillNo();
        String address = bill.getConsumer().getConsumerAddress();
        String billmonth = bill.getBillMonth();
        String kwhUsed = bill.getPowerKWH()+"";
        double surVat = bill.getVat() + bill.getSurChargeTax();
        String vat = Utility.formatDecimal(surVat);
        String due = bill.getDueDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        String dueDate = "Due Date: "+due;
        String surcharge = Utility.formatDecimal(bill.getSurCharge());
        String amountDue = Utility.formatDecimal(bill.getTotalAmount());
        //Bill amount is total amount less the sum of vat, surcharge and surchargeVat
        String amount = Utility.formatDecimal(bill.getTotalAmount() - bill.getSurCharge() - surVat);
        String teller = bill.getTeller();
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yy hh:mm aa");*/
        LocalDate serverDate = LocalDate.now();
        try {
            serverDate = Utility.serverDate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String receivedDate = dateFormat2.format(java.sql.Date.valueOf(serverDate)).toString();

        /*System.out.println("Meter: "+meter_no);
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
        System.out.println("");*/


        VBox container = new VBox();
        container.setPadding(new Insets(38, 0, 0, 0));

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
        spacer1.setStyle("-fx-font-size: 12px");
        //Label spacer2 = new Label(" ");
        Label spacer3 = new Label(" ");
        Label spacer4 = new Label(" ");
        spacer4.setStyle("-fx-font-size: 5px");
        Label spacer5 = new Label(" ");
        Label spacer6 = new Label(" ");
        spacer6.setStyle("-fx-font-size: 10px");
        Label spacer8 = new Label(" ");
        spacer8.setStyle("-fx-font-size: 12px");
        //Label spacer9 = new Label(" ");
        Label spacer10 = new Label(" ");
        Label spacer11 = new Label(" ");
        spacer11.setStyle("-fx-font-size: 5px");
        Label spacer12 = new Label(" ");
        Label spacer13 = new Label(" ");
        spacer13.setStyle("-fx-font-size: 10px");

        Label tel_meter_lbl = new Label(meter_no);
        Label tel_type_lbl = new Label(type);
        Label tel_consumer_lbl = new Label(consumer);
        Label tel_bill_lbl = new Label(billno);
        Label tel_addr_lbl = new Label(address);
        Label tel_billmonth_lbl = new Label(billmonth);
        Label tel_kwh_lbl = new Label(kwhUsed);
        Label tel_amount_lbl = new Label(amount);

        Label tel_surcharge = new Label("             Surcharge");

        Label tel_vat_lbl = new Label(vat);
        Label tel_surcharge_lbl = new Label(surcharge);
        Label tel_duedate_lbl = new Label(dueDate);
        Label tel_amountDue_lbl = new Label(amountDue);
        Label tel_teller_lbl = new Label("                     "+teller);
        tel_addr_lbl.setTextAlignment(TextAlignment.CENTER);
        Label tel_receivedDate_lbl = new Label("  "+receivedDate.toUpperCase(Locale.ROOT));

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

        Label con_surcharge = new Label("                   Surcharge");

        Label con_billmonth_lbl = new Label(billmonth);
        Label con_kwh_lbl = new Label(kwhUsed);
        Label con_amount_lbl = new Label(amount);
        Label con_vat_lbl = new Label(vat);
        Label con_surcharge_lbl = new Label(surcharge);
        Label con_duedate_lbl = new Label(dueDate);
        Label con_amountDue_lbl = new Label(amountDue);
        Label con_teller_lbl = new Label("                     "+teller);
        con_teller_lbl.setTextAlignment(TextAlignment.CENTER);
        Label con_receivedDate_lbl = new Label("  "+receivedDate.toUpperCase(Locale.ROOT));


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

    @Override
    public void receive(Object o) {

    }
}
