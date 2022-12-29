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
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.print.Printer;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private JFXButton view_account_tf;

    @FXML
    private TableView<Bill> fees_table;

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
    private TextField add_charges_tf;

    @FXML
    private TextField surcharge_tf;

    @FXML
    private TextField ppd_tf;

    @FXML
    private TextField adj_tf;

    @FXML
    private TextField ch2306_2307_tf;

    @FXML
    private TextField power_amt_tf;

    @FXML
    private TextField vat_tf;

    @FXML
    private TextField katas_tf;

    @FXML
    private TextField md_refund_tf;

    @FXML
    private JFXButton transact_btn;

    @FXML
    private VBox sidebar_vbox;

    @FXML
    private ProgressBar progressBar;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<Check> checks = FXCollections.observableArrayList();
    private boolean isPaid = false;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        if (ActiveUser.getUser().can("manage-billing"))
            sidebar_vbox.setVisible(false);

        this.transact_btn.setDisable(true);

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
                        setConsumerInfo(consumerInfo);
                        fees_table.setItems(bills);
                        setPayables();
                        payment_tf.setDisable(false);
                        payment_tf.requestFocus();
                        transact_btn.setDisable(false);
                        total_paid_tf.setDisable(false);
                        InputHelper.restrictNumbersOnly(payment_tf);
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

        this.fees_table.setRowFactory(tv -> {
            TableRow<Bill> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    ConsumerInfo consumer = row.getItem().getConsumer();
                    this.setBillInfo(bills);
                    this.setConsumerInfo(consumer);
                }
            });

            if (ActiveUser.getUser().can("manage-tellering") || ActiveUser.getUser().can("manage-cashiering")) {
                final ContextMenu rowMenu = new ContextMenu();


                MenuItem itemRemoveBill = new MenuItem("Remove Bill");
                itemRemoveBill.setOnAction(actionEvent -> {
                    this.fees_table.getItems().remove(row.getItem());
                    tv.refresh();
                    this.setPayables();
                    if (this.fees_table.getItems().size() == 0) {
                        this.reset();
                        this.resetBillInfo();
                    }else{
                        this.setBillInfo(this.bills);
                    }
                });

                MenuItem itemAddPPD = new MenuItem("Less PPD");
                itemAddPPD.setOnAction(actionEvent -> {
                    //Only I, CL, CS with >= 1kwh, B, E, AND DAYS BEFORE DUE DATE
                    if ((row.getItem().getConsumerType().equals("B")
                        || row.getItem().getConsumerType().equals("E")
                        || row.getItem().getConsumerType().equals("I")
                        || row.getItem().getConsumerType().equals("CL")
                        || (row.getItem().getConsumerType().equals("CS") && row.getItem().getPowerKWH() >= 1000))
                        && row.getItem().getDaysDelayed() <= 0) {
                        double ppd = 0;
                        try {
                            ppd = BillDAO.getDiscount(row.getItem());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        row.getItem().setDiscount(ppd);
                        row.getItem().computeTotalAmount();
                        tv.refresh();
                        this.setBillInfo(bills);
                    }else{
                        AlertDialogBuilder.messgeDialog("System Error", "Only consumer types BAPA, ECA, I, CL, and CS with more than 1KWH can avail the 1% discount on or before due date!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }
                });

                MenuItem itemRemovePPD = new MenuItem("Remove PPD");
                itemRemovePPD.setOnAction(actionEvent -> {
                    row.getItem().setDiscount(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
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
                    if (row.getItem().getConsumerType().equals("RM")) return;
                    try {
                        if (row.getItem().getConsumer().getTINNo() == null || row.getItem().getConsumer().getTINNo().isEmpty() || row.getItem().getConsumer().getTINNo().equals("")) {
                            this.showTIN(row.getItem(), "2306");
                        }else{
                            row.getItem().setForm2306(row.getItem().getConsumer().getTINNo());
                        }
                        row.getItem().setCh2306(BillDAO.getForm2306(row.getItem()));
                        row.getItem().computeTotalAmount();
                        tv.refresh();
                        this.setBillInfo(bills);
                        this.setPayables();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                MenuItem item2307 = new MenuItem("2307 2%");
                item2307.setOnAction(event -> {
                    if (row.getItem().getConsumerType().equals("RM")) return;
                    try {
                        if (row.getItem().getConsumer().getTINNo() == null || row.getItem().getConsumer().getTINNo().isEmpty() || row.getItem().getConsumer().getTINNo().equals("")) {
                            this.showTIN(row.getItem(), "2307");
                        }else{
                            row.getItem().setForm2306(row.getItem().getConsumer().getTINNo());
                        }
                        row.getItem().setCh2307(BillDAO.getForm2307(row.getItem()));
                        row.getItem().computeTotalAmount();
                        tv.refresh();
                        this.setBillInfo(bills);
                        this.setPayables();
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
                });

                MenuItem itemRemoveSLAdj = new MenuItem("Remove SL Adjustment");
                itemRemoveSLAdj.setOnAction(actionEvent -> {
                    row.getItem().setSlAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                });

                MenuItem itemOthersAdj = new MenuItem("Add Other Deduction");
                itemOthersAdj.setOnAction(actionEvent -> {
                    this.showAdjustment(row.getItem(),"Other");
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                });

                MenuItem itemRemoveOthersAdj = new MenuItem("Remove Other Deduction");
                itemRemoveOthersAdj.setOnAction(actionEvent -> {
                    row.getItem().setOtherAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(bills);
                    this.setPayables();
                });

                rowMenu.getItems().addAll(itemRemoveBill, new SeparatorMenuItem(), itemClear, new SeparatorMenuItem(), itemAddPPD, itemRemovePPD,  new SeparatorMenuItem(), itemAddSurcharge, itemWaiveSurcharge,  new SeparatorMenuItem(), item2306, item2307,  new SeparatorMenuItem(), itemSLAdj, itemRemoveSLAdj,  new SeparatorMenuItem(), itemOthersAdj, itemRemoveOthersAdj);

                row.contextMenuProperty().bind(
                        Bindings.when(row.emptyProperty())
                                .then((ContextMenu) null)
                                .otherwise(rowMenu));
            }
            return row;
        });

        this.payment_tf.setOnKeyTyped(keyEvent -> {
            this.setPayments();
        });

        this.payment_tf.setOnAction(actionEvent -> {
            this.confirmPayment();
        });

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
                        Double.parseDouble(this.payment_tf.getText().replace(",","")),
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
        double total = 0;
        try {
            double cash = Double.parseDouble(this.payment_tf.getText());
            double amount = Utility.getTotalAmount(this.checks);
            total = cash + amount;
        }catch (Exception e){

        }
        return total;
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
        this.total_payable_lbl.setText("0.00");
        this.payment_tf.setText("0.00");
        this.total_paid_tf.setText("0.00");
        this.total_paid_tf.setDisable(true);
        this.payment_tf.setDisable(true);
        this.bills = FXCollections.observableArrayList(new ArrayList<>());
        this.fees_table.setItems(this.bills);
        this.transact_btn.setDisable(true);
        this.resetChecks();
        this.resetBillInfo();
        this.acct_no_tf.requestFocus();
    }
    /**
     * Resets check details
     * @return void
     */
    public void resetChecks(){
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
        this.adj_tf.setText("");
        this.ch2306_2307_tf.setText("");
        this.power_amt_tf.setText("");
        this.katas_tf.setText("");
        this.vat_tf.setText("");
        this.md_refund_tf.setText("");
    }
    /**
     * Displays Advance Consumer Search UI
     * @return void
     */
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
        column1.setPrefWidth(140);
        column1.setMaxWidth(140);
        column1.setMinWidth(140);
        column1.setCellValueFactory(new PropertyValueFactory<>("billMonth"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Due Date");
        column2.setPrefWidth(100);
        column2.setMaxWidth(100);
        column2.setMinWidth(100);
        column2.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Bill Amount");
        column3.setPrefWidth(140);
        column3.setMaxWidth(140);
        column3.setMinWidth(140);
        column3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getAmountDue()
        )));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column31 = new TableColumn<>("Pwr Amount");
        column31.setPrefWidth(140);
        column31.setMaxWidth(140);
        column31.setMinWidth(140);
        column31.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getPowerAmount()
        )));
        column31.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column32 = new TableColumn<>("TSF");
        column32.setPrefWidth(100);
        column32.setMaxWidth(100);
        column32.setMinWidth(100);
        column32.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getTransformerRental()
        )));
        column32.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column33 = new TableColumn<>("Others");
        column33.setPrefWidth(100);
        column33.setMaxWidth(100);
        column33.setMinWidth(100);
        column33.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(
                obj.getValue().getOtherCharges()
        )));
        column33.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Penalty");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSurCharge())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column41 = new TableColumn<>("PPD");
        column41.setPrefWidth(100);
        column41.setMaxWidth(100);
        column41.setMinWidth(100);
        column41.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getDiscount())));
        column41.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column42 = new TableColumn<>("SL Adj");
        column42.setPrefWidth(100);
        column42.setMaxWidth(100);
        column42.setMinWidth(100);
        column42.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        column42.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column43 = new TableColumn<>("Other Adj");
        column43.setPrefWidth(100);
        column43.setMaxWidth(100);
        column43.setMinWidth(100);
        column43.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getOtherAdjustment())));
        column43.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column44 = new TableColumn<>("KWH");
        column44.setPrefWidth(75);
        column44.setMaxWidth(75);
        column44.setMinWidth(75);
        column44.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getPowerKWH())));
        column44.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column45 = new TableColumn<>("VAT");
        column45.setPrefWidth(100);
        column45.setMaxWidth(100);
        column45.setMinWidth(100);
        column45.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getVat() + obj.getValue().getSurChargeTax())));
        column45.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column46 = new TableColumn<>("KatasAMT");
        column46.setPrefWidth(75);
        column46.setMaxWidth(75);
        column46.setMinWidth(75);
        column46.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getKatas())));
        column46.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column47 = new TableColumn<>("MD Refund");
        column47.setPrefWidth(75);
        column47.setMaxWidth(75);
        column47.setMinWidth(75);
        column47.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getMdRefund())));
        column47.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("2307 5%");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2306()))));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column51 = new TableColumn<>("2307 2%");
        column51.setPrefWidth(100);
        column51.setMaxWidth(100);
        column51.setMinWidth(100);
        column51.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2307()))));
        column51.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column52 = new TableColumn<>("TIN");
        column52.setPrefWidth(140);
        column52.setMaxWidth(140);
        column52.setMinWidth(140);
        column52.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getTINNo()));
        column52.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Total Amount");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right;");
        column7.setPrefWidth(140);
        column7.setMaxWidth(140);
        column7.setMinWidth(140);

        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setFixedCellSize(35);
        this.fees_table.setPlaceholder(new Label("No bills added! Search or input the account number!"));

        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column);
        this.fees_table.getColumns().add(column1);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column3);
        this.fees_table.getColumns().add(column31);
        this.fees_table.getColumns().add(column32);
        this.fees_table.getColumns().add(column33);
        this.fees_table.getColumns().add(column4);
        this.fees_table.getColumns().add(column41);
        this.fees_table.getColumns().add(column42);
        this.fees_table.getColumns().add(column43);
        this.fees_table.getColumns().add(column44);
        this.fees_table.getColumns().add(column45);
        this.fees_table.getColumns().add(column46);
        this.fees_table.getColumns().add(column47);
        this.fees_table.getColumns().add(column5);
        this.fees_table.getColumns().add(column51);
        this.fees_table.getColumns().add(column52);
        this.fees_table.getColumns().add(column7);
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
                    InputHelper.restrictNumbersOnly(payment_tf);
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
    /**
     * Sets total bill details to UI
     * @param current_bills the list of bills
     * @return void
     */
    public void setBillInfo(List<Bill> current_bills){
        double add_charge_sum = 0, surcharge_sum = 0, ppd_sum = 0, adj_sum = 0, ch06_07_sum=0, power_amt_sum = 0, md_refund_sum = 0,
                vat_sum = 0, katas_sum = 0;
        for(Bill b : current_bills){
            add_charge_sum += b.getOtherCharges() + b.getTransformerRental();
            surcharge_sum += b.getSurCharge();
            ppd_sum += b.getDiscount();
            adj_sum += b.getSlAdjustment() + b.getOtherAdjustment();
            ch06_07_sum += b.getCh2306() + b.getCh2307();
            power_amt_sum += b.getPowerAmount();
            md_refund_sum += b.getMdRefund();
            vat_sum += b.getVat() + b.getSurChargeTax();
            katas_sum += b.getKatas();
        }
        this.surcharge_tf.setText(Utility.formatDecimal(surcharge_sum));
        this.ppd_tf.setText(Utility.formatDecimal(ppd_sum));
        this.adj_tf.setText(Utility.formatDecimal(adj_sum));
        this.ch2306_2307_tf.setText(Utility.formatDecimal(ch06_07_sum));
        this.power_amt_tf.setText(Utility.formatDecimal(power_amt_sum));
        this.katas_tf.setText(Utility.formatDecimal(katas_sum));
        this.md_refund_tf.setText(Utility.formatDecimal(md_refund_sum));
        this.vat_tf.setText(Utility.formatDecimal(vat_sum));
        this.add_charges_tf.setText(Utility.formatDecimal(add_charge_sum));
    }
    /**
     * Displays Add Check UI
     * @return void
     */
    public void showAddCheckForm() throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_add_check.fxml"));
        Parent parent = fxmlLoader.load();
        PaymentAddCheckController addCheckController = fxmlLoader.getController();
        addCheckController.getAdd_btn().setOnAction(actionEvent -> {
            Check check = addCheckController.getCheck();
            if (check != null){
                if (this.checks == null || this.checks.size() == 0)
                    this.checks = FXCollections.observableArrayList();
                this.checks.add(check);
                this.checks_lv.setItems(this.checks);
                double amount = Double.parseDouble(this.total_paid_tf.getText().replace(",",""));
                this.total_paid_tf.setText(Utility.formatDecimal(amount+check.getAmount()));
            }
        });
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
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
                this.fees_table.refresh();
                this.setBillInfo(bills);
                this.setPayables();
            }
        });
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
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_authenticate.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label("MANAGER AUTHENTICATION"));
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        WaiveConfirmationController waiveController = fxmlLoader.getController();
        waiveController.getAuthenticate_btn().setOnAction(actionEvent -> {
            boolean ok = waiveController.login();
            if (ok) {
                try {
                    dialog.close();
                    showWaiveForm(bill, table, total);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        dialog.show();
    }
    /**
     * Displays Payment Confirmation UI
     * @return void
     */
    public void showConfirmation(List<Bill> bills, double amount_due, double cash, List<Check> checks) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../tellering/tellering_payment_confirmation.fxml"));
        Parent parent = fxmlLoader.load();
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setHeading(new Label("PAYMENT CONFIRMATION"));
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
        PaymentConfirmationController controller = fxmlLoader.getController();
        controller.setPayments(bills, amount_due, cash, checks);
        controller.getConfirm_btn().setOnAction(action ->{
            //Get the default printer
            Printer printer = Printer.getDefaultPrinter();

            //Check if the default printer is not LQ-310 and prompt error, otherwise proceed to batch transaction and printing of oebr
            if (printer.getName().contains("PDF") || printer.getName().contains("Fax") || printer.getName().contains("XPS") || printer.getName().contains("OneNote")) {
                dialog.close();
                AlertDialogBuilder.messgeDialog("System Error", "The default printer is not set! Please set the printer before printing!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else {
                List<Bill> updated = Utility.processor(bills, cash, checks, ActiveUser.getUser().getUserName());
                this.progressBar.setVisible(true);
                try {
                    BillDAO.addPaidBill(updated);
                    for (Bill b : updated) {
                        CustomPrintHelper print = new CustomPrintHelper("OEBR", 18, 3, (PaidBill) b);

                        print.prepareDocument();

                        print.setOnFailed(e -> {
                            System.out.println("Error when printing the OEBR!");
                        });

                        print.setOnSucceeded(e -> {
                            System.out.println("Successful");
                        });

                        print.setOnRunning(e -> {
                        });

                        Thread t = new Thread(print);

                        t.start();
                    }
                    this.reset();
                    dialog.close();
                } catch (SQLException ex) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                this.progressBar.setVisible(false);
            }
        });
        dialog.show();
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
                    }
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                dialog.close();
        });
        dialog.show();
    }

    /**
     * Displays TIN UI
     * @return void
     */
    public void showTIN(Bill bill, String type){
        JFXButton accept = new JFXButton("SAVE TIN");
        JFXTextField input = new JFXTextField();
        JFXDialog dialog = DialogBuilder.showInputDialog("TIN Entry for "+type,"Enter TIN:  ", "", input, accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setOnAction(action -> {
            boolean repeat = true;
            while (repeat) {
                try {
                    if (input.getText().length() == 0) {
                        AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter TIN!",
                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    } else {
                        ConsumerDAO.updateTIN(bill.getConsumer(), input.getText());
                        bill.getConsumer().setTINNo(input.getText());
                        if (type.equals("2306")) {
                            bill.setForm2306(input.getText());
                        } else {
                            bill.setForm2307(input.getText());
                        }
                        repeat = false;
                    }
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            }
            dialog.close();
        });
        dialog.show();
    }

}
