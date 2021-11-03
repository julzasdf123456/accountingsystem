package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXButton;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class ContextMenuHelper {

    public ContextMenuHelper() {}

    public ContextMenu initializePopupContextMenu(JFXButton button, MenuItem... menuItems) {
        try {
            ContextMenu contextMenu = new ContextMenu();

            contextMenu.getItems().addAll(menuItems);

            return contextMenu;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
