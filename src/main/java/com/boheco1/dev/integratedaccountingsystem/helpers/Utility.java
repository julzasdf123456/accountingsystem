package com.boheco1.dev.integratedaccountingsystem.helpers;

import javafx.scene.layout.AnchorPane;

public class Utility {
    private static AnchorPane contentPane;

    public static AnchorPane getContentPane() {
        return contentPane;
    }

    public static void setContentPane(AnchorPane contentPane) {
        Utility.contentPane = contentPane;
    }
}
