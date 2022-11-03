package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ExcelBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class DCRController extends MenuControllerHandler implements Initializable {

    @FXML
    private ProgressBar progressbar;

    @FXML
    private DatePicker date_pker;

    @FXML
    private JFXButton view_btn;

    @FXML
    private JFXButton print_dcr_btn;

    @FXML
    private JFXTextField teller_tf;

    @FXML
    private TableView<ItemSummary> dcr_breakdown_table;

    @FXML
    private TableView<ItemSummary> payments_table;

    @FXML
    private TableView<Bill> dcr_power_table;

    @FXML
    private Label dcr_total_kwh, dcr_total, dcr_transNo_lbl;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<ItemSummary> dcrItems = FXCollections.observableArrayList();
    private ObservableList<ItemSummary> dcrPayments = FXCollections.observableArrayList();
    private double totalKwh = 0, grandTotal = 0, billTotal = 0, amountDue = 0, cashAmount = 0, checkAmount = 0;

    private String teller = ActiveUser.getUser().getUserName();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.date_pker.setValue(LocalDate.now());
        this.createDCRTransactionsTable();
        this.createDCRSummary();
        this.createDCRBreakDown();
        this.view_btn.setOnAction(action ->{
            this.generateReport();
        });

        if (ActiveUser.getUser().can("manage-cashiering")) {
            this.teller_tf.setVisible(true);
            this.teller_tf.setOnAction(actionEvent -> {
                this.generateReport();
            });
        }else {
            this.teller_tf.setVisible(false);
        }

        this.print_dcr_btn.setOnAction(actionEvent -> {
            this.generateExcel();
        });
    }
    /**
     * Generates DCR in excel format
     * @return void
     */
    public void generateExcel(){
        Stage stage = (Stage) Utility.getStackPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("DCR_"+LocalDate.now()+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);

        if (selectedFile != null) {

            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    progressbar.setVisible(true);
                    try (OutputStream fileOut = new FileOutputStream(selectedFile)) {
                        boolean proceed = true;
                        String teller;
                        if (ActiveUser.getUser().can("manage-cashiering")) {
                            teller = teller_tf.getText();
                            if (teller.isEmpty()) {
                                AlertDialogBuilder.messgeDialog("System Information", "Teller username is required! Please specify the teller!",
                                        Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                                proceed = false;
                            }
                        }else if (ActiveUser.getUser().can("manage-tellering")) {
                            teller = ActiveUser.getUser().getUserName();
                        }else {
                            proceed = false;
                        }
                        if (proceed) {
                            int month = date_pker.getValue().getMonthValue();
                            int day = date_pker.getValue().getDayOfMonth();
                            int year = date_pker.getValue().getYear();
                            DCRController.generateDCR(fileOut, "Engel", "10/3/22",
                                    BillDAO.getAllPaidBills(year, month, day, "engel"),
                                    BillDAO.getDCRBreakDown(year, month, day, "engel"));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialogBuilder.messgeDialog("System Warning", "Process failed due to: " + e.getMessage(),
                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }
                    return null;
                }
            };

            task.setOnSucceeded(wse -> {
                progressbar.setVisible(false);
                AlertDialogBuilder.messgeDialog("Daily Collection Report", "Daily Collection Report was successfully generated and saved on file!",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                try{
                    String path = selectedFile.getAbsolutePath();
                    Process p = Runtime.getRuntime().exec("rundll32 url.dll,FileProtocolHandler "+path);
                    p.waitFor();
                }catch (Exception e){
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });

            task.setOnFailed(workerStateEvent -> {
                progressbar.setVisible(false);
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        }
    }

    /**
     * Generates and displays DCR on the tables
     * @return void
     */
    public void generateReport() {
        boolean proceed = true;
        if (ActiveUser.getUser().can("manage-cashiering")) {
            teller = teller_tf.getText();
            if (teller.isEmpty()) {
                AlertDialogBuilder.messgeDialog("System Information", "Teller username is required! Please specify the teller!",
                        Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
                proceed = false;
            }
        }
        if (proceed) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    int day = 0, year = 0, month = 0;

                    totalKwh = 0;
                    billTotal = 0;
                    amountDue = 0;
                    grandTotal = 0;
                    cashAmount = 0;
                    checkAmount = 0;

                    try {
                        month = date_pker.getValue().getMonthValue();
                        day = date_pker.getValue().getDayOfMonth();
                        year = date_pker.getValue().getYear();

                        bills = FXCollections.observableArrayList(BillDAO.getAllPaidBills(year, month, day, teller));
                        HashMap<String, List<ItemSummary>> breakdown = BillDAO.getDCRBreakDown(year, month, day, teller);
                        dcrItems = FXCollections.observableArrayList(breakdown.get("Breakdown"));
                        dcrPayments = FXCollections.observableArrayList(breakdown.get("Payments"));

                        List<ItemSummary> misc = breakdown.get("Misc");
                        totalKwh = misc.get(0).getTotal();
                        grandTotal = misc.get(1).getTotal();
                        billTotal = misc.get(2).getTotal();
                        amountDue = misc.get(3).getTotal();
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request: " + e.getMessage(),
                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                bills = FXCollections.observableArrayList();
                dcrItems = FXCollections.observableArrayList();
                dcrPayments = FXCollections.observableArrayList();
                dcr_power_table.setItems(bills);
                dcr_breakdown_table.setItems(dcrItems);
                payments_table.setItems(dcrPayments);
                view_btn.setDisable(true);
                progressbar.setVisible(true);
                dcr_total_kwh.setText("0");
                dcr_total.setText("0");
            });

            task.setOnSucceeded(wse -> {
                view_btn.setDisable(false);
                dcr_power_table.setItems(bills);
                dcr_breakdown_table.setItems(dcrItems);
                payments_table.setItems(dcrPayments);
                progressbar.setVisible(false);
                dcr_transNo_lbl.setText(bills.size() + "");
                dcr_total_kwh.setText(Utility.formatDecimal(totalKwh));
                dcr_total.setText(Utility.formatDecimal(grandTotal));
            });

            task.setOnFailed(wse -> {
                dcr_transNo_lbl.setText("0");
                view_btn.setDisable(false);
                progressbar.setVisible(false);
                dcr_total_kwh.setText("0");
                dcr_total.setText("0");
                dcr_breakdown_table.setItems(FXCollections.observableArrayList());
                dcr_power_table.setItems(FXCollections.observableArrayList());
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        }
    }

    /**
     * Initializes the DCR transactions table
     * @return void
     */
    public void createDCRTransactionsTable(){
        TableColumn<Bill, String> column0 = new TableColumn<>("Bill #");

        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);
        column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column = new TableColumn<>("Account #");
        column.setPrefWidth(110);
        column.setMaxWidth(110);
        column.setMinWidth(110);
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getAccountID()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column1 = new TableColumn<>("Consumer Name");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getConsumerName()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Bill Balance");
        column2.setPrefWidth(120);
        column2.setMaxWidth(120);
        column2.setMinWidth(120);
        column2.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmountDue())));
        column2.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Type");
        column3.setPrefWidth(75);
        column3.setMaxWidth(75);
        column3.setMinWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerType"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column31 = new TableColumn<>("Month");
        column31.setPrefWidth(75);
        column31.setMaxWidth(75);
        column31.setMinWidth(75);
        column31.setCellValueFactory(new PropertyValueFactory<>("period"));
        column31.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Sys. Loss");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("PPD");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(((PaidBill)obj.getValue()).getPromptPayment()))));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column6 = new TableColumn<>("Surcharge");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(obj.getValue().getSurCharge()))));
        column6.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Net-Amt.");
        column7.setPrefWidth(125);
        column7.setMaxWidth(125);
        column7.setMinWidth(125);
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column8 = new TableColumn<>("Time");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(
                ((PaidBill) obj.getValue()).getPostingTime()+""
        ));
        column8.setPrefWidth(75);
        column8.setMaxWidth(75);
        column8.setMinWidth(75);
        column8.setStyle("-fx-alignment: center;");

        this.bills =  FXCollections.observableArrayList();
        this.dcr_power_table.setFixedCellSize(35);
        this.dcr_power_table.setPlaceholder(new Label("No Bills added"));

        this.dcr_power_table.getColumns().add(column0);
        this.dcr_power_table.getColumns().add(column);
        this.dcr_power_table.getColumns().add(column1);
        this.dcr_power_table.getColumns().add(column2);
        this.dcr_power_table.getColumns().add(column3);
        this.dcr_power_table.getColumns().add(column31);
        this.dcr_power_table.getColumns().add(column4);
        this.dcr_power_table.getColumns().add(column5);
        this.dcr_power_table.getColumns().add(column6);
        this.dcr_power_table.getColumns().add(column7);
        this.dcr_power_table.getColumns().add(column8);
    }

    public void createDCRBreakDown(){
        TableColumn<ItemSummary, String> column1 = new TableColumn<>("Item Description");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ItemSummary, String> column2 = new TableColumn<>("Total Amount");
        column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
        column2.setStyle("-fx-alignment: center-right;");

        this.dcrItems =  FXCollections.observableArrayList();
        this.dcr_breakdown_table.setFixedCellSize(35);
        this.dcr_breakdown_table.setPlaceholder(new Label("No report was generated!"));

        this.dcr_breakdown_table.getColumns().add(column1);
        this.dcr_breakdown_table.getColumns().add(column2);
    }

    public void createDCRSummary(){
        TableColumn<ItemSummary, String> column1 = new TableColumn<>("Payment Type");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ItemSummary, String> column2 = new TableColumn<>("Amount");
        column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
        column2.setStyle("-fx-alignment: center-right;");

        this.dcrPayments =  FXCollections.observableArrayList();
        this.payments_table.setFixedCellSize(35);
        this.payments_table.setPlaceholder(new Label("No report was generated!"));

        this.payments_table.getColumns().add(column1);
        this.payments_table.getColumns().add(column2);
    }

    public static void generateDCR(OutputStream fileOut, String teller, String posting, List<Bill> bills, HashMap<String, List<ItemSummary>> breakdown) throws Exception {
        double billTotal = 0, sysLossTotal = 0, ppdTotal = 0, surchargeTotal = 0, netAmtTotal = 0;
        ExcelBuilder doc = new ExcelBuilder(20);

        CellStyle style = doc.getWb().createCellStyle();
        Font font = doc.getWb().createFont();
        font.setFontHeightInPoints((short) 11);
        font.setFontName("Arial");
        style.setFont(font);

        CellStyle style_center = doc.getWb().createCellStyle();
        style_center.setFont(font);
        style_center.setAlignment(HorizontalAlignment.CENTER);

        CellStyle style_right = doc.getWb().createCellStyle();
        style_right.setFont(font);
        style_right.setAlignment(HorizontalAlignment.RIGHT);

        doc.setTitle("Daily Collection Report");

        EmployeeInfo user = ActiveUser.getUser().getEmployeeInfo();
        String names[] = {user.getEmployeeFirstName()+" "+user.getEmployeeLastName(), "", "", ""};
        String designations[] = {user.getDesignation(),"", "", ""};
        String types[] = {"Prepared by", "", "", ""};
        doc.setNames(names);
        doc.setDesignations(designations);
        doc.setTypes(types);

        doc.createHeader();
        DateFormat dateFormat2 = new SimpleDateFormat("MM/dd/yyyy hh:mm aa");
        String current_date = dateFormat2.format(new Date()).toString();
        doc.createTitle(5, "Date/Time: "+ current_date, false);

        Sheet sheet = doc.getSheet();

        Cell bill_head_cell, bill_acc_cell, bill_cust_cell, bill_bal_cell, bill_type_cell, bill_month_cell, sys_loss_cell, bill_ppd_cell, bill_surcharge_cell, bill_net_amt_cell, time_cell;
        CellRangeAddress bill_addr, acc_addr, cons_addr, bal_addr, sys_loss_addr, ppd_addr, surcharge_ddr, net_ddr, teller_ddr, posting_ddr;

        //DCR section
        int row = 7;
        Row dcr_head_row = sheet.createRow(row);
        Cell tel_lbl_cell = dcr_head_row.createCell(0);
        tel_lbl_cell.setCellValue("Teller");
        teller_ddr = new CellRangeAddress(row, row, 0, 1);
        sheet.addMergedRegion(teller_ddr);
        tel_lbl_cell.setCellStyle(style);

        Cell posting_lbl_cell = dcr_head_row.createCell(2);
        posting_lbl_cell.setCellValue("Posting Date");
        posting_lbl_cell.setCellStyle(style);
        posting_ddr = new CellRangeAddress(row, row, 2, 3);
        sheet.addMergedRegion(posting_ddr);
        posting_lbl_cell.setCellStyle(style);

        row++;

        Row dcr_tel_row = sheet.createRow(row);
        Cell teller_cell = dcr_tel_row.createCell(0);
        teller_cell.setCellValue(teller);
        teller_ddr = new CellRangeAddress(row, row, 0, 1);
        sheet.addMergedRegion(teller_ddr);
        teller_cell.setCellStyle(style);


        Cell posting_cell = dcr_tel_row.createCell(2);
        posting_cell.setCellValue(posting);
        posting_ddr = new CellRangeAddress(row, row, 2, 3);
        sheet.addMergedRegion(posting_ddr);
        posting_cell.setCellStyle(style_center);

        row = 10;

        Row dcr_table_head = sheet.createRow(row);

        bill_head_cell = dcr_table_head.createCell(0);
        bill_head_cell.setCellValue("Bill #");
        bill_addr = new CellRangeAddress(row, row, 0, 1);
        sheet.addMergedRegion(bill_addr);
        bill_head_cell.setCellStyle(style);
        doc.styleMergedCells(bill_addr);

        bill_acc_cell = dcr_table_head.createCell(2);
        bill_acc_cell.setCellValue("Account #");
        acc_addr = new CellRangeAddress(row, row, 2, 3);
        sheet.addMergedRegion(acc_addr);
        bill_acc_cell.setCellStyle(style);
        doc.styleMergedCells(acc_addr);

        bill_cust_cell = dcr_table_head.createCell(4);
        bill_cust_cell.setCellValue("Consumer Name");
        cons_addr = new CellRangeAddress(row, row, 4, 6);
        sheet.addMergedRegion(cons_addr);
        bill_cust_cell.setCellStyle(style);
        doc.styleMergedCells(cons_addr);

        bill_bal_cell = dcr_table_head.createCell(7);
        bill_bal_cell.setCellValue("Bill Balance");
        bal_addr = new CellRangeAddress(row, row, 7, 8);
        sheet.addMergedRegion(bal_addr);
        bill_bal_cell.setCellStyle(style_center);
        doc.styleMergedCells(bal_addr);


        bill_type_cell = dcr_table_head.createCell(9);
        bill_type_cell.setCellValue("Type");
        doc.styleBorder(bill_type_cell, 12, HorizontalAlignment.CENTER, false);

        bill_month_cell = dcr_table_head.createCell(10);
        bill_month_cell.setCellValue("Month");
        doc.styleBorder(bill_month_cell, 12, HorizontalAlignment.CENTER, false);

        sys_loss_cell = dcr_table_head.createCell(11);
        sys_loss_cell.setCellValue("Sys. Loss");
        sys_loss_addr = new CellRangeAddress(row, row, 11, 12);
        sheet.addMergedRegion(sys_loss_addr);
        sys_loss_cell.setCellStyle(style_center);
        doc.styleMergedCells(sys_loss_addr);

        bill_ppd_cell = dcr_table_head.createCell(13);
        bill_ppd_cell.setCellValue("PPD");
        ppd_addr = new CellRangeAddress(row, row, 13, 14);
        sheet.addMergedRegion(ppd_addr);
        bill_ppd_cell.setCellStyle(style_center);
        doc.styleMergedCells(ppd_addr);

        bill_surcharge_cell = dcr_table_head.createCell(15);
        bill_surcharge_cell.setCellValue("Surcharge");
        surcharge_ddr = new CellRangeAddress(row, row, 15, 16);
        sheet.addMergedRegion(surcharge_ddr);
        bill_surcharge_cell.setCellStyle(style_center);
        doc.styleMergedCells(surcharge_ddr);

        bill_net_amt_cell = dcr_table_head.createCell(17);
        bill_net_amt_cell.setCellValue("Net-Amt.");
        net_ddr = new CellRangeAddress(row, row, 17, 18);
        sheet.addMergedRegion(net_ddr);
        bill_net_amt_cell.setCellStyle(style_center);
        doc.styleMergedCells(net_ddr);


        time_cell = dcr_table_head.createCell(19);
        time_cell.setCellValue("Time");
        doc.styleBorder(time_cell, 12, HorizontalAlignment.CENTER, false);

        for (Bill b: bills) {
            row++;
            Row dcr_table_row_head = sheet.createRow(row);

            bill_head_cell = dcr_table_row_head.createCell(0);
            bill_head_cell.setCellValue(b.getBillNo());
            bill_addr = new CellRangeAddress(row, row, 0, 1);
            sheet.addMergedRegion(bill_addr);
            bill_head_cell.setCellStyle(style);
            doc.styleMergedCells(bill_addr);

            bill_acc_cell = dcr_table_row_head.createCell(2);
            bill_acc_cell.setCellValue(b.getConsumer().getAccountID());
            acc_addr = new CellRangeAddress(row, row, 2, 3);
            sheet.addMergedRegion(acc_addr);
            bill_acc_cell.setCellStyle(style);
            doc.styleMergedCells(acc_addr);

            bill_cust_cell = dcr_table_row_head.createCell(4);
            bill_cust_cell.setCellValue(b.getConsumer().getConsumerName());
            cons_addr = new CellRangeAddress(row, row, 4, 6);
            sheet.addMergedRegion(cons_addr);
            bill_cust_cell.setCellStyle(style);
            doc.styleMergedCells(cons_addr);

            bill_bal_cell = dcr_table_row_head.createCell(7);
            bill_bal_cell.setCellValue(Utility.formatDecimal(b.getAmountDue()));
            bal_addr = new CellRangeAddress(row, row, 7, 8);
            sheet.addMergedRegion(bal_addr);
            bill_bal_cell.setCellStyle(style_right);
            doc.styleMergedCells(bal_addr);


            bill_type_cell = dcr_table_row_head.createCell(9);
            bill_type_cell.setCellValue(b.getConsumerType());
            doc.styleBorder(bill_type_cell, 12, HorizontalAlignment.CENTER, false);

            bill_month_cell = dcr_table_row_head.createCell(10);
            bill_month_cell.setCellValue(b.getBillMonth());
            doc.styleBorder(bill_month_cell, 12, HorizontalAlignment.CENTER, false);

            sys_loss_cell = dcr_table_row_head.createCell(11);
            sys_loss_cell.setCellValue(b.getSlAdjustment());
            sys_loss_addr = new CellRangeAddress(row, row, 11, 12);
            sheet.addMergedRegion(sys_loss_addr);
            sys_loss_cell.setCellStyle(style_right);
            doc.styleMergedCells(sys_loss_addr);

            PaidBill p = (PaidBill) b;
            bill_ppd_cell = dcr_table_row_head.createCell(13);
            bill_ppd_cell.setCellValue(p.getPromptPayment());
            ppd_addr = new CellRangeAddress(row, row, 13, 14);
            sheet.addMergedRegion(ppd_addr);
            bill_ppd_cell.setCellStyle(style_right);
            doc.styleMergedCells(ppd_addr);

            bill_surcharge_cell = dcr_table_row_head.createCell(15);
            bill_surcharge_cell.setCellValue(b.getSurCharge());
            surcharge_ddr = new CellRangeAddress(row, row, 15, 16);
            sheet.addMergedRegion(surcharge_ddr);
            bill_surcharge_cell.setCellStyle(style_right);
            doc.styleMergedCells(surcharge_ddr);

            bill_net_amt_cell = dcr_table_row_head.createCell(17);
            bill_net_amt_cell.setCellValue(b.getTotalAmount());
            net_ddr = new CellRangeAddress(row, row, 17, 18);
            sheet.addMergedRegion(net_ddr);
            bill_net_amt_cell.setCellStyle(style_right);
            doc.styleMergedCells(net_ddr);

            time_cell = dcr_table_row_head.createCell(19);
            time_cell.setCellValue(((PaidBill) b).getPostingTime());
            doc.styleBorder(time_cell, 12, HorizontalAlignment.CENTER, false);

            billTotal += b.getAmountDue(); sysLossTotal += b.getSlAdjustment(); ppdTotal += p.getPromptPayment(); surchargeTotal += b.getSurCharge(); netAmtTotal += b.getTotalAmount();
        }

        List<ItemSummary> breakdownItems = breakdown.get("Breakdown");
        List<ItemSummary> totalItems = breakdown.get("Misc");

        int i = 1;
        Row dcr_table_row_head;
        row++;

        for(ItemSummary s : breakdownItems) {

            row++;

            dcr_table_row_head = sheet.createRow(row);

            bill_head_cell = dcr_table_row_head.createCell(0);
            bill_head_cell.setCellValue(s.getDescription());
            bill_addr = new CellRangeAddress(row, row, 0, 1);
            sheet.addMergedRegion(bill_addr);
            bill_head_cell.setCellStyle(style);
            doc.styleMergedCells(bill_addr, false, false, false, false, false);

            bill_acc_cell = dcr_table_row_head.createCell(2);
            bill_acc_cell.setCellValue(Utility.formatDecimal(s.getTotal()));
            acc_addr = new CellRangeAddress(row, row, 2, 3);
            sheet.addMergedRegion(acc_addr);
            bill_acc_cell.setCellStyle(style);
            doc.styleMergedCells(acc_addr, false, false, false, false, false);

            if (i == 1) {
                bill_bal_cell = dcr_table_row_head.createCell(7);
                bill_bal_cell.setCellValue(Utility.formatDecimal(billTotal));
                bal_addr = new CellRangeAddress(row, row, 7, 8);
                sheet.addMergedRegion(bal_addr);
                bill_bal_cell.setCellStyle(style_right);
                doc.styleMergedCells(bal_addr, false, false, false, false, false);

                sys_loss_cell = dcr_table_row_head.createCell(11);
                sys_loss_cell.setCellValue(Utility.formatDecimal(sysLossTotal));
                sys_loss_addr = new CellRangeAddress(row, row, 11, 12);
                sheet.addMergedRegion(sys_loss_addr);
                sys_loss_cell.setCellStyle(style_right);
                doc.styleMergedCells(sys_loss_addr, false, false, false, false, false);

                bill_ppd_cell = dcr_table_row_head.createCell(13);
                bill_ppd_cell.setCellValue(Utility.formatDecimal(ppdTotal));
                ppd_addr = new CellRangeAddress(row, row, 13, 14);
                sheet.addMergedRegion(ppd_addr);
                bill_ppd_cell.setCellStyle(style_right);
                doc.styleMergedCells(ppd_addr, false, false, false, false, false);

                bill_surcharge_cell = dcr_table_row_head.createCell(15);
                bill_surcharge_cell.setCellValue(Utility.formatDecimal(surchargeTotal));
                surcharge_ddr = new CellRangeAddress(row, row, 15, 16);
                sheet.addMergedRegion(surcharge_ddr);
                bill_surcharge_cell.setCellStyle(style_right);
                doc.styleMergedCells(surcharge_ddr, false, false, false, false, false);

                bill_net_amt_cell = dcr_table_row_head.createCell(17);
                bill_net_amt_cell.setCellValue(Utility.formatDecimal(netAmtTotal));
                net_ddr = new CellRangeAddress(row, row, 17, 18);
                sheet.addMergedRegion(net_ddr);
                bill_net_amt_cell.setCellStyle(style_right);
                doc.styleMergedCells(net_ddr, false, false, false, false, false);
            }else if (i == 6){
                bill_surcharge_cell = dcr_table_row_head.createCell(15);
                bill_surcharge_cell.setCellValue("KWH Used");
                surcharge_ddr = new CellRangeAddress(row, row, 15, 16);
                sheet.addMergedRegion(surcharge_ddr);
                bill_surcharge_cell.setCellStyle(style_right);
                doc.styleMergedCells(surcharge_ddr, false, false, false, false, false);

                bill_net_amt_cell = dcr_table_row_head.createCell(17);
                bill_net_amt_cell.setCellValue(totalItems.get(0).getTotal());
                net_ddr = new CellRangeAddress(row, row, 17, 18);
                sheet.addMergedRegion(net_ddr);
                bill_net_amt_cell.setCellStyle(style_right);
                doc.styleMergedCells(net_ddr, false, false, false, false, false);
            }
            i++;
        }

        row++;

        dcr_table_row_head = sheet.createRow(row);

        bill_head_cell = dcr_table_row_head.createCell(0);
        bill_head_cell.setCellValue("Total");
        bill_addr = new CellRangeAddress(row, row, 0, 1);
        sheet.addMergedRegion(bill_addr);
        bill_head_cell.setCellStyle(style);
        doc.styleMergedCells(bill_addr, false, false, false, false, false);

        CellStyle style_bold = doc.getWb().createCellStyle();
        Font font_bold = doc.getWb().createFont();
        font_bold.setFontHeightInPoints((short) 11);
        font_bold.setFontName("Arial");
        font_bold.setBold(true);
        style_bold.setFont(font_bold);

        bill_acc_cell = dcr_table_row_head.createCell(2);
        bill_acc_cell.setCellValue(Utility.formatDecimal(totalItems.get(1).getTotal()));
        acc_addr = new CellRangeAddress(row, row, 2, 3);
        sheet.addMergedRegion(acc_addr);
        bill_acc_cell.setCellStyle(style_bold);
        doc.styleMergedCells(acc_addr, false, false, false, false, false);

        row++;

        doc.createSignatorees(row);

        doc.save(fileOut);
    }
}
