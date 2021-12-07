//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTreeTableView;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.util.Callback;
import jdk.jfr.EventType;
import org.kordamp.ikonli.javafx.FontIcon;

public class  WarehouseDashboardController extends MenuControllerHandler implements Initializable, SubMenuHelper {
    public List<JFXButton> subMenus;
    public JFXButton options = new JFXButton("Options");
    public JFXButton reports = new JFXButton("Reports");
    @FXML
    private StackPane stackPane;
    @FXML
    private VBox displayBox;

    @FXML
    private TableView tableView;
    @FXML
    public  Label pendingApprovals_lbl, pendingReleases_lbl, critical_lbl, display_lbl;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private int LIMIT = HomeController.ROW_PER_PAGE;
    private int COUNT = 0;

    ContextMenuHelper contextMenuHelper = new ContextMenuHelper();
    MenuItem createInventory = new MenuItem("Create Inventory");
    MenuItem viewAllStocks = new MenuItem("View All Stocks");
    MenuItem trash = new MenuItem("Trash");
    MenuItem inventoryReport = new MenuItem("Inventory Report");
    MenuItem liquidationReport = new MenuItem("Liquidation Report");
    MenuItem stockEntryReport = new MenuItem("Stock Entry Report");

    public WarehouseDashboardController() {
        options = new JFXButton("Options");
        reports = new JFXButton("Reports");

        contextMenuHelper = new ContextMenuHelper();

        options.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(options, viewAllStocks, trash)
                    .show(options, NodeLocator.getNodeX(options), NodeLocator.getNodeY(options));
        });

        reports.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(reports, inventoryReport, liquidationReport, stockEntryReport)
                    .show(reports, NodeLocator.getNodeX(reports), NodeLocator.getNodeY(reports));
        });
    };
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeCounts();
        display_lbl.setText(" ");
        mirsPendingApproval(null);
    }

    @FXML
    private void mirsPendingApproval(MouseEvent event) {
        display_lbl.setText("Pending Approval");
        initializedTable("Pending");
        try {
            ObservableList<MIRS> observableList = FXCollections.observableList(MirsDAO.getMIRSByStatus("Pending"));
            tableView.getItems().setAll(observableList);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void mirsPendingReleases(MouseEvent event) {
        display_lbl.setText("Pending Releases");
        initializedTable("Releasing");
        try {
            ObservableList<MIRS> observableList = FXCollections.observableList(MirsDAO.getMIRSByStatus("Releasing"));
            tableView.getItems().setAll(observableList);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    void viewCriticalItems(MouseEvent event) {
        this.bindPages();
        this.setCriticalCount();
        this.initializeCriticalStocks();
       // Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_critical_stock.fxml"));
    }

    private void initializedTable(String s){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        TableColumn<MIRS, String> column0 = new TableColumn<>("Date Filed");
        column0.setCellValueFactory(new PropertyValueFactory<>("DateFiled"));
        column0.setMinWidth(150);
        column0.setStyle("-fx-alignment: center;");

        TableColumn<MIRS, String> column1 = new TableColumn<>("MIRS Number");
        column1.setCellValueFactory(new PropertyValueFactory<>("Id"));
        column1.setMinWidth(150);
        column1.setStyle("-fx-alignment: center;");

        TableColumn<MIRS, String> column2 = new TableColumn<>("Requisitioner");
        column2.setCellValueFactory(cellData -> {
            try {
                return new SimpleStringProperty(cellData.getValue().getRequisitioner().getFullName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
        column2.setMinWidth(200);
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MIRS, String> column3 = new TableColumn<>("Purpose");
        column3.setCellValueFactory(new PropertyValueFactory<>("Purpose"));
        column3.setMinWidth(250);
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MIRS, String> column4 = new TableColumn<>("Action");
        column4.setStyle("-fx-alignment: center;");
        Callback<TableColumn<MIRS, String>, TableCell<MIRS, String>> viewBtn
                = //
                new Callback<TableColumn<MIRS, String>, TableCell<MIRS, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRS, String> param) {
                        final TableCell<MIRS, String> cell = new TableCell<MIRS, String>() {

                            Button btn = new Button("view");
                            FontIcon icon = new FontIcon("mdi2e-eye");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                btn.setStyle("-fx-background-color: #2196f3");
                                btn.setGraphic(icon);
                                btn.setGraphicTextGap(5);
                                btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        MIRS mirs = getTableView().getItems().get(getIndex());
                                        try {
                                            Utility.setActiveMIRS(mirs);
                                            if(s.equals("Pending")){
                                                ModalBuilderForWareHouse.showModalFromXML(WarehouseDashboardController.class, "../warehouse_mirs_approval_form.fxml",stackPane);
                                            }else if (s.equals("Releasing")){
                                                ModalBuilderForWareHouse.showModalFromXML(WarehouseDashboardController.class, "../warehouse_mirs_releasing_form.fxml",stackPane);
                                            }
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
        column4.setCellFactory(viewBtn);

        TableColumn<MIRS, String> column5 = new TableColumn<>("Status");
        column5.setStyle("-fx-alignment: center;");
        Callback<TableColumn<MIRS, String>, TableCell<MIRS, String>> statusIcon
                = //
                new Callback<TableColumn<MIRS, String>, TableCell<MIRS, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MIRS, String> param) {
                        final TableCell<MIRS, String> cell = new TableCell<MIRS, String>() {
                            Label status = new Label("     ");
                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                status.setStyle("-fx-background-color: #f44336; -fx-background-radius: 12");
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    setGraphic(status);
                                    setText(null);
                                }
                            }
                        };
                        return cell;
                    }
                };
        column5.setCellFactory(statusIcon);

        tableView.setFixedCellSize(35);
        tableView.setPlaceholder(new Label("No rows to display"));
        tableView.getColumns().add(column0);
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);

        //display status column only if displaying Pending Approval MIRS
        if(s.equals("Pending"))
            tableView.getColumns().add(column5);
    }

    public void createTableForCriticalItem(){
        tableView.getItems().clear();
        tableView.getColumns().clear();

        TableColumn<Stock, String> column1 = new TableColumn<>("Stock Name");
        column1.setMinWidth(150);
        column1.setCellValueFactory(new PropertyValueFactory<>("stockName"));

        TableColumn<Stock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(300);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Stock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Stock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Stock, String> column5 = new TableColumn<>("Unit");
        column5.setCellValueFactory(new PropertyValueFactory<>("unit"));

        TableColumn<Stock, String> column6 = new TableColumn<>("Quantity");
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        this.tableView.getColumns().removeAll();
        this.tableView.getColumns().add(column1);
        this.tableView.getColumns().add(column2);
        this.tableView.getColumns().add(column3);
        this.tableView.getColumns().add(column4);
        this.tableView.getColumns().add(column5);
        this.tableView.getColumns().add(column7);
        this.tableView.getColumns().add(column6);
    }

    public void initializeCriticalStocks(){
        Platform.runLater(() -> {
            try {
                this.createTableForCriticalItem();
                ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getCritical(LIMIT, 0));
                this.tableView.getItems().setAll(stocks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void bindPages(){
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countCritical();
            } catch (Exception e) {
                e.printStackTrace();
            }

            double div = COUNT / LIMIT;
            double pages = Math.ceil(div);

            if (COUNT % LIMIT > 0)
                pages++;

            this.page_cb.getItems().clear();
            for (int i = 1; i <= pages; i++) {
                this.page_cb.getItems().add(i);
            }

            this.page_cb.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    try {
                        if (!page_cb.getSelectionModel().isEmpty()) {
                            int offset = (page_cb.getSelectionModel().getSelectedItem() - 1) * LIMIT;
                            ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getCritical(LIMIT, offset));
                            this.tableView.getItems().setAll(stocks);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            });
        });
    }

    private void setCriticalCount() {
        Platform.runLater(() -> {
            try {
                COUNT = StockDAO.countCritical();
                this.display_lbl.setText(COUNT +" Critical Items");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void setSubMenus(FlowPane flowPane) {
        this.subMenus = new ArrayList();
        this.subMenus.add(this.options);
        this.subMenus.add(this.reports);
        flowPane.getChildren().removeAll(new Node[0]);
        flowPane.getChildren().setAll(this.subMenus);
    }

    public void handleContentReplacements(AnchorPane container, Label titleHolder) {
        this.createInventory.setOnAction((actionEvent) -> {
            titleHolder.setText("Create Inventory");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockController.class, "../warehouse_create_inventory_controller.fxml"));
        });
        viewAllStocks.setOnAction(actionEvent -> {
            titleHolder.setText("View All Stocks");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockController.class, "../warehouse_stock.fxml"));
        });

        trash.setOnAction(actionEvent -> {
            titleHolder.setText("Trash");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(TrashStockController.class, "../warehouse_trash_stock.fxml"));
        });

        inventoryReport.setOnAction(actionEvent -> {
            titleHolder.setText("Inventory Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(InventoryReportController.class, "../warehouse_inventory_report.fxml"));
        });

        stockEntryReport.setOnAction(actionEvent -> {
            titleHolder.setText("Stock Entry Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockEntryReportController.class, "../warehouse_stock_entry_report.fxml"));
        });

        liquidationReport.setOnAction(actionEvent -> {
            titleHolder.setText("Stock Liquidation Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockEntryReportController.class, "../warehouse_stock_liquidation_report.fxml"));
        });
    }

    public void initializeCounts(){
        try {
            this.critical_lbl.setText(""+ StockDAO.countCritical());
            this.pendingApprovals_lbl.setText(""+MirsDAO.countMIRSByStatus("Pending"));
            this.pendingReleases_lbl.setText(""+MirsDAO.countMIRSByStatus("Releasing"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
