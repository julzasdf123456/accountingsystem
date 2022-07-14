package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MrItem;
import com.boheco1.dev.integratedaccountingsystem.objects.ReceivingItem;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AddMRItemController extends MenuControllerHandler implements Initializable {


    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stock_tf, qty_to_mr_tf, remarks_tf;

    @FXML
    private TableView stockTable;

    private ObservableList<SlimStock> stockItems = null;

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializes the stocks table
        this.createTable();
        //Enables the textfield to accept numbers only
        this.bindNumbers();
        //Initializes the variables
        this.reset();
        //Retrieves the parent controller so that the MR Item object can be passed to it
        this.parentController = Utility.getParentController();
        //Sets the stackpane for the dialogs/modals to display
        this.stackPane = Utility.getStackPane();
    }
    /**
     * Creates the MR item based on the field values and adds it to the parent controller table
     * @return void
     */
    @FXML
    public void addMRItem(){
        String remarks = remarks_tf.getText();
        int qty_to_add = 0;

        try {
            qty_to_add = Integer.parseInt(this.qty_to_mr_tf.getText());
        }catch (Exception e){

        }

        Object selectedItem = this.stockTable.getSelectionModel().getSelectedItem();

        if (!(selectedItem instanceof SlimStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "No item from the table was selected!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_to_add <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for MR quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);

        }else if (remarks.length() == 0 || remarks == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid remarks!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            SlimStock cstock = (SlimStock) selectedItem;

            if (qty_to_add > cstock.getQuantity()) {
                AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for MR quantity: "+qty_to_add+" > "+cstock.getQuantity()+"!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }else{
                try {
                    MrItem item = new MrItem();

                    ReceivingItem receivingItem = new ReceivingItem();
                    receivingItem.setStockId(cstock.getId());
                    receivingItem.setUnitCost(cstock.getPrice());
                    receivingItem.setQtyAccepted(cstock.getQuantity());
                    item.setPrice(cstock.getPrice());
                    item.setRrNo(cstock.getRRNo());
                    item.setQty(qty_to_add);
                    item.setStockID(cstock.getId());
                    item.getStock().setReceivingItem(receivingItem);
                    item.setItemName(cstock.getStockName());
                    item.setDescription(cstock.getDescription());
                    item.setRemarks(remarks);
                    this.parentController.receive(item);
                    this.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "A system error occurred due to: " + e.getMessage(),
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            }
        }
    }
    /**
     * Initializes the stocks table
     * @return void
     */
    public void createTable(){
        TableColumn<SlimStock, String> column0 = new TableColumn<>("RR No");
        column0.setMinWidth(100);
        column0.setCellValueFactory(new PropertyValueFactory<>("RRNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column1 = new TableColumn<>("Code");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(345);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<SlimStock, String> column8 = new TableColumn<>("Qty");
        column8.setMinWidth(25);
        column8.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column8.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column4 = new TableColumn<>("Brand");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("brand"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column5 = new TableColumn<>("Model");
        column5.setMinWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory<>("model"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column6 = new TableColumn<>("Unit");
        column6.setMinWidth(50);
        column6.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<SlimStock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(120);
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getPrice()+""));
        column7.setStyle("-fx-alignment: center-left;");

        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setPlaceholder(new Label("No stock added"));

        this.stockTable.getColumns().add(column0);
        this.stockTable.getColumns().add(column1);
        this.stockTable.getColumns().add(column3);
        this.stockTable.getColumns().add(column8);
        this.stockTable.getColumns().add(column4);
        this.stockTable.getColumns().add(column5);
        this.stockTable.getColumns().add(column6);
        this.stockTable.getColumns().add(column7);
    }
    /**
     * Sets the textfields to accepts number inputs only
     * @return void
     */
    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.qty_to_mr_tf);
    }
    /**
     * Search for stocks based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void search(){
        String key = this.stock_tf.getText();
        try {
            this.stockItems = FXCollections.observableArrayList(StockDAO.search_available_in_rr(key));
            this.stockTable.setItems(this.stockItems);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    /**
     * Resets all the field values
     * @return void
     */
    @FXML
    public void reset(){
        this.stockItems =  FXCollections.observableArrayList();
        this.stockTable.setItems(this.stockItems);
        this.stockTable.setPlaceholder(new Label("No item added!"));
        this.stock_tf.setText("");
        this.qty_to_mr_tf.setText("");
        this.remarks_tf.setText("");
    }
}
