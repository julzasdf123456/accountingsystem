package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.property.ReadOnlyObjectWrapper;
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

public class MREntryController extends MenuControllerHandler implements Initializable, ObjectTransaction {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField employee_search_tf, fname_tf, mname_tf, lname_tf, mr_no_tf, recommending_tf, approve_tf, purpose_tf;

    @FXML
    private TableView mr_items_table;

    private EmployeeInfo employee = null;

    private ObservableList<MrItem> mrItems = null;

    private EmployeeInfo recommending = null, approved = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            //Sets autocomplete on the employee textfield
            this.bindEmployeeAutocomplete(this.employee_search_tf);
            //Sets autocomplete on the recommending textfield
            this.bindSignatoreesAutocomplete(this.recommending_tf);
            //Sets autocomplete on the approve textfield
            this.bindSignatoreesAutocomplete(this.approve_tf);
            //Initializes the MR items table
            this.initializeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Sets the MR number to a formatted value
        this.mr_no_tf.setText(NumberGenerator.mrNumber());
        //Sets this controller so that MR items from the child dialog can be passed here and displayed in the table
        Utility.setParentController(this);
        //Sets the stackpane for the dialog/modal displays
        this.stackPane = Utility.getStackPane();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
    /**
     * Adds the MR and MR items to the database. Display prompts on success or error.
     * @return void
     */
    @FXML
    private void addMR()  {
        String mr_no = this.mr_no_tf.getText();
        if (mr_no.length() < 9) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the MR Number to 9 characters! e.g. 2022-XXXX",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.employee == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the employee!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.mrItems.size()==0) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please add item(s) to MR!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.recommending == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the recommending approval!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (this.approved == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the approved by!",
                    stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else{
            MR mr = new MR(mr_no, employee.getId(), ActiveUser.getUser().getEmployeeID(), LocalDate.now(), Utility.MR_FILED, this.recommending.getId(), this.approved.getId());
            mr.setPurpose(this.purpose_tf.getText());
            int count = 0;
            try {
                MrDAO.add(mr);
                for (MrItem i : this.mrItems){
                    MrDAO.createItem(mr, i);
                    count++;
                }
            } catch (Exception e) {
                AlertDialogBuilder.messgeDialog("System Error", "Filing of Memorandum Receipt was not successfully added due to:"+e.getMessage()+" error.", stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }

            if (count == 0) {
                AlertDialogBuilder.messgeDialog("System Error!", "Process failed! The Memorandum Receipt was not filed!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }else if (count != this.mrItems.size()){
                AlertDialogBuilder.messgeDialog("System Error!", "Process failed! Some Memorandum Receipt items was not saved in the database!",
                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
            }else{
                AlertDialogBuilder.messgeDialog("MR Entry", "The Memorandum Receipt was successfully filed!", stackPane, AlertDialogBuilder.SUCCESS_DIALOG);
                this.reset();
            }
        }
    }
    /**
     * Sets the signatorees autocomplete for the employee names
     * @param textField the textfield to attach the autocomplete
     * @return void
     */
    public void bindSignatoreesAutocomplete(JFXTextField textField){
        AutoCompletionBinding<User> employeeSuggest = TextFields.bindAutoCompletion(textField,
                param -> {
                    //Value typed in the textfield
                    String query = param.getUserText();

                    //Initialize list of stocks
                    List<User> list = new ArrayList<>();

                    //Perform DB query when length of search string is 2 or above
                    if (query.length() > 1){
                        try {
                            list = UserDAO.search(query);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (list.size() == 0) {
                        if (textField == this.recommending_tf) {
                            this.recommending = null;
                        }else if (textField == this.approve_tf) {
                            this.approved = null;
                        }
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
            User user = event.getCompletion();
            textField.setText(user.getFullName());
            try {
                if (textField == this.recommending_tf) {
                    this.recommending = user.getEmployeeInfo();
                }else if (textField == this.approve_tf) {
                    this.approved = user.getEmployeeInfo();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Sets the assigned employee autocomplete
     * @param textField the textfield to attach the autocomplete
     * @return void
     */
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
    /**
     * Resets all the field values
     * @return void
     */
    public void reset(){
        this.employee = null;
        this.recommending = null;
        this.approved = null;
        this.mr_no_tf.setText(NumberGenerator.mrNumber());
        this.employee_search_tf.setText("");
        this.fname_tf.setText("");
        this.mname_tf.setText("");
        this.lname_tf.setText("");
        this.purpose_tf.setText("");
        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setItems(this.mrItems);
        this.mr_items_table.setPlaceholder(new Label("No item added!"));
    }

    @FXML
    private void clear()  {
        reset();
    }
    /**
     * Initializes the MR items table
     * @return void
     */
    public void initializeTable() {

        TableColumn<MrItem, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(75);
        column1.setCellValueFactory(new PropertyValueFactory<>("qty"));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column2 = new TableColumn<>("Unit");
        column2.setMinWidth(75);
        column2.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getUnit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(375);
        column3.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Property No.");
        column4.setMinWidth(110);
        column4.setCellValueFactory(new PropertyValueFactory<>("stockID"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column5 = new TableColumn<>("RR No.");
        column5.setMinWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory<>("rrNo"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column6 = new TableColumn<>("Unit Price");
        column6.setMinWidth(100);
        column6.setCellValueFactory(
                item -> {
                    try {
                        return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getReceivingItem().getUnitCost());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column7 = new TableColumn<>("Total Value");
        column7.setMinWidth(100);
        column7.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getReceivingItem().getUnitCost() * item.getValue().getQty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Remarks");
        column8.setMinWidth(100);
        column8.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        column8.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column9 = new TableColumn<>("Action");
        Callback<TableColumn<MrItem, String>, TableCell<MrItem, String>> removeBtn
                = //
                new Callback<TableColumn<MrItem, String>, TableCell<MrItem, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MrItem, String> param) {
                        final TableCell<MrItem, String> cell = new TableCell<MrItem, String>() {

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
                                        MrItem selected_item = getTableView().getItems().get(getIndex());
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
        column9.setCellFactory(removeBtn);
        column9.setStyle("-fx-alignment: center;");

        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column2);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column5);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
        this.mr_items_table.getColumns().add(column8);
        this.mr_items_table.getColumns().add(column9);
    }
    /**
     * Displays the dialog to add items from stock
     * @return void
     */
    @FXML
    private void addFromStock(){
        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_add_mr_item.fxml", Utility.getStackPane());
    }
    /**
     * Sets the object returned from the child dialog and adds the MR item to the table
     * @param o the returned object
     * @return void
     */
    @Override
    public void receive(Object o) {
        boolean ok = true;
        if (o instanceof MrItem){
            MrItem item = (MrItem) o;
            for (MrItem i: this.mrItems){
                if (i.getStockID().equals(item.getStockID())){
                    ok = false;
                    break;
                }
            }
            if (ok) {
                this.mrItems.add(item);
                this.mr_items_table.setItems(this.mrItems);
            }
        }
    }
}
