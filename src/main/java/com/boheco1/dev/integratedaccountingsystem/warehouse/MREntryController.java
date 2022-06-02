package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MREntryController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField employee_search_tf, fname_tf, mname_tf, lname_tf, item_name_tf, qty_tf, cost_tf, mr_no_tf, recommending_tf, approve_tf, purpose_tf;

    @FXML
    private TableView mr_items_table;

    @FXML
    private CheckBox from_warehouse_chb;

    @FXML
    private JFXButton saveBtn, addBtn;

    private EmployeeInfo employee = null;

    private Stock currentItem = null;

    private ObservableList<MR> mrItems = null;

    private EmployeeInfo recommending = null, approved = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            this.bindEmployeeAutocomplete(this.employee_search_tf);
            //this.bindItemAutocomplete(this.item_name_tf);
            this.bindSignatoreesAutocomplete(this.recommending_tf);
            this.bindSignatoreesAutocomplete(this.approve_tf);
            this.initializeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.bindNumbers();
        this.mr_no_tf.setText(NumberGenerator.mrNumber());
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
        String mr_no = this.mr_no_tf.getText();

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
        }else if (mr_no.length() <= 5 || mr_no == null) {
            AlertDialogBuilder.messgeDialog("Invalid Input", "Please set the MR Number!",
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
        this.mr_no_tf.setText(NumberGenerator.mrNumber());
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

        TableColumn<MR, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(75);
        column1.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column1.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column2 = new TableColumn<>("Unit");
        column2.setMinWidth(75);
        //column3.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column3 = new TableColumn<>("Item Name");
        column3.setMinWidth(400);
        column3.setCellValueFactory(new PropertyValueFactory<>("extItem"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column4 = new TableColumn<>("Property No.");
        column4.setMinWidth(120);
        column4.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<String, String> column5 = new TableColumn<>("RR No.");
        column5.setMinWidth(75);
        //column5.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column6 = new TableColumn<>("Unit Price");
        column6.setMinWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<>("price"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column7 = new TableColumn<>("Total Value");
        column7.setMinWidth(100);
        //column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column7.setStyle("-fx-alignment: center;");

        TableColumn<String, String> column8 = new TableColumn<>("Remarks");
        column8.setMinWidth(100);
        //column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column8.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column9 = new TableColumn<>("Action");
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

    @FXML
    private void print()  {
        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("MR_report.pdf");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            Platform.runLater(() -> {
                try {
                    float[] columns = {.5f, .5f, 2.2f, .95f, .95f, .95f, 1f, .95f};
                    PrintPDF mr_pdf = new PrintPDF(selectedFile, columns);
                    //Create Header
                    mr_pdf.header(null, "Memorandum Receipt for Equipment, Semi-expendable,".toUpperCase(), "and non-expendable property".toUpperCase());

                    String[] mr_no = {"MR No: 2022-0042", "", "", LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))};
                    int[] head_span = {3, 1, 1, 3};
                    int[] head_aligns = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_CENTER};
                    int[] head_fonts = {Font.BOLD, Font.NORMAL, Font.NORMAL, Font.BOLD};
                    int[] head_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
                    mr_pdf.other_details(mr_no, head_span, head_fonts, head_aligns,head_borders, true);

                    EmployeeInfo user = ActiveUser.getUser().getEmployeeInfo();

                    String[] acknowledge = {"I HEREBY ACKNOWLEDGE to have received from "+user.getEmployeeFirstName().toUpperCase()+" "+user.getEmployeeLastName().toUpperCase()+", "+user.getDesignation() +", the following property for which I am responsible, " +
                            "subject to the provision of the usual accounting and auditing rules and regulations and which will be used for Operation and maintenance (Catigbian-San Isidro-Balilihan)"};
                    int[] ack_span = {8};
                    int[] ack_aligns = {Element.ALIGN_JUSTIFIED};
                    int[] ack_fonts = {Font.NORMAL};
                    int[] ack_borders = {Rectangle.NO_BORDER};
                    mr_pdf.other_details(acknowledge, ack_span, ack_fonts, ack_aligns,ack_borders, true);

                    //Create Table Header
                    String[] headers = {"QTY", "Unit", "Description", "Property No.", "RR No.", "Unit Price", "Total Value", "Remarks"};
                    int[] header_spans = {1, 1, 1, 1, 1, 1, 1, 1};
                    mr_pdf.tableHeader(headers, header_spans);

                    /*//Create Table Content
                    List<MR> mrs = MrDAO.getMRsOfEmployee(employeeInfo);
                    ArrayList<String[]> rows = new ArrayList<>();
                    for (MR mr : mrs) {
                        String rdate = "";
                        if (mr.getStatus().equals(Utility.MR_RETURNED)) {
                            rdate = " on " + mr.getDateOfReturn() + ", " + mr.getRemarks();
                        }
                        String[] data = {mr.getExtItem(), mr.getQuantity() + "", mr.getPrice() + "", mr.getDateOfMR().toString(), mr.getStatus() + rdate};
                        rows.add(data);
                    }
                    int[] rows_aligns = {Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_CENTER};
                    mr_pdf.tableContent(rows, header_spans, rows_aligns);*/

                    //Create Footer
                    int[] footer_spans = {4, 4};
                    String[] signatorees = {"Recommending Approval:", "Approved:"};
                    String[] designations = {"ESD Manager", "General Manager"};
                    String[] names = {"Victorio L. Sarigumba".toUpperCase(), "Dino Nicolas T. Roxas".toUpperCase()};
                    mr_pdf.footer(signatorees, designations, names, footer_spans, true, Element.ALIGN_CENTER, Element.ALIGN_CENTER);

                    int[] received_spans = {8};
                    String[] received = {"Received:"};
                    String[] designation = {"Line Man"};
                    String[] name = {"Rico Limocon".toUpperCase()};
                    mr_pdf.footer(received, designation, name, received_spans, true, Element.ALIGN_CENTER, Element.ALIGN_CENTER);

                    String[] note = {"INSTRUCTION"};
                    int[] note_span = {8};
                    int[] note_aligns = {Element.ALIGN_JUSTIFIED};
                    int[] note_fonts = {Font.NORMAL};
                    int[] note_borders = {Rectangle.NO_BORDER};
                    mr_pdf.other_details(note, note_span, note_fonts, note_aligns,note_borders, true);

                    String[] instruction = {"This form shall be prepared in FOUR (4) LEGIBLE COPIES, DISTRIBUTION: (1) ORIGINAL shall be KEPT by the Accountable Officer. (2) DUPLICATE must be FILED in the Personal File " +
                            "of the employee concerned. (3) TRIPLICATE should be FILED in the Office of the Accounting Section. (4) QUADRUPLICATE must be KEPT by the Responsible Employee."};
                    int[] ins_span = {8};
                    int[] ins_aligns = {Element.ALIGN_JUSTIFIED};
                    int[] ins_fonts = {Font.NORMAL};
                    int[] ins_borders = {Rectangle.NO_BORDER};
                    mr_pdf.other_details(instruction, ins_span, ins_fonts, ins_aligns,ins_borders, false);

                    mr_pdf.generate();
                }catch(Exception e){
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        }
    }
}
