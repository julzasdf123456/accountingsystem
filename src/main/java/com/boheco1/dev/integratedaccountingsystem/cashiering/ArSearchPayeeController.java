package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ArSearchPayeeController implements Initializable {

    @FXML
    JFXTextField nameOfPayee;
    @FXML
    TableView resultsTable;
    @FXML
    JFXButton selectTransaction;
    StackPane stackPane;
    ObservableList<TransactionHeader> tas;
    private TransactionHeader selectedItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        TextField receivedFrom = (TextField) Utility.getSelectedObject();
        nameOfPayee.setText(receivedFrom.getText());
        stackPane = Utility.getStackPane();
        tas = FXCollections.observableList(new ArrayList<>());
        initTable();
    }

    public void initTable() {
        TableColumn dateCol = new TableColumn<BankAccount, String>("Transaction Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<TransactionHeader, String>("transactionDate"));

        TableColumn particularsCol = new TableColumn<TransactionHeader, String>("Payee");
        particularsCol.setCellValueFactory(new PropertyValueFactory<TransactionHeader, String>("particulars"));

        TableColumn paymentForCol = new TableColumn<TransactionHeader, String>("Payment For");
        paymentForCol.setCellValueFactory(new PropertyValueFactory<TransactionHeader, String>("remarks"));

        TableColumn arNumberCol = new TableColumn<TransactionHeader, String>("AR Number");
        arNumberCol.setCellValueFactory(new PropertyValueFactory<TransactionHeader, String>("transactionNumber"));

        resultsTable.getColumns().add(dateCol);
        resultsTable.getColumns().add(arNumberCol);
        resultsTable.getColumns().add(particularsCol);
        resultsTable.getColumns().add(paymentForCol);

        resultsTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        resultsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        resultsTable.setItems(tas);
    }

    public void onSearch() {
        try {
            List<TransactionHeader> results = TransactionHeaderDAO.searchByPayee("%" + nameOfPayee.getText() + "%");
            tas.clear();
            tas.addAll(results);
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onTableClick() {
        selectedItem = (TransactionHeader) resultsTable.getSelectionModel().getSelectedItem();
        if(selectedItem!=null) {
            selectTransaction.setDisable(false);
        }
    }

    public void onSelectTransaction() {
        Utility.getParentController().receive(selectedItem);
    }
}
