package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
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
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private VBox tablePane, signaturePane;

    @FXML
    private StackPane stackPane;

    @FXML
    private DatePicker date;

    @FXML
    private JFXTextField mirsNum, requisitioner, dm, gm, particulars, remarks, quantity;

    @FXML
    private JFXTextArea purpose, details;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private TableView<MIRSItem> particularsTable;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, particularsCol, unitCol, remarksCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;

    @FXML
    private JFXButton addBtn, requestBtn, removeBtn;

    private Stock selectedStock = null;
    private EmployeeInfo requisitionerEmployee = null;
    private User userSignatory = null;
    private List<Signatory> signatories = null;
    private ObservableList<MIRSItem> requestItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        date.setValue(LocalDate.now());
        bindParticularsAutocomplete(particulars);
        bindRequisitionerAutocomplete(requisitioner);
        bindDMAutocomplete(dm);
        InputValidation.restrictNumbersOnly(mirsNum);
        InputValidation.restrictNumbersOnly(quantity);
        setSignatories();
        initializeItemTable();
    }

    private void resetInputFields() {
        selectedStock = null;
        requisitionerEmployee = null;
        particularsTable.getItems().clear();
        mirsNum.setText("");
        requisitioner.setText("");
        purpose.setText("");
        details.setText("");
        particulars.setText("");
        quantity.setText("");
        remarks.setText("");
        tablePane.setDisable(true);
        signaturePane.setDisable(true);
    }

    @FXML
    private void addBtn(ActionEvent event) {
        try{
            if(selectedStock == null){
                AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
                return;
            }else if(Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
                AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
                return;
            }

            MIRSItem mirsItem = new MIRSItem();
            mirsItem.setMirsID(mirsNum.getText());
            mirsItem.setStockID(selectedStock.getId());
            mirsItem.setParticulars(selectedStock.getStockName());
            mirsItem.setUnit(selectedStock.getUnit());
            mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
            mirsItem.setPrice(selectedStock.getPrice());
            mirsItem.setRemarks(remarks.getText());

            selectedStock = null; //set to null for validation
            requestItem.add(mirsItem);
            particularsTable.setItems(requestItem);

            particulars.setText("");
            quantity.setText("");
            particulars.requestFocus();
            inStock.setText("In Stock: 0");
            pending.setText("Pending: 0");
            available.setText("Available: 0");
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void removeBtn(ActionEvent event) {
        MIRSItem selected = particularsTable.getSelectionModel().getSelectedItem();
        for (MIRSItem m: requestItem) {
            if(m.equals(selected)){
                requestItem.remove(m);
                break;
            }
        }
        particularsTable.setItems(requestItem);
    }

    @FXML
    private void requestBtn(ActionEvent event) {
        if(mirsNum.getText().isEmpty()){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid MIRS Number!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if (requisitionerEmployee == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid Requisitioner!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(purpose.getText().isEmpty()){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid Purpose!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(requestItem.isEmpty()){
            AlertDialogBuilder.messgeDialog("Invalid Input", "No request item found.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        try {
            List<MIRSItem> mirsItemList = requestItem; //from ObservableList to List
            MIRS mirs = new MIRS();
            mirs.setId(mirsNum.getText()); //id mean MIRS number from user input
            mirs.setDetails(details.getText());
            mirs.setStatus("Pending");
            mirs.setDateFiled(date.getValue());
            mirs.setPurpose(purpose.getText());
            mirs.setRequisitionerID(requisitionerEmployee.getId());
            mirs.setUserID(ActiveUser.getUser().getId());

            MirsDAO.create(mirs); //add a new MIRS to the database

            MirsDAO.addMIRSItems(mirs, mirsItemList); //add the items request from the MIRS filled

            for(MIRSItem mirsItem : mirsItemList){
                Stock request = StockDAO.get(mirsItem.getStockID());
                //StockDAO.deductStockQuantity(request, mirsItem.getQuantity());
            }

            for(Signatory s : signatories){
                MIRSSignatory mirsSignatory = new MIRSSignatory();
                mirsSignatory.setMirsID(mirs.getId());
                mirsSignatory.setUserID(s.getUserID());
                mirsSignatory.setStatus(null);
                mirsSignatory.setComments(null);
                MIRSSignatoryDAO.add(mirsSignatory); //saving signatories for the MIRS request
            }
            AlertDialogBuilder.messgeDialog("System Message", "MIRS request successfully filed, please wait for the approval, thank you!",
                    stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
            resetInputFields();
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void initializeItemTable() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));
        particularsCol.setCellValueFactory(new PropertyValueFactory<>("Particulars"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("Unit"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));

        requestItem =  FXCollections.observableArrayList();
        particularsTable.setPlaceholder(new Label("No item Added"));
    }

    private void setSignatories(){
        try {
            signatories = SignatoryDAO.getSignatories("MIRS");

            User user1 = UserDAO.get(signatories.get(0).getUserID());
            dm.setText(user1.getFullName());

            User user2 = UserDAO.get(signatories.get(1).getUserID());
            gm.setText(user2.getFullName());

        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", "setSignatories(): "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private void bindParticularsAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SlimStock> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SlimStock> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.search(query, 0);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        selectedStock = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(SlimStock object) {
                        return object.getStockName();
                    }

                    @Override
                    public SlimStock fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            SlimStock result = event.getCompletion();
            try {
                selectedStock = StockDAO.get(result.getId());
                int av = StockDAO.countAvailable(selectedStock);
                if(av == 0) {
                    AlertDialogBuilder.messgeDialog("System Warning", "Insufficient stock.",
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    return;
                }
                for(MIRSItem added: requestItem){
                    if(added.getStockID() == result.getId()){
                        AlertDialogBuilder.messgeDialog("System Warning", "Item already added, please remove item then add again if you have changes.",
                                stackPane, AlertDialogBuilder.WARNING_DIALOG);
                        particulars.setText("");
                        quantity.setText("");
                        particulars.requestFocus();
                        inStock.setText("In Stock: 0");
                        pending.setText("Pending: 0");
                        available.setText("Available: 0");
                        return;
                    }
                }

                inStock.setText("In Stock: "+ selectedStock.getQuantity());
                pending.setText("Pending: "+ StockDAO.countPendingRequest(selectedStock));
                available.setText("Available: "+ av);
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    private void bindRequisitionerAutocomplete(JFXTextField textField){
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
                    }

                    if(query.length() == 0){
                        tablePane.setDisable(true);
                        signaturePane.setDisable(true);
                    }

                    if (list.size() == 0) {
                        requisitionerEmployee = null;
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
            requisitionerEmployee = event.getCompletion();
            tablePane.setDisable(false);
            signaturePane.setDisable(false);
            requisitioner.setText(requisitionerEmployee.getFullName());
            purpose.requestFocus();
        });
    }

    private void bindDMAutocomplete(JFXTextField textField){
        AutoCompletionBinding<User> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<User> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = UserDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        userSignatory = null;
                    }

                    return list;
                }, new StringConverter<>() {
                    //This governs what appears on the popupmenu. The given code will let the stockName appear as items in the popupmenu.
                    @Override
                    public String toString(User object) {
                        return object.getFullName();
                    }

                    @Override
                    public User fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            userSignatory = event.getCompletion();
            signatories.get(0).setUserID(userSignatory.getId());
            dm.setText(userSignatory.getFullName());
        });
    }
}
