package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
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

public class InventoryReportController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private TableView stocksTable;

    @FXML
    private JFXComboBox<Integer> page_cb;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private JFXButton downloadReport_btn;

    private int LIMIT = Utility.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.progressBar.setProgress(ProgressBar.INDETERMINATE_PROGRESS);
        this.progressBar.setVisible(false);
    }

    @FXML
    public void generateReport() {
        Platform.runLater(() -> {
            try {
                ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getInventory());
                this.bindPages(stocks.size());
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void downloadReport() {
        Stage stage = (Stage) anchorPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("inventory_"+LocalDate.now()+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {

            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (OutputStream fileOut = new FileOutputStream(selectedFile)) {
                        showProgressBar(true);
                        ExcelBuilder doc = new ExcelBuilder(10);

                        doc.setTitle("Inventory Report");

                        String names[] = {ActiveUser.getUser().getFullName().toUpperCase(Locale.ROOT), "", ""};
                        doc.setNames(names);

                        String designations[] = {"Warehouse Personnel", "Head, Warehouse", "General Manager"};
                        doc.setDesignations(designations);

                        doc.setMargin(1, 0.5, 1, 0.5);

                        doc.createHeader();
                        doc.createTitle(5, "As of " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")), false);

                        Sheet sheet = doc.getSheet();

                        int row = 9;
                        Row table_header = sheet.createRow(row);

                        Cell stock_cell = table_header.createCell(0);
                        stock_cell.setCellValue("CODE NO");
                        CellRangeAddress stock_cell_addr = new CellRangeAddress(row, row, 0, 1);
                        sheet.addMergedRegion(stock_cell_addr);
                        doc.styleMergedCells(stock_cell_addr);

                        Cell desc_cell = table_header.createCell(2);
                        desc_cell.setCellValue("ARTICLE");
                        CellRangeAddress desc_cell_addr = new CellRangeAddress(row, row, 2, 6);
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

                        List<Stock> stocks = StockDAO.getInventory();

                        int stocks_row = row + 1;
                        Row row_header = sheet.createRow(stocks_row);
                        Cell current_stock, current_desc, current_unit, current_price, current_qty;
                        CellRangeAddress sname_addr, sdec_addr;

                        for (Stock stock : stocks) {
                            row += 1;
                            row_header = sheet.createRow(row);

                            current_stock = row_header.createCell(0);
                            current_stock.setCellValue(stock.getId());
                            sname_addr = new CellRangeAddress(row, row, 0, 1);
                            sheet.addMergedRegion(sname_addr);
                            doc.styleMergedCells(sname_addr);

                            current_desc = row_header.createCell(2);
                            current_desc.setCellValue(stock.getDescription());
                            sdec_addr = new CellRangeAddress(row, row, 2, 6);
                            sheet.addMergedRegion(sdec_addr);
                            doc.styleMergedCells(sdec_addr);

                            current_unit = row_header.createCell(7);
                            current_unit.setCellValue(stock.getUnit());
                            doc.styleBorder(current_unit, 10, HorizontalAlignment.CENTER, false);

                            current_price = row_header.createCell(8);
                            current_price.setCellValue(stock.getPrice());
                            doc.styleBorder(current_price, 10, HorizontalAlignment.LEFT, false);

                            current_qty = row_header.createCell(9);
                            current_qty.setCellValue(stock.getQuantity());
                            doc.styleBorder(current_qty, 10, HorizontalAlignment.LEFT, false);
                        }

                        //Create signatorees
                        doc.createSignatorees(stocks.size() + 13);

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
                AlertDialogBuilder.messgeDialog("Inventory Report", "Inventory Report was successfully generated and saved on file!",
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

    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(125);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Stock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(400);
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
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));

        this.stocksTable.getColumns().removeAll();

        this.stocksTable.getColumns().add(column1);
        this.stocksTable.getColumns().add(column2);
        this.stocksTable.getColumns().add(column3);
        this.stocksTable.getColumns().add(column4);
        this.stocksTable.getColumns().add(column5);
        this.stocksTable.getColumns().add(column7);
        this.stocksTable.getColumns().add(column6);

        this.stocksTable.setPlaceholder(new Label("Click button to generate report!"));
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
            Platform.runLater(() -> {
                try {
                    if (!page_cb.getSelectionModel().isEmpty()) {
                        int offset = (page_cb.getSelectionModel().getSelectedItem()-1)*LIMIT;
                        ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getInventory(LIMIT, offset));
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
