package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.ColorPalette;
import com.boheco1.dev.integratedaccountingsystem.helpers.ContentHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.DrawerMenuHelper;

import com.boheco1.dev.integratedaccountingsystem.usermgt.UserMgtController;
import com.boheco1.dev.integratedaccountingsystem.warehouse.FileMIRS;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;

import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    @FXML StackPane homeStackPane;

    @FXML AnchorPane contentPane, drawerAnchorPane;

    @FXML GridPane gridPane;

    @FXML Label accountingLabel, billingLabel, telleringLabel, warehouseLabel, userLabel, adminLabel;

    @FXML JFXButton budget, journalEntries, myAcctBtn, logoutBtn, allAccounts, collection, otherPayments, usersBtn, employeesBtn;

    @FXML JFXButton warehouseDashboardBtn, mirsBtn;

    @FXML Label title;

    // DRAWER MENU ARRAYS
    public List<JFXButton> drawerMenus;
    public List<Label> labelList;

    // DRAWER
    @FXML JFXButton hamburger;
    public boolean isDrawerExpanded = true;
    public Double drawerMinWidth = 70.0;
    public FontIcon hamburgerIcon;

    @FXML
    public FlowPane subToolbar;
    public List<JFXButton> submenuList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        submenuList = new ArrayList<>();
        // INITIALIZE HAMBURGER
        hamburgerIcon = new FontIcon("mdi2a-arrow-left");
        hamburgerIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
        hamburger.setGraphic(hamburgerIcon);

        // initialize main menu labels
        labelList = new ArrayList<>();
        DrawerMenuHelper.setMenuLabels(accountingLabel, new FontIcon("mdi2f-finance"), labelList);
        DrawerMenuHelper.setMenuLabels(billingLabel, new FontIcon("mdi2r-receipt"), labelList);
        DrawerMenuHelper.setMenuLabels(telleringLabel, new FontIcon("mdi2c-contactless-payment-circle"), labelList);
        DrawerMenuHelper.setMenuLabels(warehouseLabel, new FontIcon("mdi2s-sitemap"), labelList);


        // INITIALIZE MENU ICONS
        drawerMenus = new ArrayList<>();
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(budget,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, budget.getText(), contentPane, "budget_layout.fxml", subToolbar, new BudgetController(), "budget", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(usersBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, usersBtn.getText(), contentPane, "user_mgt.fxml", subToolbar, new UserMgtController(), "manage-user", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(employeesBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, employeesBtn.getText(), contentPane, "manage_employees.fxml", subToolbar, new ManageEmployeesController(), "manage-user", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(journalEntries,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, journalEntries.getText(), contentPane, "journal_entries_layout.fxml", subToolbar, new JournalEntriesController(), title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(allAccounts, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, allAccounts.getText(), contentPane, "all_accounts_layout.fxml", subToolbar, new AllAccountsController(), title);
        DrawerMenuHelper.setMenuButtonWithView(collection, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, collection.getText(), contentPane, "all_accounts_layout.fxml");
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(otherPayments, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, otherPayments.getText(), contentPane, "all_accounts_layout.fxml", subToolbar, null, title);
        DrawerMenuHelper.setMenuButton(myAcctBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "My Account");
        DrawerMenuHelper.setMenuButton(logoutBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "Logout");

        // WAREHOUSE
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(warehouseDashboardBtn, new FontIcon("mdi2v-view-dashboard"), drawerMenus, "Warehouse Dashboard", contentPane, "warehouse_dashboard_controller.fxml", subToolbar, new WarehouseDashboardController(), title); // PERMISSION TO VIEW WAREHOUSE: warehouse-view
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(mirsBtn, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "File for MIRS", contentPane, "warehouse_file_mirs.fxml", subToolbar, new FileMIRS(), title); // PERMISSION TO VIEW WAREHOUSE: warehouse-view
    }

    @FXML
    private void toggleDrawer() {
        Timeline timeline = null;

        if (isDrawerExpanded) {
            timeline = new Timeline(new KeyFrame(Duration.millis(200),
                    new KeyValue(gridPane.getColumnConstraints().get(0).maxWidthProperty(),
                            drawerMinWidth,
                            Interpolator.EASE_BOTH)));
            gridPane.getColumnConstraints().get(0).minWidthProperty().setValue(drawerMinWidth);

        } else {
            DoubleProperty max = new SimpleDoubleProperty(246);
            timeline = new Timeline(new KeyFrame(Duration.millis(200),
                    new KeyValue(gridPane.getColumnConstraints().get(0).minWidthProperty(),
                            max.get(),
                            Interpolator.EASE_BOTH)));
        }
        timeline.play();

        timeline.setOnFinished(actionEvent -> {
            if (isDrawerExpanded) {
                isDrawerExpanded = false;

                hamburgerIcon = new FontIcon("mdi2m-menu");
                hamburgerIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
                hamburger.setGraphic(hamburgerIcon);

                // SET DRAWER MENU BUTTONS TO HIDE TEXT
                DrawerMenuHelper.setMenuButtonsIconOnly(drawerMenus);
                DrawerMenuHelper.setMenuLabelsIconOnly(labelList);
            } else {
                isDrawerExpanded = true;

                hamburgerIcon = new FontIcon("mdi2a-arrow-left");
                hamburgerIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
                hamburger.setGraphic(hamburgerIcon);
                gridPane.getColumnConstraints().get(0).maxWidthProperty().setValue(246);

                // SET DRAWER MENU BUTTONS TO SHOW TEXT
                DrawerMenuHelper.setMenuButtonsFullDisplay(drawerMenus);
                DrawerMenuHelper.setMenuLabelsFullDisplay(labelList);
            }
        });
    }

    @FXML
    private void newClick() {
    }

    public void replaceContent(String fxml) {
        contentPane.getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, fxml));
        subToolbar = null;
    }

    @FXML
    private void logout() throws IOException {
        ActiveUser.setUser(null);
        HostWindow.setRoot("login_controller");
    }

    @FXML
    private void viewMyAccount() {
        this.replaceContent("view_my_account.fxml");
    }
}
