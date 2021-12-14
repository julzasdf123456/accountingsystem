package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReceivingEntryController extends MenuControllerHandler implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField supplier_tf, bno_tf, addr_tf, carrier_tf, invoice_tf, dr_tf, rv_tf, po_tf, stock_tf, qty_delivered_tf, qty_received_tf, cost_tf,
            received_tf, received_original_tf, verified_tf, posted_tf;

    @FXML
    private TextField net_sales_tf, vat_tf, total_tf;

    @FXML
    private TableView new_stocks_table;

    @FXML
    private JFXButton addBtn;

    private JFXDialog dialog;

    private Stock currentStock = null;
    private ObservableList<Stock> receivedItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.addBtn.setDisable(true);
        this.bindAutocomplete(this.stock_tf);
        this.bindNumbers();
        this.reset();
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

        if (qty_delivered <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for delivered quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_accepted <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for accepted quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty_accepted > qty_delivered) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Accepted quantity should not exceed delivered quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (price <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Unit Cost should not be less than or equal to 0!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.currentStock == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please select a stock first before proceeding to add!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else{
            ReceivingItem receivingItem = new ReceivingItem();
            receivingItem.setRrNo(null);
            receivingItem.setStockId(currentStock.getId());
            receivingItem.setQtyDelivered(qty_delivered);
            receivingItem.setQtyAccepted(qty_accepted);
            receivingItem.setUnitCost(price);
            Stock stock = currentStock;
            stock.setReceivingItem(receivingItem);
            this.receivedItems.add(stock);
            this.new_stocks_table.setItems(this.receivedItems);
            this.credit(12, receivingItem.getQtyAccepted()*receivingItem.getUnitCost());
            this.resetAdd();
        }
    }

    @FXML
    public void addStock(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../warehouse_stock_entry.fxml"));
        Parent parent = null;
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        Label label = new Label("Add New Stock");
        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 18));
        label.setWrapText(true);
        label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
        dialogLayout.setHeading(label);
        dialogLayout.setBody(new AnchorPane(parent));
        JFXButton cancel = new JFXButton("Close");
        cancel.setDefaultButton(true);
        cancel.setMinWidth(75);
        cancel.setOnAction(event -> dialog.close());
        dialogLayout.setActions(cancel);
        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
        dialog.show();
    }

    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Code");
        column1.setMinWidth(50);
        column1.setCellValueFactory(new PropertyValueFactory<>("localCode"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column2 = new TableColumn<>("Stock");
        column2.setMinWidth(110);
        column2.setCellValueFactory(new PropertyValueFactory<>("stockName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(250);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column4 = new TableColumn<>("Unit");
        column4.setMinWidth(50);
        column4.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column5 = new TableColumn<>("Delivered");
        column5.setMinWidth(75);
        column5.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyDelivered()+""));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column6 = new TableColumn<>("Accepted");
        column6.setMinWidth(75);
        column6.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted()+""));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column7.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column8 = new TableColumn<>("Amount");
        column8.setMinWidth(100);
        column8.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted() * stocks.getValue().getPrice() +""));
        column8.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column9 = new TableColumn<>("Action");
        column9.setMinWidth(100);
        Callback<TableColumn<Stock, String>, TableCell<Stock, String>> removeBtn
                = //
                new Callback<TableColumn<Stock, String>, TableCell<Stock, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Stock, String> param) {
                        final TableCell<Stock, String> cell = new TableCell<Stock, String>() {

                            Button btn = new Button("");
                            FontIcon icon = new FontIcon("mdi2d-delete");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                btn.setStyle("-fx-background-color: #f44336");
                                btn.setGraphic(icon);
                                btn.setGraphicTextGap(5);
                                btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        Stock selected_stock = getTableView().getItems().get(getIndex());
                                        ReceivingItem receivingItem = selected_stock.getReceivingItem();
                                        double amount = receivingItem.getQtyAccepted() * receivingItem.getUnitCost();
                                        credit(12, -amount);
                                        try {
                                            receivedItems.remove(selected_stock);
                                            new_stocks_table.setItems(receivedItems);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        column9.setCellFactory(removeBtn);
        column9.setStyle("-fx-alignment: center;");

        this.receivedItems =  FXCollections.observableArrayList();
        this.new_stocks_table.setPlaceholder(new Label("No item added"));

        this.new_stocks_table.getColumns().add(column1);
        this.new_stocks_table.getColumns().add(column2);
        this.new_stocks_table.getColumns().add(column3);
        this.new_stocks_table.getColumns().add(column4);
        this.new_stocks_table.getColumns().add(column5);
        this.new_stocks_table.getColumns().add(column6);
        this.new_stocks_table.getColumns().add(column7);
        this.new_stocks_table.getColumns().add(column8);
        this.new_stocks_table.getColumns().add(column9);
    }

    public void credit(double tax, double amount){
        double net_sales = 0, vat = 0, total = 0;
        try{
            net_sales = Double.parseDouble(this.net_sales_tf.getText());
            net_sales += amount;
            vat =  net_sales * (tax/100);
            total += vat + net_sales;
        }catch (Exception e){

        }
        this.net_sales_tf.setText(net_sales+"");
        this.vat_tf.setText(vat+"");
        this.total_tf.setText(total+"");
    }

    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.qty_delivered_tf);
        InputHelper.restrictNumbersOnly(this.qty_received_tf);
        InputHelper.restrictNumbersOnly(this.cost_tf);
    }

    public void bindAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SlimStock> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SlimStock> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.search(query, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        currentStock = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SlimStock object) {
                        return object.getStockName();
                    }

                    @Override
                    public SlimStock fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            SlimStock result = event.getCompletion();
            try {
                currentStock = StockDAO.get(result.getId());
                this.stock_tf.setText(currentStock.getStockName());
                this.cost_tf.setText(currentStock.getPrice()+"");
                this.addBtn.setDisable(false);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", e.getMessage(), this.stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    public void resetAdd(){
        this.addBtn.setDisable(true);
        this.stock_tf.setText("");
        this.cost_tf.setText("");
        this.qty_delivered_tf.setText("");
        this.qty_received_tf.setText("");
        this.currentStock = null;
    }

    @FXML
    public void reset(){
        this.receivedItems =  FXCollections.observableArrayList();
        this.new_stocks_table.setItems(this.receivedItems);
        this.new_stocks_table.setPlaceholder(new Label("No item added!"));
        this.total_tf.setText("0");
        this.vat_tf.setText("0");
        this.net_sales_tf.setText("0");
        this.currentStock = null;
    }
}
