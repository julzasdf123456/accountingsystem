package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.SupplierDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

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
    private JFXTextField orNumber, particular, amount, searchSupplier;

    @FXML
    private Label totalDisplay;

    @FXML
    private TableView<ORItemSummary> itemTable;
    private SupplierInfo supplier;
    private ObservableList<ORItemSummary> observableList;
    private double totalAmount;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindSupplierInfoAutocomplete(searchSupplier);
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

        itemTable.getColumns().add(particularColumn);
        itemTable.getColumns().add(amountColumn);
        itemTable.setPlaceholder(new Label("No item Added"));
        observableList =  FXCollections.observableArrayList();
        itemTable.setItems(observableList);
    }
    @FXML
    private void getParticular(ActionEvent event) {
        if(!particular.getText().isEmpty()){
            if(!amount.getText().isEmpty() && !particular.getText().isEmpty()){
                ORItemSummary orItemSummary = new ORItemSummary(particular.getText(), Double.parseDouble(amount.getText()));
                observableList.add(orItemSummary);
                itemTable.refresh();
                particular.requestFocus();
                particular.setText("");
                amount.setText("");
                totalAmount+=orItemSummary.getAmount();
                totalDisplay.setText(Utility.formatDecimal(totalAmount));
            }else{
                amount.requestFocus();
            }
        }
    }

    @FXML
    private void getAmount(ActionEvent event) {
        if(!amount.getText().isEmpty()){
            if(!amount.getText().isEmpty() && !particular.getText().isEmpty()){
                ORItemSummary orItemSummary = new ORItemSummary(particular.getText(), Double.parseDouble(amount.getText()));
                observableList.add(orItemSummary);
                itemTable.refresh();
                particular.requestFocus();
                particular.setText("");
                amount.setText("");
                totalAmount+=orItemSummary.getAmount();
                totalDisplay.setText(Utility.formatDecimal(totalAmount));
            }else{
                particular.requestFocus();
            }
        }
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

        List<TransactionDetails> transactionDetailsList = new ArrayList<>();
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

            if(items.getAmount() > 0)
                transactionDetails.setCredit(items.getAmount());
            else
                transactionDetails.setDebit(items.getAmount());

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
