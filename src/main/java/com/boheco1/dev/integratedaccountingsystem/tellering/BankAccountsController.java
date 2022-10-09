package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BankAccountsController implements Initializable {
    @FXML JFXTextField accountNumber;
    @FXML JFXTextField bankDescription;
    @FXML JFXTextField accountCode;
    @FXML TableView banksTable;

    private StackPane stackPane;

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
            BankAccount ba = new BankAccount(
                    accountNumber.getText(),
                    bankDescription.getText(),
                    accountCode.getText()
            );

            try {
                BankAccountDAO.add(ba);
                banksTable.getItems().add(ba);
            }catch(Exception ex) {
                ex.printStackTrace();
            }
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
        try {
            bankAccountsList = FXCollections.observableList(BankAccountDAO.getAll());
            banksTable.setItems(bankAccountsList);
            banksTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        }catch(Exception ex) {
            ex.printStackTrace();
        }


    }
}
