package com.boheco1.dev.integratedaccountingsystem.cashiering;

import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDefinitionDao;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionDetailsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.TransactionHeaderDetailDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.ParticularsAccount;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDefinition;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionDetails;
import com.boheco1.dev.integratedaccountingsystem.objects.TransactionHeader;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;


public class CancelOrController extends MenuControllerHandler implements Initializable {
    @FXML
    private DatePicker transactionDate;

    @FXML
    private JFXComboBox<TransactionDefinition> transCode;

    @FXML
    private JFXTextField orNumber;
    @FXML
    private JFXTextArea note;
    @FXML
    private TableView orTable;

    TransactionHeader transactionHeader = null;

    List<TransactionDetails> transactionDetails = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initTable();
        try {
            List<TransactionDefinition> transactionDefinitions =  TransactionDefinitionDao.get();
            for(TransactionDefinition td : transactionDefinitions){
                transCode.getItems().add(td);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void searchBtn(ActionEvent event) throws Exception {
        transactionHeader = TransactionHeaderDAO.get(orNumber.getText(),transCode.getSelectionModel().getSelectedItem().getTransactionCode(), transactionDate.getValue());
        transactionDetails = TransactionDetailsDAO.get(orNumber.getText(),transCode.getSelectionModel().getSelectedItem().getTransactionCode(), transactionDate.getValue());

        ObservableList<TransactionDetails> result = FXCollections.observableArrayList(transactionDetails);
        orTable.setItems(result);
    }

    @FXML
    void submitBtn(ActionEvent event) throws Exception {
        try{
            if(!note.getText().isEmpty()){
                JFXButton accept = new JFXButton("Accept");
                JFXDialog dialog = DialogBuilder.showConfirmDialog("Confirm Transaction.","Are you sure you want to cancel this O.R transaction?", accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
                accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
                accept.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent __) {
                        try {
                            transactionHeader.setRemarks(note.getText());
                            TransactionHeaderDetailDAO.cancelOR(transactionHeader);
                            reset();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                        dialog.close();
                    }
                });
            }else{
                AlertDialogBuilder.messgeDialog("System Message", "Please provide a reason for canceling this O.R transaction.",
                        Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }

        }catch (Exception e){
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: "+ e.getMessage(),
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void initTable(){

        TableColumn<TransactionDetails, String> orTablecolumn1 = new TableColumn<>("Item Description");
        orTablecolumn1.setMinWidth(250);
        orTablecolumn1.setStyle("-fx-alignment: center-left;");
        orTablecolumn1.setCellValueFactory(new PropertyValueFactory<>("particularsLabel"));

        TableColumn<TransactionDetails, String> orTablecolumn2 = new TableColumn<>("Total Amount");
        orTablecolumn2.setMinWidth(200);
        orTablecolumn2.setStyle("-fx-alignment: center-right;");
        orTablecolumn2.setCellValueFactory(obj-> new SimpleStringProperty(obj.getValue().getAmountView()));


        this.orTable.getColumns().add(orTablecolumn1);
        this.orTable.getColumns().add(orTablecolumn2);
    }

    private void reset(){
        orTable.getItems().clear();
        transCode.getItems().clear();
        orNumber.setText("");
        note.setText("");
    }


}
