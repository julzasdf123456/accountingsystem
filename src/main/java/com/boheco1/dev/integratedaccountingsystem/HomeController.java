package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.NotificationsDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;

import com.boheco1.dev.integratedaccountingsystem.objects.Notifications;
import com.boheco1.dev.integratedaccountingsystem.usermgt.UserMgtController;
import com.boheco1.dev.integratedaccountingsystem.warehouse.FileMIRSController;
import com.boheco1.dev.integratedaccountingsystem.warehouse.StockEntryController;
import com.boheco1.dev.integratedaccountingsystem.warehouse.WarehouseDashboardController;

import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;

import com.jfoenix.controls.JFXButton;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
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

    @FXML Label accountingLabel, billingLabel, telleringLabel, warehouseLabel, userLabel, adminLabel;

    @FXML JFXButton budget, journalEntries, myAcctBtn, logoutBtn, allAccounts, collection, otherPayments, usersBtn, employeesBtn, stockBtn;

    @FXML JFXButton warehouseDashboardBtn, mirsBtn;

    @FXML Label title;

    // NOTIFICATION AND OTHER MENUS
    public JFXButton notificationButton;
    public Popup notificationPopup;
    public FontIcon notificationIcon;

    // DRAWER MENU ARRAYS
    public List<JFXButton> drawerMenus;
    public List<Label> labelList;

    public static int ROW_PER_PAGE = 25;

    // DRAWER
    @FXML JFXButton hamburger;
    public boolean isDrawerExpanded = true;
    public Double drawerMinWidth = 70.0;
    public FontIcon hamburgerIcon;

    @FXML
    public FlowPane subToolbar, notificationBin;
    public List<JFXButton> submenuList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Utility.setContentPane(contentPane);
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

        //Create notifications
//        try {
//            NotificationsDAO.create(new Notifications("This is an MIRS notification is for user 2", NotificationsDAO.MIRS_APROVAL, null, "2", "06-2021"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        // INITIALIZE MENU ICONS
        drawerMenus = new ArrayList<>();
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(budget,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, budget.getText(), contentPane, "budget_layout.fxml", subToolbar, new BudgetController(), "budget", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(usersBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, usersBtn.getText(), contentPane, "user_mgt.fxml", subToolbar, new UserMgtController(), "manage-users", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(employeesBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, employeesBtn.getText(), contentPane, "manage_employees.fxml", subToolbar, new ManageEmployeesController(), "manage-users", homeStackPane, title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(journalEntries,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, journalEntries.getText(), contentPane, "journal_entries_layout.fxml", subToolbar, new JournalEntriesController(), title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(allAccounts, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, allAccounts.getText(), contentPane, "all_accounts_layout.fxml", subToolbar, new AllAccountsController(), title);
        DrawerMenuHelper.setMenuButtonWithView(collection, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, collection.getText(), contentPane, "all_accounts_layout.fxml");
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(otherPayments, new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, otherPayments.getText(), contentPane, "all_accounts_layout.fxml", subToolbar, null, title);
        DrawerMenuHelper.setMenuButton(myAcctBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "My Account");
        DrawerMenuHelper.setMenuButton(logoutBtn,  new FontIcon("mdi2c-checkbox-blank-circle-outline"), drawerMenus, "Logout");

        // WAREHOUSE
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(warehouseDashboardBtn, new FontIcon("mdi2v-view-dashboard"), drawerMenus, "Warehouse Dashboard", contentPane, "warehouse_dashboard_controller.fxml", subToolbar, new WarehouseDashboardController(), title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(mirsBtn, new FontIcon("mdi2f-file-document-edit"), drawerMenus, "File MIRS", contentPane, "warehouse_file_mirs.fxml", subToolbar, new FileMIRSController(), title);
        DrawerMenuHelper.setMenuButtonWithViewAndSubMenu(stockBtn, new FontIcon("mdi2w-warehouse"), drawerMenus, "Stock", contentPane, "warehouse_stock_entry.fxml", subToolbar, new StockEntryController(), title);

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
