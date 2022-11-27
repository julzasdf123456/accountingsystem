package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.Printer;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewConsumerBillsController extends MenuControllerHandler implements Initializable, ObjectTransaction {

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
    private TableView<BillStanding> fees_table;

    @FXML
    private TableView<BillStanding> consumerBills_table;

    @FXML
    private Label unpaidBills_lbl;

    @FXML
    private ProgressBar progressBar;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<BillStanding> bills = FXCollections.observableArrayList();
    private ObservableList<BillStanding> paidbills = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        this.view_account_tf.setOnAction(actionEvent -> {
            //connect to CRM
        });

        this.acct_no_tf.setOnAction(actionEvent -> {
            String no = acct_no_tf.getText();
            try {
                this.consumerInfo = ConsumerDAO.getConsumerRecord(no);
                if (this.consumerInfo != null) {
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws SQLException {
                            try{
                                bills = FXCollections.observableArrayList(BillDAO.getConsumerBills(consumerInfo));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                            return null;
                        }
                    };

                    task.setOnRunning(wse -> {
                        acct_no_tf.setDisable(true);
                        progressBar.setVisible(true);
                    });

                    task.setOnSucceeded(wse -> {
                        paidbills = FXCollections.observableArrayList();
                        acct_no_tf.setDisable(false);
                        setConsumerInfo(consumerInfo);
                        if (this.bills.size() > 0) {
                            fees_table.setItems(bills);
                            int count = 0;
                            for (BillStanding b: bills) {
                                if (b.getStatus().equals("UNPAID")) {
                                    count++;
                                }else{
                                    paidbills.add(b);
                                }
                            }
                            consumerBills_table.setItems(paidbills);
                            this.unpaidBills_lbl.setText(count+"");
                        }
                        progressBar.setVisible(false);
                    });

                    task.setOnFailed(wse -> {
                        //Do nothing
                        acct_no_tf.setDisable(false);
                        progressBar.setVisible(false);
                    });

                    new Thread(task).start();
                }else{
                    AlertDialogBuilder.messgeDialog("System Error", "No existing consumer account! Please refine search and try again!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        this.createTable();
        this.createPaymentsTable();

        Utility.setParentController(this);
    }


    /**
     * Resets consumer, bills and payment details
     * @return void
     */
    @FXML
    public void reset(ActionEvent event) {
        this.reset();
    }

    public void reset(){
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
        this.consumerBills_table.setItems(this.bills);
        this.acct_no_tf.requestFocus();
        this.unpaidBills_lbl.setText("0");
    }

    /**
     * Displays Advance Consumer Search UI
     * @return void
     */
    @FXML
    public void advanceSearch(){
        ModalBuilderForWareHouse.showModalFromXMLNoClose(ViewConsumerBillsController.class, "../tellering/tellering_search_consumer.fxml", Utility.getStackPane());
    }

    /**
     * Initializes the overview bills table
     * @return void
     */
    public void createTable(){
        TableColumn<BillStanding, String> column = new TableColumn<>("Service Period");
        column.setPrefWidth(120);
        column.setMaxWidth(120);
        column.setMinWidth(120);
        column.setCellValueFactory(new PropertyValueFactory<>("servicePeriodEnd"));
        column.setStyle("-fx-alignment: center;");

        TableColumn<BillStanding, String> column0 = new TableColumn<>("Bill Number");
        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);
        column0.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getBillNo()));
        column0.setStyle("-fx-alignment: center-left;");



        TableColumn<BillStanding, String> column1 = new TableColumn<>("Type");
        column1.setPrefWidth(110);
        column1.setMaxWidth(110);
        column1.setMinWidth(110);
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumerType()));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<BillStanding, String> column2 = new TableColumn<>("Status");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<>("status"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<BillStanding, String> column3 = new TableColumn<>("Bill Amount");
        column3.setPrefWidth(140);
        column3.setMaxWidth(140);
        column3.setMinWidth(140);
        column3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmountDue())));
        column3.setStyle("-fx-alignment: center-right;");


        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setFixedCellSize(35);
        this.fees_table.setPlaceholder(new Label("No bills display! Search or input the account number!"));

        this.fees_table.getColumns().add(column);
        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column1);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column3);

    }

    /**
     * Initializes the paid bills table
     * @return void
     */
    public void createPaymentsTable(){
        TableColumn<BillStanding, String> column = new TableColumn<>("Service Period");
        column.setPrefWidth(120);
        column.setMaxWidth(120);
        column.setMinWidth(120);
        column.setCellValueFactory(new PropertyValueFactory<>("servicePeriodEnd"));
        column.setStyle("-fx-alignment: center;");

        TableColumn<BillStanding, String> column0 = new TableColumn<>("Bill Number");
        column0.setPrefWidth(110);
        column0.setMaxWidth(110);
        column0.setMinWidth(110);
        column0.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getBillNo()));
        column0.setStyle("-fx-alignment: center-left;");



        TableColumn<BillStanding, String> column1 = new TableColumn<>("Date Paid");
        column1.setPrefWidth(110);
        column1.setMaxWidth(110);
        column1.setMinWidth(110);
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getPostingDate() == null ? "" : obj.getValue().getPostingDate().toLocalDate().toString()));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<BillStanding, String> column2 = new TableColumn<>("Teller");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getTeller()));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<BillStanding, String> column3 = new TableColumn<>("Paid Amount");
        column3.setPrefWidth(140);
        column3.setMaxWidth(140);
        column3.setMinWidth(140);
        column3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<BillStanding, String> column4 = new TableColumn<>("OR Number");
        column4.setPrefWidth(140);
        column4.setMaxWidth(140);
        column4.setMinWidth(140);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getDcrNumber()));
        column4.setStyle("-fx-alignment: center-right;");

        this.bills =  FXCollections.observableArrayList();
        this.consumerBills_table.setFixedCellSize(35);
        this.consumerBills_table.setPlaceholder(new Label("No bills to display! Search or input the account number!"));

        this.consumerBills_table.getColumns().add(column);
        this.consumerBills_table.getColumns().add(column0);
        this.consumerBills_table.getColumns().add(column1);
        this.consumerBills_table.getColumns().add(column2);
        this.consumerBills_table.getColumns().add(column3);
        this.consumerBills_table.getColumns().add(column4);
    }

    /**
     * Receives ConsumerInfo object from dialog
     * @param o the object reference
     * @return void
     */
    @Override
    public void receive(Object o) {
        if (o instanceof ConsumerInfo) {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    consumerInfo = (ConsumerInfo) o;
                    try{
                        bills = FXCollections.observableArrayList(BillDAO.getConsumerBills(consumerInfo));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                progressBar.setVisible(true);
            });

            task.setOnSucceeded(wse -> {
                paidbills = FXCollections.observableArrayList();
                setConsumerInfo(consumerInfo);
                if (this.bills.size() > 0) {
                    fees_table.setItems(bills);
                    int count = 0;
                    for (BillStanding b: bills) {
                        if (b.getStatus().equals("UNPAID")) {
                            count++;
                        }else{
                            paidbills.add(b);
                        }
                    }
                    consumerBills_table.setItems(paidbills);
                    this.unpaidBills_lbl.setText(count+"");
                }
                progressBar.setVisible(false);
            });

            task.setOnFailed(wse -> {
                //Do nothing
                progressBar.setVisible(false);
            });

            new Thread(task).start();
        }
    }
    /**
     * Sets ConsumerInfo details to UI
     * @param consumerInfo the object reference
     * @return void
     */
    public void setConsumerInfo(ConsumerInfo consumerInfo){
        this.con_name_tf.setText(consumerInfo.getConsumerName());
        this.con_addr_tf.setText(consumerInfo.getConsumerAddress());
        this.account_tf.setText(consumerInfo.getAccountID());
        this.meter_no_tf.setText(consumerInfo.getMeterNumber());
        this.type_tf.setText(consumerInfo.getAccountType());
        this.status_tf.setText(consumerInfo.getAccountStatus());
        this.bapa_tf.setText(consumerInfo.getAccountType().equals("BAPA") ? "BAPA Registered" : "");
    }
}
