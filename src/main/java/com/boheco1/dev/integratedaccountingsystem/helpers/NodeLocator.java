package com.boheco1.dev.integratedaccountingsystem.helpers;

import javafx.scene.Node;

public class NodeLocator {
    public static double getNodeX(Node node) {
        try {
            return node.localToScene(node.getBoundsInLocal()).getCenterX()+20;
        } catch (Exception e) {
            return 12.0;
        }
    }

    public static double getNodeY(Node node) {
        try {
            return node.localToScene(node.getBoundsInLocal()).getCenterY()+20;
        } catch (Exception e) {
            return 12.0;
        }
    }
}
