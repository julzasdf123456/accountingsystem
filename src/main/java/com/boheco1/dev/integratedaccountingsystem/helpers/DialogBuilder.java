package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.jfoenix.controls.JFXTextField;
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

public class DialogBuilder {
    public static String INFO_DIALOG = ColorPalette.INFO;
    public static String SUCCESS_DIALOG = ColorPalette.SUCCESS;
    public static String DANGER_DIALOG = ColorPalette.DANGER;
    public static String WARNING_DIALOG = ColorPalette.WARNING;

    public static JFXDialog showConfirmDialog(String title, String message, JFXButton btn, StackPane pane, String style) {
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + style + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label(title);
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(style));
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
        dialog.setOverlayClose(false);

        btn.setDefaultButton(true);
        btn.getStyleClass().add("JFXButton");

        JFXButton cancel = new JFXButton("Cancel");
        cancel.setDefaultButton(true);
        cancel.getStyleClass().add("JFXButton");
        dialogContent.setActions(cancel,btn);

        btn.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));

        cancel.setTextFill(Paint.valueOf(ColorPalette.GREY));
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                dialog.close();
            }
        });
        dialog.show();
        return dialog;
    }

    public static JFXDialog showInputDialog(String title, String message, String promptText, JFXTextField input, JFXButton btn, StackPane pane, String style){
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + style + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label(title);
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(style));
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

        input.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 18));
        input.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");
        input.setPromptText(promptText);
        input.setLabelFloat(true);

        flowPane.getChildren().add(context);
        flowPane.getChildren().add(input);
        dialogContent.setBody(flowPane);

        JFXDialog dialog = new JFXDialog(pane, dialogContent, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);

        btn.setDefaultButton(true);
        btn.getStyleClass().add("JFXButton");

        JFXButton cancel = new JFXButton("Cancel");
        cancel.setDefaultButton(true);
        cancel.getStyleClass().add("JFXButton");
        dialogContent.setActions(cancel,btn);

        btn.setTextFill(Paint.valueOf(ColorPalette.MAIN_COLOR));

        cancel.setTextFill(Paint.valueOf(ColorPalette.GREY));
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                dialog.close();
            }
        });
        dialog.show();
        return dialog;
    }

    public static JFXDialog showInputDialog(String title, String message, Orientation orientation, JFXTextField input, JFXButton btn, String style, StackPane pane){
        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + style + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label(title);
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(style));
        dialogContent.setHeading(head);

        FlowPane flowPane = new FlowPane();
        flowPane.setOrientation(orientation);
        flowPane.setAlignment(Pos.CENTER);
        flowPane.setRowValignment(VPos.CENTER);
        flowPane.setColumnHalignment(HPos.CENTER);
        flowPane.setVgap(6);

        Label context = new Label(message);
        context.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 12));
        context.setWrapText(true);
        context.setStyle("-fx-text-fill: " + ColorPalette.BLACK + ";");

        flowPane.getChildren().add(context);
        flowPane.getChildren().add(input);
        dialogContent.setBody(flowPane);

        JFXDialog dialog = new JFXDialog(pane, dialogContent, JFXDialog.DialogTransition.CENTER);
        dialog.setOverlayClose(false);

        JFXButton cancel = new JFXButton("Cancel");
        cancel.setDefaultButton(true);
        cancel.getStyleClass().add("JFXButton");
        cancel.setTextFill(Paint.valueOf(ColorPalette.GREY_DARK));
        cancel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
               dialog.close();
            }
        });
        dialogContent.setActions(cancel,btn);
        dialog.show();
        return dialog;
    }

    public static JFXDialog showWaitDialog(String title, String message, StackPane pane, String type){

        JFXDialogLayout dialogContent = new JFXDialogLayout();
        dialogContent.setStyle("-fx-border-width: 0 0 0 15; -fx-border-color: " + type + ";");
        dialogContent.setPrefHeight(200);

        Label head = new Label(title);
        head.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.NORMAL, 15));
        head.setTextFill(Paint.valueOf(type));
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
        dialog.setOverlayClose(false);
        return  dialog;
    }

}
