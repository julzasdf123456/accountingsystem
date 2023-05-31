package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;


public class ORUpdateController extends MenuControllerHandler implements Initializable {
    @FXML
    private DatePicker transactionDate;
    @FXML
    private JFXTextField orNumber, particular;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField address;

    @FXML
    private TableView orTable;

    @FXML
    private TableView newItemTable;

    @FXML
    private JFXComboBox<String> transCode;

    @FXML
    private JFXTextField orItem;

    @FXML
    private JFXTextField orItemAmount;

    @FXML
    private Label totalAmount, newTotalAmount;
    @FXML
    private JFXTextField newItemAmount;

    private TransactionDetails selectedItem;
    TransactionHeader transactionHeader = null;

    ParticularsAccount particularsAccount = null;
    List<TransactionDetails> transactionDetails = null;
    List<TransactionDetails> newTransactionDetails = new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        tableClick();
        addNewItem(newItemAmount);
        updateItem(orItem);
        updateItem(orItemAmount);
        InputValidation.restrictNumbersOnly(newItemAmount);
        InputValidation.restrictNumbersOnly(orItemAmount);
        bindParticularAccountInfoAutocomplete(particular);
        totalAmount.setText("");
        newTotalAmount.setText("");

        transactionDate.setPromptText("Transaction Date");

