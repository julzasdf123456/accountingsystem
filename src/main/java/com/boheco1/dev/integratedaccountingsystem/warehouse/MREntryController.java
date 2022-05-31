package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.InputHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
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

public class MREntryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField employee_search_tf, fname_tf, mname_tf, lname_tf, item_name_tf, qty_tf, cost_tf;

    @FXML
    private TableView mr_items_table;

    @FXML
    private CheckBox from_warehouse_chb;

    @FXML
    private JFXButton saveBtn, addBtn;

    private EmployeeInfo employee = null;

    private Stock currentItem = null;

    private ObservableList<MR> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.bindEmployeeAutocomplete(this.employee_search_tf);
            this.bindItemAutocomplete(this.item_name_tf);
            this.initializeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.bindNumbers();
    }

    @FXML
    private void addMR()  {
        //Set item count
        int count = 0;
        List<MR> failed = new ArrayList<>();

        //Iterate the list of MR items
        for (int i = 0; i < this.mrItems.size(); i++) {
            MR item = this.mrItems.get(i);
            try {
                //Add each MR item to database
                MrDAO.add(item);

                //If MR item is from warehouse, deduct quantity
                if (item.getStockId() != null){
                    Stock item_stock = StockDAO.get(item.getStockId());
                    StockDAO.deductStockQuantity(item_stock, item.getQuantity());
                }
                count++;
            } catch (Exception e) {
                failed.add(item);
                AlertDialogBuilder.messgeDialog("System Error", "MR Entry was not successfully added due to:"+e.getMessage()+" error.", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
        //Show notification if successful
        if (count == this.mrItems.size()) {
            AlertDialogBuilder.messgeDialog("System Information", "MR Entry was successfully recorded.", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
        }else{
            String names = "";
            for (MR i : failed){
                names += i.getExtItem() + ", ";
            }
            AlertDialogBuilder.messgeDialog("System Warning", "The following MR Entries were not successfully recorded: "+names+".", stackPane, AlertDialogBuilder.WARNING_DIALOG);
        }
        //After adding all MR items, reset all fields
        this.reset();
    }

    @FXML
    private void addItem()  {
        String item_name = this.item_name_tf.getText();
        int max = 0;
        if (from_warehouse_chb.isSelected() && this.currentItem != null){
            max = this.currentItem.getQuantity();
        }
        int qty = 0;
        double price = 0;
        try {
            qty = Integer.parseInt(this.qty_tf.getText());
            price = Double.parseDouble(this.cost_tf.getText());
        }catch (Exception e){

        }
        if (employee == null){
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the employee by selecting from the dropdown list!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (item_name.length() == 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter item name or selecting from the dropdown list!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (qty <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (from_warehouse_chb.isSelected() && this.currentItem != null && qty > max) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "MR quantity cannot exceed maximum stock quantity!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (price <= 0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please enter a valid value for the item price!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else{
            //Create MR Item
            MR mr_item = new MR();

            //Set the selected employee's ID
            mr_item.setEmployeeId(employee.getId());

            //Set the current logged user's employee ID
            mr_item.setWarehousePersonnelId(ActiveUser.getUser().getEmployeeID());

            //Check if the item is from warehouse and set the stock id, item name, and price attributes using current info
            if (from_warehouse_chb.isSelected()){
                if (currentItem != null) {
                    mr_item.setStockId(this.currentItem.getId());
                    mr_item.setExtItem(this.currentItem.getDescription());
                    mr_item.setPrice(this.currentItem.getPrice());
                    mr_item.setQuantity(qty);
                    mr_item.setDateOfMR(LocalDate.now());

                    this.mrItems.add(mr_item);
                    this.mr_items_table.setItems(this.mrItems);
                    this.resetItemData();
                }else{
                    AlertDialogBuilder.messgeDialog("Invalid Input", "Please select stock from dropdown list!",
                            stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            //Else set the information from the textfields
            }else{
                mr_item.setExtItem(this.item_name_tf.getText());
                mr_item.setPrice(price);
                mr_item.setQuantity(qty);
                mr_item.setDateOfMR(LocalDate.now());

                this.mrItems.add(mr_item);
                this.mr_items_table.setItems(this.mrItems);
                this.resetItemData();
            }
        }
    }

    public void resetItemData(){
        this.currentItem = null;
        this.item_name_tf.setText("");
        this.qty_tf.setText("");
        this.cost_tf.setText("");
    }

    public void bindNumbers(){
        InputHelper.restrictNumbersOnly(this.qty_tf);
        InputHelper.restrictNumbersOnly(this.cost_tf);
    }

    public void bindEmployeeAutocomplete(JFXTextField textField){
        AutoCompletionBinding<EmployeeInfo> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<EmployeeInfo> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 1){
                        try {
                            list = EmployeeDAO.getEmployeeInfo(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        this.employee = null;
                        this.fname_tf.setText("");
                        this.mname_tf.setText("");
                        this.lname_tf.setText("");
                    }

                    return list;
                }, new StringConverter<>() {
                    @Override
                    public String toString(EmployeeInfo object) {
                        String data = object.getEmployeeLastName()+", "+object.getEmployeeFirstName()+" "+object.getEmployeeMidName();
                        return data;
                    }

                    @Override
                    public EmployeeInfo fromString(String string) {
                        throw new UnsupportedOperationException();
                    }
                });

        //This will set the actions once the user clicks an item from the popupmenu.
        employeeSuggest.setOnAutoCompleted(event -> {
            EmployeeInfo result = event.getCompletion();
            this.employee = result;
            this.fname_tf.setText(this.employee.getEmployeeFirstName());
            this.mname_tf.setText(this.employee.getEmployeeMidName());
            this.lname_tf.setText(this.employee.getEmployeeLastName());
        });
    }

    public void bindItemAutocomplete(JFXTextField textField){
        AutoCompletionBinding<SlimStock> stockSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    if (from_warehouse_chb.isSelected() == false){
                        return null;
                    }
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<SlimStock> list = new ArrayList<>();

                    //Perform DB query when length of search string is 4 or above
                    if (query.length() > 3){
                        try {
                            list = StockDAO.search_available(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        currentItem = null;
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
                currentItem = StockDAO.get(result.getId());
                this.item_name_tf.setText(currentItem.getDescription());
                this.cost_tf.setText(currentItem.getPrice()+"");
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", e.getMessage(), this.stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }

    public void reset(){
        this.employee = null;

        this.resetItemData();
        this.employee_search_tf.setText("");
        this.fname_tf.setText("");
        this.mname_tf.setText("");
        this.lname_tf.setText("");

        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setItems(this.mrItems);
        this.mr_items_table.setPlaceholder(new Label("No item added!"));
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @FXML
    private void clear()  {
        reset();
    }

    public void initializeTable() {
        TableColumn<MR, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(120);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column2 = new TableColumn<>("Item Name");
        column2.setMinWidth(400);
        column2.setCellValueFactory(new PropertyValueFactory<>("extItem"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column3 = new TableColumn<>("Quantity");
        column3.setMinWidth(75);
        column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column4 = new TableColumn<>("Unit Price");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("price"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column5 = new TableColumn<>("Action");
        Callback<TableColumn<MR, String>, TableCell<MR, String>> removeBtn
                = //
                new Callback<TableColumn<MR, String>, TableCell<MR, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MR, String> param) {
                        final TableCell<MR, String> cell = new TableCell<MR, String>() {

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
                                        MR selected_item = getTableView().getItems().get(getIndex());

                                        try {
                                            mrItems.remove(selected_item);
                                            mr_items_table.setItems(mrItems);
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
        column5.setCellFactory(removeBtn);
        column5.setStyle("-fx-alignment: center;");

        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column2);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column5);
    }
}
