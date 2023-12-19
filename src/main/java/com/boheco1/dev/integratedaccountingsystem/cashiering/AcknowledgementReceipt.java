package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ParticularsAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import com.sun.javafx.print.PrintHelper;
import com.sun.javafx.print.Units;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.collections.ObservableSet;
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

import javax.print.*;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.MediaPrintableArea;
import javax.print.attribute.standard.OrientationRequested;
import javax.print.attribute.standard.PrinterName;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AcknowledgementReceipt extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML DatePicker orDate;
    @FXML TextField orNumber;
    @FXML JFXTextField amount;
    @FXML TextField receivedFrom;
    @FXML TextField address;
    @FXML TextField paymentFor;
    @FXML Label totalLabel;
    @FXML JFXTextArea amountInWords;
    @FXML JFXComboBox<ParticularsAccount> accountDescription;

    @FXML
    JFXButton addItemButton, deleteItemButton, cancelButton;

    @FXML
    TableView breakdownTable;
    JFXDialog modal;

    @FXML
    JFXButton printButton;

    @FXML
    ComboBox<Printer> printersDropdown;

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

            //set initial date to server date
            orDate.setValue(Utility.serverDate());

            renderTable();
            breakdownTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

            generatePrinters();

        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void onCancel() {
        if(currentTransaction!=null) {
            JFXButton confirm = new JFXButton("Confirm");

            JFXDialog dialog = DialogBuilder.showConfirmDialog("Cancel Transaction?",
                    "This action will cancel this transaction which will delete all the records concerning this transaction.",
                    confirm, stackPane, DialogBuilder.WARNING_DIALOG);
            confirm.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try{
                        if(currentTransaction!=null) {
                            currentTransaction.cancelTransaction();
                        }
                        loadTransaction();
                        dialog.close();
                    }catch(Exception ex) {
                        ex.printStackTrace();
                        AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                                stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    }
                }
            });
        }else {
            AlertDialogBuilder.messgeDialog("Error!", "There is no currently opened transaction",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }
    }

    private void renderTable() {
        TableColumn accountCodeCol = new TableColumn<BankAccount, String>("Account Code");
        accountCodeCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("accountCode"));

        TableColumn accountDescriptionCol = new TableColumn<BankAccount, String>("Account Description");
        accountDescriptionCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, String>("particulars"));

        TableColumn amountCol = new TableColumn<BankAccount, String>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<TransactionDetails, Double>("formattedARAmount"));
        amountCol.setStyle( "-fx-alignment: CENTER-RIGHT;");

        breakdownTable.getColumns().add(accountCodeCol);
        breakdownTable.getColumns().add(accountDescriptionCol);
        breakdownTable.getColumns().add(amountCol);

        breakdownTable.setItems(transactionDetails);
        breakdownTable.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        recomputeTotal();
    }

    private void recomputeTotal() {
        total = 0;
        for(TransactionDetails td: transactionDetails) {
            total += td.getCredit();
            total -= td.getDebit();
        }
        totalLabel.setText(String.format("₱ %,.2f", total));

    }

    private void resetItemsEntry() {
        try {
            amount.setText(null);
            accountDescription.getSelectionModel().clearSelection();
            amountInWords.setText(null);
//            orDate.setValue(Utility.serverDate());
            deleteItemButton.setDisable(true);
            amount.requestFocus();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
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
            currentTransaction.setName(receivedFrom.getText());
            currentTransaction.setAddress(address.getText());
            currentTransaction.setAmount(total);
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
            //Add debit entry based on total credits
            recomputeTotal();
            TransactionDetailsDAO.syncAR(currentTransaction, total);
            TransactionHeaderDAO.updateAmount(currentTransaction, total);

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

            orDate.setValue(Utility.serverDate());
            paymentFor.setText(null);
            receivedFrom.setText(null);
            paymentFor.setEditable(true);
            receivedFrom.setEditable(true);
            address.setText(null);

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

            if(entry==null) return;

            if (entry.isEmpty()) {
                amountInWords.setText(null);
                return;
            }

            double amt = Double.parseDouble(entry);
            amountInWords.setText(Utility.doubleAmountToWords(amt));
        }catch(IllegalArgumentException ex1) {
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

            double amt = Double.parseDouble(amount.getText());
            td.setAccountCode(pa.getAccountCode());
            td.setParticulars(pa.getParticulars());

            if(amt>=0) {
                td.setCredit(amt);
            }else {
                td.setDebit(Math.abs(amt));
            }

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
            System.out.println("Load Transaction...");
            receivedFrom.setText(currentTransaction.getParticulars());
            address.setText(currentTransaction.getAddress());
            paymentFor.setText(currentTransaction.getRemarks());
            orNumber.setText(currentTransaction.getTransactionNumber());
            orDate.setValue(currentTransaction.getTransactionDate());

            transactionDetails.clear();
            transactionDetails.addAll(TransactionDetailsDAO.getAR(currentTransaction.getTransactionDate(), currentTransaction.getTransactionNumber()));
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

    private void generatePrinters() throws Exception {
        ObservableSet<Printer> printers = Printer.getAllPrinters();

        List<Printer> printersList = new ArrayList<>();

        for(Printer printer: printers) {
            //populate printers to comboBox
            printersList.add(printer);
        }

        ObservableList<Printer> observableList = FXCollections.observableList(printersList);

        printersDropdown.setItems(observableList);

        String arPrinterName = Utility.getARPrinterName();

        if(arPrinterName!=null) {
            for(Printer p: observableList) {
                if(p.getName().matches(arPrinterName)) printersDropdown.getSelectionModel().select(p);
            }
        }
    }

    public void onPrint() {
        System.out.println("Commence printing...");

        Printer printer = printersDropdown.getSelectionModel().getSelectedItem();

        PrinterJob printJob;

        if(printer!=null) {
            printJob = PrinterJob.createPrinterJob(printer);
        }else {
            printJob = PrinterJob.createPrinterJob();
            printer = printJob.getPrinter();
        }

        System.out.println("Selected printer: " + printer.getName());

        printJob.setPrinter(printer);
//        Printer printer = printJob.getPrinter();

        Paper arsize = PrintHelper.createPaper("ARR", 18, 3, Units.INCH);

//        Paper letter = Paper.NA_LETTER;

//        Paper a4 = Paper.A4;

        PageLayout layout = printer.createPageLayout(arsize, PageOrientation.PORTRAIT, Printer.MarginType.HARDWARE_MINIMUM);

        System.out.println(arsize.getWidth() + "x" + arsize.getHeight() + " ");
        System.out.println("Printable: " + layout.getPrintableWidth() + "x" + layout.getPrintableHeight());

        printJob.setPrinter(printer);

        JobSettings jobSettings = printJob.getJobSettings();
        jobSettings.setPageLayout(layout);

        boolean printed = printJob.printPage( buildPrintableNode() );

        try {
            Utility.setARPrinter(printer.getName());
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Error!", ex.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }

        if(printed) {
            printJob.endJob();

        }else {
            System.out.println("Unable to create print job.");
        }
    }

    public void onPrintx() {
        try{
            InputStream is = new ByteArrayInputStream(getPrintData().getBytes());
            DocFlavor flavor =  DocFlavor.INPUT_STREAM.AUTOSENSE;

            PrintService service = PrintServiceLookup.lookupDefaultPrintService();
            PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
            printRequestAttributeSet.add(new MediaPrintableArea(0, 0, 217, 76, MediaPrintableArea.MM));
            printRequestAttributeSet.add(OrientationRequested.LANDSCAPE);

            DocPrintJob job = service.createPrintJob();
            Doc doc= new SimpleDoc(is, flavor, null);

            job.print(doc, printRequestAttributeSet);

            is.close();
        } catch (PrintException | IOException e) {
            e.printStackTrace();
        }
    }

    private String leftPadding(int length, String text) {
        StringBuffer sb = new StringBuffer();
        for(int i=0; i<length-text.length(); i++) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }

    private String getPrintData() {
        StringBuffer data = new StringBuffer();
        String line = "                                                                                ";

        String pmtFor = currentTransaction.getParticulars();

        String[] item = new String[transactionDetails.size()];
        String[] amount = new String[transactionDetails.size()];
        double totalAmount = total;
        int idx=0;
        for(TransactionDetails td: transactionDetails) {
            item[idx] = td.getParticulars();
//            amount[idx++] = String.format("%,.2f", td.getDebit());
            amount[idx++] = td.getFormattedARAmount();
//            totalAmount += td.getDebit();
        }

        String amountStr = Utility.doubleAmountToWords(totalAmount);
        String amountSplit1 = Utility.doubleAmountToWords(totalAmount);
        String amountSplit2 = "";
        if(amountSplit1.length()>38) {
            amountSplit1 = amountStr.substring(0, 35);
            amountSplit2 = amountStr.substring(35);
        }

        for(int i=0; i<1; i++) data.append(line);

        String dateStr = currentTransaction.getTransactionDate().format(DateTimeFormatter.ofPattern("MM/dd/YYYY"));

        data.append(new StringBuffer(line).replace(25, 35, dateStr).replace(67,77,dateStr));

        data.append(line);

        data.append(new StringBuffer(line).replace(4,pmtFor.length()+4, pmtFor)
                .replace(45,pmtFor.length()+45, pmtFor));

        data.append(new StringBuffer(line).replace(4, amountSplit1.length()+4, amountSplit1)
                .replace(45, amountSplit1.length()+45, amountSplit1));
        data.append(new StringBuffer(line).replace(6, amountSplit2.length()+6, amountSplit2)
                .replace(47, amountSplit2.length()+47, amountSplit2));

        data.append(line);

        int nLines = 0;

        for(int i=0; i<item.length; i++) {
            String amt = leftPadding(10, amount[i]);
            String name1 = Utility.breakString(item[i], 25, 1);
            String name2 = Utility.breakString(item[i], 25, 2);
            data.append(
                    new StringBuffer(line).replace(0, name1.length(), name1).replace(25, 35, amt)
                            .replace(43, name1.length()+43, name1).replace(68,78, amt)
            );

            nLines++;

            if(!name2.isEmpty()) {
                data.append(
                        new StringBuffer(line).replace(4, name2.length()+4, name2).replace(47, 47+name2.length(), name2)
                );
                nLines++;
            }
        }

        for(int i=0; i<3-nLines; i++) data.append(line);

        data.append(
                new StringBuffer(line).replace(25,35, leftPadding(10,String.format("%,.2f", totalAmount)))
                        .replace(68,78, leftPadding(10,String.format("%,.2f", totalAmount)))
        );

        data.append(line);

        String username = ActiveUser.getUser().getUserName();

        data.append(
                new StringBuffer(line).replace(25, 25+username.length(), username)
                        .replace(68, 68+username.length(), username)
        );

        for(int i=0; i<5; i++) data.append(line);

        return data.toString();
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
                amount[i] = td.getCredit();
            }

            cnt.setData(receivedFrom, Utility.doubleAmountToWords(total), total, pmtFor, amount, currentTransaction.getTransactionDate() );

            return root;
        }catch(IOException ex) {
            ex.printStackTrace();
            return null;
        }
    }


}
