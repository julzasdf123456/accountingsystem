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

    @FXML
    private JFXComboBox<String> mr_type;

    private ObservableList<MR> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.bindPages();
        this.initializeMRs();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @FXML
    public void searchMR(){
        String key = this.query_tf.getText();
        Platform.runLater(() -> {
            try {
                List<MR> current_mrs = MrDAO.searchMRs(key, Utility.MR_ACTIVE);
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
            Platform.runLater(() -> {
                try {
                    PrintPDF mr_pdf = new PrintPDF(selectedFile);

                    //Create Header
                    mr_pdf.header(LocalDate.now(), "MR Inventory Report".toUpperCase());


                    //Create Table Header
                    String[] headers = {"Item", "Qty", "Unit Price", "Date of MR", "Employee"};
                    int[] header_spans = {2, 1, 1, 1, 1};
                    mr_pdf.tableHeader(headers, header_spans);

                    //Create Table Content
                    List<MR> mrs = MrDAO.getMRs(Utility.MR_ACTIVE);
                    ArrayList<String[]> rows = new ArrayList<>();
                    for (MR mr : mrs) {
                        String[] data = {mr.getExtItem(), mr.getQuantity() + "", mr.getPrice() + "", mr.getDateOfMR().toString(), mr.getEmployeeFirstName() + " " + mr.getEmployeeLastName()};
                        rows.add(data);
                    }
                    int[] rows_aligns = {Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER, Element.ALIGN_LEFT};
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

    public void createTable() {

        TableColumn<MR, String> column0 = new TableColumn<>("Stock ID");
        column0.setMinWidth(150);
        column0.setCellValueFactory(new PropertyValueFactory<>("stockId"));
        column0.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column1 = new TableColumn<>("Item Name");
        column1.setMinWidth(400);
        column1.setCellValueFactory(new PropertyValueFactory<>("extItem"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column2 = new TableColumn<>("Qty");
        column2.setMinWidth(50);
        column2.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column3 = new TableColumn<>("Unit Price");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column4 = new TableColumn<>("Date of MR");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("dateOfMR"));
        column4.setStyle("-fx-alignment: center;");

        TableColumn<MR, MR> column5 = new TableColumn<>("Action");
        column4.setMinWidth(50);
        column5.setCellValueFactory(mr -> new ReadOnlyObjectWrapper<>(mr.getValue()));
        column5.setCellFactory(mrtable -> new TableCell<>(){
            FontIcon icon =  new FontIcon("mdi2e-eye");
            private final JFXButton viewButton = new JFXButton("", icon);

            @Override
            public void updateItem(MR item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    viewButton.setStyle("-fx-background-color: #2196f3;");
                    icon.setIconSize(13);
                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    viewButton.setOnAction(actionEvent -> {
                        Utility.setSelectedMR(item);

                        ModalBuilderForWareHouse.showModalFromXMLWithExitPath(WarehouseDashboardController.class, "../warehouse_view_mr_item_history.fxml", Utility.getStackPane(), "../warehouse_mr_inventory.fxml");
                    });
                    setGraphic(viewButton);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column7 = new TableColumn<>("Status");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("status"));
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column8 = new TableColumn<>("FirstName");
        column8.setMinWidth(150);
        column8.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        column8.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column9 = new TableColumn<>("LastName");
        column9.setMinWidth(150);
        column9.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        column9.setStyle("-fx-alignment: center-left;");

        this.mrItems =  FXCollections.observableArrayList();
        this.table.setPlaceholder(new Label("No item added"));

        this.table.getColumns().add(column0);
        this.table.getColumns().add(column1);
        this.table.getColumns().add(column2);
        this.table.getColumns().add(column3);
        this.table.getColumns().add(column4);
        this.table.getColumns().add(column7);
        this.table.getColumns().add(column8);
        this.table.getColumns().add(column9);
        this.table.getColumns().add(column5);
    }

    public void initializeMRs(){
        Platform.runLater(() -> {
            try {
                this.createTable();
                List<MR> current_mrs = MrDAO.getMRs(Utility.MR_ACTIVE);
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

    public void bindPages(){
        Platform.runLater(() -> {
            this.mr_type.getItems().clear();
            this.mr_type.getItems().add(Utility.MR_ACTIVE);
            this.mr_type.getItems().add(Utility.MR_RETURNED);
            this.mr_type.getSelectionModel().select(0);
            this.mr_type.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    try {
                        if (!mr_type.getSelectionModel().isEmpty()) {
                            String type = mr_type.getSelectionModel().getSelectedItem();
                            List<MR> current_mrs = MrDAO.getMRs(type);
                            this.mrItems = FXCollections.observableList(current_mrs);
                            this.num_mrs_lbl.setText(current_mrs.size()+" rows");
                            this.table.getItems().setAll(this.mrItems);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }
}
