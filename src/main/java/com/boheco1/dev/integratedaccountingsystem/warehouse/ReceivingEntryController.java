package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
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
import java.time.LocalDate;
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

    private User received = null, received_original = null, verified = null, posted = null;
    private SupplierInfo supplier = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.createTable();
        this.addBtn.setDisable(true);
        this.bindSupplierAutocomplete(this.supplier_tf);
        this.bindStockAutocomplete(this.stock_tf);
        this.bindUserAutocomplete(this.received_tf);
        this.bindUserAutocomplete(this.received_original_tf);
        this.bindUserAutocomplete(this.verified_tf);
        this.bindUserAutocomplete(this.posted_tf);
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
        }else if (this.supplier == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set a supplier first before proceeding to add item!",
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
    public void addEntry(){
        //Check if supplier was selected
        if (this.supplier == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set a supplier first before proceeding to add item!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        //Check if items were added
        }else if (this.receivedItems.size() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add stock items!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);

        //Check if an employee for received by was set
        }else if (this.received == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the received by field!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);

        //Check if an employee for received original by was set
        }else if (this.received_original == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the received original by field!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);

        //Check if an employee for verified by was set
        }else if (this.verified == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the verified by field!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        /*
        }else if (this.posted == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the posted to bin card by field!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);*/
        }else{
            String rv_no = this.rv_tf.getText();
            String blbw_no = this.bno_tf.getText();
            String carrier = this.carrier_tf.getText();
            String dr_no = this.dr_tf.getText();
            String po_no = this.po_tf.getText();
            String inv_no = this.invoice_tf.getText();

            //Create Receiving object
            Receiving receiving = new Receiving();
            receiving.setDate(LocalDate.now());
            receiving.setRvNo(rv_no);
            receiving.setBlwbNo(blbw_no);
            receiving.setCarrier(carrier);
            receiving.setDrNo(dr_no);
            receiving.setPoNo(po_no);
            receiving.setSupplierId(this.supplier.getSupplierID());
            receiving.setInvoiceNo(inv_no);
            receiving.setReceivedBy(this.received.getEmployeeID());
            receiving.setReceivedOrigBy(this.received_original.getEmployeeID());
            receiving.setVerifiedBy(this.verified.getEmployeeID());
            //receiving.setPostedBinCardBy(this.posted.getEmployeeID());

            try {
                //Add Receiving
                ReceivingDAO.add(receiving);

                //List all Receiving Items
                List<ReceivingItem> items = new ArrayList<>();
                for (int i = 0; i < this.receivedItems.size(); i++) {
                    items.add(this.receivedItems.get(i).getReceivingItem());
                }

                //Add Receiving Items
                ReceivingDAO.addItems(receiving.getRrNo(), items);

                //Insert each Receiving Item as StockEntryLog
                for (ReceivingItem item : items){

                    //Create stock
                    Stock stock = StockDAO.get(item.getStockId());

                    //Create StockEntryLog object
                    StockEntryLog stockEntryLog = new StockEntryLog();
                    stockEntryLog.setQuantity(item.getQtyAccepted());
                    stockEntryLog.setSource("Purchased");
                    stockEntryLog.setPrice(item.getUnitCost());

                    //set the Entry Log RRNo
                    stockEntryLog.setRrNo(receiving.getRrNo());

                    //Insert StockEntryLog to database
                    StockDAO.stockEntry(stock, stockEntryLog);
                }
                AlertDialogBuilder.messgeDialog("Receiving and Stock Entry", "Receiving Entry, Received Items and Stock Entry Logs successfully added!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                this.reset();
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "New Receiving Entry was not successfully added due to:"+e.getMessage()+" error.", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
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
        column1.setMinWidth(25);
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
        column5.setMinWidth(50);
        column5.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyDelivered()+""));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column6 = new TableColumn<>("Accepted");
        column6.setMinWidth(50);
        column6.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted()+""));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(50);
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getUnitCost()+""));
        column7.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column8 = new TableColumn<>("Amount");
        column8.setMinWidth(50);
        column8.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted() * stocks.getValue().getReceivingItem().getUnitCost() +""));
        column8.setStyle("-fx-alignment: center-left;");

        TableColumn<Stock, String> column9 = new TableColumn<>("Action");
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
            if (this.supplier.getTaxType().equals("VAT"))
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

    public void bindStockAutocomplete(JFXTextField textField){
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

    public void bindSupplierAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SupplierInfo> supplierSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SupplierInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = SupplierDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        this.supplier = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SupplierInfo object) {
                        return object.getCompanyName();
                    }

                    @Override
                    public SupplierInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        supplierSuggest.setOnAutoCompleted(event -> {
            SupplierInfo result = event.getCompletion();
            this.supplier = result;
            this.supplier_tf.setText(this.supplier.getCompanyName());
            this.addr_tf.setText(this.supplier.getCompanyAddress()+"");
        });
    }

    public void bindUserAutocomplete(JFXTextField textField){
        AutoCompletionBinding<User> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<User> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = UserDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        if (textField == this.received_tf) {
                            this.received = null;
                        }else if (textField == this.received_original_tf) {
                            this.received_original = null;
                        }else if (textField == this.verified_tf) {
                            this.verified = null;
                        }else if (textField == this.posted_tf){
                            this.posted = null;
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(User object) {
                        return object.getFullName();
                    }

                    @Override
                    public User fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            User user = event.getCompletion();
            textField.setText(user.getFullName());
            if (textField == this.received_tf) {
                this.received = user;
            }else if (textField == this.received_original_tf) {
                this.received_original = user;
            }else if (textField == this.verified_tf) {
                this.verified = user;
            }else if (textField == this.posted_tf){
                this.posted = user;
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

    public void clear(){
        this.receivedItems =  FXCollections.observableArrayList();
        this.new_stocks_table.setItems(this.receivedItems);
        this.new_stocks_table.setPlaceholder(new Label("No item added!"));

        this.supplier_tf.setText("");
        this.bno_tf.setText("");
        this.addr_tf.setText("");
        this.carrier_tf.setText("");
        this.invoice_tf.setText("");
        this.dr_tf.setText("");
        this.rv_tf.setText("");
        this.po_tf.setText("");
        this.received_tf.setText("");
        this.received_original_tf.setText("");
        this.verified_tf.setText("");
        this.posted_tf.setText("");

        this.resetAdd();

        this.total_tf.setText("0");
        this.vat_tf.setText("0");
        this.net_sales_tf.setText("0");

        this.supplier = null;
        this.received = null;
        this.received_original = null;
        this.verified = null;
        this.posted = null;
    }

    @FXML
    public void reset(){
        clear();
    }
}
