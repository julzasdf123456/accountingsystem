package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class UserMyMIRSApplicationController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private DatePicker filingDate;

    @FXML
    private JFXTextField applicant, address, prepared, checked, approved, items, quantity;

    @FXML
    private JFXTextArea purpose, remarks;

    @FXML
    private Label available, inStock, pending, countRow, mirsNumber;

    @FXML JFXButton saveChanges;

    @FXML
    private TableView<MIRSItem> mirsItemTable;

    private Stock selectedStock = null;
    private EmployeeInfo preparedEmployeeInfo = null;
    private EmployeeInfo checkedEmployeeInfo = null;
    private EmployeeInfo approvedEmployeeInfo = null;
    private ObservableList<MIRSItem> selectedItem;

    private MIRS mirs;
    private List<MIRSSignatory> mirsSignatoryList;
    private ObservableList<MIRSItem> mirsItemList;
    private List<MIRSItem> removeMirsItems = new ArrayList<>();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        mirs = Utility.getActiveMIRS();
        bindParticularsAutocomplete(items);
        bindEmployeeInfoAutocomplete(prepared);
        bindEmployeeInfoAutocomplete(checked);
        bindEmployeeInfoAutocomplete(approved);
        InputValidation.restrictNumbersOnly(quantity);
        initializeItemTable();

        try {
            fillUpFields(mirs);
            if(mirs.getStatus().equals(Utility.CLOSED) || mirs.getStatus().equals(Utility.RELEASING)){
                isApproved();
                AlertDialogBuilder.messgeDialog("System Message", "You can no longer update/change this MIRS since it is in "+mirs.getStatus().toUpperCase()+" status.",
                        Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
            }else if(mirsSignatoryList.get(0).getStatus().equals(Utility.APPROVED) || mirsSignatoryList.get(1).getStatus().equals(Utility.APPROVED)){
                isApproved();
                AlertDialogBuilder.messgeDialog("System Message", "You can no longer update/change this MIRS since one or both of the assigned signatories already approved this request." +
                                "\n\n"+checkedEmployeeInfo.getFullName()+" : "+mirsSignatoryList.get(0).getStatus().toUpperCase()+
                                "\n"+approvedEmployeeInfo.getFullName()+" : "+mirsSignatoryList.get(1).getStatus().toUpperCase(),
                        Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fillUpFields(MIRS m) throws Exception {
        mirsItemList = FXCollections.observableList(MirsDAO.getItems(m));

        mirsSignatoryList = MIRSSignatoryDAO.get(m);
        mirsNumber.setText("MIRS #: "+m.getId());
        applicant.setText(m.getApplicant());
        address.setText(m.getAddress());
        purpose.setText(m.getPurpose());

        preparedEmployeeInfo = EmployeeDAO.getOne(m.getRequisitionerID(),DB.getConnection());
        prepared.setText(preparedEmployeeInfo.getFullName());

        checkedEmployeeInfo = EmployeeDAO.getOne(mirsSignatoryList.get(0).getUserID(),DB.getConnection());
        checked.setText(checkedEmployeeInfo.getFullName());

        approvedEmployeeInfo = EmployeeDAO.getOne(mirsSignatoryList.get(1).getUserID(),DB.getConnection());
        approved.setText(approvedEmployeeInfo.getFullName());

        filingDate.setValue(m.getDateFiled());
        remarks.setText(m.getDetails());


        selectedItem.addAll(mirsItemList);
        mirsItemTable.setItems(selectedItem);
        mirsItemTable.sort();
        countRow.setText(""+selectedItem.size());
    }

    private void isApproved(){
        applicant.setDisable(true);
        address.setDisable(true);
        purpose.setDisable(true);
        prepared.setDisable(true);
        checked.setDisable(true);
        approved.setDisable(true);
        filingDate.setDisable(true);
        remarks.setDisable(true);
        saveChanges.setDisable(true);
        items.setDisable(true);
        quantity.setDisable(true);
    }

    private void resetInputFields() {
        selectedStock = null;
        selectedItem.clear();
        mirsItemTable.getItems().clear();
        purpose.setText("");
        items.setText("");
        quantity.setText("");
        applicant.setText("-");
        address.setText("-");
        remarks.setText("");
    }

    @FXML
    private void saveMirsChanges(ActionEvent event) {
        if(purpose.getText().isEmpty() ||
                preparedEmployeeInfo == null ||
                checkedEmployeeInfo == null ||
                approvedEmployeeInfo == null
        ){
            AlertDialogBuilder.messgeDialog("Input validation", "Please provide all required information, and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        if(selectedItem.isEmpty()){
            AlertDialogBuilder.messgeDialog("Table Validation", "No item Added",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        JFXButton accept = new JFXButton("Proceed");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("Update MIRS","Confirm MIRS updates.", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {
                    List<MIRSItem> mirsItemList = selectedItem; //from ObservableList to List
                    List<MIRSSignatory> mirsSignatoryList = new ArrayList<>();
                    MIRS mirsUpdate = new MIRS();
                    mirsUpdate.setDateFiled(filingDate.getValue());
                    mirsUpdate.setApplicant(applicant.getText());
                    mirsUpdate.setAddress(address.getText());
                    mirsUpdate.setPurpose(purpose.getText());
                    mirsUpdate.setId(mirs.getId());
                    mirsUpdate.setStatus(Utility.PENDING);
                    mirsUpdate.setRequisitionerID(preparedEmployeeInfo.getId());
                    mirsUpdate.setUserID(ActiveUser.getUser().getId());
                    mirsUpdate.setDetails(remarks.getText());

                    EmployeeInfo[] signatories = {checkedEmployeeInfo, approvedEmployeeInfo};
                    for(EmployeeInfo s : signatories){
                        MIRSSignatory mirsSignatory = new MIRSSignatory();
                        mirsSignatory.setMirsID(mirsUpdate.getId());
                        mirsSignatory.setUserID(s.getId());
                        mirsSignatory.setStatus(Utility.PENDING);
                        mirsSignatoryList.add(mirsSignatory);
                    }

                    if(MirsDAO.update(mirsUpdate, mirsItemList,removeMirsItems,mirsSignatoryList)){ //return true saved successfully
                        AlertDialogBuilder.messgeDialog("System Message", "MIRS request successfully updated, please wait for the approval, thank you!",
                                Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, "user_my_mirs_application.fxml", Utility.getContentPane(), Utility.getSubToolbar(), new Label("My MIRS Application: ")));
                        String details = "New MIRS ("+mirs.getId()+") was updated.";
                        Notifications tochecker = new Notifications(details, Utility.NOTIF_MIRS_APROVAL, ActiveUser.getUser().getEmployeeID(), checkedEmployeeInfo.getId(), mirs.getId());
                        Notifications toApprover = new Notifications(details, Utility.NOTIF_MIRS_APROVAL, ActiveUser.getUser().getEmployeeID(), approvedEmployeeInfo.getId(), mirs.getId());
                        NotificationsDAO.create(tochecker);
                        NotificationsDAO.create(toApprover);

                    }else{
                        AlertDialogBuilder.messgeDialog("System Message", "Sorry an error was encountered during update, please try again.",
                                Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", e.getMessage(),
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                dialog.close();
            }
        });
    }

    @FXML
    private void addItemToTable(ActionEvent event) throws Exception {
        //Enter key on the qty text field to add items on the table
        if(selectedStock == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getText().length() == 0 || Integer.parseInt(quantity.getText()) == 0 || Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock) ) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }


        //will update quantity if item is already added to the request
        for(MIRSItem added: selectedItem){
            if(added.getStockID().equals(selectedStock.getId())){
                added.setQuantity(added.getQuantity() + Integer.parseInt(quantity.getText()));
                items.setText("");
                quantity.setText("");
                selectedStock = null;
                inStock.setText("In Stock: 0");
                pending.setText("Pending: 0");
                available.setText("Available: 0");
                mirsItemTable.refresh();
                mirsItemTable.sort();
                items.requestFocus();
                return;
            }
        }

        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setStockID(selectedStock.getId());
        mirsItem.setParticulars(selectedStock.getDescription());
        mirsItem.setUnit(selectedStock.getUnit());
        mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
        mirsItem.setPrice(selectedStock.getPrice());
        mirsItem.setAdditional(false);

        selectedItem.add(mirsItem);
        mirsItemTable.refresh();
        mirsItemTable.sort();

        selectedStock = null; //set to null for validation
        items.setText("");
        quantity.setText("");
        items.requestFocus();
        inStock.setText("In Stock: 0");
        pending.setText("Pending: 0");
        available.setText("Available: 0");
        countRow.setText(""+selectedItem.size());
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void initializeItemTable() {
        /*TableColumn<MIRSItem, String> stockIdCol = new TableColumn<>("Stock Id");
        stockIdCol.setStyle("-fx-alignment: center;");
        stockIdCol.setPrefWidth(150);
        stockIdCol.setMaxWidth(150);
        stockIdCol.setMinWidth(150);
        stockIdCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(cellData.getValue().getStockID()));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });*/

        TableColumn<MIRSItem, String> descriptionCol = new TableColumn<>("Description");
        descriptionCol.setStyle("-fx-alignment: center-left;");
        descriptionCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getDescription());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> quantityCol = new TableColumn<>("Qty");
        quantityCol.setStyle("-fx-alignment: center;");
        quantityCol.setPrefWidth(100);
        quantityCol.setMaxWidth(100);
        quantityCol.setMinWidth(100);
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));

        TableColumn<MIRSItem, String> actionCol = new TableColumn<>(" ");
        actionCol.setPrefWidth(100);
        actionCol.setMaxWidth(100);
        actionCol.setMinWidth(100);
        Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>> cellFactory
                = //
                new Callback<TableColumn<MIRSItem, String>, TableCell<MIRSItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRSItem, String> param) {
                        final TableCell<MIRSItem, String> cell = new TableCell<MIRSItem, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            private final JFXButton btn = new JFXButton("", icon);
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    //btn.setStyle("-fx-background-color: "+ColorPalette.DANGER+";");
                                    icon.setIconSize(24);
                                    icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    btn.setOnAction(event -> {
                                        MIRSItem mirsItem = getTableView().getItems().get(getIndex());
                                        try{
                                            JFXButton accept = new JFXButton("Accept");
                                            JFXDialog dialog = DialogBuilder.showConfirmDialog("Remove Item","Confirm cancellation of MIRS item: "+StockDAO.get(mirsItem.getStockID()).getDescription(), accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
                                            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
                                            accept.setOnAction(new EventHandler<ActionEvent>() {
                                                @Override
                                                public void handle(ActionEvent __) {
                                                    try {
                                                        if(mirsItem.getId() != null){
                                                            removeMirsItems.add(mirsItem);
                                                            selectedItem.remove(mirsItem);
                                                            mirsItemTable.refresh();
                                                            countRow.setText(""+selectedItem.size());
                                                            dialog.close();
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                        }catch (Exception e){
                                            AlertDialogBuilder.messgeDialog("System Error", "Error encounter while Filing MIRS reason may be: ",
                                                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                                            return;
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

        actionCol.setCellFactory(cellFactory);
        actionCol.setStyle("-fx-alignment: center;");
        //mirsItemTable.getColumns().add(stockIdCol);
        mirsItemTable.getColumns().add(descriptionCol);
        mirsItemTable.getColumns().add(quantityCol);


        if(mirs.getStatus().equals(Utility.PENDING) )
            mirsItemTable.getColumns().add(actionCol);


        mirsItemTable.setPlaceholder(new Label("No item Added"));

        selectedItem =  FXCollections.observableArrayList();

    }

    private void bindParticularsAutocomplete(JFXTextField textField){
        AutoCompletionBinding<StockDescription> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<StockDescription> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.searchDescription(query);
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
                    public String toString(StockDescription object) {
                        return object.getDescription();
                    }

                    @Override
                    public StockDescription fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });
        stockSuggest.setVisibleRowCount(10);
        stockSuggest.setMinWidth(500);
        //This will set the actions once the user clicks an item from the popupmenu.
        stockSuggest.setOnAutoCompleted(event -> {
            StockDescription result = event.getCompletion();
            try {
                selectedStock = StockDAO.get(result.getId());
                int av = StockDAO.countAvailable(selectedStock);
                if(av == 0) {
                    AlertDialogBuilder.messgeDialog("System Warning", "Insufficient stock.",
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    items.setText("");
                }else{

                    quantity.requestFocus();

                    inStock.setText("In Stock: "+ selectedStock.getQuantity());
                    pending.setText("Pending: "+ StockDAO.countPendingRequest(selectedStock));
                    available.setText("Available: "+ av);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            }
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
                    }

                    if (list.size() == 0) {
                        if(textField == this.prepared)
                            preparedEmployeeInfo = null;
                        else if(textField == this.checked)
                            checkedEmployeeInfo = null;
                        else if(textField == this.approved)
                            approvedEmployeeInfo = null;
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
            if(textField == this.prepared) {
                preparedEmployeeInfo = event.getCompletion();
                prepared.setText(preparedEmployeeInfo.getFullName());
            }else if(textField == this.checked){
                checkedEmployeeInfo = event.getCompletion();
                checked.setText(checkedEmployeeInfo.getFullName());
            }else if(textField == this.approved){
                approvedEmployeeInfo = event.getCompletion();
                approved.setText(approvedEmployeeInfo.getFullName());
            }
        });
    }
}
