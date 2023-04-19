package com.boheco1.dev.integratedaccountingsystem.finance;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import static com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO.addUpdate;

public class ORUpdateController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField orNumber;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField address;

    @FXML
    private TableView orTable;

    @FXML
    private TableView newItemTable;

    @FXML
    private JFXTextField orItem;

    @FXML
    private JFXTextField orItemAmount;

    @FXML
    private JFXTextField newItemDescription;

    @FXML
    private JFXTextField newItemAmount;

    private TransactionDetails selectedItem;
    TransactionHeader transactionHeader = null;
    List<TransactionDetails> transactionDetails = null;
    List<TransactionDetails> newTransactionDetails = new ArrayList<>();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        tableClick();
        addNewItem(newItemDescription);
        addNewItem(newItemAmount);
        InputValidation.restrictNumbersOnly(newItemAmount);
    }

    private void initTable(){

        TableColumn<TransactionDetails, String> orTablecolumn1 = new TableColumn<>("Item Description");
        orTablecolumn1.setMinWidth(220);
        orTablecolumn1.setStyle("-fx-alignment: center-left;");
        orTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particulars"));

        TableColumn<TransactionDetails, String> orTablecolumn2 = new TableColumn<>("Total Amount");
        orTablecolumn2.setStyle("-fx-alignment: center-right;");
        orTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmount()));

        this.orTable.getColumns().add(orTablecolumn1);
        this.orTable.getColumns().add(orTablecolumn2);


        TableColumn<TransactionDetails, String> newItemTablecolumn1 = new TableColumn<>("Item Description");
        newItemTablecolumn1.setMinWidth(220);
        newItemTablecolumn1.setStyle("-fx-alignment: center-left;");
        newItemTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particulars"));

        TableColumn<TransactionDetails, String> newItemTablecolumn2 = new TableColumn<>("Total Amount");
        newItemTablecolumn2.setStyle("-fx-alignment: center-right;");
        newItemTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty((obj.getValue().getAmount())));

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
                                            double newCredit = selectedItem.getCredit() + data.getCredit();
                                            selectedItem.setCredit(newCredit);
                                            orItem.setText(selectedItem.getParticulars());
                                            orItemAmount.setText(Utility.formatDecimal(selectedItem.getCredit()));
                                            orTable.refresh();
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
            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Confirm Transaction.","Continue saving O.R update?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    try {
                        addUpdate(transactionDetails,newTransactionDetails);
                        reset();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    dialog.close();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: "+ e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void searchOR(ActionEvent event) throws Exception {
        String searchOr = orNumber.getText();//temporary store search string before reset and clear all field
        reset();
        orNumber.setText(searchOr);
        transactionHeader = TransactionHeaderDAO.get(searchOr);
        transactionDetails = TransactionDetailsDAO.get(searchOr);
        fillUpFields();
    }

    private void tableClick(){
        this.orTable.setRowFactory(tv -> {
            TableRow row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    selectedItem = (TransactionDetails) row.getItem();
                    if(selectedItem.getCredit()>0) {
                        orItem.setText(selectedItem.getParticulars());
                        orItemAmount.setText(Utility.formatDecimal(selectedItem.getCredit()));
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
        }
    }

    private void addNewItem(JFXTextField jfxTextField){
        jfxTextField.setOnAction(e -> {
            if(!newItemDescription.getText().isEmpty() && !newItemAmount.getText().isEmpty()){
                double amount = Double.parseDouble(newItemAmount.getText());
                if(amount > selectedItem.getCredit()){
                    AlertDialogBuilder.messgeDialog("System Message", "Insufficient amount, to perform breakdown." ,
                            Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                }else{
                    double newCredit = selectedItem.getCredit() - amount;
                    selectedItem.setCredit(newCredit);

                    orItem.setText(selectedItem.getParticulars());
                    orItemAmount.setText(Utility.formatDecimal(selectedItem.getCredit()));
                    orTable.refresh();

                    TransactionDetails td = new TransactionDetails();
                    td.setPeriod(selectedItem.getPeriod());
                    td.setTransactionNumber(selectedItem.getTransactionNumber());
                    td.setTransactionCode(selectedItem.getTransactionCode());
                    td.setTransactionDate(selectedItem.getTransactionDate());
                    //account sequence will be set upon saving
                    td.setAccountCode(selectedItem.getAccountCode());
                    if(amount > 0){
                        td.setDebit(selectedItem.getDebit());
                        td.setCredit(amount);
                    }else{
                        td.setCredit(selectedItem.getCredit());
                        td.setDebit(amount);
                    }

                    td.setParticulars(selectedItem.getParticulars());
                    td.setOrDate(selectedItem.getOrDate());
                    td.setBankID(selectedItem.getBankID());
                    td.setNote(selectedItem.getNote());
                    td.setCheckNumber(selectedItem.getCheckNumber());
                    td.setParticulars(newItemDescription.getText());
                    td.setDepositedDate(selectedItem.getDepositedDate());

                    newTransactionDetails.add(td);
                    ObservableList<TransactionDetails> result = FXCollections.observableArrayList(newTransactionDetails);
                    newItemTable.setItems(result);
                    newItemTable.refresh();

                    newItemDescription.setText("");
                    newItemAmount.setText("");
                    newItemDescription.requestFocus();
                }
            }
        });
    }
    private void reset(){
        orNumber.setText("");
        name.setText("");
        address.setText("");
        orItem.setText("");
        orItemAmount.setText("");
        newItemDescription.setText("");
        newItemAmount.setText("");
        orTable.getItems().clear();
        newItemTable.getItems().clear();
        transactionHeader = null;
        transactionDetails = null;
        newTransactionDetails.clear();
    }
}
