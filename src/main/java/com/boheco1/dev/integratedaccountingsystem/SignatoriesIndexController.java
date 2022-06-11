package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.SignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class SignatoriesIndexController extends MenuControllerHandler implements Initializable {

    @FXML
    StackPane signatoriesIndexStackPane;

    @FXML
    TableView<SignatoryItem> signatoryItemsTable;
    @FXML
    JFXRadioButton pendingRadio;
    @FXML
    JFXRadioButton approvedRadio;
    @FXML
    AnchorPane detailsPane;
    @FXML
    Label mirsIDValue;
    @FXML Label dateFiledValue;
    @FXML Label workOrderNoValue;
    @FXML Label applicantValue;
    @FXML Label addressValue;
    @FXML Label requisitionerValue;
    @FXML Label purposeValue;
    @FXML Label detailsValue;
    @FXML Label signatoryValue;
    @FXML
    JFXButton transferButton;
    @FXML JFXTextField transferToField;

    List<SignatoryItem> signatories;

    private EmployeeInfo transferToEmployee = null;
    private SignatoryItem selectedSignatoryItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            TableColumn<SignatoryItem, String> mirsIDCol = new TableColumn("MIRS ID");
            mirsIDCol.setMinWidth(150);
            mirsIDCol.setCellValueFactory(new PropertyValueFactory<>("mirsID"));

            TableColumn<SignatoryItem, String> signatoryCol = new TableColumn("Signatory");
            signatoryCol.setMinWidth(270);
            signatoryCol.setCellValueFactory(new PropertyValueFactory<>("signatoryName"));

            TableColumn<SignatoryItem, String> statusCol = new TableColumn("Status");
            statusCol.setMinWidth(150);
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            signatoryItemsTable.getColumns().removeAll();
            signatoryItemsTable.getColumns().add(mirsIDCol);
            signatoryItemsTable.getColumns().add(signatoryCol);
            signatoryItemsTable.getColumns().add(statusCol);

            refreshSignatories();

            EventHandler<javafx.event.ActionEvent> radioEventHandler = new EventHandler<>() {
                @Override
                public void handle(javafx.event.ActionEvent actionEvent) {
                    refreshSignatories();
                }
            };

            pendingRadio.setOnAction(radioEventHandler);
            approvedRadio.setOnAction(radioEventHandler);

            transferButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    try {
                        SignatoryDAO.transferSignatory(selectedSignatoryItem.getId(), transferToEmployee.getId());
                        selectedSignatoryItem.setSignatoryName(transferToEmployee.getFullName());
                        signatoryValue.setText(transferToEmployee.getFullName());
                        transferToField.setText(null);
                        refreshSignatories();
                        AlertDialogBuilder.messgeDialog("Transfer Complete!", "The signatory has been transferred successfully", signatoriesIndexStackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                    }catch(Exception ex) {
                        AlertDialogBuilder.messgeDialog("Application Error", "Error while transferring signatories: " + ex.getMessage(), signatoriesIndexStackPane, AlertDialogBuilder.DANGER_DIALOG);
                        ex.printStackTrace();
                    }
                }
            });

            signatoryItemsTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<SignatoryItem>() {
                @Override
                public void changed(ObservableValue<? extends SignatoryItem> observableValue, SignatoryItem signatoryItem, SignatoryItem t1) {
                    if(t1==null) return;
                    selectedSignatoryItem = t1;
                    try{
                        detailsPane.setVisible(true);
                        MIRS mirs = MirsDAO.getMIRS(t1.getMirsID());

                        mirsIDValue.setText(mirs.getId());
                        dateFiledValue.setText(mirs.getDateFiled().format(DateTimeFormatter.BASIC_ISO_DATE));
                        workOrderNoValue.setText(mirs.getWorkOrderNo());
                        applicantValue.setText(mirs.getApplicant());
                        addressValue.setText(mirs.getAddress());
                        requisitionerValue.setText(mirs.getRequisitioner().getFullName());
                        purposeValue.setText(mirs.getPurpose());
                        detailsValue.setText(mirs.getDetails());
                        signatoryValue.setText(t1.getSignatoryName());
                    }catch(Exception ex) {
                        AlertDialogBuilder.messgeDialog("Application Error", "Error while selecting table row: " + ex.getMessage(), signatoriesIndexStackPane, AlertDialogBuilder.DANGER_DIALOG);
                        ex.printStackTrace();
                    }
                }
            });

            bindEmployeeAutocomplete(transferToField);

        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Initialization Error", ex.getMessage(), signatoriesIndexStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void refreshSignatories() {
        String status = pendingRadio.isSelected() ? "pending" : "approved";

        try {
            signatories = SignatoryDAO.getSignatoryItems(status);
            ObservableList<SignatoryItem> items = FXCollections.observableArrayList(signatories);
            signatoryItemsTable.getItems().setAll(items);
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Error building users table", "Error building users table: " + ex.getMessage(), signatoriesIndexStackPane, AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void bindEmployeeAutocomplete(JFXTextField textField){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 2){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

//                    if (list.size() == 0) {
//                        textField.setText("");
//                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(EmployeeInfo object) {
                        return object.getFullName();
                    }

                    @Override
                    public EmployeeInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            EmployeeInfo employee = event.getCompletion();
            textField.setText(employee.getFullName());
            transferToEmployee = employee;
        });
    }
}
