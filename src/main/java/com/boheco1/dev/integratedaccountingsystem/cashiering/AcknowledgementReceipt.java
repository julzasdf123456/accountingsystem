package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;

import java.net.URL;
import java.util.ResourceBundle;

public class AcknowledgementReceipt extends MenuControllerHandler implements Initializable {

    @FXML DatePicker orDate;
    @FXML JFXTextField orNumber;
    @FXML JFXTextField amount;
    @FXML JFXTextField receivedFrom;
    @FXML JFXTextField paymentFor;

    @FXML JFXTextArea amountInWords;
    @FXML JFXComboBox accountDescription;

    @FXML
    TableView breakdownTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onNew() {

    }

    public void onSaveChanges() {

    }

    public void onDelete() {

    }
}
