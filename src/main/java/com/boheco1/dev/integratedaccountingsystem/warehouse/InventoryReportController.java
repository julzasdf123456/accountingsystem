package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXComboBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class InventoryReportController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private StackPane stackPane;

    @FXML
    private DatePicker to_dp, from_dp;

    @FXML
    private TableView stocksTable;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private int LIMIT = 3;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
    }

    @FXML
    public void generateReport() {

        LocalDate from = from_dp.getValue();
        LocalDate to = to_dp.getValue();

        if (from == null){
            AlertDialogBuilder.messgeDialog("System Warning", "Please select a valid start date!",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else if (to == null) {
            AlertDialogBuilder.messgeDialog("System Warning", "Please select a valid end date!",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {
            /*
            try {
                ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getInventory(from, to));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
            */
        }
    }

    @FXML
    public void downloadReport() {

    }

    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Stock Name");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockName"));

        TableColumn<Stock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(300);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Stock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Stock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Stock, String> column5 = new TableColumn<>("Unit");
        column5.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Stock, String> column6 = new TableColumn<>("Quantity");
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));

        this.stocksTable.getColumns().add(column1);
        this.stocksTable.getColumns().add(column2);
        this.stocksTable.getColumns().add(column3);
        this.stocksTable.getColumns().add(column4);
        this.stocksTable.getColumns().add(column5);
        this.stocksTable.getColumns().add(column7);
        this.stocksTable.getColumns().add(column6);
    }

    public void bindPages(){
        try {
            COUNT = StockDAO.countStocks();
        } catch (Exception e) {
            e.printStackTrace();
        }

        double div = COUNT/LIMIT;
        double pages = Math.ceil(div);

        if (COUNT % LIMIT >0 )
            pages++;

        this.page_cb.getItems().clear();
        for (int i = 1; i <= pages; i++){
            this.page_cb.getItems().add(i);
        }

        this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            try {
                int offset = (page_cb.getSelectionModel().getSelectedItem()-1)*LIMIT;
                ObservableList<SlimStock> stocks = FXCollections.observableList(StockDAO.getSlimStockList(LIMIT, offset, 0));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
