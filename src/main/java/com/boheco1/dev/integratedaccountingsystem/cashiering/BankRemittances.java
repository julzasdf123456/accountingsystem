package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.PeriodDAO;
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
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import jdk.jshell.execution.Util;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.xssf.usermodel.extensions.XSSFCellBorder;
import org.kordamp.ikonli.javafx.FontIcon;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.text.DecimalFormat;
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
    @FXML JFXButton bulkEntryBtn;
    @FXML JFXButton generateReportBtn;
    @FXML StackPane stackPane;
    @FXML TextField remarksField;
    @FXML Label totalARAmount;
    @FXML Label totalORAmount;
    @FXML Label totalReceiptAmount;
    @FXML Label balancingFigure;


    private double totalReceipt;
    private double totalCollection;

    private JFXDialog modal;

    ObservableList<BankRemittance> tableList;

    private TransactionHeader transactionHeader;

    private BankRemittance selectedItem;

    private void renderTable() {

        TableColumn descriptionColumn = new TableColumn<BankRemittance, String>("Bank Account Description");
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("description"));

        TableColumn accountNoColumn = new TableColumn<BankRemittance, String>("Bank Account Number");
        accountNoColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountNumber"));

        TableColumn accountCodeColumn = new TableColumn<BankRemittance, String>("AccountCode");
        accountCodeColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("accountCode"));

        TableColumn checkNumberColumn = new TableColumn<BankRemittance, String>("Check Number");
        checkNumberColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("checkNumber"));

        TableColumn depositedDateColumn = new TableColumn<BankRemittance, String>("Deposited Date");
        depositedDateColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, String>("depositedDateStr"));

        TableColumn amountColumn = new TableColumn<BankRemittance, String>("Amount");
        amountColumn.setCellValueFactory(new PropertyValueFactory<BankRemittance, Double>("formattedAmount"));
        amountColumn.setStyle( "-fx-alignment: CENTER-RIGHT;");

        TableColumn<BankRemittance, BankRemittance> actionCol = new TableColumn<>("Action");
        actionCol.setMinWidth(40);
        actionCol.setStyle( "-fx-alignment: CENTER;");

        // ADD ACTION BUTTONS
        actionCol.setCellValueFactory(stockStringCellDataFeatures -> new ReadOnlyObjectWrapper<BankRemittance>(stockStringCellDataFeatures.getValue()));
        actionCol.setCellFactory(stockStockTableColumn -> new TableCell<>(){
            FontIcon deleteIcon =  new FontIcon("mdi2t-trash-can");
            FontIcon editIcon = new FontIcon("mdi2b-book-edit");
            private final JFXButton deleteButton = new JFXButton("", deleteIcon);
            private final JFXButton editButton = new JFXButton("", editIcon);

            @Override
            protected void updateItem(BankRemittance bankRemittance, boolean b) {
                super.updateItem(bankRemittance, b);

                if (bankRemittance != null) {

                    HBox hbox = new HBox();
                    hbox.setSpacing(10);
                    hbox.setAlignment(Pos.CENTER);

                    editButton.setStyle("-fx-background-color: green");
                    editIcon.setIconSize(13);
                    editIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                    deleteButton.setStyle("-fx-background-color: #f44336;");
                    deleteIcon.setIconSize(13);
                    deleteIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                    Pane pn = new Pane();

                    hbox.getChildren().addAll(pn, editButton, deleteButton);

//                    final JFXButton confirmDelete = new JFXButton("Confirm");

                    deleteButton.setOnAction(actionEvent -> {
                        confirmDelete(bankRemittance);
                    });

                    editButton.setOnAction(actionEvent -> {

                        editEntry(bankRemittance);
                    });

                    setGraphic(hbox);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });

        remittanceTable.getColumns().add(descriptionColumn);
        remittanceTable.getColumns().add(accountNoColumn);
        remittanceTable.getColumns().add(accountCodeColumn);
        remittanceTable.getColumns().add(checkNumberColumn);
        remittanceTable.getColumns().add(depositedDateColumn);
        remittanceTable.getColumns().add(amountColumn);
        remittanceTable.getColumns().add(actionCol);

        tableList = FXCollections.observableList(new ArrayList<BankRemittance>());

        remittanceTable.setItems(tableList);

    }

    private void showDailyTotals() {
        try {
            java.sql.Date date = java.sql.Date.valueOf(transactionDate.getValue());
            double orTotal=0;
            double arTotal=0;

            if(TransactionHeader.getBRTransactionCodeProperty().equals("BR"))
            {
                 orTotal = Utility.getTotalReceipt("OR", date);
                 arTotal = Utility.getTotalReceipt("AR", date);
            }
            else {
                 orTotal = Utility.getTotalReceipt("ORSub", date);
                 arTotal = Utility.getTotalReceipt("ARSub", date);

            }



            totalReceipt = orTotal+arTotal;

            double balancing = Math.abs(totalReceipt-totalCollection);
            //double balancing = totalReceipt-totalCollection;

            totalARAmount.setText(String.format("₱ %,.2f", arTotal));
            totalORAmount.setText(String.format("₱ %,.2f", orTotal));
            totalReceiptAmount.setText(String.format("₱ %,.2f", totalReceipt));



            if(totalReceipt>=totalCollection)
            {
                balancingFigure.setText(String.format("₱ %,.2f", balancing));

            }
            else {

                String txt=String.format("(%,.2f", balancing) +")";
                if(txt.trim().equals("(0.00)"))
                {
                    balancingFigure.setText("₱ 0.00") ;
                }
                else{
                    balancingFigure.setText("₱ "+txt) ;
                }


            }


        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    private void confirmDelete(BankRemittance bankRemittance) {
        if(Utility.checkPeriodIsLocked(transactionDate.getValue(), stackPane)) return;

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

    private void editEntry(BankRemittance bankRemittance) {
        if(Utility.checkPeriodIsLocked(transactionDate.getValue(), stackPane)) return;

        Utility.setSelectedObject(bankRemittance);
        modal = ModalBuilder.showModalFromXML(EditBankRemittance.class, "edit_bank_remittance.fxml", Utility.getStackPane());
    }

    private void removeEntry(BankRemittance bankRemittance) {
        if(Utility.checkPeriodIsLocked(transactionHeader.getPeriod(), stackPane)) return;

        try{
            TransactionDetails td = bankRemittance.getTransactionDetails();
            TransactionDetailsDAO.delete(td);
            TransactionDetailsDAO.syncDebit(td.getPeriod(),td.getTransactionNumber(),TransactionHeader.getBRTransactionCodeProperty());
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
        totalCollection = check+cash;
        showDailyTotals();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        remittanceTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        renderTable();
        remarksField.setEditable(false);
        Utility.setParentController(this);

        this.stackPane = Utility.getStackPane();

        totalCollection = 0;
    }

    @FXML
    public void onAddEntry() {
        if(Utility.checkPeriodIsLocked(transactionDate.getValue(), stackPane)) return;
        ModalBuilder.showModalFromXML(AddBankRemittance.class, "add_bank_remittance.fxml", Utility.getStackPane());
    }

    @FXML
    public void onBulkEntry() {
        if(Utility.checkPeriodIsLocked(transactionDate.getValue(), stackPane)) return;
        ModalBuilder.showModalFromXML(BrBulkEntry.class, "br_bulk_entry.fxml", Utility.getStackPane());
    }

    public void enableAddEntry() {

        try {
            LocalDate tdate = transactionDate.getValue();

            String dateStr = tdate.format(DateTimeFormatter.ofPattern("MMddyy"));

            remittanceNo.setText(dateStr);

            String transactionNumber = remittanceNo.getText();
            transactionHeader = TransactionHeaderDAO.get(transactionNumber, TransactionHeader.getBRTransactionCodeProperty());

            if(transactionHeader==null) {
                tableList.clear();
                remittanceTable.refresh();
                remarksField.setText(null);
                remarksField.setEditable(false);
                generateReportBtn.setDisable(true);
                computeTotals();
            }else {
                List<TransactionDetails> tds = TransactionDetailsDAO.get(transactionHeader.getPeriod(),dateStr, TransactionHeader.getBRTransactionCodeProperty());

                tableList.clear();

                for(TransactionDetails td: tds) {
                    if(td.getSequenceNumber()==999) continue;
                    BankRemittance br = new BankRemittance(td.getPeriod(), td.getOrDate(),null,td.getCheckNumber(), td.getDebit(), BankAccountDAO.get(td.getBankID()), td.getAccountCode());
                    br.setTransactionDetails(td);
                    br.setDepositedDate(td.getDepositedDate());
                    tableList.add(br);
                }
                remittanceTable.refresh();
                remarksField.setText(transactionHeader.getRemarks());
                remarksField.setEditable(true);
                generateReportBtn.setDisable(false);
                computeTotals();
            }

            addEntryBtn.setDisable(false);
            bulkEntryBtn.setDisable(false);
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
            transactionHeader.setTransactionCode(TransactionHeader.getBRTransactionCodeProperty());
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
        if(Utility.checkPeriodIsLocked(transactionDate.getValue(), stackPane)) return;

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

        if(o==null) {
            modal.close();
            return;
        }

        if(o instanceof BankRemittance){
            if( ((BankRemittance)o).getIsEdit() ) receiveEdit(o);
            else receiveOne(o);
            return;
        }

        if(o instanceof List) {
            receiveMultiple(o);
        }
    }

    public void receiveEdit(Object o) {

        if(o instanceof  BankRemittance) {
            //save changes to existing BR TransactionDetail
            BankRemittance br = (BankRemittance)o;
            TransactionDetails td = br.getTransactionDetails();
            try {
                TransactionDetailsDAO.updateBR(td, br);
                TransactionDetailsDAO.syncDebit(td.getPeriod(), td.getTransactionNumber(),transactionHeader.getTransactionCode());
                this.enableAddEntry();
                computeTotals();
            }catch(Exception ex) {
                ex.printStackTrace();
                AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }

        modal.close();
    }

    private void receiveMultiple(Object o) {
        try {
            if(transactionHeader == null) {
                createTransactionHeader();
            }else {
                TransactionHeaderDAO.updateTransaction(transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode(), transactionHeader.getPeriod());
            }

            ArrayList<BankRemittance> brs = (ArrayList<BankRemittance>) o;
            List<TransactionDetails> tds = new ArrayList<>();
            for(BankRemittance br: brs) {
                TransactionDetails td = new TransactionDetails();
                td.setTransactionCode(transactionHeader.getTransactionCode());
                td.setTransactionNumber(transactionHeader.getTransactionNumber());
                td.setPeriod(transactionHeader.getPeriod());
//                td.setOrDate(br.getOrDateFrom());
                td.setBankID(br.getBankAccount().getId());
                td.setAccountCode(br.getBankAccount().getAccountCode());
                td.setDebit(br.getAmount());
                td.setSequenceNumber(TransactionDetailsDAO.getNextSequenceNumber(td.getPeriod(), td.getTransactionNumber(),td.getTransactionCode()));
                td.setCheckNumber(br.getCheckNumber());
                td.setTransactionDate(transactionHeader.getTransactionDate());
                td.setDepositedDate(br.getDepositedDate());

                tableList.add(br);
                tds.add(td);
            }

            TransactionDetailsDAO.add(tds);
            TransactionDetailsDAO.syncDebit(transactionHeader.getPeriod(), transactionHeader.getTransactionNumber(),transactionHeader.getTransactionCode());

            computeTotals();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void receiveOne(Object o) {
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
//            td.setOrDate(br.getOrDateFrom());
            td.setBankID(br.getBankAccount().getId());
            td.setAccountCode(br.getBankAccount().getAccountCode());
            td.setDebit(br.getAmount());
            td.setSequenceNumber(TransactionDetailsDAO.getNextSequenceNumber(td.getPeriod(), td.getTransactionNumber(), td.getTransactionCode()));
            td.setCheckNumber(br.getCheckNumber());
            td.setTransactionDate(transactionHeader.getTransactionDate());
            td.setDepositedDate(br.getDepositedDate());

            TransactionDetailsDAO.add(td);
            br.setTransactionDetails(td);
            TransactionDetailsDAO.syncDebit(td.getPeriod(), td.getTransactionNumber(),transactionHeader.getTransactionCode());
            tableList.add((BankRemittance) o);
            computeTotals();
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    public void onGenerateReport() {
        //AlertDialogBuilder.messgeDialog("Coming Soon","This feature is yet to be implemented.",stackPane, AlertDialogBuilder.INFO_DIALOG);
        XSSFWorkbook book = new XSSFWorkbook();
        XSSFSheet sheet = book.createSheet();

        Font font = book.createFont();
        font.setBold(true);

        CellStyle dateHeaderStyle = book.createCellStyle();
        dateHeaderStyle.setDataFormat(book.getCreationHelper().createDataFormat().getFormat("MMM dd, yyyy"));
        dateHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        dateHeaderStyle.setBorderLeft(BorderStyle.HAIR);
        dateHeaderStyle.setBorderRight(BorderStyle.HAIR);
        dateHeaderStyle.setBorderTop(BorderStyle.HAIR);
        dateHeaderStyle.setBorderBottom(BorderStyle.HAIR);

        CellStyle stringHeaderStyle = book.createCellStyle();
        stringHeaderStyle.setAlignment(HorizontalAlignment.CENTER);
        stringHeaderStyle.setBorderLeft(BorderStyle.HAIR);
        stringHeaderStyle.setBorderRight(BorderStyle.HAIR);
        stringHeaderStyle.setBorderTop(BorderStyle.HAIR);
        stringHeaderStyle.setBorderBottom(BorderStyle.HAIR);
        stringHeaderStyle.setFont(font);

        CellStyle stringStyle = book.createCellStyle();
        stringStyle.setAlignment(HorizontalAlignment.LEFT);
        stringStyle.setBorderLeft(BorderStyle.HAIR);
        stringStyle.setBorderRight(BorderStyle.HAIR);
        stringStyle.setBorderTop(BorderStyle.HAIR);
        stringStyle.setBorderBottom(BorderStyle.HAIR);

        CellStyle doubleBoldStyle = book.createCellStyle();
        doubleBoldStyle.setAlignment(HorizontalAlignment.RIGHT);
        doubleBoldStyle.setBorderLeft(BorderStyle.HAIR);
        doubleBoldStyle.setBorderRight(BorderStyle.HAIR);
        doubleBoldStyle.setBorderTop(BorderStyle.HAIR);
        doubleBoldStyle.setBorderBottom(BorderStyle.HAIR);
        doubleBoldStyle.setFont(font);
        doubleBoldStyle.setDataFormat((short)4);

        CellStyle doubleStyle = book.createCellStyle();
        doubleStyle.setAlignment(HorizontalAlignment.RIGHT);
        doubleStyle.setBorderLeft(BorderStyle.HAIR);
        doubleStyle.setBorderRight(BorderStyle.HAIR);
        doubleStyle.setBorderTop(BorderStyle.HAIR);
        doubleStyle.setBorderBottom(BorderStyle.HAIR);
        doubleStyle.setDataFormat((short)4);

        sheet.createRow(0).createCell(0).setCellValue("Bank Remittance Report");
        sheet.setColumnWidth(0, 30*256);
        sheet.setColumnWidth(1, 20*256);
        sheet.setColumnWidth(2, 17*256);
        sheet.setColumnWidth(3, 2*256);
        sheet.setColumnWidth(4, 17*256);
        sheet.setColumnWidth(5, 17*256);

        createCells(sheet, 3, new Object[] {
                "22-275","ACCT.NO.","DATE","","", "AMOUNT"
        }, new CellStyle[] {
                stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle
        });

        createCells(sheet, 4, new Object[] {
                transactionHeader.getTransactionDate(), "","DEPOSITED","","",""
        }, new CellStyle[] {
                dateHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle
        });

        createCells(sheet, 5, new Object[] {
                "COLLECTION","","","","",new Double(totalCollection)
        }, new CellStyle[]{
                stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, stringHeaderStyle, doubleBoldStyle
        });

        int rowNum=6;
        for(BankRemittance br: tableList) {
            createCells(sheet, rowNum++, new Object[] {
                    br.getBankAccount().getBankDescription(),
                    br.getBankAccount().getBankAccountNumber(),
                    br.getDepositedDate(),
                    "","",
                    br.getAmount()
            }, new CellStyle[] {
                    stringStyle, stringStyle, dateHeaderStyle, stringStyle, stringStyle, doubleStyle
            });
        }

        createCells(sheet, rowNum+2, new Object[]{
                "","","","","",new Double(totalCollection)
        }, new CellStyle[]{
                stringStyle, stringStyle, stringStyle, stringStyle, stringStyle, doubleBoldStyle
        });

        FileChooser fs = new FileChooser();
        fs.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel File","*.xlsx"));

        try {
            File file = fs.showSaveDialog(null);

            FileOutputStream os = new FileOutputStream(file);

            book.write(os);

            os.close();

        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Update Error",ex.getMessage(),stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void createCells(XSSFSheet sheet, int rowNum, Object[] data, CellStyle[] styles) {
        XSSFRow row = sheet.createRow(rowNum);
        for(int i=0; i<data.length; i++) {
            XSSFCell cell = row.createCell(i);
            if(data[i] instanceof String) cell.setCellValue(String.valueOf(data[i]));
            if(data[i] instanceof LocalDate) cell.setCellValue((LocalDate)data[i]);
            if(data[i] instanceof Double) cell.setCellValue((Double)data[i]);
            cell.setCellStyle(styles[i]);
        }
    }

}
