package com.boheco1.dev.integratedaccountingsystem.tellering;

import com.boheco1.dev.integratedaccountingsystem.dao.UserDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.DB;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.User;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

public class WaiveConfirmationController extends MenuControllerHandler implements Initializable {


    @FXML
    private AnchorPane contentPane;

    @FXML
    private TextField username_tf;

    @FXML
    private JFXButton authenticate_btn;

    @FXML
    private PasswordField password_tf;

    @FXML
    private Label status_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public boolean login(){
        String user = this.username_tf.getText();
        String pass = this.password_tf.getText();
        this.status_lbl.setText("");
        if (!user.isEmpty() && !pass.isEmpty()){
            try {
                User cashier = UserDAO.login(user, pass, DB.getConnection());
                if (cashier.getEmployeeInfo().getDesignation().equals("Cashier"))
                    return true;
                else
                    this.status_lbl.setText("Needs Cashier permission!");
            } catch (Exception e) {
                this.status_lbl.setText(e.getMessage());
            }
        }
        return false;
    }
    public JFXButton getAuthenticate_btn() {
        return authenticate_btn;
    }
}
