package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.BankAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.CRMDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Paint;
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

   // @FXML
   // private JFXComboBox<String> paymentMode;
   // @FXML
   // private JFXComboBox<BankAccount> bankInfo;
    @FXML

    private JFXButton submitBtn;

    @FXML
    private JFXTextField name, address, purpose, energyBill, vat, surcharge, prepayment, accNum;

    //@FXML
    //private JFXTextArea remarks;

    //@FXML
    //private JFXTextField tinNo;

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

        InputValidation.restrictNumbersOnly(energyBill);
        InputValidation.restrictNumbersOnly(vat);
        InputValidation.restrictNumbersOnly(surcharge);
        InputValidation.restrictNumbersOnly(prepayment);
        InputValidation.restrictNumbersOnly(accNum);

        /*InputValidation.restrictNumbersOnly(tinNo);
        InputValidation.restrictNumbersOnly(orNmber);

        if(Utility.OR_NUMBER != 0) {
            orNmber.setText(""+Utility.OR_NUMBER);
        }

        bankInfo.setDisable(true);
        try {
            List<BankAccount> bankAccounts = BankAccountDAO.getAll();
            bankInfo.getItems().addAll(bankAccounts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        paymentMode.getItems().add("CASH ON HAND");
        paymentMode.getItems().add("CASH ON BANK");
        paymentMode.getItems().add("CHEQUES");
        paymentMode.getSelectionModel().selectFirst();*/
    }


    /*@FXML
    private void selectPaymentMode(ActionEvent event) {
        String payMode = paymentMode.getSelectionModel().getSelectedItem();
        if(payMode == null)return;
        if (payMode.equals("CASH ON HAND") || payMode.equals("CHEQUES")) {
            bankInfo.setDisable(true);
            bankInfo.getSelectionModel().clearSelection();
        } else {
            bankInfo.setDisable(false);
        }
    }*/

    @FXML
    private void search(ActionEvent event) {
        ModalBuilder.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    private void resetField(){
        name.setText(""); address.setText(""); purpose.setText("");// remarks.setText("");
        date.setValue(LocalDate.now());
        this.paymentTable.getColumns().clear();
        submitBtn.setDisable(false);
        total.setText("");
        paymentTable.getColumns().clear();
        crmQueue = null;
        crmDetails = null;
        consumerInfo = null;
        tellerInfo = null;
        accNum.setText("");
        prepayment.setText("");
        //tinNo.setText("");
        //paymentMode.getSelectionModel().clearSelection();
    }

    @Override
    public void receive(Object o) {
        if (o instanceof CRMQueue) {
            resetField();
            this.consumerInfo = (CRMQueue) o;
            this.name.setText(consumerInfo.getConsumerName());
            this.address.setText(consumerInfo.getConsumerAddress());
            this.purpose.setText(consumerInfo.getTransactionPurpose());
            tellerInfo = null;
            initTable(consumerInfo);
        }else if (o instanceof  HashMap) {
            resetField();
            TransactionHeader transactionHeader = (TransactionHeader) ((HashMap<?, ?>) o).get("TransactionHeader");
            LocalDate localDate = (LocalDate) ((HashMap<?, ?>) o).get("SearchDate");
            this.tellerInfo = (Teller) ((HashMap<?, ?>) o).get("SearchResult");
            this.name.setText(tellerInfo.getName());
            this.date.setValue(localDate);
            this.address.setText(tellerInfo.getAddress());
            this.purpose.setText("for DCR OR");

            if(transactionHeader != null){
                this.date.setValue(transactionHeader.getTransactionDate());
                //this.orNmber.setText(transactionHeader.getTransactionNumber());
                //this.remarks.setText(transactionHeader.getRemarks());
                submitBtn.setDisable(true);
            }
            address.setEditable(false);
            purpose.setEditable(false);
            consumerInfo = null;
            initTable(tellerInfo);
        }else if (o instanceof OROtherInfo) {
            try{
                OROtherInfo orOtherInfo = (OROtherInfo)o;
                nonePowerBills(orOtherInfo);
            }catch (Exception e){
                e.printStackTrace();
            }
        }else if (o instanceof Boolean) {
            boolean b = (Boolean) o;
            if(b){
                resetField();
            }
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
            List<ORItemSummary> orItemSummaries = teller.getOrItemSummaries();
            ObservableList<ORItemSummary> result = FXCollections.observableArrayList(orItemSummaries);
            /*ObservableList<ItemSummary> forOR = FXCollections.observableArrayList();
            for (ItemSummary itemSummary : result){
                if(itemSummary.getDescription().equals("Others") ||
                        itemSummary.getDescription().equals("Other Deductions") ||
                        itemSummary.getDescription().equals("Katas Ng VAT") ||
                        itemSummary.getDescription().equals("MD Refund") ||
                        itemSummary.getDescription().equals("SC Discount") ||
                        itemSummary.getDescription().equals("2307 (5%)"))
                    continue;

                forOR.add(itemSummary);

            }*/

            for (ORItemSummary orItemSummary : result){
                if(orItemSummary.getDescription().equals("Grand Total")){
                    collectionFromTeller = orItemSummary.getAmount();
                    total.setText(""+Utility.formatDecimal(collectionFromTeller));
                    break;
                }
            }

            this.paymentTable.setItems(result);

            //List<ItemSummary> misc = breakdown.get("Misc");
            //collectionFromTeller = misc.get(1).getTotal();
            //total.setText(""+Utility.formatDecimal(collectionFromTeller));

            TableColumn<ORItemSummary, String> column0 = new TableColumn<>("Account Code");

            column0.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAccountCode()));
            column0.setStyle("-fx-alignment: center-left;");

            TableColumn<ORItemSummary, String> column1 = new TableColumn<>("Item Description");
            column1.setMinWidth(220);
            column1.setCellValueFactory(new PropertyValueFactory<>("description"));
            /*column1.setStyle("-fx-alignment: center-left;");
            Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>> removeZeroValue
                    = //
                    new Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>>() {
                        @Override
                        public TableCell call(final TableColumn<ORItemSummary, String> param) {
                            final TableCell<ORItemSummary, String> cell = new TableCell<ORItemSummary, String>() {
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        ORItemSummary orItemSummary = getTableView().getItems().get(getIndex());
                                        if(orItemSummary.getAmount() == 0){
                                            getTableView().getItems().remove(getTableView().getItems().get(getIndex()));
                                        }
                                            setText(orItemSummary.getDescription());

                                    }
                                    paymentTable.refresh();
                                }
                            };
                            return cell;
                        }
                    };
            column1.setCellFactory(removeZeroValue);*/

            TableColumn<ORItemSummary, String> column2 = new TableColumn<>("Total Amount");

            column2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getTotalView()));
            column2.setStyle("-fx-alignment: center-right;");

            this.paymentTable.getColumns().add(column0);
            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
            //remove the last item Grand total
            paymentTable.getItems().remove(paymentTable.getItems().size()-1);

            paymentTable.refresh();
        }

        this.paymentTable.setPlaceholder(new Label("No consumer records was searched."));
    }


    @FXML
    private void addPrepayment(ActionEvent event) {
        ObservableList<ORItemSummary> result = paymentTable.getItems();
        boolean found = false;
        for(ORItemSummary search : result){
            if(search.getDescription().equals("Energy Bills - Others") && search.getAmount()>0){
                found = true;
                break;
            }
        }
        if(!found) {
            AlertDialogBuilder.messgeDialog("Transaction Error", "Cannot add prepayment, since other deduction is zero",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }
        if (tellerInfo != null) {
            if(!accNum.getText().isEmpty() && !prepayment.getText().isEmpty()){
                double amount = Double.parseDouble(prepayment.getText());
                ORItemSummary items = (ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1);
                String[] pre = items.getDescription().split("-");

                    if(pre[0].equals("Energy Prepayment")){
                        ((ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setAmount(amount);
                        ((ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setTotalView(Utility.formatDecimal(amount));
                    }else{
                        paymentTable.getItems().add(new ORItemSummary("Energy Prepayment-"+accNum.getText(),Double.parseDouble(prepayment.getText())));
                    }
            }
            paymentTable.refresh();
        }
    }
    @FXML
    private void removePrepayment(ActionEvent event) {
        int count = paymentTable.getItems().size();
        if(count >= 1) {
            try{
                if(tellerInfo==null) return;
                ItemSummary items = (ItemSummary) paymentTable.getItems().get(count - 1);
                String[] pre = items.getDescription().split("-");
                if (pre[0].equals("Energy Prepayment")) {
                    paymentTable.getItems().remove(paymentTable.getItems().size() - 1);
                    paymentTable.refresh();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @FXML
    private void printWhenEntered(ActionEvent  event) throws SQLException, ClassNotFoundException {
        if(!submitBtn.isDisable())
            checkInputs();
    }

    @FXML
    private void submitForConfirmation(ActionEvent event) throws SQLException, ClassNotFoundException {
        if(!submitBtn.isDisable())
            checkInputs();
    }

    @FXML
    void refund(ActionEvent events) {
        ObservableList<ORItemSummary> result = paymentTable.getItems();

        if(result==null) return;
        if(energyBill.getText().isEmpty() && vat.getText().isEmpty() && surcharge.getText().isEmpty()) return;
        if(tellerInfo==null) return;

        JFXButton accept = new JFXButton("Accept");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Issue refund:","Confirm transaction.\n" +
                        "Energy Bill: " +energyBill.getText()+"\n"+
                        "VAT: " +vat.getText()+"\n" +
                        "Surcharge: "+surcharge.getText()
                , accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
        dialog.setOnDialogOpened((event) -> { accept.requestFocus(); });
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));

        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                for(ORItemSummary item : result){
                    switch (item.getDescription()) {
                        case "Energy Bill":
                            if (!energyBill.getText().isEmpty()) {
                                item.setAmount(item.getAmount() - Double.parseDouble(energyBill.getText().replaceAll(",", "")));
                                item.setTotalView(Utility.formatDecimal(item.getAmount()));
                            }
                            break;
                        case "EVAT":
                            if (!vat.getText().isEmpty()) {
                                item.setAmount(item.getAmount() - Double.parseDouble(vat.getText().replaceAll(",", "")));
                                item.setTotalView(Utility.formatDecimal(item.getAmount()));
                            }

                            break;
                        case "Surcharge":
                            if (!surcharge.getText().isEmpty()) {
                                item.setAmount(item.getAmount() - Double.parseDouble(surcharge.getText().replaceAll(",", "")));
                                item.setTotalView(Utility.formatDecimal(item.getAmount()));
                            }
                            break;
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

    private void checkInputs() throws SQLException, ClassNotFoundException {
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



        if(!(Double.parseDouble(total.getText().replaceAll(",","")) > 0)){
            AlertDialogBuilder.messgeDialog("System Message", "Total amount is zero (0), cannot process transaction.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }
        ModalBuilder.showModalFromXMLNoClose(CashierConfirmOR.class, "../cashiering/cashier_confirm_or.fxml", Utility.getStackPane());
        /*if(orNmber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "OR number is required.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(paymentMode.getSelectionModel().getSelectedItem()==null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select Payment Mode and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(bankInfo.getSelectionModel().getSelectedItem()==null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select bank and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }





        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws SQLException {
                try{

                }catch (Exception e){
                    e.printStackTrace();
                }
                return null;
            }
        };

        task.setOnRunning(wse -> {
            print_btn.setDisable(true);
            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Print OR","Confirm transaction.\n" +
                            "Payment Mode: " +paymentMode.getSelectionModel().getSelectedItem()+"\n"+
                            "Date: " +date.getValue()+"\n"+
                            "OR#: " +orNmber.getText()+"\n" +
                            "Total: "+total.getText()
                    , accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            dialog.setOnDialogOpened((event) -> { accept.requestFocus(); });
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
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    dialog.close();
                }
            });

        });


        task.setOnSucceeded(wse -> {
            print_btn.setDisable(false);
        });

        task.setOnFailed(wse -> {
            print_btn.setDisable(false);
        });

        new Thread(task).start();*/

    }

    private void nonePowerBills(OROtherInfo orOtherInfo) throws Exception {
        String orNumber = orOtherInfo.getOrNumber();
        String tinNo = orOtherInfo.getTin();
        String remarks = orOtherInfo.getRemarks();
        String paymentMode = orOtherInfo.getPaymentMode();
        String bankAccountCode = orOtherInfo.getBankAccountCode();

        int month = date.getValue().getMonthValue();
        int year = date.getValue().getYear();
        LocalDate period = LocalDate.of(year, month, 1);

        TransactionHeader transactionHeader= new TransactionHeader();
        transactionHeader.setPeriod(period);
        transactionHeader.setTransactionNumber(orNumber);
        transactionHeader.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
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
        transactionHeader.setTinNo(tinNo);
        transactionHeader.setRemarks(remarks);

        //transactionHeader.setBank("N/A");
        //transactionHeader.setReferenceNo("N/A");

        transactionHeader.setDateEntered(LocalDateTime.now());

        List<TransactionDetails> transactionDetailsList = new ArrayList<>();

        if(crmDetails != null) {
            int seq = 1;
            for (CRMDetails cd : crmDetails) {
                TransactionDetails transactionDetails = new TransactionDetails();
                transactionDetails.setPeriod(period);
                transactionDetails.setTransactionNumber(orNumber);
                transactionDetails.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
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
            ObservableList<ORItemSummary> temp = paymentTable.getItems();
            for (ORItemSummary orItemSummary : temp) {
                /*if(orItemSummary.getDescription().equals("Energy Bill - Others") ||
                        orItemSummary.getDescription().equals("Sinking Fund - Katas ng VAT") ||
                        orItemSummary.getDescription().equals("Meter Deposit-Main") ||
                        orItemSummary.getDescription().equals("Senior Citizen Discount") ||
                        orItemSummary.getDescription().equals("EVAT 2307 (5%)"))
                    continue;*/

                if(orItemSummary.getAmount()>0) {
                    TransactionDetails transactionDetails = new TransactionDetails();
                    transactionDetails.setPeriod(period);
                    transactionDetails.setTransactionNumber(orNumber);

                    transactionDetails.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());

                    transactionDetails.setTransactionDate(date.getValue());

                    if (paymentMode.equals("CASH ON HAND") || paymentMode.equals("CHEQUES")) {
                        transactionDetails.setAccountCode(orItemSummary.getAccountCode());
                    } else {
                        transactionDetails.setAccountCode(bankAccountCode);
                    }

                    transactionDetails.setParticulars(orItemSummary.getDescription());
                    transactionDetails.setSequenceNumber(seq++);

                    if (orItemSummary.getAmount() > 0)
                        transactionDetails.setCredit(orItemSummary.getAmount());
                    else
                        transactionDetails.setDebit(orItemSummary.getAmount());

                    transactionDetails.setOrDate(date.getValue());
                    //transactionDetails.setBankID("N/A");
                    //transactionDetails.setNote("N/A");
                    transactionDetailsList.add(transactionDetails);
                }
            }
        }

        //check Period, TransactionNumber(OR number), TransactionCode(main or sub)
        if(TransactionHeaderDAO.isAvailable(transactionHeader.getPeriod(), transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode())){
            AlertDialogBuilder.messgeDialog("System Message", "OR number is already used.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        ORContent orContent = new ORContent(crmQueue, transactionHeader, transactionDetailsList);
        orContent.setAddress(address.getText());
        orContent.setDate(date.getValue());
        orContent.setOrNumber(orNumber);
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
        //ModalBuilder.MODAL_CLOSE();//close the CashierConfirmOR dialog, before opening ORLayoutController
        ModalBuilder.showModalFromXMLNoClose(ORLayoutController.class, "../cashiering/orLayout.fxml", Utility.getStackPane());

        /*JFXDialog dialog = DialogBuilder.showWaitDialog("System Message","Please wait, processing request.",Utility.getStackPane(), DialogBuilder.INFO_DIALOG);

        Task<String> task = new Task<>() {
            @Override
            protected String call() {

                ModalBuilder.showModalFromXMLNoClose(ORLayoutController.class, "../cashiering/orLayout.fxml", Utility.getStackPane());

                return Utility.ERROR_MSG;
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
                resetField();
            }else{
                AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: sss"+Utility.ERROR_MSG,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
            System.out.println(Utility.ERROR_MSG);

        });

        task.setOnFailed(wse -> {
            dialog.close();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while saving transaction: sss"+Utility.ERROR_MSG,
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
        });

        new Thread(task).start();*/

    }

}