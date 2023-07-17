package com.boheco1.dev.integratedaccountingsystem.helpers;

import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

public class ModalBuilder {
    private static JFXDialog dialog;
    private static boolean running;
    public static JFXDialog showModalFromXML(Class parentClass, String xml, StackPane stackPane) {
        try {
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.show();
            return dialog;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void showModalFromXMLNoClose(Class parentClass, String xml, StackPane stackPane) {
        try {
            if (MODAL_RUNNING()) return;
            Parent parent = FXMLLoader.load(parentClass.getResource(xml));
            JFXDialogLayout dialogLayout = new JFXDialogLayout();
            dialogLayout.setBody(parent);
            dialogLayout.setPadding(new Insets(-40,-15,-40,-15));// top, left, bottom, right
            dialog = new JFXDialog(stackPane, dialogLayout, JFXDialog.DialogTransition.BOTTOM);
            dialog.setOnDialogClosed(event -> {
                // Handle any actions after the dialog is closed
                running = false;
                System.out.println("Dialog closed.");
            });
            dialog.show();
            running = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean MODAL_RUNNING(){
        return running;
    }

    public static void MODAL_CLOSE(){
        dialog.close();
        running = false;
    }
}
