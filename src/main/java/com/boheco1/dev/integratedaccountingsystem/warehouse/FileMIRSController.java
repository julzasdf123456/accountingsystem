package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class FileMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private DatePicker date;

    @FXML
    private JFXTextField applicant, address, mirsNum, requisitioner, signatory1, signatory2, signatory3, particulars, quantity;

    @FXML
    private JFXTextArea purpose;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private TableView<MIRSItem> mirsItemTable;

    private Stock selectedStock = null;
    private EmployeeInfo requisitionerEmployee = null;
    private EmployeeInfo signatory1Employee = null;
    private EmployeeInfo signatory2Employee = null;
    private EmployeeInfo signatory3Employee = null;
    private ObservableList<MIRSItem> selectedItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        date.setValue(LocalDate.now());
        mirsNum.setText(Utility.CURRENT_YEAR()+"-");
        bindParticularsAutocomplete(particulars);
        bindEmployeeInfoAutocomplete(requisitioner);
        bindEmployeeInfoAutocomplete(signatory1);
        bindEmployeeInfoAutocomplete(signatory2);
        bindEmployeeInfoAutocomplete(signatory3);
        InputValidation.restrictNumbersOnly(quantity);
        initializeItemTable();
        mirsNum.requestFocus();
    }

    private void resetInputFields() {
        selectedStock = null;
        requisitionerEmployee = null;
        selectedItem.clear();
        mirsItemTable.getItems().clear();
        mirsNum.setText("");
        requisitioner.setText("");
        purpose.setText("");
        particulars.setText("");
        quantity.setText("");
        applicant.setText("");
        address.setText("");
        mirsNum.setText(Utility.CURRENT_YEAR()+"-");
    }

    @FXML
    private void requestBtn(ActionEvent event) {
        if(applicant.getText().isEmpty() ||
                address.getText().isEmpty() ||
                purpose.getText().isEmpty() ||
                mirsNum.getText().isEmpty() ||
                requisitionerEmployee == null ||
                signatory1Employee == null ||
                signatory2Employee == null ||
                signatory3Employee == null ||
                selectedItem.isEmpty()){
            AlertDialogBuilder.messgeDialog("Input validation", "Please provide all required information, and try again.",
                    Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
            return;
        }
        JFXButton accept = new JFXButton("Proceed");
        JFXDialog dialog = DialogBuilder.showConfirmDialog("File MIRS","Confirm MIRS application", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                try {
                    List<MIRSItem> mirsItemList = selectedItem; //from ObservableList to List
                    MIRS mirs = new MIRS();
                    mirs.setDateFiled(date.getValue());
                    mirs.setApplicant(applicant.getText());
                    mirs.setAddress(address.getText());
                    mirs.setPurpose(purpose.getText());
                    mirs.setId(mirsNum.getText()); //id mean MIRS number from user input
                    mirs.setStatus(Utility.PENDING);
                    mirs.setRequisitionerID(requisitionerEmployee.getId());
                    mirs.setUserID(ActiveUser.getUser().getId());
                    MirsDAO.create(mirs); //add a new MIRS to the database
                    MirsDAO.addMIRSItems(mirs, mirsItemList); //add the items request from the MIRS filled


                    EmployeeInfo[] signatories = {signatory1Employee, signatory2Employee, signatory3Employee};
                    for(EmployeeInfo s : signatories){
                        MIRSSignatory mirsSignatory = new MIRSSignatory();
                        mirsSignatory.setMirsID(mirs.getId());
                        mirsSignatory.setUserID(s.getId());
                        mirsSignatory.setStatus(Utility.PENDING);
                        mirsSignatory.setComments("");
                        MIRSSignatoryDAO.add(mirsSignatory); //saving signatories for the MIRS request
                    }
                    AlertDialogBuilder.messgeDialog("System Message", "MIRS request successfully filed, please wait for the approval, thank you!",
                            Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    resetInputFields();
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

        for(MIRSItem added: selectedItem){
            if(added.getStockID().equals(selectedStock.getId())){
                AlertDialogBuilder.messgeDialog("System Warning", "Item already added, please remove item, then add again if you have changes.",
                        Utility.getStackPane(), AlertDialogBuilder.WARNING_DIALOG);
                particulars.setText("");
                quantity.setText("");
                selectedStock = null;
                particulars.requestFocus();
                inStock.setText("In Stock: 0");
                pending.setText("Pending: 0");
                available.setText("Available: 0");
                particulars.requestFocus();
                return;
            }
        }

        if(selectedStock == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getText().length() == 0 || Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            return;
        }

        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setMirsID(mirsNum.getText());
        mirsItem.setStockID(selectedStock.getId());
        mirsItem.setParticulars(selectedStock.getStockName());
        mirsItem.setUnit(selectedStock.getUnit());
        mirsItem.setQuantity(Integer.parseInt(quantity.getText()));
        mirsItem.setPrice(selectedStock.getPrice());

        selectedItem.add(mirsItem);
        //mirsItemTable.getItems().clear();
        mirsItemTable.setItems(selectedItem);

        selectedStock = null; //set to null for validation
        particulars.setText("");
        quantity.setText("");
        particulars.requestFocus();
        inStock.setText("In Stock: 0");
        pending.setText("Pending: 0");
        available.setText("Available: 0");
    }

    @FXML
    private void removeBtn(ActionEvent event) throws Exception {
        MIRSItem selected = mirsItemTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            AlertDialogBuilder.messgeDialog("System Information", "Please select an item from the table!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }else{

            JFXButton accept = new JFXButton("Accept");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("Remove Item","Confirm cancellation of MIRS item: "+StockDAO.get(selected.getStockID()).getDescription(), accept, Utility.getStackPane(), DialogBuilder.WARNING_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent __) {
                    selectedItem.remove(selected);
                    mirsItemTable.setItems(selectedItem);
                    dialog.close();
                }
            });
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void initializeItemTable() {
        TableColumn<MIRSItem, String> neaCodeCol = new TableColumn<>("NEA Code");
        neaCodeCol.setStyle("-fx-alignment: center;");
        neaCodeCol.setPrefWidth(150);
        neaCodeCol.setMaxWidth(150);
        neaCodeCol.setMinWidth(150);
        neaCodeCol.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(Objects.requireNonNull(StockDAO.get(cellData.getValue().getStockID())).getNeaCode());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });

        TableColumn<MIRSItem, String> descriptionCol = new TableColumn<>("Description");
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

        mirsItemTable.getColumns().add(neaCodeCol);
        mirsItemTable.getColumns().add(descriptionCol);
        mirsItemTable.getColumns().add(quantityCol);
        mirsItemTable.setPlaceholder(new Label("No item Added"));

        selectedItem =  FXCollections.observableArrayList();
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
                        return object.getDescription();
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
                            Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                    particulars.setText("");
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
                        if(textField == this.requisitioner)
                            requisitionerEmployee = null;
                        else if(textField == this.signatory1)
                            signatory1Employee = null;
                        else if(textField == this.signatory2)
                            signatory2Employee = null;
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
            if(textField == this.requisitioner) {
                requisitionerEmployee = event.getCompletion();
                requisitioner.setText(requisitionerEmployee.getFullName());
            }else if(textField == this.signatory1) {
                signatory1Employee = event.getCompletion();
                signatory1.setText(signatory1Employee.getFullName());
            }else if(textField == this.signatory2){
                signatory2Employee = event.getCompletion();
                signatory2.setText(signatory2Employee.getFullName());
            }else if(textField == this.signatory3){
                signatory3Employee = event.getCompletion();
                signatory3.setText(signatory3Employee.getFullName());
            }
        });
    }
}
