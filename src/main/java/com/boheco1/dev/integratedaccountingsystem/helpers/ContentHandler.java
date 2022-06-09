package com.boheco1.dev.integratedaccountingsystem.helpers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

public class ContentHandler {
    public static Node getNodeFromFxml(Class parent, String fxml) {
        try {
            Parent root = FXMLLoader.load(parent.getResource(fxml));

            AnchorPane pane = (AnchorPane) root;
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

            return pane;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Node getNodeFromFxml(Class parent, String fxml, AnchorPane contentPane, FlowPane subToolbar, Label titleHolder) {
        try {
            FXMLLoader loader = new FXMLLoader(parent.getResource(fxml));
            Parent root = loader.load();
            MenuControllerHandler controller = loader.getController();

            AnchorPane pane = (AnchorPane) root;
            AnchorPane.setTopAnchor(pane, 0.0);
            AnchorPane.setBottomAnchor(pane, 0.0);
            AnchorPane.setLeftAnchor(pane, 0.0);
            AnchorPane.setRightAnchor(pane, 0.0);

            contentPane.getChildren().setAll(pane);
            controller.setSubMenus(subToolbar);
            controller.handleContentReplacements(pane, titleHolder);

            return pane;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
