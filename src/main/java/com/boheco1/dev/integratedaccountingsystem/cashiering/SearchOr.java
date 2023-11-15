package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.CashierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class SearchOr extends MenuControllerHandler implements Initializable, ObjectTransaction  {

    @FXML
    private DatePicker transactionDate, searchDate;

    @FXML
    private JFXComboBox<String> transCode;

    @FXML
    private JFXTextField orNumber;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField address;

    @FXML
    private TableView orTable, transHeaderTable, transDetailsTable;

    @FXML
    private Label totalAmount;

    @FXML
    private PieChart pieChart;

    private ObservableList<PieChart.Data> pieChartData;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setParentController(this);
        initTable();
        transCode.getItems().add("OR");
        transCode.getItems().add("ORSub");
        try {

            setPieChartData(countORTransactionType());
            pieChart.setStartAngle(90);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.transHeaderTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {

                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    TransactionHeader transactionHeader = (TransactionHeader) row.getItem();
                    List<TransactionDetails> transactionDetails = null;
                    try {
                        transactionDetails = TransactionDetailsDAO.get(transactionHeader.getTransactionNumber(),transactionHeader.getTransactionCode(), transactionHeader.getTransactionDate());
                        ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetails);
                        transDetailsTable.setItems(result);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return row ;
        });

    }

    private  TransactionHeader transactionHeader;
    private List<TransactionDetails> transactionDetailsList;
    @FXML
    void searchOR(ActionEvent event) throws Exception {
        String searchOr = orNumber.getText();//temporary store search string before reset and clear all field
        if(searchOr.isEmpty() || transactionDate.getValue()==null || transCode.getSelectionModel().isEmpty())
            return;


        orNumber.setText(searchOr);
        transactionHeader = TransactionHeaderDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());
        if(transactionHeader == null){
            AlertDialogBuilder.messgeDialog("System Message", "Transaction info not found.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            transactionDetailsList = TransactionDetailsDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());

            ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetailsList);
            orTable.setItems(result);
            name.setText(transactionHeader.getName());
            address.setText(transactionHeader.getAddress());
            totalAmount.setText("Total Amount: "+Utility.formatDecimal(transactionHeader.getAmount()));
        }

    }


    @FXML
    private void reprint(ActionEvent event) throws Exception {

        ObservableList<ORItemSummary> orItemSummaries = FXCollections.observableArrayList();;
        for(TransactionDetails td : transactionDetailsList){
            if(!td.getParticulars().equals("Grand Total")){
                ORItemSummary os = new ORItemSummary(td.getAccountCode(),td.getParticulars(),td.getAmount());
                orItemSummaries.add(os);
            }
        }

        ORContent orContent = new ORContent(null, transactionHeader, transactionDetailsList, true);
        orContent.setAddress(address.getText());
        orContent.setDate(transactionDate.getValue());
        orContent.setOrNumber(orNumber.getText());
        orContent.setIssuedTo(name.getText());
        orContent.setTellerCollection(orItemSummaries);
        EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
        orContent.setIssuedBy(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
        orContent.setTotal(transactionHeader.getAmount());
        Utility.setOrContent(orContent);
        ModalBuilder.showModalFromXMLNoClose(ORLayoutController.class, "../cashiering/orLayout.fxml", Utility.getStackPane());
    }



    @FXML
    private void filterDate(ActionEvent event) throws Exception {
        setPieChartData(countORTransactionType());
        setTransTableHeaderData();

    }

    private void setPieChartData(int[] counter){
        pieChartData =
                FXCollections.observableArrayList(
                        new PieChart.Data("System Generated: "+counter[0], counter[0]),
                        new PieChart.Data("Manual Entry: "+counter[1], counter[1]));
        pieChart.setData(pieChartData);
    }

    private void setTransTableHeaderData() throws Exception {
        if(searchDate.getValue()==null) return;

        List<TransactionHeader> transactionHeader = TransactionHeaderDAO.getLogTypeAndDate(searchDate.getValue(), Utility.MANUAL_ENTRY);
        if(transactionHeader == null){
            AlertDialogBuilder.messgeDialog("System Message", "Transaction info not found.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            ObservableList<TransactionHeader> result = FXCollections.observableArrayList(transactionHeader);
            transHeaderTable.setItems(result);
            transHeaderTable.refresh();
        }
    }

    private int[] countORTransactionType() throws SQLException, ClassNotFoundException {
        if(searchDate.getValue() == null){
            return CashierDAO.countORTransactionType();
        }else{
            int month = searchDate.getValue().getMonthValue();
            int day = searchDate.getValue().getDayOfMonth();
            int year = searchDate.getValue().getYear();
            return CashierDAO.countORTransactionType(year, month, day);
        }
    }

    private void initTable(){

        TableColumn<TransactionDetails, String> orTablecolumn0= new TableColumn<>("Account Code");
        orTablecolumn0.setMinWidth(180);
        orTablecolumn0.setStyle("-fx-alignment: center-left;");
        orTablecolumn0.setCellValueFactory(new PropertyValueFactory<>("accountCode"));

        TableColumn<TransactionDetails, String> orTablecolumn1 = new TableColumn<>("Item Description");
        orTablecolumn1.setMinWidth(350);
        orTablecolumn1.setStyle("-fx-alignment: center-left;");
        orTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particularsLabel"));

        TableColumn<TransactionDetails, String> orTablecolumn2 = new TableColumn<>("Total Amount");
        orTablecolumn2.setMinWidth(180);
        orTablecolumn2.setStyle("-fx-alignment: center-right;");
        orTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmountView()));

        this.orTable.getColumns().add(orTablecolumn0);
        this.orTable.getColumns().add(orTablecolumn1);
        this.orTable.getColumns().add(orTablecolumn2);


        TableColumn<TransactionHeader, String> transHeaderTableColumn1 = new TableColumn<>("O.R.");
        transHeaderTableColumn1.setMinWidth(100);
        transHeaderTableColumn1.setMaxWidth(100);
        transHeaderTableColumn1.setPrefWidth(100);
        transHeaderTableColumn1.setStyle("-fx-alignment: center-left;");
        transHeaderTableColumn1.setCellValueFactory(new PropertyValueFactory<>("transactionNumber"));

        TableColumn<TransactionHeader, String> transHeaderTableColumn2 = new TableColumn<>("Entered By");
        transHeaderTableColumn2.setMinWidth(100);
        transHeaderTableColumn2.setMaxWidth(100);
        transHeaderTableColumn2.setPrefWidth(100);
        transHeaderTableColumn2.setStyle("-fx-alignment: center-left;");
        transHeaderTableColumn2.setCellValueFactory(new PropertyValueFactory<>("enteredBy"));

        TableColumn<TransactionHeader, String> transHeaderTableColumn3 = new TableColumn<>("Name");
        transHeaderTableColumn3.setStyle("-fx-alignment: center-left;");
        transHeaderTableColumn3.setCellValueFactory(new PropertyValueFactory<>("name"));

        this.transHeaderTable.getColumns().add(transHeaderTableColumn1);
        this.transHeaderTable.getColumns().add(transHeaderTableColumn2);
        this.transHeaderTable.getColumns().add(transHeaderTableColumn3);


        TableColumn<TransactionDetails, String> transDetailsTableColumn0= new TableColumn<>("Account Code");
        transDetailsTableColumn0.setMinWidth(180);
        transDetailsTableColumn0.setStyle("-fx-alignment: center-left;");
        transDetailsTableColumn0.setCellValueFactory(new PropertyValueFactory<>("accountCode"));

        TableColumn<TransactionDetails, String>transDetailsTableColumn1 = new TableColumn<>("Item Description");
        transDetailsTableColumn1.setMinWidth(350);
        transDetailsTableColumn1.setStyle("-fx-alignment: center-left;");
        transDetailsTableColumn1.setCellValueFactory(new PropertyValueFactory<>("particularsLabel"));

        TableColumn<TransactionDetails, String> transDetailsTableColumn2 = new TableColumn<>("Total Amount");
        transDetailsTableColumn2.setMinWidth(180);
        transDetailsTableColumn2.setStyle("-fx-alignment: center-right;");
        transDetailsTableColumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmountView()));


        this.transDetailsTable.getColumns().add(transDetailsTableColumn0);
        this.transDetailsTable.getColumns().add(transDetailsTableColumn1);
        this.transDetailsTable.getColumns().add(transDetailsTableColumn2);
    }

    @Override
    public void receive(Object o) {
        if (o instanceof Boolean) {
            boolean b = (Boolean) o;
            if(!b)
                System.out.println("Re-print error");

            ModalBuilder.MODAL_CLOSE();
        }
    }
}
