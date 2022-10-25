package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.ItemSummary;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import com.jfoenix.controls.JFXButton;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class DCRController extends MenuControllerHandler implements Initializable {

    @FXML
    private ProgressBar progressbar;

    @FXML
    private DatePicker date_pker;

    @FXML
    private JFXButton view_btn;

    @FXML
    private JFXButton print_dcr_btn;

    @FXML
    private TableView<ItemSummary> dcr_breakdown_table;

    @FXML
    private TableView<ItemSummary> payments_table;

    @FXML
    private TableView<Bill> dcr_power_table;

    @FXML
    private Label dcr_total_kwh, dcr_total, dcr_transNo_lbl;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();
    private ObservableList<ItemSummary> dcrItems = FXCollections.observableArrayList();
    private ObservableList<ItemSummary> dcrPayments = FXCollections.observableArrayList();
    private double totalKwh = 0, grandTotal = 0, billTotal = 0, amountDue = 0, cashAmount = 0, checkAmount = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.date_pker.setValue(LocalDate.now());
        this.createDCRTransactionsTable();
        this.createDCRSummary();
        this.createDCRBreakDown();
        this.view_btn.setOnAction(action ->{
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    int day=0, year=0, month=0;
                    totalKwh = 0;
                    grandTotal = 0;
                    cashAmount = 0;
                    checkAmount = 0;
                    try{
                        month = date_pker.getValue().getMonthValue();
                        day = date_pker.getValue().getDayOfMonth();
                        year = date_pker.getValue().getYear();
                        bills = FXCollections.observableArrayList(BillDAO.getAllPaidBills(year, month, day, "engel"));

                        ItemSummary energy = new ItemSummary("Energy", 0);
                        ItemSummary tr = new ItemSummary("TSF/TR", 0);
                        ItemSummary others = new ItemSummary("Others", 0);
                        ItemSummary surCharge = new ItemSummary("Surcharge", 0);
                        ItemSummary evat = new ItemSummary("Evat", 0);
                        ItemSummary slAdjustments = new ItemSummary("S/L Adjustments", 0);
                        ItemSummary ppd = new ItemSummary("PPD", 0);
                        ItemSummary katasvat = new ItemSummary("Katas Ng VAT", 0);
                        ItemSummary otherDeductions = new ItemSummary("Other Deductions", 0);
                        ItemSummary mdRefund = new ItemSummary("MD Refund", 0);
                        ItemSummary scDiscount = new ItemSummary("SC Discount", 0);
                        ItemSummary ch2307 = new ItemSummary("2307 (5%)", 0);
                        ItemSummary ch2306 = new ItemSummary("2307 (2%)", 0);
                        ItemSummary arVATTrans = new ItemSummary("ARVAT - Trans", 0);
                        ItemSummary arVATGen = new ItemSummary("ARVAT - Gen", 0);

                        ItemSummary cashPayments = new ItemSummary("Cash", 0);
                        ItemSummary checkPayments = new ItemSummary("Check", 0);
                        ItemSummary totalPayments = new ItemSummary("Total", 0);

                        double power = 0, pr = 0, meter = 0, other = 0, surcharge = 0, slAdj = 0, pDisc = 0, katasVat = 0, otherDeduct = 0, mdRef = 0,
                                seniorDiscount = 0, a2307 = 0, a2306 = 0, transAmt = 0, genAmt = 0;

                        for (Bill b: bills) {
                            PaidBill bill = (PaidBill) b;
                            power += bill.getPower();
                            pr += bill.getPr();
                            other += bill.getOthers();
                            surcharge += bill.getSurCharge();
                            meter += bill.getMeter();
                            slAdj += bill.getSLAdjustment();
                            pDisc += bill.getPromptPayment();
                            katasVat += bill.getKatasNgVat();
                            otherDeduct += bill.getOtherDeduction();
                            mdRef += bill.getMdRefund();
                            seniorDiscount += bill.getScDiscount();
                            a2307 += bill.getAmount2307();
                            a2306 += bill.getAmount2306();
                            transAmt += bill.getArTran();
                            genAmt += bill.getArGen();
                            totalKwh += bill.getPowerKWH();
                            cashAmount += bill.getCashAmount();
                            checkAmount += bill.getCheckAmount();

                            energy.setTotal(power);
                            tr.setTotal(pr);
                            others.setTotal(other);
                            surCharge.setTotal(surcharge);
                            evat.setTotal(meter);
                            slAdjustments.setTotal(slAdj);
                            ppd.setTotal(pDisc);
                            katasvat.setTotal(katasVat);
                            otherDeductions.setTotal(otherDeduct);
                            mdRefund.setTotal(mdRef);
                            scDiscount.setTotal(seniorDiscount);
                            ch2306.setTotal(a2306);
                            ch2307.setTotal(a2307);
                            arVATTrans.setTotal(transAmt);
                            arVATGen.setTotal(genAmt);
                            amountDue += b.getAmountDue();
                            billTotal += b.getTotalAmount();
                        }
                        dcrItems = FXCollections.observableArrayList();
                        dcrPayments = FXCollections.observableArrayList();

                        dcrItems.add(energy);
                        dcrItems.add(tr);
                        dcrItems.add(others);
                        dcrItems.add(surCharge);
                        dcrItems.add(evat);
                        dcrItems.add(slAdjustments);
                        dcrItems.add(ppd);
                        dcrItems.add(katasvat);
                        dcrItems.add(otherDeductions);
                        dcrItems.add(mdRefund);
                        dcrItems.add(scDiscount);
                        dcrItems.add(ch2306);
                        dcrItems.add(ch2307);
                        dcrItems.add(arVATTrans);
                        dcrItems.add(arVATGen);

                        cashPayments.setTotal(cashAmount);
                        checkPayments.setTotal(checkAmount);
                        totalPayments.setTotal(cashAmount + checkAmount);
                        dcrPayments.add(cashPayments);
                        dcrPayments.add(checkPayments);
                        dcrPayments.add(totalPayments);

                        grandTotal = (power + pr + meter + other + surcharge + transAmt + genAmt) - (slAdj + pDisc + katasVat + otherDeduct + mdRef +
                                seniorDiscount + a2307 + a2306) ;

                    }catch (Exception e){
                        e.printStackTrace();
                        AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request: "+e.getMessage(),
                                Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                view_btn.setDisable(true);
                progressbar.setVisible(true);
                dcr_total_kwh.setText("0");
                dcr_total.setText("0");
            });

            task.setOnSucceeded(wse -> {
                view_btn.setDisable(false);
                dcr_power_table.setItems(bills);
                dcr_breakdown_table.setItems(dcrItems);
                payments_table.setItems(dcrPayments);
                progressbar.setVisible(false);
                dcr_transNo_lbl.setText(bills.size()+"");
                dcr_total_kwh.setText(Utility.formatDecimal(totalKwh));
                dcr_total.setText(Utility.formatDecimal(grandTotal));

                System.out.println("Bill Total: "+billTotal);
                System.out.println("Bill Total Amount Due: "+amountDue);
                System.out.println("DCR Total: "+grandTotal);
                System.out.println("Bill Total - DCR Total: "+(billTotal-grandTotal));
            });

            task.setOnFailed(wse -> {
                dcr_transNo_lbl.setText("0");
                view_btn.setDisable(false);
                progressbar.setVisible(false);
                dcr_total_kwh.setText("0");
                dcr_total.setText("0");
                dcr_breakdown_table.setItems(FXCollections.observableArrayList());
                dcr_power_table.setItems(FXCollections.observableArrayList());
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while processing the request! Please try again!",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            });

            new Thread(task).start();
        });

    }

    /**
     * Initializes the DCR transactions table
     * @return void
     */
    public void createDCRTransactionsTable(){
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

        TableColumn<Bill, String> column1 = new TableColumn<>("Consumer Name");
        column1.setCellValueFactory(obj -> new SimpleStringProperty(obj.getValue().getConsumer().getConsumerName()));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<Bill, String> column2 = new TableColumn<>("Bill Balance");
        column2.setPrefWidth(120);
        column2.setMaxWidth(120);
        column2.setMinWidth(120);
        column2.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getAmountDue())));
        column2.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column3 = new TableColumn<>("Type");
        column3.setPrefWidth(75);
        column3.setMaxWidth(75);
        column3.setMinWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerType"));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Sys. Loss");
        column4.setPrefWidth(100);
        column4.setMaxWidth(100);
        column4.setMinWidth(100);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSlAdjustment())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("PPD");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(obj.getValue().getDiscount()))));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column6 = new TableColumn<>("Surcharge");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setCellValueFactory(obj -> new SimpleStringProperty((Utility.formatDecimal(obj.getValue().getSurCharge()))));
        column6.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column7 = new TableColumn<>("Net-Amt.");
        column7.setPrefWidth(125);
        column7.setMaxWidth(125);
        column7.setMinWidth(125);
        column7.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column7.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column8 = new TableColumn<>("Time");
        column8.setCellValueFactory(obj -> new SimpleStringProperty(
                ((PaidBill) obj.getValue()).getPostingTime()+""
        ));
        column8.setPrefWidth(75);
        column8.setMaxWidth(75);
        column8.setMinWidth(75);
        column8.setStyle("-fx-alignment: center;");

        this.bills =  FXCollections.observableArrayList();
        this.dcr_power_table.setFixedCellSize(35);
        this.dcr_power_table.setPlaceholder(new Label("No Bills added"));

        this.dcr_power_table.getColumns().add(column0);
        this.dcr_power_table.getColumns().add(column);
        this.dcr_power_table.getColumns().add(column1);
        this.dcr_power_table.getColumns().add(column2);
        this.dcr_power_table.getColumns().add(column3);
        this.dcr_power_table.getColumns().add(column4);
        this.dcr_power_table.getColumns().add(column5);
        this.dcr_power_table.getColumns().add(column6);
        this.dcr_power_table.getColumns().add(column7);
        this.dcr_power_table.getColumns().add(column8);
    }

    public void createDCRBreakDown(){
        TableColumn<ItemSummary, String> column1 = new TableColumn<>("Item Description");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ItemSummary, String> column2 = new TableColumn<>("Total Amount");
        column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
        column2.setStyle("-fx-alignment: center-right;");

        this.dcrItems =  FXCollections.observableArrayList();
        this.dcr_breakdown_table.setFixedCellSize(35);
        this.dcr_breakdown_table.setPlaceholder(new Label("No report was generated added"));

        this.dcr_breakdown_table.getColumns().add(column1);
        this.dcr_breakdown_table.getColumns().add(column2);
    }

    public void createDCRSummary(){
        TableColumn<ItemSummary, String> column1 = new TableColumn<>("Payment Type");
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ItemSummary, String> column2 = new TableColumn<>("Amount");
        column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
        column2.setStyle("-fx-alignment: center-right;");

        this.dcrPayments =  FXCollections.observableArrayList();
        this.payments_table.setFixedCellSize(35);
        this.payments_table.setPlaceholder(new Label("No report was generated added"));

        this.payments_table.getColumns().add(column1);
        this.payments_table.getColumns().add(column2);
    }

}
