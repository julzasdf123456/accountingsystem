package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import java.net.URL;
import java.util.ResourceBundle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class PowerBillsPaymentController extends MenuControllerHandler implements Initializable {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField con_name_tf;

    @FXML
    private JFXTextField account_tf;

    @FXML
    private JFXTextField type_tf;

    @FXML
    private JFXTextField status_tf;

    @FXML
    private JFXTextField acct_no_tf;

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXTextField con_addr_tf;

    @FXML
    private JFXTextField cp_no_tf;

    @FXML
    private JFXTextField invoice_tf1;

    @FXML
    private JFXButton view_account_tf;

    @FXML
    private TableView<?> fees_table;

    @FXML
    private TextField or_no_tf;

    @FXML
    private JFXButton set_or;

    @FXML
    private TextField payment_tf;

    @FXML
    private JFXButton add_check_btn;

    @FXML
    private JFXButton clear_check_btn;

    @FXML
    private ListView<?> checks_lv;

    @FXML
    private TextField total_paid_tf;

    @FXML
    private JFXButton transact_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    @FXML
    void reset(ActionEvent event) {

    }

}
