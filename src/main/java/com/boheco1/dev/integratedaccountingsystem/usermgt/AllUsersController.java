package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AllUsersController extends MenuControllerHandler implements Initializable {

    @FXML TableView usersTable;
    @FXML StackPane allUsersStackPane;
    public AnchorPane contentPaneX;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        initializeUsersTable();

        // INITALIZE TABLE CONTENT
        Platform.runLater(() -> {
            try {
                ObservableList<User> users = FXCollections.observableList(UserDAO.getAll(DB.getConnection()));
                usersTable.getItems().setAll(users);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public void initializeUsersTable() {
        try {
            TableColumn<User, String> userIdCol = new TableColumn<>("User ID");
            userIdCol.setMinWidth(150);
            userIdCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<User, String> usernameCol = new TableColumn<>("Username");
            usernameCol.setMinWidth(150);
            usernameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));

            TableColumn<User, String> nameCol = new TableColumn<>("Registered Name");
            nameCol.setMinWidth(250);
            nameCol.setCellValueFactory(new PropertyValueFactory<>("fullName"));

            TableColumn<User, User> actionCol = new TableColumn<>("Action");
            actionCol.setMinWidth(150);

            // ADD ACTION BUTTONS
            actionCol.setCellValueFactory(stockStringCellDataFeatures -> new ReadOnlyObjectWrapper<User>(stockStringCellDataFeatures.getValue()));
            actionCol.setCellFactory(stockStockTableColumn -> new TableCell<>(){
                FontIcon viewIcon =  new FontIcon("mdi2e-eye");
                private final JFXButton viewButton = new JFXButton("", viewIcon);

                FontIcon deleteIcon =  new FontIcon("mdi2t-trash-can");
                private final JFXButton deleteButton = new JFXButton("", deleteIcon);

                @Override
                protected void updateItem(User users, boolean b) {
                    super.updateItem(users, b);

                    if (users != null) {
                        viewButton.setStyle("-fx-background-color: #2196f3;");
                        viewIcon.setIconSize(13);
                        viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        deleteButton.setStyle("-fx-background-color: #f44336;");
                        deleteIcon.setIconSize(13);
                        deleteIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));

                        viewButton.setOnAction(actionEvent -> {
                            Utility.setSelectedUser(users);

                            try {
                                Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "user_mgt.fxml"));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        });

                        deleteButton.setOnAction(actionEvent -> {

                        });

                        HBox hBox = new HBox();
                        hBox.setSpacing(2);
                        hBox.getChildren().add(viewButton);
                        hBox.getChildren().add(deleteButton);

                        setGraphic(hBox);
                    } else {
                        setGraphic(null);
                        return;
                    }
                }
            });

            usersTable.getColumns().removeAll();
            usersTable.getColumns().add(userIdCol);
            usersTable.getColumns().add(usernameCol);
            usersTable.getColumns().add(nameCol);
            usersTable.getColumns().add(actionCol);
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("Error building users table", "Error building users table: " + e.getMessage(), allUsersStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @Override
    public void handleContentReplacements(AnchorPane container, Label titleHolder) {
        contentPaneX = container;
    }
}
