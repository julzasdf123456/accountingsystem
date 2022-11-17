package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.boheco1.dev.integratedaccountingsystem.warehouse.CriticalStockController;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

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
