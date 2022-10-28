package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.BillDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CRMDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.boheco1.dev.integratedaccountingsystem.tellering.PowerBillsPaymentController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;


import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class CashierController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXButton print_btn;

    @FXML
    private JFXTextField orNmber, name, address, purpose, source;

    @FXML
    private JFXTextArea remarks;

    @FXML
    private JFXComboBox office;

    @FXML
    private DatePicker date;

    @FXML
    private Label total;

    @FXML
    private TableView paymentTable;


    private CRMQueue consumerInfo = null;
    private Teller tellerInfo = null;
    private CRMQueue crmQueue;
    private List<CRMDetails> crmDetails;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setParentController(this);
        date.setValue(LocalDate.now());
    }

    @FXML
    private void print(ActionEvent event) {
        regularTransaction();
    }

    @FXML
    private void search(ActionEvent event) {
        ModalBuilderForWareHouse.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    @Override
    public void receive(Object o) {
        this.paymentTable.getColumns().clear();
        total.setText("Total: 0");
        if (o instanceof CRMQueue) {
            this.consumerInfo = (CRMQueue) o;
            this.name.setText(consumerInfo.getConsumerName());
            this.address.setText(consumerInfo.getConsumerAddress());
            this.purpose.setText(consumerInfo.getTransactionPurpose());
            setUpPaymentTable(consumerInfo);
        }else if (o instanceof Teller) {
            this.tellerInfo = (Teller) o;
            this.name.setText(tellerInfo.getName());
            this.address.setText(tellerInfo.getAddress());
            this.purpose.setText(tellerInfo.getPhone());
            setUpPaymentTable(tellerInfo);
        }
    }

    private void setUpPaymentTable(Object o) {
        paymentTable.getColumns().clear();
        if(o instanceof CRMQueue){
            CRMQueue crmQueue = (CRMQueue) o;

            TableColumn<CRMDetails, String> column1 = new TableColumn<>("Particulars");
            column1.setCellValueFactory(new PropertyValueFactory<>("particulars"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMDetails, String> column2 = new TableColumn<>("Amount");
            column2.setMinWidth(200);
            column2.setMaxWidth(200);
            column2.setPrefWidth(200);
            //column3.setCellValueFactory(new PropertyValueFactory<>("total"));
            Callback<TableColumn<CRMDetails, String>, TableCell<CRMDetails, String>> qtycellFactory
                    = //
                    new Callback<TableColumn<CRMDetails, String>, TableCell<CRMDetails, String>>() {
                        @Override
                        public TableCell call(final TableColumn<CRMDetails, String> param) {
                            final TableCell<CRMDetails, String> cell = new TableCell<CRMDetails, String>() {
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        CRMDetails crmDetails = getTableView().getItems().get(getIndex());
                                        setGraphic(null);
                                        setText(Utility.formatDecimal(crmDetails.getTotal()));
                                    }
                                }
                            };
                            return cell;
                        }
                    };

            column2.setCellFactory(qtycellFactory);
            column2.setStyle("-fx-alignment: center-right;");

            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);

            ObservableList<CRMDetails> result = null;
            List<CRMDetails> crmDetails = null;
            try {
                crmDetails = CRMDAO.getConsumerTransaction(crmQueue.getId());
                result = FXCollections.observableArrayList(crmDetails);
                this.paymentTable.setItems(result);
                double calculate = 0;
                for (CRMDetails c : result){
                    calculate+=c.getTotal();
                }
                this.total.setText("Total: "+String.format("%,.2f",calculate));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.crmQueue = crmQueue;
            this.crmDetails = crmDetails;
        }if(o instanceof Teller){
            Teller teller = (Teller)  o;
            HashMap<String, List<ItemSummary>> breakdown = teller.getDCRBreakDown();
            ObservableList<ItemSummary> result = FXCollections.observableArrayList(breakdown.get("Breakdown"));
            this.paymentTable.setItems(result);

            List<ItemSummary> misc = breakdown.get("Misc");
            total.setText("Total: "+Utility.formatDecimal(misc.get(1).getTotal()));

            TableColumn<ItemSummary, String> column1 = new TableColumn<>("Item Description");
            column1.setCellValueFactory(new PropertyValueFactory<>("description"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<ItemSummary, String> column2 = new TableColumn<>("Total Amount");
            column2.setMinWidth(200);
            column2.setMaxWidth(200);
            column2.setPrefWidth(200);
            column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
            column2.setStyle("-fx-alignment: center-right;");


            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
        }

        this.paymentTable.setPlaceholder(new Label("No consumer records was searched."));
    }

    private void regularTransaction(){
        TransactionHeader transactionHeader= new TransactionHeader();
        int month = date.getValue().getMonthValue();
        int year = date.getValue().getYear();

        LocalDate period = LocalDate.of(year, month, 1);

        transactionHeader.setPeriod(period);
        transactionHeader.setTransactionNumber(orNmber.getText());
        transactionHeader.setTransactionCode("ORMain");
        transactionHeader.setOffice("Office_X");
        transactionHeader.setSource("Consumer_ID");
        //transactionHeader.setAccountID("For Teller");
        //transactionHeader.setParticulars("N/A");
        transactionHeader.setTransactionDate(date.getValue());
        transactionHeader.setRemarks(remarks.getText());
        //transactionHeader.setBank("N/A");
        //transactionHeader.setReferenceNo("N/A");
        //transactionHeader.setAmount(0);
        transactionHeader.setEnteredBy(ActiveUser.getUser().getId());
        transactionHeader.setDateEntered(LocalDateTime.now());

        List<TransactionDetails> transactionDetailsList = new ArrayList<>();

        for(CRMDetails cd : crmDetails){
            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setPeriod(period);
            transactionDetails.setTransactionNumber(orNmber.getText());
            transactionDetails.setTransactionCode("ORMain");
            transactionDetails.setTransactionDate(date.getValue());
            transactionDetails.setAccountCode(cd.getGlCode());
            //transactionDetails.setSequenceNumber(0);
            transactionDetails.setDebit(cd.getTotal());
            //transactionDetails.setCredit(0);
            transactionDetails.setOrDate(date.getValue());
            //transactionDetails.setBankID("N/A");
            //transactionDetails.setNote("N/A");
            transactionDetailsList.add(transactionDetails);
        }

        try {
            String msg = TransactionHeaderDetailDAO.save(transactionHeader, transactionDetailsList);
            if (msg.isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
            }else{
                AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+msg,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: "+e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            e.printStackTrace();
        }

    }

}