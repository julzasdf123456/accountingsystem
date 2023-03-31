package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class BulkOrController extends MenuControllerHandler implements Initializable {
    @FXML
    private StackPane stackPane;
    @FXML
    private Label rowCount,totalAmount;

    @FXML
    private JFXTextField uploadedFile;
    @FXML
    private TableView tableView;
    @FXML
    private JFXComboBox<String> workSheet;
    private File selectedFile = null;
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private ObservableList<IEOP> ieopObservableList;
    private ObservableList<NIHE> niheObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        niheObservableList =  FXCollections.observableArrayList();
        ieopObservableList =  FXCollections.observableArrayList();
        samplePrintOR();
    }

    private void resetController(){
        ieopObservableList.clear();
        niheObservableList.clear();
        tableView.getItems().clear();
        workSheet.getItems().clear();
        uploadedFile.setText("");
    }

    @FXML
    private void browseFile(ActionEvent event) {
        Stage stage = (Stage) stackPane.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();

        if(!Utility.FILE_PATH.isEmpty())
            fileChooser.setInitialDirectory(new File(Utility.FILE_PATH));

        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Excel Files", "*.xls")
        );

        selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            Task<Void> task = new Task<>() {
                @Override
                protected Void call() {
                    try {
                        resetController();
                        uploadedFile.setText(selectedFile.getName());
                        Utility.FILE_PATH = selectedFile.getParent();

                        FileInputStream excelFile = new FileInputStream(new File(selectedFile.getAbsolutePath()));
                        wb = new HSSFWorkbook(excelFile);
                        for(int i=0;i<wb.getNumberOfSheets();i++){
                            workSheet.getItems().add(wb.getSheetName(i));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        AlertDialogBuilder.messgeDialog("System Error", "Error encounter while loading excel file: "+e.getMessage(),
                                Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }
                    return null;
                }
            };
            task.setOnRunning(wse -> {
                dialog.show();
            });
            task.setOnSucceeded(wse -> {
                dialog.close();
            });
            task.setOnFailed(wse -> {
                dialog.close();
            });
            new Thread(task).start();
        }
    }

    @FXML
    private void printOR(ActionEvent event) throws Exception {
        if(uploadedFile.getText().toLowerCase().contains("iemop")) {
            saveTransactionInfoForIEOP();
        }else if(uploadedFile.getText().toLowerCase().contains("nihe")){

        }
    }

    @FXML
    private void displayTable(ActionEvent event) {
        if(uploadedFile.getText().toLowerCase().contains("iemop")) {
            ieopObservableList.clear();
            tableView.getItems().clear();
            initTable_IEMOP();
        }else if(uploadedFile.getText().toLowerCase().contains("nihe")){
            niheObservableList.clear();
            tableView.getItems().clear();
            initTable_NIHE();
        }
    }

    private void initTable_IEMOP(){
        TableColumn<ORItemSummary, String> column1 = new TableColumn<>("OR Number");
        column1.setStyle("-fx-alignment: center;");
        column1.setCellValueFactory(new PropertyValueFactory<>("orNumber"));

        TableColumn<ORItemSummary, String> column2 = new TableColumn<>("Consumer Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("consumerName"));

        TableColumn<ORItemSummary, String> column3 = new TableColumn<>("Consumer Address");
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));

        TableColumn<ORItemSummary, String> column4 = new TableColumn<>("OR Date");
        column4.setCellValueFactory(new PropertyValueFactory<>("orDate"));

        TableColumn<ORItemSummary, Double> column5 = new TableColumn<>("Sales");
        column5.setStyle("-fx-alignment: center-right;");
        column5.setCellValueFactory(new PropertyValueFactory<>("salesView"));

        TableColumn<ORItemSummary, String> column6 = new TableColumn<>("WTAX");
        column6.setStyle("-fx-alignment: center-right;");
        column6.setCellValueFactory(new PropertyValueFactory<>("wtaxView"));

        TableColumn<ORItemSummary, String> column7 = new TableColumn<>("Net Cash");
        column7.setStyle("-fx-alignment: center-right;");
        column7.setCellValueFactory(new PropertyValueFactory<>("netCashView"));

        tableView.getColumns().clear();
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);
        tableView.getColumns().add(column5);
        tableView.getColumns().add(column6);
        tableView.getColumns().add(column7);

        sheet = wb.getSheetAt(workSheet.getSelectionModel().getSelectedIndex());
        Row rowData;
        double total =0;
        int rowRange = sheet.getLastRowNum();
        rowCount.setText("Row Count: "+rowRange);
        for(int row=1; row<=rowRange;row++){
            rowData = sheet.getRow(row);
            if(rowData==null)continue;
            if(rowData.getCell(0)==null) continue;
            if(rowData.getCell(0).getNumericCellValue()==0) continue;
            IEOP ieop = new IEOP(
                    String.format("%.0f",rowData.getCell(0).getNumericCellValue()),
                    rowData.getCell(1).getStringCellValue(),
                    rowData.getCell(2).getStringCellValue(),
                    rowData.getCell(3).getLocalDateTimeCellValue(),
                    rowData.getCell(4).getNumericCellValue(),
                    rowData.getCell(5).getNumericCellValue(),
                    rowData.getCell(6).getNumericCellValue());
            total+=ieop.getNetCash();
            ieopObservableList.add(ieop);
        }
        totalAmount.setText("Total: "+Utility.formatDecimal(total));
        tableView.setItems(ieopObservableList);
    }
    private void initTable_NIHE(){
        TableColumn<ORItemSummary, String> column1 = new TableColumn<>("OR Number");
        column1.setStyle("-fx-alignment: center;");
        column1.setCellValueFactory(new PropertyValueFactory<>("orNumber"));

        TableColumn<ORItemSummary, String> column2 = new TableColumn<>("Consumer Name");
        column2.setCellValueFactory(new PropertyValueFactory<>("consumerName"));

        TableColumn<ORItemSummary, String> column3 = new TableColumn<>("Consumer Address");
        column3.setCellValueFactory(new PropertyValueFactory<>("consumerAddress"));

        TableColumn<ORItemSummary, String> column4 = new TableColumn<>("OR Date");
        column4.setCellValueFactory(new PropertyValueFactory<>("orDate"));

        TableColumn<ORItemSummary, Double> column5 = new TableColumn<>("Amount");
        column5.setStyle("-fx-alignment: center-right;");
        column5.setCellValueFactory(new PropertyValueFactory<>("amountView"));

        tableView.getColumns().clear();
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);
        tableView.getColumns().add(column5);

        sheet = wb.getSheetAt(workSheet.getSelectionModel().getSelectedIndex());
        Row rowData;
        double total=0;
        int rowRange = sheet.getLastRowNum();
        rowCount.setText("Row Count: "+rowRange);
        for(int row=1; row<=rowRange;row++){
            rowData = sheet.getRow(row);
            if(rowData==null)continue;
            if(rowData.getCell(0)==null) continue;
            if(rowData.getCell(0).getNumericCellValue()==0) continue;
            NIHE nihe = new NIHE(
                    String.format("%.0f",rowData.getCell(0).getNumericCellValue()),
                    rowData.getCell(1).getStringCellValue(),
                    rowData.getCell(2).getStringCellValue(),
                    rowData.getCell(3).getLocalDateTimeCellValue(),
                    rowData.getCell(4).getNumericCellValue());
            total+=nihe.getAmount();
            niheObservableList.add(nihe);
        }
        totalAmount.setText("Total: "+Utility.formatDecimal(total));
        tableView.setItems(niheObservableList);
    }

    private void saveTransactionInfoForIEOP() throws Exception {

        List<BatchTransactionInfo> batchTransactionInfo = new ArrayList<>();
        for(IEOP ieop : ieopObservableList){
            int month = ieop.getOrDate().getMonthValue();
            int year = ieop.getOrDate().getYear();
            LocalDate period = LocalDate.of(year, month, 1);

            TransactionHeader transactionHeader= new TransactionHeader();
            transactionHeader.setPeriod(period);
            transactionHeader.setTransactionNumber(ieop.getOrNumber());
            transactionHeader.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
            transactionHeader.setOffice(Utility.OFFICE_PREFIX);
            transactionHeader.setSource("iemop");
            transactionHeader.setParticulars(ieop.getConsumerName());
            transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
            transactionHeader.setAccountID(ActiveUser.getUser().getId());
            transactionHeader.setAmount(ieop.getNetCash());
            transactionHeader.setTransactionDate(Utility.serverDate());
            transactionHeader.setDateEntered(LocalDateTime.now());

            TransactionDetails transactionDetailsSales = new TransactionDetails();
            transactionDetailsSales.setPeriod(period);
            transactionDetailsSales.setTransactionNumber(ieop.getOrNumber());
            transactionDetailsSales.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
            transactionDetailsSales.setTransactionDate(Utility.serverDate());
            transactionDetailsSales.setOrDate(ieop.getOrDate());
            transactionDetailsSales.setParticulars("sales");
            transactionDetailsSales.setSequenceNumber(1);
            if(ieop.getSales() > 0)
                transactionDetailsSales.setCredit(ieop.getSales());
            else
                transactionDetailsSales.setDebit(ieop.getSales());

            TransactionDetails transactionDetailsWTAX = new TransactionDetails();
            transactionDetailsWTAX.setPeriod(period);
            transactionDetailsWTAX.setTransactionNumber(ieop.getOrNumber());
            transactionDetailsWTAX.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
            transactionDetailsWTAX.setTransactionDate(Utility.serverDate());
            transactionDetailsWTAX.setOrDate(ieop.getOrDate());
            transactionDetailsWTAX.setParticulars("wtax");
            transactionDetailsWTAX.setSequenceNumber(2);
            if(ieop.getWtax() > 0)
                transactionDetailsWTAX.setCredit(ieop.getWtax());
            else
                transactionDetailsWTAX.setDebit(ieop.getWtax());

            BatchTransactionInfo batch = new BatchTransactionInfo();
            batch.setTransactionHeader(transactionHeader);
            batch.add(transactionDetailsSales);
            batch.add(transactionDetailsWTAX);

            batchTransactionInfo.add(batch);
        }

        JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException, ClassNotFoundException {
                TransactionHeaderDetailDAO.save(batchTransactionInfo);
                return null;
            }
        };

        task.setOnRunning(wse -> {
            dialog.show();
        });

        task.setOnSucceeded(wse -> {
            dialog.close();
            if(Utility.ERROR_MSG == null){
                AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
            }else {
                AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+Utility.ERROR_MSG,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
        });

        task.setOnFailed(wse -> {
            dialog.close();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+Utility.ERROR_MSG,
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        });

        new Thread(task).start();

    }

    private void samplePrintOR(){
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                );
                fileChooser.setInitialFileName(uploadedFile.getText()+"_OR_.pdf");
                File selectedFile = fileChooser.showSaveDialog(stage);
                if (selectedFile != null) {
                    float[] columns = {2.3f,1f,.5f,2.2f,1f};
                    PrintPDF pdf = new PrintPDF(selectedFile, columns);

                    for(int x=1;x<500;x++)
                        pdf.createCell(" ", 2, 9, Font.NORMAL, Element.ALIGN_CENTER);

                    pdf.generateForOR();
                }


            }catch (Exception e){
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }
}
