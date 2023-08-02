package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.ItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.COBItem;
import com.boheco1.dev.integratedaccountingsystem.objects.Item;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
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
    private JFXTextField qty_tf, times_tf;

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
    private double oldAmount = 0;
    private Item selectedItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.add_btn.setDisable(false);
        this.total_amount_tf.setText("0");
        this.q1_tf.setText("0");
        this.q2_tf.setText("0");
        this.q3_tf.setText("0");
        this.q4_tf.setText("0");

        this.item = new COBItem();
        this.item.setcItemId(Utility.generateRandomId());
        this.item.setNoOfTimes(1);

        InputHelper.restrictNumbersOnly(this.cost_tf);
        InputHelper.restrictNumbersOnly(this.qty_tf);
        InputHelper.restrictNumbersOnly(this.times_tf);
        InputHelper.restrictNumbersOnly(this.q1_tf);
        InputHelper.restrictNumbersOnly(this.q2_tf);
        InputHelper.restrictNumbersOnly(this.q3_tf);
        InputHelper.restrictNumbersOnly(this.q4_tf);
        this.exists_check.setSelected(true);
        this.unit_tf.setEditable(false);
        this.unit_tf.setText("");
        this.bindAutoSuggest(description_tf);
        this.exists_check.setOnAction(evt ->{
            this.selectedItem = null;
            this.unit_tf.setText("");
            this.cost_tf.setText("");
            if (exists_check.isSelected()){
                this.unit_tf.setEditable(false);
            }else{
                this.unit_tf.setEditable(true);
            }
            this.description_tf.requestFocus();
        });

        //Quantity change event to update total amount and set it in Q1
        this.qty_tf.setOnKeyReleased(evt -> {
            int qty = 0;
            try {
                qty = Integer.parseInt(this.qty_tf.getText());
            }catch (Exception e){

            }
            this.item.setQty(qty);
            this.setTotals();
        });

        this.cost_tf.setOnKeyReleased(evt -> {
            double total = 0;
            try {
                total = Double.parseDouble(this.cost_tf.getText());
            }catch (Exception e){

            }
            this.item.setCost(total);
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

        //Add item when enter key is pressed
        this.description_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.qty_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.times_tf.setOnAction(evt -> {
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
                double qtr = this.item.getAmount() / 4;
                this.item.setQtr1(qtr);
                this.item.setQtr2(qtr);
                this.item.setQtr3(qtr);
                this.item.setQtr4(qtr);
                this.q1_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                this.q2_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                this.q3_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                this.q4_tf.setText(Utility.formatDecimal(qtr).replace(",", ""));
                this.add_btn.setDisable(false);
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
            //If item is from database
            if (this.exists_check.isSelected() && this.selectedItem != null){
                this.item.setItemId(this.selectedItem.getItemId());
                this.item.setDescription(this.selectedItem.getParticulars());
                this.item.setRemarks(this.selectedItem.getUnit());
            //Else, selected is open-ended item
            }else{
                this.item.setDescription(desc);
                this.item.setRemarks(remarks);
            }
            this.parentController.receive(this.item);
            this.reset();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    /**
     * Method to call when COB is created (COBController) and user wants to edit a newly added item
     */
    public void editItem(){
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String remarks = this.unit_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();

        if ((!desc.isEmpty() && !remarks.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && remarks.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            //If item is from database
            if (this.exists_check.isSelected() && this.selectedItem != null){
                this.item.setItemId(this.selectedItem.getItemId());
                this.item.setDescription(this.selectedItem.getParticulars());
                this.item.setRemarks(this.selectedItem.getUnit());
            //Else, selected is open-ended item
            }else{
                this.item.setDescription(desc);
                this.item.setRemarks(remarks);
            }
            COBController ctrl = (COBController) this.parentController;
            ctrl.refreshItems();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }
    /**
     * Method to call when COB is revised (EditCOBController) and user wants to edit an existing item
     * @param cob - COB reference
     */
    public void updateItem(COB cob){
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String remarks = this.unit_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();

        if ((!desc.isEmpty() && !remarks.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && remarks.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            try {
                //If item is from database
                if (this.exists_check.isSelected() && this.selectedItem != null) {
                    this.item.setItemId(this.selectedItem.getItemId());
                    this.item.setDescription(this.selectedItem.getParticulars());
                    this.item.setRemarks(this.selectedItem.getUnit());
                //Else, selected is open-ended item
                } else {
                    this.item.setItemId(null);
                    this.item.setDescription(desc);
                    this.item.setRemarks(remarks);
                }
                EditCOBController ctrl = (EditCOBController) this.parentController;
                CobItemDAO.update(cob, this.item, this.oldAmount);
                ctrl.refreshItems();
            }catch (Exception e){
                e.printStackTrace();
            }
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    public void reset(){
        this.status_lbl.setText("");
        this.item = new COBItem();
        this.item.setcItemId(Utility.generateRandomId());
        this.item.setAmount(0);
        this.item.setCost(0);
        this.item.setQty(0);
        this.item.setNoOfTimes(1);
        this.item.setQtr1(0);
        this.item.setQtr2(0);
        this.item.setQtr3(0);
        this.item.setQtr4(0);
        this.selectedItem = null;
        this.description_tf.setText("");
        this.cost_tf.setText("");
        this.unit_tf.setText("");
        this.total_amount_tf.setText("");
        this.qty_tf.setText("");
        this.times_tf.setText("1");
        this.q1_tf.setText("");
        this.q2_tf.setText("");
        this.q3_tf.setText("");
        this.q4_tf.setText("");
        this.description_tf.requestFocus();
        this.add_btn.setDisable(false);
    }
    public void setTotals(){
        this.total_amount_tf.setText(Utility.formatDecimal(this.item.getAmount()).replace(",", ""));
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
    public void bindAutoSuggest(JFXTextField textField){
        AutoCompletionBinding<Item> suggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();
                    //Initialize list of stocks
                    List<Item> list = new ArrayList<>();
                    if (query.length() >= 3 && exists_check.isSelected()){
                        try {
                            list = ItemDAO.searchItems(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    return list;
                }, new StringConverter<>() {
                    @Override
                    public String toString(Item o) {
                        return o.getParticulars();
                    }
                    @Override
                    public Item fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
        suggest.setOnAutoCompleted(event -> {
            selectedItem = event.getCompletion();
            description_tf.setText(selectedItem.getParticulars());
            if (selectedItem.getPrice() > 0) {
                item.setCost(selectedItem.getPrice());
                item.setQty(1);
                cost_tf.setText(Utility.formatDecimal(selectedItem.getPrice()).replace(",",""));
                unit_tf.setText(selectedItem.getUnit());
                total_amount_tf.setText(Utility.formatDecimal(selectedItem.getPrice() * 1));
                qty_tf.requestFocus();
                validateQuarter();
            }
        });
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

    public CheckBox getCheck(){
        return this.exists_check;
    }

    public void setType(String type){
        if (type.equals(COBItem.TYPES[0]) || type.equals(COBItem.TYPES[5])){
            this.times_tf.setDisable(false);
        }else{
            this.times_tf.setDisable(true);
        }
        if (type.equals(COBItem.TYPES[5]))
            this.unit_tf.setText("Liters per mo");
    }

    public void disableUnit(boolean ok){
        this.unit_tf.setEditable(ok);
    }

    public COBItem getItem() {
        return item;
    }

    public void setItem(COBItem item) {
        this.item = item;
        this.oldAmount = this.item.getAmount();
    }

    public void showDetails(){
        this.description_tf.setText(this.item.getDescription());
        this.cost_tf.setText(this.item.getCost()+"");
        this.unit_tf.setText(this.item.getRemarks());
        this.total_amount_tf.setText(Utility.formatDecimal(this.item.getAmount()));
        this.qty_tf.setText(this.item.getQty()+"");
        this.times_tf.setText(this.item.getNoOfTimes()+"");
        this.q1_tf.setText(this.item.getQtr1()+"");
        this.q2_tf.setText(this.item.getQtr2()+"");
        this.q3_tf.setText(this.item.getQtr3()+"");
        this.q4_tf.setText(this.item.getQtr4()+"");
    }
}
