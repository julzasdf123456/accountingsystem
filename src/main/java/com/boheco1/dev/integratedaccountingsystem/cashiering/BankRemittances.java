package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.BankRemittance;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.JFXButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BankRemittances extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    TableView<BankRemittance> remittanceTable;

    @FXML
    TextField remittanceNo;

    @FXML Label totalCheckAmount;
    @FXML Label totalCashAmount;
    @FXML Label totalAmount;
    @FXML DatePicker transactionDate;
    @FXML JFXButton addEntryBtn;
    @FXML StackPane stackPane;

    ObservableList<BankRemittance> tableList;

    private TransactionHeader transactionHeader;

    private void renderTable() {
        TableColumn orDateFromColumn = new TableColumn<BankRemittance, LocalDate>("OR Date From");
        orDateFromColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, LocalDate>("orDateFrom"));

//        TableColumn orDateToColumn = new TableColumn<BankRemittance, LocalDate>("OR Date To");
//        orDateToColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, LocalDate>("orDateTo"));

        TableColumn descriptionColumn = new TableColumn<BankRemittance, String>("Bank Account Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("description"));

        TableColumn accountNoColumn = new TableColumn<BankRemittance, String>("Bank Account Number");
        accountNoColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountNumber"));

        TableColumn checkNumberColumn = new TableColumn<BankRemittance, String>("Check Number");
        checkNumberColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("checkNumber"));

        TableColumn amountColumn = new TableColumn<BankRemittance, String>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, Double>("formattedAmount"));
        amountColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        remittanceTable.getColumns().add(orDateFromColumn);
//        remittanceTable.getColumns().add(orDateToColumn);
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

        this.stackPane = Utility.getStackPane();
    }

    @FXML
    public void onAddEntry() {
        ModalBuilder.showModalFromXML(AddBankRemittance.class, "../cashiering/add_bank_remittance.fxml", Utility.getStackPane());
    }

    public void enableAddEntry() {
        if(transactionDate.getValue()!=null && !remittanceNo.getText().isEmpty()) {
            try {
                String transactionNumber = remittanceNo.getText();
                transactionHeader = TransactionHeaderDAO.get(transactionNumber, "BR");

                if(transactionHeader==null) {
                    int year = transactionDate.getValue().getYear();
                    int month = transactionDate.getValue().getMonthValue();

                    transactionHeader = new TransactionHeader();
                    transactionHeader.setTransactionCode("BR");
                    transactionHeader.setTransactionNumber(remittanceNo.getText());
                    transactionHeader.setPeriod(LocalDate.of(year, month, 1));
                    transactionHeader.setTransactionDate(transactionDate.getValue());

                    try {
                        TransactionHeaderDAO.add(transactionHeader);
                        AlertDialogBuilder.messgeDialog("New Transaction","A new transaction has been created for \"BR " + transactionNumber + ".",stackPane, AlertDialogBuilder.INFO_DIALOG);
                    }catch(Exception ex) {
                        ex.printStackTrace();
                    }
                }

                addEntryBtn.setDisable(false);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else {
            addEntryBtn.setDisable(true);
        }
    }

    @FXML
    public void onBankAccountsClick() {
        ModalBuilder.showModalFromXML(AddBankRemittance.class, "../tellering/bank_accounts.fxml", Utility.getStackPane());
    }

    @Override
    public void receive(Object o) {
        if(o instanceof BankRemittance){
            BankRemittance br = (BankRemittance) o;
            TransactionDetails td = new TransactionDetails();
            td.setTransactionCode("BR");
            td.setTransactionNumber(transactionHeader.getTransactionNumber());
            td.setPeriod(transactionHeader.getPeriod());
            td.setOrDate(br.getOrDateFrom());
            td.setBankID(br.getBankAccount().getId());
            td.setAccountCode(br.getBankAccount().getAccountCode());

            try {
                TransactionDetailsDAO.add(td);
                tableList.add((BankRemittance) o);
                computeTotals();
            }catch(Exception ex) {
                ex.printStackTrace();
            }


        }
    }

}
