package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class JournalEntriesController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML
    public StackPane dashboardStackPane;

    // SUB MENUS
    public List<JFXButton> subToolbarMenus;
    public JFXButton menu;

    public AnchorPane anchorPane;

    // Context Sub Menus
    ContextMenuHelper contextMenuHelper;
    MenuItem changeView = new MenuItem("Change View");
    MenuItem showModal = new MenuItem("Show Modal View");

    public JournalEntriesController() {
        menu = new JFXButton("Menu");

        // initialize context menu helper
        contextMenuHelper = new ContextMenuHelper();

        menu.setOnAction(actionEvent -> {
            contextMenuHelper.initializePopupContextMenu(menu, changeView, showModal)
                    .show(menu, NodeLocator.getNodeX(menu), NodeLocator.getNodeY(menu));
        });

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void dashboardTestClick() {
        AlertDialogBuilder.messgeDialog("Eyyy!", "This is a test dialog in the dashboard wrapped by the DashboardController class", dashboardStackPane, AlertDialogBuilder.INFO_DIALOG);
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        subToolbarMenus = new ArrayList<>();
        subToolbarMenus.add(menu);
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(subToolbarMenus);
    }

    @Override
    public void handleContentReplacements(AnchorPane container, Label titleHolder) {
        changeView.setOnAction(actionEvent -> {
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "budget_layout.fxml"));
        });

        showModal.setOnAction(actionEvent -> {
            container.getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "all_accounts_layout.fxml"));
        });
    }
}
