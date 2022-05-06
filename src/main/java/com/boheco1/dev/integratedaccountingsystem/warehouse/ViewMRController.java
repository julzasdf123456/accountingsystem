package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ViewMRController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField fname_tf, mname_tf, lname_tf, address_tf, phone_tf, designation_tf;

    @FXML
    private TableView mr_items_table;

    @FXML
    private JFXButton returnBtn, cancelBtn;

    private EmployeeInfo employee = null;

    private ObservableList<MR> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employee = Utility.getSelectedEmployee();
        this.setMR(employee);
    }

    @FXML
    private void returnMR()  {

    }

    @FXML
    private void cancelReturn()  {

    }



    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void initializeTable() {
        TableColumn<MR, String> column1 = new TableColumn<>("Item Name");
        column1.setMinWidth(300);
        column1.setCellValueFactory(new PropertyValueFactory<>("extItem"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MR, String> column2 = new TableColumn<>("Qty");
        column2.setMinWidth(50);
        column2.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        column2.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column3 = new TableColumn<>("Unit Price");
        column3.setMinWidth(100);
        column3.setCellValueFactory(new PropertyValueFactory<>("price"));
        column3.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column4 = new TableColumn<>("Date of MR");
        column4.setMinWidth(100);
        column4.setCellValueFactory(new PropertyValueFactory<>("dateOfMR"));
        column4.setStyle("-fx-alignment: center;");
        /*
        TableColumn<MR, String> column5 = new TableColumn<>("Action");
        Callback<TableColumn<MR, String>, TableCell<MR, String>> removeBtn
                = //
                new Callback<TableColumn<MR, String>, TableCell<MR, String>>() {
                    @Override
                    public TableCell call(final TableColumn<MR, String> param) {
                        final TableCell<MR, String> cell = new TableCell<MR, String>() {

                            Button btn = new Button("");
                            FontIcon icon = new FontIcon("mdi2d-delete");

                            @Override
                            public void updateItem(String item, boolean empty) {
                                super.updateItem(item, empty);
                                icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                                btn.setStyle("-fx-background-color: #f44336");
                                btn.setGraphic(icon);
                                btn.setGraphicTextGap(5);
                                btn.setTextFill(Paint.valueOf(ColorPalette.WHITE));
                                if (empty) {
                                    setGraphic(null);
                                    setText(null);
                                } else {
                                    btn.setOnAction(event -> {
                                        MR selected_item = getTableView().getItems().get(getIndex());

                                        try {
                                            mrItems.remove(selected_item);
                                            mr_items_table.setItems(mrItems);
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
        column5.setCellFactory(removeBtn);
        column5.setStyle("-fx-alignment: center;");*/

        TableColumn<MR, String> column6 = new TableColumn<>("Date Returned");
        column6.setMinWidth(130);
        column6.setCellValueFactory(new PropertyValueFactory<>("dateOfReturn"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column7 = new TableColumn<>("Status");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("status"));
        column7.setStyle("-fx-alignment: center;");

        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column2);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        //this.mr_items_table.getColumns().add(column5);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
    }

    public void setMR(EmployeeInfo emp){
        this.employee = emp;
        this.fname_tf.setText(emp.getEmployeeFirstName());
        this.lname_tf.setText(emp.getEmployeeLastName());
        this.mname_tf.setText(emp.getEmployeeMidName());
        this.address_tf.setText(emp.getEmployeeAddress());
        if (emp.getPhone() != null)
            this.phone_tf.setText(emp.getPhone());
        if (emp.getDesignation() != null)
            this.designation_tf.setText(emp.getDesignation());
        this.initializeTable();
        this.populateTable(emp);
    }

    public void populateTable(EmployeeInfo emp) {
        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getMRsOfEmployee(emp));
                    this.mr_items_table.getItems().setAll(mrItems);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
}
