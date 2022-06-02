package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.*;
//For Delete
public class CriticalStockController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private TableView<Stock> stocksTable;

    @FXML
    private Label num_stocks_lbl;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private int LIMIT = Utility.ROW_PER_PAGE;
    private int COUNT = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bindPages();
        this.setCriticalCount();
        this.initializeCriticalStocks();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void createTable(){
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
        this.stocksTable.getColumns().removeAll();
        this.stocksTable.getColumns().add(column2);
        this.stocksTable.getColumns().add(column3);
        this.stocksTable.getColumns().add(column4);
        this.stocksTable.getColumns().add(column5);
        this.stocksTable.getColumns().add(column7);
        this.stocksTable.getColumns().add(column6);
    }

    public void initializeCriticalStocks(){
        Platform.runLater(() -> {
            try {
                this.createTable();
                ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getCritical(LIMIT, 0));
                this.stocksTable.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void bindPages(){
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countCritical();
            } catch (Exception e) {
                e.printStackTrace();
            }

            double div = COUNT / LIMIT;
            double pages = Math.ceil(div);

            if (COUNT % LIMIT > 0)
                pages++;

            this.page_cb.getItems().clear();
            for (int i = 1; i <= pages; i++) {
                this.page_cb.getItems().add(i);
            }

            this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    try {
                        if (!page_cb.getSelectionModel().isEmpty()) {
                            int offset = (page_cb.getSelectionModel().getSelectedItem() - 1) * LIMIT;
                            ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getCritical(LIMIT, offset));
                            this.stocksTable.getItems().setAll(stocks);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void setCriticalCount() {
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countCritical();
                this.num_stocks_lbl.setText(COUNT +" Critical Items");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
