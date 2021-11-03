package com.boheco1.dev.integratedaccountingsystem;

import com.boheco1.dev.integratedaccountingsystem.helpers.MenuControllerHandler;
import com.boheco1.dev.integratedaccountingsystem.helpers.ModalBuilder;
import com.boheco1.dev.integratedaccountingsystem.helpers.SubMenuHelper;
import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AllAccountsController extends MenuControllerHandler implements Initializable, SubMenuHelper {

    @FXML public StackPane allAccountsStackpane;

    /**
     * Initialize Sub menus in toolbar
     */
    public List<JFXButton> subMenuList;
    public JFXButton options, disconnections;

    public AllAccountsController() {
        options = new JFXButton("Options");
        disconnections = new JFXButton("Disconnections");
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

    @Override
    public void setSubMenus(FlowPane flowPane) {
        subMenuList = new ArrayList<>();
        subMenuList.add(options);
        subMenuList.add(disconnections);
        flowPane.getChildren().removeAll();
        flowPane.getChildren().setAll(subMenuList);
    }

    @Override
    public void handleContentReplacements(AnchorPane container) {
        super.handleContentReplacements(container);
    }

    @FXML
    private void showModalEvent() {
        ModalBuilder.showModalFromXML(AllAccountsController.class, "modal_test.fxml", allAccountsStackpane);
    }
}
