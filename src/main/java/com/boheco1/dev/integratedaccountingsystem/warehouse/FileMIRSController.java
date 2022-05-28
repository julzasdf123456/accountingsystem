package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
import java.util.Objects;
import java.util.ResourceBundle;

public class FileMIRSController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private DatePicker date;

    @FXML
    private JFXTextField applicant, address, mirsNum, requisitioner, checkedBy, approvedBy,particulars, quantity;

    @FXML
    private JFXTextArea purpose;

    @FXML
    private Label available, inStock, pending;

    @FXML
    private TableView<MIRSItem> mirsItemTable;

    private Stock selectedStock = null;
    private EmployeeInfo requisitionerEmployee = null;
    private EmployeeInfo checkedByEmployee = null;
    private EmployeeInfo approvedByEmployee = null;
    private ObservableList<MIRSItem> selectedItem;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        date.setValue(LocalDate.now());
        mirsNum.setText(Utility.CURRENT_YEAR()+"-");
        bindParticularsAutocomplete(particulars);
        bindEmployeeInfoAutocomplete(requisitioner);
        bindEmployeeInfoAutocomplete(checkedBy);
        bindEmployeeInfoAutocomplete(approvedBy);
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
    }


    @FXML
    private void requestBtn(ActionEvent event) {
        if(applicant.getText().isEmpty() ||
                address.getText().isEmpty() ||
                purpose.getText().isEmpty() ||
                mirsNum.getText().isEmpty() ||
                requisitionerEmployee == null ||
                checkedByEmployee == null ||
                approvedByEmployee == null ||
                selectedItem.isEmpty()){
            AlertDialogBuilder.messgeDialog("Input validation", "Please provide all required information, and try again.",
                    stackPane, AlertDialogBuilder.WARNING_DIALOG);
            return;
        }

        try {
            List<MIRSItem> mirsItemList = selectedItem; //from ObservableList to List
            MIRS mirs = new MIRS();
            mirs.setDateFiled(date.getValue());
            mirs.setApplicant(applicant.getText());
            mirs.setAddress(address.getText());
            mirs.setPurpose(purpose.getText());
            mirs.setId(mirsNum.getText()); //id mean MIRS number from user input
            mirs.setRequisitionerID(requisitionerEmployee.getId());
            mirs.setUserID(ActiveUser.getUser().getId());
            MirsDAO.create(mirs); //add a new MIRS to the database
            MirsDAO.addMIRSItems(mirs, mirsItemList); //add the items request from the MIRS filled
            mirs.setStatus(Utility.PENDING);

            EmployeeInfo[] signatories = {checkedByEmployee, approvedByEmployee};
            for(EmployeeInfo s : signatories){
                MIRSSignatory mirsSignatory = new MIRSSignatory();
                mirsSignatory.setMirsID(mirs.getId());
                mirsSignatory.setUserID(s.getId());
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

    @FXML
    private void addItemToTable(ActionEvent event) throws Exception {
        //Enter key on the qty text field to add items on the table

        for(MIRSItem added: selectedItem){
            if(added.getStockID().equals(selectedStock.getId())){
                AlertDialogBuilder.messgeDialog("System Warning", "Item already added, please remove item, then add again if you have changes.",
                        stackPane, AlertDialogBuilder.WARNING_DIALOG);
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
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getText().length() == 0 || Integer.parseInt(quantity.getText()) > StockDAO.countAvailable(selectedStock)) {
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
    private void removeBtn(ActionEvent event) {
        MIRSItem selected = mirsItemTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertDialogBuilder.messgeDialog("System Information", "Please select an item from the table!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);
        }else{
            selectedItem.remove(selected);
            mirsItemTable.setItems(selectedItem);
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
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                    particulars.setText("");
                }else{
                    quantity.requestFocus();

                    inStock.setText("In Stock: "+ selectedStock.getQuantity());
                    pending.setText("Pending: "+ StockDAO.countPendingRequest(selectedStock));
                    available.setText("Available: "+ av);
                }
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "bindParticularsAutocomplete(): "+e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
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
                        else if(textField == this.checkedBy)
                            checkedByEmployee = null;
                        else if(textField == this.approvedBy)
                            approvedByEmployee = null;
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
            }else if(textField == this.checkedBy) {
                checkedByEmployee= event.getCompletion();
                checkedBy.setText(requisitionerEmployee.getFullName());
            }else if(textField == this.approvedBy){
                approvedByEmployee = event.getCompletion();
                approvedBy.setText(requisitionerEmployee.getFullName());
            }
        });
    }
}
