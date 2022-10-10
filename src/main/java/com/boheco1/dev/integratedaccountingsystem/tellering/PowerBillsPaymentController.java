package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.ConsumerInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.SlimStock;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class PowerBillsPaymentController extends MenuControllerHandler implements Initializable, ObjectTransaction {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField con_name_tf;

    @FXML
    private JFXTextField account_tf;

    @FXML
    private JFXTextField type_tf;

    @FXML
    private JFXTextField status_tf;

    @FXML
    private JFXTextField acct_no_tf;

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXTextField con_addr_tf;

    @FXML
    private JFXTextField meter_no_tf;

    @FXML
    private JFXTextField bapa_tf;

    @FXML
    private JFXButton view_account_tf;

    @FXML
    private TableView<Bill> fees_table;

    @FXML
    private TextField or_no_tf;

    @FXML
    private JFXButton set_or;

    @FXML
    private TextField payment_tf;

    @FXML
    private JFXButton add_check_btn;

    @FXML
    private JFXButton clear_check_btn;

    @FXML
    private ListView<?> checks_lv;

    @FXML
    private TextField total_paid_tf;

    @FXML
    private JFXButton transact_btn;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<Bill> bills = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.acct_no_tf.setOnAction(actionEvent -> {
            String no = acct_no_tf.getText();
            try {
                consumerInfo = ConsumerDAO.getConsumerRecord(no);
                this.con_name_tf.setText(consumerInfo.getConsumerName());
                this.con_addr_tf.setText(consumerInfo.getConsumerAddress());
                this.account_tf.setText(consumerInfo.getAccountID());
                this.meter_no_tf.setText(consumerInfo.getMeterNumber());
                this.type_tf.setText(consumerInfo.getAccountType());
                this.status_tf.setText(consumerInfo.getAccountStatus());
                this.bapa_tf.setText(consumerInfo.getAccountType().equals("BAPA") ? "BAPA Registered" : "");

                try{
                    this.bills = FXCollections.observableArrayList(ConsumerDAO.getConsumerBills(this.consumerInfo.getAccountID(), false));
                    this.fees_table.setItems(this.bills);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.createTable();
        Utility.setParentController(this);
    }
    /**
     * Resets consumer, bills and payment details
     * @return void
     */
    @FXML
    void reset(ActionEvent event) {
        consumerInfo = null;
        this.con_name_tf.setText("");
        this.con_addr_tf.setText("");
        this.account_tf.setText("");
        this.meter_no_tf.setText("");
        this.type_tf.setText("");
        this.status_tf.setText("");
        this.bapa_tf.setText("");

        this.acct_no_tf.setText("");

        this.bills = FXCollections.observableArrayList(new ArrayList<>());
        this.fees_table.setItems(this.bills);
    }

    @FXML
    public void advanceSearch(){
        ModalBuilderForWareHouse.showModalFromXMLNoClose(PowerBillsPaymentController.class, "../tellering/tellering_search_consumer.fxml", Utility.getStackPane());
    }

    /**
     * Initializes the bills table
     * @return void
     */
    public void createTable(){
        TableColumn<Bill, String> column0 = new TableColumn<>("Bill Number");
        column0.setMinWidth(125);
        column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column1 = new TableColumn<>("Billing Month");
        column1.setMinWidth(125);
        column1.setCellValueFactory(new PropertyValueFactory<>("billMonth"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Due Date");
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Amount Due");
        column3.setMinWidth(125);
        column3.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Surcharge");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("surCharge"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column5 = new TableColumn<>("2%");
        column5.setMinWidth(75);
        column5.setCellValueFactory(new PropertyValueFactory<>("ch2306"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column6 = new TableColumn<>("5%");
        column6.setMinWidth(75);
        column6.setCellValueFactory(new PropertyValueFactory<>("ch2307"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Total Amount");
        column7.setMinWidth(150);
        column7.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        column7.setStyle("-fx-alignment: center-right;");

        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setPlaceholder(new Label("No Bills added"));

        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column1);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column3);
        this.fees_table.getColumns().add(column4);
        this.fees_table.getColumns().add(column5);
        this.fees_table.getColumns().add(column6);
        this.fees_table.getColumns().add(column7);
    }

    @Override
    public void receive(Object o) {
        if (o instanceof ConsumerInfo) {
            this.consumerInfo = (ConsumerInfo) o;
            this.con_name_tf.setText(consumerInfo.getConsumerName());
            this.con_addr_tf.setText(consumerInfo.getConsumerAddress());
            this.account_tf.setText(consumerInfo.getAccountID());
            this.meter_no_tf.setText(consumerInfo.getMeterNumber());
            this.type_tf.setText(consumerInfo.getAccountType());
            this.status_tf.setText(consumerInfo.getAccountStatus());
            this.bapa_tf.setText(consumerInfo.getAccountType().equals("BAPA") ? "BAPA Registered" : "");

            try{
                this.bills = FXCollections.observableArrayList(ConsumerDAO.getConsumerBills(this.consumerInfo.getAccountID(), false));
                this.fees_table.setItems(this.bills);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
