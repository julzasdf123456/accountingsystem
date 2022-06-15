package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.itextpdf.text.Element;
import com.jfoenix.controls.*;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MRInventoryController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView table;

    @FXML
    private Label num_mrs_lbl;

    @FXML
    private JFXTextField query_tf;


    private ObservableList<MrItem> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //Initializes the MRed Items
        this.initializeMRs();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }
    /**
     * Search for stocks based on search string and displays the results in the table
     * @return void
     */
    @FXML
    public void searchMR(){
        String key = this.query_tf.getText();
        Platform.runLater(() -> {
            try {
                List<MrItem> current_mrs = MrDAO.searchMRItems(key, Utility.MR_ACTIVE);
                this.mrItems = FXCollections.observableList(current_mrs);
                if (this.table.getItems() != null){
                    this.table.getItems().removeAll();
                }
                this.table.getItems().setAll(mrItems);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Generate and saves the current list of items in active MRs in pdf format
     * @return void
     */
    @FXML
    public void printReport(){
        Stage stage = (Stage) Utility.getContentPane().getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
        );
        fileChooser.setInitialFileName("MR_Inventory_Report_"+ LocalDate.now()+".pdf");
        File selectedFile = fileChooser.showSaveDialog(stage);
        if (selectedFile != null) {
            printActiveMRItems(selectedFile, Utility.getStackPane());
        }
    }
    /**
     * Creates the MR item table
     * @return void
     */
    public void createTable() {

        TableColumn<MrItem, String> column = new TableColumn<>("Stock ID");
        column.setMinWidth(100);
        column.setCellValueFactory(new PropertyValueFactory<>("stockID"));
        column.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column0 = new TableColumn<>("RR No");
        column0.setMinWidth(100);
        column0.setCellValueFactory(new PropertyValueFactory<>("rrNo"));
        column0.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column1 = new TableColumn<>("Description");
        column1.setMinWidth(275);
        column1.setCellValueFactory(new PropertyValueFactory<>("description"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column2 = new TableColumn<>("Quantity");
        column2.setMinWidth(75);
        column2.setCellValueFactory(new PropertyValueFactory<>("qty"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column3 = new TableColumn<>("Unit Price");
        column3.setMinWidth(75);
        column3.setCellValueFactory(item -> {
            try {
                double cost = 0;
                if (item.getValue().getStockID() == null) {
                    cost = item.getValue().getPrice();
                }else{
                    ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                    if (rc == null) {
                        cost = item.getValue().getStock().getPrice();
                    } else {
                        cost = rc.getUnitCost();
                    }
                }
                return new ReadOnlyObjectWrapper<>(cost);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, Double> column3a = new TableColumn<>("Total");
        column3a.setMinWidth(95);
        column3a.setCellValueFactory(item -> {
            try {
                double cost = 0;
                if (item.getValue().getStockID() == null) {
                    cost = item.getValue().getPrice();
                }else {
                    ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                    if (rc == null) {
                        cost = item.getValue().getStock().getPrice();
                    } else {
                        cost = rc.getUnitCost();
                    }
                }
                return new ReadOnlyObjectWrapper<>(cost*item.getValue().getQty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3a.setStyle("-fx-alignment: center-left;");
        TableColumn<MrItem, String> column3b = new TableColumn<>("MR No.");
        column3b.setMinWidth(75);
        column3b.setCellValueFactory(new PropertyValueFactory<>("mrNo"));
        column3b.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Date of MR");
        column4.setMinWidth(75);
        column4.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(MrDAO.get(item.getValue().getMrNo()).getDateOfMR().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, MrItem> column5 = new TableColumn<>("Action");
        column5.setMinWidth(50);
        column5.setCellValueFactory(mr -> new ReadOnlyObjectWrapper<>(mr.getValue()));
        column5.setCellFactory(mrtable -> new TableCell<>(){
            FontIcon icon =  new FontIcon("mdi2e-eye-circle");
            private final JFXButton viewButton = new JFXButton("", icon);

            @Override
            public void updateItem(MrItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    icon.setIconSize(24);
                    icon.setIconColor(Paint.valueOf(ColorPalette.INFO));
                    viewButton.setOnAction(actionEvent -> {
                        Utility.setSelectedObject(item);
                        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_view_mr_item_history.fxml", Utility.getStackPane());
                    });
                    setGraphic(viewButton);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column7 = new TableColumn<>("Remarks");
        column7.setMinWidth(125);
        column7.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Employee");
        column8.setMinWidth(150);
        column8.setCellValueFactory(item -> {
            try {
                MR mr = MrDAO.get(item.getValue().getMrNo());
                return new ReadOnlyObjectWrapper<>(mr.getEmployeeInfo().getEmployeeFirstName()+" "+mr.getEmployeeInfo().getEmployeeLastName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column8.setStyle("-fx-alignment: center-left;");

        this.mrItems =  FXCollections.observableArrayList();
        this.table.setPlaceholder(new Label("No item added"));

        this.table.getColumns().add(column);
        this.table.getColumns().add(column0);
        this.table.getColumns().add(column1);
        this.table.getColumns().add(column2);
        this.table.getColumns().add(column3);
        this.table.getColumns().add(column3a);
        this.table.getColumns().add(column3b);
        this.table.getColumns().add(column4);
        this.table.getColumns().add(column7);
        this.table.getColumns().add(column8);
        this.table.getColumns().add(column5);
    }
    /**
     * Initializes the MR items and displays them in the table
     * @return void
     */
    public void initializeMRs(){
        Platform.runLater(() -> {
            try {
                this.createTable();
                List<MrItem> current_mrs = MrDAO.searchMRItems("", Utility.MR_ACTIVE);
                this.mrItems = FXCollections.observableList(current_mrs);
                this.num_mrs_lbl.setText(current_mrs.size()+" rows");
                if (this.table.getItems() != null){
                    this.table.getItems().removeAll();
                }
                this.table.getItems().setAll(mrItems);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * Generates a pdf containing all active MR items
     * @param selectedFile the pointer to the pdf file using the FileChooser
     * @param stackPane the stackpane for the dialogs to display
     * @return void
     */
    public static void printActiveMRItems(File selectedFile, StackPane stackPane){
        Platform.runLater(() -> {
            try {
                float[] columns = {.75f, .75f, 2f, .75f, .75f, .75f, .75f, .75f, 1f};
                PrintPDF mr_pdf = new PrintPDF(selectedFile, columns);

                //Create Header
                mr_pdf.header(LocalDate.now(), "MR Inventory Report".toUpperCase());

                //Create Table Header
                String[] headers = {"Stock ID.", "RR No.", "Item", "Qty", "Unit Price", "Total", "MR No.", "Date of MR", "Assigned To"};
                int[] header_spans = {1, 1, 1, 1, 1, 1, 1, 1, 1};
                mr_pdf.tableHeader(headers, header_spans);

                //Create Table Content
                List<MrItem> items = MrDAO.getMRItems();
                ArrayList<String[]> rows = new ArrayList<>();
                for (MrItem item : items) {
                    MR mr = MrDAO.get(item.getMrNo());
                    EmployeeInfo employeeInfo = mr.getEmployeeInfo();
                    String desc = item.getDescription();
                    if (item.getStockID() != null)
                        desc = item.getStock().getDescription();
                    String unit = "piece";
                    if (item.getStockID() != null)
                        unit = item.getStock().getUnit();
                    double price = item.getPrice();
                    if (item.getStockID() != null)
                        price = item.getStock().getPrice();
                    String[] data = {item.getStockID(), item.getRrNo(), desc, item.getQty() + " "+unit, price + "", item.getQty()*price+"", item.getMrNo(), mr.getDateOfMR().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), employeeInfo.getEmployeeFirstName() + " " + employeeInfo.getEmployeeLastName()};
                    rows.add(data);
                }
                int[] rows_aligns = {Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_LEFT,
                        Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_LEFT};
                mr_pdf.tableContent(rows, header_spans, rows_aligns);

                //Create Footer
                int[] footer_spans = {5, 4};
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
