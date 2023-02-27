package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.CRMDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
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
    private JFXTextField orNmber, name, address, purpose, energyBill, vat, surcharge, prepayment, accNum;

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
        InputValidation.restrictNumbersOnly(energyBill);
        InputValidation.restrictNumbersOnly(vat);
        InputValidation.restrictNumbersOnly(surcharge);
        InputValidation.restrictNumbersOnly(prepayment);
        InputValidation.restrictNumbersOnly(accNum);

        if(Utility.OR_NUMBER != 0) {
            orNmber.setText(""+Utility.OR_NUMBER);
        }



    }

    @FXML
    private void search(ActionEvent event) {
        ModalBuilder.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    private void resetField(){
        name.setText(""); address.setText(""); purpose.setText(""); remarks.setText("");
        date.setValue(LocalDate.now());
        this.paymentTable.getColumns().clear();
        print_btn.setVisible(true);
        total.setText("");
        paymentTable.getColumns().clear();
        crmQueue = null;
        crmDetails = null;
        consumerInfo = null;
        tellerInfo = null;
        accNum.setText("");
        prepayment.setText("");
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
            this.address.setText(tellerInfo.getAddress());
            this.purpose.setText("for DCR OR");

            if(transactionHeader != null){
                this.date.setValue(transactionHeader.getTransactionDate());
                this.orNmber.setText(transactionHeader.getTransactionNumber());
                this.remarks.setText(transactionHeader.getRemarks());
                print_btn.setVisible(false);
            }
            address.setEditable(false);
            purpose.setEditable(false);
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
                this.total.setText(""+String.format("%,.2f",calculate));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.crmQueue = crmQueue;
            this.crmDetails = crmDetails;
        }if(o instanceof Teller){
            Teller teller = (Teller)  o;
            HashMap<String, List<ItemSummary>> breakdown = teller.getDCRBreakDown();
            ObservableList<ItemSummary> result = FXCollections.observableArrayList(breakdown.get("Breakdown"));
            ObservableList<ItemSummary> forOR = FXCollections.observableArrayList();
            for (ItemSummary itemSummary : result){
                if(itemSummary.getDescription().equals("Others") ||
                        itemSummary.getDescription().equals("Other Deductions") ||
                        itemSummary.getDescription().equals("Katas Ng VAT") ||
                        itemSummary.getDescription().equals("MD Refund") ||
                        itemSummary.getDescription().equals("SC Discount") ||
                        itemSummary.getDescription().equals("2307 (5%)"))
                    continue;

                forOR.add(itemSummary);

            }

            this.paymentTable.setItems(forOR);

            List<ItemSummary> misc = breakdown.get("Misc");
            collectionFromTeller = misc.get(1).getTotal();
            total.setText(""+Utility.formatDecimal(collectionFromTeller));

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
    private void addPrepayment(ActionEvent event) {
        if (tellerInfo != null) {
            if(!accNum.getText().isEmpty() && !prepayment.getText().isEmpty()){
                double amount = Double.parseDouble(prepayment.getText());
                ItemSummary items = (ItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1);
                String[] pre = items.getDescription().split("-");

                    if(pre[0].equals("Energy Prepayment")){
                        ((ItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setTotal(amount);
                        ((ItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setTotalView(Utility.formatDecimal(amount));
                        System.out.println("mana");
                    }else{
                        paymentTable.getItems().add(new ItemSummary("Energy Prepayment-"+accNum.getText(),Double.parseDouble(prepayment.getText())));
                    }
            }
            paymentTable.refresh();
        }
    }
    @FXML
    private void removePrepayment(ActionEvent event) {
        ItemSummary items = (ItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1);
        String[] pre = items.getDescription().split("-");
        if(pre[0].equals("Energy Prepayment")){
            paymentTable.getItems().remove(paymentTable.getItems().size()-1);
            paymentTable.refresh();
        }
    }
    @FXML
    private void printWhenEntered(KeyEvent event) throws SQLException, ClassNotFoundException {
        if (event.getCode() == KeyCode.ENTER)
            printing();
    }

    @FXML
    private void printOR(ActionEvent event) throws SQLException, ClassNotFoundException {
        printing();
    }

    @FXML
    void refund(ActionEvent event) {
        ObservableList<ItemSummary> result = paymentTable.getItems();

        if(result==null) return;

        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Issue refund:","Confirm transaction.\n" +
                        "Energy Bill: " +energyBill.getText()+"\n"+
                        "VAT: " +vat.getText()+"\n" +
                        "Surcharge: "+surcharge.getText()
                , accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                for(ItemSummary item : result){
                    if(item.getDescription().equals("Energy")){
                        item.setTotal(item.getTotal() - Double.parseDouble(energyBill.getText().replaceAll(",","")));
                        item.setTotalView(Utility.formatDecimal(item.getTotal()));
                    }
                    if(item.getDescription().equals("Evat")){
                        item.setTotal(item.getTotal() - Double.parseDouble(vat.getText().replaceAll(",","")));
                        item.setTotalView(Utility.formatDecimal(item.getTotal()));
                    }
                    if(item.getDescription().equals("Surcharge")){
                        item.setTotal(item.getTotal() - Double.parseDouble(surcharge.getText().replaceAll(",","")));
                        item.setTotalView(Utility.formatDecimal(item.getTotal()));
                    }
                }
                energyBill.setText("");
                vat.setText("");
                surcharge.setText("");

                paymentTable.refresh();
                dialog.close();
            }
        });
    }

    private void printing() throws SQLException, ClassNotFoundException {

        if(paymentTable.getItems().isEmpty()){
            AlertDialogBuilder.messgeDialog("System Message", "No item breakdown found",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }


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
                try {
                    nonePowerBills();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                dialog.close();
            }
        });
    }

    private void nonePowerBills() throws SQLException, ClassNotFoundException {
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
            transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
            transactionHeader.setTransactionDate(date.getValue());
        }else if(tellerInfo != null) {
            transactionHeader.setSource("employee");
            transactionHeader.setEnteredBy(tellerInfo.getUsername());
            transactionHeader.setAccountID(ActiveUser.getUser().getId());
            transactionHeader.setAmount(collectionFromTeller);
            transactionHeader.setTransactionDate(tellerInfo.getDate());
        }
        //transactionHeader.setParticulars("N/A");
        transactionHeader.setRemarks(remarks.getText());
        //transactionHeader.setBank("N/A");
        //transactionHeader.setReferenceNo("N/A");

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
        }else if(tellerInfo != null) {
            int seq = 1;
            ObservableList<ItemSummary> temp = paymentTable.getItems();
            for (ItemSummary itemSummary : temp) {
                if(itemSummary.getDescription().equals("Others") ||
                        itemSummary.getDescription().equals("Other Deductions") ||
                        itemSummary.getDescription().equals("Katas Ng VAT") ||
                        itemSummary.getDescription().equals("MD Refund") ||
                        itemSummary.getDescription().equals("SC Discount") ||
                        itemSummary.getDescription().equals("2307 (5%)"))
                    continue;

                TransactionDetails transactionDetails = new TransactionDetails();
                transactionDetails.setPeriod(period);
                transactionDetails.setTransactionNumber(orNmber.getText());

                if (Utility.OFFICE_PREFIX.equalsIgnoreCase("main"))
                    transactionDetails.setTransactionCode("OR");
                else
                    transactionDetails.setTransactionCode("ORSub");

                transactionDetails.setTransactionDate(date.getValue());
                transactionDetails.setAccountCode(tellerInfo.getUsername());
                transactionDetails.setParticulars(itemSummary.getDescription());
                transactionDetails.setSequenceNumber(seq++);

                if(itemSummary.getTotal() > 0)
                    transactionDetails.setCredit(itemSummary.getTotal());
                else
                    transactionDetails.setDebit(itemSummary.getTotal());

                transactionDetails.setOrDate(date.getValue());
                //transactionDetails.setBankID("N/A");
                //transactionDetails.setNote("N/A");
                transactionDetailsList.add(transactionDetails);
            }
        }

        //check Period, TransactionNumber(OR number), TransactionCode(main or sub)
        if(TransactionHeaderDAO.isAvailable(transactionHeader.getPeriod(), transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode())){
            AlertDialogBuilder.messgeDialog("System Message", "OR number is already used.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        try {
            String msg = TransactionHeaderDetailDAO.save(crmQueue, transactionHeader, transactionDetailsList);
            if (msg.isEmpty()){
                AlertDialogBuilder.messgeDialog("System Message", "Transaction Complete.",
                        Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);

                Utility.OR_NUMBER = Integer.parseInt(orNmber.getText())+1;

                /*
                    private String address;
                    private LocalDate date;
                    private String orNumber;
                    private String issuedTo;
                    private String issuedBy;
                    private double total;
                    private ObservableList<ItemSummary> breakdown;
                 */
                ORContent orContent = new ORContent();
                orContent.setAddress(address.getText());
                orContent.setDate(date.getValue());
                orContent.setOrNumber(orNmber.getText());
                orContent.setIssuedTo(name.getText());
                EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
                orContent.setIssuedBy(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
                orContent.setTotal(Double.parseDouble(total.getText().replaceAll(",","")));
                if(consumerInfo!=null){
                    orContent.setCustomerCollection(paymentTable.getItems());
                }else{
                    orContent.setTellerCollection(paymentTable.getItems());
                }
                //orContent.setBreakdown(paymentTable.getItems());
                Utility.setOrContent(orContent);

                ModalBuilder.showModalFromXMLNoClose(ORLayoutController.class, "../cashiering/orLayout.fxml", Utility.getStackPane());
                ModalBuilder.MODAL_CLOSE();

                resetField();
                orNmber.setText(""+Utility.OR_NUMBER);
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