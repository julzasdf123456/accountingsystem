package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import java.net.URL;
import java.util.ResourceBundle;

public class AddTableItemController extends MenuControllerHandler implements Initializable {


    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stock_tf, qty_delivered_tf, qty_received_tf, cost_tf;

    @FXML
    private TableView stockTable;

    private ObservableList<SlimStock> stockItems = null;

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.bindNumbers();
        this.reset();
        this.parentController = Utility.getParentController();
        this.stackPane = Utility.getStackPane();
    }

    @FXML
    public void addReceivingItem(){
        int qty_delivered = 0, qty_accepted = 0;
        double price = 0;

        try {
            qty_delivered = Integer.parseInt(this.qty_delivered_tf.getText());
            qty_accepted = Integer.parseInt(this.qty_received_tf.getText());
            price = Double.parseDouble(this.cost_tf.getText());
        }catch (Exception e){

        }
        ReceivingItem receivingItem = new ReceivingItem();

        Object selectedItem = this.stockTable.getSelectionModel().getSelectedItem();

        if (!(selectedItem instanceof SlimStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "No item from the table was selected!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_delivered <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for delivered quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_accepted <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for accepted quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_accepted > qty_delivered) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Accepted quantity should not exceed delivered quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (price <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for the item price!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            SlimStock cstock = (SlimStock) selectedItem;
            Stock stock = null;
            try {
                stock = StockDAO.get(cstock.getId());
                receivingItem.setRrNo(null);
                receivingItem.setStockId(stock.getId());
                receivingItem.setQtyDelivered(qty_delivered);
                receivingItem.setQtyAccepted(qty_accepted);
                receivingItem.setUnitCost(price);
                stock.setReceivingItem(receivingItem);
                this.parentController.receive(stock);
                this.reset();
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "A system error occurred due to: "+e.getMessage(),
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
    }

    public void createTable(){
        TableColumn<SlimStock, String> column1 = new TableColumn<>("Code");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(350);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column4 = new TableColumn<>("Brand");
        column4.setMinWidth(50);
        column4.setCellValueFactory(new PropertyValueFactory<>("brand"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column5 = new TableColumn<>("Model");
        column5.setMinWidth(50);
        column5.setCellValueFactory(new PropertyValueFactory<>("model"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column6 = new TableColumn<>("Unit");
        column6.setMinWidth(50);
        column6.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(75);
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getPrice()+""));
        column7.setStyle("-fx-alignment: center-left;");

        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setPlaceholder(new Label("No stock added"));

        this.stockTable.getColumns().add(column1);
        this.stockTable.getColumns().add(column3);
        this.stockTable.getColumns().add(column4);
        this.stockTable.getColumns().add(column5);
        this.stockTable.getColumns().add(column6);
        this.stockTable.getColumns().add(column7);
    }

    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.qty_delivered_tf);
        InputHelper.restrictNumbersOnly(this.qty_received_tf);
        InputHelper.restrictNumbersOnly(this.cost_tf);
    }
    @FXML
    public void search(){
        String key = this.stock_tf.getText();
        try {
            this.stockItems = FXCollections.observableArrayList(StockDAO.search(key, 0));
            this.stockTable.setItems(this.stockItems);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    public void reset(){
        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setItems(this.stockItems);
        this.stockTable.setPlaceholder(new Label("No item added!"));
        this.stock_tf.setText("");
        this.cost_tf.setText("");
        this.qty_delivered_tf.setText("");
        this.qty_received_tf.setText("");
    }
}
