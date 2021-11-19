package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputValidation;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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
    private StackPane stackPane;

    @FXML
    private DatePicker date;

    @FXML
    private JFXTextField mirsNum, requisitioner, dm, gm, particulars, remarks;

    @FXML
    private JFXComboBox<Integer> quantity;

    @FXML
    private JFXTextArea purpose;

    @FXML
    private Label available;

    @FXML
    private TableView<MIRSItem> particularsTable;

    @FXML
    private TableColumn<MIRSItem, String> codeCol, particularsCol, unitCol, remarksCol;

    @FXML
    private TableColumn<MIRSItem, Integer> quantityCol;

    @FXML
    private JFXButton addBtn, requestBtn, removeBtn;

    private Stock stock = null;
    private EmployeeInfo requisitionerEmployee = null;
    private List<Signatory> signatories = null;
    private ObservableList<MIRSItem> requestItem = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        date.setValue(LocalDate.now());
        bindParticularsAutocomplete(particulars);
        bindRequisitionerAutocomplete(requisitioner);
        InputValidation.restrictNumbersOnly(mirsNum);
        setSignatories();
        setItemTable();
    }

    @FXML
    private void addBtn(ActionEvent event) {
        if(stock == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid stock item!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }else if(quantity.getSelectionModel().getSelectedIndex() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please provide a valid request quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }
        MIRSItem mirsItem = new MIRSItem();
        mirsItem.setMirsID(Integer.parseInt(mirsNum.getText()));
        mirsItem.setStockID(stock.getId());
        mirsItem.setParticulars(stock.getStockName());
        mirsItem.setUnit(stock.getUnit());
        mirsItem.setQuantity(quantity.getValue());
        mirsItem.setPrice(stock.getPrice());
        mirsItem.setRemarks(remarks.getText());

        stock = null; //set to null for validation
        requestItem.add(mirsItem);
        particularsTable.setItems(requestItem);
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

        MIRS mirs = new MIRS();
        mirs.setId(Integer.parseInt(mirsNum.getText()));
        mirs.setDetails("Details");
        mirs.setStatus("pending");
        mirs.setDateFiled(date.getValue());
        mirs.setPurpose(purpose.getText());
        mirs.setRequisitionerID(requisitionerEmployee.getId());
        mirs.setUserID(ActiveUser.getUser().getId());
        try {
            List<MIRSItem> mirsItemList = requestItem; //from ObservableList to List
            MirsDAO.create(mirs); //add a new MIRS to the database
            MirsDAO.addMIRSItems(mirs, mirsItemList); //add the items request from the MIRS filled

            for(Signatory s : signatories){
                MIRSSignatory mirsSignatory = new MIRSSignatory();
                mirsSignatory.setMirsID(Integer.parseInt(mirsNum.getText()));
                mirsSignatory.setSignatoryID(s.getUserID());
                mirsSignatory.setStatus("for approval");
                mirsSignatory.setComments("No comment");
                MIRSSignatoryDAO.add(mirsSignatory); //saving signatories for the MIRS request
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    private void setItemTable() {
        codeCol.setCellValueFactory(new PropertyValueFactory<>("StockID"));
        particularsCol.setCellValueFactory(new PropertyValueFactory<>("Particulars"));
        unitCol.setCellValueFactory(new PropertyValueFactory<>("Unit"));
        quantityCol.setCellValueFactory(new PropertyValueFactory<>("Quantity"));
        remarksCol.setCellValueFactory(new PropertyValueFactory<>("Remarks"));

        requestItem =  FXCollections.<MIRSItem>observableArrayList();
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
                        stock = null;
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
                stock = StockDAO.get(result.getId());
                available.setText("Available Stock: "+stock.getQuantity());

                quantity.getItems().clear();
                for(int q=1;q<=stock.getQuantity();q++){
                    quantity.getItems().add(q);
                }
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
            requisitioner.setText(requisitionerEmployee.getEmployeeFirstName() + " " + requisitionerEmployee.getEmployeeLastName());
        });
    }
}
