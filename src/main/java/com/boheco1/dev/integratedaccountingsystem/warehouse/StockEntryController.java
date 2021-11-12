package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane;

public class StockEntryController {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private JFXTextField stockName, serialNumber, brand, model, quantity, unit, price, neaCode;

    @FXML
    private JFXTextArea comments, description;

    @FXML
    private DatePicker manuDate, valDate;

    @FXML
    private JFXComboBox<?> type, source;

    @FXML
    private JFXButton saveBtn;

    @FXML
    private void saveBtn(ActionEvent event) {

    }
}
