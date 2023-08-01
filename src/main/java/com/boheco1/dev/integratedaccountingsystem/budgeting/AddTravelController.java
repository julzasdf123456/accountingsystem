package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Travel;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddTravelController extends MenuControllerHandler implements Initializable {
    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXTextField description_tf;

    @FXML
    private JFXTextField total_tf;

    @FXML
    private JFXTextField qty_tf;

    @FXML
    private JFXTextField days_tf;

    @FXML
    private JFXTextField times_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField travel_tf;

    @FXML
    private JFXTextField total_amount_tf;

    @FXML
    private JFXComboBox<String> mode_cb;

    @FXML
    private JFXTextField transport_rate_tf;

    @FXML
    private JFXTextField transport_cost_tf;

    @FXML
    private JFXTextField lodging_rate_tf;

    @FXML
    private JFXTextField lodging_cost_tf;

    @FXML
    private JFXTextField reg_tf;

    @FXML
    private JFXTextField incidental_tf;

    @FXML
    private JFXTextField q1_tf;

    @FXML
    private JFXTextField q2_tf;

    @FXML
    private JFXTextField q3_tf;

    @FXML
    private JFXTextField q4_tf;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private JFXButton bal_btn;

    private Travel item = null;
    private ObjectTransaction parentController = null;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.total_tf.setText("0");
        this.q1_tf.setText("0");
        this.q2_tf.setText("0");
        this.q3_tf.setText("0");
        this.q4_tf.setText("0");

        this.item = new Travel();
        this.item.setcItemId(Utility.generateRandomId());
        //Initially set to 1
        this.item.setNoOfTimes(1);

        InputHelper.restrictNumbersOnly(this.qty_tf);
        InputHelper.restrictNumbersOnly(this.days_tf);
        InputHelper.restrictNumbersOnly(this.times_tf);
        InputHelper.restrictNumbersOnly(this.travel_tf);
        InputHelper.restrictNumbersOnly(this.transport_rate_tf);
        InputHelper.restrictNumbersOnly(this.lodging_rate_tf);
        InputHelper.restrictNumbersOnly(this.reg_tf);
        InputHelper.restrictNumbersOnly(this.incidental_tf);
        InputHelper.restrictNumbersOnly(this.q1_tf);
        InputHelper.restrictNumbersOnly(this.q2_tf);
        InputHelper.restrictNumbersOnly(this.q3_tf);
        InputHelper.restrictNumbersOnly(this.q4_tf);

        ObservableList<String> types = FXCollections.observableArrayList(new ArrayList<>());
        types.add("Boat");
        types.add("Bus");
        types.add("Ferry");
        types.add("Plane");
        types.add("Service");
        this.mode_cb.setItems(types);

        //Add item when enter key is pressed
        this.add_btn.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.description_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.qty_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.days_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.times_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.travel_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.transport_rate_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.lodging_rate_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.reg_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.incidental_tf.setOnAction(evt -> {
            addItem();
        });

        this.qty_tf.setOnKeyReleased(evt -> {
            int persons = 0;
            try {
                persons = Integer.parseInt(this.qty_tf.getText());
            }catch (Exception e){

            }
            this.item.setQty(persons);
            this.setTotals();
        });

        this.days_tf.setOnKeyReleased(evt -> {
            int days = 0;
            try {
                days = Integer.parseInt(this.days_tf.getText());
            }catch (Exception e){

            }
            this.item.setNoOfDays(days);
            this.setTotals();
        });

        this.times_tf.setOnKeyReleased(evt -> {
            int times = 0;
            try {
                times = Integer.parseInt(this.times_tf.getText());
            }catch (Exception e){

            }
            this.item.setNoOfTimes(times);
            this.setTotals();
        });

        this.travel_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.travel_tf.getText());
            }catch (Exception e){

            }
            this.item.setCost(total);
            this.total_amount_tf.setText(Utility.formatDecimal(total));
            this.setTotals();
        });

        this.transport_rate_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.transport_rate_tf.getText());
            }catch (Exception e){

            }
            this.item.setTransport(total);
            this.transport_cost_tf.setText(Utility.formatDecimal(total));
            this.setTotals();
        });

        this.lodging_rate_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.lodging_rate_tf.getText());
            }catch (Exception e){

            }
            this.item.setLodging(total);
            this.lodging_cost_tf.setText(Utility.formatDecimal(total));
            this.setTotals();
        });

        this.reg_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.reg_tf.getText());
            }catch (Exception e){

            }
            this.item.setRegistration(total);
            this.setTotals();
        });

        this.incidental_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.incidental_tf.getText());
            }catch (Exception e){

            }
            this.item.setIncidental(total);
            this.setTotals();
        });

        this.q1_tf.setOnKeyReleased(evt -> {
            validateQuarter();
        });

        this.q2_tf.setOnKeyReleased(evt -> {
            validateQuarter();
        });

        this.q3_tf.setOnKeyReleased(evt -> {
            validateQuarter();
        });

        this.q4_tf.setOnKeyReleased(evt -> {
            validateQuarter();
        });

        this.q1_tf.setOnAction(evt -> {
            addItem();
        });

        this.q2_tf.setOnAction(evt -> {
            addItem();
        });

        this.q3_tf.setOnAction(evt -> {
            addItem();
        });

        this.q4_tf.setOnAction(evt -> {
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
                double q = this.item.getAmount() / 4;
                this.item.setQtr1(q);
                this.item.setQtr2(q);
                this.item.setQtr3(q);
                this.item.setQtr4(q);
                this.q1_tf.setText(Utility.formatDecimal(q).replace(",", ""));
                this.q2_tf.setText(Utility.formatDecimal(q).replace(",", ""));
                this.q3_tf.setText(Utility.formatDecimal(q).replace(",", ""));
                this.q4_tf.setText(Utility.formatDecimal(q).replace(",", ""));
                this.add_btn.setDisable(false);
            }
        });
        this.parentController = Utility.getParentController();
    }
    public void setTotals(){
        this.total_amount_tf.setText(Utility.formatDecimal(this.item.getTotalTravel()));
        this.transport_cost_tf.setText(Utility.formatDecimal(this.item.getTotalTransport()));
        this.lodging_cost_tf.setText(Utility.formatDecimal(this.item.getTotalLodging()));
        this.total_tf.setText(Utility.formatDecimal(this.item.getAmount()).replace(",", ""));
        this.add_btn.setDisable(true);
    }
    public void validateQuarter(){
        double q1 = 0, q2 = 0, q3 = 0, q4 = 0;
        double total = this.item.getAmount();
        q1 = Double.parseDouble(this.q1_tf.getText().isEmpty() ? "0" : this.q1_tf.getText());
        q2 = Double.parseDouble(this.q2_tf.getText().isEmpty() ? "0" : this.q2_tf.getText());
        q3 = Double.parseDouble(this.q3_tf.getText().isEmpty() ? "0" : this.q3_tf.getText());
        q4 = Double.parseDouble(this.q4_tf.getText().isEmpty() ? "0" : this.q4_tf.getText());
        this.item.setQtr1(q1);
        this.item.setQtr2(q2);
        this.item.setQtr3(q3);
        this.item.setQtr4(q4);
        //If sum of quarter amounts exceeded the total amount
        if (q1+q2+q3+q4 == total) {
            this.status_lbl.setText("");
            this.add_btn.setDisable(false);
        }else{
            this.status_lbl.setText("The total amount per quarter is not equal to the grand total amount !");
            this.add_btn.setDisable(true);
        }
    }
    public void addItem() {
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String qtyR = this.qty_tf.getText();
        String unit = this.unit_tf.getText();
        String daysR = this.days_tf.getText();
        String timesR = this.times_tf.getText();
        String travelR = this.travel_tf.getText();
        String modeR = this.mode_cb.getSelectionModel().getSelectedItem();
        double travel = 0;
        int persons = 0, days = 0, times = 0;

        try{
            travel = Double.parseDouble(travelR);
        }catch (Exception e) {

        }

        try{
            persons = Integer.parseInt(qtyR);
        }catch (Exception e) {

        }

        try{
            days = Integer.parseInt(daysR);
        }catch (Exception e) {

        }

        try{
            times = Integer.parseInt(timesR);
        }catch (Exception e) {

        }

        if ((!desc.isEmpty() && persons > 0 && days > 0 && times > 0 && travel > 0 && modeR != null)
                || (!desc.isEmpty() && persons <= 0 && days <= 0 && times <= 1 && travel <= 0 && modeR == null)) {
            this.item.setDescription(desc);
            this.item.setRemarks(unit);
            this.item.setMode(modeR);
            this.parentController.receive(this.item);
            this.reset();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    public void editItem() {
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String qtyR = this.qty_tf.getText();
        String unit = this.unit_tf.getText();
        String daysR = this.days_tf.getText();
        String timesR = this.times_tf.getText();
        String travelR = this.travel_tf.getText();
        String modeR = this.mode_cb.getSelectionModel().getSelectedItem();
        double travel = 0;
        int persons = 0, days = 0, times = 0;

        try{
            travel = Double.parseDouble(travelR);
        }catch (Exception e) {

        }

        try{
            persons = Integer.parseInt(qtyR);
        }catch (Exception e) {

        }

        try{
            days = Integer.parseInt(daysR);
        }catch (Exception e) {

        }

        try{
            times = Integer.parseInt(timesR);
        }catch (Exception e) {

        }

        if ((!desc.isEmpty() && persons > 0 && days > 0 && times > 0 && travel > 0 && modeR != null)
                || (!desc.isEmpty() && persons <= 0 && days <= 0 && times <= 1 && travel <= 0 && modeR == null)) {
            this.item.setDescription(desc);
            this.item.setRemarks(unit);
            this.item.setMode(modeR);
            COBController ctrl = (COBController) this.parentController;
            ctrl.refreshItems();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    public void reset(){
        this.status_lbl.setText("");
        this.item = new Travel();
        this.item.setcItemId(Utility.generateRandomId());
        this.item.setNoOfDays(0);
        this.item.setQty(0);
        this.item.setNoOfTimes(1);
        this.item.setCost(0);
        this.item.setTransport(0);
        this.item.setLodging(0);
        this.item.setRegistration(0);
        this.item.setIncidental(0);

        this.description_tf.setText("");
        this.qty_tf.setText("0");
        this.q1_tf.setText("0");
        this.q2_tf.setText("0");
        this.q3_tf.setText("0");
        this.q4_tf.setText("0");
        this.days_tf.setText("0");
        this.times_tf.setText("1");
        this.travel_tf.setText("0");
        this.total_amount_tf.setText("0");
        this.transport_rate_tf.setText("0");
        this.transport_cost_tf.setText("0");
        this.lodging_rate_tf.setText("0");
        this.lodging_cost_tf.setText("0");
        this.total_tf.setText("0");
        this.reg_tf.setText("0");
        this.incidental_tf.setText("0");
        this.mode_cb.getSelectionModel().clearSelection();
    }

    public void showDetails(){
        this.description_tf.setText(this.item.getDescription());
        this.qty_tf.setText(this.item.getQty()+"");
        this.q1_tf.setText(this.item.getQtr1()+"");
        this.q2_tf.setText(this.item.getQtr2()+"");
        this.q3_tf.setText(this.item.getQtr3()+"");
        this.q4_tf.setText(this.item.getQtr4()+"");
        this.days_tf.setText(this.item.getNoOfDays()+"");
        this.times_tf.setText(this.item.getNoOfTimes()+"");
        this.travel_tf.setText(this.item.getCost()+"");
        this.total_amount_tf.setText(Utility.formatDecimal(this.item.getTotalTravel()));
        this.transport_rate_tf.setText(this.item.getTransport()+"");
        this.transport_cost_tf.setText(Utility.formatDecimal(this.item.getTotalTransport()));
        this.lodging_rate_tf.setText(this.item.getLodging()+"");
        this.lodging_cost_tf.setText(Utility.formatDecimal(this.item.getTotalLodging()));
        this.total_tf.setText(Utility.formatDecimal(this.item.getAmount()));
        this.reg_tf.setText(this.item.getRegistration()+"");
        this.incidental_tf.setText(this.item.getIncidental()+"");
        this.mode_cb.getSelectionModel().select(this.item.getMode());
    }

    public JFXButton getAdd_btn() {
        return this.add_btn;
    }
    public JFXButton getReset_btn() {
        return this.reset_btn;
    }
    public JFXTextField getDescription_tf() {
        return this.description_tf;
    }

    public Travel getItem() {
        return item;
    }

    public void setItem(Travel item) {
        this.item = item;
    }
}
