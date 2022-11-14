package com.boheco1.dev.integratedaccountingsystem.billing;

import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ORPostingController extends MenuControllerHandler implements Initializable {

    @FXML
    private TableView<Bill> paid_bills_table;

    @FXML
    private ProgressBar progressbar;

    @FXML
    private DatePicker date_pker;

    @FXML
    private JFXTextField teller_tf;

    @FXML
    private JFXButton view_btn;

    @FXML
    private JFXTextField dcr_or_tf;

    @FXML
    private Label no_bills_lbl;

    @FXML
    private TextField gross_tf;

    @FXML
    private TextField md_ref_tf;

    @FXML
    private TextField net_tf;

    @FXML
    private JFXButton transact_btn;

    private ObservableList<Bill> bills = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.date_pker.setValue(LocalDate.now());
        this.createPostingTable();
        this.view_btn.setOnAction(action -> {
            this.searchCollections();
        });

        this.teller_tf.setOnAction(actionEvent -> {
            this.searchCollections();
        });

        this.transact_btn.setOnAction(actionEvent -> {
            String dcrNo = dcr_or_tf.getText();
            if (dcrNo.isEmpty()){
                AlertDialogBuilder.messgeDialog("System Error", "DCR Number is required! Please input it and try again!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }else{
                JFXButton accept = new JFXButton("Proceed");
                JFXDialog dialog = DialogBuilder.showConfirmDialog("OR Posting Confirmation","This process is final. Confirm OR Posting?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
                accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
                accept.setOnAction(__ -> {
                    Task<Void> task = new Task<>() {
                        @Override
                        protected Void call() throws Exception {
                            BillDAO.postBills(bills, dcrNo);
                            return null;
                        }
                    };
                    task.setOnRunning(wse -> {
                        dialog.close();
                        transact_btn.setDisable(true);
                        progressbar.setVisible(true);
                    });

                    task.setOnSucceeded(wse -> {
                        dialog.close();
                        progressbar.setVisible(false);
                        reset();
                        dcr_or_tf.setText("");
                        AlertDialogBuilder.messgeDialog("System Information", "The paid bills transacted on " + date_pker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " collected by " + teller_tf.getText() + " was successfully posted!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    });

                    task.setOnFailed(wse -> {
                        dialog.close();
                        transact_btn.setDisable(false);
                        progressbar.setVisible(false);
                        AlertDialogBuilder.messgeDialog("System Error", wse.getSource().getException().getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    });

                    new Thread(task).start();
                });
                dialog.show();
            }
        });
    }
    /**
     * Perform paid bills transaction look up
     * @return void
     */
    public void searchCollections(){
        String teller = teller_tf.getText();
        if (teller.isEmpty() || teller.equals("") || teller == null){
            AlertDialogBuilder.messgeDialog("System Error", "Collector username is required! Please input it and try again!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else {
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() throws SQLException {
                    int month = date_pker.getValue().getMonthValue();
                    int day = date_pker.getValue().getDayOfMonth();
                    int year = date_pker.getValue().getYear();
                    try {
                        List<Bill> paidBills = BillDAO.getAllPaidBills(year, month, day, teller);
                        bills = FXCollections.observableArrayList(paidBills);
                    } catch (Exception e) {
                        AlertDialogBuilder.messgeDialog("System Error", e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            task.setOnRunning(wse -> {
                reset();
                view_btn.setDisable(true);
                progressbar.setVisible(true);
            });

            task.setOnSucceeded(wse -> {
                this.view_btn.setDisable(false);
                this.transact_btn.setDisable(true);
                this.paid_bills_table.setItems(bills);
                double netAmount = 0, grossAmount = 0, mdRefund = 0;
                int count = 0;
                for(Bill b : this.bills){
                    if (!((PaidBill) b).getDcrNumber().equals("") && ((PaidBill) b).getDcrNumber() != null && !((PaidBill) b).getDcrNumber().isEmpty())
                        count++;
                    netAmount += b.getTotalAmount();
                    mdRefund += b.getMdRefund();
                    grossAmount += b.getTotalAmount() - b.getMdRefund();
                }
                if (count == this.bills.size() && count > 0)
                    this.dcr_or_tf.setDisable(true);
                else if (count == this.bills.size() && count == 0)
                    this.dcr_or_tf.setDisable(true);
                else {
                    this.dcr_or_tf.setDisable(false);
                    this.dcr_or_tf.requestFocus();
                    this.transact_btn.setDisable(false);
                }
                if (this.bills.size() > 0) {
                    this.no_bills_lbl.setText(bills.size()+"");
                    this.net_tf.setText(Utility.formatDecimal(netAmount));
                    this.gross_tf.setText(Utility.formatDecimal(grossAmount));
                    this.md_ref_tf.setText(Utility.formatDecimal(mdRefund));
                }
                progressbar.setVisible(false);
            });

            task.setOnFailed(wse -> {
                reset();
            });

            new Thread(task).start();
        }
    }
    /**
     * Initializes the Paid bill transactions table
     * @return void
     */
    public void createPostingTable(){
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

        TableColumn<Bill, String> column2 = new TableColumn<>("Gross Amount");
        column2.setPrefWidth(140);
        column2.setMaxWidth(140);
        column2.setMinWidth(140);
        column2.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount() - obj.getValue().getMdRefund())));
        column2.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column3 = new TableColumn<>("MD Refund");
        column3.setPrefWidth(100);
        column3.setMaxWidth(100);
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("mdRefund"));
        column3.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column4 = new TableColumn<>("Net Amount");
        column4.setPrefWidth(140);
        column4.setMaxWidth(140);
        column4.setMinWidth(140);
        column4.setCellValueFactory(obj -> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotalAmount())));
        column4.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column5 = new TableColumn<>("Date");
        column5.setPrefWidth(100);
        column5.setMaxWidth(100);
        column5.setMinWidth(100);
        column5.setCellValueFactory(obj -> new SimpleStringProperty((((PaidBill) obj.getValue()).getPostingDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))));
        column5.setStyle("-fx-alignment: center-right;");

        TableColumn<Bill, String> column6 = new TableColumn<>("Seq");
        column6.setPrefWidth(100);
        column6.setMaxWidth(100);
        column6.setMinWidth(100);
        column6.setCellValueFactory(obj -> new SimpleStringProperty((((PaidBill) obj.getValue()).getPostingSequence()+"")));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Bill, String> column7 = new TableColumn<>("DCR No.");
        column7.setPrefWidth(100);
        column7.setMaxWidth(100);
        column7.setMinWidth(100);
        column7.setCellValueFactory(obj -> new SimpleStringProperty((((PaidBill) obj.getValue()).getDcrNumber())));
        column7.setStyle("-fx-alignment: center-right;");


        this.bills =  FXCollections.observableArrayList();
        this.paid_bills_table.setFixedCellSize(35);
        this.paid_bills_table.setPlaceholder(new Label("No report was generated!"));

        this.paid_bills_table.getColumns().add(column0);
        this.paid_bills_table.getColumns().add(column);
        this.paid_bills_table.getColumns().add(column1);
        this.paid_bills_table.getColumns().add(column2);
        this.paid_bills_table.getColumns().add(column3);
        this.paid_bills_table.getColumns().add(column4);
        this.paid_bills_table.getColumns().add(column5);
        this.paid_bills_table.getColumns().add(column6);
        this.paid_bills_table.getColumns().add(column7);
    }
    /**
     * Resets the fields
     * @return void
     */
    public void reset(){
        this.dcr_or_tf.setDisable(true);
        this.bills = FXCollections.observableArrayList();
        this.paid_bills_table.getItems().setAll(this.bills);
        this.view_btn.setDisable(false);
        this.no_bills_lbl.setText("");
        this.gross_tf.setText("");
        this.md_ref_tf.setText("");
        this.net_tf.setText("");
        this.transact_btn.setDisable(true);
    }
}
