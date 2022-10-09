package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ObjectTransaction;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TelleringAddBankRemittance extends MenuControllerHandler implements Initializable {

    @FXML DatePicker orDateFrom;
    @FXML DatePicker orDateTo;
    @FXML JFXComboBox<BankAccount> bankAccount;
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField checkNumber;
    @FXML JFXTextField amount;

    private ObjectTransaction parentController = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.parentController = Utility.getParentController();
        try {
            ObservableList<BankAccount> bankAccountsList = FXCollections.observableArrayList(BankAccountDAO.getAll());
            bankAccountsList.add(0,null);

            bankAccount.setItems(bankAccountsList);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAddEntry() {

        BankRemittance bankRemittance = new BankRemittance();
        bankRemittance.setOrDateFrom(orDateFrom.getValue());
        bankRemittance.setOrDateTo(orDateTo.getValue());
        bankRemittance.setDescription(bankAccount.getSelectionModel().getSelectedItem().getBankDescription());
        bankRemittance.setAccountNumber(accountNumber.getText());
        bankRemittance.setCheckNumber(checkNumber.getText());
        bankRemittance.setAmount(Double.parseDouble(amount.getText()));

        this.parentController.receive(bankRemittance);

        this.onClear();
    }

    public void onSelectBankAccount() {
        accountNumber.setText(bankAccount.getSelectionModel().getSelectedItem().getBankAccountNumber());
        checkNumber.requestFocus();
    }

    public void onClear() {
        orDateFrom.setValue(null);
        orDateTo.setValue(null);
        bankAccount.getSelectionModel().select(0);
        accountNumber.setText(null);
        checkNumber.setText(null);
        amount.setText(null);
        orDateFrom.requestFocus();
    }

    public void onCancel() {

    }
}
