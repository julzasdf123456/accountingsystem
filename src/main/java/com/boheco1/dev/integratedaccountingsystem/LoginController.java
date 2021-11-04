package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.usermgt.User;
import com.boheco1.dev.integratedaccountingsystem.usermgt.UserDAO;
import com.jfoenix.controls.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML GridPane card;

    @FXML JFXTextField username;

    @FXML JFXPasswordField password;

    @FXML StackPane loginStackPane;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @FXML
    private void loginEvent(Event event) {
        try {
            if (login()) {
                HostWindow.setRoot("home_controller");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            AlertDialogBuilder.messgeDialog("Cannot Log you In", ex.getMessage(), loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private boolean login() throws Exception {
        User user = UserDAO.login(username.getText(), password.getText(), DB.getConnection());
        return user!=null;
    }

}