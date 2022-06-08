package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.dao.EmployeeDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.objects.EmployeeInfo;
import com.boheco1.dev.integratedaccountingsystem.objects.Permission;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewMyAccountController extends MenuControllerHandler implements Initializable {
    @FXML
    Label userNameField, fullNameField, designationField, phoneNumberField;

    @FXML
    TextArea permissionsField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            User user = ActiveUser.getUser();
            EmployeeInfo emp = EmployeeDAO.getOne(user.getEmployeeID(), DB.getConnection());
            userNameField.setText(user.getUserName());
            fullNameField.setText(user.getFullName());
            designationField.setText(emp.getDesignation());
            phoneNumberField.setText(emp.getPhone());

            StringBuffer permissions = new StringBuffer();

            for(Permission p: user.getPermissions()) {
                permissions.append(p.getPermission() + ", ");
            }

            if(permissions.length()>0)
                permissionsField.setText(permissions.substring(0, permissions.length()-2));
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }
}
