package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ConsumerDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.application.Platform;
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
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private Label bill_amount_lbl;

    @FXML
    private JFXButton transact_btn;

    @FXML
    private VBox sidebar_vbox;

    @FXML
    private ProgressBar progressBar;

    private ConsumerInfo consumerInfo = null;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<Check> checks = FXCollections.observableArrayList();

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
                    this.setConsumerInfo(this.consumerInfo);
                    try {
                        if (this.bills.size() == 0) this.bills = FXCollections.observableArrayList();
                        this.bills.addAll(BillDAO.getConsumerBills(this.consumerInfo, false));
                        this.setPayables();
                        this.fees_table.setItems(this.bills);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                }else if (event.getClickCount() == 1 && (! row.isEmpty()) ) {
                    this.setBillInfo(row.getItem());
                }
            });

            if (ActiveUser.getUser().can("manage-tellering")) {
                final ContextMenu rowMenu = new ContextMenu();


                MenuItem itemRemoveBill = new MenuItem("Remove Bill");
                itemRemoveBill.setOnAction(actionEvent -> {
                    this.fees_table.getItems().remove(row.getItem());
                    tv.refresh();
                    this.setPayables();
                    this.resetBillInfo();
                    if (this.fees_table.getItems().size() == 0)
                        this.reset();
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
                        this.setBillInfo(row.getItem());
                    }else{
                        AlertDialogBuilder.messgeDialog("System Error", "Only consumer types BAPA, ECA, I, CL, and CS with more than 1KWH can avail the 1% discount on or before due date!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }
                });

                MenuItem itemRemovePPD = new MenuItem("Remove PPD");
                itemRemovePPD.setOnAction(actionEvent -> {
                    row.getItem().setDiscount(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(row.getItem());
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
                    this.setBillInfo(row.getItem());
                    this.setPayables();
                });

                MenuItem itemAddSurcharge = new MenuItem("Add Surcharge Manually");
                itemAddSurcharge.setOnAction(actionEvent -> {
                    try {
                        showAuthenticate(row.getItem(), this.fees_table, this.total_payable_lbl);
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

                MenuItem item2306 = new MenuItem("2306");
                item2306.setOnAction(actionEvent -> {
                    if (row.getItem().getConsumerType().equals("RM")) return;
                    try {
                        this.showTIN(row.getItem(), "2306");
                        row.getItem().setCh2306(BillDAO.getForm2306(row.getItem()));
                        row.getItem().computeTotalAmount();
                        tv.refresh();
                        this.setBillInfo(row.getItem());
                        this.setPayables();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                MenuItem item2307 = new MenuItem("2307");
                item2307.setOnAction(event -> {
                    if (row.getItem().getConsumerType().equals("RM")) return;
                    try {
                        this.showTIN(row.getItem(), "2307");
                        row.getItem().setCh2307(BillDAO.getForm2307(row.getItem()));
                        row.getItem().computeTotalAmount();
                        tv.refresh();
                        this.setBillInfo(row.getItem());
                        this.setPayables();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

                MenuItem itemSLAdj = new MenuItem("Add SL Adjustment");
                itemSLAdj.setOnAction(actionEvent -> {
                    this.showAdjustment(row.getItem(),"SL");
                    tv.refresh();
                    this.setBillInfo(row.getItem());
                    this.setPayables();
                });

                MenuItem itemRemoveSLAdj = new MenuItem("Remove SL Adjustment");
                itemRemoveSLAdj.setOnAction(actionEvent -> {
                    row.getItem().setSlAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(row.getItem());
                    this.setPayables();
                });

                MenuItem itemOthersAdj = new MenuItem("Add Other Deduction");
                itemOthersAdj.setOnAction(actionEvent -> {
                    this.showAdjustment(row.getItem(),"Other");
                    tv.refresh();
                    this.setBillInfo(row.getItem());
                    this.setPayables();
                });

                MenuItem itemRemoveOthersAdj = new MenuItem("Remove Other Deduction");
                itemRemoveOthersAdj.setOnAction(actionEvent -> {
                    row.getItem().setOtherAdjustment(0);
                    row.getItem().computeTotalAmount();
                    tv.refresh();
                    this.setBillInfo(row.getItem());
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
        this.bill_amount_lbl.setText("0.00");
        this.vat_tf.setText("");
        this.add_charges_tf.setText("");
        this.power_amt_tf.setText("");
        this.katas_tf.setText("");
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
        column3.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmountDue())));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Surcharge");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSurCharge())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("2306/07");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal((obj.getValue().getCh2306()+obj.getValue().getCh2307()))));
        column5.setStyle("-fx-alignment: center;");

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
                                            showAuthenticate(bill, fees_table, total_payable_lbl);
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
        columnWaive.setPrefWidth(60);
        columnWaive.setMaxWidth(60);
        columnWaive.setMinWidth(60);
        columnWaive.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Total Amount");
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right;");

        this.bills =  FXCollections.observableArrayList();
        this.fees_table.setFixedCellSize(35);
        this.fees_table.setPlaceholder(new Label("No bills added! Search or input the account number!"));

        this.fees_table.getColumns().add(column0);
        this.fees_table.getColumns().add(column);
        this.fees_table.getColumns().add(column1);
        this.fees_table.getColumns().add(column2);
        this.fees_table.getColumns().add(column3);
        this.fees_table.getColumns().add(column4);

        if (ActiveUser.getUser().can("manage-tellering"))
            this.fees_table.getColumns().add(columnWaive);

        this.fees_table.getColumns().add(column5);
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
     * Sets Bill details to UI
     * @param bill the bill reference
     * @return void
     */
    public void setBillInfo(Bill bill){
        this.add_charges_tf.setText(Utility.formatDecimal(bill.getAddCharges()));
        this.surcharge_tf.setText(Utility.formatDecimal(bill.getSurCharge()));
        this.ppd_tf.setText(Utility.formatDecimal(bill.getDiscount()));
        this.adj_tf.setText(Utility.formatDecimal(bill.getSlAdjustment()+bill.getOtherAdjustment()));
        this.ch2306_2307_tf.setText(Utility.formatDecimal(bill.getCh2306()+bill.getCh2307()));
        this.power_amt_tf.setText(Utility.formatDecimal(bill.getPowerAmount()));
        this.katas_tf.setText(Utility.formatDecimal(bill.getKatas()));
        this.md_refund_tf.setText(Utility.formatDecimal(bill.getMdRefund()));
        this.bill_amount_lbl.setText(Utility.formatDecimal(bill.getTotalAmount()));
        this.vat_tf.setText(Utility.formatDecimal(bill.getVat() + bill.getSurChargeTax()));
        this.add_charges_tf.setText(Utility.formatDecimal(bill.getOtherCharges()+bill.getTransformerRental()));
        this.power_amt_tf.setText(Utility.formatDecimal(bill.getPowerAmount()));
        this.katas_tf.setText(Utility.formatDecimal(bill.getKatas()));
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
        WaiveChargesController waiveController = fxmlLoader.getController();
        waiveController.setBill(bill);
        waiveController.setData(table, total);
        waiveController.getSave_btn().setOnAction(actionEvent -> {
            waiveController.save();
            this.fees_table.refresh();
            this.setBillInfo(bill);
            this.setPayables();
        });
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setBody(parent);
        JFXDialog dialog = new JFXDialog(Utility.getStackPane(), dialogLayout, JFXDialog.DialogTransition.BOTTOM);
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
            for (Bill b: bills){
                try {
                    PaidBill paidBill = (PaidBill) b;
                    paidBill.setTeller(ActiveUser.getUser().getUserName());
                    paidBill.setChecks(checks);
                    double check_amount = Utility.getTotalAmount(this.checks);
                    paidBill.setCashAmount(amount_due - check_amount);
                    paidBill.setCheckAmount(check_amount);
                    BillDAO.addPaidBill(b);
                    this.reset();
                    dialog.close();
                } catch (SQLException ex){
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                } catch (Exception e) {
                    AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
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
                        this.setBillInfo(bill);
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
            try {
                if (input.getText().length() == 0) {
                    AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter TIN!",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else {
                    if (type.equals("2306")){
                        bill.setForm2306(input.getText());
                    }else{
                        bill.setForm2307(input.getText());
                    }
                }
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "Problem encountered: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
            dialog.close();
        });
        dialog.show();
    }

}
