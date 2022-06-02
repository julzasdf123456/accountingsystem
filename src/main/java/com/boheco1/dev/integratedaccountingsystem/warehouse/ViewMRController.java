package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import org.kordamp.ikonli.javafx.FontIcon;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ViewMRController extends MenuControllerHandler implements Initializable {

    @FXML
    private AnchorPane contentPane;

    @FXML
    private StackPane stackPane;

    @FXML
    private JFXTextField fname_tf, mname_tf, lname_tf, address_tf, phone_tf, designation_tf, date_approved_tf, released_tf, recommending_tf, approved_tf, purpose_tf;

    @FXML
    private ListView mr_list_view;

    @FXML
    private JFXButton printBtn;

    @FXML
    private TableView mr_items_table;

    private EmployeeInfo employee = null;

    private ObservableList<MrItem> mrItems = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        employee = Utility.getSelectedEmployee();
        this.setMRs();
        Utility.setMrController(this);
        this.printBtn.setDisable(true);
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    public void initializeTable() {
        TableColumn<MrItem, String> column1 = new TableColumn<>("Quantity");
        column1.setMinWidth(75);
        column1.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getQty()+" "+item.getValue().getStock().getUnit());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column3 = new TableColumn<>("Description");
        column3.setMinWidth(343);
        column3.setCellValueFactory(item -> {
            try {
                return new ReadOnlyObjectWrapper<>(item.getValue().getStock().getDescription());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column4 = new TableColumn<>("Property No.");
        column4.setMinWidth(105);
        column4.setCellValueFactory(new PropertyValueFactory<>("stockID"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<MrItem, String> column5 = new TableColumn<>("RR No.");
        column5.setMinWidth(100);
        column5.setCellValueFactory(new PropertyValueFactory<>("rrNo"));
        column5.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column6 = new TableColumn<>("Unit Price");
        column6.setMinWidth(100);
        column6.setCellValueFactory(
                item -> {
                    try {
                        double cost = 0;
                        ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                        if (rc == null){
                            cost = item.getValue().getStock().getPrice();
                        }else{
                            cost = rc.getUnitCost();
                        }
                        return new ReadOnlyObjectWrapper<>(cost);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
        );
        column6.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, Double> column7 = new TableColumn<>("Total");
        column7.setMinWidth(100);
        column7.setCellValueFactory(item -> {
            try {
                double cost = 0;
                ReceivingItem rc = item.getValue().getStock().getReceivingItem();
                if (rc == null){
                    cost = item.getValue().getStock().getPrice();
                }else{
                    cost = rc.getUnitCost();
                }
                return new ReadOnlyObjectWrapper<>(cost*item.getValue().getQty());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        column7.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, String> column8 = new TableColumn<>("Remarks");
        column8.setMinWidth(200);
        column8.setCellValueFactory(item -> {
            if (item.getValue().getDateReturned() == null){
                return new ReadOnlyObjectWrapper<>(item.getValue().getRemarks());
            }else{
                String status = item.getValue().getStatus()+" on "+item.getValue().getDateReturned();
                return new ReadOnlyObjectWrapper<>(status);
            }
        });
        column8.setStyle("-fx-alignment: center;");

        TableColumn<MrItem, MrItem> column9 = new TableColumn<>(" ");
        column9.setCellValueFactory(mr -> new ReadOnlyObjectWrapper<>(mr.getValue()));
        column9.setCellFactory(mritem -> new TableCell<>(){
            FontIcon icon = new FontIcon("mdi2f-file-edit");
            private final JFXButton returnBtn = new JFXButton("", icon);

            @Override
            public void updateItem(MrItem item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null && !item.getStatus().contains(Utility.MR_RETURNED)) {
                    returnBtn.setStyle("-fx-background-color: #2196f3;");
                    icon.setIconSize(13);
                    icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
                    returnBtn.setOnAction(actionEvent -> {
                        //Utility.setSelectedMR(item);

                        //ModalBuilderForWareHouse.showModalFromXMLNoClose(WarehouseDashboardController.class, "../warehouse_mr_return.fxml", Utility.getStackPane());
                    });
                    setGraphic(returnBtn);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column9.setStyle("-fx-alignment: center;");
        this.mrItems =  FXCollections.observableArrayList();
        this.mr_items_table.setPlaceholder(new Label("No item added"));

        this.mr_items_table.getColumns().add(column1);
        this.mr_items_table.getColumns().add(column3);
        this.mr_items_table.getColumns().add(column4);
        this.mr_items_table.getColumns().add(column5);
        this.mr_items_table.getColumns().add(column6);
        this.mr_items_table.getColumns().add(column7);
        this.mr_items_table.getColumns().add(column8);
        this.mr_items_table.getColumns().add(column9);
    }

    @FXML
    public void printMR(){

    }

    public void setMRs(){
        this.fname_tf.setText(this.employee.getEmployeeFirstName());
        this.lname_tf.setText(this.employee.getEmployeeLastName());
        this.mname_tf.setText(this.employee.getEmployeeMidName());
        this.address_tf.setText(this.employee.getEmployeeAddress());
        if (this.employee.getPhone() != null)
            this.phone_tf.setText(this.employee.getPhone());
        if (this.employee.getDesignation() != null)
            this.designation_tf.setText(this.employee.getDesignation());
        this.initializeTable();
        getMRs();
    }

    public void getMRs(){
        try {
            List<MR> emp_mrs = MrDAO.getMRsOfEmployee(this.employee);
            this.mr_list_view.setItems(FXCollections.observableArrayList(emp_mrs));
            this.mr_list_view.setOnMouseClicked(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    MR selected = (MR) mr_list_view.getSelectionModel().getSelectedItem();
                    if (selected == null) return;
                    printBtn.setDisable(false);
                    date_approved_tf.setText(selected.getDateOfMR().toString());
                    purpose_tf.setText(selected.getPurpose());
                    try {
                        EmployeeInfo personnel = UserDAO.get(selected.getWarehousePersonnelId()).getEmployeeInfo();
                        released_tf.setText(personnel.getEmployeeFirstName()+" "+personnel.getEmployeeLastName());
                        EmployeeInfo recommending = UserDAO.get(selected.getRecommending()).getEmployeeInfo();
                        recommending_tf.setText(recommending.getEmployeeFirstName()+" "+recommending.getEmployeeLastName());
                        EmployeeInfo approve = UserDAO.get(selected.getApprovedBy()).getEmployeeInfo();
                        approved_tf.setText(approve.getEmployeeFirstName()+" "+approve.getEmployeeLastName());
                        populateTable(selected);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
        }
    }
    public void populateTable(MR mr) {
        try {
            Platform.runLater(() -> {
                try {
                    mrItems = FXCollections.observableList(MrDAO.getItems(mr));
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
