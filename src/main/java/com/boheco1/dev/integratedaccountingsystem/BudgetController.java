package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class BudgetController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    /**
     * These menus will replace the menus on the sub toolbar
     */
    public List<JFXButton> subToolbarMenus;
    public JFXButton options;

    public BudgetController() {
        options = new JFXButton("Options");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        subToolbarMenus = new ArrayList<>();
        subToolbarMenus.add(options);
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(subToolbarMenus);
    }

    @Override
    public void handleContentReplacements(AnchorPane container, Label titleHolder) {

    }
}
