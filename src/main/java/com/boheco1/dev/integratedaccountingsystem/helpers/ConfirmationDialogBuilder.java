package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class ConfirmationDialogBuilder {

    public static String INFO_DIALOG = ColorPalette.INFO;

    public static boolean confirmDialog(String message, StackPane pane) {
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + ColorPalette.INFO + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label("Confirm Action");
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(ColorPalette.INFO));
        dialogContent.setHeading(head);

        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(Orientation.VERTICAL);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setRowValignment(VPos.CENTER);
        flowPane.setColumnHalignment(HPos.CENTER);
        flowPane.setVgap(6);

        Label context = new Label(message);
        context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
        context.setWrapText(true);
        context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

        flowPane.getChildren().add(context);
        dialogContent.setBody(flowPane);

        JFXDialog dialog = new JFXDialog(pane, dialogContent, JFXDialog.DialogTransition.CENTER);

        JFXButton accept = new JFXButton("Accept");
        accept.setDefaultButton(true);
        accept.getStyleClass().add("JFXButton");

        JFXButton cancel = new JFXButton("Cancel");
        cancel.setDefaultButton(true);
        cancel.getStyleClass().add("JFXButton");
        dialogContent.setActions(cancel,accept);

        cancel.setTextFill(Paint.valueOf(ColorPalette.GREY));
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                dialog.close();
            }
        });

        accept.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));
        accept.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                dialog.close();
            }
        });

        dialog.show();
        return false;
    }
}
