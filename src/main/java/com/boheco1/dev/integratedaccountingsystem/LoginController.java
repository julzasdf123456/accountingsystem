package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.jfoenix.controls.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.Properties;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private StackPane loginStackPane;

    @FXML GridPane card;

    @FXML JFXTextField username;

    @FXML JFXPasswordField password;

    @FXML
    private JFXCheckBox remember_cb;

    private Properties properties = new Properties();
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = new FileInputStream("application.properties");
            properties.load(is);
            properties.list(System.out);
            String user = properties.getProperty("user");
            properties.setProperty("db_user", DB.db_user);
            properties.setProperty("host", DB.host);

            if (user.length() > 0) {
                this.username.setText(user);
                this.remember_cb.setSelected(true);
            }else{
                this.remember_cb.setSelected(false);
            }
            DB.db_user = properties.getProperty("db_user");
            DB.host = properties.getProperty("host");
        } catch (IOException e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", "An error occurred due to:"+e.getMessage()+" error.", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
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

    private boolean login(){

        String username = this.username.getText();
        String password = this.password.getText();

        if (username.length() == 0 || username== null || username.equals("")) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please enter a valid username!!", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (password.length() == 0 || password== null || password.equals("")) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please enter a valid password!", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            try {
                if (this.remember_cb.isSelected()) {
                    properties.setProperty("user", this.username.getText());
                    properties.setProperty("host", DB.host);
                    properties.setProperty("db_user", DB.db_user);
                }else{
                    properties.setProperty("user", "");
                    properties.setProperty("host", DB.host);
                    properties.setProperty("db_user", DB.db_user);
                }
                properties.store(new FileOutputStream("application.properties"), LocalDate.now().toString());
                User user = UserDAO.login(this.username.getText(), this.password.getText(), DB.getConnection());
                return user != null;
            } catch (IOException e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred due to: " + e.getMessage() + " error.", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
            } catch (Exception e) {
                e.printStackTrace();
                AlertDialogBuilder.messgeDialog("System Error", "An error occurred due to: " + e.getMessage() + " error.", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
            }
        }
        return false;
    }

}