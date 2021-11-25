//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MIRS;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXTreeTableView;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import org.kordamp.ikonli.javafx.FontIcon;

public class WarehouseDashboardController extends MenuControllerHandler implements Initializable, SubMenuHelper {
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
    private Label pendingApprovals_lbl, pendingReleases_lbl, critical_lbl, display_lbl;

    //used by pending MIRS TableView selected item
    public static MIRS activeMIRS;

    ContextMenuHelper contextMenuHelper = new ContextMenuHelper();
    MenuItem createInventory = new MenuItem("Create Inventory");
    MenuItem viewAllStocks = new MenuItem("View All Stocks");
    MenuItem trash = new MenuItem("Trash");
    MenuItem inventoryReport = new MenuItem("Inventory Report");
    MenuItem liquidationReport = new MenuItem("Liquidation Report");
    MenuItem stockeEntryReport = new MenuItem("Stock Entry Report");

    public WarehouseDashboardController() {
        options = new JFXButton("Options");
        reports = new JFXButton("Reports");

        contextMenuHelper = new ContextMenuHelper();

        options.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(options, viewAllStocks, trash)
                    .show(options, NodeLocator.getNodeX(options), NodeLocator.getNodeY(options));
        });

        reports.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(reports, inventoryReport, liquidationReport, stockeEntryReport)
                    .show(reports, NodeLocator.getNodeX(reports), NodeLocator.getNodeY(reports));
        });
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeCounts();
        display_lbl.setText(" ");
    }

    @FXML
    private void mirsPendingApproval(MouseEvent event) {
        display_lbl.setText("Pending Approval");
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
                                    activeMIRS = mirs;
                                    ModalBuilder.showModalFromXML(WarehouseDashboardController.class, "../warehouse_mirs_pending_approval.fxml",stackPane);
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

        tableView.setFixedCellSize(40);
        tableView.setPlaceholder(new Label("No rows to display"));
        tableView.getColumns().add(column0);
        tableView.getColumns().add(column1);
        tableView.getColumns().add(column2);
        tableView.getColumns().add(column3);
        tableView.getColumns().add(column4);

        try {
            ObservableList<MIRS> observableList = FXCollections.observableList(MirsDAO.getAllPending());
            tableView.getItems().setAll(observableList);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("System Error", this.getClass().getName() +": "+ e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @FXML
    private void mirsPendingReleases(MouseEvent event) {
        display_lbl.setText("Pending Releases");
        tableView.getColumns().clear();
        TableColumn<MIRS, String> column0 = new TableColumn<>("Date Filed");
        column0.setCellValueFactory(new PropertyValueFactory<>("DateFiled"));
        column0.setMinWidth(150);
        column0.setStyle("-fx-alignment: center;");
    }

    @FXML
    void viewCriticalItems(MouseEvent event) {
        System.out.println("Clicked critical");
        Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_critical_stock.fxml"));
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
    }

    public void initializeCounts(){
        Platform.runLater(() -> {
            try {
                critical_lbl.setText(""+ StockDAO.countCritical());
                pendingApprovals_lbl.setText(""+MirsDAO.countPending());
                pendingReleases_lbl.setText(""+MirsDAO.countPendingReleases());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
