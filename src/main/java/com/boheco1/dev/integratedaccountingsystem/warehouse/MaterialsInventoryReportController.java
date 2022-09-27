package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.IRDao;
import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;

public class MaterialsInventoryReportController implements Initializable {

    @FXML
    private TableView<IRItem> itemsTable;

    @FXML
    private Label begQty_lbl;

    @FXML
    private Label begCost_lbl;

    @FXML
    private Label begAmount_lbl;

    @FXML
    private Label recQty_lbl;

    @FXML
    private Label recAmount_lbl;

    @FXML
    private Label retQty_lbl;

    @FXML
    private Label retAmount_lbl;

    @FXML
    private Label chgQty_lbl;

    @FXML
    private Label chgAmount_lbl;

    @FXML
    private Label endQty_lbl;

    @FXML
    private Label endAmount_lbl;

    @FXML
    private Label endCost_lbl;

    @FXML
    private JFXComboBox<String> month_cb;

    @FXML
    private JFXTextField year_tf;

    private List<IRItem> items;

    private ObservableList<IRItem> observableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String[] month = {"January","February","March","April","May","June","July","August","September","October","November","December"};
        month_cb.getItems().addAll(month);
        year_tf.setText(""+Calendar.getInstance().get(Calendar.YEAR));
        prepareTable();
    }

    @FXML
    private void printSummary(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(items == null)
            return;

        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        String month = month_cb.getSelectionModel().getSelectedItem();
        String year = year_tf.getText();
        fileChooser.setInitialFileName("Inventory_of_construction_materials_"+month+ "_" +year+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);
        JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, generating report.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() {
                try{
                    dialog.show();

                    if (selectedFile != null) {
                        generateExcel(items, selectedFile);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("Printing Error", e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                return null;
            }
        };
        task.setOnSucceeded(wse -> {
            dialog.close();
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
            dialog.close();
            AlertDialogBuilder.messgeDialog("System Warning", "A problem was encountered, please try again",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        });
        new Thread(task).start();
    }

    @FXML
    private void selectByMonth(ActionEvent event) throws SQLException, ClassNotFoundException {
        try{
            int index = month_cb.getSelectionModel().getSelectedIndex()+1;
            int year = Integer.parseInt(year_tf.getText());
            items = IRDao.generateReport(year,index);
            System.out.println(items.size());
            observableList = FXCollections.observableArrayList(items);
            itemsTable.getItems().clear();
            itemsTable.getItems().setAll(observableList);
            itemsTable.refresh();
            double begQty=0, begCost=0, begAmount=0, recQty=0, recAmount=0, retQty=0, retAmount=0, chgQty=0, chgAmount=0, endQty=0, endCost=0, endAmount = 0;

            for (IRItem item : items) {
               begQty += item.getBeginningQty();
               begCost += item.getBeginningPrice();
               begAmount += item.getBeginningAmount();
               recQty += item.getReceivedQty();
               recAmount += item.getReceivedAmount();
               retQty += item.getReturnedQty();
               retAmount += item.getReturnedAmount();
               chgQty += item.getReleasedQty();
               chgAmount += item.getReleasedAmount();
               endQty += item.getQuantity();
               endCost += item.getPrice();
               endAmount += item.getQuantity()*item.getPrice();
            }
            /*
            System.out.println(begQty);
            System.out.println(begCost);
            System.out.println(begAmount);
            System.out.println(recQty);
            System.out.println(recAmount);
            System.out.println(retQty);
            System.out.println(retAmount);
            System.out.println(chgQty);
            System.out.println(chgAmount);
            System.out.println(endQty);
            System.out.println(endCost);
            System.out.println(endAmount);*/

            this.begQty_lbl.setText(begQty+"");
            //this.begCost_lbl.setText(begCost+"");
            this.begAmount_lbl.setText(begAmount+"");
            if (recQty != 0) {
                this.recQty_lbl.setText(recQty+"");
                this.recAmount_lbl.setText(recAmount+"");
            }
            if (retQty != 0) {
                this.retQty_lbl.setText(retQty + "");
                this.retAmount_lbl.setText(retAmount + "");
            }
            if (chgQty != 0) {
                this.chgQty_lbl.setText(chgQty + "");
                this.chgAmount_lbl.setText(chgAmount + "");
            }
            this.endQty_lbl.setText(endQty+"");
            //this.endCost_lbl.setText(endCost+"");
            this.endAmount_lbl.setText(endAmount+"");
        }catch (Exception e){
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error", e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }

    }

    private void prepareTable(){

        TableColumn<IRItem, String> code = new TableColumn<>("CODE NO");
        code.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getCode()));

        TableColumn<IRItem, String> desc = new TableColumn<>("ITEMS");
        desc.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<IRItem, String> qty = new TableColumn<>("QTY");
        qty.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getBeginningQty()+""));

        TableColumn<IRItem, String> begPrice = new TableColumn<>("UNIT COST");
        begPrice.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getBeginningPrice()+""));

        TableColumn<IRItem, String> begAmount = new TableColumn<>("AMOUNT");
        begAmount.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getBeginningAmount()+""));

        TableColumn<IRItem, String> recRef = new TableColumn<>("RECEIVING");
        recRef.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReceivedReference()+""));

        TableColumn<IRItem, String> recQty = new TableColumn<>("QTY");
        recQty.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReceivedQty()+""));

        TableColumn<IRItem, String> recAmount = new TableColumn<>("AMOUNT");
        recAmount.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReceivedAmount()+""));

        TableColumn<IRItem, String> retRef = new TableColumn<>("RETURNED");
        retRef.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReturnedReference()+""));

        TableColumn<IRItem, String> retQty = new TableColumn<>("QTY");
        retQty.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReturnedQty()+""));

        TableColumn<IRItem, String> retAmount = new TableColumn<>("AMOUNT");
        retAmount.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReturnedAmount()+""));

        TableColumn<IRItem, String> chgPrice = new TableColumn<>("CHG. QTY");
        chgPrice.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReleasedQty()+""));

        TableColumn<IRItem, String> chgAmount = new TableColumn<>("AMOUNT");
        chgAmount.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getReleasedAmount()+""));

        TableColumn<IRItem, String> endingQty = new TableColumn<>("BALANCE");
        endingQty.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getQuantity()+""));

        TableColumn<IRItem, String> endingPrice = new TableColumn<>("PRICE");
        endingPrice.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getPrice()+""));

        TableColumn<IRItem, String> endingAmount = new TableColumn<>("AMOUNT");
        endingAmount.setCellValueFactory(item -> new SimpleStringProperty(item.getValue().getQuantity()*item.getValue().getPrice()+""));

        itemsTable.getColumns().add(code);
        itemsTable.getColumns().add(desc);
        itemsTable.getColumns().add(qty);
        itemsTable.getColumns().add(begPrice);
        itemsTable.getColumns().add(begAmount);
        itemsTable.getColumns().add(recRef);
        itemsTable.getColumns().add(recQty);
        itemsTable.getColumns().add(recAmount);
        itemsTable.getColumns().add(retRef);
        itemsTable.getColumns().add(retQty);
        itemsTable.getColumns().add(retAmount);
        itemsTable.getColumns().add(chgPrice);
        itemsTable.getColumns().add(chgAmount);
        itemsTable.getColumns().add(endingQty);
        itemsTable.getColumns().add(endingPrice);
        itemsTable.getColumns().add(endingAmount);

        itemsTable.setPlaceholder(new Label("No item Added"));
    }

    public void reset(){
        this.begQty_lbl.setText("");
        this.begCost_lbl.setText("");
        this.begAmount_lbl.setText("");
        this.recQty_lbl.setText("");
        this.recAmount_lbl.setText("");
        this.retQty_lbl.setText("");
        this.retAmount_lbl.setText("");
        this.chgQty_lbl.setText("");
        this.chgAmount_lbl.setText("");
        this.endQty_lbl.setText("");
        this.endCost_lbl.setText("");
        this.endAmount_lbl.setText("");
    }

    public void generateExcel(List<IRItem> items, File file){
        List<String> rrnos = new ArrayList<>();

        ExcelBuilder excel = new ExcelBuilder(19);
        excel.setTitle("INVENTORY OF CONSTRUCTION MATERIALS ");
        excel.setMargin(1, 0.5, 1, 0.5);
        excel.createHeader();
        excel.createTitle(5, "As of " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), false);

        Sheet sheet = excel.getSheet();
        int row = 9;

        //Begin table header
        Row table_header = sheet.createRow(row);
        //First column
        Cell top1 = table_header.createCell(0);
        top1.setCellValue(" ");
        excel.styleBorder(top1, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Second column
        Cell item_top = table_header.createCell(1);
        item_top.setCellValue(" ");
        CellRangeAddress item_top_add = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(item_top_add);
        excel.styleMergedCells(item_top_add, false, true, true, false, true);

        //Third column
        Cell top3 = table_header.createCell(5);
        top3.setCellValue(" ");
        excel.styleBorder(top3, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Center Cell Style
        CellStyle center_style = excel.getWb().createCellStyle();
        center_style.setAlignment(HorizontalAlignment.CENTER);

        //Beginning Balance
        Cell start_balance_cell = table_header.createCell(6);
        start_balance_cell.setCellValue("BEGINNING BALANCE");
        start_balance_cell.setCellStyle(center_style);
        CellRangeAddress start_balance_addr = new CellRangeAddress(row, row, 6, 7);
        sheet.addMergedRegion(start_balance_addr);
        excel.styleMergedCells(start_balance_addr, true, true, true, false, true);

        //Receipts
        Cell receipts_cell = table_header.createCell(8);
        receipts_cell.setCellValue("RECEIPTS");
        receipts_cell.setCellStyle(center_style);
        CellRangeAddress receipts_addr = new CellRangeAddress(row, row, 8, 10);
        sheet.addMergedRegion(receipts_addr);
        excel.styleMergedCells(receipts_addr, true, true, true, false, true);

        //Returns
        Cell returns_cell = table_header.createCell(11);
        returns_cell.setCellValue("RETURNS");
        returns_cell.setCellStyle(center_style);
        CellRangeAddress returns_addr = new CellRangeAddress(row, row, 11, 13);
        sheet.addMergedRegion(returns_addr);
        excel.styleMergedCells(returns_addr, true, true, true, false, true);

        //Charges
        Cell charges_cell = table_header.createCell(14);
        charges_cell.setCellValue("CHARGES");
        charges_cell.setCellStyle(center_style);
        CellRangeAddress charges_addr = new CellRangeAddress(row, row, 14, 15);
        sheet.addMergedRegion(charges_addr);
        excel.styleMergedCells(charges_addr, true, true, true, false, true);

        //Balance
        Cell balance_cell = table_header.createCell(16);
        balance_cell.setCellValue("BALANCE");
        balance_cell.setCellStyle(center_style);
        CellRangeAddress balance_addr = new CellRangeAddress(row, row, 16, 18);
        sheet.addMergedRegion(balance_addr);
        excel.styleMergedCells(balance_addr, true, true, true, false, true);

        //Second Line
        row = 10;

        //Begin table header
        table_header = sheet.createRow(row);

        //First column
        Cell top1_2 = table_header.createCell(0);
        top1_2.setCellValue("CODE");
        excel.styleBorder(top1_2, 11, HorizontalAlignment.CENTER, false, false, true, false, true);

        //Second column
        Cell item_top2 = table_header.createCell(1);
        item_top2.setCellValue("I T E M S");
        item_top2.setCellStyle(center_style);
        CellRangeAddress item_top_add2 = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(item_top_add2);
        excel.styleMergedCells(item_top_add2, false, false, true, false, true);

        //Third column
        Cell top3_2 = table_header.createCell(5);
        top3_2.setCellValue(" ");
        excel.styleBorder(top3_2, 11, HorizontalAlignment.CENTER, false, false, true, false, true);

        //Beginning Balance
        Cell start_balance_cell2 = table_header.createCell(6);
        start_balance_cell2.setCellValue(" ");
        start_balance_cell2.setCellStyle(center_style);
        CellRangeAddress start_balance_addr2 = new CellRangeAddress(row, row, 6, 7);
        sheet.addMergedRegion(start_balance_addr2);
        excel.styleMergedCells(start_balance_addr2, true, false, true, false, true);

        //Receipts
        Cell receipts_2_1 = table_header.createCell(8);
        receipts_2_1.setCellValue(" ");
        excel.styleBorder(receipts_2_1, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell receipts_2_2 = table_header.createCell(9);
        receipts_2_2.setCellValue(" ");
        excel.styleBorder(receipts_2_2, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell receipts_2_3 = table_header.createCell(10);
        receipts_2_3.setCellValue(" ");
        excel.styleBorder(receipts_2_3, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Returns
        Cell returns_2_1 = table_header.createCell(11);
        returns_2_1.setCellValue(" ");
        excel.styleBorder(returns_2_1, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell returns_2_2 = table_header.createCell(12);
        returns_2_2.setCellValue(" ");
        excel.styleBorder(returns_2_2, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell returns_2_3 = table_header.createCell(13);
        returns_2_3.setCellValue("MISSING");
        excel.styleBorder(returns_2_3, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Charges
        Cell charges_2_1 = table_header.createCell(14);
        charges_2_1.setCellValue(" ");
        excel.styleBorder(charges_2_1, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell charges_2_2 = table_header.createCell(15);
        charges_2_2.setCellValue(" ");
        excel.styleBorder(charges_2_2, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Balance
        Cell balance_2_1 = table_header.createCell(16);
        balance_2_1.setCellValue(" ");
        excel.styleBorder(balance_2_1, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell balance_2_2 = table_header.createCell(17);
        balance_2_2.setCellValue(" ");
        excel.styleBorder(balance_2_2, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        Cell balance_2_3 = table_header.createCell(18);
        balance_2_3.setCellValue(" ");
        excel.styleBorder(balance_2_3, 11, HorizontalAlignment.CENTER, false, true, true, false, true);

        //Third Line
        row = 11;

        //Begin table header
        table_header = sheet.createRow(row);

        //First column
        Cell top1_3 = table_header.createCell(0);
        top1_3.setCellValue("NUMBER");
        excel.styleBorder(top1_3, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Second column
        Cell item_top3 = table_header.createCell(1);
        item_top3.setCellValue(" ");
        item_top3.setCellStyle(center_style);
        CellRangeAddress item_top_add3 = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(item_top_add3);
        excel.styleMergedCells(item_top_add3, false, false, true, true, true);

        //Third column
        Cell top3_3 = table_header.createCell(5);
        top3_3.setCellValue("QTY");
        top3_3.setCellStyle(center_style);
        excel.styleBorder(top3_3, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Beginning Balance
        Cell start_balance_cell3 = table_header.createCell(6);
        start_balance_cell3.setCellValue("UNIT COST");
        start_balance_cell3.setCellStyle(center_style);
        excel.styleBorder(start_balance_cell3, 11, HorizontalAlignment.CENTER, false, false, false, true, true);

        Cell start_balance_cell4 = table_header.createCell(7);
        start_balance_cell4.setCellValue("AMOUNT");
        start_balance_cell4.setCellStyle(center_style);
        excel.styleBorder(start_balance_cell4, 11, HorizontalAlignment.CENTER, false, false, true, true, false);

        //Receipts
        Cell receipts_3_1 = table_header.createCell(8);
        receipts_3_1.setCellValue("REF.");
        receipts_3_1.setCellStyle(center_style);
        excel.styleBorder(receipts_3_1, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell receipts_3_2 = table_header.createCell(9);
        receipts_3_2.setCellValue("QTY.");
        receipts_3_2.setCellStyle(center_style);
        excel.styleBorder(receipts_3_2, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell receipts_3_3 = table_header.createCell(10);
        receipts_3_3.setCellValue("AMOUNT");
        receipts_3_3.setCellStyle(center_style);
        excel.styleBorder(receipts_3_3, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Returns
        Cell returns_3_1 = table_header.createCell(11);
        returns_3_1.setCellValue("REF.");
        returns_3_1.setCellStyle(center_style);
        excel.styleBorder(returns_3_1, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell returns_3_2 = table_header.createCell(12);
        returns_3_2.setCellValue("QTY.");
        returns_3_2.setCellStyle(center_style);
        excel.styleBorder(returns_3_2, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell returns_3_3 = table_header.createCell(13);
        returns_3_3.setCellValue("AMOUNT");
        returns_3_3.setCellStyle(center_style);
        excel.styleBorder(returns_3_3, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Charges
        Cell charges_3_1 = table_header.createCell(14);
        charges_3_1.setCellValue("QTY.");
        charges_3_1.setCellStyle(center_style);
        excel.styleBorder(charges_3_1, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell charges_3_2 = table_header.createCell(15);
        charges_3_2.setCellValue("AMOUNT");
        charges_3_2.setCellStyle(center_style);
        excel.styleBorder(charges_3_2, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Balance
        Cell balance_3_1 = table_header.createCell(16);
        balance_3_1.setCellValue("QTY.");
        balance_3_1.setCellStyle(center_style);
        excel.styleBorder(balance_3_1, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell balance_3_2 = table_header.createCell(17);
        balance_3_2.setCellValue("UNIT COST");
        balance_3_2.setCellStyle(center_style);
        excel.styleBorder(balance_3_2, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        Cell balance_3_3 = table_header.createCell(18);
        balance_3_3.setCellValue("AMOUNT");
        balance_3_3.setCellStyle(center_style);
        excel.styleBorder(balance_3_3, 11, HorizontalAlignment.CENTER, false, false, true, true, true);

        //Center Cell Style
        CellStyle right_style = excel.getWb().createCellStyle();
        right_style.setAlignment(HorizontalAlignment.RIGHT);

        int i = 12;

        double begQty=0, begCost=0, begAmount=0, recQty=0, recAmount=0, retQty=0, retAmount=0, chgQty=0, chgAmount=0, endQty=0, endCost=0, endAmount = 0;

        for (IRItem item:items){

            begQty += item.getBeginningQty();
            begCost += item.getBeginningPrice();
            begAmount += item.getBeginningAmount();
            recQty += item.getReceivedQty();
            recAmount += item.getReceivedAmount();
            retQty += item.getReturnedQty();
            retAmount += item.getReturnedAmount();
            chgQty += item.getReleasedQty();
            chgAmount += item.getReleasedAmount();
            endQty += item.getQuantity();
            endCost += item.getPrice();
            endAmount += item.getQuantity()*item.getPrice();

            row = i;

            table_header = sheet.createRow(row);

            //First column
            Cell col_code = table_header.createCell(0);
            col_code.setCellValue(item.getCode());
            excel.styleBorder(col_code, 11, HorizontalAlignment.LEFT, false, false, true, false, true);

            //Second column
            Cell col_item = table_header.createCell(1);
            col_item.setCellValue(item.getDescription());
            CellRangeAddress col_item_add2 = new CellRangeAddress(row, row, 1, 4);
            sheet.addMergedRegion(col_item_add2);
            excel.styleMergedCells(col_item_add2, false, false, true, false, true);

            //Third column
            Cell col_qty = table_header.createCell(5);
            col_qty.setCellValue(item.getBeginningQty());
            col_qty.setCellStyle(right_style);
            excel.styleBorder(col_qty, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            //Beginning Balance
            Cell col_bal_cost = table_header.createCell(6);
            col_bal_cost.setCellValue(item.getBeginningPrice());
            excel.styleBorder(col_bal_cost, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell col_bal_amount = table_header.createCell(7);
            col_bal_amount.setCellValue(item.getBeginningAmount());
            excel.styleBorder(col_bal_amount, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            //Receipts
            Cell col_receipt = table_header.createCell(8);
            String rr = item.getReceivedReference();
            col_receipt.setCellValue(rr);
            excel.styleBorder(col_receipt, 11, HorizontalAlignment.LEFT, false, false, true, false, true);
            if (!rr.isEmpty() && !rr.equals("") && rr != null)
                rrnos.add(rr);
            Cell receipts_qty = table_header.createCell(9);
            receipts_qty.setCellValue(item.getReceivedQty());
            receipts_qty.setCellStyle(right_style);
            excel.styleBorder(receipts_qty, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell receipts_amount = table_header.createCell(10);
            receipts_amount.setCellValue(item.getReceivedAmount());
            receipts_amount.setCellStyle(right_style);
            excel.styleBorder(receipts_amount, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            //Returns
            Cell returns_ref = table_header.createCell(11);
            returns_ref.setCellValue(item.getReturnedReference());
            excel.styleBorder(returns_ref, 11, HorizontalAlignment.LEFT, false, false, true, false, true);

            Cell returns_qty = table_header.createCell(12);
            returns_qty.setCellValue(item.getReturnedQty());
            excel.styleBorder(returns_qty, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell returns_amount = table_header.createCell(13);
            returns_amount.setCellValue(item.getReturnedAmount());
            excel.styleBorder(returns_amount, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            //Charges
            Cell charges_qty = table_header.createCell(14);
            charges_qty.setCellValue(item.getReleasedQty());
            excel.styleBorder(charges_qty, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell charges_amount = table_header.createCell(15);
            charges_amount.setCellValue(item.getReleasedAmount());
            excel.styleBorder(charges_amount, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            //Balance
            Cell balance_qty = table_header.createCell(16);
            balance_qty.setCellValue(item.getQuantity());
            excel.styleBorder(balance_qty, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell balance_cost = table_header.createCell(17);
            balance_cost.setCellValue(item.getPrice());
            excel.styleBorder(balance_cost, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            Cell balance_amount = table_header.createCell(18);
            balance_amount.setCellValue(item.getPrice()*item.getQuantity());
            excel.styleBorder(balance_amount, 11, HorizontalAlignment.RIGHT, false, false, true, false, true);

            i++;
        }

        //Empty line
        row++;
        table_header = sheet.createRow(row);

        //First column
        Cell col_code = table_header.createCell(0);
        col_code.setCellValue(" ");
        excel.styleBorder(col_code, 11, HorizontalAlignment.LEFT, false, true, true, false, true);

        //Second column
        Cell col_item = table_header.createCell(1);
        col_item.setCellValue(" ");
        CellRangeAddress col_item_add2 = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(col_item_add2);
        excel.styleMergedCells(col_item_add2, false, true, true, false, true);

        //Third column
        Cell col_qty = table_header.createCell(5);
        col_qty.setCellValue(" ");
        col_qty.setCellStyle(right_style);
        excel.styleBorder(col_qty, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Beginning Balance
        Cell col_bal_cost = table_header.createCell(6);
        col_bal_cost.setCellValue(" ");
        excel.styleBorder(col_bal_cost, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell col_bal_amount = table_header.createCell(7);
        col_bal_amount.setCellValue(" ");
        excel.styleBorder(col_bal_amount, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Receipts
        Cell col_receipt = table_header.createCell(8);
        col_receipt.setCellValue(" ");
        excel.styleBorder(col_receipt, 11, HorizontalAlignment.LEFT, false, true, true, false, true);

        Cell receipts_qty = table_header.createCell(9);
        receipts_qty.setCellValue(" ");
        receipts_qty.setCellStyle(right_style);
        excel.styleBorder(receipts_qty, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell receipts_amount = table_header.createCell(10);
        receipts_amount.setCellValue(" ");
        receipts_amount.setCellStyle(right_style);
        excel.styleBorder(receipts_amount, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Returns
        Cell returns_ref = table_header.createCell(11);
        returns_ref.setCellValue(" ");
        excel.styleBorder(returns_ref, 11, HorizontalAlignment.LEFT, false, true, true, false, true);

        Cell returns_qty = table_header.createCell(12);
        returns_qty.setCellValue(" ");
        excel.styleBorder(returns_qty, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell returns_amount = table_header.createCell(13);
        returns_amount.setCellValue(" ");
        excel.styleBorder(returns_amount, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Charges
        Cell charges_qty = table_header.createCell(14);
        charges_qty.setCellValue(" ");
        excel.styleBorder(charges_qty, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell charges_amount = table_header.createCell(15);
        charges_amount.setCellValue(" ");
        excel.styleBorder(charges_amount, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Balance
        Cell balance_qty = table_header.createCell(16);
        balance_qty.setCellValue(" ");
        excel.styleBorder(balance_qty, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell balance_cost = table_header.createCell(17);
        balance_cost.setCellValue(" ");
        excel.styleBorder(balance_cost, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        Cell balance_amount = table_header.createCell(18);
        balance_amount.setCellValue(" ");
        excel.styleBorder(balance_amount, 11, HorizontalAlignment.RIGHT, false, true, true, false, true);

        //Grand Total
        row++;
        table_header = sheet.createRow(row);

        //First column
        col_code = table_header.createCell(0);
        col_code.setCellValue(" ");
        excel.styleBorder(col_code, 11, HorizontalAlignment.LEFT, false, false, true, true, true);

        //Second column
        col_item = table_header.createCell(1);
        col_item.setCellValue("G R A N D   T O T A L");
        col_item_add2 = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(col_item_add2);
        excel.styleMergedCells(col_item_add2, false, false, true, true, true);

        //Third column
        col_qty = table_header.createCell(5);
        col_qty.setCellValue(begQty);
        col_qty.setCellStyle(right_style);
        excel.styleBorder(col_qty, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Beginning Balance
        col_bal_cost = table_header.createCell(6);
        //col_bal_cost.setCellValue(begCost);
        col_bal_cost.setCellValue(" ");
        excel.styleBorder(col_bal_cost, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        col_bal_amount = table_header.createCell(7);
        col_bal_amount.setCellValue(begAmount);
        excel.styleBorder(col_bal_amount, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Receipts
        col_receipt = table_header.createCell(8);
        col_receipt.setCellValue(" ");
        excel.styleBorder(col_receipt, 11, HorizontalAlignment.LEFT, false, false, true, true, true);

        receipts_qty = table_header.createCell(9);
        receipts_qty.setCellValue(recQty);
        receipts_qty.setCellStyle(right_style);
        excel.styleBorder(receipts_qty, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        receipts_amount = table_header.createCell(10);
        receipts_amount.setCellValue(recAmount);
        receipts_amount.setCellStyle(right_style);
        excel.styleBorder(receipts_amount, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Returns
        returns_ref = table_header.createCell(11);
        returns_ref.setCellValue(" ");
        excel.styleBorder(returns_ref, 11, HorizontalAlignment.LEFT, false, false, true, true, true);

        returns_qty = table_header.createCell(12);
        returns_qty.setCellValue(retQty);
        excel.styleBorder(returns_qty, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        returns_amount = table_header.createCell(13);
        returns_amount.setCellValue(retAmount);
        excel.styleBorder(returns_amount, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Charges
        charges_qty = table_header.createCell(14);
        charges_qty.setCellValue(chgQty);
        excel.styleBorder(charges_qty, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        charges_amount = table_header.createCell(15);
        charges_amount.setCellValue(chgAmount);
        excel.styleBorder(charges_amount, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Balance
        balance_qty = table_header.createCell(16);
        balance_qty.setCellValue(endQty);
        excel.styleBorder(balance_qty, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        balance_cost = table_header.createCell(17);
        //balance_cost.setCellValue(endCost);
        balance_cost.setCellValue(" ");
        excel.styleBorder(balance_cost, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        balance_amount = table_header.createCell(18);
        balance_amount.setCellValue(endAmount);
        excel.styleBorder(balance_amount, 11, HorizontalAlignment.RIGHT, false, false, true, true, true);

        //Prepared by line
        row++;
        table_header = sheet.createRow(row);

        Cell col_prep = table_header.createCell(1);
        col_prep.setCellValue("PREPARED BY");
        excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        //Per Book Line
        row++;
        table_header = sheet.createRow(row);

        Cell col_per = table_header.createCell(5);
        col_per.setCellValue("PER BOOK");
        excel.styleBorder(col_per, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        col_per = table_header.createCell(16);
        col_per.setCellValue("PER BOOK");
        excel.styleBorder(col_per, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        //Active user who generated the report
        row++;
        table_header = sheet.createRow(row);

        col_prep = table_header.createCell(1);
        col_prep.setCellValue(ActiveUser.getUser().getFullName().toUpperCase());
        col_prep.setCellStyle(center_style);
        CellRangeAddress col_prep_addr = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(col_prep_addr);
        excel.styleMergedCells(col_prep_addr, false, false, false, false, false);

        //Difference column
        col_prep = table_header.createCell(5);
        col_prep.setCellValue("DIFFERENCE");
        excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        //Beginning amount
        col_prep = table_header.createCell(7);
        col_prep.setCellValue(begAmount);
        excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        //Receiving Report
        if (rrnos.size() > 0) {
            col_prep = table_header.createCell(11);
            col_prep.setCellValue("RECEIVED OF THE AMOUNT:");
            col_prep.setCellStyle(center_style);
            col_prep_addr = new CellRangeAddress(row, row, 11, 13);
            sheet.addMergedRegion(col_prep_addr);
            excel.styleMergedCells(col_prep_addr, false, false, false, false, false);
        }

        //Difference column
        col_prep = table_header.createCell(16);
        col_prep.setCellValue("DIFFERENCE");
        excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        //Balance amount
        col_prep = table_header.createCell(18);
        col_prep.setCellValue(endAmount);
        excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

        row++;
        table_header = sheet.createRow(row);

        //Designation
        String designation = "";
        col_prep = table_header.createCell(1);
        try {
            designation = ActiveUser.getUser().getEmployeeInfo().getDesignation();
        } catch (Exception e) {
            e.printStackTrace();
        }
        col_prep.setCellValue(designation);
        col_prep.setCellStyle(center_style);
        col_prep_addr = new CellRangeAddress(row, row, 1, 4);
        sheet.addMergedRegion(col_prep_addr);
        excel.styleMergedCells(col_prep_addr, false, false, false, false, false);

        double total_rec_amount = 0;

        //Receiving Reports for the month
        for (String rrno :rrnos) {
            row++;
            table_header = sheet.createRow(row);

            //RR No
            col_prep = table_header.createCell(11);
            col_prep.setCellValue(rrno);
            excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

            Receiving receiving = null;
            SupplierInfo supplier = null;
            List<Stock> rr_items = null;

            try {
                receiving = ReceivingDAO.get(rrno);
                supplier = SupplierDAO.get(receiving.getSupplierId());
                rr_items = ReceivingDAO.getReceivingItems(rrno);
            } catch (Exception e) {
                e.printStackTrace();
            }

            double rec_amount = 0;

            if (receiving != null) {
                for (Stock stock : rr_items) {
                    ReceivingItem receivingItem = stock.getReceivingItem();
                    rec_amount += receivingItem.getQtyAccepted() * receivingItem.getUnitCost();
                }
            }

            //Amount
            col_prep = table_header.createCell(12);
            col_prep.setCellValue(rec_amount);
            excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

            //Supplier
            col_prep = table_header.createCell(13);
            col_prep.setCellValue(supplier.getCompanyName());
            excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

            total_rec_amount += rec_amount;
        }

        if (rrnos.size() > 0) {
            row++;
            table_header = sheet.createRow(row);

            col_prep = table_header.createCell(11);
            col_prep.setCellValue("TOTAL");
            excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);

            //Total Received Amount
            col_prep = table_header.createCell(12);
            col_prep.setCellValue(total_rec_amount);
            excel.styleBorder(col_prep, 11, HorizontalAlignment.LEFT, false, false, false, false, false);
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            excel.save(out);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
