package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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
    @FXML JFXButton generateReportBtn;
    @FXML StackPane stackPane;

    @FXML TextField remarksField;

    ObservableList<BankRemittance> tableList;

    private TransactionHeader transactionHeader;

    private void renderTable() {
        TableColumn orDateFromColumn = new TableColumn<BankRemittance, LocalDate>("OR Date From");
        orDateFromColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, LocalDate>("orDateFrom"));

        TableColumn descriptionColumn = new TableColumn<BankRemittance, String>("Bank Account Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("description"));

        TableColumn accountNoColumn = new TableColumn<BankRemittance, String>("Bank Account Number");
        accountNoColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountNumber"));

        TableColumn accountCodeColumn = new TableColumn<BankRemittance, String>("AccountCode");
        accountCodeColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountCode"));

        TableColumn checkNumberColumn = new TableColumn<BankRemittance, String>("Check Number");
        checkNumberColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("checkNumber"));

        TableColumn amountColumn = new TableColumn<BankRemittance, String>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, Double>("formattedAmount"));
        amountColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        TableColumn<BankRemittance, BankRemittance> actionCol = new TableColumn<>("Action");
        actionCol.setMinWidth(40);
        actionCol.setStyle( "-fx-alignment: CENTER-RIGHT;");

        // ADD ACTION BUTTONS
        actionCol.setCellValueFactory(stockStringCellDataFeatures -> new ReadOnlyObjectWrapper<BankRemittance>(stockStringCellDataFeatures.getValue()));
        actionCol.setCellFactory(stockStockTableColumn -> new TableCell<>(){
            FontIcon deleteIcon =  new FontIcon("mdi2t-trash-can");
            private final JFXButton deleteButton = new JFXButton("", deleteIcon);

            @Override
            protected void updateItem(BankRemittance bankRemittance, boolean b) {
                super.updateItem(bankRemittance, b);

                if (bankRemittance != null) {

                    deleteButton.setStyle("-fx-background-color: #f44336;");
                    deleteIcon.setIconSize(13);
                    deleteIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                    final JFXButton confirmDelete = new JFXButton("Confirm");

                    deleteButton.setOnAction(actionEvent -> {
                        confirmDelete(bankRemittance);
                    });

                    setGraphic(deleteButton);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });


        remittanceTable.getColumns().add(orDateFromColumn);
        remittanceTable.getColumns().add(descriptionColumn);
        remittanceTable.getColumns().add(accountNoColumn);
        remittanceTable.getColumns().add(accountCodeColumn);
        remittanceTable.getColumns().add(checkNumberColumn);
        remittanceTable.getColumns().add(amountColumn);
        remittanceTable.getColumns().add(actionCol);

        tableList = FXCollections.observableList(new ArrayList<BankRemittance>());

        remittanceTable.setItems(tableList);
    }

    private void confirmDelete(BankRemittance bankRemittance) {
        JFXButton confirm = new JFXButton("Confirm");

        final JFXDialog confirmDialog = DialogBuilder.showConfirmDialog("Delete Entry?","You are about to delete this entry.", confirm, stackPane, DialogBuilder.DANGER_DIALOG);;
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                removeEntry(bankRemittance);
                confirmDialog.close();
            }
        });
    }

    private void removeEntry(BankRemittance bankRemittance) {
        try{
            TransactionDetails td = bankRemittance.getTransactionDetails();
            TransactionDetailsDAO.delete(td);
            TransactionDetailsDAO.syncDebit(td.getPeriod(),td.getTransactionNumber(),"BR");
            tableList.remove(bankRemittance);
            remittanceTable.refresh();
            computeTotals();

            TransactionHeaderDAO.updateTransaction(td.getTransactionNumber(), td.getTransactionCode(), td.getPeriod());
        }catch(Exception ex) {
            ex.printStackTrace();
        }
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

        totalCheckAmount.setText(String.format("₱ %,.2f", check));
        totalCashAmount.setText(String.format("₱ %,.2f", cash));
        totalAmount.setText(String.format("₱ %,.2f", check+cash));
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remittanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        renderTable();
        remarksField.setEditable(false);
        Utility.setParentController(this);

        this.stackPane = Utility.getStackPane();
    }

    @FXML
    public void onAddEntry() {
        ModalBuilder.showModalFromXML(AddBankRemittance.class, "../cashiering/add_bank_remittance.fxml", Utility.getStackPane());
    }

    public void enableAddEntry() {

        try {
            LocalDate tdate = transactionDate.getValue();

            String dateStr = tdate.format(DateTimeFormatter.ofPattern("MMddyy"));

            remittanceNo.setText(dateStr);

            String transactionNumber = remittanceNo.getText();
            transactionHeader = TransactionHeaderDAO.get(transactionNumber, "BR");

            if(transactionHeader==null) {
                tableList.clear();
                remittanceTable.refresh();
                remarksField.setText(null);
                remarksField.setEditable(false);
                generateReportBtn.setDisable(true);
                computeTotals();
            }else {
                List<TransactionDetails> tds = TransactionDetailsDAO.get(transactionHeader.getPeriod(),dateStr,"BR");

                tableList.clear();

                for(TransactionDetails td: tds) {
                    if(td.getSequenceNumber()==999) continue;
                    BankRemittance br = new BankRemittance(td.getPeriod(), td.getOrDate(),null,td.getCheckNumber(), td.getDebit(), BankAccountDAO.get(td.getBankID()), td.getAccountCode());
                    br.setTransactionDetails(td);
                    tableList.add(br);
                }
                remittanceTable.refresh();
                remarksField.setText(transactionHeader.getRemarks());
                remarksField.setEditable(true);
                generateReportBtn.setDisable(false);
                computeTotals();
            }

            addEntryBtn.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void createTransactionHeader() {
        int year = transactionDate.getValue().getYear();
        int month = transactionDate.getValue().getMonthValue();
        LocalDate period = LocalDate.of(year, month, 1);

        transactionHeader = new TransactionHeader();
        transactionHeader.setTransactionNumber(remittanceNo.getText());
        transactionHeader.setPeriod(period);
        transactionHeader.setTransactionDate(transactionDate.getValue());
        transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
        transactionHeader.setDateEntered(LocalDateTime.now());
        transactionHeader.setRemarks(remarksField.getText());

        try {
            transactionHeader.setTransactionCode(TransactionHeader.getTransactionCodeProperty());
            TransactionHeaderDAO.add(transactionHeader);
            AlertDialogBuilder.messgeDialog("New Transaction","A new transaction has been created for \"BR\" " + remittanceNo.getText() + ".",stackPane, AlertDialogBuilder.INFO_DIALOG);
            tableList.clear();
            computeTotals();
            remarksField.setEditable(true);
            generateReportBtn.setDisable(false);
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onBankAccountsClick() {
        ModalBuilder.showModalFromXML(AddBankRemittance.class, "../cashiering/bank_accounts.fxml", Utility.getStackPane());
    }

    @FXML
    public void onRemarksUpdate() {
        if(transactionHeader==null) {
            createTransactionHeader();
        }

        try {
            TransactionHeaderDAO.updateRemarks(transactionHeader, remarksField.getText());
            AlertDialogBuilder.messgeDialog("Remarks Updated", "The remarks of this Bank Remittance has been updated.",stackPane, AlertDialogBuilder.INFO_DIALOG);
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @Override
    public void receive(Object o) {
        if(o instanceof BankRemittance){
            try {
                if (transactionHeader == null) {
                    createTransactionHeader();
                }else {
                    TransactionHeaderDAO.updateTransaction(transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode(), transactionHeader.getPeriod());
                }
                BankRemittance br = (BankRemittance) o;
                TransactionDetails td = new TransactionDetails();
                td.setTransactionCode(transactionHeader.getTransactionCode());
                td.setTransactionNumber(transactionHeader.getTransactionNumber());
                td.setPeriod(transactionHeader.getPeriod());
                td.setOrDate(br.getOrDateFrom());
                td.setBankID(br.getBankAccount().getId());
                td.setAccountCode(br.getBankAccount().getAccountCode());
                td.setDebit(br.getAmount());
                td.setSequenceNumber(TransactionDetailsDAO.getNextSequenceNumber(td.getPeriod(), td.getTransactionNumber()));
                td.setCheckNumber(br.getCheckNumber());
                td.setTransactionDate(transactionHeader.getTransactionDate());
                td.setDepositedDate(br.getDepositedDate());

                TransactionDetailsDAO.add(td);
                TransactionDetailsDAO.syncDebit(td.getPeriod(), td.getTransactionNumber(),transactionHeader.getTransactionCode());
                tableList.add((BankRemittance) o);
                computeTotals();
            }catch(Exception ex) {
                ex.printStackTrace();
            }


        }
    }

    @FXML
    public void onGenerateReport() {
        AlertDialogBuilder.messgeDialog("Coming Soon","This feature is yet to be implemented.",stackPane, AlertDialogBuilder.INFO_DIALOG);
    }

}
