package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Item;
import com.boheco1.dev.integratedaccountingsystem.objects.Representation;
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

public class AddReprController extends MenuControllerHandler implements Initializable {

    private Representation item = null;
    private Item selectedItem = null;

    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXTextField description_tf;

    @FXML
    private JFXTextField cost_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField qty_tf;

    @FXML
    private JFXTextField total_amount_tf;

    @FXML
    private JFXComboBox<String> allowance_type_cb;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton reset_btn;

    public JFXButton getAdd_btn() {
        return this.add_btn;
    }
    public JFXButton getReset_btn() {
        return this.reset_btn;
    }
    public JFXTextField getDescription_tf() {
        return this.description_tf;
    }

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.item = new Representation();
        this.item.setcItemId(Utility.generateRandomId());

        ObservableList<String> types = FXCollections.observableArrayList(new ArrayList<>());
        types.add("Representation");
        types.add("Reimbursable");
        types.add("Other Allowance");
        this.allowance_type_cb.setItems(types);
        this.allowance_type_cb.getSelectionModel().select(0);

        InputHelper.restrictNumbersOnly(this.cost_tf);
        InputHelper.restrictNumbersOnly(this.qty_tf);

        //Add item when enter key is pressed
        this.description_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.qty_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.total_amount_tf.setOnAction(evt -> {
            addItem();
        });

        //Quantity change event to update total amount and set it in Q1
        this.qty_tf.setOnKeyReleased(evt -> {
            costUpdate(cost_tf.getText(), qty_tf.getText());
        });

        //Quantity change event to update total amount and set it in Q1
        this.cost_tf.setOnKeyReleased(evt -> {
            costUpdate(cost_tf.getText(), qty_tf.getText());
        });

        this.allowance_type_cb.setOnAction(evt -> {
            costUpdate(cost_tf.getText(), qty_tf.getText());
        });

        //Add item when button is clicked
        this.add_btn.setOnAction(evt -> {
            addItem();
        });

        //Reset the fields
        this.reset_btn.setOnAction(evt ->{reset();});

        this.parentController = Utility.getParentController();
    }

    public void costUpdate(String c, String q){
        String type = this.allowance_type_cb.getSelectionModel().getSelectedItem();
        int qty = 0;
        double amount = 0;
        try {
            qty = Integer.parseInt(q);
        }catch (Exception e){
        }
        try{
            amount = Double.parseDouble(c);
        }catch (Exception e){
        }
        this.item.setQty(qty);
        this.item.setCost(amount);
        if (type.equals("Representation")) {
            this.item.setRepresentationAllowance(this.item.getAmount());
            this.item.setReimbursableAllowance(0);
            this.item.setOtherAllowance(0);
        }else if (type.equals("Reimbursable")) {
            this.item.setReimbursableAllowance(this.item.getAmount());
            this.item.setRepresentationAllowance(0);
            this.item.setOtherAllowance(0);
        }else{
            this.item.setOtherAllowance(this.item.getAmount());
            this.item.setRepresentationAllowance(0);
            this.item.setReimbursableAllowance(0);
        }
        total_amount_tf.setText(Utility.formatDecimal(this.item.getAmount()));
    }
    public void addItem(){
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String remarks = this.unit_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();
        if ((!desc.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            this.item.setDescription(desc);
            this.item.setRemarks(remarks);
            this.parentController.receive(this.item);
            this.reset();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }

    public void editItem(){
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String remarks = this.unit_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();
        if ((!desc.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            this.item.setDescription(desc);
            this.item.setRemarks(remarks);
            COBController ctrl = (COBController) this.parentController;
            ctrl.refreshItems();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }
    public void reset(){
        this.status_lbl.setText("");
        this.item = new Representation();
        this.item.setcItemId(Utility.generateRandomId());
        this.item.setAmount(0);
        this.item.setCost(0);
        this.item.setQty(0);
        this.item.setRepresentationAllowance(0);
        this.item.setReimbursableAllowance(0);
        this.item.setOtherAllowance(0);
        this.description_tf.setText("");
        this.cost_tf.setText("");
        this.total_amount_tf.setText("");
        this.qty_tf.setText("");
        this.description_tf.requestFocus();
    }

    public Representation getItem() {
        return item;
    }

    public void setItem(Representation item) {
        this.item = item;
    }

    public void showDetails(){
        this.description_tf.setText(this.item.getDescription());
        this.cost_tf.setText(this.item.getCost()+"");
        this.total_amount_tf.setText(Utility.formatDecimal(this.item.getAmount()));
        this.qty_tf.setText(this.item.getQty()+"");
        if (this.item.getRepresentationAllowance() > 0) {
            this.allowance_type_cb.getSelectionModel().select(0);
        }else if (this.item.getReimbursableAllowance() > 0){
            this.allowance_type_cb.getSelectionModel().select(1);
        }else{
            this.allowance_type_cb.getSelectionModel().select(2);
        }
    }
}

