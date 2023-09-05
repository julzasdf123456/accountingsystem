package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.PODAO;
import com.boheco1.dev.integratedaccountingsystem.dao.POItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;


import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

public class VoucherController extends MenuControllerHandler implements Initializable {

    @FXML
    private TableView cvTable;

    @FXML
    private TextField searchPO;

    @FXML
    private DatePicker date;

    @FXML
    private TextArea nature;

    @FXML
    private Label totalDebit, totalCredit;



    @FXML
    private JFXTextField payee;

    private ChartOfAccount selectedChartOfAccount;


    private PurchaseOrder purchaseOrder;
    private List<POItem> purchaseOrderItem;

    private ObservableList<TransactionDetails> cvDetails;

    private TransactionHeader cvHeader;

    private int sequence = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            date.setValue(Utility.serverDate());
        } catch (Exception e) {
            date.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }
        initTable();

    }


    private void initTable(){
        TableColumn<TransactionDetails, String> column1 = new TableColumn<>("Account Title");
        column1.setCellValueFactory(new PropertyValueFactory<>("particulars"));
        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        //column1.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setParticulars(e.getNewValue()));
        column1.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    TransactionDetails editingRow = t.getTableView().getItems().get(t.getTablePosition().getRow());
                    if(editingRow.isEditable()){
                        System.out.println(editingRow.isEditable());
                        editingRow.setParticulars(t.getNewValue());
                    }else {
                        editingRow.setParticulars(editingRow.getParticulars());
                    }
                    getTotal();
                });
        column1.setMinWidth(300);
        column1.setPrefWidth(300);
        column1.setEditable(true);
        column1.setStyle("-fx-alignment: top-left;");

        TableColumn<TransactionDetails, String> column6 = new TableColumn<>("Specification");
        column6.setCellValueFactory(new PropertyValueFactory<>("specification"));
        //column1.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setParticulars(e.getNewValue()));
        column6.setMinWidth(300);
        column6.setPrefWidth(300);
        column6.setEditable(true);
        column6.setStyle("-fx-alignment: top-left;");

        TableColumn<TransactionDetails, String> column2 = new TableColumn<>("Account Code");
        column2.setCellValueFactory(new PropertyValueFactory<>("accountCode"));
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        //column2.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setAccountCode(e.getNewValue()));
        //column2.setOnEditCommit(t -> t.getRowValue().setAccountCode(t.getNewValue()));
        column2.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())
                            .setAccountCode(t.getNewValue());
                    getTotal();
                });
        column2.setMinWidth(50);
        column2.setPrefWidth(50);
        column2.setEditable(true);
        column2.setStyle("-fx-alignment: top-center;");

        TableColumn<TransactionDetails, String> column3 = new TableColumn<>("Debit");
        column3.setCellValueFactory(new PropertyValueFactory<>("debitView"));
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        //column3.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setDebit(Double.parseDouble(e.getNewValue())));
        column3.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())
                            .setDebit(Double.parseDouble(t.getNewValue()));
                    getTotal();
                });
        column3.setMinWidth(20);
        column3.setPrefWidth(20);
        column3.setEditable(true);
        column3.setStyle("-fx-alignment: top-right;");

        TableColumn<TransactionDetails, String> column4 = new TableColumn<>("credit");
        column4.setCellValueFactory(new PropertyValueFactory<>("creditView"));
        column4.setCellFactory(TextFieldTableCell.forTableColumn());
        //column4.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setCredit(Double.parseDouble(e.getNewValue())));
        column4.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())
                            .setCredit(Double.parseDouble(t.getNewValue()));
                    getTotal();
                });
        column4.setMinWidth(20);
        column4.setPrefWidth(20);
        column4.setEditable(true);
        column4.setStyle("-fx-alignment: top-right;");

        TableColumn<TransactionDetails, String> column5 = new TableColumn<>("Amount");
        column5.setEditable(true);


        cvTable.getColumns().removeAll();
        cvTable.getColumns().add(column1);
        cvTable.getColumns().add(column6);
        cvTable.getColumns().add(column2);
        column5.getColumns().addAll(column3,column4);
        cvTable.getColumns().add(column5);
        cvTable.setEditable(true);


    }


    @FXML
    private void saveCV(ActionEvent event) {
        for(TransactionDetails td : cvDetails){
            if(td.getAccountCode() != null && !td.getAccountCode().isEmpty())
                System.out.println(td);
        }
        cvTable.refresh();
    }

    @FXML
    private void addBlankTableRow(ActionEvent event) {
        TransactionDetails cvItem = new TransactionDetails();
        cvItem.setTransactionNumber(purchaseOrder.getPoNo());
        cvItem.setTransactionCode(Utility.VOUCHER_TYPE);
        cvItem.setTransactionDate(date.getValue());
        cvItem.setSequenceNumber(sequence++);
        //cvItem.setAccountCode(accountCode);
        //cvItem.setDebit(i.getAmount());
        //cvItem.setCredit(cvCredit);
        cvItem.setParticulars("-");
        //cvItem.setSpecification("-");
        cvItem.setEditable(true);
        cvDetails.add(cvItem);
        getTotal();
    }

    @FXML
    private void getPOInfo(ActionEvent event) throws Exception {
        purchaseOrder = PODAO.get(searchPO.getText());
        purchaseOrderItem = POItemDAO.getItems(purchaseOrder);
        cvDetails = FXCollections.observableArrayList();
        this.cvTable.setItems(cvDetails);
        cvTable.refresh();
        if(purchaseOrder != null) {
            for(POItem i : purchaseOrderItem){
                TransactionDetails cvItem = new TransactionDetails();
                cvItem.setTransactionNumber(purchaseOrder.getPoNo());
                cvItem.setTransactionCode(Utility.VOUCHER_TYPE);
                cvItem.setTransactionDate(date.getValue());
                cvItem.setSequenceNumber(sequence++);
                //cvItem.setAccountCode(accountCode);
                cvItem.setDebit(i.getAmount());
                //cvItem.setCredit(cvCredit);
                cvItem.setParticulars(i.getDescription());
                cvItem.setSpecification(i.getDetails());
                cvItem.setEditable(false);
                cvDetails.add(cvItem);
                getTotal();
            }

            cvHeader = new TransactionHeader();
            cvHeader.setTransactionNumber(purchaseOrder.getPoNo());
            cvHeader.setTransactionCode("CV");
            cvHeader.setTransactionDate(date.getValue());
            cvHeader.setAmount(purchaseOrder.getAmount());
            cvHeader.setEnteredBy(ActiveUser.getUser().getUserName());
        }
    }


    private void getTotal(){
        double totCredit = 0;
        double totDebit = 0;
        for(TransactionDetails dt : cvDetails){
            double credit = dt.getCredit();
            double debit = dt.getDebit();

            totCredit += credit;
            totDebit += debit;
        }

        totalDebit.setText("Debit: "+ Utility.formatDecimal(totDebit));
        totalCredit.setText("Credit: "+ Utility.formatDecimal(totCredit));
        cvTable.refresh();
    }

}
