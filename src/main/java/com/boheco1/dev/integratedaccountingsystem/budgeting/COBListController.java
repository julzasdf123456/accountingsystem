package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.dao.AppDAO;
import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.objects.APP;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class COBListController extends MenuControllerHandler implements Initializable {

    @FXML
    private Label app_status_lbl;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        APP current = null;
        try {
            current = AppDAO.getOpen(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (current != null) {
            app_status_lbl.setText("Budget preparation for CY "+current.getYear()+" ("+current.getBoardRes()+") is currently opened! Please prepare the department's cash operating budget!");
        }else{
            app_status_lbl.setText("");
        }
    }


}
