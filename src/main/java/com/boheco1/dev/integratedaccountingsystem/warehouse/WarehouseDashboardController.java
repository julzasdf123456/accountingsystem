//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MIRSSignatoryDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.boheco1.dev.integratedaccountingsystem.objects.Stock;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.*;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

public class  WarehouseDashboardController extends MenuControllerHandler implements Initializable, SubMenuHelper {
    public List<JFXButton> subMenus;
    public JFXButton options = new JFXButton("Options");
    public JFXButton reports = new JFXButton("Reports");

    @FXML
    private VBox displayBox;

    @FXML
    private JFXTextField searchMIRS;

    @FXML
    private TableView tableView;
    @FXML
    public  Label pendingApprovals_lbl, pendingReleases_lbl, critical_lbl, display_lbl, mr_lbl, mr_inventory_lbl;

    @FXML
    private JFXComboBox<Integer> page_cb;

    private int LIMIT = Utility.ROW_PER_PAGE;
    private int COUNT = 0;



    private String APPROVAL = "Pending Approval";
    private String RELEASES = "Pending Releases";


    ContextMenuHelper contextMenuHelper = new ContextMenuHelper();
    MenuItem createInventory = new MenuItem("Create Inventory");
    MenuItem viewAllStocks = new MenuItem("View All Stocks");
    MenuItem viewAllMirs = new MenuItem("View All MIRS");
    MenuItem viewAllMRTs = new MenuItem("View All MRT");
    MenuItem viewMRItems = new MenuItem("View All MRs");
    MenuItem trash = new MenuItem("Trash");
    MenuItem inventoryReport = new MenuItem("Inventory Report");

    MenuItem inventoryMRReport = new MenuItem("MR Inventory Report");
    MenuItem liquidationReport = new MenuItem("Liquidation Report");
    MenuItem stockEntryReport = new MenuItem("Stock Entry Report");
    MenuItem receivingReport = new MenuItem("Receiving Report");
    MenuItem mctReport = new MenuItem("MCT Report");
    MenuItem summaryOfCharges = new MenuItem("Summary of Charges");

    MenuItem materialsInventory = new MenuItem("Construction Materials");

