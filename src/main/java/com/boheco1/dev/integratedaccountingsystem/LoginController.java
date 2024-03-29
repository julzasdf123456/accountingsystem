package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.AlertDialogBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.Utility;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.jfoenix.controls.*;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;

import java.io.*;
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
            //Read app properties
            File appData = new File("application.properties");
            //Create defaults if first run
            if (!appData.exists()){
                properties = new Properties();
                properties.setProperty("user", "");
                properties.setProperty("host", DB.host);
                properties.setProperty("db_user", DB.db_user);
                properties.setProperty("db_pass", DB.db_pass);
                properties.setProperty("station", Utility.STATION);
                properties.setProperty("office", Utility.OFFICE_PREFIX);
                properties.setProperty("db_billing", Utility.DB_BILLING);
                properties.store(new FileOutputStream("application.properties"), LocalDate.now().toString());
            }
            InputStream is = new FileInputStream("application.properties");
            properties.load(is);
            String user = properties.getProperty("user");
            if (user.length() > 0) {
                this.username.setText(user);
                this.remember_cb.setSelected(true);
            }else{
                this.remember_cb.setSelected(false);
            }
            DB.db_user = properties.getProperty("db_user");
            DB.host = properties.getProperty("host");
            DB.db_pass = properties.getProperty("db_pass");
            DB.db_billing = properties.getProperty("db_billing");
            Utility.STATION = properties.getProperty("station");
            Utility.OFFICE_PREFIX = properties.getProperty("office");
            Utility.DB_BILLING = properties.getProperty("db_billing");
            Utility.MIRS_PATH = properties.getProperty("mirs_path");
        } catch (Exception e) {
            e.printStackTrace();
            AlertDialogBuilder.messgeDialog("System Error", e.getMessage(), loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
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
            AlertDialogBuilder.messgeDialog("System Error", ex.getMessage(), loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }
    }

    private boolean login() throws Exception{

        String username = this.username.getText();
        String password = this.password.getText();

        if (username.length() == 0 || username== null || username.equals("")) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please enter a valid username!!", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else if (password.length() == 0 || password== null || password.equals("")) {
            AlertDialogBuilder.messgeDialog("Input Error", "Please enter a valid password!", loginStackPane, AlertDialogBuilder.DANGER_DIALOG);
        }else {
            this.saveAppData(this.remember_cb.isSelected());
            User user = UserDAO.login(this.username.getText(), this.password.getText(), DB.getConnection());
            return user != null;
        }
        return false;
    }

    public void saveAppData(boolean remember) throws Exception {
        this.properties.setProperty("user", "");
        if (remember)
            this.properties.setProperty("user", this.username.getText());
        properties.setProperty("host", DB.host);
        properties.setProperty("db_user", DB.db_user);
        properties.setProperty("db_pass", DB.db_pass);
        properties.setProperty("db_billing", Utility.DB_BILLING);
        properties.setProperty("station", Utility.STATION);
        properties.setProperty("office", Utility.OFFICE_PREFIX);
        properties.store(new FileOutputStream("application.properties"), LocalDate.now().toString());
    }
}