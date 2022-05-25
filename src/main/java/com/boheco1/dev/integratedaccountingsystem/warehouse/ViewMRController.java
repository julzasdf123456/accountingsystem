package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

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
        this.setMR();
        Utility.setMrController(this);
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void initializeTable() {
        TableColumn<MR, String> column1 = new TableColumn<>("Item Name");
        column1.setMinWidth(350);
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

        TableColumn<MR, MR> column5 = new TableColumn<>("Action");
        column4.setMinWidth(50);
        column5.setCellValueFactory(mr -> new ReadOnlyObjectWrapper<>(mr.getValue()));
        column5.setCellFactory(mrtable -> new TableCell<>(){
            FontIcon icon = new FontIcon("mdi2f-file-edit");
            private final JFXButton returnBtn = new JFXButton("", icon);

            @Override
            public void updateItem(MR item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !item.getStatus().contains("returned")) {
                    returnBtn.setStyle("-fx-background-color: #2196f3;");
                    icon.setIconSize(13);
                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    returnBtn.setOnAction(actionEvent -> {
                        Utility.setSelectedMR(item);

                        ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mr_return.fxml", Utility.getStackPane());
                    });
                    setGraphic(returnBtn);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column6 = new TableColumn<>("Returned");
        column6.setMinWidth(100);
        column6.setCellValueFactory(new PropertyValueFactory<>("dateOfReturn"));
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column7 = new TableColumn<>("Status");
        column7.setMinWidth(100);
        column7.setCellValueFactory(new PropertyValueFactory<>("status"));
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MR, String> column8 = new TableColumn<>("Remarks");
        column8.setMinWidth(115);
        column8.setCellValueFactory(new PropertyValueFactory<>("remarks"));
        column8.setStyle("-fx-alignment: center;");

        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column2);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
        this.mr_items_table.getColumns().add(column8);
        this.mr_items_table.getColumns().add(column5);
    }

    public void setMR(){
        this.fname_tf.setText(this.employee.getEmployeeFirstName());
        this.lname_tf.setText(this.employee.getEmployeeLastName());
        this.mname_tf.setText(this.employee.getEmployeeMidName());
        this.address_tf.setText(this.employee.getEmployeeAddress());
        if (this.employee.getPhone() != null)
            this.phone_tf.setText(this.employee.getPhone());
        if (this.employee.getDesignation() != null)
            this.designation_tf.setText(this.employee.getDesignation());
        this.initializeTable();
        this.populateTable();
    }

    public void populateTable() {
        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getMRsOfEmployee(this.employee));
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
