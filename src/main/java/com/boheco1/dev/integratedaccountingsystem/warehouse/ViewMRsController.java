package com.boheco1.dev.integratedaccountingsystem.warehouse;

import com.boheco1.dev.integratedaccountingsystem.dao.MrDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.*;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ViewMRsController extends MenuControllerHandler implements Initializable  {

    @FXML
    private StackPane stackPane;

    @FXML
    private TableView employeesTable;

    @FXML
    private JFXButton search_btn;

    @FXML
    private JFXTextField query_tf;

    private JFXDialog dialog;

    private ObservableList<EmployeeInfo> employees = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.initializeTable();
        this.populateTable(null);
        this.query_tf.setOnAction(actionEvent -> searchEmployee());
    }

    @FXML
    public void searchEmployee(){
        String key = this.query_tf.getText();
        this.populateTable(key);
    }


    public void initializeTable() {
        TableColumn<EmployeeInfo, String> column1 = new TableColumn<>("Employee ID");
        column1.setMinWidth(50);
        column1.setCellValueFactory(new PropertyValueFactory<>("id"));
        column1.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column2 = new TableColumn<>("Last Name");
        column2.setMinWidth(175);
        column2.setCellValueFactory(new PropertyValueFactory<>("employeeLastName"));
        column2.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column3 = new TableColumn<>("First Name");
        column3.setMinWidth(175);
        column3.setCellValueFactory(new PropertyValueFactory<>("employeeFirstName"));
        column3.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column4 = new TableColumn<>("Middle Name");
        column4.setMinWidth(175);
        column4.setCellValueFactory(new PropertyValueFactory<>("employeeMidName"));
        column4.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, String> column5 = new TableColumn<>("Designation");
        column5.setMinWidth(150);
        column5.setCellValueFactory(new PropertyValueFactory<>("designation"));
        column5.setStyle("-fx-alignment: center-left;");

        TableColumn<EmployeeInfo, EmployeeInfo> column6 = new TableColumn<>("Action");

        column6.setCellValueFactory(emp -> new ReadOnlyObjectWrapper<>(emp.getValue()));
        column6.setCellFactory(mrtable -> new TableCell<>(){

            FontIcon viewIcon =  new FontIcon("mdi2e-eye");

            private final JFXButton viewButton = new JFXButton("", viewIcon);

            @Override
            protected void updateItem(EmployeeInfo employee, boolean b) {
                super.updateItem(employee, b);
                if (employee != null) {
                    viewButton.setStyle("-fx-background-color: #2196f3;");
                    viewIcon.setIconSize(13);
                    viewIcon.setIconColor(Paint.valueOf(ColorPalette.WHITE));


                    viewButton.setOnAction(actionEvent -> {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("../warehouse_view_mr.fxml"));
                        Parent parent = null;
                        try {
                            parent = loader.load();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ViewMRController view_mr = loader.getController();
                        view_mr.setMR(employee);
                        JFXDialogLayout dialogLayout = new JFXDialogLayout();
                        Label label = new Label("View Employee MR");
                        label.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 18));
                        label.setWrapText(true);
                        label.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
                        dialogLayout.setHeading(label);
                        dialogLayout.setBody(new AnchorPane(parent));
                        JFXButton cancel = new JFXButton("Close");
                        cancel.setDefaultButton(true);
                        cancel.setMinWidth(75);
                        cancel.setOnAction(ev -> dialog.close());
                        dialogLayout.setActions(cancel);
                        dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.CENTER);
                        dialog.show();
                    });
                    setGraphic(viewButton);
                } else {
                    setGraphic(null);
                    return;
                }
            }
        });
        column6.setStyle("-fx-alignment: center;");

        this.employees =  FXCollections.observableArrayList();
        this.employeesTable.setPlaceholder(new Label("No item added"));

        this.employeesTable.getColumns().add(column1);
        this.employeesTable.getColumns().add(column2);
        this.employeesTable.getColumns().add(column3);
        this.employeesTable.getColumns().add(column4);
        this.employeesTable.getColumns().add(column5);
        this.employeesTable.getColumns().add(column6);
    }

    public void populateTable(String key) {
        try {
            Platform.runLater(() -> {
                try {
                    ObservableList<EmployeeInfo> list;
                    if (key != null) {
                        list = FXCollections.observableList(MrDAO.getEmployeesWithMR(key));

                    }else{
                        list = FXCollections.observableList(MrDAO.getEmployeesWithMR());
                    }
                    this.employeesTable.getItems().setAll(list);
                } catch (Exception e) {
                    e.printStackTrace();
                    AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred while populating table due to: " + e.getMessage(), stackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }


}
