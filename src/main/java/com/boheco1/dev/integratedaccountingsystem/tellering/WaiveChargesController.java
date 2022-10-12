package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Bill;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WaiveChargesController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXButton save_btn;

    @FXML
    private TextField billno_tf;

    @FXML
    private TextField billing_month_tf;

    @FXML
    private TextField surcharge_tf;

    @FXML
    private TextField ewt_tf;

    @FXML
    private TextField evat_tf;

    private Bill bill;
    private TableView<Bill> table;
    private Label total_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.save_btn.setOnAction(actionEvent -> {
            double charge = 0, ewt = 0, evat = 0;
            try {
                charge = Double.parseDouble(this.surcharge_tf.getText());
                ewt = Double.parseDouble(this.ewt_tf.getText());
                evat = Double.parseDouble(this.evat_tf.getText());
                this.bill.setSurCharge(charge);
                this.bill.setCh2306(ewt);
                this.bill.setCh2307(evat);
                this.bill.setTotalAmount();
                this.table.refresh();
                Utility.setAmount(this.total_lbl, this.table.getItems());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }

    public void setData(TableView<Bill> table, Label total){
        this.table = table;
        this.total_lbl = total;
        this.billno_tf.setText(bill.getBillNo());
        this.billing_month_tf.setText(bill.getBillMonth());
        this.surcharge_tf.setText(bill.getSurCharge()+"");
        this.ewt_tf.setText(bill.getCh2307()+"");
        this.evat_tf.setText(bill.getCh2306()+"");
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }
}
