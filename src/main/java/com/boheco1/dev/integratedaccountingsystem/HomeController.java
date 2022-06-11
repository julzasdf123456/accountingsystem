package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.NotificationsDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.Notifications;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Popup;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class HomeController implements Initializable {

    /**
     * ICONS ARE IN https://kordamp.org/ikonli/cheat-sheet-materialdesign2.html
     */

    @FXML StackPane homeStackPane;

    @FXML AnchorPane contentPane, drawerAnchorPane;

    @FXML GridPane gridPane;

    @FXML Label title;

    // NOTIFICATION AND OTHER MENUS
    public JFXButton notificationButton;
    public Popup notificationPopup;
    public FontIcon notificationIcon;

    // DRAWER MENU ARRAYS
    public List<JFXButton> drawerMenus;
    public List<Label> labelList;


    // DRAWER
    @FXML JFXButton hamburger;
    @FXML VBox navMenuBox;
    public boolean isDrawerExpanded = true;
    public Double drawerMinWidth = 70.0;
    public FontIcon hamburgerIcon;

    /**
     * Nav Menu Items
     */
    // FINANCE
    public JFXButton journalEntries, budget;

    // BILLING
    public JFXButton allAccounts;

    // TELLER
    public JFXButton collection;

    // WAREHOUSE
    public JFXButton warehouseDashboard, fileMirs, generateMct, mrT, stocks, receiving, addMR;

    // ADMINISTRATIVE
    public JFXButton employees, users, signatoriesButton;

    // USER
    public JFXButton approvalTask, myAccount, logout;

    @FXML
    public FlowPane subToolbar, notificationBin;
    public List<JFXButton> submenuList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setContentPane(contentPane);
        Utility.setStackPane(homeStackPane);
        submenuList = new ArrayList<>();
        // INITIALIZE HAMBURGER
        hamburgerIcon = new FontIcon("mdi2a-arrow-left");
        hamburgerIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
        hamburger.setGraphic(hamburgerIcon);

        // initialize main menu labels
        labelList = new ArrayList<>();

        // INITIALIZE CUSTOM MENU
        journalEntries = new JFXButton("Journal Entries");
        signatoriesButton = new JFXButton("Signatories");
        budget = new JFXButton("Budget");
        allAccounts = new JFXButton("All Accounts");
        collection = new JFXButton("Collection");
        warehouseDashboard = new JFXButton("Dashboard");
        fileMirs = new JFXButton("File MIRS");
        generateMct = new JFXButton("Generate MCT");
        mrT = new JFXButton("Material Return");
        stocks = new JFXButton("Stock Entry");
        employees = new JFXButton("Employees");
        users = new JFXButton("Users");
        myAccount = new JFXButton("My Account");
        approvalTask = new JFXButton("Approval Task");
        logout = new JFXButton("Logout");
        receiving = new JFXButton("Receiving Entry");
        addMR = new JFXButton("MR Entry");

        // ADD ALL ITEMS TO NAV SEQUENTIALLY
        if(ActiveUser.getUser().can("manage-finance")) {
            NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("Finance"), new FontIcon("mdi2f-finance"), homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, journalEntries, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, budget, homeStackPane);
        }

        if(ActiveUser.getUser().can("manage-billing")) {
            NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("Billing"), new FontIcon("mdi2r-receipt"), homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, allAccounts, homeStackPane);
        }

        if (ActiveUser.getUser().can("manage-tellering")){
            NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("Teller"), new FontIcon("mdi2c-contactless-payment-circle"), homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, collection, homeStackPane);
        }

        if(ActiveUser.getUser().can("manage-warehouse")) {
            NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("Warehouse"), new FontIcon("mdi2s-sitemap"), homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, warehouseDashboard, homeStackPane);
        }

        if(ActiveUser.getUser().can("file-mirs")) {
            NavMenuHelper.addMenu(navMenuBox, fileMirs, homeStackPane);
        }

        if(ActiveUser.getUser().can("manage-warehouse")) {
            NavMenuHelper.addMenu(navMenuBox, generateMct, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, mrT, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, receiving, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, addMR, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, stocks, homeStackPane);
        }
        if (ActiveUser.getUser().can("manage-employees")) {
            NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("Administrator"), new FontIcon("mdi2s-security"), homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, employees, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, users, homeStackPane);
            NavMenuHelper.addMenu(navMenuBox, signatoriesButton, homeStackPane);
        }
        NavMenuHelper.addSeparatorLabel(labelList, navMenuBox, new Label("User"), new FontIcon("mdi2a-account-circle"), homeStackPane);
        NavMenuHelper.addMenu(navMenuBox, myAccount, homeStackPane);
        NavMenuHelper.addMenu(navMenuBox, approvalTask, homeStackPane);
        NavMenuHelper.addMenu(navMenuBox, logout, homeStackPane);

        // INITIALIZE MENU FUNCTIONS
        drawerMenus = new ArrayList<>();
        if(ActiveUser.getUser().can("manage-finance")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(journalEntries, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, journalEntries.getText(), contentPane, "journal_entries_layout.fxml", subToolbar, null, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(budget, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, budget.getText(), contentPane, "budget_layout.fxml", subToolbar, null, "manage-budget", homeStackPane, title);
        }

        if(ActiveUser.getUser().can("manage-billing")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(allAccounts, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, allAccounts.getText(), contentPane, "all_accounts_layout.fxml", subToolbar, null, title);
        }

        if (ActiveUser.getUser().can("manage-tellering")) {
            DrawerMenuHelper.setMenuButton(collection, new FontIcon("mdi2c-cash-usd"), drawerMenus, collection.getText());
        }

        // WAREHOUSE
        if (ActiveUser.getUser().can("manage-warehouse")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(warehouseDashboard, new FontIcon("mdi2v-view-dashboard"), drawerMenus, "Warehouse Dashboard", contentPane, "warehouse_dashboard_controller.fxml", subToolbar, null, title);
        }

        if (ActiveUser.getUser().can("file-mirs")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(fileMirs, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "File MIRS", contentPane, "warehouse_file_mirs.fxml", subToolbar, null, title);
        }

        if (ActiveUser.getUser().can("manage-warehouse")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(generateMct, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "Generate MCT", contentPane, "warehouse_generate_mct.fxml", subToolbar, null, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(mrT, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "Material Return", contentPane, "warehouse_mrt_form.fxml", subToolbar, null, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(addMR, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "MR Entry", contentPane, "warehouse_mr_entry.fxml", subToolbar, null, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(stocks, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "Stock Entry", contentPane, "warehouse_stock_entry.fxml", subToolbar, null, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(receiving, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "Receiving Entry", contentPane, "warehouse_receiving_entry.fxml", subToolbar, null, title);
        }
        // USERS
        if (ActiveUser.getUser().can("manage-employees")) {
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(employees, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, employees.getText(), contentPane, "manage_employees.fxml", subToolbar, null, "manage-users", homeStackPane, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(users, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, users.getText(), contentPane, "all_users_controller.fxml", subToolbar, null, "manage-users", homeStackPane, title);
            DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(signatoriesButton, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "Manage Signatories", contentPane, "signatories_index.fxml", subToolbar, null, "manage-users", homeStackPane, title);
        }
        DrawerMenuHelper.setMenuButton(logout,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "Logout");
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(myAccount,new FontIcon("mdi2c-checkbox-blank-circle-outline"),drawerMenus,myAccount.getText(),contentPane,"view_my_account.fxml",subToolbar, null, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(approvalTask,new FontIcon("mdi2c-checkbox-blank-circle-outline"),drawerMenus,approvalTask.getText(),contentPane,"view_mirs_for_approval_controller.fxml",subToolbar, null, title);

        logout.setOnAction(actionEvent -> {
            ActiveUser.setUser(null);
            try {
                HostWindow.setRoot("login_controller");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        // INITIALIZE NOTIFICATION BUTTON
        notificationIcon = new FontIcon("mdi2b-bell");
        notificationIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
        notificationIcon.setIconSize(15);
        notificationButton = new JFXButton("", notificationIcon);
        notificationButton.setFont(Font.font(notificationButton.getFont().getFamily(), 10));
        notificationBin.getChildren().add(notificationButton);

        notificationButton.setOnAction(actionEvent -> {
            showNotification();
        });

        //Create notifications
//        try {
//            NotificationsDAO.create(new Notifications("This is an MIRS notification is for user 2", NotificationsDAO.MIRS_APROVAL, null, "2", "06-2021"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        notifyUser();
    }

    public void notifyUser() {
        try {
            Timer timer = new java.util.Timer();

            timer.schedule(new TimerTask() {
                public void run() {
                    Platform.runLater(new Runnable() {
                        public void run() {
                            try {
                                if (ActiveUser.getUser() == null)
                                    return;
                                int count = NotificationsDAO.getNotifCount(ActiveUser.getUser().getId()+"");
                                if (count > 0) {
                                    notificationButton.setTextFill(Paint.valueOf(ColorPalette.DANGER));
                                    notificationIcon.setIconColor(Paint.valueOf(ColorPalette.DANGER));
                                    notificationButton.setFont(Font.font(notificationButton.getFont().getFamily(), FontWeight.BOLD, 10));
                                } else {
                                    notificationButton.setTextFill(Paint.valueOf(ColorPalette.GREY_DARK));
                                    notificationIcon.setIconColor(Paint.valueOf(ColorPalette.GREY_DARK));
                                    notificationButton.setFont(Font.font(notificationButton.getFont().getFamily(), 10));
                                }
                                notificationButton.setText("(" + count + ")");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }, 0, 1000); // 1 second
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public void replaceContent(String fxml) {
        contentPane.getChildren().setAll(ContentHandler.getNodeFromFxml(HomeController.class, fxml));
        subToolbar = null;
    }

    /**
     * NOTIFICATION CUSTOMIZATION
     */
    public void showNotification() {
        try {
            notificationPopup = new Popup();
            VBox vBox = new VBox();
            vBox.setPrefWidth(240);
            vBox.setFillWidth(true);

            List<Notifications> notificationsList = NotificationsDAO.getNotificationsForUser(ActiveUser.getUser().getId()+"");
            Label notifTitle = new Label("Your Notifications");
            notifTitle.setPadding(new Insets(10, 0, 10, 10));
            notifTitle.setFont(Font.font(notifTitle.getFont().getFamily(), FontWeight.BOLD, 14));

            vBox.getChildren().add(notifTitle);
            for(Notifications notifications : notificationsList) {
                FontIcon icon = new FontIcon(notifications.getIcon()!=null ? notifications.getIcon() : NotificationsDAO.INFORMATION_ICON);
                JFXButton item = new JFXButton(notifications.getNotificationDetails(), icon);
                item.setGraphicTextGap(10);
                item.setPadding(new Insets(10, 10, 10, 10));

                if (notifications.getStatus().equals("UNREAD")) {
                    item.setFont(Font.font(item.getFont().getFamily(), FontWeight.BOLD, 13));
                } else {
                    item.setFont(Font.font(item.getFont().getFamily(), 13));
                }

                item.setOnAction(actionEvent -> {
                    // Mark status as "READ" when clicked
                    if (notifications.getStatus().equals("UNREAD")) {
                        notifications.setStatus("READ");
                        try {
                            NotificationsDAO.update(notifications);
                            item.setFont(Font.font(item.getFont().getFamily(), 13));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // PERFORM EVENT HERE
                    if (notifications.getNotificationType().equals(NotificationsDAO.INFORMATION)) {
                        AlertDialogBuilder.messgeDialog("Notification", notifications.getNotificationDetails(), homeStackPane, AlertDialogBuilder.INFO_DIALOG);
                    } else if (notifications.getNotificationType().equals(NotificationsDAO.MIRS_APROVAL)) {
                        // forward to viewing of mirs
                    }
                });
                vBox.getChildren().add(item);
            }

            ScrollPane scrollPane = new ScrollPane(vBox);
            scrollPane.getStyleClass().add("popup-holder");
            scrollPane.setMaxHeight(notificationButton.getScene().getWindow().getHeight()-110);//Adjust max height of the popup here
            scrollPane.setMaxWidth(350);//Adjust max width of the popup here
            notificationPopup.getContent().add(scrollPane);

            notificationPopup.setAutoHide(true);
            notificationPopup.show(notificationButton.getScene().getWindow(), NodeLocator.getNodeX(notificationButton)-20, NodeLocator.getNodeY(notificationButton)+30);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
