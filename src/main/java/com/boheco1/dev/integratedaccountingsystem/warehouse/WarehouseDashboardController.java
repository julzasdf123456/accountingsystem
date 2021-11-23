package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.StockDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WarehouseDashboardController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    /**
     * Initialize Sub Menus
     */
    public List<JFXButton> subMenus;
    public JFXButton options, reports;

    @FXML
    private Label critical_lbl;

    /**
     * Initialize Sub Menu Context Menu Items
     */
    ContextMenuHelper contextMenuHelper;
    // FOR OPTIONS
    MenuItem viewAllStocks = new MenuItem("View All Stocks");
    MenuItem trash = new MenuItem("Trash");

    // FOR REPORTS
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeCriticalStocks();
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        subMenus = new ArrayList<>();
        subMenus.add(options);
        subMenus.add(reports);

        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(subMenus);
    }

    @Override
    public void handleContentReplacements(AnchorPane container, Label titleHolder) {

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
