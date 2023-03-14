package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.Check;
import com.boheco1.dev.integratedaccountingsystem.objects.PaidBill;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class PaymentConfirmationController extends MenuControllerHandler implements Initializable {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextField cash_tf;

    @FXML
    private TextField check_tf;

    @FXML
    private TextField total_amount_paid_tf;

    @FXML
    private TextField total_amount_due_tf;

    @FXML
    private TextField change_tf;

    @FXML
    private CheckBox deposit_cb;

    @FXML
    private Label status_label;

    @FXML
    private ComboBox account_list;

    @FXML
    private JFXButton confirm_btn;
    private List<Bill> bills;
    private List<Check> checks;
    private double check_amount, total_payments, change, amount_due, cash;

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.cash_tf.setOnKeyTyped(type -> {
            this.setFigures();
        });
        this.confirm_btn.requestFocus();
        this.deposit_cb.setOnMouseClicked(action ->{
            if (deposit_cb.isSelected()){
                this.account_list.setVisible(true);
            }else{
                this.account_list.setVisible(false);
            }
            account_list.getSelectionModel().clearSelection();
            setFigures();
        });
        InputHelper.restrictNumbersOnly(cash_tf);

        this.account_list.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            setFigures();
        });
    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void confirm(){

    }

    public void setBills(List<Bill> bills){
        ObservableList<PaidBill> accounts = FXCollections.observableArrayList(new ArrayList<>());
        for(Bill b : bills){
            PaidBill p = (PaidBill) b;
            if (!accounts.contains(p))
                accounts.add(p);
        }
        this.account_list.setItems(accounts);
    }

    public void setFigures(){
        String cash = this.cash_tf.getText();
        try {
            this.cash = Double.parseDouble(cash.replace(",",""));
            this.check_amount = 0;
            if (this.checks.size() > 0) {
                for (Check c : this.checks) {
                    this.check_amount += c.getOriginalAmount();
                }
            }
            this.total_payments = Utility.round(this.cash + this.check_amount, 2);
            this.change = Utility.round(this.total_payments - this.amount_due, 2);

            this.change_tf.setText(Utility.formatDecimal(this.change));
            this.total_amount_paid_tf.setText(Utility.formatDecimal(this.total_payments));

            if (this.change < 0)
                this.confirm_btn.setDisable(true);
            else
                this.confirm_btn.setDisable(false);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void setPayments(List<Bill> bills, double amount_due, double cash, List<Check> checks){
        this.bills = bills;
        this.cash = cash;
        this.checks = checks;
        this.check_amount = 0;
        this.amount_due = amount_due;
        for (Check c: checks) {
            this.check_amount += c.getOriginalAmount();
        }
        this.total_payments = Utility.round(this.cash + this.check_amount, 2);
        this.change = Utility.round(this.total_payments - this.amount_due, 2);

        this.cash_tf.setText(Utility.formatDecimal(this.cash));
        this.check_tf.setText(Utility.formatDecimal(this.check_amount));
        this.total_amount_paid_tf.setText(Utility.formatDecimal(this.total_payments));
        this.total_amount_due_tf.setText(Utility.formatDecimal(this.amount_due));
        this.change_tf.setText(Utility.formatDecimal(this.change));
    }

    public JFXButton getConfirm_btn() {
        return confirm_btn;
    }

    public TextField getCash_tf(){return cash_tf;}

    public TextField getChange_tf(){return change_tf;}

    public ComboBox getAccount_list(){return account_list;}

    public boolean checkDeposit(){
        return deposit_cb.isSelected();
    }

    public Label getStatus_label(){return status_label;}
}
