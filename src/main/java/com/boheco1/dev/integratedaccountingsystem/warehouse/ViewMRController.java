package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
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
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewMRController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField fname_tf, mname_tf, lname_tf, address_tf, phone_tf, designation_tf, date_approved_tf, released_tf, recommending_tf, approved_tf, purpose_tf;

    @FXML
    private ListView mr_list_view;

    @FXML
    private JFXButton printBtn;

    @FXML
    private TableView mr_items_table;

    private EmployeeInfo employee = null;

    private ObservableList<MrItem> mrItems = null;

    private MR current_mr = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initialize the selected employee
        employee = Utility.getSelectedEmployee();
        //Set the employee details, MR list view, MR item table
        this.setMRs();
        //Set this controller so it can be passed to Return MR dialog for processing
        Utility.setMrController(this);
        //Initially disable print button
        this.printBtn.setDisable(true);
    }

    /**
     * Initializes the MR Items table
     * @return void
     */
    public void initializeTable() {
        TableColumn<MrItem, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(75);
        column1.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getQty()+" "+item.getValue().getStock().getUnit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(343);
        column3.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Property No.");
        column4.setMinWidth(105);
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
                        double cost = 0;
                        ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                        if (rc == null){
                            cost = item.getValue().getStock().getPrice();
                        }else{
                            cost = rc.getUnitCost();
                        }
                        return new ReadOnlyObjectWrapper<>(cost);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column7 = new TableColumn<>("Total");
        column7.setMinWidth(100);
        column7.setCellValueFactory(item -> {
            try {
                double cost = 0;
                ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                if (rc == null){
                    cost = item.getValue().getStock().getPrice();
                }else{
                    cost = rc.getUnitCost();
                }
                return new ReadOnlyObjectWrapper<>(cost*item.getValue().getQty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Remarks");
        column8.setMinWidth(200);
        column8.setCellValueFactory(item -> {
            if (item.getValue().getDateReturned() == null){
                return new ReadOnlyObjectWrapper<>(item.getValue().getRemarks());
            }else{
                String status = item.getValue().getStatus()+" on "+item.getValue().getDateReturned();
                return new ReadOnlyObjectWrapper<>(status);
            }
        });
        column8.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, MrItem> column9 = new TableColumn<>(" ");
        column9.setCellValueFactory(mr -> new ReadOnlyObjectWrapper<>(mr.getValue()));
        column9.setCellFactory(mritem -> new TableCell<>(){
            FontIcon icon = new FontIcon("mdi2f-file-edit");
            private final JFXButton returnBtn = new JFXButton("", icon);

            @Override
            public void updateItem(MrItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !item.getStatus().contains(Utility.MR_RETURNED)) {
                    returnBtn.setStyle("-fx-background-color: #2196f3;");
                    icon.setIconSize(13);
                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    returnBtn.setOnAction(actionEvent -> {
                        //Utility.setSelectedMR(item);

                        //ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mr_return.fxml", Utility.getStackPane());
                    });
                    setGraphic(returnBtn);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column9.setStyle("-fx-alignment: center;");
        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column5);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
        this.mr_items_table.getColumns().add(column8);
        this.mr_items_table.getColumns().add(column9);
    }

    /**
     * Initializes the Employee details & MRs
     * @return void
     */
    public void setMRs(){
        this.fname_tf.setText(this.employee.getEmployeeFirstName());
        this.lname_tf.setText(this.employee.getEmployeeLastName());
        this.mname_tf.setText(this.employee.getEmployeeMidName());
        this.address_tf.setText(this.employee.getEmployeeAddress());
        if (this.employee.getPhone() != null)
            this.phone_tf.setText(this.employee.getPhone());
        if (this.employee.getDesignation() != null)
            this.designation_tf.setText(this.employee.getDesignation());
        this.initializeTable();
        getMRs();
    }
    /**
     * Retrieves the MRs of current employee
     * @return void
     */
    public void getMRs(){
        try {
            List<MR> emp_mrs = MrDAO.getMRsOfEmployee(this.employee);
            this.mr_list_view.setItems(FXCollections.observableArrayList(emp_mrs));
            this.mr_list_view.setOnMouseClicked(event -> {
                MR selected = (MR) mr_list_view.getSelectionModel().getSelectedItem();
                if (selected == null) return;
                printBtn.setDisable(false);
                date_approved_tf.setText(selected.getDateOfMR().toString());
                purpose_tf.setText(selected.getPurpose());
                try {
                    EmployeeInfo personnel = UserDAO.get(selected.getWarehousePersonnelId()).getEmployeeInfo();
                    released_tf.setText(personnel.getEmployeeFirstName()+" "+personnel.getEmployeeLastName());
                    EmployeeInfo recommending = UserDAO.get(selected.getRecommending()).getEmployeeInfo();
                    recommending_tf.setText(recommending.getEmployeeFirstName()+" "+recommending.getEmployeeLastName());
                    EmployeeInfo approve = UserDAO.get(selected.getApprovedBy()).getEmployeeInfo();
                    approved_tf.setText(approve.getEmployeeFirstName()+" "+approve.getEmployeeLastName());
                    populateTable(selected);
                    printBtn.setOnAction(actionEvent -> {
                        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                        FileChooser fileChooser = new FileChooser();
                        fileChooser.getExtensionFilters().addAll(
                                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
                        );
                        fileChooser.setInitialFileName("MR_report_"+selected.getId()+".pdf");
                        File selectedFile = fileChooser.showSaveDialog(stage);
                        if (selectedFile != null) {
                            printMR(selectedFile, selected, Utility.getStackPane());
                        }
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
    /**
     * Populates the MR items table when MR is selected from the list view
     * @param mr the MR object to display the MR items
     * @return void
     */
    public void populateTable(MR mr) {
        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getItems(mr));
                    this.mr_items_table.getItems().setAll(mrItems);
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
     * Generates a pdf containing the MR
     * @param selectedFile the pointer to the pdf file using the FileChooser
     * @param mr the MR object to generate
     * @param stackpane the stackpane for the dialogs to display
     * @return void
     */
    public static void printMR(File selectedFile, MR mr, StackPane stackpane){
        Platform.runLater(() -> {
            try {
                float[] columns = {.5f, .5f, 2.3f, .95f, .95f, .95f, 0.95f, 1.4f};
                PrintPDF mr_pdf = new PrintPDF(selectedFile, columns);
                //Create Header
                mr_pdf.header(null, "Memorandum Receipt for Equipment, Semi-expendable,".toUpperCase(), "and non-expendable property".toUpperCase());

                String[] mr_no = {"MR No: 2022-0042", "", "", mr.getDateOfMR().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy"))};
                int[] head_span = {3, 1, 1, 3};
                int[] head_aligns = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_CENTER};
                int[] head_fonts = {Font.BOLD, Font.NORMAL, Font.NORMAL, Font.BOLD};
                int[] head_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
                mr_pdf.other_details(mr_no, head_span, head_fonts, head_aligns,head_borders, true);

                EmployeeInfo user = UserDAO.get(mr.getWarehousePersonnelId()).getEmployeeInfo();

                String[] acknowledge = {"I HEREBY ACKNOWLEDGE to have received from "+user.getEmployeeFirstName().toUpperCase()+" "+user.getEmployeeLastName().toUpperCase()+", "+user.getDesignation() +", the following property for which I am responsible, " +
                        "subject to the provision of the usual accounting and auditing rules and regulations and which will be used for "+mr.getPurpose()+"."};
                int[] ack_span = {8};
                int[] ack_aligns = {Element.ALIGN_JUSTIFIED};
                int[] ack_fonts = {Font.NORMAL};
                int[] ack_borders = {Rectangle.NO_BORDER};
                mr_pdf.other_details(acknowledge, ack_span, ack_fonts, ack_aligns,ack_borders, true);

                //Create Table Header
                String[] headers = {"QTY", "Unit", "Description", "Property No.", "RR No.", "Unit Price", "Total Value", "Remarks"};
                int[] header_spans = {1, 1, 1, 1, 1, 1, 1, 1};
                mr_pdf.tableHeader(headers, header_spans);

                //Create Table Content
                List<MrItem> items = MrDAO.getItems(mr);
                ArrayList<String[]> rows = new ArrayList<>();
                int[] rows_aligns = {Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER,
                                     Element.ALIGN_CENTER, Element.ALIGN_LEFT,   Element.ALIGN_LEFT, Element.ALIGN_LEFT};
                for (MrItem item : items) {
                    String remarks = item.getRemarks();
                    if (item.getStatus().equals(Utility.MR_RETURNED)) {
                        remarks = Utility.MR_RETURNED+ " on " + item.getDateReturned();
                    }
                    String[] data = {item.getQty()+"", item.getStock().getUnit(), item.getStock().getDescription(), item.getStockID(),
                            item.getRrNo(), item.getStock().getPrice()+"", item.getQty()*item.getStock().getPrice()+"", remarks};
                    rows.add(data);
                }

                mr_pdf.tableContent(rows, header_spans, rows_aligns);

                //Create Footer
                int[] footer_spans = {4, 4};
                String[] signatorees = {"Recommending Approval:", "Approved:"};
                EmployeeInfo recommender = UserDAO.get(mr.getRecommending()).getEmployeeInfo();
                EmployeeInfo approver = UserDAO.get(mr.getApprovedBy()).getEmployeeInfo();
                String[] designations = {recommender.getDesignation(), approver.getDesignation()};
                String[] names = {recommender.getEmployeeFirstName()+" "+recommender.getEmployeeLastName().toUpperCase(), approver.getEmployeeFirstName()+" "+approver.getEmployeeLastName().toUpperCase()};
                mr_pdf.footer(signatorees, designations, names, footer_spans, true, Element.ALIGN_CENTER, Element.ALIGN_CENTER);

                int[] received_spans = {8};
                String[] received = {"Received:"};
                EmployeeInfo employee = mr.getEmployeeInfo();
                String[] designation = {employee.getDesignation()};
                String[] name = {employee.getEmployeeFirstName()+" "+employee.getEmployeeLastName().toUpperCase()};
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
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), stackpane, AlertDialogBuilder.DANGER_DIALOG);
            }
        });
    }
}
