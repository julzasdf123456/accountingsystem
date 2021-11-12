package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.Department;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.ChoiceBoxListCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ManageEmployeesController extends MenuControllerHandler implements Initializable {

    @FXML
    JFXTextField firstNameField, middleNameField, lastNameField, designationField, phoneField;

    @FXML
    JFXTextArea addressField;

    @FXML
    JFXComboBox<Department> departmentField;

    @FXML
    private TableView<EmployeeInfo> employeesTable;

    @FXML
    private TableColumn<EmployeeInfo, String> nameColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> departmentColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> designationColumn;

    @FXML
    StackPane employeeStackPane;

    private EmployeeInfo currentEmployee;
    private ObservableList<Department> listOfDepartments;
    private ObservableList<EmployeeInfo> listOfEmployees;

    public ManageEmployeesController()
    {


    }

    public void onNewEmployee()
    {
        firstNameField.setText(null);
        middleNameField.setText(null);
        lastNameField.setText(null);
        addressField.setText(null);
        phoneField.setText(null);
        designationField.setText(null);
        departmentField.getSelectionModel().clearSelection();
        employeesTable.getSelectionModel().clearSelection();
        firstNameField.requestFocus();
    }

    public void onSaveEmployee()
    {
        try {
            if(currentEmployee==null) {
                //create new
                currentEmployee = new EmployeeInfo(
                        -1,
                        firstNameField.getText(),
                        middleNameField.getText(),
                        lastNameField.getText(),
                        addressField.getText(),
                        phoneField.getText(),
                        designationField.getText(),
                        ((Department)departmentField.getSelectionModel().getSelectedItem()).getDepartmentID()
                );

                EmployeeDAO.addEmployee(currentEmployee, DB.getConnection());
                AlertDialogBuilder.messgeDialog("Success","A new employee record has been added!", employeeStackPane, AlertDialogBuilder.INFO_DIALOG);

                listOfEmployees.add(currentEmployee);
            }else {
                //update existing
                currentEmployee.setEmployeeFirstName(firstNameField.getText());
                currentEmployee.setEmployeeMidName(middleNameField.getText());
                currentEmployee.setEmployeeLastName(lastNameField.getText());
                currentEmployee.setEmployeeAddress(addressField.getText());
                currentEmployee.setPhone(phoneField.getText());
                currentEmployee.setDesignation(designationField.getText());
                currentEmployee.setDepartmentID(departmentField.getSelectionModel().getSelectedItem().getDepartmentID());

                EmployeeDAO.update(currentEmployee);

                AlertDialogBuilder.messgeDialog("Success","The employee record has been updated!", employeeStackPane, AlertDialogBuilder.INFO_DIALOG);

            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), employeeStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        currentEmployee = null;

        try {
            listOfDepartments = FXCollections.observableArrayList(DepartmentDAO.getAll(DB.getConnection()));
            departmentField.setItems(listOfDepartments);

            nameColumn.setCellValueFactory(new PropertyValueFactory<EmployeeInfo, String>("fullName"));
            designationColumn.setCellValueFactory(new PropertyValueFactory<EmployeeInfo, String>("designation"));
            departmentColumn.setCellValueFactory(new PropertyValueFactory<EmployeeInfo, String>("departmentName"));

            listOfEmployees = FXCollections.observableArrayList(EmployeeDAO.getAll(DB.getConnection()));
            employeesTable.setItems(listOfEmployees);

            employeesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EmployeeInfo>() {
                @Override
                public void changed(ObservableValue<? extends EmployeeInfo> observableValue, EmployeeInfo employeeInfo, EmployeeInfo t1) {
                    currentEmployee = t1;
                    if(currentEmployee!=null)
                        renderSelectedEmployee();
                }
            });

            nameColumn.setCellFactory(new Callback<TableColumn<EmployeeInfo, String>, TableCell<EmployeeInfo, String>>() {
                @Override
                public TableCell<EmployeeInfo, String> call(TableColumn<EmployeeInfo, String> employeeInfoStringTableColumn) {
                    return new TableCell<EmployeeInfo, String>() {
                        @Override
                        protected void updateItem(String s, boolean b) {
                            super.updateItem(s, b);
                            this.setFont(new Font("Arial", 14.0));
                            this.setText(s);
                        }
                    };
                }
            });

        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), employeeStackPane, AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }

    private void renderSelectedEmployee() {
        try {
            firstNameField.setText(currentEmployee.getEmployeeFirstName());
            middleNameField.setText(currentEmployee.getEmployeeMidName());
            lastNameField.setText(currentEmployee.getEmployeeLastName());
            addressField.setText(currentEmployee.getEmployeeAddress());
            phoneField.setText(currentEmployee.getPhone());
            designationField.setText(currentEmployee.getDesignation());
            departmentField.getSelectionModel().select(currentEmployee.getDepartment());
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), employeeStackPane, AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }
}
