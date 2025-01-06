package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WithHoldingController extends MenuControllerHandler implements Initializable {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private Label status_lbl;

    @FXML
    private Label withhold_lbl;

    @FXML
    private TextField withholding_tf;

    @FXML
    private JFXButton save_btn;

    @FXML
    private TextField tin_tf;

    @FXML
    private Label withhold_Agent_lbl;

    @FXML
    private JFXComboBox<String> withhold_agent;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputHelper.restrictNumbersOnly(this.withholding_tf);
        withhold_agent.getItems().add("YES");
        withhold_agent.getItems().add("NO");
    }
    public JFXButton getSave_btn() {
        return save_btn;
    }

    public JFXComboBox getWithhold_Agent() {return this.withhold_agent;}

    public TextField getTin_tf(){ return this.tin_tf; }

    public TextField getWithhold(){return this.withholding_tf; }

    public Label getStatus_lbl() {
        return status_lbl;
    }

    public Label getWithhold_lbl() {
        return withhold_lbl;
    }
}
