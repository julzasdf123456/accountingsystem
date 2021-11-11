package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.HomeController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;

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
}
