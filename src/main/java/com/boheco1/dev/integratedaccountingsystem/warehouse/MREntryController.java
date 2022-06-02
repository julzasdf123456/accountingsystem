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
            this.bindEmployeeAutocomplete(this.employee_search_tf);
            //this.bindItemAutocomplete(this.item_name_tf);
            this.bindSignatoreesAutocomplete(this.recommending_tf);
            this.bindSignatoreesAutocomplete(this.approve_tf);
            this.initializeTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.mr_no_tf.setText(NumberGenerator.mrNumber());
        Utility.setParentController(this);
        this.stackPane = Utility.getStackPane();
    }

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
    @FXML
    private void addFromStock(){
        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_add_mr_item.fxml", Utility.getStackPane());
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
