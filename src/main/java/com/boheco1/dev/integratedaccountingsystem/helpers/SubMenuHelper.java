package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

import java.util.List;

public interface SubMenuHelper {
    /**
     * Initialize sub menus inside this function
     * @param flowPane
     */
    public void setSubMenus(FlowPane flowPane);

    /**
     * Set menu events inside this function
     * @param container
     */
    public void handleContentReplacements(AnchorPane container);
}
