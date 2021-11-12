package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextArea;
import com.jfoenix.controls.JFXTextField;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;

public class ManageEmployeesController extends MenuControllerHandler {

    @FXML
    JFXTextField firstNameField, middleNameField, lastNameField, designationField, phoneField;

    @FXML
    JFXTextArea addressField;

    @FXML
    JFXComboBox departmentField;

    @FXML
    TableView employeesTable;

    private EmployeeInfo currentEmployee;

    public ManageEmployeesController() {
        currentEmployee = null;
    }

    public void onNewEmployee()
    {
        firstNameField.setText(null);
        middleNameField.setText(null);
        lastNameField.setText(null);

    }

    public void onSaveEmployee()
    {

    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(new ArrayList<>());
    }

}
