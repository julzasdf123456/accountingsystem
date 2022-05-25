package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ExcelBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.objects.StockEntryLog;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class StockEntryReportController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private DatePicker to_dp, from_dp;

    @FXML
    private TableView stocksTable;

    @FXML
    private JFXComboBox<Integer> page_cb;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private JFXButton downloadReport_btn;

    private int LIMIT = HomeController.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.progressBar.setVisible(false);
    }

    @FXML
    public void generateReport() {

        LocalDate from = from_dp.getValue();
        LocalDate to = to_dp.getValue();


        if (from == null) {
            AlertDialogBuilder.messgeDialog("System Information", "Please select a valid start date!",
                stackPane, AlertDialogBuilder.INFO_DIALOG);
        } else if (to == null) {AlertDialogBuilder.messgeDialog("System Information", "Please select a valid end date!",
                stackPane, AlertDialogBuilder.INFO_DIALOG);
        } else {
            Platform.runLater(() -> {
                try {
                    ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getStockEntries(from, to));
                    this.bindPages(stocks.size());
                    this.stocksTable.getItems().setAll(stocks);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @FXML
    public void downloadReport() {
        LocalDate from = from_dp.getValue();
        LocalDate to = to_dp.getValue();

        if (from == null){
            AlertDialogBuilder.messgeDialog("System Information", "Please select a valid start date!",
                    stackPane, AlertDialogBuilder.INFO_DIALOG);
        }else if (to == null) {
            AlertDialogBuilder.messgeDialog("System Information", "Please select a valid end date!",
                    stackPane, AlertDialogBuilder.INFO_DIALOG);
        }else {

            Stage stage = (Stage) anchorPane.getScene().getWindow();
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
            );
            fileChooser.setInitialFileName("stock_entry_report_"+LocalDate.now()+".xlsx");
            File selectedFile = fileChooser.showSaveDialog(stage);
            if (selectedFile != null) {
                // Create a background Task
                Task<Void> task = new Task<>() {
                    @Override
                    protected Void call() throws Exception {
                        try (OutputStream fileOut = new FileOutputStream(selectedFile)) {

                            //Create the ExcelBuilder
                            ExcelBuilder doc = new ExcelBuilder(10);

                            doc.setTitle("Stock Entry Report");

                            String names[] = {ActiveUser.getUser().getFullName().toUpperCase(Locale.ROOT), "", ""};
                            doc.setNames(names);

                            String designations[] = {"Warehouse Personnel", "Head, Warehouse", "General Manager"};
                            doc.setDesignations(designations);

                            //Set the margin
                            doc.setMargin(1, 0.5, 1, 0.5);

                            //Create the header
                            doc.createHeader();

                            //Create the title
                            doc.createTitle(5, "Period of " + from.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")) + " - " + to.format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), false);

                            Sheet sheet = doc.getSheet();

                            //Create the table header
                            int row = 9;
                            Font font = doc.getWb().createFont();
                            font.setFontHeightInPoints((short) 11);
                            font.setFontName("Arial");
                            CellStyle style = doc.getWb().createCellStyle();
                            style.setAlignment(HorizontalAlignment.CENTER);
                            style.setFont(font);

                            Row table_header = sheet.createRow(row);

                            Cell entry_cell = table_header.createCell(0);
                            entry_cell.setCellValue("DATE");
                            doc.styleBorder(entry_cell, 11, HorizontalAlignment.CENTER, false);

                            Cell stock_cell = table_header.createCell(1);
                            stock_cell.setCellValue("STOCK NAME");
                            CellRangeAddress stock_cell_addr = new CellRangeAddress(row, row, 1, 2);
                            stock_cell.setCellStyle(style);
                            sheet.addMergedRegion(stock_cell_addr);
                            doc.styleMergedCells(stock_cell_addr);

                            Cell desc_cell = table_header.createCell(3);
                            desc_cell.setCellValue("DESCRIPTION");
                            CellRangeAddress desc_cell_addr = new CellRangeAddress(row, row, 3, 6);
                            desc_cell.setCellStyle(style);
                            sheet.addMergedRegion(desc_cell_addr);
                            doc.styleMergedCells(desc_cell_addr);

                            Cell unit_cell = table_header.createCell(7);
                            unit_cell.setCellValue("UNIT");
                            doc.styleBorder(unit_cell, 11, HorizontalAlignment.CENTER, false);

                            Cell price_cell = table_header.createCell(8);
                            price_cell.setCellValue("PRICE");
                            doc.styleBorder(price_cell, 11, HorizontalAlignment.CENTER, false);

                            Cell qty_cell = table_header.createCell(9);
                            qty_cell.setCellValue("QTY");
                            doc.styleBorder(qty_cell, 11, HorizontalAlignment.CENTER, false);

                            //Get stock entries from database
                            List<Stock> stocks = StockDAO.getStockEntries(from, to);

                            int stocks_row = row + 1;

                            Font font2 = doc.getWb().createFont();
                            font2.setFontHeightInPoints((short) 10);
                            font2.setFontName("Arial");
                            CellStyle style2 = doc.getWb().createCellStyle();
                            style2.setAlignment(HorizontalAlignment.LEFT);
                            style2.setFont(font2);

                            Row row_header = sheet.createRow(stocks_row);
                            Cell current_entry, current_stock, current_desc, current_unit, current_price, current_qty;
                            CellRangeAddress sname_addr, sdec_addr;

                            //For every stock, set the stock data in the appropriate cells per row
                            for (Stock stock : stocks) {
                                StockEntryLog log = stock.getEntryLog();

                                row += 1;
                                row_header = sheet.createRow(row);

                                current_entry = row_header.createCell(0);
                                current_entry.setCellValue(log.getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
                                doc.styleBorder(current_entry, 10, HorizontalAlignment.CENTER, false);

                                current_stock = row_header.createCell(1);
                                current_stock.setCellValue(stock.getStockName());
                                sname_addr = new CellRangeAddress(row, row, 1, 2);
                                current_stock.setCellStyle(style2);
                                sheet.addMergedRegion(sname_addr);
                                doc.styleMergedCells(sname_addr);

                                current_desc = row_header.createCell(3);
                                current_desc.setCellValue(stock.getDescription());
                                sdec_addr = new CellRangeAddress(row, row, 3, 6);
                                current_desc.setCellStyle(style2);
                                sheet.addMergedRegion(sdec_addr);
                                doc.styleMergedCells(sdec_addr);

                                current_unit = row_header.createCell(7);
                                current_unit.setCellValue(stock.getUnit());
                                doc.styleBorder(current_unit, 10, HorizontalAlignment.CENTER, false);

                                current_price = row_header.createCell(8);
                                current_price.setCellValue(log.getPrice());
                                doc.styleBorder(current_price, 10, HorizontalAlignment.LEFT, false);

                                current_qty = row_header.createCell(9);
                                current_qty.setCellValue(log.getQuantity());
                                doc.styleBorder(current_qty, 10, HorizontalAlignment.LEFT, false);
                            }
                            //Create the signatorees
                            doc.createSignatorees(stocks.size() + 13);

                            //Save the excel file
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
                    AlertDialogBuilder.messgeDialog("Stock Entry Report", "Stock Entry Report was successfully generated and saved on file!",
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
    }

    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Stock Name");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockName"));

        TableColumn<Stock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(300);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Stock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Stock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(175);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Stock, String> column5 = new TableColumn<>("Unit");
        column5.setMinWidth(75);
        column5.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column6 = new TableColumn<>("Quantity");
        column6.setMinWidth(75);
        column6.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getEntryLog().getQuantity()+""));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getEntryLog().getPrice()+""));
        column7.setMinWidth(75);

        TableColumn<Stock, String> column8 = new TableColumn<>("Entry Date");
        column8.setCellValueFactory(stockStringCellDataFeatures -> new SimpleStringProperty(stockStringCellDataFeatures.getValue().getEntryLog().getCreatedAt().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        column8.setStyle("-fx-alignment: center;");

        this.stocksTable.getColumns().removeAll();

        this.stocksTable.getColumns().add(column8);
        this.stocksTable.getColumns().add(column1);
        this.stocksTable.getColumns().add(column2);
        this.stocksTable.getColumns().add(column3);
        this.stocksTable.getColumns().add(column4);
        this.stocksTable.getColumns().add(column5);
        this.stocksTable.getColumns().add(column7);
        this.stocksTable.getColumns().add(column6);
        this.stocksTable.setPlaceholder(new Label("No period selected!"));
    }

    public void bindPages(int count){

        double div = count/LIMIT;
        double pages = Math.ceil(div);

        if (count % LIMIT >0 )
            pages++;

        this.page_cb.getItems().clear();
        for (int i = 1; i <= pages; i++){
            this.page_cb.getItems().add(i);
        }

        this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            LocalDate from = from_dp.getValue();
            LocalDate to = to_dp.getValue();
            Platform.runLater(() -> {
                try {
                    if (!page_cb.getSelectionModel().isEmpty() && from != null && to != null ) {
                        int offset = (page_cb.getSelectionModel().getSelectedItem()-1)*LIMIT;
                        ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getStockEntries(from, to, LIMIT, offset));
                        this.stocksTable.getItems().setAll(stocks);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }

    public void showProgressBar(boolean show){
        this.downloadReport_btn.setDisable(show);
        this.progressBar.setVisible(show);
    }

}
