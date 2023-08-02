package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.CobItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.COB;
import com.boheco1.dev.integratedaccountingsystem.objects.Salary;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class AddSalaryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private CheckBox salary_type_cb;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXTextField description_tf;

    @FXML
    private Label unit_lbl;

    @FXML
    private JFXTextField qty_tf;

    @FXML
    private JFXTextField cost_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField longetivity_tf;

    @FXML
    private JFXTextField sss_tf;

    @FXML
    private JFXTextField cashgift_tf;

    @FXML
    private JFXTextField bonus_tf;

    @FXML
    private JFXButton add_btn;

    @FXML
    private JFXButton reset_btn;

    private ObjectTransaction parentController = null;
    private double oldAmount = 0;
    private Salary item = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.item = new Salary();
        this.item.setcItemId(Utility.generateRandomId());

        if (this.salary_type_cb.isSelected()){
            this.unit_tf.setText("SG ");
            this.item.setCashGift(5000);
            this.item.setLongetivity(0);
            this.cashgift_tf.setText("5000");
            this.longetivity_tf.setText("");
            this.longetivity_tf.setDisable(false);
        }else{
            this.unit_tf.setText(Salary.PER_DAY);
            this.item.setRemarks(Salary.PER_DAY);
            this.item.setCashGift(2000);
            this.item.setLongetivity(0);
            this.cashgift_tf.setText("2000");
            this.longetivity_tf.setText("0");
            this.longetivity_tf.setDisable(true);
        }

        this.salary_type_cb.setOnAction(evt ->{
            if (salary_type_cb.isSelected()){
                this.unit_tf.setText("SG ");
                this.item.setCashGift(5000);
                this.item.setLongetivity(0);
                this.cashgift_tf.setText("5000");
                this.longetivity_tf.setText("");
                this.longetivity_tf.setDisable(false);
            }else{
                this.unit_tf.setText(Salary.PER_DAY);
                this.item.setRemarks(Salary.PER_DAY);
                this.item.setCashGift(2000);
                this.item.setLongetivity(0);
                this.cashgift_tf.setText("2000");
                this.longetivity_tf.setText("0");
                this.longetivity_tf.setDisable(true);
            }
        });

        InputHelper.restrictNumbersOnly(this.cost_tf);
        InputHelper.restrictNumbersOnly(this.qty_tf);
        InputHelper.restrictNumbersOnly(this.longetivity_tf);
        InputHelper.restrictNumbersOnly(this.sss_tf);
        InputHelper.restrictNumbersOnly(this.cashgift_tf);

        //Add item when enter key is pressed
        this.description_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.cost_tf.setOnAction(evt -> {
            addItem();
        });

        this.cost_tf.setOnKeyReleased(evt -> {
            double amount = 0;
            try{
                amount = Double.parseDouble(this.cost_tf.getText());
            }catch (Exception e){

            }
            this.item.setCost(amount);
            this.item.setBonus13(amount);
            this.bonus_tf.setText(Utility.formatDecimal(amount));
        });

        this.qty_tf.setOnKeyReleased(evt -> {
            int persons = 0;
            try{
                persons = Integer.parseInt(this.qty_tf.getText());
            }catch (Exception e){

            }
            this.item.setQty(persons);
        });

        this.longetivity_tf.setOnKeyReleased(evt -> {
            double amount = 0;
            try{
                amount = Double.parseDouble(this.longetivity_tf.getText());
            }catch (Exception e){

            }
            this.item.setLongetivity(amount);
        });

        this.sss_tf.setOnKeyReleased(evt -> {
            double amount = 0;
            try{
                amount = Double.parseDouble(this.sss_tf.getText());
            }catch (Exception e){

            }
            this.item.setsSSPhilH(amount);
        });

        this.cashgift_tf.setOnKeyReleased(evt -> {
            double amount = 0;
            try{
                amount = Double.parseDouble(this.cashgift_tf.getText());
            }catch (Exception e){

            }
            this.item.setCashGift(amount);
        });

        //Add item when enter key is pressed
        this.qty_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.longetivity_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.sss_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.cashgift_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.bonus_tf.setOnAction(evt -> {
            addItem();
        });

        //Add item when enter key is pressed
        this.add_btn.setOnAction(evt -> {
            addItem();
        });

        //Reset the fields
        this.reset_btn.setOnAction(evt ->{reset();});

        this.parentController = Utility.getParentController();
    }

    public void addItem() {
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();
        if ((!desc.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            this.item.setDescription(desc);
            this.item.setRemarks(this.unit_tf.getText());
            this.parentController.receive(this.item);
            this.reset();
        }else{
            this.status_lbl.setText("Item is not set! Please provide the item details!");
        }
    }
    /**
     * Method to call when COB is created (COBController) and user wants to edit a newly added item
     */
    public void editItem() {
        this.status_lbl.setText("");
        String desc = this.description_tf.getText();
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();
        if ((!desc.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            this.item.setDescription(desc);
            this.item.setRemarks(this.unit_tf.getText());
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
        String cost = this.cost_tf.getText();
        String qty = this.qty_tf.getText();
        if ((!desc.isEmpty() && !cost.isEmpty() && !qty.isEmpty()) || (!desc.isEmpty() && cost.isEmpty() && qty.isEmpty())) {
            try{
                this.item.setDescription(desc);
                this.item.setRemarks(this.unit_tf.getText());
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
        this.item = new Salary();
        this.item.setcItemId(Utility.generateRandomId());
        this.item.setCost(0);
        this.item.setQty(0);
        this.item.setsSSPhilH(0);
        this.item.setBonus13(0);

        this.description_tf.setText("");
        this.cost_tf.setText("");
        this.qty_tf.setText("");
        this.sss_tf.setText("");
        this.bonus_tf.setText("");

        if (this.salary_type_cb.isSelected()){
            this.unit_tf.setText("SG ");
            this.item.setCashGift(5000);
            this.item.setLongetivity(0);
            this.cashgift_tf.setText("5000");
            this.longetivity_tf.setText("");
            this.longetivity_tf.setDisable(false);
        }else{
            this.unit_tf.setText(Salary.PER_DAY);
            this.item.setRemarks(Salary.PER_DAY);
            this.item.setCashGift(2000);
            this.item.setLongetivity(0);
            this.cashgift_tf.setText("2000");
            this.longetivity_tf.setText("0");
            this.longetivity_tf.setDisable(true);
        }

        this.description_tf.requestFocus();
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

    public Salary getItem() {
        return item;
    }

    public void setItem(Salary item) {
        this.item = item;
        this.oldAmount = this.item.getAmount();
    }

    public void showDetails(){
        this.description_tf.setText(this.item.getDescription());
        this.cost_tf.setText(this.item.getCost()+"");
        this.qty_tf.setText(this.item.getQty()+"");
        this.sss_tf.setText(this.item.getsSSPhilH()+"");
        this.bonus_tf.setText(Utility.formatDecimal(this.item.getBonus13()));
        this.unit_tf.setText(this.item.getRemarks());
        this.cashgift_tf.setText(this.item.getCashGift()+"");
        this.longetivity_tf.setText(this.item.getLongetivity()+"");
        //If salary is not per day (for regular employees), longetivity applies
        if (this.item.getRemarks().toUpperCase().contains(Salary.SG)){
            this.salary_type_cb.setSelected(true);
            this.longetivity_tf.setDisable(false);
        }else{
            this.salary_type_cb.setSelected(false);
            this.longetivity_tf.setDisable(true);
        }
    }
}
