//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.MirsDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContextMenuHelper;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.NodeLocator;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

public class WarehouseDashboardController extends MenuControllerHandler implements Initializable, SubMenuHelper {
    public List<JFXButton> subMenus;
    public JFXButton options = new JFXButton("Options");
    public JFXButton reports = new JFXButton("Reports");
    @FXML
    private StackPane stackPane;
    @FXML
    private Label pendingApprovals_lbl, pendingReleases_lbl, critical_lbl;

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
        this.initializeCriticalStocks();
        try {
            pendingApprovals_lbl.setText(""+MirsDAO.countPending());
            pendingReleases_lbl.setText(""+MirsDAO.countPendingReleases());
        } catch (Exception var4) {
            var4.printStackTrace();
        }

    }

    @FXML
    private void mirsPendingApproval(MouseEvent event) {
        ModalBuilder.showModalFromXML(this.getClass(), "../warehouse_pending_mirs.fxml", this.stackPane);
    }

    @FXML
    private void mirsPendingReleases(MouseEvent event) {
        ModalBuilder.showModalFromXML(this.getClass(), "../warehouse_pending_mirs.fxml", this.stackPane);
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

    public void initializeCriticalStocks(){
        Platform.runLater(() -> {
            try {
                critical_lbl.setText(""+ StockDAO.countCritical());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
