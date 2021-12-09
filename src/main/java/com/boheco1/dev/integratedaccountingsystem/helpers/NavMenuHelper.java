package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXButton;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.List;

public class NavMenuHelper {
    /**
     * Add additional menu item on the nav
     * @param vBox
     * @param menuButton
     * @param stackPane
     */
    public static void addMenu(VBox vBox, JFXButton menuButton, StackPane stackPane) {
        try {
            menuButton.setFont(Font.font(menuButton.getFont().getFamily(),14));
            menuButton.setPrefWidth(238);
            menuButton.setStyle("-fx-alignment: LEFT;");
            vBox.getChildren().add(menuButton);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("Error creating menu", "An error occurred while adding the menu " + menuButton.getText() + ". (" + e.getMessage() + ")", stackPane, AlertDialogBuilder.INFO_DIALOG);
        }
    }

    public static void addSeparatorLabel(List<Label> labelList, VBox vBox, Label label, FontIcon icon, StackPane stackPane) {
        try {
            label.setFont(Font.font(label.getFont().getFamily(),14));
            label.setPrefWidth(246.0);
            label.setPrefHeight(25);
            label.setTooltip(new Tooltip(label.getText()));

            icon.setIconColor(Paint.valueOf(ColorPalette.WHITE));
            icon.setIconSize(16);
            label.setGraphicTextGap(10);
            label.setGraphic(icon);

            label.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
            label.setStyle("-fx-background-color: #26a69a");
            label.setTextFill(Paint.valueOf(ColorPalette.WHITE));
            label.setPadding(new Insets(10, 0, 10, 10));
            vBox.getChildren().add(label);
            labelList.add(label);
        } catch (Exception e) {
            AlertDialogBuilder.messgeDialog("Error creating menu", "An error occurred while adding the menu " + label.getText() + ". (" + e.getMessage() + ")", stackPane, AlertDialogBuilder.INFO_DIALOG);
        }
    }
}
