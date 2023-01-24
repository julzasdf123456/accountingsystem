package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.CRMDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;


import java.net.URL;
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
    private JFXTextField orNmber, name, address, purpose;

    @FXML
    private JFXTextArea remarks;


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

    private double collectionFromTeller;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setParentController(this);
        try {
            date.setValue(Utility.serverDate());
        } catch (Exception e) {
            date.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }
        InputValidation.restrictNumbersOnly(orNmber);
    }

    @FXML
    private void search(ActionEvent event) {
        ModalBuilder.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    private void resetField(){
        orNmber.setText(""); name.setText(""); address.setText(""); purpose.setText(""); remarks.setText("");
        date.setValue(LocalDate.now());
        this.paymentTable.getColumns().clear();
        print_btn.setVisible(true);
        total.setText("Total: 0.00");
        paymentTable.getColumns().clear();
        crmQueue = null;
        crmDetails = null;
        consumerInfo = null;
        tellerInfo = null;
    }

    @Override
    public void receive(Object o) {
        resetField();

        if (o instanceof CRMQueue) {
            this.consumerInfo = (CRMQueue) o;
            this.name.setText(consumerInfo.getConsumerName());
            this.address.setText(consumerInfo.getConsumerAddress());
            this.purpose.setText(consumerInfo.getTransactionPurpose());
            tellerInfo = null;
            initTable(consumerInfo);
        }else if (o instanceof  HashMap) {
            TransactionHeader transactionHeader = (TransactionHeader) ((HashMap<?, ?>) o).get("TransactionHeader");
            LocalDate localDate = (LocalDate) ((HashMap<?, ?>) o).get("SearchDate");
            this.tellerInfo = (Teller) ((HashMap<?, ?>) o).get("SearchResult");
            this.name.setText(tellerInfo.getName());
            this.date.setValue(localDate);

            if(transactionHeader != null){
                this.date.setValue(transactionHeader.getTransactionDate());
                this.orNmber.setText(transactionHeader.getTransactionNumber());
                this.remarks.setText(transactionHeader.getRemarks());
                print_btn.setVisible(false);
            }

            this.address.setText("-");
            this.purpose.setText("-");
            address.setDisable(true);
            purpose.setDisable(true);
            consumerInfo = null;
            initTable(tellerInfo);
        }
        ModalBuilder.MODAL_CLOSE();
    }

    private void initTable(Object o) {
        paymentTable.getColumns().clear();
        if(o instanceof CRMQueue){
            CRMQueue crmQueue = (CRMQueue) o;

            TableColumn<CRMDetails, String> column1 = new TableColumn<>("Particulars");
            column1.setMinWidth(200);
            column1.setCellValueFactory(new PropertyValueFactory<>("particulars"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<CRMDetails, String> column2 = new TableColumn<>("Sub Total");
            column2.setMinWidth(100);
            column2.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getSubTotal())));
            column2.setStyle("-fx-alignment: center-right;");

            TableColumn<CRMDetails, String> column3 = new TableColumn<>("VAT");
            column3.setMinWidth(100);
            column3.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getVAT())));
            column3.setStyle("-fx-alignment: center-right;");

            TableColumn<CRMDetails, String> column4 = new TableColumn<>("Total");
            column4.setMinWidth(100);
            column4.setCellValueFactory(obj-> new SimpleStringProperty(Utility.formatDecimal(obj.getValue().getTotal())));
            column4.setStyle("-fx-alignment: center-right;");

            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
            this.paymentTable.getColumns().add(column3);
            this.paymentTable.getColumns().add(column4);

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
            collectionFromTeller = misc.get(1).getTotal();
            total.setText("Total: "+Utility.formatDecimal(collectionFromTeller));

            TableColumn<ItemSummary, String> column1 = new TableColumn<>("Item Description");
            column1.setMinWidth(200);
            column1.setCellValueFactory(new PropertyValueFactory<>("description"));
            column1.setStyle("-fx-alignment: center-left;");

            TableColumn<ItemSummary, String> column2 = new TableColumn<>("Total Amount");
            column2.setMinWidth(200);
            column2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getTotalView()));
            column2.setStyle("-fx-alignment: center-right;");

            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
        }

        this.paymentTable.setPlaceholder(new Label("No consumer records was searched."));
    }

    @FXML
    private void printWhenEntered(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER)
            printing();
    }

    @FXML
    private void printOR(ActionEvent event) {
        printing();
    }

    private void printing() {
        if(consumerInfo == null && tellerInfo == null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select consumer or teller information.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(orNmber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "OR number is required.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }
        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Print OR","Confirm transaction.\n" +
                        "Date: " +date.getValue()+"\n"+
                        "OR#: " +orNmber.getText()+"\n" +
                        ""+total.getText()
                , accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                nonePowerBills();
                dialog.close();
            }
        });
    }

    private void nonePowerBills(){
        int month = date.getValue().getMonthValue();
        int year = date.getValue().getYear();
        LocalDate period = LocalDate.of(year, month, 1);

        TransactionHeader transactionHeader= new TransactionHeader();
        transactionHeader.setPeriod(period);
        transactionHeader.setTransactionNumber(orNmber.getText());
        if(Utility.OFFICE_PREFIX.equalsIgnoreCase("main"))
            transactionHeader.setTransactionCode("OR");
        else
            transactionHeader.setTransactionCode("ORSub");
        transactionHeader.setOffice(Utility.OFFICE_PREFIX);
        if(consumerInfo != null) {
            transactionHeader.setSource(crmQueue.getSource());
            transactionHeader.setAccountID(crmQueue.getSourseId());//CRM Source ID
            transactionHeader.setTransactionDate(date.getValue());
        }else if(tellerInfo != null) {
            transactionHeader.setSource("employee");
            transactionHeader.setAccountID(tellerInfo.getUsername());
            transactionHeader.setAmount(collectionFromTeller);
            transactionHeader.setTransactionDate(tellerInfo.getDate());
        }
        //transactionHeader.setParticulars("N/A");
        transactionHeader.setRemarks(remarks.getText());
        //transactionHeader.setBank("N/A");
        //transactionHeader.setReferenceNo("N/A");
        transactionHeader.setEnteredBy(ActiveUser.getUser().getId());
        transactionHeader.setDateEntered(LocalDateTime.now());

        List<TransactionDetails> transactionDetailsList = new ArrayList<>();

        if(crmDetails != null) {
            int seq = 1;
            for (CRMDetails cd : crmDetails) {
                TransactionDetails transactionDetails = new TransactionDetails();
                transactionDetails.setPeriod(period);
                transactionDetails.setTransactionNumber(orNmber.getText());

                if (Utility.OFFICE_PREFIX.equalsIgnoreCase("main"))
                    transactionDetails.setTransactionCode("OR");
                else
                    transactionDetails.setTransactionCode("ORSub");

                transactionDetails.setTransactionDate(date.getValue());
                transactionDetails.setAccountCode(cd.getGlCode());//GL Code or the account code of the particular
                transactionDetails.setParticulars(cd.getParticulars());
                transactionDetails.setSequenceNumber(seq++);

                if(cd.getTotal() > 0)
                    transactionDetails.setCredit(cd.getTotal());
                else
                    transactionDetails.setDebit(cd.getTotal());

                transactionDetails.setOrDate(date.getValue());
                //transactionDetails.setBankID("N/A");
                //transactionDetails.setNote("N/A");
                transactionDetailsList.add(transactionDetails);
            }
        }

        try {
            String msg = TransactionHeaderDetailDAO.save(crmQueue, transactionHeader, transactionDetailsList);
            if (msg.isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                resetField();
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