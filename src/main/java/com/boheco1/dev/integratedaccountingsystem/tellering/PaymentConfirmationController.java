package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.boheco1.dev.integratedaccountingsystem.objects.Check;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
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
    }

    /**
     * Search for consumers based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void confirm(){

    }

    public void setFigures(){
        try {
            this.cash = Double.parseDouble(this.cash_tf.getText().replace(",",""));
            this.check_amount = 0;
            if (this.checks.size() > 0) {
                for (Check c : this.checks) {
                    this.check_amount += c.getAmount();
                }
            }
            this.total_payments = this.cash + this.check_amount;
            this.change = this.total_payments - this.amount_due;
            this.change_tf.setText(Utility.formatDecimal(this.change));
            this.total_amount_paid_tf.setText(Utility.formatDecimal(total_payments));

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
            this.check_amount += c.getAmount();
        }
        this.total_payments = this.cash + this.check_amount;
        this.change = this.total_payments - this.amount_due;
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
}
