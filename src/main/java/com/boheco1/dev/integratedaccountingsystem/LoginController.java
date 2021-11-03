package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
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
            if (username.getText().equals("user") && password.getText().equals("user")) {
                HostWindow.setRoot("home_controller");
            } else {
                AlertDialogBuilder.messgeDialog("Test Login", "This is a test login message " + username.getText() + " - " + password.getText(), loginStackPane, AlertDialogBuilder.WARNING_DIALOG);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}