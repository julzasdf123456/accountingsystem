package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewReceivingReportController extends MenuControllerHandler implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;


    @FXML
    private JFXTextField supplier_tf, bno_tf, addr_tf, carrier_tf, invoice_tf, dr_tf, rv_tf, po_tf,
            received_tf, received_original_tf, verified_tf, posted_tf;

    @FXML
    private TextField net_sales_tf, vat_tf, total_tf;

    @FXML
    private TableView items_table;

    private JFXDialog dialog;

    private Stock currentStock = null;
    private ObservableList<Stock> receivedItems = null;

    private SupplierInfo supplier = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
    }

    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Code");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("localCode"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column2 = new TableColumn<>("Stock");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("stockName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(275);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column4 = new TableColumn<>("Unit");
        column4.setMinWidth(50);
        column4.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column5 = new TableColumn<>("Delivered");
        column5.setMinWidth(80);
        column5.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyDelivered()+""));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column6 = new TableColumn<>("Accepted");
        column6.setMinWidth(80);
        column6.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted()+""));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(100);
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getUnitCost()+""));
        column7.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column8 = new TableColumn<>("Amount");
        column8.setMinWidth(100);
        column8.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted() * stocks.getValue().getReceivingItem().getUnitCost() +""));
        column8.setStyle("-fx-alignment: center-left;");

        this.receivedItems =  FXCollections.observableArrayList();
        this.items_table.setPlaceholder(new Label("No item added"));

        this.items_table.getColumns().add(column1);
        this.items_table.getColumns().add(column2);
        this.items_table.getColumns().add(column3);
        this.items_table.getColumns().add(column4);
        this.items_table.getColumns().add(column5);
        this.items_table.getColumns().add(column6);
        this.items_table.getColumns().add(column7);
        this.items_table.getColumns().add(column8);
    }

    public void credit(double tax, SupplierInfo supplier, double amount){
        double vat = 0, total = 0;
        if (supplier.getTaxType() != null && supplier.getTaxType().equals("VAT"))
                vat =  amount * (tax/100);
        total += vat + amount;

        this.net_sales_tf.setText(amount+"");
        this.vat_tf.setText(vat+"");
        this.total_tf.setText(total+"");
    }

    public void setReceiving(Receiving report){
        try {
            SupplierInfo supplier = SupplierDAO.get(report.getSupplierId());
            this.supplier_tf.setText(supplier.getCompanyName());
            this.addr_tf.setText(supplier.getCompanyAddress());
            this.bno_tf.setText(report.getBlwbNo());
            this.carrier_tf.setText(report.getCarrier());
            this.invoice_tf.setText(report.getInvoiceNo());
            this.dr_tf.setText(report.getDrNo());
            this.rv_tf.setText(report.getRvNo());
            this.po_tf.setText(report.getPoNo());
            User receiver = UserDAO.get(report.getReceivedBy());
            User receiverOrig = UserDAO.get(report.getReceivedOrigBy());
            User verifier = UserDAO.get(report.getVerifiedBy());
            this.received_tf.setText(receiver.getFullName());
            this.received_original_tf.setText(receiverOrig.getFullName());
            this.verified_tf.setText(verifier.getFullName());
            List<Stock> items = ReceivingDAO.getReceivingItems(report.getRrNo());
            this.receivedItems =  FXCollections.observableArrayList(items);
            this.items_table.getItems().setAll(receivedItems);

            double amount = 0;
            for (int i = 0; i < this.receivedItems.size(); i++) {
                ReceivingItem receivingItem = this.receivedItems.get(i).getReceivingItem();
                amount += receivingItem.getQtyAccepted() * receivingItem.getUnitCost();
            }
            this.credit(12, supplier, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveReport(String rrno){

        Stage stage = (Stage) contentPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("receiving_report_"+LocalDate.now()+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {

            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (OutputStream fileOut = new FileOutputStream(selectedFile)) {
                        Receiving receiving = ReceivingDAO.get(rrno);

                        String rrno = receiving.getRrNo();

                        SupplierInfo supplier = SupplierDAO.get(receiving.getSupplierId());

                        ExcelBuilder doc = new ExcelBuilder(10);

                        CellStyle style = doc.getWb().createCellStyle();
                        org.apache.poi.ss.usermodel.Font font = doc.getWb().createFont();
                        font.setFontHeightInPoints((short) 10);
                        font.setFontName("Arial");
                        style.setFont(font);

                        doc.setTitle("Receiving Report");
                        User received_by = UserDAO.get(receiving.getReceivedBy());
                        User received_orig_by = UserDAO.get(receiving.getReceivedOrigBy());
                        User verified_by = UserDAO.get(receiving.getVerifiedBy());
                        String names[] = {
                                received_by.getEmployeeInfo().getFullName(),
                                received_orig_by.getEmployeeInfo().getFullName(),
                                verified_by.getEmployeeInfo().getFullName(),
                                " "};
                        doc.setNames(names);

                        String designations[] = {
                                received_by.getEmployeeInfo().getDesignation(),
                                received_orig_by.getEmployeeInfo().getDesignation(),
                                verified_by.getEmployeeInfo().getDesignation(),
                                "Stock Clerk"};
                        doc.setDesignations(designations);

                        String types[] = {"Received by", "Received Original by", "Verified by", "Posted to Bin Card by"};
                        doc.setTypes(types);

                        doc.setMargin(1, 0.5, 1, 0.5);

                        doc.createHeader();
                        doc.createTitle(5, "", true);

                        Sheet sheet = doc.getSheet();

                        //RR NO and Date Section
                        int rr_head_row = 7;
                        Row rr_row = sheet.createRow(rr_head_row);
                        Cell rrno_lbl_cell = rr_row.createCell(7);
                        rrno_lbl_cell.setCellValue("RR NO:");
                        rrno_lbl_cell.setCellStyle(style);

                        Cell rrno_cell = rr_row.createCell(8);
                        rrno_cell.setCellValue(receiving.getRrNo());
                        CellRangeAddress rrno_cell_addr = new CellRangeAddress(rr_head_row, rr_head_row, 8, 9);
                        sheet.addMergedRegion(rrno_cell_addr);
                        rrno_cell.setCellStyle(style);

                        int date_head_row = rr_head_row + 1;
                        Row date_row = sheet.createRow(date_head_row);
                        Cell date_lbl_cell = date_row.createCell(7);
                        date_lbl_cell.setCellValue("DATE:");
                        date_lbl_cell.setCellStyle(style);

                        Cell date_cell = date_row.createCell(8);
                        date_cell.setCellValue(receiving.getDate().format(DateTimeFormatter.ofPattern("MMM. dd, yyyy")));
                        CellRangeAddress date_cell_addr = new CellRangeAddress(date_head_row, date_head_row, 8, 9);
                        sheet.addMergedRegion(date_cell_addr);
                        date_cell.setCellStyle(style);

                        //RR Information Section
                        //Supplier
                        int info_head_row = date_head_row + 2;
                        Row info_row = sheet.createRow(info_head_row);
                        Cell supplier_lbl_cell = info_row.createCell(0);
                        supplier_lbl_cell.setCellValue("Supplier:");
                        CellRangeAddress supplier_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 0, 1);
                        sheet.addMergedRegion(supplier_lbl_cell_addr);
                        supplier_lbl_cell.setCellStyle(style);

                        Cell supplier_cell = info_row.createCell(2);
                        supplier_cell.setCellValue(supplier.getCompanyName());
                        CellRangeAddress supplier_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 2, 5);
                        sheet.addMergedRegion(supplier_cell_addr);
                        CellStyle style2 = doc.getWb().createCellStyle();
                        style2.setFont(font);
                        style2.setWrapText(true);
                        supplier_cell.setCellStyle(style2);

                        //BLWNO
                        Cell blno_lbl_cell = info_row.createCell(6);
                        blno_lbl_cell.setCellValue("B/L-W/B-AW/B-NO:");
                        CellRangeAddress blno_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 6, 7);
                        sheet.addMergedRegion(blno_lbl_cell_addr);
                        blno_lbl_cell.setCellStyle(style);

                        Cell blno_cell = info_row.createCell(8);
                        blno_cell.setCellValue(receiving.getBlwbNo());
                        CellRangeAddress blno_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 8, 9);
                        sheet.addMergedRegion(blno_cell_addr);
                        blno_cell.setCellStyle(style);

                        //Address
                        int addrress_head_row = info_head_row + 1;
                        Row addr_row = sheet.createRow(addrress_head_row);
                        Cell addr_lbl_cell = addr_row.createCell(0);
                        addr_lbl_cell.setCellValue("Address:");
                        CellRangeAddress addr_lbl_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 0, 1);
                        sheet.addMergedRegion(addr_lbl_cell_addr);
                        addr_lbl_cell.setCellStyle(style);

                        Cell addr_cell = addr_row.createCell(2);
                        addr_cell.setCellValue(supplier.getCompanyAddress());
                        CellRangeAddress addr_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 2, 5);
                        sheet.addMergedRegion(addr_cell_addr);
                        addr_cell.setCellStyle(style2);

                        //Carrier
                        Cell carrier_lbl_cell = addr_row.createCell(6);
                        carrier_lbl_cell.setCellValue("Carrier:");
                        CellRangeAddress carrier_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 6, 7);
                        sheet.addMergedRegion(carrier_cell_addr);
                        carrier_lbl_cell.setCellStyle(style);

                        Cell carrier_cell = addr_row.createCell(8);
                        carrier_cell.setCellValue(receiving.getCarrier());
                        CellRangeAddress carrier_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 8, 9);
                        sheet.addMergedRegion(carrier_addr);
                        carrier_cell.setCellStyle(style);

                        //Invoice No
                        int invoice_head_row = addrress_head_row + 1;
                        Row invoice_row = sheet.createRow(invoice_head_row);
                        Cell invoice_lbl_cell = invoice_row.createCell(0);
                        invoice_lbl_cell.setCellValue("Invoice No:");
                        CellRangeAddress invoice_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 0, 1);
                        sheet.addMergedRegion(invoice_lbl_cell_addr);
                        invoice_lbl_cell.setCellStyle(style);

                        Cell invoice_cell = invoice_row.createCell(2);
                        invoice_cell.setCellValue(receiving.getInvoiceNo());
                        CellRangeAddress invoice_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 2, 5);
                        sheet.addMergedRegion(invoice_cell_addr);
                        invoice_cell.setCellStyle(style);

                        //DR
                        Cell dr_lbl_cell = invoice_row.createCell(6);
                        dr_lbl_cell.setCellValue("D.R. No.:");
                        CellRangeAddress dr_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 6, 7);
                        sheet.addMergedRegion(dr_lbl_cell_addr);
                        dr_lbl_cell.setCellStyle(style);

                        Cell dr_cell = invoice_row.createCell(8);
                        dr_cell.setCellValue(receiving.getDrNo());
                        CellRangeAddress dr_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 8, 9);
                        sheet.addMergedRegion(dr_cell_addr);
                        dr_cell.setCellStyle(style);

                        //RV No
                        int rv_head_row = invoice_head_row + 1;
                        Row rv_row = sheet.createRow(rv_head_row);
                        Cell rv_lbl_cell = rv_row.createCell(0);
                        rv_lbl_cell.setCellValue("R.V. No:");
                        CellRangeAddress rv_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 0, 1);
                        sheet.addMergedRegion(rv_lbl_cell_addr);
                        rv_lbl_cell.setCellStyle(style);

                        Cell rv_cell = rv_row.createCell(2);
                        rv_cell.setCellValue(receiving.getRvNo());
                        CellRangeAddress rv_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 2, 5);
                        sheet.addMergedRegion(rv_cell_addr);
                        rv_cell.setCellStyle(style);

                        //PO No
                        Cell po_lbl_cell = rv_row.createCell(6);
                        po_lbl_cell.setCellValue("P.O. No.:");
                        CellRangeAddress po_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 6, 7);
                        sheet.addMergedRegion(po_lbl_cell_addr);
                        po_lbl_cell.setCellStyle(style);

                        Cell po_cell = rv_row.createCell(8);
                        po_cell.setCellValue(receiving.getPoNo());
                        CellRangeAddress po_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 8, 9);
                        sheet.addMergedRegion(po_cell_addr);
                        po_cell.setCellStyle(style);

                        //Table Header Section
                        int row = rv_head_row + 2;
                        Row table_header = sheet.createRow(row);

                        Cell code_cell = table_header.createCell(0);
                        code_cell.setCellValue("CODE NO");
                        doc.styleBorder(code_cell, 10, HorizontalAlignment.CENTER, false);

                        Cell desc_cell = table_header.createCell(1);
                        desc_cell.setCellValue("ARTICLE");
                        CellRangeAddress desc_cell_addr = new CellRangeAddress(row, row, 1, 4);
                        sheet.addMergedRegion(desc_cell_addr);
                        org.apache.poi.ss.usermodel.Font desc_font = doc.getWb().createFont();
                        desc_font.setFontHeightInPoints((short) 10);
                        desc_font.setFontName("Arial");
                        CellStyle desc_style = doc.getWb().createCellStyle();
                        desc_style.setFont(desc_font);
                        desc_style.setAlignment(HorizontalAlignment.CENTER);
                        desc_cell.setCellStyle(desc_style);
                        doc.styleMergedCells(desc_cell_addr);

                        Cell unit_cell = table_header.createCell(5);
                        unit_cell.setCellValue("UNIT");
                        doc.styleBorder(unit_cell, 10, HorizontalAlignment.CENTER, false);

                        Cell qty_del_cell = table_header.createCell(6);
                        qty_del_cell.setCellValue("QTY\nDELIVERED");
                        doc.styleBorder(qty_del_cell, 10, HorizontalAlignment.CENTER, true);

                        Cell qty_cell = table_header.createCell(7);
                        qty_cell.setCellValue("QTY\nACCEPTED");
                        doc.styleBorder(qty_cell, 10, HorizontalAlignment.CENTER, true);

                        Cell cost_cell = table_header.createCell(8);
                        cost_cell.setCellValue("UNIT COST");
                        doc.styleBorder(cost_cell, 10, HorizontalAlignment.RIGHT, true);

                        Cell amount_cell = table_header.createCell(9);
                        amount_cell.setCellValue("AMOUNT");
                        doc.styleBorder(amount_cell, 10, HorizontalAlignment.CENTER, false);

                        //Table Body Section
                        List<Stock> stocks = ReceivingDAO.getReceivingItems(rrno);

                        int stocks_row = row + 1;
                        Row row_header = sheet.createRow(stocks_row);
                        Cell current_stock_code, current_desc, current_unit, current_delivered, current_accepted, current_cost, current_amount;
                        CellRangeAddress sdec_addr;

                        double net_sales = 0, total_amount = 0, vat_added = 0;

                        for (Stock stock : stocks) {
                            ReceivingItem item = stock.getReceivingItem();
                            row += 1;
                            row_header = sheet.createRow(row);

                            //LocalCode
                            current_stock_code = row_header.createCell(0);
                            current_stock_code.setCellValue(stock.getLocalCode());
                            doc.styleBorder(current_stock_code, 10, HorizontalAlignment.LEFT, false);

                            current_desc = row_header.createCell(1);
                            current_desc.setCellValue(stock.getStockName() + " " + stock.getDescription());
                            sdec_addr = new CellRangeAddress(row, row, 1, 4);
                            sheet.addMergedRegion(sdec_addr);
                            current_desc.setCellStyle(style);
                            doc.styleMergedCells(sdec_addr);

                            current_unit = row_header.createCell(5);
                            current_unit.setCellValue(stock.getUnit());
                            doc.styleBorder(current_unit, 10, HorizontalAlignment.CENTER, false);

                            current_delivered = row_header.createCell(6);
                            current_delivered.setCellValue(item.getQtyDelivered());
                            doc.styleBorder(current_delivered, 10, HorizontalAlignment.CENTER, false);

                            current_accepted = row_header.createCell(7);
                            current_accepted.setCellValue(item.getQtyAccepted());
                            doc.styleBorder(current_accepted, 10, HorizontalAlignment.CENTER, false);

                            current_cost = row_header.createCell(8);
                            current_cost.setCellValue(item.getUnitCost());
                            doc.styleBorder(current_cost, 10, HorizontalAlignment.RIGHT, false);

                            current_amount = row_header.createCell(9);
                            double amount = item.getUnitCost() * item.getQtyAccepted();
                            current_amount.setCellValue(amount);
                            doc.styleBorder(current_amount, 10, HorizontalAlignment.RIGHT, false);

                            net_sales += amount;
                        }

                        //Net Sales Section
                        int list_size = row + stocks.size();

                        //Net Sales
                        int sales = list_size;
                        Row sales_row = sheet.createRow(sales);
                        Cell net_cell = sales_row.createCell(5);
                        net_cell.setCellValue("NET SALES");
                        CellRangeAddress net_cell_addr = new CellRangeAddress(sales, sales, 5, 8);
                        sheet.addMergedRegion(net_cell_addr);
                        doc.styleMergedCells(net_cell_addr);

                        Cell sales_cell = sales_row.createCell(9);
                        sales_cell.setCellValue(net_sales);
                        doc.styleBorder(sales_cell, 10, HorizontalAlignment.RIGHT, false);

                        //VAT
                        int vat = sales + 2;
                        Row vat_row = sheet.createRow(vat);
                        Cell vat_cell = vat_row.createCell(7);
                        vat_cell.setCellValue("Add Vat 12%");

                        Cell vat_amount_cell = vat_row.createCell(9);
                        if (supplier.getTaxType().equals("VAT"))
                            vat_added = net_sales * 0.12;
                        vat_amount_cell.setCellValue(vat_added);
                        doc.styleBorder(vat_amount_cell, 10, HorizontalAlignment.RIGHT, false);

                        //Total Amount
                        int total = vat + 2;
                        Row total_row = sheet.createRow(total);
                        Cell total_cell = total_row.createCell(5);
                        total_cell.setCellValue("TOTAL AMOUNT");
                        CellRangeAddress total_cell_addr = new CellRangeAddress(total, total, 5, 8);
                        sheet.addMergedRegion(total_cell_addr);
                        doc.styleMergedCells(total_cell_addr);

                        Cell total_amount_cell = total_row.createCell(9);
                        total_amount_cell.setCellValue(net_sales + vat_added);
                        doc.styleBorder(total_amount_cell, 10, HorizontalAlignment.RIGHT, false);

                        //Signatorees Section
                        int sign_row = sales + 7;
                        doc.createSignatorees(sign_row);

                        //Save file
                        doc.save(fileOut);
                    } catch (Exception e) {
                        AlertDialogBuilder.messgeDialog("System Warning", "Process failed due to: " + e.getMessage(),
                                stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    }
                    return null;
                }
            };

            task.setOnSucceeded(wse -> {
                //this.showProgressBar(false);
                AlertDialogBuilder.messgeDialog("Receiving Report", "Receiving Report was successfully entered, generated and saved on file!",
                        stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
            });

            task.setOnFailed(workerStateEvent -> {
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        }
    }
}
