package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class BankAccountsController implements Initializable {
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField bankDescription;
    @FXML JFXTextField accountCode;
    @FXML TableView banksTable;



    @FXML
    public void onSaveChanges() {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        
    }
}
