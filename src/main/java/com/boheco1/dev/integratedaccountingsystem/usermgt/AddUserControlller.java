package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.JournalEntriesController;
import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.*;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

import javax.swing.*;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddUserControlller extends MenuControllerHandler implements Initializable {
    @FXML
    StackPane addUserStackPane;

    @FXML
    JFXComboBox employeeCombo;
    @FXML
    JFXTextField userNameField;
    @FXML
    JFXTextField fullNameField;
    @FXML
    JFXPasswordField passwordField;

    ObservableList<EmployeeInfo> employeeInfos;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection conn = DB.getConnection();

            employeeInfos = FXCollections.observableArrayList(EmployeeDAO.getAll(conn));

            employeeCombo.setItems(employeeInfos);

        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    public void onSave(ActionEvent event) {
        if(employeeCombo.getSelectionModel().isEmpty() || fullNameField.getText().isEmpty() || userNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            AlertDialogBuilder.messgeDialog("Entry Error", "All the fields must be filled in.", addUserStackPane, AlertDialogBuilder.DANGER_DIALOG);
            return;
        }
        try {
            EmployeeInfo employeeInfo = (EmployeeInfo) employeeCombo.getSelectionModel().getSelectedItem();
            User user = new User(employeeInfo.getId(),userNameField.getText());
            user.setPassword(passwordField.getText());
            UserDAO.addUser(user, DB.getConnection());

            Utility.setSelectedObject(user);

            Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(JournalEntriesController.class, "user_mgt.fxml"));
        }catch(Exception ex) {
            ex.printStackTrace();
        }


    }

    @FXML
    public void onSelectEmployee(ActionEvent event) {
        EmployeeInfo employeeInfo = (EmployeeInfo) employeeCombo.getSelectionModel().getSelectedItem();
        fullNameField.setText(employeeInfo.getFullName());
    }
}
