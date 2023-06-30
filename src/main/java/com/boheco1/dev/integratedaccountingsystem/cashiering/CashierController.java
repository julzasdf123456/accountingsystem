package com.boheco1.dev.integratedaccountingsystem.cashiering;


import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;


import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class CashierController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private JFXToggleButton manualORToggle;

    @FXML
    private JFXButton submitBtn, search_btn;

    @FXML
    private HBox manualORinput;

    @FXML
    private JFXTextField orItem, orItemAmount, serviceFeeAmount, serviceFeeAccounts, name, address, purpose, energyBill, vat, surcharge, prepayment, accNum, tinNo, orNumber, businessStyle;


    @FXML
    private JFXComboBox<String> paymentMode;
    @FXML
    private JFXComboBox<BankAccount> bankInfo;

    @FXML
    private JFXTextArea remarks;

    @FXML
    private DatePicker date;

    @FXML
    private Label total;

    @FXML
    private TableView paymentTable;

    private CRMQueue customerFromCRM = null;
    private Teller tellerInfo = null;

    private CRMQueue crmQueue;
    private List<CRMDetails> crmDetails;

    //ORItemSummary grandTotalHolder;
    ParticularsAccount particularsAccount = null;
    private ObservableList<ORItemSummary> OR_itemList;
    private ParticularsAccount serviceFee;
    private double collectionFromTeller;
    //private double otherDeduction;
    double collectionFromCRM;
    double originalEvatAmount;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setParentController(this);
        try {
            date.setValue(Utility.serverDate());
        } catch (Exception e) {
            date.setValue(LocalDate.now());
            throw new RuntimeException(e);
        }
        manualORinput.setVisible(false);

        InputValidation.restrictNumbersOnly(energyBill);
        InputValidation.restrictNumbersOnly(vat);
        InputValidation.restrictNumbersOnly(surcharge);
        InputValidation.restrictNumbersOnly(prepayment);
        InputValidation.restrictNumbersOnly(accNum);
        //InputValidation.restrictNumbersOnly(amount);

        addManualORItem(orItemAmount);
        addManualORItem(name);
        InputValidation.restrictNumbersOnly(orItemAmount);
        bindParticularAccountInfoAutocomplete(orItem);
        bindEmployeeInfoAutocomplete(name);

        InputValidation.restrictNumbersOnly(tinNo);
        InputValidation.restrictNumbersOnly(orNumber);
        if(Utility.OR_NUMBER != 0) {
            orNumber.setText(""+Utility.OR_NUMBER);
        }

        paymentMode.getItems().add("CASH ON HAND");
        paymentMode.getItems().add("CASH IN BANK");
        paymentMode.getItems().add("CHEQUES");

        try {
            List<BankAccount> bankAccounts = BankAccountDAO.getAll();
            bankInfo.getItems().addAll(bankAccounts);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        paymentMode.getSelectionModel().selectFirst();
        bankInfo.setDisable(true);

        try {
            serviceFee = ParticularsAccountDAO.getParticularsAccount("Service Fee");
            serviceFeeAmount.setText(Utility.formatDecimal(serviceFee.getAmount()));
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "Unable to retrieve service fee information, due to: " + e.getMessage() ,
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void addManualORItem(JFXTextField jfxTextField) {
        jfxTextField.setOnAction(e -> {
            if(!orItem.getText().isEmpty() && !orItemAmount.getText().isEmpty()){
                try{
                    double amount = Double.parseDouble(orItemAmount.getText());
                    ORItemSummary orItemSummary = new ORItemSummary(particularsAccount.getAccountCode(), particularsAccount.getParticulars(), amount);
                    OR_itemList.add(orItemSummary);
                    orItem.requestFocus();
                    orItem.setText("");
                    orItemAmount.setText("");
                    reCompute();
                    paymentTable.setItems(OR_itemList);
                    reCompute();
                    paymentTable.refresh();
                }catch (Exception ex){
                    AlertDialogBuilder.messgeDialog("System Error", "Manual O.R.:"+ex.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            }
        });
    }

    @FXML
    private void search(ActionEvent event) {
        ModalBuilder.showModalFromXMLNoClose(CashierController.class, "../cashiering/cashiering_search_consumer.fxml", Utility.getStackPane());
    }

    @FXML
    private void switchORMode(ActionEvent event) {
        resetField();
        if(manualORToggle.isSelected()){
            search_btn.setDisable(true);
            manualORinput.setVisible(true);
            name.setEditable(true);
            address.setEditable(true);
            purpose.setEditable(true);

            TableColumn<ORItemSummary, String> column0 = new TableColumn<>("Account Code");
            column0.setStyle("-fx-alignment: center-left;");
            column0.setMinWidth(120);
            column0.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAccountCode()));

            TableColumn<ORItemSummary, String> column1 = new TableColumn<>("Item Description");
            column1.setMinWidth(250);
            column1.setCellValueFactory(new PropertyValueFactory<>("description"));

            TableColumn<ORItemSummary, String> column2 = new TableColumn<>("Total Amount");
            column2.setStyle("-fx-alignment: center-right;");
            column2.setMinWidth(100);
            column2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getTotalView()));

            TableColumn<ORItemSummary, String> column3 = new TableColumn<>(" ");
            column3.setPrefWidth(50);
            column3.setMaxWidth(50);
            column3.setMinWidth(50);
            Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>> column3Remove
                    = //
                    new Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>>() {
                        @Override
                        public TableCell call(final TableColumn<ORItemSummary, String> param) {
                            final TableCell<ORItemSummary, String> cell = new TableCell<ORItemSummary, String>() {

                                FontIcon icon = new FontIcon("mdi2c-close-circle");
                                private final JFXButton btn = new JFXButton("", icon);
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        ORItemSummary data = getTableView().getItems().get(getIndex());
                                        icon.setIconSize(16);
                                        icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                        btn.setOnAction(event -> {
                                            try{
                                                OR_itemList.remove(data);
                                                //ObservableList<TransactionDetails> result = FXCollections.observableArrayList(newTransactionDetails);
                                                //newItemTable.setItems(result);
                                                paymentTable.refresh();
                                                reCompute();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                AlertDialogBuilder.messgeDialog("System Error", "Error encountered while removing item: "+ e.getMessage(),
                                                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                            }
                                        });
                                        setStyle("-fx-background-color: #ffff; -fx-alignment: center; ");
                                        setGraphic(btn);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            column3.setCellFactory(column3Remove);

            this.paymentTable.getColumns().add(column0);
            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
            this.paymentTable.getColumns().add(column3);
            paymentTable.setPlaceholder(new Label("No item Added"));
            OR_itemList =  FXCollections.observableArrayList();
            reCompute();
        }else{
            search_btn.setDisable(false);
            manualORinput.setVisible(false);
            name.setEditable(false);
            address.setEditable(false);
            purpose.setEditable(false);
        }

    }



    @FXML
    private void selectPaymentMode(ActionEvent event) {
        String payMode = paymentMode.getSelectionModel().getSelectedItem();
        if(payMode == null)return;
        if (payMode.equals("CASH ON HAND") || payMode.equals("CHEQUES")) {
            bankInfo.setDisable(true);
            bankInfo.getSelectionModel().clearSelection();
        } else {
            bankInfo.setDisable(false);
        }
    }
    private void resetField(){
        name.setText(""); address.setText(""); purpose.setText("");
        OR_itemList = null;
        date.setValue(LocalDate.now());
        submitBtn.setDisable(false);
        total.setText("0");
        paymentTable.getColumns().clear();
        paymentTable.getItems().clear();
        crmQueue = null;
        crmDetails = null;
        customerFromCRM = null;
        tellerInfo = null;
        accNum.setText("");
        prepayment.setText("");
        tinNo.setText("");
        paymentMode.getSelectionModel().selectFirst();
        bankInfo.getSelectionModel().clearSelection();
        remarks.setText("");
        businessStyle.setText("");
        serviceFeeAccounts.setText("");
    }
    @Override
    public void receive(Object o) {
        if (o instanceof CRMQueue) {
            resetField();
            this.customerFromCRM = (CRMQueue) o;
            this.name.setText(customerFromCRM.getConsumerName());
            this.address.setText(customerFromCRM.getConsumerAddress());
            this.purpose.setText(customerFromCRM.getTransactionPurpose());
            tellerInfo = null;
            initTable(customerFromCRM);
        }else if (o instanceof  HashMap) {
            resetField();
            TransactionHeader transactionHeader = (TransactionHeader) ((HashMap<?, ?>) o).get("TransactionHeader");
            LocalDate localDate = (LocalDate) ((HashMap<?, ?>) o).get("SearchDate");
            this.tellerInfo = (Teller) ((HashMap<?, ?>) o).get("SearchResult");
            serviceFeeAccounts.setText(""+(int) ((HashMap<?, ?>) o).get("CountAccount"));


            if(transactionHeader == null){
                this.name.setText(tellerInfo.getName());
                this.date.setValue(localDate);
                this.address.setText(tellerInfo.getAddress());
                this.purpose.setText("for DCR O.R");
                address.setEditable(false);
                purpose.setEditable(false);
                customerFromCRM = null;
                submitBtn.setDisable(false);
            }else{
                this.address.setText(tellerInfo.getAddress());
                this.name.setText(tellerInfo.getName());
                this.date.setValue(transactionHeader.getTransactionDate());
                this.orNumber.setText(transactionHeader.getTransactionNumber());
                this.purpose.setText("for DCR O.R");
                this.remarks.setText(transactionHeader.getRemarks());
                submitBtn.setDisable(true);
                AlertDialogBuilder.messgeDialog("System Message", "Transaction has already been processed and assigned with an OR number." ,
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            }
            initTable(tellerInfo);

        }else if (o instanceof Boolean) {
            //Receive from ORLayoutController
            boolean b = (Boolean) o;
            if(b){
                resetField();
                orNumber.setText(""+Utility.OR_NUMBER);
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
            column4.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getTotalView()));
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
                collectionFromCRM = 0;
                for (CRMDetails c : result){
                    collectionFromCRM +=c.getTotal();
                }
                this.total.setText(""+String.format("%,.2f", collectionFromCRM));
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.crmQueue = crmQueue;
            this.crmDetails = crmDetails;
        }if(o instanceof Teller){
            Teller teller = (Teller)  o;
            ORItemSummary removeGrandTotal=null;
            List<ORItemSummary> orItemSummaries = teller.getOrItemSummaries();
            ObservableList<ORItemSummary> result = FXCollections.observableArrayList(orItemSummaries);
            for (ORItemSummary os : result){
                if(os.getDescription().equals("EVAT")){
                    originalEvatAmount = os.getAmount();
                    break;
                }
            }

            TableColumn<ORItemSummary, String> column0 = new TableColumn<>("Account Code");
            column0.setStyle("-fx-alignment: center-left;");
            column0.setMinWidth(120);
            column0.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAccountCode()));

            TableColumn<ORItemSummary, String> column1 = new TableColumn<>("Item Description");
            column1.setMinWidth(250);
            column1.setCellValueFactory(new PropertyValueFactory<>("description"));

            TableColumn<ORItemSummary, String> column2 = new TableColumn<>("Total Amount");
            column2.setStyle("-fx-alignment: center-right;");
            column2.setMinWidth(100);
            column2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getTotalView()));

            TableColumn<ORItemSummary, String> column3 = new TableColumn<>(" ");
            column3.setPrefWidth(50);
            column3.setMaxWidth(50);
            column3.setMinWidth(50);
            Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>> column3Remove
                    = //
                    new Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>>() {
                        @Override
                        public TableCell call(final TableColumn<ORItemSummary, String> param) {
                            final TableCell<ORItemSummary, String> cell = new TableCell<ORItemSummary, String>() {

                                FontIcon icon = new FontIcon("mdi2c-close-circle");
                                private final JFXButton btn = new JFXButton("", icon);
                                @Override
                                public void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);
                                    if (empty) {
                                        setGraphic(null);
                                        setText(null);
                                    } else {
                                        ORItemSummary data = getTableView().getItems().get(getIndex());
                                        icon.setIconSize(16);
                                        icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                        btn.setOnAction(event -> {
                                            try{
                                                OR_itemList.remove(data);
                                                //ObservableList<TransactionDetails> result = FXCollections.observableArrayList(newTransactionDetails);
                                                //newItemTable.setItems(result);
                                                paymentTable.refresh();
                                                reCompute();
                                            }catch (Exception e){
                                                e.printStackTrace();
                                                AlertDialogBuilder.messgeDialog("System Error", "Error encountered while removing item: "+ e.getMessage(),
                                                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                            }
                                        });
                                        setStyle("-fx-background-color: #ffff; -fx-alignment: center; ");
                                        setGraphic(btn);
                                    }
                                }
                            };
                            return cell;
                        }
                    };
            column3.setCellFactory(column3Remove);

            this.paymentTable.getColumns().add(column0);
            this.paymentTable.getColumns().add(column1);
            this.paymentTable.getColumns().add(column2);
            //this.paymentTable.getColumns().add(column3);

            OR_itemList =  FXCollections.observableArrayList(teller.getOrItemSummaries());
            paymentTable.setItems(OR_itemList);
            reCompute();
            //paymentTable.refresh();
        }

        this.paymentTable.setPlaceholder(new Label("No consumer records was searched."));
    }




    private void reCompute() {
        if(OR_itemList == null)
            return;

        double calculate = 0;
        for (ORItemSummary t : OR_itemList)
            calculate += t.getAmount();

        collectionFromTeller=calculate;
        total.setText(""+Utility.formatDecimal(collectionFromTeller));
        //totalDisplay.setText(Utility.formatDecimal(totalAmount));
    }

    @FXML
    private void addPrepayment(ActionEvent event) {
        double otherDeduction=0;
        if (tellerInfo != null) {
            if(!accNum.getText().isEmpty() && !prepayment.getText().isEmpty()){
                double amount = Double.parseDouble(prepayment.getText());
                for(ORItemSummary os : OR_itemList){
                    if(os.getAccountCode().equals("124101010001")){ //check is Energy Bill - Others is already added
                        otherDeduction = os.getAmount();
                        break;
                    }
                }
                if(otherDeduction==0) {
                    AlertDialogBuilder.messgeDialog("Transaction Error", "Cannot add prepayment, since other deduction is zero.",
                            Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    return;
                }else if(amount>otherDeduction){
                    AlertDialogBuilder.messgeDialog("Transaction Error", "Prepayment cannot be more than other deduction.",
                            Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    return;
                }

                ORItemSummary items = (ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1);
                String[] pre = items.getDescription().split("-");

                if(pre[0].equals("Energy Prepayment")){
                    ((ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setAmount(amount);
                    ((ORItemSummary) paymentTable.getItems().get(paymentTable.getItems().size()-1)).setTotalView(Utility.formatDecimal(amount));
                }else{
                    paymentTable.getItems().add(new ORItemSummary("23220901000","Energy Prepayment-"+accNum.getText(),Double.parseDouble(prepayment.getText())));
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
                ORItemSummary items = (ORItemSummary) paymentTable.getItems().get(count - 1);
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
    private void addRefund(ActionEvent events) {
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
                reCompute();
            }
        });
    }

    @FXML
    private void addServiceFee(ActionEvent event) {
        try{
            if(!serviceFeeAccounts.getText().isEmpty()){
                double fee = serviceFee.getAmount() * Double.parseDouble(serviceFeeAccounts.getText());
                boolean found = false;
                for(ORItemSummary os : OR_itemList){
                    if(os.getDescription().equals("Service Fee")){
                        if(fee <= 0){
                            OR_itemList.remove(os);
                            os.setAmount(originalEvatAmount);
                            reCompute();
                            paymentTable.refresh();
                            return;
                        }
                        found = true;
                        os.setAmount(fee);
                    }

                    if(os.getDescription().equals("EVAT")){
                        os.setAmount(originalEvatAmount + (3.6 * Double.parseDouble(serviceFeeAccounts.getText())));
                    }
                }
                if(!found){
                    ORItemSummary orItemSummary = new ORItemSummary(serviceFee.getAccountCode(), serviceFee.getParticulars(), fee);
                    OR_itemList.add(orItemSummary);
                    paymentTable.setItems(OR_itemList);
                }

                reCompute();
                paymentTable.refresh();
            }
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "Can not add service fee information, due to: " + e.getMessage() ,
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }


    @FXML
    private void submitForConfirmation(ActionEvent event) throws Exception {
        //if(!submitBtn.isDisable())
            //checkInputs();
        if(paymentTable.getItems().isEmpty()){
            AlertDialogBuilder.messgeDialog("System Message", "No item breakdown found",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }


        if(customerFromCRM == null && tellerInfo == null && name.getText().isEmpty() && address.getText().isEmpty() && purpose.getText().isEmpty()){

            AlertDialogBuilder.messgeDialog("System Message", "Please select/set consumer or teller information.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }



        if(!(Double.parseDouble(total.getText().replaceAll(",","")) > 0)){
            AlertDialogBuilder.messgeDialog("System Message", "Total amount is zero (0), cannot process transaction.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(orNumber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "OR number is required.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(paymentMode.getSelectionModel().getSelectedItem()==null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select Payment Mode and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(paymentMode.getSelectionModel().getSelectedItem().equals("CASH IN BANK")) {
            if (bankInfo.getSelectionModel().getSelectedItem() == null) {
                AlertDialogBuilder.messgeDialog("System Message", "Please select bank and try again.",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                return;
            }
        }

        OROtherInfo orOtherInfo;
        if(paymentMode.getSelectionModel().getSelectedItem().equals("CASH IN BANK")) {
            orOtherInfo = new OROtherInfo(
                    paymentMode.getSelectionModel().getSelectedItem(),
                    bankInfo.getSelectionModel().getSelectedItem().getAccountCode(),
                    tinNo.getText(),
                    orNumber.getText(),
                    remarks.getText());
        }else{
            orOtherInfo = new OROtherInfo(
                    paymentMode.getSelectionModel().getSelectedItem(),
                    tinNo.getText(),
                    orNumber.getText(),
                    remarks.getText());
        }
        nonePowerBills(orOtherInfo);
    }

    @FXML

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
        if(customerFromCRM != null) {
            transactionHeader.setSource(crmQueue.getSource());
            transactionHeader.setAccountID(crmQueue.getSourseId());//CRM Source ID
            transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
            transactionHeader.setTransactionDate(date.getValue());
            transactionHeader.setAmount(collectionFromCRM);
        }else if(tellerInfo != null) {
            transactionHeader.setSource("Billing");
            transactionHeader.setEnteredBy(tellerInfo.getUsername());
            transactionHeader.setAccountID(ActiveUser.getUser().getId());
            transactionHeader.setAmount(collectionFromTeller);
            transactionHeader.setTransactionDate(tellerInfo.getDate());
        }else{
            transactionHeader.setSource("Normal Customer");
            transactionHeader.setEnteredBy(ActiveUser.getUser().getUserName());
            transactionHeader.setAccountID(ActiveUser.getUser().getId());
            transactionHeader.setAmount(collectionFromTeller);
            transactionHeader.setTransactionDate(date.getValue());
        }
        //transactionHeader.setParticulars("N/A");
        transactionHeader.setTinNo(tinNo);
        transactionHeader.setRemarks(purpose.getText()+", "+this.remarks.getText()+", "+remarks);
        transactionHeader.setName(name.getText());
        transactionHeader.setAddress(address.getText());
        if(manualORToggle.isSelected()){
            transactionHeader.setTransactionLog(Utility.MANUAL_ENTRY);
        }else {
            transactionHeader.setTransactionLog(Utility.SYSTEM_GENERATED);
        }
        //transactionHeader.setBank("N/A");
        //transactionHeader.setReferenceNo("N/A");

        transactionHeader.setDateEntered(LocalDateTime.now());

        //check Period, TransactionNumber(OR number), TransactionCode(main or sub)
        if(TransactionHeaderDAO.isAvailable(transactionHeader.getPeriod(), transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode())){
            AlertDialogBuilder.messgeDialog("System Message", "OR number is already used.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            List<TransactionDetails> transactionDetailsList = new ArrayList<>();
            if(crmDetails != null) {

                CRMDetails getOne = crmDetails.get(0);
                CRMDetails grandTotal = new CRMDetails();
                grandTotal.setReferenceNo(getOne.getReferenceNo());
                grandTotal.setParticulars("Grand Total");
                grandTotal.setTotal(collectionFromCRM);
                crmDetails.add(grandTotal);
                int seq = 1;
                for (CRMDetails cd : crmDetails) {
                    TransactionDetails transactionDetails = new TransactionDetails();
                    transactionDetails.setPeriod(period);
                    transactionDetails.setTransactionNumber(orNumber);
                    transactionDetails.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
                    transactionDetails.setTransactionDate(date.getValue());

                    transactionDetails.setParticulars(cd.getParticulars());
                    transactionDetails.setSequenceNumber(seq++);

                    if(cd.getParticulars().equals("Grand Total")){
                        transactionDetails.setAccountCode(Utility.getAccountCodeProperty());
                        transactionDetails.setDebit(cd.getTotal());
                    }else{
                        if (cd.getTotal() > 0)
                            transactionDetails.setCredit(cd.getTotal());
                        else
                            transactionDetails.setDebit(cd.getTotal());

                        transactionDetails.setAccountCode(cd.getGlCode());//GL Code or the account code of the particular
                    }

                    transactionDetails.setOrDate(date.getValue());
                    //transactionDetails.setBankID("N/A");
                    //transactionDetails.setNote("N/A");
                    transactionDetailsList.add(transactionDetails);
                }
            }else { //for teller and walk-in customer
                int seq = 1;
                ObservableList<ORItemSummary> temp = paymentTable.getItems();
                for (ORItemSummary orItemSummary : temp) {
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

                /*if(orItemSummary.getDescription().equals("Grand Total")){
                    transactionDetails.setDebit(orItemSummary.getAmount());
                }else{*/
                    if (orItemSummary.getAmount() > 0)
                        transactionDetails.setCredit(orItemSummary.getAmount());
                    else
                        transactionDetails.setDebit(orItemSummary.getAmount());
                    //}


                    transactionDetails.setOrDate(date.getValue());
                    //transactionDetails.setBankID("N/A");
                    //transactionDetails.setNote("N/A");
                    transactionDetailsList.add(transactionDetails);
                }

                //Grand total transaction detail item
                TransactionDetails transactionDetails = new TransactionDetails();
                transactionDetails.setPeriod(period);
                transactionDetails.setTransactionNumber(orNumber);
                transactionDetails.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
                transactionDetails.setTransactionDate(date.getValue());
                transactionDetails.setAccountCode(Utility.getAccountCodeProperty());
                transactionDetails.setParticulars("Grand Total");
                transactionDetails.setSequenceNumber(seq++);
                transactionDetails.setDebit(collectionFromTeller);
                transactionDetails.setOrDate(date.getValue());
                transactionDetailsList.add(transactionDetails);

            }



            ORContent orContent = new ORContent(crmQueue, transactionHeader, transactionDetailsList);
            orContent.setAddress(address.getText());
            orContent.setDate(date.getValue());
            orContent.setOrNumber(orNumber);
            orContent.setIssuedTo(name.getText());
            EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
            orContent.setIssuedBy(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
            orContent.setTotal(Double.parseDouble(total.getText().replaceAll(",","")));
            if(customerFromCRM !=null){
                orContent.setCustomerCollection(paymentTable.getItems());
            }else{
                orContent.setTellerCollection(paymentTable.getItems());
            }

            Utility.setOrContent(orContent);
            ModalBuilder.showModalFromXMLNoClose(ORLayoutController.class, "../cashiering/orLayout.fxml", Utility.getStackPane());
        }
    }

    private void bindParticularAccountInfoAutocomplete(JFXTextField textField){
        AutoCompletionBinding<ParticularsAccount> suggestion = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<ParticularsAccount> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() >= 1){
                        try {
                            list = ParticularsAccountDAO.get(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(ParticularsAccount object) {
                        return object.getParticulars() ;
                    }

                    @Override
                    public ParticularsAccount fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        suggestion.setOnAutoCompleted(event -> {
            particularsAccount = event.getCompletion();
            orItemAmount.requestFocus();
        });
    }

    private void bindEmployeeInfoAutocomplete(JFXTextField textField){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else{
                        tellerInfo = null;
                        customerFromCRM = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(EmployeeInfo object) {
                        return object.getEmployeeFirstName() + " "+ object.getEmployeeLastName();
                    }

                    @Override
                    public EmployeeInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            try {
                EmployeeInfo employeeInfo = event.getCompletion();
                User user = UserDAO.get(employeeInfo.getId());
                tellerInfo = new Teller(user.getUserName(), employeeInfo.getSignatoryNameFormat(),employeeInfo.getEmployeeAddress(),employeeInfo.getPhone(), date.getValue());
                this.name.setText(tellerInfo.getName());
                this.date.setValue(date.getValue());
                this.address.setText(tellerInfo.getAddress());
                this.purpose.setText("for customer O.R");
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }


}