        transCode.getItems().add("OR");
        transCode.getItems().add("ORSub");


    }

    private void initTable(){

        TableColumn<TransactionDetails, String> orTablecolumn1 = new TableColumn<>("Item Description");
        orTablecolumn1.setMinWidth(220);
        orTablecolumn1.setStyle("-fx-alignment: center-left;");
        orTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particularsLabel"));

        TableColumn<TransactionDetails, String> orTablecolumn2 = new TableColumn<>("Total Amount");
        orTablecolumn2.setStyle("-fx-alignment: center-right;");
        orTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmountView()));

        TableColumn<TransactionDetails, String> orTablecolumnremoveCol = new TableColumn<>(" ");
        orTablecolumnremoveCol.setPrefWidth(50);
        orTablecolumnremoveCol.setMaxWidth(50);
        orTablecolumnremoveCol.setMinWidth(50);
        Callback<TableColumn<TransactionDetails, String>, TableCell<TransactionDetails, String>> orTableRemoveColCellFactory
                = //
                new Callback<TableColumn<TransactionDetails, String>, TableCell<TransactionDetails, String>>() {
                    @Override
                    public TableCell call(final TableColumn<TransactionDetails, String> param) {
                        final TableCell<TransactionDetails, String> cell = new TableCell<TransactionDetails, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    TransactionDetails data = getTableView().getItems().get(getIndex());
                                    icon.setIconSize(16);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    btn.setOnAction(event -> {
                                        try{
                                            if(data.getAmount()>0)
                                                data.setCredit(0);
                                            else
                                                data.setDebit(0);
                                            data.setNote("Remove during O.R Update");//do not change this message its use during delete db record
                                            //transactionDetails.remove(data);
                                            ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetails);
                                            orTable.setItems(result);
                                            orTable.refresh();
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
        orTablecolumnremoveCol.setCellFactory(orTableRemoveColCellFactory);

        this.orTable.getColumns().add(orTablecolumn1);
        this.orTable.getColumns().add(orTablecolumn2);
        this.orTable.getColumns().add(orTablecolumnremoveCol);


        TableColumn<TransactionDetails, String> newItemTablecolumn1 = new TableColumn<>("Item Description");
        newItemTablecolumn1.setMinWidth(220);
        newItemTablecolumn1.setStyle("-fx-alignment: center-left;");
        newItemTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particulars"));

        TableColumn<TransactionDetails, String> newItemTablecolumn2 = new TableColumn<>("Total Amount");
        newItemTablecolumn2.setStyle("-fx-alignment: center-right;");
        newItemTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty((obj.getValue().getAmountView())));

        TableColumn<TransactionDetails, String> removeCol = new TableColumn<>(" ");
        removeCol.setPrefWidth(50);
        removeCol.setMaxWidth(50);
        removeCol.setMinWidth(50);
        Callback<TableColumn<TransactionDetails, String>, TableCell<TransactionDetails, String>> removeColCellFactory
                = //
                new Callback<TableColumn<TransactionDetails, String>, TableCell<TransactionDetails, String>>() {
                    @Override
                    public TableCell call(final TableColumn<TransactionDetails, String> param) {
                        final TableCell<TransactionDetails, String> cell = new TableCell<TransactionDetails, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    TransactionDetails data = getTableView().getItems().get(getIndex());
                                    icon.setIconSize(24);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    btn.setOnAction(event -> {
                                        try{
                                            newTransactionDetails.remove(data);
                                            ObservableList<TransactionDetails> result = FXCollections.observableArrayList(newTransactionDetails);
                                            newItemTable.setItems(result);
                                            newItemTable.refresh();
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
        removeCol.setCellFactory(removeColCellFactory);

        this.newItemTable.getColumns().add(newItemTablecolumn1);
        this.newItemTable.getColumns().add(newItemTablecolumn2);
        this.newItemTable.getColumns().add(removeCol);
    }

    @FXML
    private void saveTransaction(ActionEvent event) {
        try{
            if(reCompute()){
                JFXButton accept = new JFXButton("Accept");
                JFXDialog dialog = DialogBuilder.showConfirmDialog("Confirm Transaction.","Continue saving O.R update?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
                accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
                accept.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent __) {
                        try {
                            TransactionDetailsDAO.addUpdate(transactionDetails,newTransactionDetails);
                            reset();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        dialog.close();
                    }
                });
            }else{
                AlertDialogBuilder.messgeDialog("System Message", "New mount did not match the original amount.",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }

        }catch (Exception e){
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: "+ e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void searchOR(ActionEvent event) throws Exception {
        String searchOr = orNumber.getText();//temporary store search string before reset and clear all field
        if(searchOr.isEmpty() || transactionDate.getValue()==null || transCode.getSelectionModel().isEmpty())
            return;


        reset();
        orNumber.setText(searchOr);
        transactionHeader = TransactionHeaderDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());
        transactionDetails = TransactionDetailsDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());
        newTotalAmount.setText("");
        fillUpFields();
        reCompute();
    }

    private void tableClick(){
        this.orTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    selectedItem = (TransactionDetails) row.getItem();
                    if(selectedItem.getAmount()!=0) {
                        orItem.setText(selectedItem.getParticularsLabel());
                        orItemAmount.setText("" + selectedItem.getAmount());
                    }
                }
            });
            return row ;
        });
    }

    private void fillUpFields(){
        if(transactionHeader != null && transactionDetails != null) {
            ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetails);
            orTable.setItems(result);
            name.setText(transactionHeader.getName());
            address.setText(transactionHeader.getAddress());
            totalAmount.setText("Total Amount: "+Utility.formatDecimal(transactionHeader.getAmount()));
        }
    }

    private void updateItem(JFXTextField jfxTextField){
        jfxTextField.setOnAction(e -> {
            if(!orItem.getText().isEmpty() && !orItemAmount.getText().isEmpty()) {
                double amount = Double.parseDouble(orItemAmount.getText());
                if(amount > 0){
                    selectedItem.setCredit(amount);
                }else{
                    selectedItem.setDebit(amount);
                }
                    selectedItem.setNewParticularsLabel(orItem.getText());
                    orTable.refresh();
                    orItem.setText("");
                    orItemAmount.setText("");
                    reCompute();
            }
        });
    }

    private void addNewItem(JFXTextField jfxTextField){
        jfxTextField.setOnAction(e -> {
            if(!particular.getText().isEmpty() && !newItemAmount.getText().isEmpty()){
                double amount = Double.parseDouble(newItemAmount.getText());

                TransactionDetails temp = (TransactionDetails) orTable.getItems().get(0);
                TransactionDetails td = new TransactionDetails();
                td.setPeriod(temp.getPeriod());
                td.setTransactionNumber(temp.getTransactionNumber());
                td.setTransactionCode(temp.getTransactionCode());
                td.setTransactionDate(temp.getTransactionDate());
                //account sequence will be set upon saving

                ParticularsAccount newItem = particularsAccount;
                td.setAccountCode(newItem.getAccountCode());
                if(amount > 0){
                    td.setCredit(amount);
                }else{
                    td.setDebit(amount);
                }
                td.setParticulars(newItem.getParticulars());

                td.setOrDate(temp.getOrDate());
                td.setBankID(temp.getBankID());
                td.setNote(temp.getParticulars()+": "+temp.getNote()+", added as new item during finance update");
                td.setCheckNumber(temp.getCheckNumber());
                td.setDepositedDate(temp.getDepositedDate());

                newTransactionDetails.add(td);
                ObservableList<TransactionDetails> result = FXCollections.observableArrayList(newTransactionDetails);
                newItemTable.setItems(result);
                newItemTable.refresh();
                particular.setText("");
                newItemAmount.setText("");
                particular.requestFocus();
                reCompute();

            }
        });
    }
    private void reset(){
        orNumber.setText("");
        name.setText("");
        address.setText("");
        orItem.setText("");
        orItemAmount.setText("");
        particular.setText("");
        newItemAmount.setText("");
        orTable.getItems().clear();
        newItemTable.getItems().clear();
        transactionHeader = null;
        transactionDetails = null;
        newTransactionDetails.clear();
        totalAmount.setText("");
        newTotalAmount.setText("");
    }

    private boolean reCompute(){

        if(transactionHeader==null){
            AlertDialogBuilder.messgeDialog("System Message", "No result found." ,
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return false;
        }
        double newTotal=0;
        for(TransactionDetails t : transactionDetails)
            newTotal+=t.getAmount();


        for(TransactionDetails t : newTransactionDetails)
            newTotal+=t.getAmount();

        if(!Utility.formatDecimal(newTotal).equals(Utility.formatDecimal(transactionHeader.getAmount())) ){
            newTotalAmount.setText("Amount Difference: "+Utility.formatDecimal(transactionHeader.getAmount() - newTotal));
            return false;
        }else{
            newTotalAmount.setText("");
            return true;
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
}
