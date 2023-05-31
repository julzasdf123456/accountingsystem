package com.boheco1.dev.integratedaccountingsystem.budgeting;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class CreateAppController extends MenuControllerHandler implements Initializable {

    @FXML
    TextField budgetYearText;
    @FXML TextField boardResoText;
    @FXML TextField cobText;
    @FXML TableView thresholdTable;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    public void onSave() {

    }

}
