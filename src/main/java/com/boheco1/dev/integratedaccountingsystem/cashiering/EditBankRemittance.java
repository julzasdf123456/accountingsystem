package com.boheco1.dev.integratedaccountingsystem.cashiering;

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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class EditBankRemittance  extends MenuControllerHandler implements Initializable{
    @FXML DatePicker depositedDate;
    @FXML ComboBox<BankAccount> bankAccount;
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField checkNumber;
    @FXML JFXTextField amount;

    private ObjectTransaction parentController = null;
    private StackPane stackPane;

    private BankRemittance selectedBR;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.parentController = Utility.getParentController();
        this.stackPane = Utility.getStackPane();

        try {
            selectedBR = (BankRemittance) Utility.getSelectedObject();
            ObservableList<BankAccount> bankAccountsList = FXCollections.observableArrayList(BankAccountDAO.getAll());
            bankAccountsList.add(0,null);

            bankAccount.setItems(bankAccountsList);
            bankAccount.getSelectionModel().clearAndSelect(2);

            depositedDate.setValue(selectedBR.getDepositedDate());
            bankAccount.getSelectionModel().select(selectedBR.getBankAccount());
            accountNumber.setText(selectedBR.getAccountNumber());
            checkNumber.setText(selectedBR.getCheckNumber());
            amount.setText(String.valueOf(selectedBR.getAmount()));

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onCancel() {
        parentController.receive(null);
    }

    public void onSaveChanges() {
        selectedBR.setBankAccount(bankAccount.getSelectionModel().getSelectedItem());
        selectedBR.setCheckNumber(checkNumber.getText());
        selectedBR.setAmount(Double.parseDouble(amount.getText()));
        selectedBR.setAccountCode(bankAccount.getSelectionModel().getSelectedItem().getAccountCode());
        selectedBR.setDepositedDate(depositedDate.getValue());
        selectedBR.setIsEdit(true);

        parentController.receive(selectedBR);
    }

    public void onCheckNumberEntry() {
        amount.requestFocus();
    }

    public void onSelectBankAccount() {
        if(bankAccount.getSelectionModel().getSelectedIndex()==0) return;
        accountNumber.setText(bankAccount.getSelectionModel().getSelectedItem().getBankAccountNumber());
//        checkNumber.requestFocus();
        amount.requestFocus();
    }

}
