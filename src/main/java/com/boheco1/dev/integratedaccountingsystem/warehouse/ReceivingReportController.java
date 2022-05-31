package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReceivingReportController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private DatePicker to_dp, from_dp;

    @FXML
    private TableView reportsTable;

    @FXML
    private JFXTextField report_no;

    @FXML
    private ProgressBar progressBar;

    private ObservableList<Receiving> rreports;

    private int LIMIT = Utility.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.progressBar.setVisible(false);
    }

    @FXML
    public void reset(){
        this.rreports =  FXCollections.observableArrayList();
        this.reportsTable.setItems(this.rreports);
        this.reportsTable.setPlaceholder(new Label("No queries selected!"));

        this.report_no.setText("");
        this.from_dp.getEditor().clear();
        this.to_dp.getEditor().clear();
    }

    public void search(){
        LocalDate from = from_dp.getValue();
        LocalDate to = to_dp.getValue();

        String rrno = this.report_no.getText();

        if (rrno.length() == 0 && (from == null || to == null)){
            AlertDialogBuilder.messgeDialog("System Information", "Please enter receiving report number or select a valid start/end date!",
                    stackPane, AlertDialogBuilder.INFO_DIALOG);
        } else {
            Platform.runLater(() -> {
                try {
                    rreports = null;

                    if (rrno.length() != 0) {
                        rreports = FXCollections.observableList(ReceivingDAO.getByReportNo(rrno));
                    }else {
                        rreports = FXCollections.observableList(ReceivingDAO.getByDateRange(from, to));
                    }
                    this.reportsTable.getItems().setAll(rreports);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void generateReport() {
        search();
    }

    public void createTable(){
        TableColumn<Receiving, String> column1 = new TableColumn<>("Report Number");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("rrNo"));

        TableColumn<Receiving, String> column2 = new TableColumn<>("Date");
        column2.setMinWidth(120);
        column2.setCellValueFactory(stockStringCellDataFeatures -> new SimpleStringProperty(stockStringCellDataFeatures.getValue().getDate().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Receiving, String> column3 = new TableColumn<>("RV Number");
        column3.setMinWidth(150);
        column3.setCellValueFactory(new PropertyValueFactory<>("rvNo"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column4 = new TableColumn<>("BLWB Number");
        column4.setMinWidth(150);
        column4.setCellValueFactory(new PropertyValueFactory<>("blwbNo"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column5 = new TableColumn<>("Invoice Number");
        column5.setMinWidth(150);
        column5.setCellValueFactory(new PropertyValueFactory<>("invoiceNo"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Receiving, Receiving> column6 = new TableColumn<>("Action");
        column6.setMinWidth(50);
        column6.setCellValueFactory(data -> new ReadOnlyObjectWrapper<>(data.getValue()));
        column6.setCellFactory(table -> new TableCell<>() {
            FontIcon viewIcon = new FontIcon("mdi2e-eye");
            private final JFXButton viewButton = new JFXButton("", viewIcon);

            FontIcon printIcon = new FontIcon("mdi2p-printer");
            private final JFXButton printButton = new JFXButton("", printIcon);

            @Override
            public void updateItem(Receiving item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    viewButton.setStyle("-fx-background-color: #2196f3;");
                    viewIcon.setIconSize(13);
                    viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                    printButton.setStyle("-fx-background-color: #00AD8E;");
                    printIcon.setIconSize(13);
                    printIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    if (empty) {
                        setGraphic(null);
                        setText(null);
                    } else {
                        viewButton.setOnAction(actionEvent -> {
                            Utility.setSelectedReceiving(item);
                            ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_view_receiving_report.fxml", Utility.getStackPane(),  "../warehouse_receiving_report.fxml");
                        });

                        printButton.setOnAction(actionEvent -> {
                            saveReport(item.getRrNo());
                        });

                        HBox hBox = new HBox();
                        HBox filler = new HBox();
                        hBox.setHgrow(filler, Priority.ALWAYS);
                        hBox.setSpacing(5);
                        hBox.getChildren().add(viewButton);
                        hBox.getChildren().add(filler);
                        hBox.getChildren().add(printButton);

                        setGraphic(hBox);
                    }
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column6.setStyle("-fx-alignment: center;");

        this.reportsTable.getColumns().removeAll();

        this.reportsTable.getColumns().add(column1);
        this.reportsTable.getColumns().add(column2);
        this.reportsTable.getColumns().add(column3);
        this.reportsTable.getColumns().add(column4);
        this.reportsTable.getColumns().add(column5);
        this.reportsTable.getColumns().add(column6);
        this.reportsTable.setPlaceholder(new Label("No queries selected!"));
    }

    public void saveReport(String rrno){

        Stage stage = (Stage) stackPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("receiving_report_"+rrno+"_"+LocalDate.now()+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {

            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    showProgressBar(true);
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
                        org.apache.poi.ss.usermodel.Cell rrno_lbl_cell = rr_row.createCell(7);
                        rrno_lbl_cell.setCellValue("RR NO:");
                        rrno_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell rrno_cell = rr_row.createCell(8);
                        rrno_cell.setCellValue(receiving.getRrNo());
                        CellRangeAddress rrno_cell_addr = new CellRangeAddress(rr_head_row, rr_head_row, 8, 9);
                        sheet.addMergedRegion(rrno_cell_addr);
                        rrno_cell.setCellStyle(style);

                        int date_head_row = rr_head_row + 1;
                        Row date_row = sheet.createRow(date_head_row);
                        org.apache.poi.ss.usermodel.Cell date_lbl_cell = date_row.createCell(7);
                        date_lbl_cell.setCellValue("DATE:");
                        date_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell date_cell = date_row.createCell(8);
                        date_cell.setCellValue(receiving.getDate().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
                        CellRangeAddress date_cell_addr = new CellRangeAddress(date_head_row, date_head_row, 8, 9);
                        sheet.addMergedRegion(date_cell_addr);
                        date_cell.setCellStyle(style);

                        //RR Information Section
                        //Supplier
                        int info_head_row = date_head_row + 2;
                        Row info_row = sheet.createRow(info_head_row);
                        org.apache.poi.ss.usermodel.Cell supplier_lbl_cell = info_row.createCell(0);
                        supplier_lbl_cell.setCellValue("Supplier:");
                        CellRangeAddress supplier_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 0, 1);
                        sheet.addMergedRegion(supplier_lbl_cell_addr);
                        supplier_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell supplier_cell = info_row.createCell(2);
                        supplier_cell.setCellValue(supplier.getCompanyName());
                        CellRangeAddress supplier_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 2, 5);
                        sheet.addMergedRegion(supplier_cell_addr);
                        CellStyle style2 = doc.getWb().createCellStyle();
                        style2.setFont(font);
                        style2.setWrapText(true);
                        supplier_cell.setCellStyle(style2);

                        //BLWNO
                        org.apache.poi.ss.usermodel.Cell blno_lbl_cell = info_row.createCell(6);
                        blno_lbl_cell.setCellValue("B/L-W/B-AW/B-NO:");
                        CellRangeAddress blno_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 6, 7);
                        sheet.addMergedRegion(blno_lbl_cell_addr);
                        blno_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell blno_cell = info_row.createCell(8);
                        blno_cell.setCellValue(receiving.getBlwbNo());
                        CellRangeAddress blno_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 8, 9);
                        sheet.addMergedRegion(blno_cell_addr);
                        blno_cell.setCellStyle(style);

                        //Address
                        int addrress_head_row = info_head_row + 1;
                        Row addr_row = sheet.createRow(addrress_head_row);
                        org.apache.poi.ss.usermodel.Cell addr_lbl_cell = addr_row.createCell(0);
                        addr_lbl_cell.setCellValue("Address:");
                        CellRangeAddress addr_lbl_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 0, 1);
                        sheet.addMergedRegion(addr_lbl_cell_addr);
                        addr_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell addr_cell = addr_row.createCell(2);
                        addr_cell.setCellValue(supplier.getCompanyAddress());
                        CellRangeAddress addr_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 2, 5);
                        sheet.addMergedRegion(addr_cell_addr);
                        addr_cell.setCellStyle(style2);

                        //Carrier
                        org.apache.poi.ss.usermodel.Cell carrier_lbl_cell = addr_row.createCell(6);
                        carrier_lbl_cell.setCellValue("Carrier:");
                        CellRangeAddress carrier_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 6, 7);
                        sheet.addMergedRegion(carrier_cell_addr);
                        carrier_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell carrier_cell = addr_row.createCell(8);
                        carrier_cell.setCellValue(receiving.getCarrier());
                        CellRangeAddress carrier_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 8, 9);
                        sheet.addMergedRegion(carrier_addr);
                        carrier_cell.setCellStyle(style);

                        //Invoice No
                        int invoice_head_row = addrress_head_row + 1;
                        Row invoice_row = sheet.createRow(invoice_head_row);
                        org.apache.poi.ss.usermodel.Cell invoice_lbl_cell = invoice_row.createCell(0);
                        invoice_lbl_cell.setCellValue("Invoice No:");
                        CellRangeAddress invoice_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 0, 1);
                        sheet.addMergedRegion(invoice_lbl_cell_addr);
                        invoice_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell invoice_cell = invoice_row.createCell(2);
                        invoice_cell.setCellValue(receiving.getInvoiceNo());
                        CellRangeAddress invoice_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 2, 5);
                        sheet.addMergedRegion(invoice_cell_addr);
                        invoice_cell.setCellStyle(style);

                        //DR
                        org.apache.poi.ss.usermodel.Cell dr_lbl_cell = invoice_row.createCell(6);
                        dr_lbl_cell.setCellValue("D.R. No.:");
                        CellRangeAddress dr_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 6, 7);
                        sheet.addMergedRegion(dr_lbl_cell_addr);
                        dr_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell dr_cell = invoice_row.createCell(8);
                        dr_cell.setCellValue(receiving.getDrNo());
                        CellRangeAddress dr_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 8, 9);
                        sheet.addMergedRegion(dr_cell_addr);
                        dr_cell.setCellStyle(style);

                        //RV No
                        int rv_head_row = invoice_head_row + 1;
                        Row rv_row = sheet.createRow(rv_head_row);
                        org.apache.poi.ss.usermodel.Cell rv_lbl_cell = rv_row.createCell(0);
                        rv_lbl_cell.setCellValue("R.V. No:");
                        CellRangeAddress rv_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 0, 1);
                        sheet.addMergedRegion(rv_lbl_cell_addr);
                        rv_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell rv_cell = rv_row.createCell(2);
                        rv_cell.setCellValue(receiving.getRvNo());
                        CellRangeAddress rv_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 2, 5);
                        sheet.addMergedRegion(rv_cell_addr);
                        rv_cell.setCellStyle(style);

                        //PO No
                        org.apache.poi.ss.usermodel.Cell po_lbl_cell = rv_row.createCell(6);
                        po_lbl_cell.setCellValue("P.O. No.:");
                        CellRangeAddress po_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 6, 7);
                        sheet.addMergedRegion(po_lbl_cell_addr);
                        po_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell po_cell = rv_row.createCell(8);
                        po_cell.setCellValue(receiving.getPoNo());
                        CellRangeAddress po_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 8, 9);
                        sheet.addMergedRegion(po_cell_addr);
                        po_cell.setCellStyle(style);

                        //Table Header Section
                        int row = rv_head_row + 2;
                        Row table_header = sheet.createRow(row);

                        org.apache.poi.ss.usermodel.Cell code_cell = table_header.createCell(0);
                        code_cell.setCellValue("CODE NO");
                        doc.styleBorder(code_cell, 10, HorizontalAlignment.CENTER, false);

                        org.apache.poi.ss.usermodel.Cell desc_cell = table_header.createCell(1);
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

                        org.apache.poi.ss.usermodel.Cell unit_cell = table_header.createCell(5);
                        unit_cell.setCellValue("UNIT");
                        doc.styleBorder(unit_cell, 10, HorizontalAlignment.CENTER, false);

                        org.apache.poi.ss.usermodel.Cell qty_del_cell = table_header.createCell(6);
                        qty_del_cell.setCellValue("QTY\nDELIVERED");
                        doc.styleBorder(qty_del_cell, 10, HorizontalAlignment.CENTER, true);

                        org.apache.poi.ss.usermodel.Cell qty_cell = table_header.createCell(7);
                        qty_cell.setCellValue("QTY\nACCEPTED");
                        doc.styleBorder(qty_cell, 10, HorizontalAlignment.CENTER, true);

                        org.apache.poi.ss.usermodel.Cell cost_cell = table_header.createCell(8);
                        cost_cell.setCellValue("UNIT COST");
                        doc.styleBorder(cost_cell, 10, HorizontalAlignment.RIGHT, true);

                        org.apache.poi.ss.usermodel.Cell amount_cell = table_header.createCell(9);
                        amount_cell.setCellValue("AMOUNT");
                        doc.styleBorder(amount_cell, 10, HorizontalAlignment.CENTER, false);

                        //Table Body Section
                        List<Stock> stocks = ReceivingDAO.getReceivingItems(rrno);

                        int stocks_row = row + 1;
                        Row row_header = sheet.createRow(stocks_row);
                        org.apache.poi.ss.usermodel.Cell current_stock_code, current_desc, current_unit, current_delivered, current_accepted, current_cost, current_amount;
                        CellRangeAddress sdec_addr;

                        double net_sales = 0, total_amount = 0, vat_added = 0;

                        for (Stock stock : stocks) {
                            ReceivingItem item = stock.getReceivingItem();
                            row += 1;
                            row_header = sheet.createRow(row);

                            current_stock_code = row_header.createCell(0);
                            current_stock_code.setCellValue(stock.getId());
                            doc.styleBorder(current_stock_code, 10, HorizontalAlignment.LEFT, false);

                            current_desc = row_header.createCell(1);
                            current_desc.setCellValue(stock.getDescription());
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
                        org.apache.poi.ss.usermodel.Cell net_cell = sales_row.createCell(5);
                        net_cell.setCellValue("NET SALES");
                        CellRangeAddress net_cell_addr = new CellRangeAddress(sales, sales, 5, 8);
                        sheet.addMergedRegion(net_cell_addr);
                        doc.styleMergedCells(net_cell_addr);

                        org.apache.poi.ss.usermodel.Cell sales_cell = sales_row.createCell(9);
                        sales_cell.setCellValue(net_sales);
                        doc.styleBorder(sales_cell, 10, HorizontalAlignment.RIGHT, false);

                        //VAT
                        int vat = sales + 2;
                        Row vat_row = sheet.createRow(vat);
                        org.apache.poi.ss.usermodel.Cell vat_cell = vat_row.createCell(7);
                        vat_cell.setCellValue("Add Vat 12%");

                        org.apache.poi.ss.usermodel.Cell vat_amount_cell = vat_row.createCell(9);
                        if (supplier.getTaxType().equals("VAT"))
                            vat_added = net_sales * 0.12;
                        vat_amount_cell.setCellValue(vat_added);
                        doc.styleBorder(vat_amount_cell, 10, HorizontalAlignment.RIGHT, false);

                        //Total Amount
                        int total = vat + 2;
                        Row total_row = sheet.createRow(total);
                        org.apache.poi.ss.usermodel.Cell total_cell = total_row.createCell(5);
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
                this.showProgressBar(false);
                AlertDialogBuilder.messgeDialog("Receiving Report", "Receiving Report was successfully generated and saved on file!",
                        stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                try{
                    String path = selectedFile.getAbsolutePath();
                    Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+path);
                    p.waitFor();
                }catch (Exception e){
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });

            task.setOnFailed(workerStateEvent -> {
                this.showProgressBar(false);
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        }
    }

    public void showProgressBar(boolean show){
        this.progressBar.setVisible(show);
    }



}
