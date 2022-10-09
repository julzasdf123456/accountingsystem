package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class DCRController extends MenuControllerHandler implements Initializable {


    @FXML
    private JFXComboBox<?> month_cb;

    @FXML
    private JFXTextField day_tf;

    @FXML
    private JFXTextField year_tf;

    @FXML
    private JFXButton view_btn;

    @FXML
    private JFXButton print_dcr_btn;

    @FXML
    private TableView<?> dcr_breakdown_table;

    @FXML
    private TableView<?> payments_table;

    @FXML
    private Label dcr_total;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }


}
