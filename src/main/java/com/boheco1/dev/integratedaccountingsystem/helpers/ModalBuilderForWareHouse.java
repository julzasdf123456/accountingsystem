package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.warehouse.CriticalStockController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import org.kordamp.ikonli.javafx.FontIcon;

public class ModalBuilderForWareHouse {
    private static JFXDialog dialog;
    public static void showModalFromXML(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.setOnDialogClosed(e ->
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_dashboard_controller.fxml")));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModalFromXMLNoClose(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModalFromXMLHeader(Class parentClass, String xml, StackPane stackPane,String dialogTitle) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            // Create a custom header with a close button
            HBox customHeader = new HBox();

            FontIcon closeIcon = new FontIcon("mdi2c-close-circle");
            closeIcon.setIconSize(25);
            Label title = new Label(dialogTitle);
            title.maxWidthProperty().bind(customHeader.widthProperty());
            title.setAlignment(Pos.CENTER);
            title.setStyle("-fx-font-size: 18px;");
            HBox.setHgrow(title, javafx.scene.layout.Priority.ALWAYS);
            JFXButton closeButton = new JFXButton("", closeIcon);
            closeButton.setFont(Font.font(closeIcon.getFont().getFamily(), 10));
            closeButton.setContentDisplay(ContentDisplay.CENTER); // Set the icon position
            closeButton.setButtonType(JFXButton.ButtonType.RAISED); // Apply a style
            closeButton.setOnAction(event -> {
                dialog.close();
            });
            customHeader.getChildren().add(title);
            customHeader.getChildren().add(closeButton);
            customHeader.setStyle("-fx-alignment: center-right;");

            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setPadding(new Insets(-20,-15,-40,-15));// top, left, bottom, right
            dialogLayout.setBody(parent);
            dialogLayout.setHeading(customHeader);

            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.setOverlayClose(false);
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModalFromXMLWithExitPath(Class parentClass, String xml, StackPane stackPane, String fxml) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.setOnDialogClosed(e ->
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, fxml)));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModalFromXML(Class parentClass, String xml, StackPane stackPane, JFXButton action) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            JFXButton cancel = new JFXButton("Cancel");
            cancel.setOnAction(actionEvent -> dialog.close());
            dialogLayout.setActions(action, cancel);
            dialog.setOnDialogClosed(e ->
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, "../warehouse_dashboard_controller.fxml")));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void showModalFromXMLWithExitPath(Class parentClass, String xml, StackPane stackPane, String fxml, JFXButton action) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            JFXButton cancel = new JFXButton("Cancel");
            cancel.setOnAction(actionEvent -> dialog.close());
            dialogLayout.setActions(action, cancel);
            dialog.setOnDialogClosed(e ->
                    Utility.getContentPane().getChildren().setAll(ContentHandler.getNodeFromFxml(CriticalStockController.class, fxml)));
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void MODAL_CLOSE(){
        dialog.close();
    }

}
