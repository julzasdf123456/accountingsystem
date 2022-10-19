package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
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

    private Bill bill;
    private TableView<Bill> table;
    private Label total_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.save_btn.setOnAction(actionEvent -> {

        });
    }

    public void setData(TableView<Bill> table, Label total){
        this.table = table;
        this.total_lbl = total;
        this.billno_tf.setText(bill.getBillNo());
        this.billing_month_tf.setText(bill.getBillMonth());
        this.surcharge_tf.setText(bill.getSurCharge()+"");
    }

    public void setBill(Bill bill) {
        this.bill = bill;
    }

    public JFXButton getSave_btn() {
        return save_btn;
    }
    public void save(){
        double charge = 0;
        try {
            charge = Double.parseDouble(this.surcharge_tf.getText());
            this.bill.setSurCharge(charge);
            this.bill.setSurChargeTax(charge*0.12);
            this.bill.computeTotalAmount();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
