package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.ChartOfAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.PODAO;
import com.boheco1.dev.integratedaccountingsystem.dao.POItemDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;


import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class VoucherController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXToggleButton voucherType;

    @FXML
    private TableView voucherItemTable;

    @FXML
    private TextField searchAccount, searchPO;


    @FXML
    private TextArea nature;

    @FXML
    private Label totalDebit, totalCredit;



    @FXML
    private JFXTextField payee;

    private ChartOfAccount selectedChartOfAccount;


    private PurchaseOrder purchaseOrder;
    private List<POItem> purchaseOrderItem;

    private ObservableList<TransactionDetails> voucherItem;

    private TransactionHeader transactionHeader;

    private String transactionCode;

    private int sequence = 1;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindChartOfAccountAutocomplete(searchAccount);
        initTable();

    }


    private void initTable() {
        TableColumn<TransactionDetails, String> column1 = new TableColumn<>("Account Title");
        column1.setCellValueFactory(new PropertyValueFactory<>("particulars"));
        column1.setCellFactory(TextFieldTableCell.forTableColumn());
        //column1.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setParticulars(e.getNewValue()));
        /*column1.setOnEditCommit(
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

        column1.setEditable(true);*/
        //column1.setMinWidth(300);
        //column1.setPrefWidth(300);

        column1.setStyle("-fx-alignment: top-left;");

        TableColumn<TransactionDetails, String> column6 = new TableColumn<>("Specification");
        column6.setCellValueFactory(new PropertyValueFactory<>("specification"));
        //column1.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setParticulars(e.getNewValue()));
        column6.setMinWidth(100);
        column6.setPrefWidth(100);
        column6.setEditable(true);
        column6.setStyle("-fx-alignment: top-left;");

        TableColumn<TransactionDetails, String> column2 = new TableColumn<>("Account Code");
        column2.setCellValueFactory(new PropertyValueFactory<>("accountCode"));
        column2.setCellFactory(TextFieldTableCell.forTableColumn());
        //column2.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setAccountCode(e.getNewValue()));
        //column2.setOnEditCommit(t -> t.getRowValue().setAccountCode(t.getNewValue()));
        /*column2.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    t.getTableView().getItems().get(
                                    t.getTablePosition().getRow())
                            .setAccountCode(t.getNewValue());
                    getTotal();
                });
        column2.setEditable(true);*/
        column2.setMinWidth(150);
        column2.setPrefWidth(150);
        column2.setMaxWidth(150);

        column2.setStyle("-fx-alignment: top-center;");

        TableColumn<TransactionDetails, String> column3 = new TableColumn<>("Debit");
        column3.setCellValueFactory(new PropertyValueFactory<>("debitView"));
        column3.setCellFactory(TextFieldTableCell.forTableColumn());
        //column3.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setDebit(Double.parseDouble(e.getNewValue())));

            column3.setOnEditCommit(

                    (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                        try {
                            t.getTableView().getItems().get(
                                            t.getTablePosition().getRow())

                                    .setDebit(Double.parseDouble(t.getNewValue()));

                            getTotal();
                        voucherItemTable.requestFocus();
                        }catch (Exception e){

                        }
                    });

        column3.setMinWidth(150);
        column3.setPrefWidth(150);
        column3.setMaxWidth(150);
        column3.setEditable(true);
        column3.setStyle("-fx-alignment: top-right;");

        TableColumn<TransactionDetails, String> column4 = new TableColumn<>("Credit");
        column4.setCellValueFactory(new PropertyValueFactory<>("creditView"));
        column4.setCellFactory(TextFieldTableCell.forTableColumn());
        //column4.setOnEditCommit(e->e.getTableView().getItems().get(e.getTablePosition().getRow()).setCredit(Double.parseDouble(e.getNewValue())));
        column4.setOnEditCommit(
                (TableColumn.CellEditEvent<TransactionDetails, String> t) -> {
                    try {
                        t.getTableView().getItems().get(
                                        t.getTablePosition().getRow())
                                .setCredit(Double.parseDouble(t.getNewValue()));
                        getTotal();
                        voucherItemTable.requestFocus();
                    }catch (Exception e){

                    }
                });
        column4.setMinWidth(150);
        column4.setPrefWidth(150);
        column4.setMaxWidth(150);
        column4.setEditable(true);
        column4.setStyle("-fx-alignment: top-right;");

        TableColumn<TransactionDetails, String> column5 = new TableColumn<>("Amount");
        column5.setEditable(true);


        voucherItemTable.getColumns().removeAll();
        voucherItemTable.getColumns().add(column1);
        //cvTable.getColumns().add(column6);
        voucherItemTable.getColumns().add(column2);
        column5.getColumns().addAll(column3,column4);
        voucherItemTable.getColumns().add(column5);
        voucherItemTable.setEditable(true);

        voucherItem = FXCollections.observableArrayList();
        this.voucherItemTable.setItems(voucherItem);

    }


    @FXML
    private void saveCV(ActionEvent event) {
        for(TransactionDetails td : voucherItem){
            if(td.getAccountCode() != null && !td.getAccountCode().isEmpty())
                System.out.println(td);
        }
        voucherItemTable.refresh();
    }

    @FXML
    private void getPOInfo(ActionEvent event) throws Exception {
        purchaseOrder = PODAO.get(searchPO.getText());
        purchaseOrderItem = POItemDAO.getItems(purchaseOrder);

        voucherItemTable.refresh();
        if(purchaseOrder != null) {
            for(POItem i : purchaseOrderItem){
                TransactionDetails cvItem = new TransactionDetails();
                cvItem.setTransactionNumber(purchaseOrder.getPoNo());
                cvItem.setTransactionCode(Utility.VOUCHER_TYPE);
                cvItem.setTransactionDate(Utility.serverDate());
                cvItem.setSequenceNumber(sequence++);
                //cvItem.setAccountCode(accountCode);
                cvItem.setDebit(i.getAmount());
                //cvItem.setCredit(cvCredit);
                cvItem.setParticulars(i.getDescription());
                cvItem.setSpecification(i.getDetails());
                cvItem.setEditable(false);
                voucherItem.add(cvItem);
                getTotal();
            }

            transactionHeader = new TransactionHeader();
            transactionHeader.setTransactionNumber(purchaseOrder.getPoNo());
            transactionHeader.setTransactionCode("CV");
            transactionHeader.setTransactionDate(Utility.serverDate());
            transactionHeader.setAmount(purchaseOrder.getAmount());
            transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
        }
    }

    @FXML
    private void selectVoucherType(ActionEvent event) {
        if(voucherType.isSelected())
            voucherType.setText("JV");
        else
            voucherType.setText("CV");

        transactionCode = voucherType.getText();
    }


    private void getTotal(){
        double totCredit = 0;
        double totDebit = 0;
        for(TransactionDetails dt : voucherItem){
            double credit = dt.getCredit();
            double debit = dt.getDebit();

            totCredit += credit;
            totDebit += debit;
        }

        totalDebit.setText("Debit: "+ Utility.formatDecimal(totDebit));
        totalCredit.setText("Credit: "+ Utility.formatDecimal(totCredit));
        voucherItemTable.refresh();
    }

    private void bindChartOfAccountAutocomplete(TextField textField){
        AutoCompletionBinding<ChartOfAccount> chartSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<ChartOfAccount> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 0){
                        try {
                            list = ChartOfAccountDAO.get(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(ChartOfAccount object) {
                        return object.getCode() +"-"+object.getDescription();
                    }

                    @Override
                    public ChartOfAccount fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
        chartSuggest.setMinWidth(500);
        chartSuggest.setPrefWidth(500);
        chartSuggest.setMaxWidth(500);
        //This will set the actions once the user clicks an item from the popupmenu.
        chartSuggest.setOnAutoCompleted(event -> {
            ChartOfAccount chartOfAccount = event.getCompletion();
            textField.setText(chartOfAccount.getTitle());
            textField.positionCaret(textField.getText().length());
            TransactionDetails account = new TransactionDetails();
            account.setTransactionCode(Utility.VOUCHER_TYPE);
            try {
                account.setTransactionDate(Utility.serverDate());
            } catch (Exception e) {
                account.setTransactionDate(LocalDate.now());
                throw new RuntimeException(e);
            }
            account.setSequenceNumber(sequence++);
            account.setAccountCode(chartOfAccount.getCode());
            //cvItem.setDebit(i.getAmount());
            //cvItem.setCredit(cvCredit);
            account.setParticulars(chartOfAccount.getTitle());
            //cvItem.setSpecification("-");
            account.setEditable(true);
            voucherItem.add(account);
            voucherItemTable.refresh();
        });
    }
}
