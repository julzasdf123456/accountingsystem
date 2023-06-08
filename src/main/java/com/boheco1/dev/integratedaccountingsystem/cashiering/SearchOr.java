package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SearchOr extends MenuControllerHandler implements Initializable {

    @FXML
    private DatePicker transactionDate;

    @FXML
    private JFXComboBox<String> transCode;

    @FXML
    private JFXTextField orNumber;

    @FXML
    private JFXTextField name;

    @FXML
    private JFXTextField address;

    @FXML
    private TableView orTable;

    @FXML
    private Label totalAmount;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        transCode.getItems().add("OR");
        transCode.getItems().add("ORSub");
    }

    @FXML
    void searchOR(ActionEvent event) throws Exception {
        String searchOr = orNumber.getText();//temporary store search string before reset and clear all field
        if(searchOr.isEmpty() || transactionDate.getValue()==null || transCode.getSelectionModel().isEmpty())
            return;


        orNumber.setText(searchOr);
        TransactionHeader transactionHeader = TransactionHeaderDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());
        if(transactionHeader == null){
            AlertDialogBuilder.messgeDialog("System Message", "Transaction info not found.",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            List<TransactionDetails> transactionDetails = TransactionDetailsDAO.get(searchOr,transCode.getSelectionModel().getSelectedItem(), transactionDate.getValue());

            ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetails);
            orTable.setItems(result);
            name.setText(transactionHeader.getName());
            address.setText(transactionHeader.getAddress());
            totalAmount.setText("Total Amount: "+Utility.formatDecimal(transactionHeader.getAmount()));
        }

    }

    private void initTable(){

        TableColumn<TransactionDetails, String> orTablecolumn0= new TableColumn<>("Account Code");
        orTablecolumn0.setMinWidth(180);
        orTablecolumn0.setStyle("-fx-alignment: center-left;");
        orTablecolumn0.setCellValueFactory(new PropertyValueFactory<>("accountCode"));

        TableColumn<TransactionDetails, String> orTablecolumn1 = new TableColumn<>("Item Description");
        orTablecolumn1.setMinWidth(350);
        orTablecolumn1.setStyle("-fx-alignment: center-left;");
        orTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particularsLabel"));

        TableColumn<TransactionDetails, String> orTablecolumn2 = new TableColumn<>("Total Amount");
        orTablecolumn2.setMinWidth(180);
        orTablecolumn2.setStyle("-fx-alignment: center-right;");
        orTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmountView()));


        this.orTable.getColumns().add(orTablecolumn0);
        this.orTable.getColumns().add(orTablecolumn1);
        this.orTable.getColumns().add(orTablecolumn2);
    }
}