    public WarehouseDashboardController() {
        options = new JFXButton("Options");
        reports = new JFXButton("Reports");

        contextMenuHelper = new ContextMenuHelper();

        options.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(options, viewAllStocks, viewAllMirs, viewAllMRTs, viewMRItems, trash)
                    .show(options, NodeLocator.getNodeX(options), NodeLocator.getNodeY(options));
        });

        reports.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(reports, inventoryReport, inventoryMRReport, liquidationReport, stockEntryReport, receivingReport, mctReport, summaryOfCharges, materialsInventory)
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
        this.searchMIRS.setVisible(true);
        display_lbl.setText(APPROVAL);
        initializedTable(Utility.PENDING);
        try {
            ObservableList<MIRS> observableList = FXCollections.observableList(MirsDAO.getMIRSByStatus(Utility.PENDING));
            tableView.getItems().setAll(observableList);
            tableView.refresh();
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void mirsPendingReleases(MouseEvent event) {
        this.searchMIRS.setVisible(true);
        display_lbl.setText(RELEASES);
        initializedTable(Utility.RELEASING);
        try {
            ObservableList<MIRS> observableList = FXCollections.observableList(MirsDAO.getMIRSByStatus(Utility.RELEASING));
            tableView.getItems().setAll(observableList);
            tableView.refresh();
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void viewCriticalItems(MouseEvent event) {
        this.searchMIRS.setVisible(false);
        this.bindPages();
        this.setCriticalCount();
        this.initializeCriticalStocks();
       // Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_critical_stock.fxml"));
    }

    @FXML
    private void viewMRItems(MouseEvent event) {
        this.searchMIRS.setVisible(false);
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(MRInventoryController.class, "../warehouse_mr_inventory.fxml"));
    }

    @FXML
    private void viewEmployeesWithMR(MouseEvent event) {
        this.searchMIRS.setVisible(false);
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(ViewMRsController.class, "../view_mrs_controller.fxml"));
    }

    @FXML
    private void searchMIRS(ActionEvent event) {
        ObservableList<MIRS> observableList = null;
        try {
            if(display_lbl.getText().equals(APPROVAL)){
                initializedTable(Utility.PENDING);
                observableList = FXCollections.observableList(MirsDAO.searchMIRS(searchMIRS.getText(), Utility.PENDING));
            }else if(display_lbl.getText().equals(RELEASES)){
                initializedTable(Utility.RELEASING);
                observableList = FXCollections.observableList(MirsDAO.searchMIRS(searchMIRS.getText(), Utility.RELEASING));
            }
            tableView.getItems().setAll(observableList);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }


    @FXML
    private void tableViewClicked(MouseEvent event) {

        Object selectedItem = tableView.getSelectionModel().getSelectedItem();
        if(selectedItem == null)
            return;

        if(selectedItem instanceof MIRS && event.getClickCount() == 2){
            MIRS mirs = (MIRS) selectedItem;
            try {
                Utility.setActiveMIRS(mirs);
                if(display_lbl.getText().equals(APPROVAL)){
                    ModalBuilderForWareHouse.showModalFromXML(WarehouseDashboardController.class, "../warehouse_mirs_approval.fxml",Utility.getStackPane());
                }else if(display_lbl.getText().equals(RELEASES)){
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSReleasingController.class, "../warehouse_mirs_releasing.fxml"));
                    options.setVisible(false);
                    reports.setVisible(false);
                    //ModalBuilderForWareHouse.showModalFromXML(WarehouseDashboardController.class, "../warehouse_mirs_releasing.fxml",Utility.getStackPane());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void initializedTable(String s){
        tableView.getItems().clear();
        tableView.getColumns().clear();
        TableColumn<MIRS, String> column0 = new TableColumn<>("Date Filed");
        column0.setCellValueFactory(new PropertyValueFactory<>("DateFiled"));
        column0.setPrefWidth(100);
        column0.setMaxWidth(100);
        column0.setMinWidth(100);
        column0.setStyle("-fx-alignment: center;");

        TableColumn<MIRS, String> column1 = new TableColumn<>("MIRS Number");
        column1.setCellValueFactory(new PropertyValueFactory<>("Id"));
        column1.setPrefWidth(200);
        column1.setMaxWidth(200);
        column1.setMinWidth(200);
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
        column2.setPrefWidth(200);
        column2.setMaxWidth(200);
        column2.setMinWidth(200);
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<MIRS, String> column3 = new TableColumn<>("Purpose");
        column3.setCellValueFactory(new PropertyValueFactory<>("Purpose"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MIRS, String> column5 = new TableColumn<>("Status");
        column5.setStyle("-fx-alignment: center;");
        column5.setPrefWidth(80);
        column5.setMaxWidth(80);
        column5.setMinWidth(80);
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

                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    try {
                                        Timer timer = new java.util.Timer();
                                        timer.schedule(new TimerTask() {
                                        public void run() {
                                            Platform.runLater(new Runnable() {
                                                public void run() {
                                                    try {
                                                        if(Utility.getSelectedObject() instanceof Label) {
                                                            Label titleLabel = (Label) Utility.getSelectedObject();

                                                            if (titleLabel.getText().equals("Warehouse Dashboard")) {
                                                                int index = getIndex();
                                                                if (index >= 0) {
                                                                    if (getTableView().getItems().get(index) instanceof MIRS && getTableView().getItems().get(index) != null) {
                                                                        MIRS mirs = getTableView().getItems().get(index);
                                                                        int count = MIRSSignatoryDAO.getSignatoryCount(mirs.getId());
                                                                        if (count >= 2) {
                                                                            status.setStyle("-fx-background-color: " + ColorPalette.DANGER + "; -fx-background-radius: 12; -fx-font-size: 14");
                                                                        } else if (count == 1) {
                                                                            status.setStyle("-fx-background-color: " + ColorPalette.WARNING + "; -fx-background-radius: 12; -fx-font-size: 14");
                                                                        } else if (count == 0) {
                                                                            status.setStyle("-fx-background-color: " + ColorPalette.SUCCESS + "; -fx-background-radius: 12; -fx-font-size: 14");
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                       }
                                        }, 0, 10000); // 10 seconds
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
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
        //tableView.getColumns().add(column2); //Hide Requisitioner column
        tableView.getColumns().add(column3);
        //tableView.getColumns().add(column4);


        //display status column only if displaying Pending Approval MIRS
        if(display_lbl.getText().equals(APPROVAL))
            tableView.getColumns().add(column5);

        tableView.refresh();
    }

    public void createTableForCriticalItem(){
        tableView.getItems().clear();
        tableView.getColumns().clear();

        TableColumn<Stock, String> column1 = new TableColumn<>("Stock ID");
        column1.setMinWidth(75);
        column1.setCellValueFactory(stock -> {
            String neaCode = stock.getValue().getNeaCode();
            String value = neaCode;
            if (neaCode == null || neaCode.length() == 0) {
                value = stock.getValue().getLocalCode();
            }
            return new ReadOnlyObjectWrapper<>(value);
        });

        TableColumn<Stock, String> column2 = new TableColumn<>("Description");
        column2.setMinWidth(400);
        column2.setCellValueFactory(new PropertyValueFactory<>("description"));

        TableColumn<Stock, String> column3 = new TableColumn<>("Brand");
        column3.setMinWidth(150);
        column3.setCellValueFactory(new PropertyValueFactory<>("brand"));

        TableColumn<Stock, String> column4 = new TableColumn<>("Model");
        column4.setMinWidth(150);
        column4.setCellValueFactory(new PropertyValueFactory<>("model"));

        TableColumn<Stock, String> column6 = new TableColumn<>("Quantity");
        column6.setMinWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<Stock, String> column7 = new TableColumn<>("Price");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("price"));
        column7.setStyle("-fx-alignment: center-left;");
        this.tableView.getColumns().removeAll();
        this.tableView.getColumns().add(column1);
        this.tableView.getColumns().add(column2);
        this.tableView.getColumns().add(column3);
        this.tableView.getColumns().add(column4);
        this.tableView.getColumns().add(column6);
        this.tableView.getColumns().add(column7);
    }

    public void initializeCriticalStocks(){
        Platform.runLater(() -> {
            try {
                this.createTableForCriticalItem();
                ObservableList<Stock> stocks = FXCollections.observableList(StockDAO.getCritical(LIMIT, 0));
                this.tableView.getItems().setAll(stocks);
                this.tableView.refresh();
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

        viewAllMirs.setOnAction(actionEvent -> {
            titleHolder.setText("MIRS Repository");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSViewAllController.class, "../warehouse_mirs_view_all.fxml"));
        });

        viewAllMRTs.setOnAction(actionEvent -> {
            titleHolder.setText("MRT Repository");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(MIRSViewAllController.class, "../warehouse_mrt_view_all.fxml"));
        });

        viewMRItems.setOnAction(actionEvent -> {
            titleHolder.setText("View All MRs");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(ViewMRsController.class, "../view_mrs_controller.fxml"));
        });

        trash.setOnAction(actionEvent -> {
            titleHolder.setText("Trash");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(TrashStockController.class, "../warehouse_trash_stock.fxml"));
        });

        inventoryReport.setOnAction(actionEvent -> {
            titleHolder.setText("Inventory Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(InventoryReportController.class, "../warehouse_inventory_report.fxml"));
        });

        inventoryMRReport.setOnAction(actionEvent -> {
            titleHolder.setText("MR Inventory Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(MRInventoryController.class, "../warehouse_mr_inventory.fxml"));
        });

        stockEntryReport.setOnAction(actionEvent -> {
            titleHolder.setText("Stock Entry Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockEntryReportController.class, "../warehouse_stock_entry_report.fxml"));
        });

        liquidationReport.setOnAction(actionEvent -> {
            titleHolder.setText("Stock Liquidation Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(StockEntryReportController.class, "../warehouse_stock_liquidation_report.fxml"));
        });

        receivingReport.setOnAction(actionEvent -> {
            titleHolder.setText("Receiving Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(ReceivingReportController.class, "../warehouse_receiving_report.fxml"));
        });

        mctReport.setOnAction(actionEvent -> {
            titleHolder.setText("MCT Report");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(ReceivingReportController.class, "../view_all_mct_controller.fxml"));
        });

        summaryOfCharges.setOnAction(actionEvent -> {
            titleHolder.setText("Summary of Charges");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(ReceivingReportController.class, "../view_summary_charges.fxml"));
        });

        materialsInventory.setOnAction(actionEvent -> {
            titleHolder.setText("Inventory of Construction Materials");
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(ReceivingReportController.class, "../materials_inventory_report.fxml"));
        });
    }

    public void initializeCounts(){
        try {
            this.critical_lbl.setText(""+ StockDAO.countCritical());
            this.pendingApprovals_lbl.setText(""+MirsDAO.countMIRSByStatus(Utility.PENDING));
            this.pendingReleases_lbl.setText(""+MirsDAO.countMIRSByStatus(Utility.RELEASING));
            this.mr_lbl.setText(""+MrDAO.countEmployeesWithMRs());
            this.mr_inventory_lbl.setText(""+MrDAO.countMRs("active"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
