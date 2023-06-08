package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Item;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AddItemController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXTextField description_tf;

    @FXML
    private CheckBox exists_check;

    @FXML
    private Label price_lbl;

    @FXML
    private Label unit_lbl;

    @FXML
    private JFXTextField cost_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField total_amount_tf;

    @FXML
    private JFXTextField qty_tf;

    @FXML
    private JFXTextField q1_tf;

    @FXML
    private JFXTextField q2_tf;

    @FXML
    private JFXTextField q3_tf;

    @FXML
    private JFXTextField q4_tf;

    @FXML
    private JFXButton add_btn, reset_btn, bal_btn;

    private ObjectTransaction parentController = null;
    private COBItem item = null;
    private Item storedItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputHelper.restrictNumbersOnly(this.cost_tf);
        InputHelper.restrictNumbersOnly(this.qty_tf);
        InputHelper.restrictNumbersOnly(this.q1_tf);
        InputHelper.restrictNumbersOnly(this.q2_tf);
        InputHelper.restrictNumbersOnly(this.q3_tf);
        InputHelper.restrictNumbersOnly(this.q4_tf);
        this.exists_check.setSelected(true);
        this.exists_check.setOnAction(evt ->{
            if (exists_check.isSelected()){
                this.price_lbl.setText("Price");
                this.unit_lbl.setText("Unit");
            }else{
                this.price_lbl.setText("Cost");
                this.unit_lbl.setText("Remarks");
            }
            this.description_tf.requestFocus();
        });

        //Quantity change event to update total amount and set it in Q1
        this.qty_tf.textProperty().addListener((observableValue, o, n) -> {
            double total = 0, cost = 0;
            int qty = 0;
            try{
                cost = Double.parseDouble(cost_tf.getText());
                qty = Integer.parseInt(n);
                total = cost * qty;
                q1_tf.setText(Utility.formatDecimal(total).replace(",", ""));
                total_amount_tf.setText(Utility.formatDecimal(total));
            } catch (Exception e) {

            }
        });

        this.q1_tf.textProperty().addListener((observable, o, n) -> {
            double total = 0, q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            try{
                total = Double.parseDouble(total_amount_tf.getText().replace(",", ""));
                q1 = Double.parseDouble(n.isEmpty() ? "0" : n);
                q2 = Double.parseDouble(q2_tf.getText().isEmpty() ? "0" : q2_tf.getText());
                q3 = Double.parseDouble(q3_tf.getText().isEmpty() ? "0" : q3_tf.getText());
                q4 = Double.parseDouble(q4_tf.getText().isEmpty() ? "0" : q4_tf.getText());
                //If sum of quarter amounts exceeded the total amount
                if (q1+q2+q3+q4 == total) {
                    status_lbl.setText("");
                    add_btn.setDisable(false);
                }else{
                    status_lbl.setText("The sum of amount per quarter is not equal to the total amount!");
                    add_btn.setDisable(true);
                }
            } catch (Exception e) {

            }
        });

        this.q2_tf.textProperty().addListener((observable, o, n) -> {
            double total = 0, q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            try{
                total = Double.parseDouble(total_amount_tf.getText().replace(",", ""));
                q1 = Double.parseDouble(q1_tf.getText().isEmpty() ? "0" : q1_tf.getText());
                q2 = Double.parseDouble(n.isEmpty() ? "0" : n);
                q3 = Double.parseDouble(q3_tf.getText().isEmpty() ? "0" : q3_tf.getText());
                q4 = Double.parseDouble(q4_tf.getText().isEmpty() ? "0" : q4_tf.getText());
                //If sum of quarter amounts exceeded the total amount
                if (q1+q2+q3+q4 == total) {
                    status_lbl.setText("");
                    add_btn.setDisable(false);
                }else{
                    status_lbl.setText("The sum of amount per quarter is not equal to the total amount!");
                    add_btn.setDisable(true);
                }
            } catch (Exception e) {

            }
        });

        this.q3_tf.textProperty().addListener((observable, o, n) -> {
            double total = 0, q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            try{
                total = Double.parseDouble(total_amount_tf.getText().replace(",", ""));
                q1 = Double.parseDouble(q1_tf.getText().isEmpty() ? "0" : q1_tf.getText());
                q2 = Double.parseDouble(q2_tf.getText().isEmpty() ? "0" : q2_tf.getText());
                q3 = Double.parseDouble(n.isEmpty() ? "0" : n);
                q4 = Double.parseDouble(q4_tf.getText().isEmpty() ? "0" : q4_tf.getText());
                //If sum of quarter amounts exceeded the total amount
                if (q1+q2+q3+q4 == total) {
                    status_lbl.setText("");
                    add_btn.setDisable(false);
                }else{
                    status_lbl.setText("The sum of amount per quarter is not equal to the total amount!");
                    add_btn.setDisable(true);
                }
            } catch (Exception e) {

            }
        });

        this.q4_tf.textProperty().addListener((observable, o, n) -> {
            double total = 0, q1 = 0, q2 = 0, q3 = 0, q4 = 0;

            try{
                total = Double.parseDouble(total_amount_tf.getText().replace(",", ""));
                q1 = Double.parseDouble(q1_tf.getText().isEmpty() ? "0" : q1_tf.getText());
                q2 = Double.parseDouble(q2_tf.getText().isEmpty() ? "0" : q2_tf.getText());
                q3 = Double.parseDouble(q3_tf.getText().isEmpty() ? "0" : q3_tf.getText());
                q4 = Double.parseDouble(n.isEmpty() ? "0" : n);
                //If sum of quarter amounts exceeded the total amount
                if (q1+q2+q3+q4 == total) {
                    status_lbl.setText("");
                    add_btn.setDisable(false);
                }else{
                    status_lbl.setText("The sum of amount per quarter is not equal to the total amount!");
                    add_btn.setDisable(true);
                }
            } catch (Exception e) {

            }
        });

        //Add item when enter key is pressed
        this.description_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when button is clicked
        this.add_btn.setOnAction(evt -> {
            addItem();
        });

        //Reset the fields
        this.reset_btn.setOnAction(evt ->{reset();});

        //Distribute amount
        this.bal_btn.setOnAction(evt -> {
            this.status_lbl.setText("");
            String desc = this.description_tf.getText();
            if (desc.isEmpty()) {
                this.status_lbl.setText("Item is not set! Please provide the item details!");
            }else {
                double total = 0, qtr = 0;
                try {
                    double cost = Double.parseDouble(this.cost_tf.getText());
                    int qty = Integer.parseInt(this.qty_tf.getText());
                    total = cost * qty;
                    qtr = total / 4;
                    this.q1_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                    this.q2_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                    this.q3_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                    this.q4_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                } catch (Exception e) {

                }
            }
        });

        this.parentController = Utility.getParentController();
    }
    public void addItem(){
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String remarks = this.unit_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();

        if ((!desc.isEmpty() && !remarks.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && remarks.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            String q1S = this.q1_tf.getText();
            String q2S = this.q2_tf.getText();
            String q3S = this.q3_tf.getText();
            String q4S = this.q4_tf.getText();

            this.item = new COBItem();

            //If item is from database
            if (this.exists_check.isSelected() && this.storedItem != null){
                this.item.setItemId(this.storedItem.getItemId());
                this.item.setDescription(this.storedItem.getParticulars());
                this.item.setRemarks(this.storedItem.getUnit());
            //Else, selected is open-ended item
            }else{
                this.item.setDescription(desc);
                this.item.setRemarks(remarks);
            }
            double amount = 0, q1 = 0, q2 = 0, q3 = 0, q4 = 0;
            int no = 0;
            try{
                amount = Double.parseDouble(cost);
            }catch (Exception e) {

            }
            try {
                q1 = Double.parseDouble(q1S);
            }catch (Exception e) {

            }
            try {
                q2 = Double.parseDouble(q2S);
            }catch (Exception e) {

            }
            try {
                q3 = Double.parseDouble(q3S);
            }catch (Exception e) {

            }
            try {
                q4 = Double.parseDouble(q4S);
            }catch (Exception e) {

            }
            try{
                no = Integer.parseInt(qty);
            }catch (Exception e){

            }
            this.item.setCost(amount);
            this.item.setQty(no);
            this.item.setQtr1(q1);
            this.item.setQtr2(q2);
            this.item.setQtr3(q3);
            this.item.setQtr4(q4);
            this.parentController.receive(this.item);
            this.reset();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    public void reset(){
        this.status_lbl.setText("");
        this.item = null;
        this.description_tf.setText("");
        this.cost_tf.setText("");
        this.unit_tf.setText("");
        this.total_amount_tf.setText("");
        this.qty_tf.setText("");
        this.q1_tf.setText("");
        this.q2_tf.setText("");
        this.q3_tf.setText("");
        this.q4_tf.setText("");
        this.description_tf.requestFocus();
    }

    public JFXButton getAdd_btn() {
        return this.add_btn;
    }

    public JFXTextField getDescription_tf() {
        return this.description_tf;
    }

    public CheckBox getCheck(){
        return this.exists_check;
    }
}
