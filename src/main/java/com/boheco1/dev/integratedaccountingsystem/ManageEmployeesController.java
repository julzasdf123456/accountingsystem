package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.dao.DepartmentDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.Department;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.MRTItem;
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
    JFXTextField firstNameField, middleNameField, lastNameField, suffixField, designationField, phoneField;

    @FXML
    JFXTextArea addressField;

    @FXML
    JFXComboBox<Department> departmentField;

    @FXML JFXComboBox<String> signatoryLevelField;

    @FXML
    private TableView<EmployeeInfo> employeesTable;

    @FXML
    private TableColumn<EmployeeInfo, String> nameColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> departmentColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> designationColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> phoneColumn;

    @FXML
    private TableColumn<EmployeeInfo, String> signatoryColumn;

    private EmployeeInfo currentEmployee;
    private ObservableList<Department> listOfDepartments;
    private ObservableList<EmployeeInfo> listOfEmployees;

    public ManageEmployeesController()
    {}

    public void onNewEmployee()
    {
        firstNameField.setText(null);
        middleNameField.setText(null);
        lastNameField.setText(null);
        addressField.setText(null);
        suffixField.setText(null);
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
                        "",
                        firstNameField.getText(),
                        middleNameField.getText(),
                        lastNameField.getText(),
                        suffixField.getText(),
                        addressField.getText(),
                        phoneField.getText(),
                        designationField.getText(),
                        signatoryLevelField.getSelectionModel().getSelectedItem(),
                        departmentField.getSelectionModel().getSelectedItem().getDepartmentID()
                );

                EmployeeDAO.addEmployee(currentEmployee, DB.getConnection());
                AlertDialogBuilder.messgeDialog("Success","A new employee record has been added!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);

                listOfEmployees.add(currentEmployee);
            }else {
                //update existing
                currentEmployee.setEmployeeFirstName(firstNameField.getText());
                currentEmployee.setEmployeeMidName(middleNameField.getText());
                currentEmployee.setEmployeeLastName(lastNameField.getText());
                currentEmployee.setEmployeeAddress(addressField.getText());
                currentEmployee.setEmployeeSuffix(suffixField.getText());
                currentEmployee.setPhone(phoneField.getText());
                currentEmployee.setDesignation(designationField.getText());
                currentEmployee.setSignatoryLevel(signatoryLevelField.getSelectionModel().getSelectedItem());
                currentEmployee.setDepartmentID(departmentField.getSelectionModel().getSelectedItem().getDepartmentID());

                EmployeeDAO.update(currentEmployee);
                int index = 0;
                for (EmployeeInfo emp : this.employeesTable.getItems()){
                    if (emp.getId().equals(this.currentEmployee.getId())){
                        this.employeesTable.getItems().set(index, currentEmployee);
                    }
                    index++;
                }
                AlertDialogBuilder.messgeDialog("Success","The employee record has been updated!", Utility.getStackPane(), AlertDialogBuilder.INFO_DIALOG);

            }
        }catch(Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
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
            nameColumn = new TableColumn<>("Employee Name");
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("fullName"));
            nameColumn.setStyle("-fx-alignment: center-left;");

            designationColumn = new TableColumn<>("Designation");
            designationColumn.setCellValueFactory(new PropertyValueFactory<>("designation"));
            designationColumn.setStyle("-fx-alignment: center-left;");

            departmentColumn = new TableColumn<>("Dept");
            departmentColumn.setCellValueFactory(new PropertyValueFactory<>("departmentName"));
            departmentColumn.setStyle("-fx-alignment: center-left;");

            phoneColumn = new TableColumn<>("Phone");
            phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
            phoneColumn.setStyle("-fx-alignment: center;");

            signatoryColumn = new TableColumn<>("Signatory Level");
            signatoryColumn.setCellValueFactory(new PropertyValueFactory<>("signatoryLevel"));
            signatoryColumn.setStyle("-fx-alignment: center-left;");

            listOfEmployees = FXCollections.observableArrayList(EmployeeDAO.getAll(DB.getConnection()));
            employeesTable.setItems(listOfEmployees);

            ArrayList<String> signatoryLevels = new ArrayList<>();
            signatoryLevels.add("General Manager");
            signatoryLevels.add("Department Manager");
            signatoryLevels.add("Supervisor");
            signatoryLevels.add("Section Head");
            signatoryLevelField.setItems(FXCollections.observableArrayList(signatoryLevels));

            employeesTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<EmployeeInfo>() {
                @Override
                public void changed(ObservableValue<? extends EmployeeInfo> observableValue, EmployeeInfo employeeInfo, EmployeeInfo t1) {
                    currentEmployee = t1;
                    if(currentEmployee!=null)
                        renderSelectedEmployee();
                }
            });

            this.nameColumn.setPrefWidth(250);
            this.nameColumn.setMinWidth(250);
            this.nameColumn.setMaxWidth(250);

            this.departmentColumn.setPrefWidth(100);
            this.departmentColumn.setMinWidth(100);
            this.departmentColumn.setMaxWidth(100);

            this.designationColumn.setPrefWidth(200);
            this.designationColumn.setMinWidth(200);
            this.designationColumn.setMaxWidth(200);

            this.phoneColumn.setPrefWidth(150);
            this.phoneColumn.setMinWidth(150);
            this.phoneColumn.setMaxWidth(150);

            this.signatoryColumn.setPrefWidth(250);
            this.signatoryColumn.setMinWidth(250);
            this.signatoryColumn.setMaxWidth(250);

            this.employeesTable.getColumns().add(this.nameColumn);
            this.employeesTable.getColumns().add(this.departmentColumn);
            this.employeesTable.getColumns().add(this.designationColumn);
            this.employeesTable.getColumns().add(this.phoneColumn);
            this.employeesTable.getColumns().add(this.signatoryColumn);
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }

    private void renderSelectedEmployee() {
        try {
            firstNameField.setText(currentEmployee.getEmployeeFirstName());
            middleNameField.setText(currentEmployee.getEmployeeMidName());
            lastNameField.setText(currentEmployee.getEmployeeLastName());
            suffixField.setText(currentEmployee.getEmployeeSuffix());
            addressField.setText(currentEmployee.getEmployeeAddress());
            phoneField.setText(currentEmployee.getPhone());
            designationField.setText(currentEmployee.getDesignation());
            signatoryLevelField.getSelectionModel().select(currentEmployee.getSignatoryLevel());
            departmentField.getSelectionModel().select(currentEmployee.getDepartment());
        }catch(Exception ex) {
            AlertDialogBuilder.messgeDialog("Exception!", ex.getMessage(), Utility.getStackPane(), AlertDialogBuilder.DANGER_DIALOG);
            ex.printStackTrace();
        }
    }
}
