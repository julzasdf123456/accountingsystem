package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewMRsController extends MenuControllerHandler implements Initializable  {

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView employeesTable;

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXTextField query_tf;

    private ObservableList<EmployeeInfo> employees = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeTable();
        this.populateTable(null);
        this.query_tf.setOnAction(actionEvent -> searchEmployee());
    }

    @FXML
    public void searchEmployee(){
        String key = this.query_tf.getText();
        this.populateTable(key);
    }


    public void initializeTable() {
        TableColumn<EmployeeInfo, String> column1 = new TableColumn<>("Employee ID");
        column1.setMinWidth(100);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column2 = new TableColumn<>("Last Name");
        column2.setMinWidth(175);
        column2.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column3 = new TableColumn<>("First Name");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column4 = new TableColumn<>("Middle Name");
        column4.setMinWidth(175);
        column4.setCellValueFactory(new PropertyValueFactory<>("employeeMidName"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column5 = new TableColumn<>("Designation");
        column5.setMinWidth(150);
        column5.setCellValueFactory(new PropertyValueFactory<>("designation"));
        column5.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, EmployeeInfo> column6 = new TableColumn<>("Action");
        column6.setMinWidth(50);
        column6.setCellValueFactory(emp -> new ReadOnlyObjectWrapper<>(emp.getValue()));
        column6.setCellFactory(mrtable -> new TableCell<>(){

            FontIcon viewIcon =  new FontIcon("mdi2e-eye");
            FontIcon printIcon = new FontIcon("mdi2p-printer");

            private final JFXButton viewButton = new JFXButton("", viewIcon);
            private final JFXButton printButton = new JFXButton("", printIcon);

            @Override
            protected void updateItem(EmployeeInfo employee, boolean b) {
                super.updateItem(employee, b);
                if (employee != null) {
                    Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
                    printButton.setStyle("-fx-background-color: #00AD8E;");
                    printIcon.setIconSize(13);
                    printIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    printButton.setOnAction(actionEvent1 -> {
                        Platform.runLater(() -> {
                            try {
                                printMR(employee, stage);
                            } catch (Exception e) {
                                e.printStackTrace();
                                AlertDialogBuilder.messgeDialog("System Warning", "Process failed due to: " + e.getMessage(),
                                        stackPane, AlertDialogBuilder.DANGER_DIALOG);
                            }
                        });
                    });

                    viewButton.setStyle("-fx-background-color: #2196f3;");
                    viewIcon.setIconSize(13);
                    viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                    viewButton.setOnAction(actionEvent -> {
                        Utility.setSelectedEmployee(employee);
                        ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_view_mr.fxml", Utility.getStackPane(),  "../view_mrs_controller.fxml");
                    });
                    HBox hBox = new HBox();
                    HBox filler = new HBox();
                    hBox.setHgrow(filler, Priority.ALWAYS);
                    hBox.setSpacing(5);
                    hBox.getChildren().add(viewButton);
                    hBox.getChildren().add(filler);
                    hBox.getChildren().add(printButton);
                    setGraphic(hBox);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column6.setStyle("-fx-alignment: center;");

        this.employees =  FXCollections.observableArrayList();
        this.employeesTable.setPlaceholder(new Label("No item added"));

        this.employeesTable.getColumns().add(column1);
        this.employeesTable.getColumns().add(column2);
        this.employeesTable.getColumns().add(column3);
        this.employeesTable.getColumns().add(column4);
        this.employeesTable.getColumns().add(column5);
        this.employeesTable.getColumns().add(column6);
    }

    public void populateTable(String key) {
        try {
            Platform.runLater(() -> {
                try {
                    ObservableList<EmployeeInfo> list;
                    if (key != null) {
                        list = FXCollections.observableList(MrDAO.getEmployeesWithMR(key));

                    }else{
                        list = FXCollections.observableList(MrDAO.getEmployeesWithMR());
                    }
                    this.employeesTable.getItems().setAll(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    public void printMR(EmployeeInfo employeeInfo, Stage stage) throws Exception{

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("MR_report_"+employeeInfo.getEmployeeLastName()+"_"+employeeInfo.getEmployeeFirstName()+".pdf");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            Platform.runLater(() -> {
                try {
                    PrintPDF mr_pdf = new PrintPDF(selectedFile);
                    //Create Header
                    mr_pdf.header(LocalDate.now(), "Memorandum Receipt".toUpperCase());

                    //Create Header details
                    String[] employee_details = {"Employee:", employeeInfo.getFullName(), "Designation:", employeeInfo.getDesignation()};
                    int[] emp_span = {1, 2, 1, 2};
                    int[] emp_aligns = {Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT, Element.ALIGN_LEFT};
                    int[] emp_borders = {Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER, Rectangle.NO_BORDER};
                    int[] emp_fonts = {Font.NORMAL, Font.BOLD, Font.NORMAL, Font.BOLD};
                    mr_pdf.other_details(employee_details, emp_span, emp_fonts, emp_aligns, emp_borders, true);

                    //Create Table Header
                    String[] headers = {"Item", "Qty", "Unit Price", "Date of MR", "Remarks"};
                    int[] header_spans = {2, 1, 1, 1, 1};
                    mr_pdf.tableHeader(headers, header_spans);

                    //Create Table Content
                    List<MR> mrs = MrDAO.getMRsOfEmployee(employeeInfo);
                    ArrayList<String[]> rows = new ArrayList<>();
                    /*for (MR mr : mrs) {
                        String rdate = "";
                        if (mr.getStatus().equals(Utility.MR_RETURNED)) {
                            rdate = " on " + mr.getDateOfReturn() + ", " + mr.getRemarks();
                        }
                        String[] data = {mr.getExtItem(), mr.getQuantity() + "", mr.getPrice() + "", mr.getDateOfMR().toString(), mr.getStatus() + rdate};
                        rows.add(data);
                    }*/
                    int[] rows_aligns = {Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_CENTER};
                    mr_pdf.tableContent(rows, header_spans, rows_aligns);

                    //Create Footer
                    int[] footer_spans = {2, 4};
                    EmployeeInfo user = ActiveUser.getUser().getEmployeeInfo();
                    String[] prepared = {"Prepared by", ""};
                    String[] designations = {user.getDesignation(), ""};
                    String[] names = {user.getEmployeeFirstName() + " " + user.getEmployeeLastName(), ""};
                    mr_pdf.footer(prepared, designations, names, footer_spans, true);
                    mr_pdf.generate();
                }catch(Exception e){
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while generating the pdf due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        }
    }

}
