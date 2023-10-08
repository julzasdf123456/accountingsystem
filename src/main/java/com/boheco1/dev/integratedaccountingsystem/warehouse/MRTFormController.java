package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.*;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.*;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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

public class MRTFormController extends MenuControllerHandler implements Initializable {

    @FXML
    private JFXTextField searchItem_tf,returned_tf,received_tf;

    @FXML
    private TableView releasedItemTable, returnItemTable;

    private ObservableList<MRTItem> mrtItems = null;
    private ObservableList<ReleasedItems> releasedItems = null;
    private MRT currentMRT = null;
    private EmployeeInfo receivedBy = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeReleasedItemTable();
        this.initializeReturnItemTable();
        this.bindReturnedByAutocomplete(this.received_tf, true);
        this.bindReturnedByAutocomplete(this.returned_tf, false);
        try {
            this.receivedBy = EmployeeDAO.getByDesignation("Head, Warehous");
            if (this.receivedBy != null)
                this.received_tf.setText(this.receivedBy.getFullName());
            else {
                this.receivedBy = EmployeeDAO.getByDesignation("Warehouse");
                this.received_tf.setText(this.receivedBy.getFullName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Searches for the released items  and displays them in the released items table
     * @return void
     */
    @FXML
    public void searchReleasedItem(){
        String key = this.searchItem_tf.getText();
        try {
            Platform.runLater(() -> {
                try {
                    this.releasedItems = FXCollections.observableList(MRTDao.searchReleasedItems(key));
                    this.releasedItemTable.getItems().setAll(this.releasedItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    /**
     * Selects the released items from the released items table, displays the quantity to return and displays them in the return items table
     * @return void
     */
    @FXML
    public void returnItem(){
        Object selectedItem = this.releasedItemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please select from the released item table before proceeding!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else{
            ReleasedItems item = (ReleasedItems) selectedItem;
            JFXButton returnNumber = new JFXButton("Return");
            JFXTextField qty_tf = new JFXTextField();
            qty_tf.setText(item.getBalance()+"");
            InputValidation.restrictNumbersOnly(qty_tf);
            JFXDialog returnDialog = DialogBuilder.showInputDialog("Quantity To Return","Please enter the quantity:  ", "", qty_tf, returnNumber, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            returnNumber.setOnAction(__ -> {
                if(qty_tf.getText().isEmpty()) {
                    AlertDialogBuilder.messgeDialog("System Message", "No return quantity provided", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }else{
                    try{
                        int qty_to_return = Integer.parseInt(qty_tf.getText());

                        if (qty_to_return <= 0){
                            AlertDialogBuilder.messgeDialog("System Message", "No return quantity provided", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        }else if (qty_to_return > item.getBalance()){
                            AlertDialogBuilder.messgeDialog("System Message", "The return quantity provided should not exceed the maximum released quantity!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                        }else{
                            MRTItem returnItem = new MRTItem(null, item.getId(), null, qty_to_return);
                            returnItem.setCode(item.getCode());
                            returnItem.setStockID(item.getStockID());
                            boolean ok = true;
                            for (MRTItem i : this.mrtItems){
                                if (i.getReleasingID().equals(returnItem.getReleasingID())) {
                                    ok = false;
                                    break;
                                }
                            }
                            if (!ok){
                                AlertDialogBuilder.messgeDialog("System Message", "The selected item is already found in the return items table!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                            }else{
                                this.mrtItems.add(returnItem);
                                this.returnItemTable.setItems(this.mrtItems);
                            }

                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                returnDialog.close();
            });
        }
    }
    /**
     * Creates and MCT and returns the MRT items
     * @return void
     */
    @FXML
    public void returnItems(){
        if (this.mrtItems.size() == 0) {
            AlertDialogBuilder.messgeDialog("System Message", "No items were selected for returning!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.returned_tf.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("System Message", "No returned by employee was set!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.receivedBy == null) {
            AlertDialogBuilder.messgeDialog("System Message", "No received by employee was set!", Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }else  {
            JFXButton accept = new JFXButton("Proceed");
            JFXDialog dialog = DialogBuilder.showConfirmDialog("MRT","This process is final. Confirm Return Item(s)?", accept, Utility.getStackPane(), DialogBuilder.INFO_DIALOG);
            accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
            accept.setOnAction(__ -> {
                this.currentMRT = new MRT(null, this.returned_tf.getText(), this.receivedBy.getId(), LocalDate.now());
                try {
                    MRTDao.create(this.currentMRT);
                    MRTDao.addItems(this.currentMRT, this.mrtItems);
                    AlertDialogBuilder.messgeDialog("Material Return", "The selected released items were successfully returned!", Utility.getStackPane(), AlertDialogBuilder.SUCCESS_DIALOG);
                    this.reset();
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while returning the items due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
                dialog.close();
            });
        }
    }

    /**
     * Initializes the Released items table
     * @return void
     */
    public void initializeReleasedItemTable() {
        TableColumn<ReleasedItems, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(120);
        column1.setMaxWidth(120);
        column1.setPrefWidth(120);
        column1.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<ReleasedItems, String> column2 = new TableColumn<>("Description");
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<ReleasedItems, String> column3 = new TableColumn<>("MCT No");
        column3.setMinWidth(115);
        column3.setMaxWidth(115);
        column3.setPrefWidth(115);
        column3.setCellValueFactory(new PropertyValueFactory<>("mctNo"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<ReleasedItems, String> column4 = new TableColumn<>("Price");
        column4.setMinWidth(115);
        column4.setMaxWidth(115);
        column4.setPrefWidth(115);
        column4.setCellValueFactory(new PropertyValueFactory<>("price"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<ReleasedItems, String> column5 = new TableColumn<>("Quantity");
        column5.setMinWidth(115);
        column5.setMaxWidth(115);
        column5.setPrefWidth(115);
        column5.setCellValueFactory(new PropertyValueFactory<>("balance"));
        column5.setStyle("-fx-alignment: center;");

        this.releasedItems =  FXCollections.observableArrayList();
        this.releasedItemTable.setPlaceholder(new Label("No item searched!"));

        this.releasedItemTable.getColumns().add(column1);
        this.releasedItemTable.getColumns().add(column2);
        this.releasedItemTable.getColumns().add(column3);
        this.releasedItemTable.getColumns().add(column4);
        this.releasedItemTable.getColumns().add(column5);
    }

    /**
     * Initializes the Return items table
     * @return void
     */
    public void initializeReturnItemTable() {
        TableColumn<MRTItem, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(120);
        column1.setMaxWidth(120);
        column1.setPrefWidth(120);
        column1.setCellValueFactory(new PropertyValueFactory<>("Code"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column2 = new TableColumn<>("Description");
        column2.setCellValueFactory(item -> {
            try {
                Stock stock = StockDAO.get(ReleasingDAO.get(item.getValue().getReleasingID()).getStockID());
                return new ReadOnlyObjectWrapper<>(stock.getDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MRTItem, String> column3 = new TableColumn<>("Quantity");
        column3.setMinWidth(75);
        column3.setMaxWidth(75);
        column3.setPrefWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<MRTItem, String> column4 = new TableColumn<>("Action");
        column4.setMinWidth(75);
        column4.setMaxWidth(75);
        column4.setPrefWidth(75);
        Callback<TableColumn<MRTItem, String>, TableCell<MRTItem, String>> removeBtn
                = //
                new Callback<TableColumn<MRTItem, String>, TableCell<MRTItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MRTItem, String> param) {
                        final TableCell<MRTItem, String> cell = new TableCell<MRTItem, String>() {

                            FontIcon icon = new FontIcon("mdi2c-close-circle");
                            JFXButton btn = new JFXButton("", icon);

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconSize(24);
                                icon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        MRTItem selectedItem = getTableView().getItems().get(getIndex());

                                        try {
                                            mrtItems.remove(selectedItem);
                                            returnItemTable.setItems(mrtItems);
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
        column4.setCellFactory(removeBtn);
        column4.setStyle("-fx-alignment: center;");

        this.mrtItems =  FXCollections.observableArrayList();
        this.returnItemTable.setPlaceholder(new Label("No item added!"));

        this.returnItemTable.getColumns().add(column1);
        this.returnItemTable.getColumns().add(column2);
        this.returnItemTable.getColumns().add(column3);
        this.returnItemTable.getColumns().add(column4);
    }

    /**
     * Binds the textfield to autosuggest employee
     * @return void
     */
    public void bindReturnedByAutocomplete(JFXTextField textField, boolean isReceived){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 1){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0 && isReceived) {
                       receivedBy = null;
                    }

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
            EmployeeInfo user = event.getCompletion();
            textField.setText(user.getFullName());
            if (isReceived) {receivedBy = user;}
        });
    }

    /**
     * Resets all field values
     * @return void
     */
    @FXML
    public void reset(){
        this.mrtItems = FXCollections.observableArrayList();;
        this.releasedItems = FXCollections.observableArrayList();;
        this.currentMRT = null;
        this.returned_tf.setText("");
        this.searchItem_tf.setText("");
        this.returnItemTable.setItems(this.mrtItems);
        this.releasedItemTable.setItems(this.releasedItems);
        this.returnItemTable.setPlaceholder(new Label("No item added!"));
        this.releasedItemTable.setPlaceholder(new Label("No item searched!"));
    }
}
