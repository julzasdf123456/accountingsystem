package com.boheco1.dev.integratedaccountingsystem.usermgt;

import com.boheco1.dev.integratedaccountingsystem.usermgt.ActiveUser;
import com.boheco1.dev.integratedaccountingsystem.usermgt.Permission;
import com.boheco1.dev.integratedaccountingsystem.usermgt.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ViewMyAccountController implements Initializable {
    @FXML
    Label userNameField, fullNameField, designationField, phoneNumberField;

    @FXML
    TextArea permissionsField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        User user = ActiveUser.getUser();

        userNameField.setText(user.getUserName());
        fullNameField.setText(user.getFullName());
        designationField.setText(user.getDesignation());
        phoneNumberField.setText(user.getPhone());

        StringBuffer permissions = new StringBuffer();

        for(Permission p: user.getPermissions()) {
            permissions.append(p.getPermission() + ", ");
        }

        permissionsField.setText(permissions.substring(0, permissions.length()-2));
    }
}
