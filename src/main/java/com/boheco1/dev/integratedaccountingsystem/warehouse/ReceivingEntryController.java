package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ReceivingEntryController extends MenuControllerHandler implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;


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

                    //Update the Stock table's price to latest received item unit cost
                    stock.setPrice(item.getUnitCost());
                    StockDAO.updateDetails(stock);

                    //Create StockEntryLog object
                    StockEntryLog stockEntryLog = new StockEntryLog();
                    stockEntryLog.setQuantity(item.getQtyAccepted());
                    stockEntryLog.setSource("Purchased");
                    stockEntryLog.setPrice(item.getUnitCost());

                    //set the Entry Log RRNo
                    stockEntryLog.setRrNo(receiving.getRrNo());

                    //Insert StockEntryLog to database and update the Stock's quantity
                    StockDAO.stockEntry(stock, stockEntryLog);
                }

                this.saveReport(receiving.getRrNo());
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

                    //Perform DB query when length of search string is 3 or above
                    if (query.length() > 2){
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
                        return object.getDescription();
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
                this.stock_tf.setText(currentStock.getDescription());
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

                    //Perform DB query when length of search string is 3 or above
                    if (query.length() > 2){
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

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 1){
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

    public void saveReport(String rrno){

        Stage stage = (Stage) contentPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xlsx")
        );
        fileChooser.setInitialFileName("receiving_report_"+LocalDate.now()+".xlsx");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {

            // Create a background Task
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws Exception {
                    try (OutputStream fileOut = new FileOutputStream(selectedFile)) {
                        Receiving receiving = ReceivingDAO.get(rrno);

                        String rrno = receiving.getRrNo();

                        SupplierInfo supplier = SupplierDAO.get(receiving.getSupplierId());

                        ExcelBuilder doc = new ExcelBuilder(10);

                        CellStyle style = doc.getWb().createCellStyle();
                        org.apache.poi.ss.usermodel.Font font = doc.getWb().createFont();
                        font.setFontHeightInPoints((short) 10);
                        font.setFontName("Arial");
                        style.setFont(font);

                        doc.setTitle("Receiving Report");
                        User received_by = UserDAO.get(receiving.getReceivedBy());
                        User received_orig_by = UserDAO.get(receiving.getReceivedOrigBy());
                        User verified_by = UserDAO.get(receiving.getVerifiedBy());
                        String names[] = {
                                received_by.getEmployeeInfo().getFullName(),
                                received_orig_by.getEmployeeInfo().getFullName(),
                                verified_by.getEmployeeInfo().getFullName(),
                                " "};
                        doc.setNames(names);

                        String designations[] = {
                                received_by.getEmployeeInfo().getDesignation(),
                                received_orig_by.getEmployeeInfo().getDesignation(),
                                verified_by.getEmployeeInfo().getDesignation(),
                                "Stock Clerk"};
                        doc.setDesignations(designations);

                        String types[] = {"Received by", "Received Original by", "Verified by", "Posted to Bin Card by"};
                        doc.setTypes(types);

                        doc.setMargin(1, 0.5, 1, 0.5);

                        doc.createHeader();
                        doc.createTitle(5, "", true);

                        Sheet sheet = doc.getSheet();

                        //RR NO and Date Section
                        int rr_head_row = 7;
                        Row rr_row = sheet.createRow(rr_head_row);
                        org.apache.poi.ss.usermodel.Cell rrno_lbl_cell = rr_row.createCell(7);
                        rrno_lbl_cell.setCellValue("RR NO:");
                        rrno_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell rrno_cell = rr_row.createCell(8);
                        rrno_cell.setCellValue(receiving.getRrNo());
                        CellRangeAddress rrno_cell_addr = new CellRangeAddress(rr_head_row, rr_head_row, 8, 9);
                        sheet.addMergedRegion(rrno_cell_addr);
                        rrno_cell.setCellStyle(style);

                        int date_head_row = rr_head_row + 1;
                        Row date_row = sheet.createRow(date_head_row);
                        org.apache.poi.ss.usermodel.Cell date_lbl_cell = date_row.createCell(7);
                        date_lbl_cell.setCellValue("DATE:");
                        date_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell date_cell = date_row.createCell(8);
                        date_cell.setCellValue(receiving.getDate().format(DateTimeFormatter.ofPattern("MMM. dd, yyyy")));
                        CellRangeAddress date_cell_addr = new CellRangeAddress(date_head_row, date_head_row, 8, 9);
                        sheet.addMergedRegion(date_cell_addr);
                        date_cell.setCellStyle(style);

                        //RR Information Section
                        //Supplier
                        int info_head_row = date_head_row + 2;
                        Row info_row = sheet.createRow(info_head_row);
                        org.apache.poi.ss.usermodel.Cell supplier_lbl_cell = info_row.createCell(0);
                        supplier_lbl_cell.setCellValue("Supplier:");
                        CellRangeAddress supplier_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 0, 1);
                        sheet.addMergedRegion(supplier_lbl_cell_addr);
                        supplier_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell supplier_cell = info_row.createCell(2);
                        supplier_cell.setCellValue(supplier.getCompanyName());
                        CellRangeAddress supplier_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 2, 5);
                        sheet.addMergedRegion(supplier_cell_addr);
                        CellStyle style2 = doc.getWb().createCellStyle();
                        style2.setFont(font);
                        style2.setWrapText(true);
                        supplier_cell.setCellStyle(style2);

                        //BLWNO
                        org.apache.poi.ss.usermodel.Cell blno_lbl_cell = info_row.createCell(6);
                        blno_lbl_cell.setCellValue("B/L-W/B-AW/B-NO:");
                        CellRangeAddress blno_lbl_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 6, 7);
                        sheet.addMergedRegion(blno_lbl_cell_addr);
                        blno_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell blno_cell = info_row.createCell(8);
                        blno_cell.setCellValue(receiving.getBlwbNo());
                        CellRangeAddress blno_cell_addr = new CellRangeAddress(info_head_row, info_head_row, 8, 9);
                        sheet.addMergedRegion(blno_cell_addr);
                        blno_cell.setCellStyle(style);

                        //Address
                        int addrress_head_row = info_head_row + 1;
                        Row addr_row = sheet.createRow(addrress_head_row);
                        org.apache.poi.ss.usermodel.Cell addr_lbl_cell = addr_row.createCell(0);
                        addr_lbl_cell.setCellValue("Address:");
                        CellRangeAddress addr_lbl_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 0, 1);
                        sheet.addMergedRegion(addr_lbl_cell_addr);
                        addr_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell addr_cell = addr_row.createCell(2);
                        addr_cell.setCellValue(supplier.getCompanyAddress());
                        CellRangeAddress addr_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 2, 5);
                        sheet.addMergedRegion(addr_cell_addr);
                        addr_cell.setCellStyle(style2);

                        //Carrier
                        org.apache.poi.ss.usermodel.Cell carrier_lbl_cell = addr_row.createCell(6);
                        carrier_lbl_cell.setCellValue("Carrier:");
                        CellRangeAddress carrier_cell_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 6, 7);
                        sheet.addMergedRegion(carrier_cell_addr);
                        carrier_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell carrier_cell = addr_row.createCell(8);
                        carrier_cell.setCellValue(receiving.getCarrier());
                        CellRangeAddress carrier_addr = new CellRangeAddress(addrress_head_row, addrress_head_row, 8, 9);
                        sheet.addMergedRegion(carrier_addr);
                        carrier_cell.setCellStyle(style);

                        //Invoice No
                        int invoice_head_row = addrress_head_row + 1;
                        Row invoice_row = sheet.createRow(invoice_head_row);
                        org.apache.poi.ss.usermodel.Cell invoice_lbl_cell = invoice_row.createCell(0);
                        invoice_lbl_cell.setCellValue("Invoice No:");
                        CellRangeAddress invoice_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 0, 1);
                        sheet.addMergedRegion(invoice_lbl_cell_addr);
                        invoice_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell invoice_cell = invoice_row.createCell(2);
                        invoice_cell.setCellValue(receiving.getInvoiceNo());
                        CellRangeAddress invoice_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 2, 5);
                        sheet.addMergedRegion(invoice_cell_addr);
                        invoice_cell.setCellStyle(style);

                        //DR
                        org.apache.poi.ss.usermodel.Cell dr_lbl_cell = invoice_row.createCell(6);
                        dr_lbl_cell.setCellValue("D.R. No.:");
                        CellRangeAddress dr_lbl_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 6, 7);
                        sheet.addMergedRegion(dr_lbl_cell_addr);
                        dr_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell dr_cell = invoice_row.createCell(8);
                        dr_cell.setCellValue(receiving.getDrNo());
                        CellRangeAddress dr_cell_addr = new CellRangeAddress(invoice_head_row, invoice_head_row, 8, 9);
                        sheet.addMergedRegion(dr_cell_addr);
                        dr_cell.setCellStyle(style);

                        //RV No
                        int rv_head_row = invoice_head_row + 1;
                        Row rv_row = sheet.createRow(rv_head_row);
                        org.apache.poi.ss.usermodel.Cell rv_lbl_cell = rv_row.createCell(0);
                        rv_lbl_cell.setCellValue("R.V. No:");
                        CellRangeAddress rv_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 0, 1);
                        sheet.addMergedRegion(rv_lbl_cell_addr);
                        rv_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell rv_cell = rv_row.createCell(2);
                        rv_cell.setCellValue(receiving.getRvNo());
                        CellRangeAddress rv_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 2, 5);
                        sheet.addMergedRegion(rv_cell_addr);
                        rv_cell.setCellStyle(style);

                        //PO No
                        org.apache.poi.ss.usermodel.Cell po_lbl_cell = rv_row.createCell(6);
                        po_lbl_cell.setCellValue("P.O. No.:");
                        CellRangeAddress po_lbl_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 6, 7);
                        sheet.addMergedRegion(po_lbl_cell_addr);
                        po_lbl_cell.setCellStyle(style);

                        org.apache.poi.ss.usermodel.Cell po_cell = rv_row.createCell(8);
                        po_cell.setCellValue(receiving.getPoNo());
                        CellRangeAddress po_cell_addr = new CellRangeAddress(rv_head_row, rv_head_row, 8, 9);
                        sheet.addMergedRegion(po_cell_addr);
                        po_cell.setCellStyle(style);

                        //Table Header Section
                        int row = rv_head_row + 2;
                        Row table_header = sheet.createRow(row);

                        org.apache.poi.ss.usermodel.Cell code_cell = table_header.createCell(0);
                        code_cell.setCellValue("CODE NO");
                        doc.styleBorder(code_cell, 10, HorizontalAlignment.CENTER, false);

                        org.apache.poi.ss.usermodel.Cell desc_cell = table_header.createCell(1);
                        desc_cell.setCellValue("ARTICLE");
                        CellRangeAddress desc_cell_addr = new CellRangeAddress(row, row, 1, 4);
                        sheet.addMergedRegion(desc_cell_addr);
                        org.apache.poi.ss.usermodel.Font desc_font = doc.getWb().createFont();
                        desc_font.setFontHeightInPoints((short) 10);
                        desc_font.setFontName("Arial");
                        CellStyle desc_style = doc.getWb().createCellStyle();
                        desc_style.setFont(desc_font);
                        desc_style.setAlignment(HorizontalAlignment.CENTER);
                        desc_cell.setCellStyle(desc_style);
                        doc.styleMergedCells(desc_cell_addr);

                        org.apache.poi.ss.usermodel.Cell unit_cell = table_header.createCell(5);
                        unit_cell.setCellValue("UNIT");
                        doc.styleBorder(unit_cell, 10, HorizontalAlignment.CENTER, false);

                        org.apache.poi.ss.usermodel.Cell qty_del_cell = table_header.createCell(6);
                        qty_del_cell.setCellValue("QTY\nDELIVERED");
                        doc.styleBorder(qty_del_cell, 10, HorizontalAlignment.CENTER, true);

                        org.apache.poi.ss.usermodel.Cell qty_cell = table_header.createCell(7);
                        qty_cell.setCellValue("QTY\nACCEPTED");
                        doc.styleBorder(qty_cell, 10, HorizontalAlignment.CENTER, true);

                        org.apache.poi.ss.usermodel.Cell cost_cell = table_header.createCell(8);
                        cost_cell.setCellValue("UNIT COST");
                        doc.styleBorder(cost_cell, 10, HorizontalAlignment.RIGHT, true);

                        org.apache.poi.ss.usermodel.Cell amount_cell = table_header.createCell(9);
                        amount_cell.setCellValue("AMOUNT");
                        doc.styleBorder(amount_cell, 10, HorizontalAlignment.CENTER, false);

                        //Table Body Section
                        List<Stock> stocks = ReceivingDAO.getReceivingItems(rrno);

                        int stocks_row = row + 1;
                        Row row_header = sheet.createRow(stocks_row);
                        org.apache.poi.ss.usermodel.Cell current_stock_code, current_desc, current_unit, current_delivered, current_accepted, current_cost, current_amount;
                        CellRangeAddress sdec_addr;

                        double net_sales = 0, total_amount = 0, vat_added = 0;

                        for (Stock stock : stocks) {
                            ReceivingItem item = stock.getReceivingItem();
                            row += 1;
                            row_header = sheet.createRow(row);

                            //LocalCode
                            current_stock_code = row_header.createCell(0);
                            current_stock_code.setCellValue(stock.getLocalCode());
                            doc.styleBorder(current_stock_code, 10, HorizontalAlignment.LEFT, false);

                            current_desc = row_header.createCell(1);
                            current_desc.setCellValue(stock.getStockName() + " " + stock.getDescription());
                            sdec_addr = new CellRangeAddress(row, row, 1, 4);
                            sheet.addMergedRegion(sdec_addr);
                            current_desc.setCellStyle(style);
                            doc.styleMergedCells(sdec_addr);

                            current_unit = row_header.createCell(5);
                            current_unit.setCellValue(stock.getUnit());
                            doc.styleBorder(current_unit, 10, HorizontalAlignment.CENTER, false);

                            current_delivered = row_header.createCell(6);
                            current_delivered.setCellValue(item.getQtyDelivered());
                            doc.styleBorder(current_delivered, 10, HorizontalAlignment.CENTER, false);

                            current_accepted = row_header.createCell(7);
                            current_accepted.setCellValue(item.getQtyAccepted());
                            doc.styleBorder(current_accepted, 10, HorizontalAlignment.CENTER, false);

                            current_cost = row_header.createCell(8);
                            current_cost.setCellValue(item.getUnitCost());
                            doc.styleBorder(current_cost, 10, HorizontalAlignment.RIGHT, false);

                            current_amount = row_header.createCell(9);
                            double amount = item.getUnitCost() * item.getQtyAccepted();
                            current_amount.setCellValue(amount);
                            doc.styleBorder(current_amount, 10, HorizontalAlignment.RIGHT, false);

                            net_sales += amount;
                        }

                        //Net Sales Section
                        int list_size = row + stocks.size();

                        //Net Sales
                        int sales = list_size;
                        Row sales_row = sheet.createRow(sales);
                        org.apache.poi.ss.usermodel.Cell net_cell = sales_row.createCell(5);
                        net_cell.setCellValue("NET SALES");
                        CellRangeAddress net_cell_addr = new CellRangeAddress(sales, sales, 5, 8);
                        sheet.addMergedRegion(net_cell_addr);
                        doc.styleMergedCells(net_cell_addr);

                        org.apache.poi.ss.usermodel.Cell sales_cell = sales_row.createCell(9);
                        sales_cell.setCellValue(net_sales);
                        doc.styleBorder(sales_cell, 10, HorizontalAlignment.RIGHT, false);

                        //VAT
                        int vat = sales + 2;
                        Row vat_row = sheet.createRow(vat);
                        org.apache.poi.ss.usermodel.Cell vat_cell = vat_row.createCell(7);
                        vat_cell.setCellValue("Add Vat 12%");

                        org.apache.poi.ss.usermodel.Cell vat_amount_cell = vat_row.createCell(9);
                        if (supplier.getTaxType().equals("VAT"))
                            vat_added = net_sales * 0.12;
                        vat_amount_cell.setCellValue(vat_added);
                        doc.styleBorder(vat_amount_cell, 10, HorizontalAlignment.RIGHT, false);

                        //Total Amount
                        int total = vat + 2;
                        Row total_row = sheet.createRow(total);
                        org.apache.poi.ss.usermodel.Cell total_cell = total_row.createCell(5);
                        total_cell.setCellValue("TOTAL AMOUNT");
                        CellRangeAddress total_cell_addr = new CellRangeAddress(total, total, 5, 8);
                        sheet.addMergedRegion(total_cell_addr);
                        doc.styleMergedCells(total_cell_addr);

                        Cell total_amount_cell = total_row.createCell(9);
                        total_amount_cell.setCellValue(net_sales + vat_added);
                        doc.styleBorder(total_amount_cell, 10, HorizontalAlignment.RIGHT, false);

                        //Signatorees Section
                        int sign_row = sales + 7;
                        doc.createSignatorees(sign_row);

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
                //this.showProgressBar(false);
                AlertDialogBuilder.messgeDialog("Receiving Report", "Receiving Report was successfully entered, generated and saved on file!",
                        stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
            });

            task.setOnFailed(workerStateEvent -> {
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        }
    }

    @FXML
    public void reset(){
        clear();
    }
}
