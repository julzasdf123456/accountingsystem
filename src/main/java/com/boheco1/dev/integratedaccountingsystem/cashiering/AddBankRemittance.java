package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
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
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AddBankRemittance extends MenuControllerHandler implements Initializable {

//    @FXML DatePicker orDateFrom;
    @FXML DatePicker depositedDate;
    @FXML JFXComboBox<BankAccount> bankAccount;
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField checkNumber;
    @FXML JFXTextField amount;

    private ObjectTransaction parentController = null;
    private StackPane stackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.parentController = Utility.getParentController();
        this.stackPane = Utility.getStackPane();
        try {
            ObservableList<BankAccount> bankAccountsList = FXCollections.observableArrayList(BankAccountDAO.getAll());
            bankAccountsList.add(0,null);

            bankAccount.setItems(bankAccountsList);

            LocalDate date = LocalDate.now();

//            orDateFrom.setValue(date);

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void onAddEntry() {
        if(bankAccount.getSelectionModel().isEmpty() || bankAccount.getSelectionModel().getSelectedItem()==null) {
            AlertDialogBuilder.messgeDialog("Invalid Entry", "Please select a Bank Account", stackPane, AlertDialogBuilder.WARNING_DIALOG);
//        }else if(orDateFrom.getValue()==null){
//            AlertDialogBuilder.messgeDialog("Invalid Entry","Please enter a valid OR Date.",stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else if(amount.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Entry","Please enter a valid amount.",stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {
            try {
                BankRemittance bankRemittance = new BankRemittance();
//                bankRemittance.setOrDateFrom(orDateFrom.getValue());
                bankRemittance.setBankAccount(bankAccount.getSelectionModel().getSelectedItem());
                bankRemittance.setCheckNumber(checkNumber.getText());
                bankRemittance.setAmount(Double.parseDouble(amount.getText()));
                bankRemittance.setAccountCode(bankAccount.getSelectionModel().getSelectedItem().getAccountCode());
                bankRemittance.setDepositedDate(depositedDate.getValue());

                this.parentController.receive(bankRemittance);

                this.onClear();
            }catch(NumberFormatException ex) {
                AlertDialogBuilder.messgeDialog("Invalid Entry","Please enter a valid amount.",stackPane, AlertDialogBuilder.WARNING_DIALOG);
            }
        }
    }

    public void onSelectBankAccount() {
        if(bankAccount.getSelectionModel().getSelectedIndex()==0) return;
        accountNumber.setText(bankAccount.getSelectionModel().getSelectedItem().getBankAccountNumber());
//        checkNumber.requestFocus();
        amount.requestFocus();
    }

    public void onCheckNumberEntry() {
        amount.requestFocus();
    }

    public void onClear() {
//        orDateFrom.setValue(null);
        bankAccount.getSelectionModel().clearSelection();
        accountNumber.setText(null);
        checkNumber.setText(null);
        amount.setText(null);
        depositedDate.setValue(null);
        depositedDate.requestFocus();
//        orDateFrom.requestFocus();
    }

}
