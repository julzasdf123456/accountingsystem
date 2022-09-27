package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ReceivingDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
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
import java.util.List;
import java.util.ResourceBundle;

public class ViewReceivingReportController extends MenuControllerHandler implements Initializable {

    @FXML
    private StackPane stackPane;

    @FXML
    private AnchorPane contentPane;


    @FXML
    private JFXTextField supplier_tf, bno_tf, addr_tf, carrier_tf, invoice_tf, dr_tf, rv_tf, po_tf,
            received_tf, received_original_tf, verified_tf;

    @FXML
    private Label net_sales_lbl, vat_lbl, total_lbl;

    @FXML
    private TableView items_table;

    private ObservableList<Stock> receivedItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Creates the receiving item table
        this.createTable();
        //Gets the receiving object
        Receiving receiving = (Receiving) Utility.getSelectedObject();
        //Displays the receiving report details
        this.setReceiving(receiving);
    }
    /**
     * Creates the receiving items table
     * @return void
     */
    public void createTable(){
        TableColumn<Stock, String> column1 = new TableColumn<>("Code");
        column1.setMinWidth(120);
        column1.setCellValueFactory(item -> {
            if (item.getValue().getNeaCode()!=null && item.getValue().getNeaCode().length()!=0) {
                return new ReadOnlyObjectWrapper<>(item.getValue().getNeaCode());
            }else{
                return new ReadOnlyObjectWrapper<>(item.getValue().getLocalCode());
            }
        });

        TableColumn<Stock, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(450);
        column3.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Stock, String> column4 = new TableColumn<>("Unit");
        column4.setMinWidth(70);
        column4.setCellValueFactory(new PropertyValueFactory<>("unit"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column5 = new TableColumn<>("Delivered");
        column5.setMinWidth(80);
        column5.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyDelivered()+""));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column6 = new TableColumn<>("Accepted");
        column6.setMinWidth(80);
        column6.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted()+""));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(118);
        column7.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getUnitCost()+""));

        TableColumn<Stock, String> column8 = new TableColumn<>("Amount");
        column8.setMinWidth(250);
        column8.setStyle("-fx-alignment: center-right;");
        column8.setCellValueFactory(stocks -> new SimpleStringProperty(stocks.getValue().getReceivingItem().getQtyAccepted() * stocks.getValue().getReceivingItem().getUnitCost() +""));

        this.receivedItems =  FXCollections.observableArrayList();
        this.items_table.setPlaceholder(new Label("No item added"));

        this.items_table.getColumns().add(column1);
        this.items_table.getColumns().add(column3);
        this.items_table.getColumns().add(column4);
        this.items_table.getColumns().add(column5);
        this.items_table.getColumns().add(column6);
        this.items_table.getColumns().add(column7);
        this.items_table.getColumns().add(column8);
    }
    /**
     * Calculates the tax
     * @param supplier the supplier object to determine if VAT is applied
     * @param amount the amount to VAT
     * @return void
     */
    public void credit( SupplierInfo supplier, double amount){
        double vat = 0, total = 0;
        if (supplier.getTaxType() != null && supplier.getTaxType().equals("VAT"))
                vat =  amount * (Utility.TAX/100);
        total += vat + amount;

        this.net_sales_lbl.setText(amount+"");
        this.vat_lbl.setText(vat+"");
        this.total_lbl.setText(total+"");
    }
    /**
     * Initializes the information of the receiving and receiving items
     * @return void
     */
    public void setReceiving(Receiving report){
        try {
            SupplierInfo supplier = SupplierDAO.get(report.getSupplierId());
            this.supplier_tf.setText(supplier.getCompanyName());
            this.addr_tf.setText(supplier.getCompanyAddress());
            this.bno_tf.setText(report.getBlwbNo());
            this.carrier_tf.setText(report.getCarrier());
            this.invoice_tf.setText(report.getInvoiceNo());
            this.dr_tf.setText(report.getDrNo());
            this.rv_tf.setText(report.getRvNo());
            this.po_tf.setText(report.getPoNo());
            EmployeeInfo receiver = EmployeeDAO.getOne(report.getReceivedBy(), DB.getConnection());
            EmployeeInfo receiverOrig = EmployeeDAO.getOne(report.getReceivedOrigBy(), DB.getConnection());
            EmployeeInfo verifier = EmployeeDAO.getOne(report.getVerifiedBy(), DB.getConnection());
            this.received_tf.setText(receiver.getFullName());
            this.received_original_tf.setText(receiverOrig.getFullName());
            this.verified_tf.setText(verifier.getFullName());
            List<Stock> items = ReceivingDAO.getReceivingItems(report.getRrNo());
            this.receivedItems =  FXCollections.observableArrayList(items);
            this.items_table.getItems().setAll(receivedItems);

            double amount = 0;
            for (int i = 0; i < this.receivedItems.size(); i++) {
                ReceivingItem receivingItem = this.receivedItems.get(i).getReceivingItem();
                amount += receivingItem.getQtyAccepted() * receivingItem.getUnitCost();
            }
            this.credit(supplier, amount);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
