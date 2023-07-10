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


public class AlertDialogBuilder {

    public static String INFO_DIALOG = ColorPalette.INFO;
    public static String SUCCESS_DIALOG = ColorPalette.SUCCESS;
    public static String DANGER_DIALOG = ColorPalette.DANGER;
    public static String WARNING_DIALOG = ColorPalette.WARNING;
    private static JFXDialog alertDialog;
    public static void messgeDialog(String title, String message, StackPane pane, String type) {
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
        dialogContent.requestFocus();

        JFXButton close = new JFXButton("Close");
        close.setDefaultButton(true);
        close.getStyleClass().add("JFXButton");
        dialogContent.setActions(close);

        alertDialog = new JFXDialog(pane, dialogContent, JFXDialog.DialogTransition.CENTER);

        close.setTextFill(Paint.valueOf(ColorPalette.GREY));
        close.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent __) {
                alertDialog.close();
            }
        });

        alertDialog.show();
    }

    public static void CLOSE_ALERT_DIALOG(){
        if(alertDialog != null)
            alertDialog.close();
    }
}
