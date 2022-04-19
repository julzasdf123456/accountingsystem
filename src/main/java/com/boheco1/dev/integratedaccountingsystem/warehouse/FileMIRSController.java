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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class FileMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private DatePicker date;

    @FXML
    private JFXTextField workOrderNum, mirsNum, requisitioner, dm, gm, particulars, quantity;

    @FXML
    private Label workOrderNum1, mirsNum1, date1, requisitioner1;

    @FXML
    private JFXTextArea purpose, details, remark;

    @FXML
    private TextArea purpose1, details1;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private TableView<MIRSItem> particularsTable, requestedItemsTable;

    @FXML
    private TableColumn<MIRSItem, String> particularsCol, unitCol, remarksCol, actionCol;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, particularsCol1, unitCol1, remarksCol1;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol1;

    @FXML
    private JFXButton addBtn, requestBtn, removeBtn;

    @FXML
    private TabPane tabPane;

    private Stock selectedStock = null;
    private EmployeeInfo requisitionerEmployee = null;
    private User userSignatory = null;
    private List<Signatory> signatories = null;
    private ObservableList<MIRSItem> selectedItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        date.setValue(LocalDate.now());
        bindParticularsAutocomplete(particulars);
        bindRequisitionerAutocomplete(requisitioner);
        bindDMAutocomplete(dm);
        //InputValidation.restrictNumbersOnly(mirsNum);
        //InputValidation.restrictNumbersOnly(workOrderNum);
        InputValidation.restrictNumbersOnly(quantity);
        initializeItemTable();
    }

    private void resetInputFields() {
        selectedStock = null;
        requisitionerEmployee = null;
        selectedItem.clear();
        particularsTable.getItems().clear();
        requestedItemsTable.getItems().clear();
        particularsTable.getItems().clear();
        mirsNum.setText("");
        requisitioner.setText("");
        purpose.setText("");
        details.setText("");
        particulars.setText("");
        quantity.setText("");
        remark.setText("");
        tabPane.getSelectionModel().select(0);
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
            mirsItem.setRemarks(remark.getText());
            mirsItem.setWorkOrderNo(workOrderNum.getText());

            selectedStock = null; //set to null for validation
            selectedItem.add(mirsItem);
            particularsTable.setItems(selectedItem);

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
    private void selectedTab(MouseEvent event) {
        try{
            if(tabPane.getSelectionModel().getSelectedIndex() == 1){
                if(workOrderNum.getText().isEmpty() || mirsNum.getText().isEmpty()
                || requisitionerEmployee == null || purpose.getText().isEmpty() ){
                    AlertDialogBuilder.messgeDialog("System Message", "Please complete all the MIRS details on Step 1.",
                            stackPane, AlertDialogBuilder.WARNING_DIALOG);
                    tabPane.getSelectionModel().select(0);
                }
            }else if(tabPane.getSelectionModel().getSelectedIndex() == 2){
                workOrderNum1.setText(workOrderNum.getText());
                mirsNum1.setText(mirsNum.getText());
                date1.setText(""+date.getValue());
                purpose1.setText(purpose.getText());
                details1.setText(details.getText());

                codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));
                particularsCol1.setCellValueFactory(new PropertyValueFactory<>("Particulars"));
                unitCol1.setCellValueFactory(new PropertyValueFactory<>("Unit"));
                unitCol1.setStyle("-fx-alignment: center;");
                quantityCol1.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
                quantityCol1.setStyle("-fx-alignment: center;");
                remarksCol1.setCellValueFactory(new PropertyValueFactory<>("Remarks"));
                requestedItemsTable.setItems(selectedItem);

                requisitioner1.setText(requisitioner.getText());

                setSignatories();
            }

        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
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
        }else if(selectedItem.isEmpty()){
            AlertDialogBuilder.messgeDialog("Invalid Input", "No request item found.",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        try {
            List<MIRSItem> mirsItemList = selectedItem; //from ObservableList to List
            MIRS mirs = new MIRS();
            mirs.setId(mirsNum.getText()); //id mean MIRS number from user input
            mirs.setDetails(details.getText());
            mirs.setStatus(Utility.PENDING);
            mirs.setDateFiled(date.getValue());
            mirs.setPurpose(purpose.getText());
            mirs.setRequisitionerID(requisitionerEmployee.getId());
            mirs.setUserID(ActiveUser.getUser().getId());

            MirsDAO.create(mirs); //add a new MIRS to the database

            MirsDAO.addMIRSItems(mirs, mirsItemList); //add the items request from the MIRS filled

            for(Signatory s : signatories){
                MIRSSignatory mirsSignatory = new MIRSSignatory();
                mirsSignatory.setMirsID(mirs.getId());
                mirsSignatory.setUserID(s.getUserID());
                mirsSignatory.setStatus(Utility.PENDING);
                mirsSignatory.setComments("");
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
        particularsCol.setCellValueFactory(new PropertyValueFactory<>("Particulars"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("Unit"));
        unitCol.setStyle("-fx-alignment: center;");
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        quantityCol.setStyle("-fx-alignment: center;");
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));
        remarksCol.setStyle("-fx-alignment: center-left;");
        actionCol.setStyle("-fx-alignment: center;");
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> removeBtn
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                            Button btn = new Button("");
                            FontIcon icon = new FontIcon("mdi2d-delete");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                btn.setStyle("-fx-background-color: #f44336");
                                btn.setGraphic(icon);
                                btn.setGraphicTextGap(5);
                                btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        try {
                                            selectedItem.remove(mirsItem);
                                            particularsTable.setItems(selectedItem);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    });
                                    setGraphic(btn);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        actionCol.setCellFactory(removeBtn);

        selectedItem =  FXCollections.observableArrayList();
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
                for(MIRSItem added: selectedItem){
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
                e.printStackTrace();
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
