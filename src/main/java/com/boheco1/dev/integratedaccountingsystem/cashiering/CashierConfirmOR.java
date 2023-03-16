package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputValidation;
import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.OROtherInfo;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class CashierConfirmOR implements Initializable {
    @FXML
    private JFXComboBox<String> paymentMode;
    @FXML
    private JFXComboBox<BankAccount> bankInfo;

    @FXML
    private JFXTextField tinNo;

    @FXML
    private JFXTextField orNumber;

    @FXML
    private JFXTextArea remarks;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        InputValidation.restrictNumbersOnly(tinNo);
        InputValidation.restrictNumbersOnly(orNumber);
        if(Utility.OR_NUMBER != 0) {
            orNumber.setText(""+Utility.OR_NUMBER);
        }

        paymentMode.getItems().add("CASH ON HAND");
        paymentMode.getItems().add("CASH IN BANK");
        paymentMode.getItems().add("CHEQUES");

        try {
            List<BankAccount> bankAccounts = BankAccountDAO.getAll();
            bankInfo.getItems().addAll(bankAccounts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        paymentMode.getSelectionModel().selectFirst();
        bankInfo.setDisable(true);
    }

    @FXML
    private void selectPaymentMode(ActionEvent event) {
        String payMode = paymentMode.getSelectionModel().getSelectedItem();
        if(payMode == null)return;
        if (payMode.equals("CASH ON HAND") || payMode.equals("CHEQUES")) {
            bankInfo.setDisable(true);
            bankInfo.getSelectionModel().clearSelection();
        } else {
            bankInfo.setDisable(false);
        }
    }

    @FXML
    private void confirmTransaction(ActionEvent event) {
        if(orNumber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "OR number is required.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(paymentMode.getSelectionModel().getSelectedItem()==null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select Payment Mode and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(paymentMode.getSelectionModel().getSelectedItem().equals("CASH IN BANK")) {
            if (bankInfo.getSelectionModel().getSelectedItem() == null) {
                AlertDialogBuilder.messgeDialog("System Message", "Please select bank and try again.",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                return;
            }
        }

        OROtherInfo orOtherInfo;
        if(paymentMode.getSelectionModel().getSelectedItem().equals("CASH IN BANK")) {
            orOtherInfo = new OROtherInfo(
                    paymentMode.getSelectionModel().getSelectedItem(),
                    bankInfo.getSelectionModel().getSelectedItem().getAccountCode(),
                    tinNo.getText(),
                    orNumber.getText(),
                    remarks.getText());
        }else{
            orOtherInfo = new OROtherInfo(
                    paymentMode.getSelectionModel().getSelectedItem(),
                    tinNo.getText(),
                    orNumber.getText(),
                    remarks.getText());
        }
        ModalBuilder.MODAL_CLOSE();
        Utility.getParentController().receive(orOtherInfo);

    }
}
