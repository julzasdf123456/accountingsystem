package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.ParticularsAccountDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SupplierORController extends MenuControllerHandler implements Initializable, ObjectTransaction {
    @FXML
    private DatePicker date;

    @FXML
    private JFXTextArea supplierInfo;

    @FXML
    private JFXTextField orNumber, amount, searchSupplier, particular;

    @FXML
    private Label totalDisplay;

    @FXML
    private TableView<ORItemSummary> itemTable;
    private SupplierInfo supplier;

    ParticularsAccount particularsAccount = null;
    private ObservableList<ORItemSummary> observableList;
    private double totalAmount;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindSupplierInfoAutocomplete(searchSupplier);
        bindParticularAccountInfoAutocomplete(particular);
        initController();
        clearText();
        Utility.setParentController(this);

    }

    private void clearText(){
        if(Utility.OR_NUMBER!=0)
            orNumber.setText(""+Utility.OR_NUMBER);

        totalDisplay.setText("");
        particular.setText("");
        amount.setText("");
        searchSupplier.setText("");
        supplierInfo.setText("");
        observableList.clear();
        itemTable.refresh();
    }

    private void initController(){
        InputValidation.restrictNumbersOnly(amount);

        try {
            date.setValue(Utility.serverDate());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        TableColumn<ORItemSummary, String> particularColumn = new TableColumn<>("Particular");
        particularColumn.setPrefWidth(400);
        particularColumn.setMinWidth(400);
        particularColumn.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<ORItemSummary, Double> amountColumn = new TableColumn<>("Amount");
        amountColumn.setStyle("-fx-alignment: center-right;");
        amountColumn.setPrefWidth(150);
        amountColumn.setMinWidth(150);
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("totalView"));

        TableColumn<ORItemSummary, String> removeColumn = new TableColumn<>(" ");
        removeColumn.setPrefWidth(50);
        removeColumn.setMaxWidth(50);
        removeColumn.setMinWidth(50);
        Callback<TableColumn<ORItemSummary, String>, TableCell<ORItemSummary, String>> removeColCellFactory
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
                                    icon.setIconSize(24);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    btn.setOnAction(event -> {
                                        try{
                                            observableList.remove(data);
                                            //ObservableList<ORItemSummary> result = FXCollections.observableArrayList(observableList);
                                            //itemTable.setItems(result);
                                            itemTable.refresh();
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
        removeColumn.setCellFactory(removeColCellFactory);

        itemTable.getColumns().add(particularColumn);
        itemTable.getColumns().add(amountColumn);
        itemTable.getColumns().add(removeColumn);
        itemTable.setPlaceholder(new Label("No item Added"));
        observableList =  FXCollections.observableArrayList();
        itemTable.setItems(observableList);
    }

    @FXML
    private void getAmount(ActionEvent event) {
        if(!amount.getText().isEmpty()){
            if(!amount.getText().isEmpty() && !particular.getText().isEmpty()){
                ORItemSummary orItemSummary = new ORItemSummary(particularsAccount.getAccountCode(), particularsAccount.getParticulars(), Double.parseDouble(amount.getText()));
                observableList.add(orItemSummary);
                itemTable.refresh();
                particular.requestFocus();
                particular.setText("");
                amount.setText("");
                reCompute();
                itemTable.refresh();
            }else{
                particular.requestFocus();
            }
        }
    }

    private void reCompute() {
        double total = 0;
        for (ORItemSummary t : observableList)
            total += t.getAmount();

        totalAmount=total;
        totalDisplay.setText(Utility.formatDecimal(totalAmount));
    }

    @FXML
    private void printOR(ActionEvent event) throws Exception {
        if(itemTable.getItems().isEmpty()){
            AlertDialogBuilder.messgeDialog("System Message", "No item found",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(supplier == null){
            AlertDialogBuilder.messgeDialog("System Message", "Please select supplier, and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        if(orNumber.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "OR number is required.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        EmployeeInfo employeeInfo = ActiveUser.getUser().getEmployeeInfo();
        int month = date.getValue().getMonthValue();
        int year = date.getValue().getYear();
        LocalDate period = LocalDate.of(year, month, 1);
        TransactionHeader transactionHeader= new TransactionHeader();
        transactionHeader.setPeriod(period);
        transactionHeader.setTransactionNumber(orNumber.getText());
        transactionHeader.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
        transactionHeader.setOffice(Utility.OFFICE_PREFIX);
        transactionHeader.setSource("supplier");
        transactionHeader.setEnteredBy(employeeInfo.getId());
        transactionHeader.setAccountID(supplier.getAccountID());
        transactionHeader.setAmount(totalAmount);
        transactionHeader.setTransactionDate(date.getValue());
        transactionHeader.setTinNo(supplier.getTINNo());
        transactionHeader.setAddress(supplier.getCompanyAddress());
        transactionHeader.setName(supplier.getCompanyName());

        List<TransactionDetails> transactionDetailsList = new ArrayList<>();

        //do not delete and change the description it used during supplier save transaction details
        ORItemSummary grandTotal = new ORItemSummary(Utility.getAccountCodeProperty(),"Grand Total", totalAmount);
        observableList.add(grandTotal);
        int seq =1;
        for(ORItemSummary items : observableList){
            TransactionDetails transactionDetails = new TransactionDetails();
            transactionDetails.setPeriod(period);
            transactionDetails.setTransactionNumber(orNumber.getText());
            transactionDetails.setTransactionCode(TransactionHeader.getORTransactionCodeProperty());
            transactionDetails.setTransactionDate(date.getValue());
            transactionDetails.setParticulars(items.getDescription());
            if(!transactionDetails.getParticulars().contains("TIN"))
                transactionDetails.setSequenceNumber(seq++);
            transactionDetails.setAccountCode(items.getAccountCode());

            if(items.getDescription().equals("Grand Total")){
                transactionDetails.setDebit(items.getAmount());
            }else{
                if (items.getAmount() > 0)
                    transactionDetails.setCredit(items.getAmount());
                else
                    transactionDetails.setDebit(items.getAmount());
            }

            transactionDetails.setOrDate(date.getValue());

            transactionDetailsList.add(transactionDetails);
        }

        ORContent orContent = new ORContent(transactionHeader, transactionDetailsList);
        orContent.setSupplierItems(observableList);
        orContent.setAddress(supplier.getCompanyAddress());
        orContent.setDate(date.getValue());
        orContent.setOrNumber(orNumber.getText());
        orContent.setIssuedTo(supplier.getCompanyName());
        orContent.setIssuedBy(employeeInfo.getEmployeeFirstName().charAt(0)+". "+employeeInfo.getEmployeeLastName());
        orContent.setTotal(totalAmount);


        if(TransactionHeaderDAO.isAvailable(transactionHeader.getPeriod(), transactionHeader.getTransactionNumber(), transactionHeader.getTransactionCode())){
            AlertDialogBuilder.messgeDialog("System Message", "OR number is already used.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
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
        });
    }

    private void bindSupplierInfoAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SupplierInfo> suggestion = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SupplierInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() >= 1){
                        try {
                            list = SupplierDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SupplierInfo object) {
                        return object.getCompanyName() ;
                    }

                    @Override
                    public SupplierInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        suggestion.setOnAutoCompleted(event -> {
            clearText();
            supplier = event.getCompletion();
            searchSupplier.setText(supplier.getCompanyName());
            supplierInfo.setText(supplier.getCompanyAddress());

            if(supplier.getTINNo().isEmpty())
                return;

            ORItemSummary orItemSummary = new ORItemSummary("TIN "+ supplier.getTINNo());//just to display TIN in the table
            observableList.add(orItemSummary);
            itemTable.refresh();
        });
    }

    @Override
    public void receive(Object o) {
        ModalBuilder.MODAL_CLOSE();
        boolean tf = (boolean) o;
        if(tf){
            clearText();
        }
    }
}
