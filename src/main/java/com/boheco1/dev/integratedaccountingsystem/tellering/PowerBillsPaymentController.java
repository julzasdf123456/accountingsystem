package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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
    private JFXButton view_account_tf, view_bills_tf;

    @FXML
    private TableView<Bill> fees_table;
    private TableView<Bill> excluded_table = new TableView();
    @FXML
    private TextField payment_tf;

    @FXML
    private JFXButton add_check_btn;

    @FXML
    private JFXButton clear_check_btn;

    @FXML
    private ListView<Check> checks_lv;

    @FXML
    private TextField total_paid_tf;

    @FXML
    private Label total_payable_lbl;

    @FXML
    private JFXTextField add_charges_tf;

    @FXML
    private JFXTextField surcharge_tf;

    @FXML
    private JFXTextField ppd_tf;

    @FXML
    private JFXTextField sl_adj_tf;

    @FXML
    private JFXTextField other_adj_tf;

    @FXML
    private JFXTextField daa_tf;

    @FXML
    private JFXTextField ch2306_tf;

    @FXML
    private JFXTextField ch2307_tf;

    @FXML
    private JFXTextField power_amt_tf;

    @FXML
    private JFXTextField vat_tf;

    @FXML
    private JFXTextField katas_tf;

    @FXML
    private JFXTextField md_refund_tf;

    @FXML
    private JFXButton transact_btn;

    @FXML
    private VBox sidebar_vbox;

    @FXML
    private VBox tableBox;

    @FXML
    private ProgressBar progressBar;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<Bill> excluded_bills = FXCollections.observableArrayList();
    private ObservableList<Check> checks = FXCollections.observableArrayList();
    private boolean isPaid = false;
    private double toDeposit, cash = 0;
    private JFXDialog dialogConfirm = null;
    private EventHandler<ActionEvent> confirmEvent;

    @FXML
    private Label billsNo_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        confirmEvent = actionEvent -> confirmPayment();

        if (ActiveUser.getUser().can("manage-billing"))
            sidebar_vbox.setVisible(false);
        
        if (ActiveUser.getUser().can("manage-tellering"))
            sidebar_vbox.setVisible(true);

        this.transact_btn.setDisable(true);

        this.view_account_tf.setOnAction(actionEvent -> {
            //connect to CRM
        });
        this.view_bills_tf.setOnAction(actionEvent -> {
            viewBills();
        });
        //If account number entered is 10 digits, automatic display
        this.acct_no_tf.setOnKeyReleased(event -> {
            String no = acct_no_tf.getText();
            if (no.strip().length() == 10){
                try {
                    this.consumerInfo = ConsumerDAO.getConsumerRecord(no);
                    if (this.consumerInfo != null) {
                        Task<Void> task = new Task<>() {
                            @Override
                            protected Void call() throws SQLException {
                                try{
                                    if (bills.size() == 0) bills = FXCollections.observableArrayList();
                                    List<Bill> consumerBills = BillDAO.getConsumerBills(consumerInfo, false);
                                    if (consumerBills.size() > 0) {
                                        for (Bill b : consumerBills){
                                            if (!bills.contains(b)) {
                                                bills.add(b);
                                            }
                                        }
                                    }
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
                            setBillInfo(bills);
                            acct_no_tf.setDisable(false);
                            add_check_btn.setDisable(false);
                            acct_no_tf.setText("");
                            setConsumerInfo(consumerInfo);
                            fees_table.setItems(bills);
                            setPayables();
                            payment_tf.setDisable(false);
                            payment_tf.requestFocus();
                            transact_btn.setDisable(false);
                            total_paid_tf.setDisable(false);
                            InputHelper.restrictNumbersOnly(payment_tf);
                            progressBar.setVisible(false);
                            acct_no_tf.requestFocus();
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
            }
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
                                if (bills.size() == 0) bills = FXCollections.observableArrayList();
                                List<Bill> consumerBills = BillDAO.getConsumerBills(consumerInfo, false);
                                if (consumerBills.size() > 0) {
                                    for (Bill b : consumerBills){
                                        if (!bills.contains(b)) {
                                            bills.add(b);
                                        }
                                    }
                                }
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
                        setBillInfo(bills);
                        acct_no_tf.setDisable(false);
                        add_check_btn.setDisable(false);
                        acct_no_tf.setText("");
                        setConsumerInfo(consumerInfo);
                        fees_table.setItems(bills);
                        setPayables();
                        payment_tf.setDisable(false);
                        payment_tf.requestFocus();
                        transact_btn.setDisable(false);
                        total_paid_tf.setDisable(false);
                        InputHelper.restrictNumbersOnly(payment_tf);
                        progressBar.setVisible(false);
                        acct_no_tf.requestFocus();
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
        this.createExcludedTable();
        this.setMenuActions();

        this.payment_tf.setOnKeyReleased(keyEvent -> {
            this.setPayments();
        });

        this.payment_tf.addEventHandler(ActionEvent.ACTION, confirmEvent);

        this.add_check_btn.setOnAction(action -> {
            try {
                this.showAddCheckForm();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        this.clear_check_btn.setOnAction(action ->{
            this.resetChecks();
        });

        this.transact_btn.setOnAction(actionEvent -> {
            this.confirmPayment();
        });
        Utility.setParentController(this);
    }

    public void confirmPayment(){
        try {
            double totalBills = Utility.getTotalAmount(this.bills);
            double totalPayments = this.computeTotalPayments();

            if (totalPayments < totalBills){
                AlertDialogBuilder.messgeDialog("Partial Payment Warning", "The total payment does not exceed the total payable amount! Please check the amount!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else {
                this.showConfirmation(
                        this.bills,
                        totalBills,
                        this.checks);
            }
        } catch (IOException e) {
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
    /**
     * Sets the total payment values
     * @return void
     */
    public void setPayments(){
        try {
            double total = this.computeTotalPayments();
            this.total_paid_tf.setText(Utility.formatDecimal(total));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    /**
     * Sets the total bill values
     * @return void
     */
    public void setPayables(){
        try {
            double total = Utility.getTotalAmount(this.bills);
            this.total_payable_lbl.setText(Utility.formatDecimal(total));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public double computeTotalPayments(){
        double total = 0, amount = 0;

        try {
            cash = Double.parseDouble(this.payment_tf.getText().replace(",",""));
        }catch (Exception e){

        }

        try {
            amount = Utility.getTotalAmount(this.checks);
        }catch (Exception e){

        }

        total = cash + amount;

        return Utility.round(total, 2);
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
        cash = 0;
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
        this.payment_tf.setText("");
        this.total_paid_tf.setText("0.00");
        this.total_paid_tf.setDisable(true);
        this.payment_tf.setDisable(true);
        this.bills = FXCollections.observableArrayList(new ArrayList<>());
        this.excluded_bills = FXCollections.observableArrayList(new ArrayList<>());
        this.fees_table.setItems(this.bills);
        this.excluded_table.setItems(this.excluded_bills);
        this.tableBox.getChildren().remove(this.excluded_table);
        this.transact_btn.setDisable(true);
        this.resetChecks();
        this.resetBillInfo();
        this.daa_tf.setText("");
        this.acct_no_tf.requestFocus();
        this.add_check_btn.setDisable(true);
        this.billsNo_lbl.setText("0");
    }
    /**
     * Resets check details
     * @return void
     */
    public void resetChecks() {
        if (this.checks == null || this.checks.size() == 0) return;
        double amount = Utility.getTotalAmount(this.checks);
        double current_total = Double.parseDouble(this.total_paid_tf.getText().replace(",","")) - amount;
        if (current_total < 0)
            current_total = 0;
        this.total_paid_tf.setText(Utility.formatDecimal(current_total));
        this.checks =  FXCollections.observableArrayList();
        this.checks_lv.setItems(this.checks);

    }
    public void resetBillInfo(){
        this.add_charges_tf.setText("");
        this.surcharge_tf.setText("");
        this.ppd_tf.setText("");
        this.sl_adj_tf.setText("");
        this.other_adj_tf.setText("");
        this.ch2306_tf.setText("");
        this.ch2307_tf.setText("");
        this.power_amt_tf.setText("");
        this.katas_tf.setText("");
        this.vat_tf.setText("");
        this.md_refund_tf.setText("");
        this.billsNo_lbl.setText(bills.size()+"");
    }
    /**
     * Displays Advance Consumer Search UI
     * @return void
     */
    @FXML
    public void advanceSearch() throws IOException {
        this.showSearchConsumer();
    }
    /**
     * Shows the View Consumer Bills UI
     * @return void
     */
    public void viewBills(){
        if (this.consumerInfo != null){
            Utility.setSelectedObject(this.consumerInfo);
            ModalBuilderForWareHouse.showModalFromXMLNoClose(PowerBillsPaymentController.class, "../tellering/tellering_consumer_bills.fxml", Utility.getStackPane());
        }
    }
    /**
     * Initializes the bills table
     * @return void
     */
    public void createTable(){
        TableColumn<Bill, String> column0 = new TableColumn<>("Bill #");
        column0.setPrefWidth(88);
        column0.setMaxWidth(88);
        column0.setMinWidth(88);
        column0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column = new TableColumn<>("Account #");
        column.setPrefWidth(88);
        column.setMaxWidth(88);
        column.setMinWidth(88);
        column.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getAccountID()));
        column.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column_con = new TableColumn<>("(T) - Consumer Name");
        column_con.setPrefWidth(200);
        column_con.setMinWidth(200);
        column_con.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumerType()+" - "+obj.getValue().getConsumer().getConsumerName() + ((obj.getValue().getConsumer().getAccountStatus().contains("DISCO"))? " - DISCONNECTED" : "") + ((obj.getValue().getConsumer().getAccountStatus().contains("ILLEGAL"))? " - ILLEGAL" : "")));
        column_con.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column1 = new TableColumn<>("Billing Month");
        column1.setPrefWidth(112);
        column1.setMaxWidth(112);
        column1.setMinWidth(112);
        column1.setCellValueFactory(new PropertyValueFactory<>("billMonth"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Due Date");
        column2.setPrefWidth(89);
        column2.setMaxWidth(89);
        column2.setMinWidth(89);
        column2.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        column2.setStyle("-fx-alignment: center;");
        int amtSize = 93;
        int smSize = 70;
        TableColumn<Bill, String> column3 = new TableColumn<>("Bill Amount");
        column3.setPrefWidth(amtSize);
        column3.setMaxWidth(amtSize);
        column3.setMinWidth(amtSize);
        column3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getAmountDue()
        )));
        column3.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column31 = new TableColumn<>("Pwr Amount");
        column31.setPrefWidth(amtSize);
        column31.setMaxWidth(amtSize);
        column31.setMinWidth(amtSize);
        column31.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getPowerAmount() - obj.getValue().getAcrmVat() - obj.getValue().getDAAVat()
        )));
        column31.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column32 = new TableColumn<>("TSF");
        column32.setPrefWidth(smSize);
        column32.setMaxWidth(smSize);
        column32.setMinWidth(smSize);
        column32.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getTransformerRental()
        )));
        column32.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column33 = new TableColumn<>("Others");
        column33.setPrefWidth(smSize);
        column33.setMaxWidth(smSize);
        column33.setMinWidth(smSize);
        column33.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getOtherCharges()
        )));
        column33.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column41 = new TableColumn<>("Surcharge");
        column41.setPrefWidth(smSize);
        column41.setMaxWidth(smSize);
        column41.setMinWidth(smSize);
        column41.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSurCharge())));
        column41.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column4 = new TableColumn<>("PPD");
        column4.setPrefWidth(smSize);
        column4.setMaxWidth(smSize);
        column4.setMinWidth(smSize);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getDiscount())));
        column4.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column42 = new TableColumn<>("SL Adj");
        column42.setPrefWidth(smSize);
        column42.setMaxWidth(smSize);
        column42.setMinWidth(smSize);
        column42.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        column42.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column43 = new TableColumn<>("Other Adj");
        column43.setPrefWidth(smSize);
        column43.setMaxWidth(smSize);
        column43.setMinWidth(smSize);
        column43.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getOtherAdjustment())));
        column43.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column44 = new TableColumn<>("KWH");
        column44.setPrefWidth(75);
        column44.setMaxWidth(75);
        column44.setMinWidth(75);
        column44.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getPowerKWH())));
        column44.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column45 = new TableColumn<>("VAT");
        column45.setPrefWidth(smSize);
        column45.setMaxWidth(smSize);
        column45.setMinWidth(smSize);
        column45.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getVat() + obj.getValue().getSurChargeTax())));
        column45.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column46 = new TableColumn<>("Katas");
        column46.setPrefWidth(smSize);
        column46.setMaxWidth(smSize);
        column46.setMinWidth(smSize);
        column46.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getKatas())));
        column46.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column47 = new TableColumn<>("MD Refd");
        column47.setPrefWidth(smSize);
        column47.setMaxWidth(smSize);
        column47.setMinWidth(smSize);
        column47.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getMdRefund())));
        column47.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("2307 5%");
        column5.setPrefWidth(smSize);
        column5.setMaxWidth(smSize);
        column5.setMinWidth(smSize);
        column5.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2306()))));
        column5.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column51 = new TableColumn<>("2307 2%");
        column51.setPrefWidth(smSize);
        column51.setMaxWidth(smSize);
        column51.setMinWidth(smSize);
        column51.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2307()))));
        column51.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> column52 = new TableColumn<>("TIN");
        column52.setPrefWidth(smSize);
        column52.setMaxWidth(smSize);
        column52.setMinWidth(smSize);
        column52.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getTINNo()));
        column52.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Total Amount");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");
        column7.setPrefWidth(amtSize);
        column7.setMaxWidth(amtSize);
        column7.setMinWidth(amtSize);

        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setFixedCellSize(35);
        this.fees_table.setPlaceholder(new Label("No bills added! Search or input the account number!"));

        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column);
        this.fees_table.getColumns().add(column_con);
        this.fees_table.getColumns().add(column1);

        this.fees_table.getColumns().add(column3);
        this.fees_table.getColumns().add(column31);
        this.fees_table.getColumns().add(column32);
        this.fees_table.getColumns().add(column33);
        this.fees_table.getColumns().add(column4);
        this.fees_table.getColumns().add(column41);
        this.fees_table.getColumns().add(column42);
        this.fees_table.getColumns().add(column43);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column44);
        this.fees_table.getColumns().add(column45);
        this.fees_table.getColumns().add(column46);
        this.fees_table.getColumns().add(column47);
        this.fees_table.getColumns().add(column5);
        this.fees_table.getColumns().add(column51);
        this.fees_table.getColumns().add(column52);
        this.fees_table.getColumns().add(column7);
        this.fees_table.setFixedCellSize(27.0);
        //Keyboard event to remove bill when E is pressed
        this.fees_table.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.E){
                this.fees_table.getItems().remove(this.fees_table.getSelectionModel().getSelectedIndex());
                fees_table.refresh();
                this.setPayables();
                if (this.fees_table.getItems().size() == 0) {
                    this.reset();
                    this.resetBillInfo();
                }else{
                    this.payment_tf.requestFocus();
                    this.setBillInfo(this.bills);
                }
                this.billsNo_lbl.setText(bills.size()+"");
            }
        });
    }
    /**
     * Initializes the excluded bills table
     * @return void
     */
    public void createExcludedTable(){
        TableColumn<Bill, String> ecolumn0 = new TableColumn<>("Bill #");
        ecolumn0.setPrefWidth(88);
        ecolumn0.setMaxWidth(88);
        ecolumn0.setMinWidth(88);
        ecolumn0.setCellValueFactory(new PropertyValueFactory<>("billNo"));
        ecolumn0.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> ecolumn = new TableColumn<>("Account #");
        ecolumn.setPrefWidth(88);
        ecolumn.setMaxWidth(88);
        ecolumn.setMinWidth(88);
        ecolumn.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getAccountID()));
        ecolumn.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> ecolumn_con = new TableColumn<>("(T) - Consumer Name");
        ecolumn_con.setPrefWidth(200);
        ecolumn_con.setMinWidth(200);
        ecolumn_con.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumerType()+" - "+obj.getValue().getConsumer().getConsumerName() + ((obj.getValue().getConsumer().getAccountStatus().contains("DISCO"))? " - DISCONNECTED" : "") + ((obj.getValue().getConsumer().getAccountStatus().contains("ILLEGAL"))? " - ILLEGAL" : "")));
        ecolumn_con.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> ecolumn1 = new TableColumn<>("Billing Month");
        ecolumn1.setPrefWidth(112);
        ecolumn1.setMaxWidth(112);
        ecolumn1.setMinWidth(112);
        ecolumn1.setCellValueFactory(new PropertyValueFactory<>("billMonth"));
        ecolumn1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> ecolumn2 = new TableColumn<>("Due Date");
        ecolumn2.setPrefWidth(89);
        ecolumn2.setMaxWidth(89);
        ecolumn2.setMinWidth(89);
        ecolumn2.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        ecolumn2.setStyle("-fx-alignment: center;");
        int amtSize = 93;
        int smSize = 67;
        TableColumn<Bill, String> ecolumn3 = new TableColumn<>("Bill Amount");
        ecolumn3.setPrefWidth(amtSize);
        ecolumn3.setMaxWidth(amtSize);
        ecolumn3.setMinWidth(amtSize);
        ecolumn3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getAmountDue()
        )));
        ecolumn3.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn31 = new TableColumn<>("Pwr Amount");
        ecolumn31.setPrefWidth(amtSize);
        ecolumn31.setMaxWidth(amtSize);
        ecolumn31.setMinWidth(amtSize);
        ecolumn31.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getPowerAmount() - obj.getValue().getAcrmVat() - obj.getValue().getDAAVat()
        )));
        ecolumn31.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn32 = new TableColumn<>("TSF");
        ecolumn32.setPrefWidth(smSize);
        ecolumn32.setMaxWidth(smSize);
        ecolumn32.setMinWidth(smSize);
        ecolumn32.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getTransformerRental()
        )));
        ecolumn32.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn33 = new TableColumn<>("Others");
        ecolumn33.setPrefWidth(smSize);
        ecolumn33.setMaxWidth(smSize);
        ecolumn33.setMinWidth(smSize);
        ecolumn33.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getOtherCharges()
        )));
        ecolumn33.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn4 = new TableColumn<>("Surcharge");
        ecolumn4.setPrefWidth(smSize);
        ecolumn4.setMaxWidth(smSize);
        ecolumn4.setMinWidth(smSize);
        ecolumn4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSurCharge())));
        ecolumn4.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn41 = new TableColumn<>("PPD");
        ecolumn41.setPrefWidth(smSize);
        ecolumn41.setMaxWidth(smSize);
        ecolumn41.setMinWidth(smSize);
        ecolumn41.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getDiscount())));
        ecolumn41.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn42 = new TableColumn<>("SL Adj");
        ecolumn42.setPrefWidth(smSize);
        ecolumn42.setMaxWidth(smSize);
        ecolumn42.setMinWidth(smSize);
        ecolumn42.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        ecolumn42.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn43 = new TableColumn<>("Other Adj");
        ecolumn43.setPrefWidth(smSize);
        ecolumn43.setMaxWidth(smSize);
        ecolumn43.setMinWidth(smSize);
        ecolumn43.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getOtherAdjustment())));
        ecolumn43.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn44 = new TableColumn<>("KWH");
        ecolumn44.setPrefWidth(75);
        ecolumn44.setMaxWidth(75);
        ecolumn44.setMinWidth(75);
        ecolumn44.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getPowerKWH())));
        ecolumn44.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn45 = new TableColumn<>("VAT");
        ecolumn45.setPrefWidth(smSize);
        ecolumn45.setMaxWidth(smSize);
        ecolumn45.setMinWidth(smSize);
        ecolumn45.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getVat() + obj.getValue().getSurChargeTax())));
        ecolumn45.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn46 = new TableColumn<>("Katas");
        ecolumn46.setPrefWidth(smSize);
        ecolumn46.setMaxWidth(smSize);
        ecolumn46.setMinWidth(smSize);
        ecolumn46.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getKatas())));
        ecolumn46.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn47 = new TableColumn<>("MD Refd");
        ecolumn47.setPrefWidth(smSize);
        ecolumn47.setMaxWidth(smSize);
        ecolumn47.setMinWidth(smSize);
        ecolumn47.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getMdRefund())));
        ecolumn47.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> ecolumn5 = new TableColumn<>("2307 5%");
        ecolumn5.setPrefWidth(smSize);
        ecolumn5.setMaxWidth(smSize);
        ecolumn5.setMinWidth(smSize);
        ecolumn5.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2306()))));
        ecolumn5.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn51 = new TableColumn<>("2307 2%");
        ecolumn51.setPrefWidth(smSize);
        ecolumn51.setMaxWidth(smSize);
        ecolumn51.setMinWidth(smSize);
        ecolumn51.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2307()))));
        ecolumn51.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");

        TableColumn<Bill, String> ecolumn52 = new TableColumn<>("TIN");
        ecolumn52.setPrefWidth(smSize);
        ecolumn52.setMaxWidth(smSize);
        ecolumn52.setMinWidth(smSize);
        ecolumn52.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getTINNo()));
        ecolumn52.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> ecolumn7 = new TableColumn<>("Total Amount");
        ecolumn7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        ecolumn7.setStyle("-fx-alignment: center-right; -fx-font-weight: bold;");
        ecolumn7.setPrefWidth(amtSize);
        ecolumn7.setMaxWidth(amtSize);
        ecolumn7.setMinWidth(amtSize);

        this.excluded_bills =  FXCollections.observableArrayList();
        this.excluded_table.setFixedCellSize(35);
        this.excluded_table.setPlaceholder(new Label("No bills added! Search or input the account number!"));
        this.excluded_table.getColumns().removeAll();
        this.excluded_table.getColumns().add(ecolumn0);
        this.excluded_table.getColumns().add(ecolumn);
        this.excluded_table.getColumns().add(ecolumn_con);
        this.excluded_table.getColumns().add(ecolumn1);

        this.excluded_table.getColumns().add(ecolumn3);
        this.excluded_table.getColumns().add(ecolumn31);
        this.excluded_table.getColumns().add(ecolumn32);
        this.excluded_table.getColumns().add(ecolumn33);
        this.excluded_table.getColumns().add(ecolumn41);
        this.excluded_table.getColumns().add(ecolumn4);
        this.excluded_table.getColumns().add(ecolumn42);
        this.excluded_table.getColumns().add(ecolumn43);
        this.excluded_table.getColumns().add(ecolumn2);
        this.excluded_table.getColumns().add(ecolumn44);
        this.excluded_table.getColumns().add(ecolumn45);
        this.excluded_table.getColumns().add(ecolumn46);
        this.excluded_table.getColumns().add(ecolumn47);
        this.excluded_table.getColumns().add(ecolumn5);
        this.excluded_table.getColumns().add(ecolumn51);
        this.excluded_table.getColumns().add(ecolumn52);
        this.excluded_table.getColumns().add(ecolumn7);
        this.excluded_table.setFixedCellSize(27.0);
        this.excluded_table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        this.excluded_table.getStyleClass().add("datatable");
        this.excluded_table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    ConsumerInfo consumer = row.getItem().getConsumer();
                    this.setConsumerInfo(consumer);
                }
            });
            //Popup Menu when right click table row
            if (ActiveUser.getUser().can("manage-tellering") || ActiveUser.getUser().can("manage-cashiering")) {
                final ContextMenu rowMenu = new ContextMenu();

                MenuItem itemRemoveBill = new MenuItem("Remove Bill");
                itemRemoveBill.setOnAction(actionEvent -> {
                    this.excluded_table.getItems().remove(row.getItem());
                    if (this.excluded_table.getItems().size() == 0) {
                        this.tableBox.getChildren().remove(this.excluded_table);
                    }else{
                        this.excluded_table.refresh();
                    }
                });

                MenuItem itemIncludeBill = new MenuItem("Include Bill");
                itemIncludeBill.setOnAction(actionEvent -> {

                    this.bills.add(row.getItem());
                    this.fees_table.setItems(this.bills);
                    this.fees_table.refresh();
                    this.payment_tf.requestFocus();
                    this.setBillInfo(this.bills);
                    this.setPayables();
                    this.billsNo_lbl.setText(bills.size()+"");

                    this.excluded_table.getItems().remove(row.getItem());
                    if (this.excluded_table.getItems().size() == 0) {
                        this.tableBox.getChildren().remove(this.excluded_table);
                    }else{
                        this.excluded_table.refresh();
                    }

                });

                rowMenu.getItems().addAll(itemIncludeBill, new SeparatorMenuItem(), itemRemoveBill);

                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(rowMenu));
            }
            return row;
        });
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
                    isPaid = true;
                    consumerInfo = (ConsumerInfo) o;
                    try{
                        if (bills.size() == 0) bills = FXCollections.observableArrayList();
                        List<Bill> consumerBills = BillDAO.getConsumerBills(consumerInfo, false);
                        if (consumerBills.size() > 0) {
                            for (Bill b : consumerBills){
                                if (!bills.contains(b)) {
                                    bills.add(b);
                                }
                            }
                            isPaid = false;
                        }
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
                if (!isPaid) setConsumerInfo(consumerInfo);
                if (this.bills.size() > 0) {
                    setBillInfo(bills);
                    setPayables();
                    fees_table.setItems(bills);
                    payment_tf.setDisable(false);
                    payment_tf.requestFocus();
                    transact_btn.setDisable(false);
                    total_paid_tf.setDisable(false);
                    add_check_btn.setDisable(false);
                    InputHelper.restrictNumbersOnly(payment_tf);
                }else{
                    add_check_btn.setDisable(true);
                }
                billsNo_lbl.setText(bills.size()+"");
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
        if (this.consumerInfo.getAccountStatus().toLowerCase().contains("disco")) {
            this.status_tf.setText("DISCONNECTED");
            this.status_tf.setStyle("-fx-text-fill: red;");
        }else if (this.consumerInfo.getAccountStatus().toLowerCase().contains("illegal")) {
            this.status_tf.setText("ILLEGAL");
            this.status_tf.setStyle("-fx-text-fill: red;");
        }else{
            this.status_tf.setStyle("-fx-text-fill: black;");
        }
        this.bapa_tf.setText(consumerInfo.getAccountType().equals("BAPA") ? "BAPA Registered" : "");
    }
    /**
     * Sets total bill details to UI
     * @param current_bills the list of bills
     * @return void
     */
    public void setBillInfo(List<Bill> current_bills){
        double add_charge_sum = 0, surcharge_sum = 0, ppd_sum = 0, sl_adj_sum = 0, other_adj_sum = 0, ch06_sum=0, ch07_sum=0,power_amt_sum = 0, md_refund_sum = 0,
                vat_sum = 0, katas_sum = 0, daa = 0, acrm = 0;
        for(Bill b : current_bills){
            add_charge_sum += b.getOtherCharges() + b.getTransformerRental();
            surcharge_sum += b.getSurCharge();
            ppd_sum += b.getDiscount();
            sl_adj_sum += b.getSlAdjustment() ;
            other_adj_sum += b.getOtherAdjustment();
            ch06_sum += b.getCh2306();
            ch07_sum += b.getCh2307();
            power_amt_sum += b.getPowerAmount();
            md_refund_sum += b.getMdRefund();
            vat_sum += b.getVat() + b.getSurChargeTax();
            katas_sum += b.getKatas();
            daa += b.getDAAVat();
            acrm += b.getAcrmVat();
        }
        power_amt_sum -= (daa+acrm);
        this.billsNo_lbl.setText(bills.size()+"");
        this.surcharge_tf.setText(Utility.formatDecimal(surcharge_sum));
        this.ppd_tf.setText(Utility.formatDecimal(ppd_sum));
        this.sl_adj_tf.setText(Utility.formatDecimal(sl_adj_sum));
        this.other_adj_tf.setText(Utility.formatDecimal(other_adj_sum));
        this.ch2306_tf.setText(Utility.formatDecimal(ch06_sum));
        this.ch2307_tf.setText(Utility.formatDecimal(ch07_sum));
        this.power_amt_tf.setText(Utility.formatDecimal(power_amt_sum));
        this.katas_tf.setText(Utility.formatDecimal(katas_sum));
        this.md_refund_tf.setText(Utility.formatDecimal(md_refund_sum));
        this.vat_tf.setText(Utility.formatDecimal(vat_sum));
        this.add_charges_tf.setText(Utility.formatDecimal(add_charge_sum));
        this.daa_tf.setText(Utility.formatDecimal(daa+acrm));
    }
    /**
     * Displays Search Consumer UI
     * @return void
     */
    public void showSearchConsumer() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_search_consumer.fxml"));
        Parent parent = fxmlLoader.load();
        SearchConsumerController ctrl = fxmlLoader.getController();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        dialog.setOnDialogOpened((event) -> { ctrl.getSearch_tf().requestFocus(); });
        dialog.setOnDialogClosed((event) -> { payment_tf.requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Add Check UI
     * @return void
     */
    public void showAddCheckForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_add_check.fxml"));
        Parent parent = fxmlLoader.load();
        PaymentAddCheckController addCheckController = fxmlLoader.getController();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);

        addCheckController.getAmount_tf().setOnAction(actionEvent -> {
            Check check = addCheckController.getCheck();
            if (check != null){
                if (this.checks == null || this.checks.size() == 0)
                    this.checks = FXCollections.observableArrayList();
                this.checks.add(check);
                this.checks_lv.setItems(this.checks);
                this.total_paid_tf.setText(Utility.formatDecimal(this.computeTotalPayments()));
                dialog.close();
            }
        });

        addCheckController.getAmount_tf().setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.ESCAPE){
                dialog.close();
                payment_tf.requestFocus();
            }else if (event.getCode() == KeyCode.ENTER) {
                Check check = addCheckController.getCheck();
                if (check != null){
                    if (this.checks == null || this.checks.size() == 0)
                        this.checks = FXCollections.observableArrayList();
                    this.checks.add(check);
                    this.checks_lv.setItems(this.checks);
                    this.total_paid_tf.setText(Utility.formatDecimal(this.computeTotalPayments()));
                    dialog.close();
                }
            }
        });

        addCheckController.getAdd_btn().setOnAction(actionEvent -> {
            Check check = addCheckController.getCheck();
            if (check != null){
                if (this.checks == null || this.checks.size() == 0)
                    this.checks = FXCollections.observableArrayList();
                this.checks.add(check);
                this.checks_lv.setItems(this.checks);
                this.total_paid_tf.setText(Utility.formatDecimal(this.computeTotalPayments()));
                dialog.close();
            }
        });
        dialog.setOnDialogOpened((event) -> { addCheckController.getBank_tf().requestFocus(); });
        dialog.setOnDialogClosed((event) -> { payment_tf.requestFocus(); });
        dialog.show();
    }
    /**
     * Displays Waive Form UI
     * @param bill the bill object reference
     * @param table the table to refresh
     * @param total the total label to display amount
     * @return void
     */
    public void showWaiveForm(Bill bill, TableView table, Label total) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_waive_surcharge.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        WaiveChargesController waiveController = fxmlLoader.getController();
        waiveController.setBill(bill);
        waiveController.setData(table, total);
        waiveController.getSave_btn().setOnAction(actionEvent -> {
            double addon = 0;
            try {
                addon = Double.parseDouble(waiveController.getSurcharge_tf().getText());
            }catch (Exception e){
                e.printStackTrace();
            }
            if (addon <= 0){
                dialog.close();
                AlertDialogBuilder.messgeDialog("System Error", "The surcharge cannot be 0!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else {
                waiveController.save();
                this.fees_table.setItems(bills);
                this.setBillInfo(bills);
                this.setPayables();
                this.setMenuActions();
            }
        });
        dialog.setOnDialogClosed((event) -> { payment_tf.requestFocus(); });
        dialog.show();
    }
    /**
     * Displays Authenticate Form UI
     * @param bill the bill object reference
     * @param table the table to refresh
     * @param total the total label to display amount
     * @return void
     */
    public void showAuthenticate(Bill bill, TableView table, Label total) throws IOException {
        //Show manager authentication when cancelling surcharge for residential bills
        if (bill.getConsumerType().equals("R") || bill.getConsumerType().equals("RM")) {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_authenticate.fxml"));
            Parent parent = fxmlLoader.load();
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setHeading(new Label("MANAGER AUTHENTICATION"));
            dialogLayout.setBody(parent);
            JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            WaiveConfirmationController waiveController = fxmlLoader.getController();
            waiveController.getPassword_tf().setOnAction(actionEvent -> {
                boolean ok = waiveController.login();
                if (ok) {
                    dialog.close();
                    bill.setSurChargeTax(0);
                    bill.setSurCharge(0);
                    bill.computeTotalAmount();
                    this.fees_table.setItems(bills);
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.setMenuActions();
                }
            });
            waiveController.getUsername_tf().setOnAction(actionEvent -> {
                boolean ok = waiveController.login();
                if (ok) {
                    dialog.close();
                    bill.setSurChargeTax(0);
                    bill.setSurCharge(0);
                    bill.computeTotalAmount();
                    this.fees_table.setItems(bills);
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.setMenuActions();
                }
            });
            waiveController.getUsername_tf().setOnKeyReleased(event -> {
                if (event.getCode() == KeyCode.ESCAPE) {
                    dialog.close();
                    payment_tf.requestFocus();
                }
            });
            waiveController.getAuthenticate_btn().setOnAction(actionEvent -> {
                boolean ok = waiveController.login();
                if (ok) {
                    dialog.close();
                    bill.setSurChargeTax(0);
                    bill.setSurCharge(0);
                    bill.computeTotalAmount();
                    this.fees_table.setItems(bills);
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.setMenuActions();
                }
            });
            dialog.setOnDialogOpened((event) -> {
                waiveController.getUsername_tf().requestFocus();
            });
            dialog.setOnDialogClosed((event) -> {
                payment_tf.requestFocus();
            });
            dialog.show();
        //Else, remove surcharge to non-residential bills directly
        }else{
            bill.setSurChargeTax(0);
            bill.setSurCharge(0);
            bill.computeTotalAmount();
            this.fees_table.setItems(bills);
            this.setBillInfo(bills);
            this.setPayables();
            this.setMenuActions();
        }
    }
    /**
     * Displays Payment Confirmation UI
     * @return void
     */
    public void showConfirmation(List<Bill> bills, double amount_due, List<Check> checks) throws IOException {
        payment_tf.removeEventHandler(ActionEvent.ACTION, confirmEvent);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_payment_confirmation.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label("PAYMENT CONFIRMATION"));
        dialogLayout.setBody(parent);
        dialogConfirm = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM, true);
        PaymentConfirmationController controller = fxmlLoader.getController();
        controller.setPayments(bills, amount_due, cash, checks);
        controller.setBills(bills);

        toDeposit = 0;

        try{
            toDeposit = Double.parseDouble(controller.getChange_tf().getText().replace(",",""));
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        controller.getCash_tf().setOnAction(actionEvent -> {
            Object obj = controller.getAccount_list().getSelectionModel().getSelectedItem();

            if (obj == null && controller.checkDeposit()) {
                controller.getStatus_label().setText("Deposit checkbox is enabled. Select an account to proceed!");
                return;
            }else{
                controller.getStatus_label().setText("");
            }
            PaidBill selection = null;
            if (obj != null) selection = (PaidBill) obj;
            try{
                cash = Double.parseDouble(controller.getCash_tf().getText().replace(",",""));
            }catch (Exception e){

            }
            this.transact(bills, cash, checks, dialogConfirm, controller.checkDeposit(), toDeposit, selection);
        });

        controller.getCash_tf().setOnKeyReleased(event ->{
            //Close if ESC is pressed
            if (event.getCode() == KeyCode.ESCAPE) {
                dialogConfirm.close();
            //Transact if SPACE is pressed
            }else if (event.getCode() == KeyCode.SPACE){
                Object obj = controller.getAccount_list().getSelectionModel().getSelectedItem();

                if (obj == null && controller.checkDeposit()) {
                    controller.getStatus_label().setText("Deposit checkbox is enabled. Select an account to proceed!");
                    return;
                }else{
                    controller.getStatus_label().setText("");
                }
                PaidBill selection = null;
                if (obj != null) selection = (PaidBill) obj;
                try{
                    cash = Double.parseDouble(controller.getCash_tf().getText().replace(",",""));
                }catch (Exception e){

                }
                this.transact(bills, cash, checks, dialogConfirm, controller.checkDeposit(), toDeposit, selection);
            //If numeric keys, set digits in text field
            }else if (event.getCode() == KeyCode.ALPHANUMERIC) {
                try {
                    cash = Double.parseDouble(controller.getCash_tf().getText().replace(",", ""));
                } catch (Exception e) {

                }
            }
        });
        controller.getConfirm_btn().setOnAction(action ->{
            Object obj = controller.getAccount_list().getSelectionModel().getSelectedItem();

            if (obj == null && controller.checkDeposit()) {
                controller.getStatus_label().setText("Deposit checkbox is enabled. Select an account to proceed!");
                return;
            }else{
                controller.getStatus_label().setText("");
            }

            PaidBill selection = null;
            if (obj != null) selection = (PaidBill) obj;
            try{
                cash = Double.parseDouble(controller.getCash_tf().getText().replace(",",""));
            }catch (Exception e){

            }
            this.transact(bills, cash, checks, dialogConfirm, controller.checkDeposit(), toDeposit, selection);
            //}
        });
        dialogConfirm.setOnDialogOpened((event) -> { controller.getCash_tf().requestFocus(); });
        dialogConfirm.setOnDialogClosed((event) -> { payment_tf.requestFocus(); payment_tf.addEventHandler(ActionEvent.ACTION, confirmEvent); });
        dialogConfirm.show();
    }

    /**
     * Displays Add Adjustment UI
     * @return void
     */
    public void showAdjustment(Bill bill, String type){
        JFXButton accept = new JFXButton("Add "+type+" Adjustment");
        JFXTextField input = new JFXTextField();
        InputValidation.restrictNumbersOnly(input);
        JFXDialog dialog = DialogBuilder.showInputDialog("Add "+type+" Adjustment","Enter "+type+" Adjustment:  ", "0.00", input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setOnAction(action -> {
                try {
                    if (input.getText().length() == 0) {
                        AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid amount!",
                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }else {
                        double amount = Double.parseDouble(input.getText());
                        if (type.equals("SL")){
                            bill.setSlAdjustment(amount);
                        }else{
                            bill.setOtherAdjustment(amount);
                        }
                        bill.computeTotalAmount();
                        this.fees_table.refresh();
                        this.setBillInfo(bills);
                        this.setPayables();
                        this.setMenuActions();
                    }
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                dialog.close();
        });
        dialog.setOnDialogOpened((event) -> {input.requestFocus();});
        dialog.setOnDialogClosed((event) -> {this.payment_tf.requestFocus(); });
        dialog.show();
    }

    /**
     * Displays Withholding & TIN UI
     * @return void
     */
    public void showTIN(Bill bill, String type) throws Exception{
        payment_tf.removeEventHandler(ActionEvent.ACTION, confirmEvent);
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_withholding.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label("2307 5%/2% & TIN"));
        dialogLayout.setBody(parent);
        dialogConfirm = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM, true);
        WithHoldingController controller = fxmlLoader.getController();
        double withholding = 0;
        EventHandler saveEvent = evt -> {
            try{
                double amount = Double.parseDouble(controller.getWithhold().getText());
                String tn = controller.getTin_tf().getText();
                if (type.equals("2306")) {
                    bill.setCh2306(amount);
                    bill.setForm2306(tn);
                }else{
                    bill.setCh2307(amount);
                    bill.setForm2307(tn);
                }
                ConsumerDAO.updateTIN(bill.getConsumer(), tn);
                bill.getConsumer().setTINNo(tn);
                bill.computeTotalAmount();
                this.fees_table.refresh();
                this.setBillInfo(bills);
                this.setPayables();
                dialogConfirm.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        };

        if (type.equals("2306")) {
            withholding = BillDAO.getForm2306(bill);
            controller.getWithhold_lbl().setText("2307 5%");
        } else {
            withholding = BillDAO.getForm2307(bill);
            controller.getWithhold_lbl().setText("2307 2%");
        }
        controller.getWithhold().setText(withholding+"");
        controller.getTin_tf().setText(bill.getConsumer().getTINNo() != null ? bill.getConsumer().getTINNo() : "");

        controller.getTin_tf().setOnKeyReleased(event->{
            if (event.getCode() == KeyCode.ESCAPE) {
                dialogConfirm.close();
            }else if (event.getCode() == KeyCode.ENTER) {
                try{
                    double amount = Double.parseDouble(controller.getWithhold().getText());
                    String tn = controller.getTin_tf().getText();
                    if (type.equals("2306")) {
                        bill.setCh2306(amount);
                        bill.setForm2306(tn);
                    }else{
                        bill.setCh2307(amount);
                        bill.setForm2307(tn);
                    }
                    ConsumerDAO.updateTIN(bill.getConsumer(), tn);
                    bill.getConsumer().setTINNo(tn);
                    bill.computeTotalAmount();
                    this.fees_table.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    dialogConfirm.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        controller.getWithhold().setOnKeyReleased(event->{
            if (event.getCode() == KeyCode.ESCAPE) {
                dialogConfirm.close();
            }else if (event.getCode() == KeyCode.ENTER) {
                try{
                    double amount = Double.parseDouble(controller.getWithhold().getText());
                    String tn = controller.getTin_tf().getText();
                    if (type.equals("2306")) {
                        bill.setCh2306(amount);
                        bill.setForm2306(tn);
                    }else{
                        bill.setCh2307(amount);
                        bill.setForm2307(tn);
                    }
                    ConsumerDAO.updateTIN(bill.getConsumer(), tn);
                    bill.getConsumer().setTINNo(tn);
                    bill.computeTotalAmount();
                    this.fees_table.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    dialogConfirm.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        controller.getSave_btn().addEventHandler(ActionEvent.ACTION, saveEvent);

        dialogConfirm.setOnDialogOpened((event) -> { controller.getWithhold().requestFocus(); });
        dialogConfirm.setOnDialogClosed((event) -> { payment_tf.requestFocus(); payment_tf.addEventHandler(ActionEvent.ACTION, confirmEvent); });
        dialogConfirm.show();
    }

    /**
     * Displays menu items when table is clicked
     * @return void
     */
    public void setMenuActions(){
        this.fees_table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    ConsumerInfo consumer = row.getItem().getConsumer();
                    this.setBillInfo(bills);
                    this.setConsumerInfo(consumer);
                }
            });
            //Popup Menu when right click table row
            if (ActiveUser.getUser().can("manage-tellering") || ActiveUser.getUser().can("manage-cashiering")) {
                final ContextMenu rowMenu = new ContextMenu();

                MenuItem itemExcludeBill = new MenuItem("Exclude Bill");
                itemExcludeBill.setOnAction(actionEvent -> {
                    if (this.excluded_bills.size() == 0){
                        this.tableBox.getChildren().add(excluded_table);
                    }
                    this.excluded_bills.add(row.getItem());
                    this.excluded_table.setItems(this.excluded_bills);
                    this.excluded_table.refresh();
                    this.fees_table.getItems().remove(row.getItem());
                    tv.refresh();
                    this.setPayables();
                    if (this.fees_table.getItems().size() == 0) {
                        this.reset();
                        this.resetBillInfo();
                    }else{
                        this.payment_tf.requestFocus();
                        this.setBillInfo(this.bills);
                    }
                    this.billsNo_lbl.setText(bills.size()+"");
                });

                MenuItem itemAddPPD = new MenuItem("Less PPD");
                itemAddPPD.setOnAction(actionEvent -> {
                    //Only CL & CS with >= 1kwh, I, B, E, AND DAYS BEFORE DUE DATE
                    if (
                            (row.getItem().getConsumerType().equals("B")
                             || row.getItem().getConsumerType().equals("E")
                             || row.getItem().getConsumerType().equals("I")
                             || ((row.getItem().getConsumerType().equals("CL") || row.getItem().getConsumerType().equals("CS")) && row.getItem().getPowerKWH() >= 1000)
                            )
                       && row.getItem().getDaysDelayed() <= 0
                      ) {
                        JFXButton ppdBtn = new JFXButton("Add PPD");
                        JFXTextField input = new JFXTextField();
                        InputValidation.restrictNumbersOnly(input);
                        JFXDialog dialog = DialogBuilder.showInputDialog("Less PPD","Enter Discount Amount:  ", "0.00", input, ppdBtn, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
                        ppdBtn.setOnAction(action-> {
                            if (input.getText().length() == 0) {
                                AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid amount!",
                                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                            } else {
                                double amount = 0;
                                try {
                                    amount = Double.parseDouble(input.getText());
                                } catch (Exception e) {

                                }
                                row.getItem().setDiscount(amount);
                                row.getItem().computeTotalAmount();
                                tv.refresh();
                                this.setBillInfo(bills);
                                this.setPayables();
                            }
                            dialog.close();
                        });
                        dialog.setOnDialogOpened((event) -> { input.requestFocus(); });
                        dialog.setOnDialogClosed((event) -> {this.payment_tf.requestFocus(); });
                        dialog.show();
                    }else{
                        AlertDialogBuilder.messgeDialog("System Error", "Only consumer types: BAPA, ECA, and I, CL, CS with more than 1KWH can avail the 1%/3% discount on or before due date!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        this.payment_tf.requestFocus();
                    }
                });

                MenuItem itemRemovePPD = new MenuItem("Remove PPD");
                itemRemovePPD.setOnAction(actionEvent -> {
                    row.getItem().setDiscount(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                MenuItem itemClear = new MenuItem("Clear");
                itemClear.setOnAction(actionEvent -> {
                    row.getItem().setDiscount(0);
                    row.getItem().setCh2306(0);
                    row.getItem().setCh2307(0);
                    row.getItem().setSlAdjustment(0);
                    row.getItem().setOtherAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                MenuItem itemAddSurcharge = new MenuItem("Add Surcharge Manually");
                itemAddSurcharge.setOnAction(actionEvent -> {
                    try {
                        showWaiveForm(row.getItem(), this.fees_table, this.total_payable_lbl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                MenuItem itemWaiveSurcharge = new MenuItem("Remove Surcharge");
                itemWaiveSurcharge.setOnAction(actionEvent -> {
                    try {
                        showAuthenticate(row.getItem(), this.fees_table, this.total_payable_lbl);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });

                MenuItem item2306 = new MenuItem("2307 5%");
                item2306.setOnAction(actionEvent -> {
                    if (row.getItem().getConsumerType().equals("RM")) {
                        this.payment_tf.requestFocus();
                        return;
                    }
                    try {
                        this.showTIN(row.getItem(), "2306");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                MenuItem item2307 = new MenuItem("2307 2%");
                item2307.setOnAction(event -> {
                    if (row.getItem().getConsumerType().equals("RM")) {
                        this.payment_tf.requestFocus();
                        return;
                    }
                    try {
                        this.showTIN(row.getItem(), "2307");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                MenuItem itemSLAdj = new MenuItem("Add SL Adjustment");
                itemSLAdj.setOnAction(actionEvent -> {
                    this.showAdjustment(row.getItem(),"SL");
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                MenuItem itemRemoveSLAdj = new MenuItem("Remove SL Adjustment");
                itemRemoveSLAdj.setOnAction(actionEvent -> {
                    row.getItem().setSlAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                MenuItem itemOthersAdj = new MenuItem("Add Other Deduction");
                itemOthersAdj.setOnAction(actionEvent -> {
                    this.showAdjustment(row.getItem(),"Other");
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                MenuItem itemRemoveOthersAdj = new MenuItem("Remove Other Deduction");
                itemRemoveOthersAdj.setOnAction(actionEvent -> {
                    row.getItem().setOtherAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                    this.payment_tf.requestFocus();
                });

                rowMenu.getItems().addAll(itemExcludeBill, new SeparatorMenuItem(), itemClear, new SeparatorMenuItem(), itemAddPPD, itemRemovePPD,  new SeparatorMenuItem(), itemAddSurcharge, itemWaiveSurcharge,  new SeparatorMenuItem(), item2306, item2307,  new SeparatorMenuItem(), itemSLAdj, itemRemoveSLAdj,  new SeparatorMenuItem(), itemOthersAdj, itemRemoveOthersAdj);

                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(rowMenu));
            }
            return row;
        });
    }

    /**
     * Transacts powerbills payment
     * @return void
     */
    public void transact(List<Bill> bills, double cash, List<Check> checks, JFXDialog dialog, boolean deposit, double change, PaidBill account){
        this.progressBar.setVisible(true);
        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            List<Bill> updated = Utility.processor(bills, cash, checks, ActiveUser.getUser().getUserName());
            BillDAO.addPaidBill(updated, change, deposit, account);
            for (Bill b : updated) {

                PrintOEBR print = new PrintOEBR((PaidBill) b);

                print.setOnFailed(e -> {
                    AlertDialogBuilder.messgeDialog("System Error", "Print error due to: " + print.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                });

                print.setOnSucceeded(e -> {
                    System.out.println("Printing was successful.");
                });

                print.setOnRunning(e -> {

                });

                Thread t = new Thread(print);
                exec.submit(t);
            }
            this.reset();
            dialog.close();
        } catch (SQLException ex) {
            AlertDialogBuilder.messgeDialog("System Error", "SQL Problem encountered: " + ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }finally {
            exec.shutdown();
        }
        this.progressBar.setVisible(false);
    }
}
