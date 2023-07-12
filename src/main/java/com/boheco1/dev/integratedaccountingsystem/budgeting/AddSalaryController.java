package com.boheco1.dev.integratedaccountingsystem.budgeting;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

public class AddSalaryController {

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
