package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.DialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BankAccountsController implements Initializable {
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField bankDescription;
    @FXML JFXTextField accountCode;
    @FXML TableView banksTable;

    private StackPane stackPane;

    private BankAccount currentBankAccount;

    private JFXButton delBtn;
    private JFXDialog confirmDelete;

    ObservableList<BankAccount> bankAccountsList;

    @FXML
    public void onSaveChanges() {

        if (accountNumber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid Account Number",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else if(bankDescription.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "The Bank Description cannot be blank.",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else if(accountCode.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid Account Code.",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }else {
            if(currentBankAccount==null) {
                BankAccount ba = new BankAccount(
                        accountNumber.getText(),
                        bankDescription.getText(),
                        accountCode.getText()
                );

                try {
                    BankAccountDAO.add(ba);
                    banksTable.getItems().add(ba);
                    currentBankAccount = null;
                    reset();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }else {
                //Update an existing bank account
                currentBankAccount.setBankAccountNumber(accountNumber.getText());
                currentBankAccount.setBankDescription(bankDescription.getText());
                currentBankAccount.setAccountCode(accountCode.getText());
                try {
                    BankAccountDAO.update(currentBankAccount);
                    banksTable.refresh();
                }catch(Exception ex) {
                    ex.printStackTrace();
                }
            }

        }
    }

    public void onNewReset() {
        this.currentBankAccount = null;
        reset();
    }

    private void reset() {
        accountNumber.setText(null);
        bankDescription.setText(null);
        accountCode.setText(null);
        accountNumber.requestFocus();
    }

    public void onTableClick() {
        currentBankAccount = (BankAccount) banksTable.getSelectionModel().getSelectedItem();
        accountNumber.setText(currentBankAccount.getBankAccountNumber());
        bankDescription.setText(currentBankAccount.getBankDescription());
        accountCode.setText(currentBankAccount.getAccountCode());
        accountNumber.requestFocus();
    }

    public void onDelete() {
        confirmDelete = DialogBuilder.showConfirmDialog("Delete?","Are you sure you want to delete this Bank Account?",delBtn,stackPane,DialogBuilder.DANGER_DIALOG);
    }

    public void deleteBankAccount() {
        try {
            BankAccountDAO.delete(currentBankAccount);
            banksTable.getItems().remove(currentBankAccount);
            banksTable.refresh();
            currentBankAccount = null;
            reset();
            confirmDelete.close();
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void renderTable() {
        TableColumn accountNumberCol = new TableColumn<BankAccount, String>("Account Number");
        accountNumberCol.setCellValueFactory(new PropertyValueFactory<BankAccount, String>("bankAccountNumber"));

        TableColumn bankDescriptionCol = new TableColumn<BankAccount, String>("Bank Description");
        bankDescriptionCol.setCellValueFactory(new PropertyValueFactory<BankAccount, String>("bankDescription"));

        TableColumn accountCodeCol = new TableColumn<BankAccount, String>("Account Code");
        accountCodeCol.setCellValueFactory(new PropertyValueFactory<BankAccount, String>("accountCode"));

        banksTable.getColumns().add(accountNumberCol);
        banksTable.getColumns().add(bankDescriptionCol);
        banksTable.getColumns().add(accountCodeCol);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        renderTable();
        stackPane = Utility.getStackPane();
        delBtn = new JFXButton("Delete");
        delBtn.setTextFill(Paint.valueOf(ColorPalette.DANGER));
        delBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                deleteBankAccount();
            }
        });
        try {
            bankAccountsList = FXCollections.observableList(BankAccountDAO.getAll());
            banksTable.setItems(bankAccountsList);
            banksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            banksTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }catch(Exception ex) {
            ex.printStackTrace();
        }


    }
}
