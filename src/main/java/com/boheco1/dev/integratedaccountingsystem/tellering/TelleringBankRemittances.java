package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TelleringBankRemittances extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    TableView<BankRemittance> remittanceTable;

    @FXML
    TextField remittanceNo;

    @FXML Label totalCheckAmount;
    @FXML Label totalCashAmount;
    @FXML Label totalAmount;

    ObservableList<BankRemittance> tableList;
    private void renderTable() {
        TableColumn orDateFromColumn = new TableColumn<BankRemittance, LocalDate>("OR Date From");
        orDateFromColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, LocalDate>("orDateFrom"));

        TableColumn orDateToColumn = new TableColumn<BankRemittance, LocalDate>("OR Date To");
        orDateToColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, LocalDate>("orDateTo"));

        TableColumn descriptionColumn = new TableColumn<BankRemittance, String>("Bank Account Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("description"));

        TableColumn accountNoColumn = new TableColumn<BankRemittance, String>("Bank Account Number");
        accountNoColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountNumber"));

        TableColumn checkNumberColumn = new TableColumn<BankRemittance, String>("Check Number");
        checkNumberColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("checkNumber"));

        TableColumn amountColumn = new TableColumn<BankRemittance, Double>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, Double>("amount"));
        amountColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        remittanceTable.getColumns().add(orDateFromColumn);
        remittanceTable.getColumns().add(orDateToColumn);
        remittanceTable.getColumns().add(descriptionColumn);
        remittanceTable.getColumns().add(accountNoColumn);
        remittanceTable.getColumns().add(checkNumberColumn);
        remittanceTable.getColumns().add(amountColumn);

        tableList = FXCollections.observableList(new ArrayList<BankRemittance>());

        remittanceTable.setItems(tableList);
    }

    private void computeTotals() {
        double cash = 0;
        double check = 0;

        for(BankRemittance br: tableList) {
            if(br.getCheckNumber()!=null && !br.getCheckNumber().isEmpty() ){
                check += br.getAmount();
            }else{
                cash += br.getAmount();
            }
        }

        totalCheckAmount.setText(String.format("%,.2f", check));
        totalCashAmount.setText(String.format("%,.2f", cash));
        totalAmount.setText(String.format("%,.2f", check+cash));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remittanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        renderTable();
        Utility.setParentController(this);
    }

    @FXML
    public void onAddEntry() {
        ModalBuilder.showModalFromXML(TelleringAddBankRemittance.class, "../tellering/tellering_add_bank_remittance.fxml", Utility.getStackPane());
    }

    @FXML
    public void onSaveChanges() {

    }

    @Override
    public void receive(Object o) {
        if(o instanceof BankRemittance){
            tableList.add((BankRemittance) o);
            computeTotals();
        }
    }
}
