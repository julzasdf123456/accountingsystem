package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

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
    private Label total_payable_lbl;

    @FXML
    private JFXButton transact_btn;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<Bill> bills = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.acct_no_tf.setOnAction(actionEvent -> {
            String no = acct_no_tf.getText();
            try {
                this.consumerInfo = ConsumerDAO.getConsumerRecord(no);
                this.setConsumerInfo(this.consumerInfo);
                try{
                    if (this.bills.size() == 0) this.bills = FXCollections.observableArrayList();
                    this.bills.addAll(ConsumerDAO.getConsumerBills(this.consumerInfo, false));
                    Utility.setAmount(this.total_payable_lbl, this.bills);
                    this.fees_table.setItems(this.bills);
                }catch (Exception e){
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        this.createTable();
        this.fees_table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    ConsumerInfo consumer = row.getItem().getConsumer();
                    this.setConsumerInfo(consumer);
                }
            });
            return row ;
        });
        this.set_or.setOnAction(actionEvent -> {
            this.or_no_tf.setDisable(!this.or_no_tf.isDisabled());
        });
        this.payment_tf.setOnKeyTyped(keyEvent -> {
            total_paid_tf.setText(this.payment_tf.getText());
        });
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
        this.total_payable_lbl.setText("0.00");
        this.payment_tf.setText("0.00");
        this.total_paid_tf.setText("0.00");
        this.payment_tf.setDisable(true);
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
        TableColumn<Bill, String> column0 = new TableColumn<>("Bill #");

        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);
        column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column = new TableColumn<>("Account #");
        column.setPrefWidth(110);
        column.setMaxWidth(110);
        column.setMinWidth(110);
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getAccountID()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column1 = new TableColumn<>("Billing Month");
        column1.setPrefWidth(130);
        column1.setMaxWidth(130);
        column1.setMinWidth(130);
        column1.setCellValueFactory(new PropertyValueFactory<>("billMonth"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Due Date");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Amount Due");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("amountDue"));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Surcharge");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("surCharge"));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("2%");
        column5.setPrefWidth(75);
        column5.setMaxWidth(75);
        column5.setMinWidth(75);
        column5.setCellValueFactory(new PropertyValueFactory<>("ch2306"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column6 = new TableColumn<>("5%");
        column6.setPrefWidth(75);
        column6.setMaxWidth(75);
        column6.setMinWidth(75);
        column6.setCellValueFactory(new PropertyValueFactory<>("ch2307"));
        column6.setStyle("-fx-alignment: center;");
        TableColumn<Bill, String> columnWaive = new TableColumn<>("Waive");
        Callback<TableColumn<Bill, String>, TableCell<Bill, String>> waiveBtn
                = //
                new Callback<TableColumn<Bill, String>, TableCell<Bill, String>>() {
                    @Override
                    public TableCell call(final TableColumn<Bill, String> param) {
                        final TableCell<Bill, String> cell = new TableCell<Bill, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            JFXButton btn = new JFXButton("", icon);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconSize(24);
                                icon.setIconColor(Paint.valueOf(ColorPalette.INFO));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        try {
                                            Bill bill = getTableView().getItems().get(getIndex());
                                            showWaiveForm(bill, getTableView(), total_payable_lbl);
                                        } catch (IOException e) {
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
        columnWaive.setCellFactory(waiveBtn);
        columnWaive.setPrefWidth(58);
        columnWaive.setMaxWidth(58);
        columnWaive.setMinWidth(58);
        columnWaive.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Total Amount");
        column7.setCellValueFactory(new PropertyValueFactory<>("totalAmount"));
        column7.setStyle("-fx-alignment: center-right;");

        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setFixedCellSize(35);
        this.fees_table.setPlaceholder(new Label("No Bills added"));

        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column);
        this.fees_table.getColumns().add(column1);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column3);
        this.fees_table.getColumns().add(column4);
        this.fees_table.getColumns().add(column5);
        this.fees_table.getColumns().add(column6);
        this.fees_table.getColumns().add(columnWaive);
        this.fees_table.getColumns().add(column7);
    }

    @Override
    public void receive(Object o) {
        if (o instanceof ConsumerInfo) {
            this.consumerInfo = (ConsumerInfo) o;
            try{
                if (this.bills.size() == 0) this.bills = FXCollections.observableArrayList();
                List<Bill> consumerBills = ConsumerDAO.getConsumerBills(this.consumerInfo, false);
                if (consumerBills.size() > 0) {
                    this.bills.addAll(consumerBills);
                    this.setConsumerInfo(this.consumerInfo);
                    Utility.setAmount(this.total_payable_lbl, this.bills);
                    this.fees_table.setItems(this.bills);
                    this.payment_tf.setDisable(false);
                    this.payment_tf.requestFocus();
                    InputHelper.restrictNumbersOnly(payment_tf);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public void setConsumerInfo(ConsumerInfo consumerInfo){
        this.con_name_tf.setText(consumerInfo.getConsumerName());
        this.con_addr_tf.setText(consumerInfo.getConsumerAddress());
        this.account_tf.setText(consumerInfo.getAccountID());
        this.meter_no_tf.setText(consumerInfo.getMeterNumber());
        this.type_tf.setText(consumerInfo.getAccountType());
        this.status_tf.setText(consumerInfo.getAccountStatus());
        this.bapa_tf.setText(consumerInfo.getAccountType().equals("BAPA") ? "BAPA Registered" : "");
    }

    public void showWaiveForm(Bill bill, TableView table, Label total) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_waive_surcharge.fxml"));
        Parent parent = fxmlLoader.load();
        WaiveChargesController waiveController = fxmlLoader.getController();
        waiveController.setBill(bill);
        waiveController.setData(table, total);
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        dialog.show();
    }
}
