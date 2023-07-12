package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AddTravelController {
    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label status_lbl;

    @FXML
    private JFXTextField description_tf;

    @FXML
    private CheckBox exists_check;

    @FXML
    private Label unit_lbl;

    @FXML
    private Label price_lbl;

    @FXML
    private Label price_lbl1;

    @FXML
    private JFXTextField qty_ty;

    @FXML
    private JFXTextField days_ty;

    @FXML
    private JFXTextField times_tf;

    @FXML
    private JFXTextField unit_tf;

    @FXML
    private JFXTextField travel_tf;

    @FXML
    private JFXTextField total_amount_tf;

    @FXML
    private JFXComboBox<?> mode_cb;

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
