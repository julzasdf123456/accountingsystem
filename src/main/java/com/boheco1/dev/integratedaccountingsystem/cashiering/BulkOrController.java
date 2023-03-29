package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.IEOP;
import com.boheco1.dev.integratedaccountingsystem.objects.NIHE;
import com.boheco1.dev.integratedaccountingsystem.objects.ORItemSummary;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
            try {
                resetController();
                this.uploadedFile.setText(selectedFile.getName());
                Utility.FILE_PATH = selectedFile.getParent();

                FileInputStream excelFile = new FileInputStream(new File(selectedFile.getAbsolutePath()));
                wb = new HSSFWorkbook(excelFile);
                for(int i=0;i<wb.getNumberOfSheets();i++){
                    workSheet.getItems().add(wb.getSheetName(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void printOR(ActionEvent event) {

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
}
