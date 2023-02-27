package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ParticularsAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.BankAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.ParticularsAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.*;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.print.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.poi.ss.formula.functions.Column;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AcknowledgementReceipt extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML DatePicker orDate;
    @FXML TextField orNumber;
    @FXML JFXTextField amount;
    @FXML TextField receivedFrom;
    @FXML TextField paymentFor;
    @FXML Label totalLabel;
    @FXML JFXTextArea amountInWords;
    @FXML JFXComboBox<ParticularsAccount> accountDescription;

    @FXML
    JFXButton addItemButton, deleteItemButton;

    @FXML
    TableView breakdownTable;
    JFXDialog modal;

    @FXML
    JFXButton printButton;

    private StackPane stackPane;

    private TransactionHeader currentTransaction;
    private TransactionDetails currentItem;

    private ObservableList<TransactionDetails> transactionDetails;

    private float total;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        stackPane = Utility.getStackPane();
        try {

            int nextARNumber = TransactionHeaderDAO.getNextARNumber();

            if(nextARNumber>0) {
                orNumber.setText(nextARNumber+"");
            }

            accountDescription.setItems(FXCollections.observableList(ParticularsAccountDAO.getByType("AR")));
            transactionDetails = FXCollections.observableList(new ArrayList<>());
            renderTable();
            breakdownTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);



        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void renderTable() {
        TableColumn accountCodeCol = new TableColumn<BankAccount, String>("Account Code");
        accountCodeCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("accountCode"));

        TableColumn accountDescriptionCol = new TableColumn<BankAccount, String>("Account Description");
        accountDescriptionCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("particulars"));

        TableColumn amountCol = new TableColumn<BankAccount, String>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, Double>("formattedDebit"));
        amountCol.setStyle( "-fx-alignment: CENTER-RIGHT;");

        breakdownTable.getColumns().add(accountCodeCol);
        breakdownTable.getColumns().add(accountDescriptionCol);
        breakdownTable.getColumns().add(amountCol);

        breakdownTable.setItems(transactionDetails);
        breakdownTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
    }

    private void recomputeTotal() {
        total = 0;
        for(TransactionDetails td: transactionDetails) {
            total += td.getDebit();
        }
        totalLabel.setText(String.format("₱ %,.2f", total));
    }

    private void resetItemsEntry() {
        amount.setText(null);
        accountDescription.getSelectionModel().clearSelection();
        amountInWords.setText(null);
        deleteItemButton.setDisable(true);
        amount.requestFocus();
    }
    public void onNew() {
        resetItemsEntry();
    }
    public void onSaveChanges() {

        if(orDate.getValue()==null) {
            AlertDialogBuilder.messgeDialog("Error!","You must first set the Date",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(orNumber.getText()==null || orNumber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Error!","You must first set the OR/AR Number",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(receivedFrom.getText()==null || receivedFrom.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Error!","You must first set the Received From field.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(transactionDetails.size()<1) {
            AlertDialogBuilder.messgeDialog("Error!","It is required to have at least one item in the breakdown table.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        int month = orDate.getValue().getMonthValue();
        int year = orDate.getValue().getYear();

        LocalDate period = LocalDate.of(year, month, 1);

        boolean isNew = false;
        //create Transaction Header if null
        if(currentTransaction==null) {
            isNew=true;
            currentTransaction = new TransactionHeader();
            currentTransaction.setPeriod(period);
            currentTransaction.setTransactionCode("AR");
            currentTransaction.setTransactionNumber(orNumber.getText());
            currentTransaction.setTransactionDate(orDate.getValue());
            currentTransaction.setParticulars(receivedFrom.getText());
            currentTransaction.setRemarks(paymentFor.getText());
            try {
                TransactionHeaderDAO.add(currentTransaction);
            }catch(Exception ex) {
                ex.printStackTrace();
                AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }else {
            //update transaction details
        }

        try {
            int num = 0;
            for(TransactionDetails td: transactionDetails) {
                if(td.getTransactionNumber()==null) {
                    td.setTransactionNumber(currentTransaction.getTransactionNumber());
                    td.setPeriod(currentTransaction.getPeriod());
                    td.setTransactionDate(currentTransaction.getTransactionDate());
                    td.setTransactionCode("AR");
                    td.setOrDate(currentTransaction.getTransactionDate());
                    TransactionDetailsDAO.add(td);
                    num++;
                }
            }
            TransactionDetailsDAO.syncDebit(currentTransaction.getPeriod(), currentTransaction.getTransactionNumber(),"AR");
            AlertDialogBuilder.messgeDialog("Saved!", (isNew ? "New AR transaction saved with " : "Current AR saved with ") + num + " new " + (num>1? " items." : "item"),
                    stackPane, AlertDialogBuilder.INFO_DIALOG);
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onReset() {
        try {
            int nextARNumber = TransactionHeaderDAO.getNextARNumber();

            if(nextARNumber>0) {
                orNumber.setText(nextARNumber+"");
            }

            orDate.setValue(null);
            paymentFor.setText(null);
            receivedFrom.setText(null);
            paymentFor.setEditable(true);
            receivedFrom.setEditable(true);

            transactionDetails.clear();

            resetItemsEntry();
            currentItem = null;
            currentTransaction = null;

            recomputeTotal();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }
    public void onDelete() {
        JFXButton confirm = new JFXButton("Confirm");

        JFXDialog dialog = DialogBuilder.showConfirmDialog("Delete?","Please confirm deleting this item.", confirm, stackPane, DialogBuilder.WARNING_DIALOG);
        confirm.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try{
                    if(currentItem.getTransactionNumber()!=null) {
                        TransactionDetailsDAO.delete(currentItem);
                        TransactionDetailsDAO.syncDebit(currentTransaction.getPeriod(), currentTransaction.getTransactionNumber(),"AR");
                    }

                    transactionDetails.remove(currentItem);
                    currentItem = null;
                    recomputeTotal();
                    dialog.close();
                }catch(Exception ex) {
                    ex.printStackTrace();
                    AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            }
        });

    }

    public void onSearchPayee() {
        Utility.setParentController(this);
        Utility.setSelectedObject(receivedFrom);
        modal = ModalBuilder.showModalFromXML(ArSearchPayeeController.class, "../cashiering/ar_search_payee.fxml", Utility.getStackPane());
    }

    public void onAmountEntry() {
        try {
            String entry = amount.getText();

            if(entry.isEmpty()) {
                amountInWords.setText(null);
                return;
            }

            double amt = Double.parseDouble(entry);
            amountInWords.setText(Utility.doubleAmountToWords(amt));
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onItemEntry() {
        if(amount.getText()!=null && !amount.getText().isEmpty() && accountDescription.getSelectionModel().getSelectedItem()!=null) {
            addItemButton.setDisable(false);
        }else {
            addItemButton.setDisable(true);
        }
    }

    public void onAddItem() {
        TransactionDetails td = new TransactionDetails();
        ParticularsAccount pa = accountDescription.getSelectionModel().getSelectedItem();
        try {
            td.setAccountCode(pa.getAccountCode());
            td.setParticulars(pa.getParticulars());
            td.setDebit(Double.parseDouble(amount.getText()));

            transactionDetails.add(td);

            recomputeTotal();

            resetItemsEntry();
        }catch(NumberFormatException ex) {
            AlertDialogBuilder.messgeDialog("Error!", "Possible invalid value for amount field.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onTransactionCheck() {
        if(orDate.getValue()!=null && !orNumber.getText().isEmpty()) {
            try {
                //find or create Transaction Header
                currentTransaction = TransactionHeaderDAO.get(orNumber.getText(),"AR", orDate.getValue());

                if(currentTransaction!=null) {
                    loadTransaction();
                }else {
                    resetItemsEntry();
                    receivedFrom.setText(null);
                    paymentFor.setText(null);
                    receivedFrom.setEditable(true);
                    paymentFor.setEditable(true);
                    transactionDetails.clear();
                    receivedFrom.requestFocus();
                }
                deleteItemButton.setDisable(true);
            }catch(Exception ex) {
                ex.printStackTrace();
                AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }

        }
    }

    public void onTableClick() {
        if(breakdownTable.getSelectionModel().getSelectedItem()!=null){
            if(breakdownTable.getSelectionModel().getSelectedItem() instanceof  TransactionDetails) {
                TransactionDetails td = (TransactionDetails) breakdownTable.getSelectionModel().getSelectedItem();
                currentItem = td;
                deleteItemButton.setDisable(false);
            }
        }
    }

    private void loadTransaction() {
        try {
            receivedFrom.setText(currentTransaction.getParticulars());
            paymentFor.setText(currentTransaction.getRemarks());
            orNumber.setText(currentTransaction.getTransactionNumber());
            orDate.setValue(currentTransaction.getTransactionDate());

            transactionDetails.clear();
            transactionDetails.addAll(TransactionDetailsDAO.getDebitOnly(currentTransaction.getPeriod(), currentTransaction.getTransactionNumber(), "AR"));
            paymentFor.setEditable(false);
            receivedFrom.setEditable(false);
            recomputeTotal();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @Override
    public void receive(Object o) {
        if(o instanceof TransactionHeader) {
            currentTransaction = (TransactionHeader) o;
            loadTransaction();
            modal.close();
        }
    }

    public void onPrint() {
        System.out.println("Commence printing...");
        PrinterJob printJob = PrinterJob.createPrinterJob();
        Printer printer = printJob.getPrinter();

        Paper arsize = PrintHelper.createPaper("8.5x11", 8.5,8.5,Units.INCH);

        Paper letter = Paper.NA_LETTER;

        Paper a4 = Paper.A4;

        PageLayout layout = printer.createPageLayout(letter, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

        System.out.println(letter.getWidth() + "x" + letter.getHeight() + " ");
        System.out.println("Printable: " + layout.getPrintableWidth() + "x" + layout.getPrintableHeight());

        printJob.setPrinter(printer);

        JobSettings jobSettings = printJob.getJobSettings();
        jobSettings.setPageLayout(layout);

        boolean printed = printJob.printPage( buildPrintableNode() );

        if(printed) {
            printJob.endJob();
        }else {
            System.out.println("Unable to create print job.");
        }
    }

    private Node buildPrintableNode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ar_print.fxml"));
            Parent root = loader.load();

            ArPrint cnt = loader.getController();

            String receivedFrom = currentTransaction.getParticulars();

            String[] pmtFor = new String[transactionDetails.size()];
            double[] amount = new double[transactionDetails.size()];

            for(int i=0; i<transactionDetails.size(); i++) {
                TransactionDetails td = transactionDetails.get(i);
                pmtFor[i] = td.getParticulars();
                amount[i] = td.getDebit();
            }

            cnt.setData(receivedFrom, Utility.doubleAmountToWords(total), total, pmtFor, amount );

            return root;
        }catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
